
package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import java.io.StringWriter
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._
import de.fosd.typechef.error.{Severity, TypeChefError}
import de.fosd.typechef.parser.c.SwitchStatement
import scala.Some
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
            case e: ExternalDef => externalDefFExprs.get(e).getOrElse(FeatureExprFactory.True)
            case _ => FeatureExprFactory.True
        }


        for (f <- fdefs) {
            writer.writeMethodGraph(getAllSucc(f, FeatureExprFactory.empty, env).map {
                x => (x._1, x._2.distinct.filter { y => y.feature.isSatisfiable(fm)}) // filter duplicates and wrong succs
            }, lookupFExpr, f.declarator.getName)
        }
        writer.writeFooter()
        writer.close()

        if (writer.isInstanceOf[StringWriter])
            println(writer.toString)
    }
}

// TODO: refactoring different dataflow analyses into a composite will reduce code: handling of invalid paths, error printing ...
class CIntraAnalysisFrontend(tunit: TranslationUnit, ts: CTypeSystemFrontend, fm: FeatureModel = FeatureExprFactory.empty) extends CAnalysisFrontend(tunit) with IntraCFG {

    private lazy val udm = ts.getUseDeclMap
    private lazy val dum = ts.getDeclUseMap

    private val fanalyze = fdefs.map {
        x => (x, getAllSucc(x, FeatureExprFactory.empty, env))
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
        var res: List[TypeChefError] = List()

        val df = new Liveness(env, udm, FeatureExprFactory.empty)

        val nss = fa._2.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val k = df.kill(s)

            if (k.size > 0) {
                val out = df.out(s)

                for ((i, fi) <- k) {
                    out.find { case (t, _) => t == i } match {
                        case None => {
                            var idecls = udm.get(i)
                            if (idecls == null)
                                idecls = List(i)
                            if (idecls.exists(isPartOf(_, fa._1)))
                                res ::= new TypeChefError(Severity.Warning, fi, "warning: Variable " + i.name + " is a dead store!", i, "")
                        }
                        case Some((x, z)) => {
                            if (! z.isTautology(fm)) {
                                val xdecls = udm.get(x)
                                var idecls = udm.get(i)
                                if (idecls == null)
                                    idecls = List(i)
                                for (ei <- idecls) {
                                    // with isPartOf we reduce the number of false positives, since we only check local variables and function parameters.
                                    // an assignment to a global variable might be used in another function
                                    if (isPartOf(ei, fa._1) && xdecls.exists(_.eq(ei)))
                                        res ::= new TypeChefError(Severity.Warning, z.not(), "warning: Variable " + i.name + " is a dead store!", i, "")
                                }

                            }
                        }
                    }
                }
            }
        }

        res
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
        var res: List[TypeChefError] = List()

        val df = new DoubleFree(env, dum, udm, FeatureExprFactory.empty, fa._1, casestudy)

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
                        val xdecls = udm.get(x)
                        var idecls = udm.get(i)
                        if (idecls == null)
                            idecls = List(i)
                        for (ei <- idecls)
                            if (xdecls.exists(_.eq(ei)))
                                res ::= new TypeChefError(Severity.Warning, h, "warning: Variable " + x.name + " is freed multiple times!", x, "")
                    }
                }
        }

            }
        }
        res
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
        var res: List[TypeChefError] = List()

        val um = new UninitializedMemory(env, dum, udm, FeatureExprFactory.empty, fa._1)
        val nss = fa._2.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = um.getFunctionCallArguments(s)
            if (g.size > 0) {
            val in = um.in(s)

            for (((i, _), h) <- in)
                g.find { case ((t, _), _) => t == i } match {
                    case None =>
                    case Some(((x, _), _)) => {
                            if (h.isSatisfiable(fm)) {
                                var xdecls = udm.get(x)
                                if (xdecls == null)
                                    xdecls = List(x)
                        var idecls = udm.get(i)
                        if (idecls == null)
                            idecls = List(i)
                        for (ei <- idecls)
                            if (xdecls.exists(_.eq(ei)))
                                res ::= new TypeChefError(Severity.Warning, h, "warning: Variable " + x.name + " is used uninitialized!", x, "")
                    }
                        }
                    }
                }
        }

        res
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
        var res: List[TypeChefError] = List()

        val xf = new XFree(env, dum, udm, FeatureExprFactory.empty, fa._1, "")
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
                        val xdecls = udm.get(x)
                        var idecls = udm.get(i)
                        if (idecls == null)
                            idecls = List(i)
                        for (ei <- idecls)
                            if (xdecls.exists(_.eq(ei)))
                                res ::= new TypeChefError(Severity.Warning, h, "warning: Variable " + x.name + " is freed although not dynamically allocted!", x, "")
                    }
                        }
                    }
                }
        }

        res
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
        val ds = new DanglingSwitchCode(env, FeatureExprFactory.empty)

        ss.flatMap(s => {
            ds.danglingSwitchCode(s).map(e => {
                new TypeChefError(Severity.Warning, e.feature, "warning: switch statement has dangling code ", e.entry, "")
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
        val cf = new CFGInNonVoidFunc(env, fm, ts)

        cf.cfgInNonVoidFunc(fa._1).map(
            e => new TypeChefError(Severity.Warning, e.feature, "Control flow of non-void function ends here!", e.entry, "")
        )
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
            //new StdLibFuncReturn_EOF(env, udm, fm),

            new StdLibFuncReturn_Null(env, dum, udm, FeatureExprFactory.empty, fa._1)
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
                for (((e, _), fi) <- cle.out(s))
                    g.find(_ == e) match {
                        case None =>
                        case Some(x) => {
                            if (fi.isSatisfiable(fm)) {
                            val xdecls = udm.get(x)
                            var edecls = udm.get(e)
                            if (edecls == null) edecls = List(e)

                            for (ee <- edecls) {
                                val kills = cle.kill(s)
                                if (xdecls.exists(_.eq(ee)) && !kills.contains(x._1)) {
                                        err ::= new TypeChefError(Severity.SecurityWarning, fi, "The value of " +
                                        PrettyPrinter.print(e) + " is not properly checked for (" + errorvalues + ")!", e)
                                }
                            }
                        }
                    }
            }
        }

        }
        errors ++= err
        err
    }
}
