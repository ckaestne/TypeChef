
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


sealed abstract class CAnalysisFrontend(tu: TranslationUnit, fm: FeatureModel, opt: ICAnalysisOptions) extends EnforceTreeHelper {
    // the result of CParser is sometimes a DAG instead of an AST
    // prepareAST rewrites the DAG in order to get an AST
    protected val tunit = prepareAST[TranslationUnit](tu)

    // some dataflow analyses need typing (CTypeCache) and/or reference information (CDeclUse)
    protected var tsi: CTypeSystemFrontend with CTypeCache with CDeclUse = null

    protected def ts: CTypeSystemFrontend with CTypeCache with CDeclUse = {
        if (tsi == null) {
            // TODO we always have to enable CTypeCache and CDeclUse, although the selected analyses (see opt) do not use them
            //
            tsi = new CTypeSystemFrontend(tunit, fm) with CTypeCache with CDeclUse
            assert(tsi.checkASTSilent, "typecheck fails!")
            tsi
        } else {
            tsi
        }
    }
    assert(ts.checkASTSilent, "typecheck fails!")

    protected val env = CASTEnv.createASTEnv(tunit)
}

class CInterAnalysisFrontend(tu: TranslationUnit, fm: FeatureModel = FeatureExprFactory.empty, opt: ICAnalysisOptions = CAnalysisDefaultOptions) extends CAnalysisFrontend(tu, fm, opt) with InterCFG with CFGHelper {

    def getTranslationUnit(): TranslationUnit = tunit

    def writeCFG(title: String, writer: CFGWriter) {
        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val env = CASTEnv.createASTEnv(tunit)
        writer.writeHeader(title)

        def lookupFExpr(e: AST): FeatureExpr = e match {
            case o if env.isKnown(o) => env.featureExpr(o)
            case e: ExternalDef => externalDefFExprs.get(e).getOrElse(FeatureExprFactory.True)
            case _ => FeatureExprFactory.True
        }


        for (f <- fdefs) {
            writer.writeMethodGraph(getAllSucc(f, fm, env), lookupFExpr)
        }
        writer.writeFooter()
        writer.close()

        if (writer.isInstanceOf[StringWriter])
            println(writer.toString)
    }
}

class CIntraAnalysisFrontend(tu: TranslationUnit, fm: FeatureModel = FeatureExprFactory.empty, opt: ICAnalysisOptions = CAnalysisDefaultOptions) extends CAnalysisFrontend(tu, fm, opt) with IntraCFG with CFGHelper {

