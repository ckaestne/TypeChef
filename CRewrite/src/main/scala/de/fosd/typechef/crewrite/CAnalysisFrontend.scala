
package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import java.io.StringWriter
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._
import de.fosd.typechef.error.{Severity, TypeChefError}
import de.fosd.typechef.parser.c.SwitchStatement
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.TranslationUnit
import de.fosd.typechef.conditional.Opt


sealed abstract class CAnalysisFrontend(tunit: TranslationUnit) extends CFGHelper {

    protected val env = CASTEnv.createASTEnv(tunit)
    protected val fdefs = filterAllASTElems[FunctionDef](tunit)
}

class CInterAnalysisFrontend(tunit: TranslationUnit, fm: FeatureModel = FeatureExprFactory.empty) extends CAnalysisFrontend(tunit) with InterCFG {

    def getTranslationUnit(): TranslationUnit = tunit

    def writeCFG(title: String, writer: CFGWriter) {
        val env = CASTEnv.createASTEnv(tunit)
        writer.writeHeader(title)

        def lookupFExpr(e: AST): FeatureExpr = e match {
            case o if env.isKnown(o) => env.featureExpr(o)
            case e: ExternalDef => externalDefFExprs.getOrElse(e, FeatureExprFactory.True)
            case _ => FeatureExprFactory.True
        }


        for (f <- fdefs) {
            writer.writeMethodGraph(getAllSucc(f, env).map {
                x => (x._1, x._2.distinct.filter { y => y.condition.isSatisfiable(fm)}) // filter duplicates and wrong succs
            }, lookupFExpr, f.declarator.getName)
        }
        writer.writeFooter()
        writer.close()

        if (writer.isInstanceOf[StringWriter])
            println(writer.toString)
    }
}

// TODO: refactoring different dataflow analyses into a composite will reduce code: handling of invalid paths, error printing ...
class CIntraAnalysisFrontend(tunit: TranslationUnit, ts: CTypeSystemFrontend with CDeclUse, fm: FeatureModel = FeatureExprFactory.empty) extends CAnalysisFrontend(tunit) with IntraCFG {

    private lazy val udm = ts.getUseDeclMap
    private lazy val dum = ts.getDeclUseMap

    private def getDecls(key: Id): List[Id] = {
        if (! udm.containsKey(key)) List(key)
        else udm.get(key).filter { d => env.featureExpr(d) and env.featureExpr(key) isSatisfiable fm }
    }

    private val fanalyze = fdefs.map {
        x => (x, getAllSucc(x, env).filterNot { x => x._1.isInstanceOf[FunctionDef] } )
    }

    var errors: List[TypeChefError] = List()

    def deadStore(): Boolean = {
        val err = fanalyze.flatMap(deadStore)

        if (err.isEmpty) {
            println("No dead stores found!")
        } else {
            println(err.map(_.toString + "\n").reduce(_ + _))
        }
        errors ++= err
        err.isEmpty
    }

    private def deadStore(fa: (FunctionDef, List[(AST, List[Opt[AST]])])): List[TypeChefError] = {
        var err: List[TypeChefError] = List()

        val df = new Liveness(env, udm, FeatureExprFactory.empty)

        val nss = fa._2.map(_._1)

        for (s <- nss) {
            for ((i, fi) <- df.kill(s)) {
                val out = df.out(s)

                // code such as "int a;" occurs frequently and issues an error
                // we filter them out by checking the declaration use map for usages
                if (dum.containsKey(i) && dum.get(i).size > 0) {}
                else out.find { case (t, _) => t == i } match {
                    case None => {
                        var idecls = getDecls(i)
                        if (idecls.exists(isPartOf(_, fa._1)))
                            err ::= new TypeChefError(Severity.Warning, fi, "warning: Variable " + i.name + " is a dead store!", i, "")
                    }
                    case Some((x, z)) => {
                        if (fi.and(z.not()).isSatisfiable(fm)) {
                            var xdecls = getDecls(x)
                            var idecls = getDecls(i)
                            for (ei <- idecls) {
                                // with isPartOf we reduce the number of false positives, since we only check local variables and function parameters.
                                // an assignment to a global variable might be used in another function
                                if (isPartOf(ei, fa._1) && xdecls.exists(_.eq(ei)))
                                    err ::= new TypeChefError(Severity.Warning, z.not(), "warning: Variable " + i.name + " is a dead store!", i, "")
                            }

                        }
                    }
                }
            }
        }

        err
    }

