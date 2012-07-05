package de.fosd.typechef.crewrite

import org.junit.Test
import de.fosd.typechef.featureexpr.sat._
import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.c._
import de.fosd.typechef.crewrite.CASTEnv._
import de.fosd.typechef.typesystem._

class IfdefToIfTest extends ConditionalNavigation with ASTNavigation with CDefUse with CTypeSystem with TestHelper {

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

  @Test def test_replace_same() {
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
    println(PrettyPrinter.print(i.replaceSame(c.value, e2, e4)))

  }

  @Test def test_struct() {
    val ast = getAST("""
    main {
    int a;
    #if definedEx(A)
    a = 2;
    #elif !definedEx(B)
    a = 4;
    #elif definedEx(C)
    a = 8;
    #elif definedEx(D)
    a = 16;
    #elif definedEx(E)
    a = 32;
    #elif definedEx(F)
    a = 64;
    #elif definedEx(G)
    a = 128;
    #elif definedEx(H)
    a = 256;
    #endif
    }
                     """)
    val env = createASTEnv(ast)
    val i = new IfdefToIf()
    println("Test:\n")

    val fio = i.filterInvariableOpts(ast, env)

    val feat = i.filterFeatures(ast, env)
    println ("++Distinct features in ast" + " (" + feat.size + ")" + "++")
    println(feat)
    val cstmt = i.definedExternalToStruct(feat)
    println()
    println("++CompoundStatement++")
    println(cstmt)
    println()
    println("++Pretty printed++")
    println(PrettyPrinter.print(cstmt))

  }

  @Test def test_map() {
    val ast = getAST("""
      #if definedEx(A)
      int
      #elif definedEx(B)
      double
      #else
      short
      #endif
      foo1()  {
      return 0;
      }

      #if definedEx(A)
      int
      #elif definedEx(B)
      double
      #else
      short
      #endif
      foo2()  {
      return 0;
      }

      #if definedEx(A)
      int
      #else
      short
      #endif
      foo3()  {
      return 0;
      }

      int foo4() {
      return 0;
      }
                     """)
    val env = createASTEnv(ast)
    val i = new IfdefToIf()
    println(i.functionMap(ast))
    println()
    println("Id map:")
    println(i.IdMap)
    for (c <- 0 until i.IdMap.size - 1) {
      println("getFeatureForId(" + c + ") = " + i.getFeatureForId(c))
    }

    //println(i.featureToCExpr(i.getFeatureForId(2)))
  }

  @Test def test_function() {
    val ast = getAST("""
      #if definedEx(A)
      int
      #elif !definedEx(B)
      double
      #else
      short
      #endif
      foo(int jahreseinkommen) {
        return 0;
      }

      #if !definedEx(C)
      int
      #elif definedEx(D)
      double
      #else
      short
      #endif
      foo2(int jahreseinkommen) {
        return 0;
      }

      bar3() {
        println("Wir sind in bar3");
        foo(3);
        foo2(3);
      }

       bar5() {
        println("Wir sind in bar5");
        foo2(5);
        foo(5);
      }
    """)
    println("++ Vorgabe ++")
    println(ast)

    println()
    typecheckTranslationUnit(ast)
    val defUseMap = getDefUseMap
    val i = new IfdefToIf()
    val env = createASTEnv(ast)

    val newAst = i.replaceConvertFunctions(ast, env, defUseMap)
    println(PrettyPrinter.print(newAst))

    //val newAst2 = i.replaceConvertFunctionsNew(ast, env)
    //println("\n\n" + PrettyPrinter.print(newAst2))
  }

  @Test def test_text2() {
    val ast = getAST("""
      #if definedEx(A)
      int
      #elif !definedEx(B)
      double
      #else
      short
      #endif
      foo() {
        println("Hello World!");
        return 0;
      }

      main(void) {
        foo();
      }
                     """)
    println(ast + "\n")
    val i = new IfdefToIf()
    val env = createASTEnv(ast)
    val newAst = i.replaceFunctionDef(ast, env)
    println(PrettyPrinter.print(newAst))
    println("FeatureExpr 0: " + i.getFeatureForId(0))
    println("FeatureExpr 1: " + i.getFeatureForId(1))
    println("FeatureExpr 2: " + i.getFeatureForId(2))
    println("\n\nConverting feature:\n" + i.getFeatureForId(2))
    println("To:\n" + PrettyPrinter.print(i.featureToCExpr(i.getFeatureForId(2))))
  }

