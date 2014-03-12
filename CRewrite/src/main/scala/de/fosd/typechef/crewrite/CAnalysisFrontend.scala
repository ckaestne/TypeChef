
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
    val fDefs: List[FunctionDef] = filterAllASTElems[FunctionDef](tunit)

}

class CInterAnalysisFrontend(tunit: TranslationUnit, fm: FeatureModel = FeatureExprFactory.empty)
    extends CAnalysisFrontend(tunit) with InterCFG {

    def getTranslationUnit(): TranslationUnit = tunit

    def writeCFG(title: String, writer: CFGWriter) {
        val env = CASTEnv.createASTEnv(tunit)
        writer.writeHeader(title)

        def lookupFExpr(e: AST): FeatureExpr = e match {
            case o if env.isKnown(o) => env.featureExpr(o)
            case e: ExternalDef => externalDefFExprs.get(e).getOrElse(FeatureExprFactory.True)
            case _ => FeatureExprFactory.True
        }

        for (fDef <- fDefs) {
            val fName = fDef.declarator.getName
            val cfg = getAllSucc(fDef, env)
            val cleanedCfg = cfg.map {
                x => (x._1, x._2.distinct.filter {y => y.feature.isSatisfiable(fm)}) // filter duplicates and wrong succs
            }
            writer.writeMethodGraph(cleanedCfg, lookupFExpr, fName)
        }
        writer.writeFooter()
        writer.close()

        if (writer.isInstanceOf[StringWriter])
            println(writer.toString)
    }
}