    def doubleFree() = {
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

        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(doubleFreeFunctionDef(_, casestudy))

        if (errors.isEmpty) {
            println("No double frees found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }


    private def doubleFreeFunctionDef(f: FunctionDef, casestudy: String): List[TypeChefError] = {
        var res: List[TypeChefError] = List()

        // It's ok to use FeatureExprFactory.empty here.
        // Using the project's fm is too expensive since control
        // flow computation requires a lot of sat calls.
        // We use the proper fm in DoubleFree (see MonotoneFM).
        val ss = getAllSucc(f, FeatureExprFactory.empty, env).reverse
        val udm = ts.getUseDeclMap
        val df = new DoubleFree(env, udm, fm, casestudy)

        val nss = ss.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = df.gen(s)
            val out = df.out(s)

            for ((i, h) <- out)
                for ((f, j) <- g)
                    j.find(_ == i) match {
                        case None =>
                        case Some(x) => {
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

        res
    }

    def uninitializedMemory(): Boolean = {
        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(uninitializedMemory)

        if (errors.isEmpty) {
            println("No uages of uninitialized memory found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }


    private def uninitializedMemory(f: FunctionDef): List[TypeChefError] = {
        var res: List[TypeChefError] = List()

        // It's ok to use FeatureExprFactory.empty here.
        // Using the project's fm is too expensive since control
        // flow computation requires a lot of sat calls.
        // We use the proper fm in UninitializedMemory (see MonotoneFM).
        val ss = getAllPred(f, FeatureExprFactory.empty, env).reverse
        val udm = ts.getUseDeclMap
        val um = new UninitializedMemory(env, udm, FeatureExprFactory.empty)
        val nss = ss.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = um.getFunctionCallArguments(s)
            val in = um.in(s)

            for ((i, h) <- in)
                for ((f, j) <- g)
                    j.find(_ == i) match {
                        case None =>
                        case Some(x) => {
                            val xdecls = udm.get(x)
                            var idecls = udm.get(i)
                            if (idecls == null)
                                idecls = List(i)
                            for (ei <- idecls)
                                if (xdecls.exists(_.eq(ei)))
                                    res ::= new TypeChefError(Severity.Warning, h, "warning: Variable " + x.name + " is used uninitialized!", x, "")
                        }
                    }
        }

        res
    }

    def xfree(): Boolean = {
        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(xfree)

        if (errors.isEmpty) {
            println("No uages of uninitialized memory found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }


    private def xfree(f: FunctionDef): List[TypeChefError] = {
        var res: List[TypeChefError] = List()

        // It's ok to use FeatureExprFactory.empty here.
        // Using the project's fm is too expensive since control
        // flow computation requires a lot of sat calls.
        // We use the proper fm in UninitializedMemory (see MonotoneFM).
        val ss = getAllPred(f, FeatureExprFactory.empty, env).reverse
        val udm = ts.getUseDeclMap
        val xf = new XFree(env, udm, FeatureExprFactory.empty, "")
        val nss = ss.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = xf.freedVariables(s)
            val in = xf.in(s)

            for ((i, h) <- in)
                for ((f, j) <- g)
                    j.find(_ == i) match {
                        case None =>
                        case Some(x) => {
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

        res
    }

    def danglingSwitchCode(): Boolean = {
        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(danglingSwitchCode)

        if (errors.isEmpty) {
            println("No dangling code in switch statements found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        !errors.isEmpty
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
        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(cfgInNonVoidFunc)

        if (errors.isEmpty) {
            println("Control flow in non-void functions always ends in return statements!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }

    private def cfgInNonVoidFunc(f: FunctionDef): List[TypeChefError] = {
        val cf = new CFGInNonVoidFunc(env, fm, ts)

        cf.cfgInNonVoidFunc(f).map(
            e => new TypeChefError(Severity.Warning, e.feature, "Control flow of non-void function ends here!", e.entry, "")
        )
    }

    def stdLibFuncReturn(): Boolean = {
        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(stdLibFuncReturn)

        if (errors.isEmpty) {
            println("Return values of stdlib functions are properly checked for errors!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }

    private def stdLibFuncReturn(f: FunctionDef): List[TypeChefError] = {
        var errors: List[TypeChefError] = List()
        val ss = getAllSucc(f, FeatureExprFactory.empty, env).map(_._1).filterNot(_.isInstanceOf[FunctionDef])
        val udm = ts.getUseDeclMap
        val cl: List[StdLibFuncReturn] = List(
            //new StdLibFuncReturn_EOF(env, udm, fm),

            new StdLibFuncReturn_Null(env, udm, fm)
        )

        for (s <- ss) {
            for (cle <- cl) {
                lazy val errorvalues = cle.errorreturn.map(PrettyPrinter.print).mkString(" 'or' ")

                // check CFG element directly; without dataflow analysis
                for (e <- cle.checkForPotentialCalls(s)) {
                    errors ::= new TypeChefError(Severity.SecurityWarning, env.featureExpr(e), "Return value of " +
                        PrettyPrinter.print(e) + " is not properly checked for (" + errorvalues + ")!", e)
                }


                // stdlib call is assigned to a variable that we track with our dataflow analysis
                // we check whether used variables that hold the value of a stdlib function are killed in s,
                // if not we report an error
                for ((e, fi) <- cle.out(s)) {
                    for ((fu, u) <- cle.getUsedVariables(s)) {
                        u.find(_ == e) match {
                            case None =>
                            case Some(x) => {
                                val xdecls = udm.get(x)
                                var edecls = udm.get(e)
                                if (edecls == null) edecls = List(e)

                                for (ee <- edecls) {
                                    val kills = cle.kill(s)
                                    if (xdecls.exists(_.eq(ee)) && (!kills.contains(fu) || kills.contains(fu) && !kills(fu).contains(x))) {
                                        errors ::= new TypeChefError(Severity.SecurityWarning, fi, "The value of " +
                                            PrettyPrinter.print(e) + " is not properly checked for (" + errorvalues + ")!", e)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        errors
    }
}
