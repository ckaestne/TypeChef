
package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import java.io.StringWriter
import scala.Some
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CDeclUse, CTypeCache, CTypeSystemFrontend}

sealed abstract class CAnalysisFrontend(tu: TranslationUnit, fm: FeatureModel, opt: ICAnalysisOptions) extends EnforceTreeHelper {
    // the result of CParser is sometimes a DAG instead of an AST
    // prepareAST rewrites the DAG in order to get an AST
    protected val tunit = prepareAST[TranslationUnit](tu)

    // some dataflow analyses need typing information
    // this flag enables the use of CTypeCache in CTypeSystemFrontend
    protected val cacheTypes: Boolean
    protected lazy val ts = if (cacheTypes) new CTypeSystemFrontend(tunit, fm) with CTypeCache with CDeclUse
                            else new CTypeSystemFrontend(tunit, fm) with CDeclUse

    // we need to make sure that the input is free of typing errors
    assert(ts.checkASTSilent, "typecheck fails!")
    protected val env = CASTEnv.createASTEnv(tunit)
}

class CInterAnalysisFrontend(tu: TranslationUnit, fm: FeatureModel = FeatureExprFactory.empty, opt: ICAnalysisOptions = CAnalysisDefaultOptions) extends CAnalysisFrontend(tu, fm, opt) with InterCFG with CFGHelper {

    protected val cacheTypes = false
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

    protected val cacheTypes: Boolean = false // use opt.<param> to enable cacheTypes for a specific analysis

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

    private def doubleFreeFunctionDef(f: FunctionDef, casestudy: String): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

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
                                    res ::= new AnalysisError(h, "warning: Variable " + x.name + " is freed multiple times!", x)
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

    private def uninitializedMemory(f: FunctionDef): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

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

            println(PrettyPrinter.print(s), " g: ", g, "i: ", in)
            println("g: ", g.values.flatten.map(System.identityHashCode(_)))
            println("i: ", in.map(_._1).map(System.identityHashCode(_)))

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
                                    res ::= new AnalysisError(h, "warning: Variable " + x.name + " is used uninitialized!", x)
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

    private def xfree(f: FunctionDef): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

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
                                    res ::= new AnalysisError(h, "warning: Variable " + x.name + " is freed although not dynamically allocted!", x)
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

    private def danglingSwitchCode(f: FunctionDef): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

        val ss = filterAllASTElems[SwitchStatement](f)

        for (s <- ss) {
            val ds = new DanglingSwitchCode(env, FeatureExprFactory.empty).computeDanglingCode(s)

            if (!ds.isEmpty) {
                for (e <- ds)
                    res ::= new AnalysisError(e.feature, "warning: switch statement has dangling code ", e.entry)
            }
        }

        res
    }
}
