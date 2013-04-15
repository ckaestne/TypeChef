package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import java.util
import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.c._
import org.kiama.attribution.AttributionBase
import de.fosd.typechef.conditional.{One, Conditional, Opt}

// defines and uses we can jump to using succ
// beware of List[Opt[_]]!! all list elements can possibly have a different annotation
trait Variables {


    // add annotation to elements of a Set[Id]
    // used for uses, defines, and declares
    private def addAnnotation2ResultSet(in: Set[Id], env: ASTEnv): Map[FeatureExpr, Set[Id]] = {
        var res = Map[FeatureExpr, Set[Id]]()

        for (r <- in) {
            val rfexp = env.featureExpr(r)

            val key = res.find(_._1 equivalentTo rfexp)
            key match {
                case None => res = res.+((rfexp, Set(r)))
                case Some((k, v)) => res = res.+((k, v ++ Set(r)))
            }
        }

        res
    }

    // returns all used variables with their annotation
    val usesVar: PartialFunction[(Any, ASTEnv), Map[FeatureExpr, Set[Id]]] = {
        case (a, env) => addAnnotation2ResultSet(uses(a, dataflowUses = false), env)
    }

    // returns all used variables (apart from declarations) with their annotation
    val dataflowUsesVar: PartialFunction[(Any, ASTEnv), Map[FeatureExpr, Set[Id]]] = {
        case (a, env) => addAnnotation2ResultSet(uses(a, dataflowUses = true), env)
    }

    // returns all defined variables with their annotation
    val definesVar: PartialFunction[(Any, ASTEnv), Map[FeatureExpr, Set[Id]]] = {
        case (a, env) => addAnnotation2ResultSet(defines(a), env)
    }

    // returns all declared variables with their annotation
    val declaresVar: PartialFunction[(Any, ASTEnv), Map[FeatureExpr, Set[Id]]] = {
        case (a, env) => addAnnotation2ResultSet(declares(a), env)
    }

    // returns all used Ids independent of their annotation
    def uses(a: Any, dataflowUses: Boolean): Set[Id] = {
        a match {
            case ForStatement(expr1, expr2, expr3, _) => uses(expr1, dataflowUses) ++ uses(expr2, dataflowUses) ++ uses(expr3, dataflowUses)
            case ReturnStatement(Some(x)) => uses(x, dataflowUses)
            case WhileStatement(expr, _) => uses(expr, dataflowUses)
            case DeclarationStatement(d) => uses(d, dataflowUses)
            case Declaration(_, init) => init.flatMap(uses(_, dataflowUses)).toSet
            case InitDeclaratorI(_, _, Some(i)) => uses(i, dataflowUses)
            case AtomicNamedDeclarator(_, id, _) => Set(id)
            case NestedNamedDeclarator(_, nestedDecl, _) => uses(nestedDecl, dataflowUses)
            case Initializer(_, expr) => uses(expr, dataflowUses)
            case i@Id(name) => Set(i)
            case FunctionCall(params) => params.exprs.map(_.entry).flatMap(uses(_, dataflowUses)).toSet
            case ArrayAccess(expr) => uses(expr, dataflowUses)
            case PostfixExpr(Id(_), f@FunctionCall(_)) => uses(f, dataflowUses)
            case PostfixExpr(p, s) => uses(p, dataflowUses) ++ uses(s, dataflowUses)
            case UnaryExpr(_, ex) => uses(ex, dataflowUses)
            case SizeOfExprU(expr) => uses(expr, dataflowUses)
            case CastExpr(_, expr) => uses(expr, dataflowUses)
            case PointerDerefExpr(castExpr) => uses(castExpr, dataflowUses)
            case PointerCreationExpr(castExpr) => uses(castExpr, dataflowUses)
            case UnaryOpExpr(kind, castExpr) => uses(castExpr, dataflowUses)
            case NAryExpr(ex, others) => uses(ex, dataflowUses) ++ others.flatMap(uses(_, dataflowUses)).toSet
            case NArySubExpr(_, ex) => uses(ex, dataflowUses)
            case ConditionalExpr(condition, _, _) => uses(condition, dataflowUses)
            case ExprStatement(expr) => uses(expr, dataflowUses)
            case AssignExpr(target, op, source) => uses(source, dataflowUses) ++ ({
                op match {
                    case "=" if (!dataflowUses) => Set()
                    case _ => uses(target, dataflowUses)
                }
            })
            case Opt(_, entry) => uses(entry, dataflowUses)
            case _ => Set()
        }
    }

