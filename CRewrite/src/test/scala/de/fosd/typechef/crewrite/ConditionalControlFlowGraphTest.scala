package de.fosd.typechef.crewrite

import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import org.junit.{Ignore, Test}
import de.fosd.typechef.featureexpr.True
import de.fosd.typechef.conditional.{Opt, One}

class ConditionalControlFlowGraphTest extends TestHelper with ShouldMatchers with ConditionalControlFlow with CASTEnv {

  @Test def test_if_the_else() {
    val a = parseCompoundStmt("""
    {
      #ifdef A
      int a;
      #elif defined(B)
      int b;
      #else
      int c;
      #endif
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_simple_ifdef() {
    val a = parseCompoundStmt("""
    {
      int a0;
      #ifdef A1
      int a1;
      #endif
      int a2;
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_for_loop() {
    val a = parseCompoundStmt("""
    {
      for (;;) { }
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_nested_loop() {
    val a = parseCompoundStmt("""
    {
      for(;;) {
        for(;;) {
          for(;;) {
          }
        }
      }
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_switch_case() {
    val a = parseCompoundStmt("""
    {
      switch(x) {
      case 1: break;
      case 2: break;
      case 3: break;
      default: break;
      }
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_do_while_loop() {
    val a = parseCompoundStmt("""
    {
      do {
      } while (k);
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_while_loop() {
    val a = parseCompoundStmt("""
    {
      while (k) {
        k--;
      }
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_if_the_else_chain() {
    val a = parseCompoundStmt("""
    {
      int k = 3;
      if (k < 3) {
        k = -1;
      }
      #ifdef A
      else if (k = 3) {
        k = 0;
      }
      #endif
      else {
        k = 1;
      }
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_labelstatements_if_elif_else() {
    val e1 = Opt(True, LabelStatement(Id("e1"), None))
    val e2 = Opt(fx, LabelStatement(Id("e2"), None))
    val e3 = Opt(fy.and(fx.not()), LabelStatement(Id("e3"), None))
    val e4 = Opt(fy.not().and(fx.not()), LabelStatement(Id("e4"), None))
    val e5 = Opt(True, LabelStatement(Id("e5"), None))
    val c = One(CompoundStatement(List(e1, e2, e3, e4, e5)))

    val env = createASTEnv(c.value)
    succ(e1, env) should be (List(e2.entry, e3.entry, e4.entry))
    succ(e2, env) should be (List(e5.entry))
    succ(e3, env) should be (List(e5.entry))
    succ(e4, env) should be (List(e5.entry))
    DotGraph.map2file(getAllSucc(e1.entry, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_labelstatements_with_sequence_of_annotated_elements() {
    val e1 = Opt(True, LabelStatement(Id("e1"), None))
    val e2 = Opt(fx, LabelStatement(Id("e2"), None))
    val e3 = Opt(fx, LabelStatement(Id("e3"), None))
    val e4 = Opt(fx.not(), LabelStatement(Id("e4"), None))
    val e5 = Opt(True, LabelStatement(Id("e5"), None))
    val c = One(CompoundStatement(List(e1, e2, e3, e4, e5)))

    val env = createASTEnv(c.value)
    succ(e1, env) should be(List(e2.entry, e4.entry))
    succ(e2, env) should be(List(e3.entry))
    succ(e3, env) should be(List(e5.entry))
    succ(e4, env) should be(List(e5.entry))
    DotGraph.map2file(getAllSucc(e1.entry, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_labelstatements_if_if_else() {
    val e0 = Opt(fx, LabelStatement(Id("e0"), None))
    val e1 = Opt(True, LabelStatement(Id("e1"), None))
    val e2 = Opt(True, LabelStatement(Id("e2"), None))
    val e3 = Opt(fx, LabelStatement(Id("e3"), None))
    val e4 = Opt(fx.not().and(fy), LabelStatement(Id("e4"), None))
    val e5 = Opt(fx.not().and(fy.not()), LabelStatement(Id("e5"), None))
    val e6 = Opt(fa, LabelStatement(Id("e6"), None))
    val e7 = Opt(fa.not(), LabelStatement(Id("e7"), None))
    val e8 = Opt(fb.not(), LabelStatement(Id("e8"), None))
    val e9 = Opt(True, LabelStatement(Id("e9"), None))
    val c = One(CompoundStatement(List(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9)))

    val env = createASTEnv(c.value)
    succ(e0, env) should be(List(e1.entry))
    succ(e1, env) should be(List(e2.entry))
    succ(e2, env) should be(List(e3.entry, e4.entry, e5.entry))
    succ(e3, env) should be(List(e6.entry, e7.entry))
    succ(e4, env) should be(List(e6.entry, e7.entry))
    succ(e5, env) should be(List(e6.entry, e7.entry))
    succ(e6, env) should be(List(e8.entry, e9.entry))
    succ(e7, env) should be(List(e8.entry, e9.entry))
    succ(e8, env) should be(List(e9.entry))
    DotGraph.map2file(getAllSucc(e0.entry, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_declaration_statement() {
    val e0 = Opt(True, LabelStatement(Id("e0"), None))
    val e1 = Opt(fx,
      DeclarationStatement(
        Declaration(
          List(Opt(True,IntSpecifier())),
          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("k"),List()),List(),None))))))
    val e2 = Opt(fx.not(),
      DeclarationStatement(
        Declaration(
          List(Opt(True,DoubleSpecifier())),
          List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("k"),List()),List(),None))))))
    val e3 = Opt(True, LabelStatement(Id("e3"), None))
    val c = One(CompoundStatement(List(e0, e1, e2, e3)))

    val env = createASTEnv(c.value)
    succ(e0, env) should be(List(e1.entry, e2.entry))
    succ(e1, env) should be(List(e3.entry))
    succ(e2, env) should be(List(e3.entry))
    DotGraph.map2file(getAllSucc(e0.entry, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_while_statement() {
    val e0 = Opt(True, LabelStatement(Id("e0"), None))
    val e11 = Opt(True, LabelStatement(Id("e11"), None))
    val e12 = Opt(fy, LabelStatement(Id("e12"), None))
    val e1c = Id("k")
    val e1 = Opt(fx, WhileStatement(e1c, One(CompoundStatement(List(e11, e12)))))
    val e2 = Opt(True, LabelStatement(Id("e2"), None))
    val c = One(CompoundStatement(List(e0, e1, e2)))

    val env = createASTEnv(c.value)
    succ(e0, env) should be(List(e1.entry, e2.entry))
    succ(e1, env) should be(List(e1c))
    succ(e1c, env) should be(List(e11.entry, e2.entry))
    DotGraph.map2file(getAllSucc(e0.entry, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_for_loop() {
    val a = parseCompoundStmt("""
    {
      int k = 2;
      int i;
      for(i=0;
      #ifdef A
      i<10
      #endif
      ;i++) j++;
      int j;
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_if_statement() {
    val a = parseCompoundStmt("""
    {
      int k = 3;
      if (k < 2) { k = 2; }
      #ifdef A
      else if (k < 5) { k = 5; }
      #endif
      #ifdef B
      else if (k < 7) { k = 7; }
      #endif
      else { k = 10; }
      int l = 3;
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_switch_statement() {
    val a = parseCompoundStmt("""
    {
      int k = 3;
      switch (k) {
      case 1: break;
      #ifdef A
      case 2: break;
      #endif
      case 3: break;
      default: break;
      }
      int l = 2;
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_for_loop_elems() {
    val e0 = Opt(True, LabelStatement(Id("e0"), None))
    val e1 = Opt(fx, ForStatement(
      Some(AssignExpr(Id("i"),"=",Constant("0"))),
      Some(AssignExpr(Id("i"),"=",Constant("2"))),
      Some(PostfixExpr(Id("i"),SimplePostfixSuffix("++"))),
      One(CompoundStatement(List(Opt(fx,ExprStatement(PostfixExpr(Id("j"),SimplePostfixSuffix("++")))))))))
    val e2 = Opt(True, LabelStatement(Id("e2"), None))
    val c = One(CompoundStatement(List(e0, e1, e2)))

    val env = createASTEnv(c.value)
    DotGraph.map2file(getAllSucc(e0.entry, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_for_loop_alternative() {
    val a = parseCompoundStmt("""
    {
      int i;
      for(;;) {
      #ifdef A
      int a;
      #else
      double a;
      #endif
      }
      int j;
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_for_loop_infinite() {
    val a = parseCompoundStmt("""
    {
      int i;
      for(;;) {
      }
      int j;
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_conditional_for_loop_infinite_single_statement() {
    val a = parseCompoundStmt("""
    {
      int i = 0;
      for(;;) {
        i++;
      }
      int j;
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

  @Test def test_statements_increment() {
    val a = parseCompoundStmt("""
    {
      int k = 0;
      k++;
      k++;
      k++;
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

//  test("conditional label and jump statements", totest) {
//    val a = parseCompoundStmt("""
//    {
//      label1:
//      int k;
//      int l;
//      if (l != 0)
//        goto label1;
//    }
//    """, p.compoundStatement)
//    DotGraph.map2file(getAllSucc(childAST(a.children.next)))
//  }

  @Test def test_conditional_statements() {
    val a = parseCompoundStmt("""
    {
      int a = 2;
      int b = 200;
      while (
      #ifdef A
      a < b
      #else
      true
      #endif
      )
      {
        a++;
        #ifdef B
        b--;
        #endif
      }
      #ifdef C
      b = 20;
      a = 30;
      #endif
      while (a > b) {
        a++;
      }
      int c;
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

//  test("conditional label and goto statements", totest) {
//    val a = parseCompoundStmt("""
//    {
//      goto label1;
//      #ifdef A
//      label1:
//        int a;
//      #else
//      label1:
//        int b;
//      #endif
//      label2:
//    }
//    """, p.compoundStatement)
//    DotGraph.map2file(getAllSucc(childAST(a.children.next)))
//  }


//  test("conditional label and goto statements - constructed", totest) {
//    val e0 = Opt(FeatureExpr.base, GotoStatement(Id("label1")))
//    val e1 = Opt(fx, LabelStatement(Id("label1"), None))
//    val e2 = Opt(fx, DeclarationStatement(Declaration(List(Opt(fx, IntSpecifier())), List(Opt(fx, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("a"), List()), List(), None))))))
//    val e3 = Opt(fx.not, LabelStatement(Id("label1"), None))
//    val e4 = Opt(fx.not, DeclarationStatement(Declaration(List(Opt(fx.not, IntSpecifier())), List(Opt(fx.not, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("b"), List()), List(), None))))))
//    val e5 = Opt(FeatureExpr.base, LabelStatement(Id("label2"), None))
//    val f = FunctionDef(List(Opt(FeatureExpr.base, VoidSpecifier())), AtomicNamedDeclarator(List(),Id("foo"),List(Opt(True,DeclIdentifierList(List())))), List(), CompoundStatement(List(e0, e1, e2, e3, e4, e5)))
//    succ(e0) should be (List(e1.entry, e3.entry))
//    succ(e1) should be (List(e2.entry))
//    succ(e2) should be (List(e5.entry))
//    succ(e3) should be (List(e4.entry))
//    succ(e4) should be (List(e5.entry))
//    DotGraph.map2file(getAllSucc(childAST(e0)))
//  }

  @Test def test_simpel_function() {
    val a = parseFunctionDef("""
    void foo() {
      #ifdef A
      int a;
      #else
      int anot;
      #endif
    }
    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }

//  test("testLiveness") {
//    val a = parseCompoundStmt("""
//    {
//      int y = v;       // s1
//      int z = y;       // s2
//      int x = v;       // s3
//      while (x) {      // s4
//        x = w;         // s41
//        x = v;         // s42
//      }
//      return x;        // s5
//    }
//    """, p.compoundStatement)
//    println("in: " + in(childAST(a.children.next)))
//    println("out: " + out(childAST(a.children.next)))
//    println("defines: " + defines(childAST(a.children.next)))
//    println("uses: " + uses(childAST(a.children.next)))
//  }

//  test("testLiveness - constructed") {
//    val s1 = Opt(True, DeclarationStatement(Declaration(List(Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("y"), List()), List(), Some(Initializer(None, Id("v")))))))))
//    val s2 = Opt(fx, DeclarationStatement(Declaration(List(Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("z"), List()), List(), Some(Initializer(None, Id("y")))))))))
//    val s3 = Opt(True, DeclarationStatement(Declaration(List(Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("x"), List()), List(), Some(Initializer(None, Id("v")))))))))
//    val s41 = Opt(fy, ExprStatement(AssignExpr(Id("x"), "=", Id("w"))))
//    val s42 = Opt(True, ExprStatement(AssignExpr(Id("x"), "=", Id("v"))))
//    val s4 = Opt(True, WhileStatement(Id("x"), One(CompoundStatement(List(s41, s42)))))
//    val s5 = Opt(True, ReturnStatement(Some(Id("x"))))
//    val c = One(CompoundStatement(List(s1, s2, s3, s4, s5)))
//
//    println("in      (s1): " + in(childAST(s1)))
//    println("out     (s1): " + out(childAST(s1)))
//    println("defines (s1): " + defines(childAST(s1)))
//    println("uses    (s1): " + uses(childAST(s1)))
//    println("#"*80)
//    println("in      (s2): " + in(childAST(s2)))
//    println("out     (s2): " + out(childAST(s2)))
//    println("defines (s2): " + defines(childAST(s2)))
//    println("uses    (s2): " + uses(childAST(s2)))
//    println("#"*80)
//    println("in      (s3): " + in(childAST(s3)))
//    println("out     (s3): " + out(childAST(s3)))
//    println("defines (s3): " + defines(childAST(s3)))
//    println("uses    (s3): " + uses(childAST(s3)))
//    println("#"*80)
//    println("in      (s4): " + in(childAST(s4)))
//    println("out     (s4): " + out(childAST(s4)))
//    println("defines (s4): " + defines(childAST(s4)))
//    println("uses    (s4): " + uses(childAST(s4)))
//    println("#"*80)
//    println("in      (s41): " + in(childAST(s41)))
//    println("out     (s41): " + out(childAST(s41)))
//    println("defines (s41): " + defines(childAST(s41)))
//    println("uses    (s41): " + uses(childAST(s41)))
//    println("#"*80)
//    println("in      (s42): " + in(childAST(s42)))
//    println("out     (s42): " + out(childAST(s42)))
//    println("defines (s42): " + defines(childAST(s42)))
//    println("uses    (s42): " + uses(childAST(s42)))
//    println("#"*80)
//    println("in      (s5): " + in(childAST(s5)))
//    println("out     (s5): " + out(childAST(s5)))
//    println("defines (s5): " + defines(childAST(s5)))
//    println("uses    (s5): " + uses(childAST(s5)))
//
//
//
//  }

  @Test def test_boa_hash() {
    val a = parseCompoundStmt("""
    {
          int i;
          hash_struct *temp;
          int total = 0;
          int count;

          for (i = 0; i < MIME_HASHTABLE_SIZE; ++i) { /* these limits OK? */
              if (mime_hashtable[i]) {
                  count = 0;
                  temp = mime_hashtable[i];
                  while (temp) {
                      temp = temp->next;
                      ++count;
                  }
      #ifdef NOISY_SIGALRM
                  log_error_time();
                  fprintf(stderr, "mime_hashtable[%d] has %d entries\n",
                          i, count);
      #endif
                  total += count;
              }
          }
          log_error_time();
          fprintf(stderr, "mime_hashtable has %d total entries\n",
                  total);

          total = 0;
          for (i = 0; i < PASSWD_HASHTABLE_SIZE; ++i) { /* these limits OK? */
              if (passwd_hashtable[i]) {
                  temp = passwd_hashtable[i];
                  count = 0;
                  while (temp) {
                      temp = temp->next;
                      ++count;
                  }
      #ifdef NOISY_SIGALRM
                  log_error_time();
                  fprintf(stderr, "passwd_hashtable[%d] has %d entries\n",
                          i, count);
      #endif
                  total += count;
              }
          }

          log_error_time();
          fprintf(stderr, "passwd_hashtable has %d total entries\n",
                  total);

      }

    """)

    val env = createASTEnv(a)
    DotGraph.map2file(getAllSucc(a, env), env.asInstanceOf[DotGraph.ASTEnv])
  }
}