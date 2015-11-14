package de.fosd.typechef.parser.c

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import java.io.{FileWriter, File}
import FeatureExprFactory.True
import java.util.Collections
import de.fosd.typechef.error.{WithPosition, Position}
import de.fosd.typechef.conditional.{One, Choice, Opt}
import de.fosd.typechef.lexer.LexerFrontend
import org.kiama.rewriting.Rewriter._
import org.kiama.rewriting.Strategy

object MyUtil {
    implicit def runnable(f: () => Unit): Runnable =
        new Runnable() {
            override def run() = f()
        }
}
//
//object ParserMain {
//    import scala.collection.JavaConversions._
//
//    def main(args: Array[String]) = {
//        val parserMain = new ParserMain(new CParser(null))
//
//        for (filename <- args) {
//            println("**************************************************************************")
//            println("** Processing file: " + filename)
//            println("**************************************************************************")
////            val currentDir = new File(filename).getParent()
////            parserMain.parserMain(filename, Collections.singletonList(currentDir))
//
//            val currentDir = new File(filename).getParent()
//            val lexer = (() => CLexer.prepareTokens(new LexerFrontend().parseFile(filename, Collections.singletonList(currentDir), null)))
//            parserMain.parserMain(lexer, new CTypeContext(), DefaultParserOptions)
//
//            println("**************************************************************************")
//            println("** End of processing for: " + filename)
//            println("**************************************************************************")
//        }
//    }
//}


class ParserMain(p: CParser) {

//    /**
//     * debug and testing function only; do not use for serious processing since it ignores all lexer options
//     */
//    def parserMain(filePath: String, systemIncludePath: java.util.List[String], parserOptions: ParserOptions = DefaultParserOptions): TranslationUnit = {
//        val lexer = (() => CLexer.lexFile(filePath, systemIncludePath, p.featureModel))
//        parserMain(lexer, new CTypeContext(), parserOptions)
//    }

    def parserMain(tokenstream: TokenReader[CToken, CTypeContext], parserOptions: ParserOptions, fullFeatureModel: FeatureModel): TranslationUnit = {
        parserMain((() => tokenstream), new CTypeContext(), parserOptions, fullFeatureModel)
    }


    def parserMain(lexer: () => TokenReader[CToken, CTypeContext], initialContext: CTypeContext, parserOptions: ParserOptions, fullFeatureModel: FeatureModel): TranslationUnit = {
        assert(parserOptions != null)
        val ctx = True
        val in: p.Input = lexer().setContext(initialContext)

        val parserStartTime = System.currentTimeMillis
        val result2: p.MultiParseResult[TranslationUnit] = p.phrase(p.translationUnit)(in, ctx)
        val result = result2.prune(fullFeatureModel)
        val endTime = System.currentTimeMillis

        //ensure that "did not reach end errors are handled as part of the phrase combinator
        result.mapr({
            case x@p.Success(_, rest) => assert(rest.atEnd, "phrase() should have ensured reaching the end of the tokenstream in a success case"); x
            case x => x
        })

        //print parsing results to sysout and the the error file (if configured)
        if (parserOptions.printParserResult)
            println(printParseResult(result, ctx))
        renderParseResult(result, ctx, parserOptions.renderParserError)

        //print statistics if configured
        if (parserOptions.printParserStatistics) {
            val distinctFeatures = getDistinctFeatures(in.tokens) //expensive to calculate with bdds (at least the current implementation)
            print("Parsing statistics: \n" +
                "  Duration parsing: " + (endTime - parserStartTime) + " ms\n" +
                "  Tokens: " + in.tokens.size + "\n")
            if (in.first.isInstanceOf[ProfilingToken])
                print(
                    "  Tokens Consumed: " + ProfilingTokenHelper.totalConsumed(in.asInstanceOf[TokenReader[ProfilingToken, CTypeContext]]) + "\n" +
                        "  Tokens Backtracked: " + ProfilingTokenHelper.totalBacktracked(in.asInstanceOf[TokenReader[ProfilingToken, CTypeContext]]) + "\n" +
                        "  Tokens Repeated: " + ProfilingTokenHelper.totalRepeated(in.asInstanceOf[TokenReader[ProfilingToken, CTypeContext]]) + "\n");
            //                "  Repeated Distribution: " + ProfilingTokenHelper.repeatedDistribution(in) + "\n" +
            print(
                "  Conditional Tokens: " + countConditionalTokens(in.tokens) + "\n" +
                    "  Distinct Features#: " + distinctFeatures.size + "\n" +
                    "  Distinct Features: " + distinctFeatures.toList.sorted.mkString(";") + "\n" +
                    "  Distinct Feature Expressions: " + countFeatureExpr(in.tokens) + "\n" +
                    "  Choice Nodes: " + countChoiceNodes(result) + "\n\n")
        }


        //return null (if parsing failed in all branches) or a single AST combining all parse results
        val ast = mergeResultsIntoSingleAST(ctx, result)
        if (parserOptions.simplifyPresenceConditions) {
            if (FeatureExprFactory.default == FeatureExprFactory.bdd) {
                return simplifyPresenceConditions(ast, FeatureExprFactory.True)
            } else {
                print("\"-bdd\" option required to simplify AST presence conditions.\n")
            }
        }

        ast
    }
    /**
     * Simplifies presence conditions on ast nodes in the AST.
     * AST is not changed but a new AST with changed pcs is returned. Positions are copied.
     * This method is based on Florian Garbe's method prepareASTforIfdef in de.fosd.typechef.cifdeftoif.IfdefToIf in the Hercules fork of TypeChef.
     */
    def simplifyPresenceConditions(ast: TranslationUnit, ctx: FeatureExpr = FeatureExprFactory.True): TranslationUnit = {
        val astEnv = de.fosd.typechef.parser.c.CASTEnv.createASTEnv(ast)
        def traverseASTrecursive[T <: Product](t: T, currentContext: FeatureExpr = FeatureExprFactory.True): T = {
            val r = alltd(rule[Any] {
                case l: List[_] =>
                    l.flatMap(x => x match {
                        case o@Opt(ft: FeatureExpr, entry) =>
                            if (!ft.and(currentContext).isSatisfiable()) {
                                // current context makes ft impossible (-> ft == false and we can omit the node)
                                List()
                                    } else {
                                List(traverseASTrecursive(Opt(ft.simplify(currentContext), entry), ft.and(currentContext)))
                                    }
                    })
                case c@Choice(ft, thenBranch, elseBranch) =>
                    val ctx = astEnv.featureExpr(c)
                    val newChoiceFeature = ft.simplify(ctx)
                    val result = Choice(newChoiceFeature,
                        traverseASTrecursive(thenBranch,ctx.and(newChoiceFeature)) ,
                        traverseASTrecursive(elseBranch,ctx.and(newChoiceFeature.not)))
                    result
            })
            r(t) match {
                case None =>
                    t
                case k =>
                    k.get.asInstanceOf[T]
                                }
                            }
        traverseASTrecursive(ast, ctx)
    }

