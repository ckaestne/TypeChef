
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
    private val fdefs = filterAllASTElems[FunctionDef](tunit)
    protected val fanalyze = fdefs.map {
        x => (x, getAllSucc(x, FeatureExprFactory.empty, env))
    }
}

class CInterAnalysisFrontend(tunit: TranslationUnit, fm: FeatureModel = FeatureExprFactory.empty) extends CAnalysisFrontend(tunit) with InterCFG with CFGHelper {

    def getTranslationUnit(): TranslationUnit = tunit

    def writeCFG(title: String, writer: CFGWriter) {
        val env = CASTEnv.createASTEnv(tunit)
        writer.writeHeader(title)

        def lookupFExpr(e: AST): FeatureExpr = e match {
            case o if env.isKnown(o) => env.featureExpr(o)
            case e: ExternalDef => externalDefFExprs.get(e).getOrElse(FeatureExprFactory.True)
            case _ => FeatureExprFactory.True
        }

        // although we use fanalyze here, we recompute getAllSucc with fm
        // TODO: simplify filtering result with SatSolver.
        for ((f, _) <- fanalyze) {
            writer.writeMethodGraph(getAllSucc(f, fm, env), lookupFExpr)
        }
        writer.writeFooter()
        writer.close()

        if (writer.isInstanceOf[StringWriter])
            println(writer.toString)
    }
}

// TODO: Running all analyses on a per function level would be faster, since reoccuring computations such as getAllSucc had to be done only once. However determining analysis times would take more effort.
class CIntraAnalysisFrontend(tunit: TranslationUnit, ts: CTypeSystemFrontend with CTypeCache with CDeclUse, fm: FeatureModel = FeatureExprFactory.empty) extends CAnalysisFrontend(tunit) with IntraCFG with CFGHelper {

    private lazy val udm = ts.getUseDeclMap
    private lazy val dum = ts.getDeclUseMap

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

        val errors = fanalyze.flatMap(doubleFree(_, casestudy))

        if (errors.isEmpty) {
            println("No double frees found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }


    private def doubleFree(fa: (FunctionDef, List[(AST, List[Opt[AST]])]), casestudy: String): List[TypeChefError] = {
        println("analyzing following function with doublefree: " + fa._1.getName)
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
        val errors = fanalyze.flatMap(uninitializedMemory)

        if (errors.isEmpty) {
            println("No usages of uninitialized memory found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }


    private def uninitializedMemory(fa: (FunctionDef, List[(AST, List[Opt[AST]])])): List[TypeChefError] = {
        println("analyzing following function with uninitializedmemory: " + fa._1.getName)
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
        val errors = fanalyze.flatMap(xfree)

        if (errors.isEmpty) {
            println("No static allocated memory is freed!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }


    private def xfree(fa: (FunctionDef, List[(AST, List[Opt[AST]])])): List[TypeChefError] = {
        println("analyzing following function with xfree: " + fa._1.getName)
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
        val errors = fanalyze.flatMap { x => danglingSwitchCode(x._1) }

        if (errors.isEmpty) {
            println("No dangling code in switch statements found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        !errors.isEmpty
    }


    private def danglingSwitchCode(f: FunctionDef): List[TypeChefError] = {
        println("analyzing following function with danglingswitch: " + f.getName)
        val ss = filterAllASTElems[SwitchStatement](f)
        val ds = new DanglingSwitchCode(env, FeatureExprFactory.empty)

        ss.flatMap(s => {
            ds.danglingSwitchCode(s).map(e => {
                new TypeChefError(Severity.Warning, e.feature, "warning: switch statement has dangling code ", e.entry, "")
            })

        })
    }

    def cfgInNonVoidFunc(): Boolean = {
        val errors = fanalyze.flatMap(cfgInNonVoidFunc)

        if (errors.isEmpty) {
            println("Control flow in non-void functions always ends in return statements!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }

    private def cfgInNonVoidFunc(fa: (FunctionDef, List[(AST, List[Opt[AST]])])): List[TypeChefError] = {
        println("analyzing function (cfginnonvoid): " + fa._1.getName)
        val cf = new CFGInNonVoidFunc(env, fm, ts)

        cf.cfgInNonVoidFunc(fa._1).map(
            e => new TypeChefError(Severity.Warning, e.feature, "Control flow of non-void function ends here!", e.entry, "")
        )
    }

    def stdLibFuncReturn(): Boolean = {
        val errors = fanalyze.flatMap(stdLibFuncReturn)

        if (errors.isEmpty) {
            println("Return values of stdlib functions are properly checked for errors!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }

    private def stdLibFuncReturn(fa: (FunctionDef, List[(AST, List[Opt[AST]])])): List[TypeChefError] = {
        println("analyzing following function with stdlibfuncreturn: " + fa._1.getName)
        var errors: List[TypeChefError] = List()
        val cl: List[StdLibFuncReturn] = List(
            //new StdLibFuncReturn_EOF(env, udm, fm),

            new StdLibFuncReturn_Null(env, dum, udm, FeatureExprFactory.empty, fa._1)
        )

        for ((s, _) <- fa._2) {
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
                                        errors ::= new TypeChefError(Severity.SecurityWarning, fi, "The value of " +
                                            PrettyPrinter.print(e) + " is not properly checked for (" + errorvalues + ")!", e)
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
