
package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import java.io.StringWriter
import de.fosd.typechef.typesystem._
import scala.Some
import de.fosd.typechef.parser.c._

sealed abstract class CAnalysisFrontend(tu: TranslationUnit) extends EnforceTreeHelper {
    protected val tunit = prepareAST[TranslationUnit](tu)
}

class CInterAnalysisFrontend(tu: TranslationUnit, fm: FeatureModel = FeatureExprFactory.empty) extends CAnalysisFrontend(tu) with InterCFG with CFGHelper {

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

class CIntraAnalysisFrontend(tu: TranslationUnit, fm: FeatureModel = FeatureExprFactory.empty) extends CAnalysisFrontend(tu) with IntraCFG with CFGHelper {

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

        val ts = new CTypeSystemFrontend(tunit, fm) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunit)
        val udm = ts.getUseDeclMap

        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(doubleFreeFunctionDef(_, env, udm, casestudy))

        if (errors.isEmpty) {
            println("No double frees found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }

    private def doubleFreeFunctionDef(f: FunctionDef, env: ASTEnv, udm: UseDeclMap, casestudy: String): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

        // It's ok to use FeatureExprFactory.empty here.
        // Using the project's fm is too expensive since control
        // flow computation requires a lot of sat calls.
        // We use the proper fm in DoubleFree (see MonotoneFM).
        val ss = getAllSucc(f, FeatureExprFactory.empty, env).reverse
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
        val ts = new CTypeSystemFrontend(tunit, fm) with CDeclUse
        assert(ts.checkAST(), "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunit)
        val udm = ts.getUseDeclMap

        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(uninitializedMemory(_, env, udm))

        if (errors.isEmpty) {
            println("No uages of uninitialized memory found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }

    private def uninitializedMemory(f: FunctionDef, env: ASTEnv, udm: UseDeclMap): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

        // It's ok to use FeatureExprFactory.empty here.
        // Using the project's fm is too expensive since control
        // flow computation requires a lot of sat calls.
        // We use the proper fm in UninitializedMemory (see MonotoneFM).
        val ss = getAllPred(f, FeatureExprFactory.empty, env).reverse
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
        val ts = new CTypeSystemFrontend(tunit, fm) with CDeclUse
        assert(ts.checkAST(), "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunit)
        val udm = ts.getUseDeclMap

        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(xfree(_, env, udm))

        if (errors.isEmpty) {
            println("No uages of uninitialized memory found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }

    private def xfree(f: FunctionDef, env: ASTEnv, udm: UseDeclMap): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

        // It's ok to use FeatureExprFactory.empty here.
        // Using the project's fm is too expensive since control
        // flow computation requires a lot of sat calls.
        // We use the proper fm in UninitializedMemory (see MonotoneFM).
        val ss = getAllPred(f, FeatureExprFactory.empty, env).reverse
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
        val ts = new CTypeSystemFrontend(tunit, fm) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunit)

        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(danglingSwitchCode(_, env))

        if (errors.isEmpty) {
            println("No dangling code in switch statements found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        !errors.isEmpty
    }

    private def danglingSwitchCode(f: FunctionDef, env: ASTEnv): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

        val ss = filterAllASTElems[SwitchStatement](f)

        for (s <- ss) {
            val ds = new DanglingSwitchCode(env, FeatureExprFactory.empty).computeDanglingCode(s)

            if (! ds.isEmpty) {
                for (e <- ds)
                    res ::= new AnalysisError(e.feature, "warning: switch statement has dangling code ", e.entry)
            }
        }

        res
    }
}
