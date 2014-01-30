
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
import de.fosd.typechef.crewrite.asthelper.{ASTEnv, CASTEnv}


sealed abstract class CAnalysisFrontend(tunit: TranslationUnit) extends CFGHelper {

    protected val env: ASTEnv = CASTEnv.createASTEnv(tunit)
    val funDefs: List[FunctionDef] = filterAllASTElems[FunctionDef](tunit)

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


        for (fun <- funDefs) {
            val functionName = fun.declarator.getName
            val cfg = getAllSucc(fun, env)
            val cleanedCfg = cfg.map {
                x => (x._1, x._2.distinct.filter {y => y.feature.isSatisfiable(fm)}) // filter duplicates and wrong succs
            }
            writer.writeMethodGraph(cleanedCfg, lookupFExpr, functionName)
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

    private val fanalyze = funDefs.map {
        x => (x, getAllSucc(x, env).filterNot {x => x._1.isInstanceOf[FunctionDef]})
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

        val df = new Liveness(fa._1, env, udm, FeatureExprFactory.empty)

        val nss = fa._2.map(_._1)

        println("analyzing " + fa._1.getName + " with " + nss.size + " cfg stmts and " + fa._2.map(_._2.size).sum + " succs")

        for (s <- nss) {
            for ((i, fi) <- df.kill(s)) {
                val out = df.out(s)

                // code such as "int a;" occurs frequently and issues an error
                // we filter them out by checking the declaration use map for usages
                if (dum.containsKey(i) && dum.get(i).size > 0) {}
                else out.find {case (t, _) => t == i} match {
                    case None => {
                        var idecls = udm.get(i)
                        if (idecls == null)
                            idecls = List(i)
                        if (idecls.exists(isPartOf(_, fa._1)))
                            err ::= new TypeChefError(Severity.Warning, fi, "warning: Variable " + i.name + " is a dead store!", i, "")
                    }
                    case Some((x, z)) => {
                        if (!z.isTautology(fm)) {
                            var xdecls = udm.get(x)
                            if (xdecls == null)
                                xdecls = List(x)
                            var idecls = udm.get(i)
                            if (idecls == null)
                                idecls = List(i)
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

        val df = new DoubleFree(env, dum, udm, FeatureExprFactory.empty, fa._1, casestudy)

        val nss = fa._2.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = df.gen(s)
            if (g.size > 0) {
                val in = df.in(s)

                for (((i, _), h) <- in)
                    g.find {case ((t, _), _) => t == i} match {
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

        val um = new UninitializedMemory(env, dum, udm, FeatureExprFactory.empty, fa._1)
        val nss = fa._2.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = um.getRelevantIdUsages(s)
            if (g.size > 0) {
                val in = um.in(s)

                for (((i, _), h) <- in)
                    g.find {case ((t, _), _) => t == i} match {
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

        val xf = new XFree(env, dum, udm, FeatureExprFactory.empty, fa._1, "")
        val nss = fa._2.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = xf.freedVariables(s)
            if (g.size > 0) {
                val in = xf.in(s)

                for (((i, _), h) <- in)
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
                                        err ::= new TypeChefError(Severity.Warning, h, "warning: Variable " + x.name + " is freed although not dynamically allocted!", x, "")
                            }
                        }
                    }
            }
        }

        err
    }

    def danglingSwitchCode(): Boolean = {
        val err = fanalyze.flatMap {x => danglingSwitchCode(x._1)}

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
        val cf = new CFGInNonVoidFunc(env, ts)

        cf.cfgInNonVoidFunc(fa._1).map(
            e => new TypeChefError(Severity.Warning, e.feature, "Control flow of non-void function ends here!", e.entry, "")
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
            //new StdLibFuncReturn_EOF(env, dum, udm, fm, fa._1),

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
                val in = cle.in(s)
                for (((e, _), fi) <- in) {
                    println("s", PrettyPrinter.print(s), "in", in, "g", g, "u", cle.uses(s))
                    g.find {case ((t, _), _) => t == e} match {
                        case None =>
                        case Some((k@(x, _), _)) => {
                            if (fi.isSatisfiable(fm)) {
                                var xdecls = udm.get(x)
                                if (xdecls == null)
                                    xdecls = List(x)
                                var edecls = udm.get(e)
                                if (edecls == null)
                                    edecls = List(e)

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