  @Test def test_switch1 {
    val source_ast = getAST("""
      void foo_01(int a) {
        int optA = 1;
        switch (a) {
          case 0: printf("in 0\n"); break;
          case 1: printf("in 1\n"); break;
          #if definedEx(A)
          case 2: printf("in 2\n"); break;
          #endif
          case 3: printf("in 3\n"); break;
        }
      }
                            """)
    val target_ast = getAST("""
      void foo_01(int a) {
        int optA = 1;
        switch (a) {
          case 0: printf("in 0\n"); break;
          case 1: printf("in 1\n"); break;
          case 2: if (optA) {
            printf("in 2\n"); break;
          } else {
            break;
          }
          case 3: printf("in 3\n"); break;
        }
      }
                            """)
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)
    println("Source:\n" + source_ast)
    //println("\n\nTarget:\n" + target_ast)
    //println("\n\nTarget:\n" + PrettyPrinter.print(source_ast))

    val newAst = i.setGoToFlags(source_ast, env)
    println("\n++PrettyPrinted++\n" + PrettyPrinter.print(newAst) + "\n")
  }


  @Test def test_switch2 {
    val source_ast = getAST("""
      void foo_02(int a) {
        int optA = 1;
        switch (a) {
          case 0: printf("in 0\n"); break;
          case 1: printf("in 1\n"); break;
          #if definedEx(A)
          case 2: printf("in 2\n");
          #endif
          case 3: printf("in 3\n"); break;
        }
      }
                            """)
    val target_ast = getAST("""
      void foo_02(int a) {
        int optA = 1;
        switch (a) {
          case 0: printf("in 0\n"); break;
          case 1: printf("in 1\n"); break;
          case 2: if (optA) {
            printf("in 2\n");
          } else {
            goto sexit;
          }
          case 3: printf("in 3\n"); break;
        }
      sexit:;
      }
                            """)

    val i = new IfdefToIf
    val env = createASTEnv(source_ast)
    println("Source:\n" + source_ast)
    //println("\n\nTarget:\n" + target_ast)
    //println("\n\nTarget:\n" + PrettyPrinter.print(source_ast))

    val newAst = i.setGoToFlags(source_ast, env)
    println("\n++PrettyPrinted++\n" + PrettyPrinter.print(newAst) + "\n")
  }


  @Test def test_switch3 {
    val source_ast = getAST("""
      void foo_03(int a) {
        int optA = 1;
        switch (a) {
          case 0: printf("in 0\n"); break;
          case 1: printf("in 1\n"); break;
          #if definedEx(A)
          case 2: printf("in 2A\n"); break;
          #endif
          #if definedEx(B)
          case 2: printf("in 2B\n"); break;
          #endif
          #if definedEx(C)
          case 2: printf("in 2C\n"); break;
          #else
          case 2: printf("in !2C\n"); break;
          #endif
          case 3: printf("in 3\n"); break;
        }
      }
                            """)
    val target_ast = getAST("""
      void foo_03(int a) {
        int optA = 1;
        switch (a) {
          case 0: printf("in 0\n"); break;
          case 1: printf("in 1\n"); break;
          case 2: if (options.a) {
            printf("in 2A\n");
          }
          if (! options.a) {
            printf("in !2A\n"); break;
          }
          case 3: printf("in 3\n"); break;
        }
      }
                            """)
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)
    println("Source:\n" + source_ast)
    //println("\n\nTarget:\n" + target_ast)
    //println("\n\nTarget:\n" + PrettyPrinter.print(source_ast))

    val newAst = i.setGoToFlags(source_ast, env)
    println("\n++PrettyPrinted++\n" + PrettyPrinter.print(newAst) + "\n")
  }


  @Test def test_switch4 {
    val source_ast = getAST("""
      void foo_04(int a) {
        int optA = 1;
        switch (a) {
          case 0: printf("in 0\n"); break;
          case 1: printf("in 1\n"); break;
          #if definedEx(A)
          case 2: printf("in 2\n"); break;
          #endif
          case 3: printf("in 3\n"); break;
          default: printf("in default\n"); break;
        }
      }
                            """)
    val target_ast = getAST("""
      void foo_04(int a) {
        int optA = 1;
        switch (a) {
          case 0: printf("in 0\n"); break;
          case 1: printf("in 1\n"); break;
          case 2: if (options.a) {
            printf("in 2\n");
          }
          if (options.a) {
            break;
          }
          if (! options.a) {
            goto sdefault;
          }
          case 3: printf("in 3\n"); break;
          sdefault:;
          default: printf("in default\n"); break;
        }
      }
                            """)
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)
    println("Source:\n" + source_ast)
    //println("\n\nTarget:\n" + target_ast)
    //println("\n\nTarget:\n" + PrettyPrinter.print(source_ast))

    val newAst = i.setGoToFlags(source_ast, env)
    println("\n++PrettyPrinted++\n" + PrettyPrinter.print(newAst) + "\n")
  }

  @Test def if_test {
    val source_ast = getAST("""
      void foo_04(int a) {
      int i;
      #if definedEx(A)
      i = 32;
      #elif definedEx(B)
      i = 64;
      #else
      i = 128;
      #endif
      }
                            """)
    val target_ast = getAST("""
      void foo_04(int a) {
        int i;
        if (options.a) {
         i = 32;
        }
        if (!options.a && options.b) {
          i = 64;
        }
        if (!options.b&!options.a) {
          i = 128;
        }
      }
                            """)
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)
    println("Source:\n" + source_ast)
    println("\n\nTarget:\n" + target_ast + "\n")
    println("\n\nPrettyPrinted:\n" + PrettyPrinter.print(i.replaceIfs(source_ast, env)))

  }

  @Test def if_test2 {
    val source_ast = getAST("""
      void foo_04(int a) {
      int i = 0;
      if (i) {
        i = 32;
      }
      #if definedEx(A)
      else {
        i = 64;
      }
      #endif
      }
                            """)
    val target_ast = getAST("""
      void foo_04(int a) {
        int i = 0;
        if (i) {
          i = 32;
        } else if (options.a) {
          i = 64;
        }
      }
                            """)
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)
    println("Source:\n" + source_ast)
    println("\n\nTarget:\n" + target_ast + "\n")
    println("\n\n" + PrettyPrinter.print(i.replaceIfs(source_ast, env)))

  }

  @Test def test_jump {
    val source_ast = getAST("""
      int main(void) {
        goto j1;
        goto j2;
        goto j3;

        #if definedEx(A)
        j1:
        #endif
        #if definedEx(B)
        j1:
        #endif
        #if definedEx(C)
        j1:
        #endif
        #if definedEx(D)
        j2:
        #else
        j3:
        #endif

        return 0;
      }
                            """);
    val target_ast = getAST("""
      int main(void) {
        if(options.a) {
          goto _0_j1;
        }
        if(options.b) {
          goto _1_j1;
        }
        if(options.c) {
          goto _2_j1;
        }

        if(options.d) {
          goto _3_j2;
        }

        if(! options.d) {
          goto _4_j3;
        }

        if(options.a) {
          _0_j1:
        }
        if(options.b) {
          _1_j1:
        }
        if(options.c) {
          _2_j1:
        }
        if(options.d) {
          _3_j2:
        }
        if(! options.d) {
          _4_j3:
        }

        return 0;
      }
                            """);
    println("Source:\n" + source_ast)
    //println("\n\nTarget:\n" + target_ast + "\n\n")

    val i = new IfdefToIf
    val env = createASTEnv(source_ast)
    println("++ Pretty printed: +++\n" + PrettyPrinter.print(i.replaceLabelsGotos(source_ast, env)))
  }

  @Test def test_int {
    val source_ast = getAST("""
      int main(void) {
        #if definedEx(A)
        int i = 8;
        #endif
        #if definedEx(B)
        int i = 16;
        #endif
        #if definedEx(C)
        int i = 32;
        #else
        int i = 64;
        #endif

        #if definedEx(D)
        int j = 32;
        #else
        int j = 64;
        #endif

        i = i*i;
        j = 2*j;
        return 0;
      }
                            """);
    val target_ast = getAST("""
      int main(void) {
        int _0_i = 8

        int _1_i;
        if(options.b) {
          int _1_i = 16;
        }
        int _2_i;
        if(options.c) {
          int _2_i = 32;
        }
        int _3_i;
        if(!options.a && !options.b && !options.c) {
          int _3_i = 64;
        }

        int _4_j;
        if(options.d) {
          int _4_j = 32;
        }
        int _5_j;
        if(! options.d) {
          int _5_j = 64;
        }


        if(options.a) {
          _0_i = _0_i * _0_i;
        }
        if(options.b) {
          _1_i = _1_i * _1_i;
        }
        if(options.c) {
          _2_i = _2_i * _2_i;
        }
        if (!options.a && !options.b && options.c) {
          _3_i = _3_i * _3_i;
        }
        if(options.d) {
          _4_j = 2 * _4_j;
        }
        if(! options.d) {
          _5_j = 2 * _5_j;
        }

        return 0;
      }
                            """);
    println("Source:\n" + source_ast + "\n")
    //println("\n\nTarget:\n" + target_ast + "\n\n")

    val i = new IfdefToIf
    val env = createASTEnv(source_ast)
    println(PrettyPrinter.print(i.replaceLabelsGotos(source_ast, env)))
  }

  @Test def ac_test {
    val source_ast = getAST("""
      #ifdef CONFIG_ACPI_PROCFS_POWER
      static const struct file_operations acpi_ac_fops = {
        .owner = THIS_MODULE,
        .open = acpi_ac_open_fs,
        .read = seq_read,
        .llseek = seq_lseek,
        .release = single_release,
        };
        #endif
                            """);
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println(PrettyPrinter.print(i.transformVariableDeclarations(source_ast, env)))
  }

  @Test def normal_struct {
    val source_ast = getAST("""
      static const struct file_operations acpi_ac_fops = {
        .owner = THIS_MODULE,
        .open = acpi_ac_open_fs,
        .read = seq_read,
        .llseek = seq_lseek,
        .release = single_release,
        };
                            """);
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println(i.transformVariableDeclarations(source_ast, env))
  }

  @Test def test_opt_in_struct {
    val source_ast = getAST("""
      const unsigned int e2attr_flags_value[] = {
      #ifdef ENABLE_COMPRESSION
	      EXT2_COMPRBLK_FL,
	      EXT2_DIRTY_FL,
	      EXT2_NOCOMPR_FL,
      	EXT2_ECOMPR_FL,
     #endif
	      EXT2_INDEX_FL,
	      EXT2_SECRM_FL,
      	EXT2_UNRM_FL,
	      EXT2_SYNC_FL,
	      EXT2_DIRSYNC_FL,
	      EXT2_IMMUTABLE_FL,
	      EXT2_APPEND_FL,
	      EXT2_NODUMP_FL,
	      EXT2_NOATIME_FL,
	      EXT2_COMPR_FL,
	      EXT3_JOURNAL_DATA_FL,
	      EXT2_NOTAIL_FL,
	      EXT2_TOPDIR_FL
    };

                            """);
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println("+++PrettyPrinted result+++\n" + PrettyPrinter.print(i.transformVariableDeclarations(source_ast, env)))
  }

  @Test def test_opt_struct {
    val source_ast = getAST("""
      #ifdef ENABLE_COMPRESSION
      const unsigned int e2attr_flags_value[] = {
	      EXT2_COMPRBLK_FL,
	      EXT2_DIRTY_FL,
	      EXT2_NOCOMPR_FL,
      	EXT2_ECOMPR_FL,
      };
      #endif
      """);
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println("+++PrettyPrinted result+++\n" + PrettyPrinter.print(i.transformVariableDeclarations(source_ast, env)))
  }

  @Test def test_opt_int {
    val source_ast = getAST("""
      int main(void) {
        #if definedEx(A)
        int i = 8;
        #endif
        #if definedEx(B)
        int i = 16;
        #endif
        #if definedEx(C)
        int i = 32;
        #else
        int i = 64;
        #endif

        #if definedEx(D)
        int j = 32;
        #else
        int j = 64;
        #endif

        i = i*i;
        j = 2*j;
        return 0;
      }
                            """);
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println("+++PrettyPrinted result+++\n" + PrettyPrinter.print(i.transformVariableDeclarations(source_ast, env)))
  }

  @Test def test_int_def_use {
    val source_ast = getAST("""
      int foo(int x, int z) {
        int i = x + 5;
        i = 5;
        int y;
        y = 5;
        return x + i;
      }
      int main(void) {
        int i = 0;
        i = i + 1;
        foo(i);

        int j;
        j = 10;
        i = j * j;
        return 0;
      }
                            """);
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)

    typecheckTranslationUnit(source_ast)
    val defUseMap = getDefUseMap

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println("\nDef Use Map:\n" + defUseMap)
  }
}