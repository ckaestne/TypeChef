package de.fosd.typechef.parser.c

//import de.fosd.typechef.parser.MultiFeatureParser._

object GrammarAnalyzer {

    val p = new CParser()

    def main(args: Array[String]) {

        val root = p.statementList

        //analyze(root, 0)

        val firstList = first(root, Set(), 0)
        println(firstList)

        var seen: Set[String] = Set()
        for (e <- firstList) {
            if (seen contains e)
                println("first-first conflict: " + e)
            seen = seen + e
        }

    }

    def analyze(c: p.ConditionalParser[Any], level: Int) {
        println("  " * level + c)
        if (c.isInstanceOf[p.MapParser[_, _]]) {
            analyze(c.asInstanceOf[p.MapParser[Any, Any]].a, level + 1)
        }
        if (c.isInstanceOf[p.SeqParser[_, _]]) {
            analyze(c.asInstanceOf[p.SeqParser[Any, Any]].a, level + 1)
            analyze(c.asInstanceOf[p.SeqParser[Any, Any]].b, level + 1)
        }
        if (c.isInstanceOf[p.AltParser[_, _]]) {
            analyze(c.asInstanceOf[p.AltParser[Any, Any]].a, level + 1)
            analyze(c.asInstanceOf[p.AltParser[Any, Any]].b, level + 1)
        }
        if (c.isInstanceOf[p.RepParser[_]]) {
            analyze(c.asInstanceOf[p.RepParser[Any]].a, level + 1)
        }
        if (c.isInstanceOf[p.JoinParser[_]]) {
            analyze(c.asInstanceOf[p.JoinParser[Any]].a, level + 1)
        }
    }

    def first(c: p.ConditionalParser[Any], known: Set[Object], level: Int): List[String] = {
        println("  " * level + c)
        if (c.isInstanceOf[p.AtomicParser[_]]) {
            return List(c.asInstanceOf[p.AtomicParser[Any]].kind)
        }
        if (known.contains(c))
            return List()
        val newknown = known + c
        if (c.isInstanceOf[p.MapParser[_, _]]) {
            return first(c.asInstanceOf[p.MapParser[Any, Any]].a, newknown, level + 1)
        }
        if (c.isInstanceOf[p.SeqParser[_, _]]) {
            val aa = c.asInstanceOf[p.SeqParser[Any, Any]].a
            return first(aa, newknown, level + 1) ++
                    (if (aa.isInstanceOf[p.RepParser[_]] || aa.isInstanceOf[p.OptParser[_]])
                        first(c.asInstanceOf[p.SeqParser[Any, Any]].b, newknown, level + 1)
                    else List())
        }
        if (c.isInstanceOf[p.AltParser[_, _]]) {
            return first(c.asInstanceOf[p.AltParser[Any, Any]].a, newknown, level + 1) ++ first(c.asInstanceOf[p.AltParser[Any, Any]].b, newknown, level + 1)
        }
        if (c.isInstanceOf[p.RepParser[_]]) {
            return first(c.asInstanceOf[p.RepParser[Any]].a, newknown, level + 1)
        }
        if (c.isInstanceOf[p.OptParser[_]]) {
            return first(c.asInstanceOf[p.OptParser[Any]].a, newknown, level + 1)
        }
        if (c.isInstanceOf[p.OtherParser[_]]) {
            return first(c.asInstanceOf[p.OtherParser[Any]].a, newknown, level + 1)
        }
        if (c.isInstanceOf[p.JoinParser[ _]]) {
            return first(c.asInstanceOf[p.JoinParser[ Any]].a, newknown, level + 1)
        }
        return List()
    }

}