    // returns all defined Ids independent of their annotation
    val defines: PartialFunction[Any, Set[Id]] = {
        case i@Id(_) => Set(i)
        case AssignExpr(target, _, source) => defines(target)
        case DeclarationStatement(d) => defines(d)
        case Declaration(_, init) => init.flatMap(defines).toSet
        case InitDeclaratorI(a, _, _) => defines(a)
        case AtomicNamedDeclarator(_, i, _) => Set(i)
        case ExprStatement(_: Id) => Set()
        case ExprStatement(expr) => defines(expr)
        case PostfixExpr(i@Id(_), SimplePostfixSuffix(_)) => Set(i) // a++; or a--;
        case UnaryExpr(_, i@Id(_)) => Set(i) // ++a; or --a;
        case Opt(_, entry) => defines(entry)
        case _ => Set()
    }

    // returns all declared Ids independent of their annotation
    val declares: PartialFunction[Any, Set[Id]] = {
        case DeclarationStatement(decl) => declares(decl)
        case Declaration(_, init) => init.flatMap(declares).toSet
        case InitDeclaratorI(declarator, _, _) => declares(declarator)
        case AtomicNamedDeclarator(_, id, _) => Set(id)
        case Opt(_, entry) => declares(entry)
        case _ => Set()
    }
}

class IdentityHashMapCache[A] {
    private val cache: java.util.IdentityHashMap[Any, A] = new util.IdentityHashMap[Any, A]()
    def update(k: Any, v: A) { cache.put(k, v) }
    def lookup(k: Any): Option[A] = {
        val v = cache.get(k)
        if (v != null) Some(v)
        else None
    }
}

trait Liveness extends AttributionBase with Variables with ConditionalControlFlow {

    type UsesDeclaresRel = java.util.IdentityHashMap[Id, Option[Conditional[Option[Id]]]]

    private val incache = new IdentityHashMapCache[Map[Id, FeatureExpr]]()
    private val outcache = new IdentityHashMapCache[Map[Id, FeatureExpr]]()
    private var env: ASTEnv = null
    private var udr: UsesDeclaresRel = null
    private var fm: FeatureModel = null

    def setEnv(newenv: ASTEnv) { env = newenv }
    def setUdr(newudr: UsesDeclaresRel) { udr = newudr }
    def setFm(newfm: FeatureModel) { fm = newfm }

    private def updateMap(map: Map[Id, FeatureExpr],
                          fexp: FeatureExpr,
                          difun: Set[Id],
                          diff: Boolean): Map[Id, FeatureExpr] = {
        var curmap = map

        if (diff) {
            for (v <- difun) curmap = curmap.-(v)
        } else {
            for (v <- difun) {
                curmap.get(v) match {
                    case None => curmap = curmap.+((v, fexp))
                    case Some(x) => curmap = curmap.+((v, fexp or x))
                }
            }
        }

        curmap
    }

