package de.fosd.typechef.parser.c

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExpr
import scala.annotation.tailrec
import scala.reflect.ClassTag

// simplified navigation support
// reimplements basic navigation between AST nodes not affected by Opt and Choice nodes
// see old version: https://github.com/ckaestne/TypeChef/blob/ConditionalControlFlow/CParser/src/main/scala/de/fosd/typechef/parser/c/ASTNavigation.scala
trait ASTNavigation {

    // method simply goes up the hierarchy and looks for next AST element and returns it
    def parentAST(e: Product, env: ASTEnv): AST = {
        val eparent = env.parent(e)
        eparent match {
            case o: Opt[_] => parentAST(o, env)
            case c: Conditional[_] => parentAST(c, env)
            case a: AST => a
            case _ => null
        }
    }

    // getting the previous element of an AST element in the presence of Opt and Choice
    // has to consider the following situation
    // Opt elements usually appear in the presence of lists
    // List[Opt[_]] (see AST for more information)
    // having a list of Opt elements, AST elements usually appear in those Opt elements
    // [ Opt(f1, AST), Opt(f2, AST), ..., Opt(fn, AST)]
    // to get the previous AST element of each AST element in that list, we have to
    // go one level up and look for previous Opt elements and their children
    def prevAST(e: Product, env: ASTEnv): AST = {
        val eprev = env.previous(e)
        eprev match {
            case c: Choice[_] => lastChoice(c)
            case o: One[_] => o.value.asInstanceOf[AST]
            case a: AST => a
            case Opt(_, v: Choice[_]) => lastChoice(v)
            case Opt(_, v: One[_]) => v.value.asInstanceOf[AST]
            case Opt(_, v: AST) => v
            case null => {
                val eparent = env.parent(e)
                eparent match {
                    case o: Opt[_] => prevAST(o, env)
                    case c: Choice[_] => prevAST(c, env)
                    case c: One[_] => prevAST(c, env)
                    case _ => null
                }
            }
        }
    }

    // similar to prevAST but with next
    def nextAST(e: Product, env: ASTEnv): AST = {
        val enext = env.next(e)
        enext match {
            case c: Choice[_] => firstChoice(c)
            case o: One[_] => o.value.asInstanceOf[AST]
            case a: AST => a
            case Opt(_, v: Choice[_]) => firstChoice(v)
            case Opt(_, v: One[_]) => v.value.asInstanceOf[AST]
            case Opt(_, v: AST) => v
            case null => {
                val eparent = env.parent(e)
                eparent match {
                    case o: Opt[_] => nextAST(o, env)
                    case c: Choice[_] => nextAST(c, env)
                    case c: One[_] => nextAST(c, env)
                    case _ => null
                }
            }
        }
    }

    // returns a list of all previous AST elements including e
    // useful in compound statements that have a list of Opt elements
    // prevASTElems(e, env) // ei == e
    // [ Opt(f1, e1), Opt(f2, e2), ..., Opt(fi, ei), ..., Opt(fn, en) ]
    // returns [e1, e2, ..., ei]
    def prevASTElems(e: Product, env: ASTEnv): List[AST] = {

        @tailrec def prevASTElemsRec(e: Product, cres: List[AST] = List()): List[AST] = {
            e match {
                case null => cres
                case s => prevASTElemsRec(prevAST(s, env), childAST(s) :: cres)
            }
        }

        prevASTElemsRec(e)
    }

    // returns a list of all next AST elements including e
    // [ Opt(f1, e1), Opt(f2, e2), ..., Opt(fi, ei), ..., Opt(fn, en) ]
    // returns [ei, ..., en]
    def nextASTElems(e: Product, env: ASTEnv): List[AST] = {

        @tailrec def nextASTElemsRec(e: Product, cres: List[AST] = List()): List[AST] = {
            e match {
                case null => cres.reverse
                case s => nextASTElemsRec(nextAST(s, env), childAST(s) :: cres)
            }
        }

        nextASTElemsRec(e)
    }

