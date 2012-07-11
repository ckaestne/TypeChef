package de.fosd.typechef.crewrite
import org.junit.Test
import de.fosd.typechef.featureexpr.FeatureExprFactory.True
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.{One, Opt}

class IfdefToIfTest extends TestHelper {

  @Test def test_replace() {
    val e1 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True,IntSpecifier())),
          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e1"),List()),List(),None))))))
    val e2 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True,IntSpecifier())),
          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e2"),List()),List(),None))))))
    val e21 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True,IntSpecifier())),
          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e21"),List()),List(),None))))))
    val e22 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True,IntSpecifier())),
          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e22"),List()),List(),None))))))
    val e3 = Opt(fx,
      DeclarationStatement(
        Declaration(
          List(Opt(True,IntSpecifier())),
          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e3"),List()),List(),None))))))
    val e4 = Opt(fx.not(),
      DeclarationStatement(
        Declaration(
          List(Opt(True,IntSpecifier())),
          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e4"),List()),List(),None))))))
    val e5 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True,IntSpecifier())),
          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e5"),List()),List(),None))))))

    val c = One(CompoundStatement(List(e1, e2, e3, e4, e5)))

    val i = new IfdefToIf()
    println(PrettyPrinter.print(c.value))
    println(PrettyPrinter.print(i.replace(c.value, e2, List(e21, e22))))

  }

}
