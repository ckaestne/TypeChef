package de.fosd.typechef.ast


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import de.fosd.typechef.parser._
import TestAST._

@RunWith(classOf[JUnitRunner])
class KiamaTest extends FunSuite {

    val vd = new VarDecl(One("int"), One("a"))
    val as = new Assign(One("a"), One(new Primitive(One("int"))))
    val rt = new Return(One("a"))
    val prog = new CompUnit(Opt(), Many(), One(
        new Block(Many(
            One(vd),
            One(new VarDecl(One("int"), One("b"))),
            One(as),
            One(rt)
        ))
    ))


    import Analysis._

    test("defines (vd)") {
        expect(Set("a"))(defines(vd))
    }
    test("defines (as)") {
        expect(Set())(defines(as))
    }
    test("defines (rt)") {
        expect(Set())(defines(rt))
    }
    test("uses (vd)") {
        expect(Set())(uses(vd))
    }
    test("uses (as)") {
        expect(Set("a"))(uses(as))
    }
    test("uses (rt)") {
        expect(Set("a"))(uses(rt))
    }

    test("print") {
        println(testEnv(prog))
    }
}