// TODO: refactoring different dataflow analyses into a composite will reduce code:
// handling of invalid paths, error printing ...
class CIntraAnalysisFrontend(tunit: TranslationUnit, ts: CTypeSystemFrontend with CDeclUse,
                             fm: FeatureModel = FeatureExprFactory.empty) extends CAnalysisFrontend(tunit) with IntraCFG {

    private lazy val udm = ts.getUseDeclMap
    private lazy val dum = ts.getDeclUseMap

    private def getDecls(key: Id): List[Id] = {
        if (! udm.containsKey(key)) List(key)
        else udm.get(key).filter { d => env.featureExpr(d) and env.featureExpr(key) isSatisfiable fm }
    }

    private val fAnalyze = fDefs.map {
        x => (x, getAllSucc(x, env).filterNot {x => x._1.isInstanceOf[FunctionDef]})
    }

    var errors: List[TypeChefError] = List()

    def deadStore(): Boolean = {
        val err = fAnalyze.flatMap(deadStore)

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

        for (s <- nss) {
            for ((i, fi) <- df.kill(s)) {
                val out = df.out(s)

                // code such as "int a;" occurs frequently and issues an error
                // we filter them out by checking the declaration use map for usages
                if (dum.containsKey(i) && dum.get(i).nonEmpty) {}
                else out.find {case (t, _) => t == i} match {
                    case None =>
                        val idecls = getDecls(i)
                        if (idecls.exists(isPartOf(_, fa._1)))
                            err ::= new TypeChefError(Severity.Warning, fi, "warning: Variable " + i.name +
                                " is a dead store!", i, "")
                    case Some((x, liveCondition)) =>
                        // we use Liveness to determine dead stores;
                        // in the case of the liveness conditions is not satisfiable
                        // we emit an error
                        if (! liveCondition.isSatisfiable(fm)) {
                            val xdecls = getDecls(x)
                            val idecls = getDecls(i)
                            for (iEntry <- idecls) {
                                // with isPartOf we reduce the number of false positives, since we only
                                // check local variables and function parameters.
                                // an assignment to a global variable might be used in another function
                                if (isPartOf(iEntry, fa._1) && xdecls.exists(_.eq(iEntry)))
                                    err ::= new TypeChefError(Severity.Warning, liveCondition.not(), "warning: Variable " +
                                        i.name + " is a dead store!", i, "")
                            }

                        }
                }
            }
        }

        err
    }

    def doubleFree(): Boolean = {
        val caseStudy = {
            tunit.getFile match {
                case None => ""
                case Some(x) =>
                    if (x.contains("linux")) "linux"
                    else if (x.contains("openssl")) "openssl"
                    else ""
            }
        }

        val err = fAnalyze.flatMap(doubleFree(_, caseStudy))

        if (err.isEmpty) {
            println("No double frees found!")
        } else {
            println(err.map(_.toString + "\n").reduce(_ + _))
        }

        errors ++= err
        err.isEmpty
    }


    private def doubleFree(fa: (FunctionDef, List[(AST, List[Opt[AST]])]), caseStudy: String): List[TypeChefError] = {
        var err: List[TypeChefError] = List()
        val df = new DoubleFree(env, dum, udm, FeatureExprFactory.empty, fa._1, caseStudy)
        val nss = fa._2.map(_._1)

        for (s <- nss) {
            val g = df.gen(s)
            if (g.nonEmpty) {
                val in = df.in(s)

                for (((i, _), errCondition) <- in)
                    g.find {case ((t, _), _) => t == i} match {
                        case None =>
                        case Some(((x, _), _)) =>
                            if (errCondition.isSatisfiable(fm)) {
                                val xDecls = getDecls(x)
                                val iDecls = getDecls(i)
                                for (iEntry <- iDecls)
                                    if (xDecls.exists(_.eq(iEntry)))
                                        err ::= new TypeChefError(Severity.Warning, errCondition, "warning: Variable " +
                                            x.name + " is freed multiple times!", x, "")
                            }
                    }
            }
        }
        err
    }

    def uninitializedMemory(): Boolean = {
        val err = fAnalyze.flatMap(uninitializedMemory)

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
        val nss = fa._2.map(_._1)

        for (s <- nss) {
            val g = um.getRelevantIdUsages(s)
            if (g.nonEmpty) {
                val in = um.in(s)

                for (((i, _), errCondition) <- in)
                    g.find {case ((t, _), _) => t == i} match {
                        case None =>
                        case Some(((x, _), _)) =>
                            if (errCondition.isSatisfiable(fm)) {
                                val xDecls = getDecls(x)
                                val iDecls = getDecls(i)
                                for (iEntry <- iDecls)
                                    if (xDecls.exists(_.eq(iEntry)))
                                        err ::= new TypeChefError(Severity.Warning, errCondition, "warning: Variable " +
                                            x.name + " is used uninitialized!", x, "")
                            }
                    }
            }
        }

        err
    }

    def xfree(): Boolean = {
        val err = fAnalyze.flatMap(xfree)

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
        val nss = fa._2.map(_._1)

        for (s <- nss) {
            val g = xf.freedVariables(s)
            if (g.nonEmpty) {
                val in = xf.in(s)

                for (((i, _), errCondition) <- in)
                    g.find(_ == i) match {
                        case None =>
                        case Some(x) =>
                            if (errCondition.isSatisfiable(fm)) {
                                val xDecls = getDecls(x)
                                val iDecls = getDecls(i)
                                for (iEntry <- iDecls)
                                    if (xDecls.exists(_.eq(iEntry)))
                                        err ::= new TypeChefError(Severity.Warning, errCondition, "warning: Variable " +
                                            x.name + " is freed although not dynamically allocated!", x, "")
                            }
                    }
            }
        }

        err
    }

    def danglingSwitchCode(): Boolean = {
        val err = fAnalyze.flatMap {x => danglingSwitchCode(x._1)}

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
        val err = fAnalyze.flatMap(cfgInNonVoidFunc)

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
        val err = fAnalyze.flatMap(caseTermination)

        if (err.isEmpty) {
            println("Case statements with code are properly terminated with break statements!")
        } else {
            println(err.map(_.toString + "\n").reduce(_ + _))
        }

        errors ++= err
        err.isEmpty
    }

    private def caseTermination(fa: (FunctionDef, List[(AST, List[Opt[AST]])])): List[TypeChefError] = {
        val caseStmts = filterAllASTElems[CaseStatement](fa._1)
        val ct = new CaseTermination(env)

        caseStmts.filterNot(ct.isTerminating).map {
            x => {
                new TypeChefError(Severity.Warning, env.featureExpr(x),
                    "Case statement is not terminated by a break!", x, "")
            }
        }
    }

    def stdLibFuncReturn(): Boolean = {
        val err = fAnalyze.flatMap(stdLibFuncReturn)

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
            new StdLibFuncReturn_Null(env, dum, udm, FeatureExprFactory.empty, fa._1)
        )
        val nss = fa._2.map(_._1)

        for (s <- nss) {
            for (cle <- cl) {
                lazy val errorValues = cle.errorReturn.map(PrettyPrinter.print).mkString(" 'or' ")

                // check CFG element directly; without dataflow analysis
                for (e <- cle.checkForPotentialCalls(s)) {
                    err ::= new TypeChefError(Severity.SecurityWarning, env.featureExpr(e), "Return value of " +
                        PrettyPrinter.print(e) + " is not properly checked for (" + errorValues + ")!", e)
                }

                // stdlib call is assigned to a variable that we track with our dataflow analysis
                // we check whether used variables that hold the value of a stdlib function are killed in s or not,
                // if it is not in s, we report an error
                val g = cle.getUsedVariables(s)
                val in = cle.in(s)
                for (((i, _), errCondition) <- in) {
                    g.find {case ((elem, _), _) => elem == i} match {
                        case None =>
                        case Some((k@(x, _), _)) =>
                            if (errCondition.isSatisfiable(fm)) {
                                val xDecls = getDecls(x)
                                val iDecls = getDecls(i)

                                for (iEntry <- iDecls) {
                                    val kills = cle.kill(s)
                                    if (xDecls.exists(_.eq(iEntry)) && !kills.contains(k)) {
                                        err ::= new TypeChefError(Severity.SecurityWarning, errCondition, "The value of " +
                                            PrettyPrinter.print(i) + " is not properly checked for (" + errorValues + ")!", i)
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
