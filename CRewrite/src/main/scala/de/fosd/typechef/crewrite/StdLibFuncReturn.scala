package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory, FeatureModel}
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._
import de.fosd.typechef.parser.c.PostfixExpr
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.parser.c.FunctionCall
import de.fosd.typechef.conditional.Opt

// https://www.securecoding.cert.org/confluence/display/seccode/ERR33-C.+Detect+and+handle+standard+library+errors
// ERR33-C
//
// the analysis is not precise for several reasons:
// 1. we assume that a variable that we check is in the left part of the expression, e.g., x != EOF vs. EOF != x
// 2. we don't check the various variants on how to express the check, e.g., x != EOF vs. !(x == EOF)
// 3. we don't check what is done after the check and whether it belongs for instance to error handling,
//    e.g., if (x == EOF) { // error handling }
// 4. we don't check whether a variable is only used in a context, in which we can exclude errors,
//    e.g., if (x == EOF) { ... } else { q = x; // no error }; if-then-else check for all conditions
// 5. the check for erroreous return values could be in a separate function, e.g., checkCorrectReturnValue(x)
//
// instance of the monotone framework
// TODO analysis is unfinished and untested!
// L  = P(Var*)
// ⊑  = ⊆             // see MonotoneFW
// ∐  = ??
// ⊥  = ??
// i  = ??
// E  = {FunctionDef} // see MonotoneFW
// F  = ??
// Analysis_○ = ??
// Analysis_● = ??
abstract class StdLibFuncReturn(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFWId(env, udm, fm) with UsedDefinedDeclaredVariables {
    // list of standard library functions and their possible error returns
    // taken from above website
    val function: List[String]
    val errorreturn: List[AST]

    def gen(a: AST) = {
        var res = Set[Id]()

        // we track variables with the return value of a stdlib function call that is in function
        val retvar = manytd(query {
            case AssignExpr(i@Id(_), "=", source) => {
                filterAllASTElems[PostfixExpr](source).map(
                    pfe => pfe match {
                        case PostfixExpr(Id(name), FunctionCall(_)) => if (function.contains(name)) res += i
                    }
                )
            }
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, Some(init)) =>
                filterAllASTElems[PostfixExpr](init).map(
                    pfe => pfe match {
                        case PostfixExpr(Id(name), FunctionCall(_)) => if (function.contains(name)) res += i
                    }
                )
        })

        retvar(a)
        addAnnotations(res)
    }

    private def subtermIsPartOfTerm(subterm: Product, term: Any): List[Product] = {
        term match {
            case x: Product if term == subterm => List(x)
            case l: List[_] => l.flatMap(subtermIsPartOfTerm(subterm, _))
            case p: Product => p.productIterator.toList.flatMap(subtermIsPartOfTerm(subterm, _))
            case _ => List()
        }
    }

    def kill(a: AST) = {
        var res = Set[Id]()

        val checkvar = manytd(query {
            case NAryExpr(i@Id(_), others) => {
                val existingerrchecks = errorreturn.flatMap(subtermIsPartOfTerm(_, others))
                val fexp = existingerrchecks.foldRight(FeatureExprFactory.False)((x, y) => env.featureExpr(x) or y)

                if (env.featureExpr(i).equivalentTo(fexp, fm))
                    res += i
            }
        })

        checkvar(a)
        addAnnotations(res)
    }

    // function checks function calls directly, without having to track a variable
    // this means that the function call does not occur within an AssignExpression or is part of a
    // DeclarationStatement
    def checkForPotentialCalls(a: AST): List[Id] = {
        var potentialfcalls: List[Id] = List()
        val getfcalls = manytd(query {
            case PostfixExpr(i@Id(name), FunctionCall(_)) => {
                // the function call is in our list of function calls we track
                // and the call is not part of an AssignExpr or InitDeclarator, which will be handled with
                // the dataflow variant of this analysis
                if (function.contains(name)
                    && findPriorASTElem[AssignExpr](i, env).isEmpty
                    && findPriorASTElem[InitDeclaratorI](i, env).isEmpty)
                    potentialfcalls ::= i
            }
        })

        getfcalls(a)

        // check for each tracked function call, whether it belongs to an NAryExpr and, if so,
        // whether errorReturn is part of that NAryExpr
        var erroreouscalls: List[Id] = List()
        potentialfcalls.map(c => {
            val ne = findPriorASTElem[NAryExpr](c, env)

            if (ne.isDefined) {
                // iterate errorreturn and check whether one of the elements in there occurs somewhere in the
                // NAryExpr, i.e., we check "e" and "others" of NAryExpr
                errorreturn.map(e => {
                    if (ne.get.others.exists(sne => isPartOf(e, sne)) || isPartOf(e, ne.get.e)) {}
                    else erroreouscalls ::= c
                })
            } else {
                erroreouscalls ::= c
            }
        })

        erroreouscalls
    }

    def getUsedVariables(a: AST) = { addAnnotations(uses(a)) }

    protected def F(e: AST) = flow(e)

    protected val i = Map[Id, FeatureExpr]()
    protected def b = Map[Id, FeatureExpr]()
    protected def combinationOperator(r: L, f: FeatureExpr, s: Set[Id]) = union(r, f, s)

    protected def circle(e: AST) = exitcache(e)
    protected def point(e: AST) = entrycache(e)
}

class StdLibFuncReturn_Null(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends StdLibFuncReturn(env, udm, fm) {

    val function: List[String] = List(
        "aligned_alloc",
        "bsearch",
        "bsearch_s",
        "calloc",
        "freopen",
        "getenv",
        "getenv_s",
        "gets_s",
        "gmtime",
        "gmtime_s",
        "localtime",
        "localtime_s",
        "malloc",
        "memchr",
        "realloc",
        "setlocale",
        "strchr",
        "strpbrk",
        "strrchr",
        "strstr",
        "strtok",
        "strtok_s",
        "tmpfile",
        "tmpnam",
        "wcschr",
        "wcspbrk",
        "wcsrchr",
        "wcsstr",
        "wcstok",
        "wcstok_s",
        "wmemchr"
    )

    val errorreturn = List(
        Constant("0"),
        // ((void*)0)
        CastExpr(TypeName(List(Opt(FeatureExprFactory.True, VoidSpecifier())),
            Some(AtomicAbstractDeclarator(List(Opt(FeatureExprFactory.True, Pointer(List()))),List()))),Constant("0")))
}

class StdLibFuncReturn_EOF(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends StdLibFuncReturn(env, udm, fm) {

    val function: List[String] = List(
        "fclose",
        "fflush",
        "fputc",
        "fputs",
        "fputws",
        "fscanf",
        "fscanf_s",
        "fwscanf",
        "fwscanf_s",
        "getc",
        "getchar",
        "putc",
        "scanf",
        "scanf_s",
        "sscanf",
        "sscanf_s",
        "swscanf",
        "swscanf_s",
        "ungetc",
        "vfscanf",
        "vfscanf_s",
        "vfwscanf",
        "vfwscanf_s",
        "vscanf",
        "vscanf_s",
        "vsscanf",
        "vsscanf_s",
        "vswscanf",
        "vswscanf_s",
        "vwscanf",
        "vwscanf_s",
        "wctob",
        "wscanf",
        "wscanf_s"
    )

    val errorreturn = List(Constant("-1")) // EOF, EOF (negative)
}