    /**
     * merges multiple results into a single AST (possibly empty)
     *
     * in the merged result, all top-level declarations have presence conditions
     * restricted to the context where parsing was successful
     *
     * ideally, there should not be multiple successful results, because typechef should
     * have merged them before. here is a very simple merge strategy where the individual
     * top-level declarations are simply concatenated (with the mutually exclusive presence
     * conditions)
     */
    private def mergeResultsIntoSingleAST(ctx: FeatureExpr, result: p.MultiParseResult[TranslationUnit]): TranslationUnit = {

        def collectTopLevelDeclarations(ctx: FeatureExpr, result: p.MultiParseResult[TranslationUnit]): List[Opt[ExternalDef]] = {
            result match {
                case p.Success(r: TranslationUnit, in) => r.defs.map(_.and(ctx))
                case p.NoSuccess(_, _, _) => List()
                case p.SplittedParseResult(f, left, right) =>
                    collectTopLevelDeclarations(ctx and f, left) ++
                        collectTopLevelDeclarations(ctx andNot f, right)
            }
        }

        if (result.allFailed) null
        else TranslationUnit(collectTopLevelDeclarations(ctx, result))
    }


    def renderParseResult[T](result: p.MultiParseResult[T], feature: FeatureExpr, renderError: (FeatureExpr, String, Position) => Object): Unit =
        if (renderError != null)
            result.mapfr(feature, {
                case (f, x@p.Success(ast, unparsed)) => x
                case (f, x@p.NoSuccess(msg, unparsed, inner)) =>
                    renderError(f, msg + " (" + inner + ")", unparsed.pos); x
            })

    def printParseResult(result: p.MultiParseResult[Any], feature: FeatureExpr): String = {
        result match {
            case p.Success(ast, unparsed) =>
                (feature.toString + "\tparsing succeeded\n")
            case p.NoSuccess(msg, unparsed, inner) =>
                (feature.toString + "\tfailed: " + msg + " at " + unparsed.pos + " (" + inner + ")\n")
            case p.SplittedParseResult(f, left, right) => {
                printParseResult(left, feature.and(f)) + "\n" +
                    printParseResult(right, feature.and(f.not))
            }
        }
    }


    def countConditionalTokens(tokens: List[AbstractToken]): Int =
        tokens.count(_.getFeature != FeatureExprFactory.True)

    def getDistinctFeatures(tokens: List[AbstractToken]): Set[String] = {
        var features: Set[String] = Set()
        for (t <- tokens)
            features ++= t.getFeature.collectDistinctFeatures
        features
    }


    def printDistinctFeatures(tokens: List[AbstractToken], filename: String) {
        val w = new FileWriter(new File(filename))
        w.write(getDistinctFeatures(tokens).toList.sorted.mkString("\n"))
        w.close()
    }

    def countFeatureExpr(tokens: List[AbstractToken]): Int =
        tokens.foldLeft[Set[FeatureExpr]](Set())(_ + _.getFeature).size

    def countChoiceNodes(ast: p.MultiParseResult[AST]): Int = ast match {
        case p.Success(ast, _) => countChoices(ast)
        case _ => -1
    }


    def countChoices(ast: AST): Int = {
        var result: Int = 0
        //        ast.accept(new ASTVisitor {
        //            def visit(node: AST, ctx: FeatureExpr) {
        //                if (node.isInstanceOf[Choice[_]])
        //                    result += 1
        //                for (opt <- node.getInnerOpt)
        //                    if (opt.feature != FeatureExprFactory.True && opt.feature != ctx)
        //                        if (!((ctx implies (opt.feature)).isTautology)) {
        //                            result += 1
        //                        }
        //            }
        //            def postVisit(node: AST, feature: FeatureExpr) {}
        //        })
        result
    }

    /* match {
        case x: AbstractToken => 0
        case a ~ b => countCoices(a, ctx) + countCoices(b, ctx)
        case l: List[Any] => l.foldLeft[Int](0)(_ + countCoices(_, ctx))
        case Opt(f, r) =>
            countCoices(r, ctx and f) + (if ((ctx implies f).isTautology)
                0
            else
                1)
        case c: Choice => countCoices(c.left, ctx and c.feature) + countCoices(c.right, ctx andNot (c.feature)) + 1
        case Some(x) => countCoices(x, ctx)
        case None => 0
        case x: String => 0
        case e => {println(e); assert(false); 0}
    }*/
}