    // returns the first AST element that is nested in the following elements
    // or null; elements are Opt, Conditional, and Some
    // function does not work for type List[_]
    def childAST(e: Product): AST = {
        e match {
            case Opt(_, v: AST) => v
            case Opt(_, v: One[_]) => v.value.asInstanceOf[AST]
            case Opt(_, v: Choice[_]) => firstChoice(v)
            case x: One[_] => x.value.asInstanceOf[AST]
            case a: AST => a
            case x: Option[_] if (x.isDefined) => childAST(x.get.asInstanceOf[Product])
            case _ => null
        }
    }

    // method recursively filters all AST elements for a given type
    // base case is the element of type T
    def filterASTElems[T <: AST](a: Any)(implicit m: ClassTag[T]): List[T] = {
        a match {
            case p: Product if (m.runtimeClass.isInstance(p)) => List(p.asInstanceOf[T])
            case l: List[_] => l.flatMap(filterASTElems[T])
            case p: Product => p.productIterator.toList.flatMap(filterASTElems[T])
            case _ => List()
        }
    }

    // method recursively filters all AST elements for a given type and feature expression
    // base case is the element of type T with feature expression ctx
    def filterASTElems[T <: AST](a: Any, ctx: FeatureExpr, env: ASTEnv)(implicit m: ClassTag[T]): List[T] = {
        a match {
            case p: Product if (m.runtimeClass.isInstance(p) && (env.featureExpr(p) implies ctx isSatisfiable())) => List(p.asInstanceOf[T])
            case l: List[_] => l.flatMap(filterASTElems[T](_, ctx, env))
            case p: Product => p.productIterator.toList.flatMap(filterASTElems[T](_, ctx, env))
            case _ => List()
        }
    }

    // in contrast to filterASTElems, filterAllASTElems visits all elements of the tree-wise input structure
    def filterAllASTElems[T <: AST](a: Any)(implicit m: ClassTag[T]): List[T] = {
        a match {
            case p: Product if (m.runtimeClass.isInstance(p)) => List(p.asInstanceOf[T]) ++
                p.productIterator.toList.flatMap(filterAllASTElems[T])
            case l: List[_] => l.flatMap(filterAllASTElems[T])
            case p: Product => p.productIterator.toList.flatMap(filterAllASTElems[T])
            case _ => List()
        }
    }

    // in contrast to filterASTElems, filterAllASTElems visits all elements of the tree-wise input structure and
    // checks feature expressions also
    def filterAllASTElems[T <: AST](a: Any, ctx: FeatureExpr, env: ASTEnv)
                                   (implicit m: ClassTag[T]): List[T] = {
        a match {
            case p: Product if (m.runtimeClass.isInstance(p) && (env.featureExpr(p) implies ctx isSatisfiable())) => List(p.asInstanceOf[T]) ++
                p.productIterator.toList.flatMap(filterAllASTElems[T](_, ctx, env))
            case l: List[_] => l.flatMap(filterAllASTElems[T](_, ctx, env))
            case p: Product => p.productIterator.toList.flatMap(filterAllASTElems[T](_, ctx, env))
            case _ => List()
        }
    }

    // go up the AST hierarchy and look for a specific AST element with type T
    def findPriorASTElem[T <: AST](a: Product, env: ASTEnv)(implicit m: ClassTag[T]): Option[T] = {
        a match {
            case x if (m.runtimeClass.isInstance(x)) => Some(x.asInstanceOf[T])
            case x: Product => findPriorASTElem[T](parentAST(x, env), env)
            case null => None
        }
    }

    // go up the AST hierarchy and loog for specific AST elements with type T
    def findPriorASTElems[T <: AST](a: Product, env: ASTEnv)(implicit m: ClassTag[T]): List[T] = {
        a match {
            case x if (m.runtimeClass.isInstance(x)) => x.asInstanceOf[T] :: findPriorASTElems(parentAST(x, env), env)
            case x: Product => findPriorASTElems(parentAST(x, env), env)
            case null => Nil
        }
    }

    // recursively walk right branch of Choice structure until we hit an AST element
    private def lastChoice(x: Choice[_]): AST = {
        x.elseBranch match {
            case c: Choice[_] => lastChoice(c)
            case One(c) => c.asInstanceOf[AST]
        }
    }

    // recursively walk left branch of Choice structure until we hit an AST element
    private def firstChoice(x: Choice[_]): AST = {
        x.thenBranch match {
            case c: Choice[_] => firstChoice(c)
            case One(c) => c.asInstanceOf[AST]
        }
    }
}