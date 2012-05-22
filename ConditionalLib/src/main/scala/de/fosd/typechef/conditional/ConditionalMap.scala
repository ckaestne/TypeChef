package de.fosd.typechef.conditional

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.featureexpr.FeatureExprFactory.{True, False}

/**
 * maintains a map
 * a name may be mapped to alternative entries with different feature expressions
 */
class ConditionalMap[A, B](private val entries: Map[A, Seq[(FeatureExpr, B)]]) {
    def this() = this(Map())
    /*
       feature expressions are not rewritten as in the macrotable, but we
        may later want to ensure that they are mutually exclusive
        in get, they simply overwrite each other in order of addition
    */
    //       def apply(key: A): Conditional[B]= getOrElse(key, {throw new NoSuchElementException})
    def getOrElse(key: A, other: B): Conditional[B] = {
        if (!contains(key)) One(other)
        else {
            val types = entries(key)
            if (types.size == 1 && types.head._1 == True) One(types.head._2)
            else createChoice(types, other)
        }
    }

    def ++(that: ConditionalMap[A, B]) = {
        var r = entries
        for ((name, seq) <- that.entries) {
            if (r contains name)
                r = r + (name -> (seq ++ r(name)))
            else
                r = r + (name -> seq)
        }
        new ConditionalMap(r)
    }
    def ++(decls: Seq[(A, FeatureExpr, B)]) = {
        var r = entries
        for (decl <- decls) {
            if (r contains decl._1)
                r = r + (decl._1 -> ((decl._2, decl._3) +: r(decl._1)))
            else
                r = r + (decl._1 -> Seq((decl._2, decl._3)))
        }
        new ConditionalMap(r)
    }
    def +(key: A, f: FeatureExpr, t: B) = this ++ Seq((key, f, t))
    def contains(name: A) = (entries contains name) && !entries(name).isEmpty
    def isEmpty = entries.isEmpty
    def allEntriesFlat: Iterable[B] = entries.values.flatten.map(_._2)
    def whenDefined(name: A): FeatureExpr = entries.getOrElse(name, Seq()).foldLeft(False)(_ or _._1)
    def keys = entries.keys

    private def createChoice(entries: Seq[(FeatureExpr, B)], other: B): Conditional[B] =
        entries.foldRight[Conditional[B]](One(other))((p, t) => Choice(p._1, One(p._2), t)) simplify


    override def equals(that: Any) = that match {
        case c: ConditionalMap[_, _] => entries equals c.entries;
        case _ => false
    }
    override def hashCode = entries.hashCode
    override def toString = entries.toString
}