    // TypeChef does not enforce us to be type-uniform,
    // so a variable use may belong to different variable declarations
    // e.g.:
    // void foo() {
    //   int a = 0; // 3
    //   int b = a;
    //   if (b) {
    //     #if A
    //     int a = b; // 2
    //     #endif
    //     a;  // 1
    //   }
    // }
    // a (// 1) has two different declarations: Choice(A, One(// 3), One(// 2))
    // in presence of A (// 2) shadows declaration (// 3)
    // we compute the relation between variable uses and declarations per function
    def determineUseDeclareRelation(func: FunctionDef): UsesDeclaresRel = {
        // we use a working stack to maintain scoping of nested compound statements
        // each element of the list refers to a block; if we enter a compound statement then we
        // add a Map to the stack; if we leave a compound statement we return the tail of wstack
        // current block is head
        // Map[Id, Conditional[Option[Id]]] maintains all variable declarations in the block that are visible
        type BlockDecls = Map[Id, Conditional[Option[Id]]]
        val res: java.util.IdentityHashMap[Id, Option[Conditional[Option[Id]]]] =
            new java.util.IdentityHashMap[Id, Option[Conditional[Option[Id]]]]()
        var curIdSuffix = 1

        def handleElement(e: Any, curws: List[BlockDecls]): List[BlockDecls] = {
            def handleCFGInstruction(i: AST) = {
                var curblock = curws.head
                val declares = declaresVar(i, env)
                val uses = dataflowUsesVar(i, env)

                // first check uses then update curws using declares (and update defines accordingly)
                for ((k, v) <- uses) {
                    for (id <- v) {
                        val prevblockswithid = curws.flatMap(_.get(id))
                        if (prevblockswithid.isEmpty) res.put(id, None)
                        else res.put(id, Some(ConditionalLib.findSubtree(k, prevblockswithid.head)))
                    }
                }

                for ((k, v) <- declares) {
                    for (id <- v) {
                        // adding the declaration itself
                        res.put(id, Some(One(Some(Id(id.name + curIdSuffix.toString)))))

                        // look for alternative types
                        if (curblock.get(id).isDefined) {
                            curblock = curblock.+((id, ConditionalLib.insert[Option[Id]](curblock.get(id).get,
                                FeatureExprFactory.True, k, Some(Id(id.name + curIdSuffix.toString)))))
                            curIdSuffix += 1
                        } else {
                            // get previous block with declaring id and embed that block in a choice
                            val prevblockswithid = curws.tail.flatMap(_.get(id))
                            if (prevblockswithid.isEmpty) {
                                curblock = curblock.+((id, Choice(k, One(Some(Id(id.name + curIdSuffix.toString))), One(None))))
                                curIdSuffix += 1
                            } else {
                                curblock = curblock.+((id, Choice(k, One(Some(Id(id.name + curIdSuffix.toString))), prevblockswithid.head).simplify))
                                curIdSuffix += 1
                            }
                        }
                    }
                }

                curblock :: curws.tail
            }

            e match {
                // add map to ws when entering a {}; remove when leaving {}
                case CompoundStatement(innerStatements) => handleElement(innerStatements, Map[Id, Conditional[Option[Id]]]() :: curws); curws
                case l: List[_] => {
                    var newws = curws
                    for (s <- l)
                        newws = handleElement(s, newws)
                    newws
                }

                // statements with special treatment of statements with compound statements in it
                case s: IfStatement => s.productIterator.toList.map(x => handleElement(x, Map[Id, Conditional[Option[Id]]]() :: curws)); curws
                case s: ForStatement => s.productIterator.toList.map(x => handleElement(x, Map[Id, Conditional[Option[Id]]]() :: curws)); curws
                case s: ElifStatement => s.productIterator.toList.map(x => handleElement(x, Map[Id, Conditional[Option[Id]]]() :: curws)); curws
                case s: WhileStatement => s.productIterator.toList.map(x => handleElement(x, Map[Id, Conditional[Option[Id]]]() :: curws)); curws
                case s: DoStatement => s.productIterator.toList.map(x => handleElement(x, Map[Id, Conditional[Option[Id]]]() :: curws)); curws
                case s: SwitchStatement => s.productIterator.toList.map(x => handleElement(x, Map[Id, Conditional[Option[Id]]]() :: curws)); curws

                case s: Statement => handleCFGInstruction(s)
                case e: Expr => handleCFGInstruction(e)

                case Opt(_, entry) => handleElement(entry, curws)
                case Choice(_, thenBranch, elseBranch) => handleElement(thenBranch, curws); handleElement(elseBranch, curws)
                case One(value) => handleElement(value, curws)
                case Some(x) => handleElement(x, curws)
                case None => curws

                case _: FeatureExpr => curws
                case x => println("not handling: " + x); curws
            }
        }
        handleElement(func.stmt, List())
        res
    }