    def doubleFree(): Boolean = {
        val casestudy = {
            tunit.getFile match {
                case None => ""
                case Some(x) => {
                    if (x.contains("linux")) "linux"
                    else if (x.contains("openssl")) "openssl"
                    else ""
                }
            }
        }

        val err = fanalyze.flatMap(doubleFree(_, casestudy))

        if (err.isEmpty) {
            println("No double frees found!")
        } else {
            println(err.map(_.toString + "\n").reduce(_ + _))
        }

        errors ++= err
        err.isEmpty
    }


    private def doubleFree(fa: (FunctionDef, List[(AST, List[Opt[AST]])]), casestudy: String): List[TypeChefError] = {
        var err: List[TypeChefError] = List()

        val df = new DoubleFree(env, dum, udm, FeatureExprFactory.empty, casestudy)

        val nss = fa._2.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = df.gen(s)
            if (g.size > 0) {
            val in = df.in(s)

            for (((i, _), h) <- in)
                g.find { case ((t, _), _) => t == i } match {
                    case None =>
                    case Some(((x, _), _)) => {
                        if (h.isSatisfiable(fm)) {
                            var xdecls = getDecls(x)
                            var idecls = getDecls(i)
                            for (ei <- idecls)
                                if (xdecls.exists(_.eq(ei)))
                                    err ::= new TypeChefError(Severity.Warning, h, "warning: Variable " + x.name + " is freed multiple times!", x, "")
                        }
                    }
                }
            }
        }
        err
    }

    def uninitializedMemory(): Boolean = {
        val err = fanalyze.flatMap(uninitializedMemory)

        if (err.isEmpty) {
            println("No usages of uninitialized memory found!")
        } else {
            println(err.map(_.toString + "\n").reduce(_ + _))
        }

        errors ++= err
        err.isEmpty
    }


    private def uninitializedMemory(fa: (FunctionDef, List[(AST, List[Opt[AST]])])): List[TypeChefError] = {
        var err: List[TypeChefError] = List()

        val um = new UninitializedMemory(env, dum, udm, FeatureExprFactory.empty)
        val nss = fa._2.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = um.getRelevantIdUsages(s)
            if (g.size > 0) {
                val in = um.in(s)

                for (((i, _), h) <- in)
                    g.find { case ((t, _), _) => t == i } match {
                        case None =>
                        case Some(((x, _), _)) => {
                            if (h.isSatisfiable(fm)) {
                                var xdecls = getDecls(x)
                                var idecls = getDecls(i)
                                for (ei <- idecls)
                                    if (xdecls.exists(_.eq(ei)))
                                        err ::= new TypeChefError(Severity.Warning, h, "warning: Variable " + x.name + " is used uninitialized!", x, "")
                            }
                        }
                    }
            }
        }

        err
    }

    def xfree(): Boolean = {
        val err = fanalyze.flatMap(xfree)

        if (err.isEmpty) {
            println("No static allocated memory is freed!")
        } else {
            println(err.map(_.toString + "\n").reduce(_ + _))
        }

        errors ++= err
        err.isEmpty
    }


    private def xfree(fa: (FunctionDef, List[(AST, List[Opt[AST]])])): List[TypeChefError] = {
        var err: List[TypeChefError] = List()

        val xf = new XFree(env, dum, udm, FeatureExprFactory.empty, "")
        val nss = fa._2.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = xf.freedVariables(s)
            if (g.size > 0) {
                val in = xf.in(s)

                for (((i,_), h) <- in)
                    g.find(_ == i) match {
                        case None =>
                        case Some(x) => {
                            if (h.isSatisfiable(fm)) {
                                val xdecls = getDecls(x)
                                var idecls = getDecls(i)
                                for (ei <- idecls)
                                    if (xdecls.exists(_.eq(ei)))
                                        err ::= new TypeChefError(Severity.Warning, h, "warning: Variable " + x.name + " is freed although not dynamically allocated!", x, "")
                            }
                        }
                    }
            }
        }

        err
    }

    def danglingSwitchCode(): Boolean = {
        val err = fanalyze.flatMap { x => danglingSwitchCode(x._1) }

        if (err.isEmpty) {
            println("No dangling code in switch statements found!")
        } else {
            println(err.map(_.toString + "\n").reduce(_ + _))
        }

        errors ++= err
        err.isEmpty
    }


    private def danglingSwitchCode(f: FunctionDef): List[TypeChefError] = {
        val ss = filterAllASTElems[SwitchStatement](f)
        val ds = new DanglingSwitchCode(env)

        ss.flatMap(s => {
            ds.danglingSwitchCode(s).map(e => {
                new TypeChefError(Severity.Warning, e.condition, "warning: switch statement has dangling code ", e.entry, "")
            })

        })
    }

    def cfgInNonVoidFunc(): Boolean = {
        val err = fanalyze.flatMap(cfgInNonVoidFunc)

        if (err.isEmpty) {
            println("Control flow in non-void functions always ends in return statements!")
        } else {
            println(err.map(_.toString + "\n").reduce(_ + _))
        }

        errors ++= err
        err.isEmpty
    }

    private def cfgInNonVoidFunc(fa: (FunctionDef, List[(AST, List[Opt[AST]])])): List[TypeChefError] = {
        val cf = new CFGInNonVoidFunc(env, ts)

        cf.cfgInNonVoidFunc(fa._1).map(
            e => new TypeChefError(Severity.Warning, e.condition, "Control flow of non-void function ends here!", e.entry, "")
        )
    }

    def caseTermination(): Boolean = {
        val err = fanalyze.flatMap(caseTermination)

        if (err.isEmpty) {
            println("Case statements with code are properly terminated with break statements!")
        } else {
            println(err.map(_.toString + "\n").reduce(_ + _))
        }

        errors ++= err
        err.isEmpty
    }

    private def caseTermination(fa: (FunctionDef, List[(AST, List[Opt[AST]])])): List[TypeChefError] = {
        val casestmts = filterAllASTElems[CaseStatement](fa._1)

        val ct = new CaseTermination(env)

        casestmts.filterNot(ct.isTerminating).map {
            x => {
                new TypeChefError(Severity.Warning, env.featureExpr(x),
                    "Case statement is not terminated by a break!", x, "")
            }
        }
    }

    def stdLibFuncReturn(): Boolean = {
        val err = fanalyze.flatMap(stdLibFuncReturn)

        if (err.isEmpty) {
            println("Return values of stdlib functions are properly checked for errors!")
        } else {
            println(err.map(_.toString + "\n").reduce(_ + _))
        }

        errors ++= err
        err.isEmpty
    }

    private def stdLibFuncReturn(fa: (FunctionDef, List[(AST, List[Opt[AST]])])): List[TypeChefError] = {
        var err: List[TypeChefError] = List()
        val cl: List[StdLibFuncReturn] = List(
            //new StdLibFuncReturn_EOF(env, dum, udm, fm),

            new StdLibFuncReturn_Null(env, dum, udm, FeatureExprFactory.empty)
        )

        for ((s, _) <- fa._2) {
            for (cle <- cl) {
                lazy val errorvalues = cle.errorreturn.map(PrettyPrinter.print).mkString(" 'or' ")

                // check CFG element directly; without dataflow analysis
                for (e <- cle.checkForPotentialCalls(s)) {
                    err ::= new TypeChefError(Severity.SecurityWarning, env.featureExpr(e), "Return value of " +
                        PrettyPrinter.print(e) + " is not properly checked for (" + errorvalues + ")!", e)
                }


                // stdlib call is assigned to a variable that we track with our dataflow analysis
                // we check whether used variables that hold the value of a stdlib function are killed in s,
                // if not we report an error
                val g = cle.getUsedVariables(s)
                val in = cle.in(s)
                for (((e, _), fi) <- in) {
                    g.find { case ((t, _), _) => t == e } match {
                        case None =>
                        case Some((k@(x, _), _)) => {
                            if (fi.isSatisfiable(fm)) {
                                var xdecls = getDecls(x)
                                var edecls = getDecls(e)

                                for (ee <- edecls) {
                                    val kills = cle.kill(s)
                                    if (xdecls.exists(_.eq(ee)) && !kills.contains(k)) {
                                        err ::= new TypeChefError(Severity.SecurityWarning, fi, "The value of " +
                                            PrettyPrinter.print(e) + " is not properly checked for (" + errorvalues + ")!", e)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        err
    }
}
