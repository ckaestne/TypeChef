package de.fosd.typechef.crewrite

import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.{Opt, One}
import de.fosd.typechef.featureexpr.FeatureExprFactory.True
import org.junit.{Ignore, Test}
import de.fosd.typechef.featureexpr.FeatureExprFactory

class IntraCFGTest extends EnforceTreeHelper with TestHelper with ShouldMatchers with IntraCFG with Liveness with Variables {

//  @Test def test_if_the_else() {
//    val a = parseCompoundStmt("""
//    {
//      #ifdef A
//      int a;
//      #elif defined(B)
//      int b;
//      #else
//      int c;
//      #endif
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_simple_ifdef() {
//    val a = parseCompoundStmt("""
//    {
//      int a0;
//      #ifdef A1
//      int a1;
//      #endif
//      int a2;
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_for_loop() {
//    val a = parseCompoundStmt("""
//    {
//      for (;;) { }
//    }
//    """)
//
//    val newa = rewriteInfiniteForLoops(a)
//    val env = CASTEnv.createASTEnv(newa)
//    println("succs: " + DotGraph.map2file(getAllSucc(newa, env), env))
//    println("preds: " + DotGraph.map2file(getAllPred(newa, env), env))
//  }
//
//  @Test def test_nested_loop() {
//    val a = parseCompoundStmt("""
//    {
//      for(;;) {
//        for(;;) {
//          for(;;) {
//          }
//        }
//      }
//    }
//    """)
//
//    val newa = rewriteInfiniteForLoops[CompoundStatement](a)
//    val env = CASTEnv.createASTEnv(newa)
//    println("succs: " + DotGraph.map2file(getAllSucc(newa, env), env))
//    println("preds: " + DotGraph.map2file(getAllPred(newa, env), env))
//  }
//
//  @Test def test_infinite_while_loop() {
//    val a = parseCompoundStmt("""
//    {
//      while (1) { }
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//    println("preds: " + DotGraph.map2file(getAllPred(a, env), env))
//  }
//
//  @Test def test_infinite_while_loop_2() {
//    val a = parseCompoundStmt("""
//    {
//      while (1)
//        ;
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//    println("preds: " + DotGraph.map2file(getAllPred(a, env), env))
//  }
//
//  @Test def test_infinite_do_while_loop() {
//    val a = parseCompoundStmt("""
//    {
//      do {
//      } while (1);
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//    println("preds: " + DotGraph.map2file(getAllPred(a, env), env))
//  }
//
//  @Test def test_infinite_do_while_loop2() {
//    val a = parseCompoundStmt("""
//    {
//      do
//        ;
//      while (1);
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//    println("preds: " + DotGraph.map2file(getAllPred(a, env), env))
//  }
//
//  @Test def test_switch_case() {
//    val a = parseCompoundStmt("""
//    {
//      switch(x) {
//      case 1: break;
//      case 2: break;
//      case 3: break;
//      default: break;
//      }
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_do_while_loop() {
//    val a = parseCompoundStmt("""
//    {
//      do {
//      } while (k);
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_while_loop() {
//    val a = parseCompoundStmt("""
//    {
//      while (k) {
//        k--;
//      }
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_if_the_else_chain() {
//    val a = parseCompoundStmt("""
//    {
//      int k = 3;
//      if (k < 3) {
//        k = -1;
//      }
//      #ifdef A
//      else if (k = 3) {
//        k = 0;
//      }
//      #endif
//      else {
//        k = 1;
//      }
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_simple_conditional_label_statements() {
//    val e1 = Opt(True, LabelStatement(Id("e1"), None))
//    val e2 = Opt(True, LabelStatement(Id("e2"), None))
//    val e3 = Opt(True, LabelStatement(Id("e3"), None))
//    val e4 = Opt(True, LabelStatement(Id("e4"), None))
//    val e5 = Opt(True, LabelStatement(Id("e5"), None))
//    val c = One(CompoundStatement(List(e1, e2, e3, e4, e5)))
//
//    val env = CASTEnv.createASTEnv(c.value)
//    succ(e1, env) should be (List(e2.entry))
//    succ(e2, env) should be (List(e3.entry))
//    succ(e3, env) should be (List(e4.entry))
//    succ(e4, env) should be (List(e5.entry))
//  }
//
//  @Ignore def test_conditional_labelstatements_if_elif_else() {
//    val e1 = Opt(True, LabelStatement(Id("e1"), None))
//    val e2 = Opt(fx, LabelStatement(Id("e2"), None))
//    val e3 = Opt(fy.and(fx.not()), LabelStatement(Id("e3"), None))
//    val e4 = Opt(fy.not().and(fx.not()), LabelStatement(Id("e4"), None))
//    val e5 = Opt(True, LabelStatement(Id("e5"), None))
//    val c = One(CompoundStatement(List(e1, e2, e3, e4, e5)))
//
//    val env = CASTEnv.createASTEnv(c.value)
//    succ(e1, env) should be (List(e2.entry, e3.entry, e4.entry))
//    succ(e2, env) should be (List(e5.entry))
//    succ(e3, env) should be (List(e5.entry))
//    succ(e4, env) should be (List(e5.entry))
//    println("succs: " + DotGraph.map2file(getAllSucc(e1.entry, env), env))
//  }
//
//  @Ignore def test_conditional_labelstatements_with_sequence_of_annotated_elements() {
//    val e1 = Opt(True, LabelStatement(Id("e1"), None))
//    val e2 = Opt(fx, LabelStatement(Id("e2"), None))
//    val e3 = Opt(fx, LabelStatement(Id("e3"), None))
//    val e4 = Opt(fx.not(), LabelStatement(Id("e4"), None))
//    val e5 = Opt(True, LabelStatement(Id("e5"), None))
//    val c = One(CompoundStatement(List(e1, e2, e3, e4, e5)))
//
//    val env = CASTEnv.createASTEnv(c.value)
//    succ(e1, env) should be(List(e2.entry, e4.entry))
//    succ(e2, env) should be(List(e3.entry))
//    succ(e3, env) should be(List(e5.entry))
//    succ(e4, env) should be(List(e5.entry))
//    println("succs: " + DotGraph.map2file(getAllSucc(e1.entry, env), env))
//  }
//
//  @Ignore def test_conditional_labelstatements_if_if_else() {
//    val e0 = Opt(fx, LabelStatement(Id("e0"), None))
//    val e1 = Opt(True, LabelStatement(Id("e1"), None))
//    val e2 = Opt(True, LabelStatement(Id("e2"), None))
//    val e3 = Opt(fx, LabelStatement(Id("e3"), None))
//    val e4 = Opt(fx.not().and(fy), LabelStatement(Id("e4"), None))
//    val e5 = Opt(fx.not().and(fy.not()), LabelStatement(Id("e5"), None))
//    val e6 = Opt(fa, LabelStatement(Id("e6"), None))
//    val e7 = Opt(fa.not(), LabelStatement(Id("e7"), None))
//    val e8 = Opt(fb.not(), LabelStatement(Id("e8"), None))
//    val e9 = Opt(True, LabelStatement(Id("e9"), None))
//    val c = One(CompoundStatement(List(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9)))
//
//    val env = CASTEnv.createASTEnv(c.value)
//    succ(e0, env) should be(List(e1.entry))
//    succ(e1, env) should be(List(e2.entry))
//    succ(e2, env) should be(List(e3.entry, e4.entry, e5.entry))
//    succ(e3, env) should be(List(e6.entry, e7.entry))
//    succ(e4, env) should be(List(e6.entry, e7.entry))
//    succ(e5, env) should be(List(e6.entry, e7.entry))
//    succ(e6, env) should be(List(e8.entry, e9.entry))
//    succ(e7, env) should be(List(e8.entry, e9.entry))
//    succ(e8, env) should be(List(e9.entry))
//    println("succs: " + DotGraph.map2file(getAllSucc(e0.entry, env), env))
//  }
//
//  @Ignore def test_conditional_declaration_statement_pred() {
//    val e1 = Opt(True,
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,IntSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e1"),List()),List(),None))))))
//    val e2 = Opt(True,
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,IntSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e2"),List()),List(),None))))))
//    val e3 = Opt(True,
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,IntSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e3"),List()),List(),None))))))
//    val e4 = Opt(True,
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,IntSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e4"),List()),List(),None))))))
//    val e5 = Opt(True,
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,IntSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e5"),List()),List(),None))))))
//
//    val c = One(CompoundStatement(List(e1, e2, e3, e4, e5)))
//
//    val env = CASTEnv.createASTEnv(c.value)
//    succ(e1, env) should be (List(e2.entry))
//    succ(e2, env) should be (List(e3.entry))
//    succ(e3, env) should be (List(e4.entry))
//    succ(e4, env) should be (List(e5.entry))
//
//    pred(e5, env) should be (List(e4.entry))
//    pred(e4, env) should be (List(e3.entry))
//    pred(e3, env) should be (List(e2.entry))
//    pred(e2, env) should be (List(e1.entry))
//  }
//
//  @Ignore def test_conditional_declaration_statement_pred2() {
//    val e1 = Opt(True,
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,IntSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e1"),List()),List(),None))))))
//    val e2 = Opt(True,
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,IntSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e2"),List()),List(),None))))))
//    val e3 = Opt(fx,
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,IntSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e3"),List()),List(),None))))))
//    val e4 = Opt(fx.not(),
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,IntSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e4"),List()),List(),None))))))
//    val e5 = Opt(True,
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,IntSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("e5"),List()),List(),None))))))
//
//    val c = One(CompoundStatement(List(e1, e2, e3, e4, e5)))
//
//    val env = CASTEnv.createASTEnv(c.value)
//    succ(e1, env) should be (List(e2.entry))
//    succ(e2, env) should be (List(e3.entry, e4.entry))
//    succ(e3, env) should be (List(e5.entry))
//    succ(e4, env) should be (List(e5.entry))
//
//    pred(e5, env) should be (List(e4.entry, e3.entry))
//    pred(e4, env) should be (List(e2.entry))
//    pred(e3, env) should be (List(e2.entry))
//    pred(e2, env) should be (List(e1.entry))
//  }
//
//  @Ignore def test_conditional_declaration_statement() {
//    val e0 = Opt(True, LabelStatement(Id("e0"), None))
//    val e1 = Opt(fx,
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,IntSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("k"),List()),List(),None))))))
//    val e2 = Opt(fx.not(),
//      DeclarationStatement(
//        Declaration(
//          List(Opt(True,DoubleSpecifier())),
//          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("k"),List()),List(),None))))))
//    val e3 = Opt(True, LabelStatement(Id("e3"), None))
//    val c = One(CompoundStatement(List(e0, e1, e2, e3)))
//
//    val env = CASTEnv.createASTEnv(c.value)
//    succ(e0, env) should be(List(e1.entry, e2.entry))
//    succ(e1, env) should be(List(e3.entry))
//    succ(e2, env) should be(List(e3.entry))
//    println("succs: " + DotGraph.map2file(getAllSucc(e0.entry, env), env))
//  }
//
//  @Test def test_conditional_while_statement() {
//    val e0 = Opt(True, LabelStatement(Id("e0"), None))
//    val e11 = Opt(True, LabelStatement(Id("e11"), None))
//    val e12 = Opt(fy, LabelStatement(Id("e12"), None))
//    val e1c = Id("k")
//    val e1 = Opt(fx, WhileStatement(e1c, One(CompoundStatement(List(e11, e12)))))
//    val e2 = Opt(True, LabelStatement(Id("e2"), None))
//    val c = One(CompoundStatement(List(e0, e1, e2)))
//
//    val env = CASTEnv.createASTEnv(c.value)
//    succ(e0, env) should be(List(e1c, e2.entry))
//    succ(e1, env) should be(List(e1c))
//    succ(e1c, env) should be(List(e11.entry, e2.entry))
//    println("succs: " + DotGraph.map2file(getAllSucc(e0.entry, env), env))
//  }
//
//  @Test def test_conditional_for_loop() {
//    val a = parseCompoundStmt("""
//    {
//      int k = 2;
//      int i;
//      for(i=0;
//      #ifdef A
//      i<10
//      #endif
//      ;i++) j++;
//      int j;
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_conditional_if_statement() {
//    val a = parseCompoundStmt("""
//    {
//      int k = 3;
//      if (k < 2) { k = 2; }
//      #ifdef A
//      else if (k < 5) { k = 5; }
//      #endif
//      #ifdef B
//      else if (k < 7) { k = 7; }
//      #endif
//      else { k = 10; }
//      int l = 3;
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_conditional_switch_statement() {
//    val a = parseCompoundStmt("""
//    {
//      int k = 3;
//      switch (k) {
//      case 1: break;
//      #ifdef A
//      case 2: break;
//      #endif
//      case 3: break;
//      default: break;
//      }
//      int l = 2;
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_conditional_for_loop_elems() {
//    val e0 = Opt(True, LabelStatement(Id("e0"), None))
//    val e1 = Opt(fx, ForStatement(
//      Some(AssignExpr(Id("i"),"=",Constant("0"))),
//      Some(AssignExpr(Id("i"),"=",Constant("2"))),
//      Some(PostfixExpr(Id("i"),SimplePostfixSuffix("++"))),
//      One(CompoundStatement(List(Opt(fx,ExprStatement(PostfixExpr(Id("j"),SimplePostfixSuffix("++")))))))))
//    val e2 = Opt(True, LabelStatement(Id("e2"), None))
//    val c = One(CompoundStatement(List(e0, e1, e2)))
//
//    val env = CASTEnv.createASTEnv(c)
//    println("succs: " + DotGraph.map2file(getAllSucc(e0.entry, env), env))
//  }
//
//  @Test def test_conditional_for_loop_alternative() {
//    val a = parseCompoundStmt("""
//    {
//      int i;
//      for(;;) {
//      #ifdef A
//      int a;
//      #else
//      double a;
//      #endif
//      }
//      int j;
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_conditional_for_loop_infinite() {
//    val a = parseCompoundStmt("""
//    {
//      int i;
//      for(;;) {
//      }
//      int j;
//    }
//    """)
//
//    val newa = rewriteInfiniteForLoops(a)
//    val env = CASTEnv.createASTEnv(newa)
//    println("succs: " + DotGraph.map2file(getAllSucc(newa, env), env))
//  }
//
//  @Test def test_conditional_for_loop_infinite_single_statement() {
//    val a = parseCompoundStmt("""
//    {
//      int i = 0;
//      for(;;) {
//        i++;
//      }
//      int j;
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_statements_increment() {
//    val a = parseCompoundStmt("""
//    {
//      int k = 0;
//      k++;
//      k++;
//      k++;
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_conditional_statements() {
//    val a = parseCompoundStmt("""
//    {
//      int a = 2;
//      int b = 200;
//      while (
//      #ifdef A
//      a < b
//      #else
//      true
//      #endif
//      )
//      {
//        a++;
//        #ifdef B
//        b--;
//        #endif
//      }
//      #ifdef C
//      b = 20;
//      a = 30;
//      #endif
//      while (a > b) {
//        a++;
//      }
//      int c;
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Ignore def test_conditional_label_and_goto_statements_constructed() {
//    val e0 = Opt(True, GotoStatement(Id("label1")))
//    val e1 = Opt(fx, LabelStatement(Id("label1"), None))
//    val e2 = Opt(fx, DeclarationStatement(Declaration(List(Opt(fx, IntSpecifier())), List(Opt(fx, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("a"), List()), List(), None))))))
//    val e3 = Opt(fx.not(), LabelStatement(Id("label1"), None))
//    val e4 = Opt(fx.not(), DeclarationStatement(Declaration(List(Opt(fx.not(), IntSpecifier())), List(Opt(fx.not(), InitDeclaratorI(AtomicNamedDeclarator(List(), Id("b"), List()), List(), None))))))
//    val e5 = Opt(True, LabelStatement(Id("label2"), None))
//    val f = FunctionDef(List(Opt(True, VoidSpecifier())), AtomicNamedDeclarator(List(),Id("foo"),List(Opt(True,DeclIdentifierList(List())))), List(), CompoundStatement(List(e0, e1, e2, e3, e4, e5)))
//
//    val env = CASTEnv.createASTEnv(f)
//    succ(e0, env) should be (List(e1.entry, e3.entry))
//    succ(e1, env) should be (List(e2.entry))
//    succ(e2, env) should be (List(e5.entry))
//    succ(e3, env) should be (List(e4.entry))
//    succ(e4, env) should be (List(e5.entry))
//    println("succs: " + DotGraph.map2file(getAllSucc(f, env), env))
//  }
//
//  @Test def test_liveness_std() {
//    val a = parseFunctionDef("""
//    void foo() {
//      a = 0;
//      l1: b = a + 1;
//      c = c + b;
//      a = b + 2;
//      if (a < 10) goto l1;
//      return c;
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    val ss = getAllSucc(a.stmt.innerStatements.head.entry, env).map(_._1).filterNot(_.isInstanceOf[FunctionDef])
//
//    for (s <- ss)
//      println(PrettyPrinter.print(s) + "  out: " + outsimple(s, env) + "   in: " + insimple(s, env))
//
//    println("#################################################")
//
//    for (s <- ss)
//      println(PrettyPrinter.print(s) + "  out: " + out(s, env) + "   in: " + in(s, env))
//  }
//
//  @Test def test_simpel_function() {
//    val a = parseFunctionDef("""
//    void foo() {
//      #ifdef A
//      int a;
//      #else
//      int anot;
//      #endif
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
//  }
//
//  @Test def test_liveness_simple() {
//    val a = parseCompoundStmt("""
//    {
//      int a = 1;
//      int b = c;
//    }
//    """)
//
//    println("defines: " + defines(a))
//    println("uses: " + uses(a))
//  }
//
//  @Test def test_liveness_simple_constructed() {
//    val s1 = Opt(True, DeclarationStatement(Declaration(List(Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("a"), List()), List(), Some(Initializer(None, Id("b")))))))))
//    val s2 = Opt(True, DeclarationStatement(Declaration(List(Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("c"), List()), List(), Some(Initializer(None, Id("d")))))))))
//    val c = One(CompoundStatement(List(s1, s2)))
//
//    val env = CASTEnv.createASTEnv(c)
//    println("in   (s1): " + in((s1, env)))
//    println("out  (s1): " + out((s1, env)))
//    println("in   (s2): " + in((s2, env)))
//    println("out  (s2): " + out((s2, env)))
//  }
//
//  // stack overflow
//  @Ignore def test_liveness_constructed() {
//    val s1 = Opt(True, DeclarationStatement(Declaration(List(Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("y"), List()), List(), Some(Initializer(None, Id("v")))))))))
//    val s2 = Opt(fx, DeclarationStatement(Declaration(List(Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("z"), List()), List(), Some(Initializer(None, Id("y")))))))))
//    val s3 = Opt(True, DeclarationStatement(Declaration(List(Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("x"), List()), List(), Some(Initializer(None, Id("v")))))))))
//    val s41 = Opt(fy, ExprStatement(AssignExpr(Id("x"), "=", Id("w"))))
//    val s42 = Opt(True, ExprStatement(AssignExpr(Id("x"), "=", Id("v"))))
//    val s4 = Opt(True, WhileStatement(Id("x"), One(CompoundStatement(List(s41, s42)))))
//    val s5 = Opt(True, ReturnStatement(Some(Id("x"))))
//    val c = CompoundStatement(List(s1, s2, s3, s4, s5))
//
//    val env = CASTEnv.createASTEnv(c)
//    println("in      (s1): " + in((s1, env)))
//    println("out     (s1): " + out((s1, env)))
//    println("defines (s1): " + defines(s1))
//    println("uses    (s1): " + uses(s1))
//    println("#"*80)
//    println("in      (s2): " + in((s2, env)))
//    println("out     (s2): " + out((s2, env)))
//    println("defines (s2): " + defines(s2))
//    println("uses    (s2): " + uses(s2))
//    println("#"*80)
//    println("in      (s3): " + in((s3, env)))
//    println("out     (s3): " + out((s3, env)))
//    println("defines (s3): " + defines(s3))
//    println("uses    (s3): " + uses(s3))
//    println("#"*80)
//    println("in      (s4): " + in((s4, env)))
//    println("out     (s4): " + out((s4, env)))
//    println("defines (s4): " + defines(s4))
//    println("uses    (s4): " + uses(s4))
//    println("#"*80)
//    println("in      (s41): " + in((s41, env)))
//    println("out     (s41): " + out((s41, env)))
//    println("defines (s41): " + defines(s41))
//    println("uses    (s41): " + uses(s41))
//    println("#"*80)
//    println("in      (s42): " + in((s42, env)))
//    println("out     (s42): " + out((s42, env)))
//    println("defines (s42): " + defines(s42))
//    println("uses    (s42): " + uses(s42))
//    println("#"*80)
//    println("in      (s5): " + in((s5, env)))
//    println("out     (s5): " + out((s5, env)))
//    println("defines (s5): " + defines(s5))
//    println("uses    (s5): " + uses(s5))
//  }
//
//  @Test def test_binary_search() {
//    val a = parseFunctionDef("""
//     int binarySearch(int sortedArray[], int first, int last, int key) {
//       // function:
//       //   Searches sortedArray[first]..sortedArray[last] for key.
//       // returns: index of the matching element if it finds key,
//       //         otherwise  -(index where it could be inserted)-1.
//       // parameters:
//       //   sortedArray in  array of sorted (ascending) values.
//       //   first, last in  lower and upper subscript bounds
//       //   key         in  value to search for.
//       // returns:
//       //   index of key, or -insertion_position -1 if key is not
//       //                 in the array. This value can easily be
//       //                 transformed into the position to insert it.
//       int a;
//       while (first <= last) {
//         int mid = (first + last) / 2;  // compute mid point.
//         if (key > sortedArray[mid])
//           first = mid + 1;  // repeat search in top half.
//         else if (key < sortedArray[mid])
//           last = mid - 1; // repeat search in bottom half.
//         else
//           return mid;     // found it. return position /////
//       }
//       return -(first + 1);    // failed to find key
//     }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    val succs = getAllSucc(a, env)
//    val preds = getAllPred(a, env)
//
//    val errors = compareSuccWithPred(succs, preds, env)
//    CCFGErrorOutput.printCCFGErrors(succs, preds, errors, env)
//    assert(errors.isEmpty)
//  }
//
//  @Ignore def test_goto_pred_succ() {
//    val a = parseFunctionDef("""
//    int foo(void) {
//      goto l;
//    }
//    """)
//    val env = CASTEnv.createASTEnv(a)
//    val succs = getAllSucc(a, env)
//    val preds = getAllPred(a, env)
//
//    val errors = compareSuccWithPred(succs, preds, env)
//    CCFGErrorOutput.printCCFGErrors(succs, preds, errors, env)
//    assert(errors.isEmpty)
//  }
//
//  @Test def test_goto_pred_succ2() {
//    val a = parseFunctionDef("""
//    int foo(void) {
//      int k;
//      if (k) {
//        goto l;
//      }
//      l:;
//    }
//    """)
//    val env = CASTEnv.createASTEnv(a)
//    val succs = getAllSucc(a, env)
//    val preds = getAllPred(a, env)
//
//    val errors = compareSuccWithPred(succs, preds, env)
//    CCFGErrorOutput.printCCFGErrors(succs, preds, errors, env)
//    assert(errors.isEmpty)
//  }
//
//  @Test def test_bug01() {
//    val a = parseFunctionDef("""
//      int
//      unlzma_main (int argc, char **argv) {
//        #if definedEx(CONFIG_LZMA)
//        int opts = getopt32(argv , "cfvdt");
//        #endif
//        #if (!definedEx(CONFIG_LZMA) && (!definedEx(CONFIG_UNLZMA) || !definedEx(CONFIG_LZMA)))
//        getopt32(argv , "cfvdt");
//        #endif
//        #if definedEx(CONFIG_LZMA)
//        if (((applet_name[2] == 'm') && (! (opts & (OPT_DECOMPRESS | OPT_TEST))))) bb_show_usage();
//        #endif
//        if ((applet_name[2] == 'c')) (option_mask32 |= OPT_STDOUT);
//        (argv += optind);
//        return bbunpack(argv , unpack_unlzma , make_new_name_generic , "lzma");
//      }
//    """)
//    val env = CASTEnv.createASTEnv(a)
//    val succs = getAllSucc(a, env)
//    val preds = getAllPred(a, env)
//
//    val errors = compareSuccWithPred(succs, preds, env)
//    CCFGErrorOutput.printCCFGErrors(succs, preds, errors, env)
//    assert(errors.isEmpty)
//  }
//
//  @Test def test_bug01_simplified() {
//    val a = parseFunctionDef("""
//      void foo () {
//        #if definedEx(A)
//        k:
//        #else
//        l:
//        #endif
//        #if definedEx(A)
//        m:
//        #endif
//        n:
//      }
//    """)
//    val env = CASTEnv.createASTEnv(a)
//    val succs = getAllSucc(a, env)
//    val preds = getAllPred(a, env)
//
//    val errors = compareSuccWithPred(succs, preds, env)
//    CCFGErrorOutput.printCCFGErrors(succs, preds, errors, env)
//    assert(errors.isEmpty)
//  }
//
//  @Test def test_bug02() {
//    val a = parseFunctionDef("""
//    static void handle_compress(void) {
//      EState *s = strm->state;
//      while (1) {
//        if (s->state == 1) {
//          copy_output_until_stop(s);
//          if (s->state_out_pos < s->numZ) break;
//          if (((s->mode == 4) && (s->strm->avail_in == 0) && isempty_RL(s))) break;
//          prepare_new_block(s);
//          s->state = 2;
//        }
//        if ((s->state == 2)) {
//          copy_input_until_stop(s);
//          if (((s->mode != 2) && (s->strm->avail_in == 0))) {
//            flush_RL(s);
//            BZ2_compressBlock(s , (s->mode == 4));
//            (s->state = 1);
//          } else if ((s->nblock >= s->nblockMAX)) {
//            BZ2_compressBlock(s , 0);
//            (s->state = 1);
//          } else if ((s->strm->avail_in == 0)) {
//            break;
//          }
//        }
//      }
//    }
//    """)
//    val env = CASTEnv.createASTEnv(a)
//    val succs = getAllSucc(a, env)
//    val preds = getAllPred(a, env)
//
//    val errors = compareSuccWithPred(succs, preds, env)
//    CCFGErrorOutput.printCCFGErrors(succs, preds, errors, env)
//    assert(errors.isEmpty)
//  }
//
//  @Test def test_bug02_simplified() {
//    val a = parseFunctionDef("""
//    static void handle_compress(void) {
//      while (1) {
//        if (s) {
//          if (s1) break;
//          if (s2) break;
//          s = 1;
//        }
//        if (s3) {
//          s = 2;
//          if (s4) {
//            s = 3;
//          } else if (s5) {
//            s = 4;
//          } else if (s6) {
//            break;
//          }
//        }
//      }
//    }
//    """)
//    val env = CASTEnv.createASTEnv(a)
//    val succs = getAllSucc(a, env)
//    val preds = getAllPred(a, env)
//
//    val errors = compareSuccWithPred(succs, preds, env)
//    CCFGErrorOutput.printCCFGErrors(succs, preds, errors, env)
//    assert(errors.isEmpty)
//  }
//
//  @Test def test_label_in_switch() {
//    val a = parseFunctionDef("""
//    int foo(int param) {
//      int exp = 0;
//      int res = -1;
//      switch (exp) {
//        case 0: if (param > 0) {
//          res = 0;
//          break;
//        } else {
//          goto l;
//        }
//        l:
//        default: res = 2;
//      }
//      return res;
//    }
//    """)
//    val env = CASTEnv.createASTEnv(a)
//    val succs = getAllSucc(a, env)
//    val preds = getAllPred(a, env)
//
//    val errors = compareSuccWithPred(succs, preds, env)
//    CCFGErrorOutput.printCCFGErrors(succs, preds, errors, env)
//    assert(errors.isEmpty)
//  }
//
//  @Test def test_fosd_liveness() {
//    val a = parseFunctionDef("""
//    int foo(void) {
//      int a = 24;
//      int b = 25;
//      int c;
//      c = a << 2;
//      #ifdef A
//      return c;
//      #endif
//      b = 24;
//      return b;
//    }
//    """)
//
//   val env = CASTEnv.createASTEnv(a)
//   val ins = getAllSucc(a.stmt.innerStatements.head.entry, env).map(_._1).filterNot(_.isInstanceOf[FunctionDef])
//
//  for (s <- ins)
//    println(PrettyPrinter.print(s) + "  def: " + defines(s) + "   use: " + uses(s))
//
//
//   for (i <- ins)
//     println("ins: " + PrettyPrinter.print(i) + "  out: " + out(i, env) + "   in: " + in(i, env))
//  }
//
//  @Test def test_fosd_liveness2() {
//    val a = parseFunctionDef("""
//    int foo(void) {
//      a = 0;
//      l1: b = a + 1;
//      c = c + b;
//      a = b + 2;
//      if (a < N) goto l1;
//      return c;
//    }
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    val ins = getAllSucc(a.stmt.innerStatements.head.entry, env).map(_._1).filterNot(_.isInstanceOf[FunctionDef])
//
//    for (s <- ins)
//      println(PrettyPrinter.print(s) + "  def: " + defines(s) + "   use: " + uses(s))
//
//    for (i <- ins)
//      println("ins: " + PrettyPrinter.print(i) + "  out: " + out(i, env) + "   in: " + in(i, env))
//  }
//
//  @Test def test_boa_hash() {
//    val a = parseFunctionDef("""
//    void test()
//    {
//          int i;
//          hash_struct *temp;
//          int total = 0;
//          int count;
//
//          for (i = 0; i < MIME_HASHTABLE_SIZE; ++i) { /* these limits OK? */
//              if (mime_hashtable[i]) {
//                  count = 0;
//                  temp = mime_hashtable[i];
//                  while (temp) {
//                      temp = temp->next;
//                      ++count;
//                  }
//      #ifdef NOISY_SIGALRM
//                  log_error_time();
//                  fprintf(stderr, "mime_hashtable[%d] has %d entries\n",
//                          i, count);
//      #endif
//                  total += count;
//              }
//          }
//          log_error_time();
//          fprintf(stderr, "mime_hashtable has %d total entries\n",
//                  total);
//
//          total = 0;
//          for (i = 0; i < PASSWD_HASHTABLE_SIZE; ++i) { /* these limits OK? */
//              if (passwd_hashtable[i]) {
//                  temp = passwd_hashtable[i];
//                  count = 0;
//                  while (temp) {
//                      temp = temp->next;
//                      ++count;
//                  }
//      #ifdef NOISY_SIGALRM
//                  log_error_time();
//                  fprintf(stderr, "passwd_hashtable[%d] has %d entries\n",
//                          i, count);
//      #endif
//                  total += count;
//              }
//          }
//
//          log_error_time();
//          fprintf(stderr, "passwd_hashtable has %d total entries\n",
//                  total);
//
//      }
//
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    val succs = getAllSucc(a, env)
//    val preds = getAllPred(a, env)
//
//    val errors = compareSuccWithPred(succs, preds, env)
//    CCFGErrorOutput.printCCFGErrors(succs, preds, errors, env)
//    assert(errors.isEmpty)
//  }
//
//  @Test def test_choice() {
//    val a = parseFunctionDef("""
//int hello() {
//// copied from coreutils/ls.pi:80305 - 80320
//	if (
//#if definedEx(CONFIG_FEATURE_CLEAN_UP)
//	1
//#endif
//#if !definedEx(CONFIG_FEATURE_CLEAN_UP)
//	0
//#endif
//	)
//
//#if !definedEx(CONFIG_FEATURE_LS_RECURSIVE)
//	((void)0)
//#endif
//#if definedEx(CONFIG_FEATURE_LS_RECURSIVE)
//	bar()
//#endif
//	;
//
//	return 1;
//}
//    """)
//
//    val env = CASTEnv.createASTEnv(a)
//    val succs = getAllSucc(a, env)
//    val preds = getAllPred(a, env)
//
//    val errors = compareSuccWithPred(succs, preds, env)
//    CCFGErrorOutput.printCCFGErrors(succs, preds, errors, env)
//    assert(errors.isEmpty)
//  }
}