    // cf. http://www.cs.colostate.edu/~mstrout/CS553/slides/lecture03.pdf
    // page 5
    //  in(n) = uses(n) + (out(n) - defines(n))
    // out(n) = for s in succ(n) r = r + in(s); r
    // insimple and outsimple are the non variability-aware in and out versiosn
    // of liveness determination
    val insimple: AST => Set[Id] = {
        circular[AST, Set[Id]](Set[Id]()) {
            case FunctionDef(_, _, _, _) => Set()
            case e => {
                val u = uses(e, dataflowUses = false)
                val d = defines(e)
                var res = outsimple(e)

                res = u.union(res.diff(d))
                res
            }
        }
    }

    val outsimple: AST => Set[Id] = {
        circular[AST, Set[Id]](Set[Id]()) {
            case e => {
                val ss = succ(e, fm, env).filterNot(x => x.entry.isInstanceOf[FunctionDef])
                var res: Set[Id] = Set()
                for (s <- ss.map(_.entry)) res = res.union(insimple(s))
                res
            }
        }
    }

    // this method internally explodes the use of a variable in case it has multiple declarations
    // e.g.:
    // int a = 0;
    // {
    //   #if A    int a = 1;
    //   a;
    // }
    // the use of a either has "int a = 0;" or "int a = 1;" as declaration
    // udr holds rename versions of both variables and runs the analysis with it (e.g., int a = 0; -> a1
    // and int a = 1; -> a2)
    private def explodeIdUse(s: Set[Id], sfexp: FeatureExpr, udr: UsesDeclaresRel, res: Map[Id, FeatureExpr], diff: Boolean) = {
        var curres = res
        for (i <- s) {
            val newname = udr.get(i)
            newname match {
                case null => curres = updateMap(curres, sfexp, Set(i), diff)
                case None => curres = updateMap(curres, sfexp, Set(i), diff)
                case Some(c) => {
                    val leaves = ConditionalLib.items(c)
                    for ((nfexp, nid) <- leaves)
                        if (nid.isDefined) curres = updateMap(curres, sfexp and nfexp, Set(nid.get), diff)
                        else curres = updateMap(curres, sfexp, Set(i), diff)
                }
            }
        }
        curres
    }

    // in and out variability-aware versions
    val inrec: AST => Map[Id, FeatureExpr] = {
        circular[AST, Map[Id, FeatureExpr]](Map[Id, FeatureExpr]()) {
            case FunctionDef(_, _, _, _) => Map()
            case t => {
                val uses = usesVar(t, env)
                val defines = definesVar(t, env)

                var res = out(t)
                for ((k, v) <- defines) res = explodeIdUse(v, k, udr, res, diff = true)
                for ((k, v) <- uses) res = explodeIdUse(v, k, udr, res, diff = false)
                res
            }
        }
    }

    val outrec: AST => Map[Id, FeatureExpr] =
        circular[AST, Map[Id, FeatureExpr]](Map[Id, FeatureExpr]()) {
            case e => {
                val ss = succ(e, fm, env).filterNot(x => x.entry.isInstanceOf[FunctionDef])
                var res = Map[Id, FeatureExpr]()
                for (s <- ss) {
                    for ((r, f) <- in(s.entry))
                        res = updateMap(res, f and s.feature, Set(r), diff = false)
                }
                res
            }
        }

    def out(a: AST) = {
        outcache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = outrec(a)
                outcache.update(a, r)
                r
            }
        }
    }

    def in(a: AST) = {
        incache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = inrec(a)
                incache.update(a, r)
                r
            }
        }
    }
}
