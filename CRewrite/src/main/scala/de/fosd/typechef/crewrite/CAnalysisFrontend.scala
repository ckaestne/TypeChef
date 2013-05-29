
package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser.c.{TranslationUnit, FunctionDef}
import java.io.{FileWriter, File, Writer, StringWriter}
import de.fosd.typechef.typesystem._

class CAnalysisFrontend(tunit: TranslationUnit, fm: FeatureModel = FeatureExprFactory.empty) extends CFGHelper with EnforceTreeHelper {

    def dumpCFG(writer: Writer = new StringWriter()) {
        val tunittree = prepareAST[TranslationUnit](tunit)
        val fdefs = filterAllASTElems[FunctionDef](tunittree)
        val dump = new DotGraph(writer)
        val env = CASTEnv.createASTEnv(tunittree)
        dump.writeHeader("CFGDump")

        for (f <- fdefs) {
            dump.writeMethodGraph(getAllSucc(f, fm, env), env, Map())
        }
        dump.writeFooter()
        dump.close()

        if (writer.isInstanceOf[StringWriter])
            println(writer.toString)
    }

    def doubleFree() {

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

        val tunittree = prepareAST[TranslationUnit](tunit)
        val ts = new CTypeSystemFrontend(tunittree, fm) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunittree)
        val udm = ts.getUseDeclMap

        val fdefs = filterAllASTElems[FunctionDef](tunittree)
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

        val fn = File.createTempFile("testfiles", ".dot")
        val fw = new FileWriter(fn)

        val dot = new DotGraph(fw)
        dot.writeHeader(f.getName)
        dot.writeMethodGraph(ss, env, Map())
        dot.writeFooter()
        fw.close()

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
                                    res ::= new AnalysisError(h, "warning: Variable " + x.name + " is used uninitialized!", x)
                        }
                    }
        }

        res
    }

    def uninitializedMemory(): Boolean = {
        val tunittree = prepareAST[TranslationUnit](tunit)
        val ts = new CTypeSystemFrontend(tunittree, fm) with CDeclUse
        assert(ts.checkAST, "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunittree)
        val udm = ts.getUseDeclMap

        val fdefs = filterAllASTElems[FunctionDef](tunittree)
        val errors = fdefs.flatMap(uninitializedMemory(_, env, udm))

        if (errors.isEmpty) {
            println("No uages of uninitialized memory found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        !errors.isEmpty
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

            for ((i, h) <- in)
                for ((f, j) <- g)
                    j.find(_ == i) match {
                        case None =>
                        case Some(x) => {
                            val xdecls = udm.get(x)

                            if (xdecls.exists(_.eq(i)))
                                  res ::= new AnalysisError(h, "warning: Variable " + x.name + " is used uninitialized!", x)
                        }
                    }
        }

        res
    }

    def xfree(): Boolean = {
        val tunittree = prepareAST[TranslationUnit](tunit)
        val ts = new CTypeSystemFrontend(tunittree, fm) with CDeclUse
        assert(ts.checkAST, "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunittree)
        val udm = ts.getUseDeclMap

        val fdefs = filterAllASTElems[FunctionDef](tunittree)
        val errors = fdefs.flatMap(xfree(_, env, udm))

        if (errors.isEmpty) {
            println("No uages of uninitialized memory found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        !errors.isEmpty
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
}
