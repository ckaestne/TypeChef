package de.fosd.typechef.crewrite

import org.junit.Test
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.featureexpr.sat._
import de.fosd.typechef.parser.c._
import de.fosd.typechef.crewrite.CASTEnv._
import de.fosd.typechef.typesystem._
import java.io._
import collection.mutable.ListBuffer
import java.util
import util.IdentityHashMap
import scala.Some
import de.fosd.typechef.conditional.One
import de.fosd.typechef.conditional.Opt
import scala.Tuple2

class IfdefToIfTest extends ConditionalNavigation with ASTNavigation with CDeclUse with CTypeSystem with TestHelper {
  /**
   * Used for reading/writing to database, files, etc.
   * Code From the book "Beginning Scala"
   * http://www.amazon.com/Beginning-Scala-David-Pollak/dp/1430219890
   */
  def using[A <: {def close() : Unit}, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      param.close()
    }

  def writeToFile(fileName: String, data: String) =
    using(new FileWriter(fileName)) {
      fileWriter => fileWriter.write(data)
    }

  def appendToFile(fileName: String, textData: String) = {
    using(new FileWriter(fileName, true)) {
      fileWriter => using(new PrintWriter(fileWriter)) {
        printWriter => printWriter.println(textData)
      }
    }
  }

  def getFileNameWithoutExtension(file: File): String = {
    file.getName().replaceFirst("[.][^.]+$", "")
  }

  def testFile(file: File, i: IfdefToIf) {
    val fileNameWithoutExtension = getFileNameWithoutExtension(file)

    val source_ast = getAstFromPi(file)
    val env = createASTEnv(source_ast)

    typecheckTranslationUnit(source_ast)
    val defUseMap = getDeclUseMap

    val optionsAst = i.getOptionFile(source_ast)
    println("++Pretty printed++")
    println(PrettyPrinter.print(optionsAst))

    val tempAst = i.transformAst(source_ast, env, defUseMap)
    writeToTextFile(fileNameWithoutExtension ++ "_tmp.txt", PrettyPrinter.print(tempAst))
    writeToTextFile(fileNameWithoutExtension ++ "_src.txt", PrettyPrinter.print(source_ast))
  }

  def testAst(source_ast: TranslationUnit): String = {
    val i = new IfdefToIf
    val env = createASTEnv(source_ast)

    typecheckTranslationUnit(source_ast)
    val defUseMap = getDeclUseMap

    val optionsAst = i.getOptionFile(source_ast)
    ("+++Feature Struct+++\n" + PrettyPrinter.print(optionsAst) + "\n\n+++New Code+++\n" + PrettyPrinter.print(i.transformAst(source_ast, env, defUseMap)))
  }

  def testFolder(path: String) {
    val i = new IfdefToIf
    val folder = new File(path)
    val asts = analyseDir(folder)

    asts.foreach(x => writeToTextFile(x._2 ++ "_tmp", PrettyPrinter.print(i.transformAst(x._1, createASTEnv(x._1), getDefUse(x._1)))))

    /*val quad = asts.map(x => (x._1, createASTEnv(x._1), getDefUse(x._1), x._2))
    val newAsts = i.transformAsts(quad)
    newAsts.foreach(x => writeToTextFile(PrettyPrinter.print(x._1), x._2 ++ "_tmp"))*/
  }

  private def getAstFromPi(fileToAnalyse: File): TranslationUnit = {
    println("++Analyse: " + fileToAnalyse.getName + "++")
    val fis = new FileInputStream(fileToAnalyse)
    val ast = parseFile(fis, fileToAnalyse.getName, fileToAnalyse.getParent)
    fis.close()
    ast
  }

  private def getDefUse(ast: TranslationUnit): IdentityHashMap[Id, List[Id]] = {
    typecheckTranslationUnit(ast)
    getDeclUseMap
  }

  @Test def test_folder() {
    val i = new IfdefToIf
    val folderPath = "C:\\users\\flo\\dropbox\\hiwi\\flo\\TypeChef\\ifdeftoif"
    val folder = new File(folderPath)
    val asts = analyseDir(folder)

    val quad = asts.map(x => (x._1, createASTEnv(x._1), getDefUse(x._1), x._2))
    val newAsts = i.transformAsts(quad)
    newAsts.foreach(x => writeToTextFile(x._2 ++ "_tmp", PrettyPrinter.print(x._1)))
  }

  @Test def test_replace() {
    val e1 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e1"), List()), List(), None))))))
    val e2 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e2"), List()), List(), None))))))
    val e21 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e21"), List()), List(), None))))))
    val e22 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e22"), List()), List(), None))))))
    val e3 = Opt(fx,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e3"), List()), List(), None))))))
    val e4 = Opt(fx.not(),
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e4"), List()), List(), None))))))
    val e5 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e5"), List()), List(), None))))))
    val e6 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(fx.not(), IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e5"), List()), List(), None))))))

    val t1 = Opt(True, Declaration(List(Opt(True, ExternSpecifier()), Opt(True, TypeDefTypeSpecifier(Id("smallint")))), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("wrote_pidfile"), List()), List(), None)))))


    val c = One(CompoundStatement(List(e1, e2, e3, e4, e5)))
    val d = CompoundStatement(List(e1, e2, e6))

    val i = new IfdefToIf()
    println(PrettyPrinter.print(c.value))
    println(PrettyPrinter.print(i.replace(c.value, e2, List(e21, e22))))

    val t2 = Opt(True, Declaration(List(Opt(True, ExternSpecifier()), Opt(True, TypeDefTypeSpecifier(Id("smallint")))), List(Opt(fa, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("wrote_pidfile"), List()), List(), None)))))
    val t3 = Opt(True, Declaration(List(Opt(fa, ExternSpecifier()), Opt(True, TypeDefTypeSpecifier(Id("smallint")))), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("wrote_pidfile"), List()), List(), None)))))
    println("T1 next level variablity: " + i.nextLevelContainsVariability(t1.entry))
    println("T2 next level variablity: " + i.nextLevelContainsVariability(t2.entry))
    println("T3 next level variablity: " + i.nextLevelContainsVariability(t3.entry))
    println("C next level variablity: " + i.nextLevelContainsVariability(c.value))
    println("D next level variablity: " + i.nextLevelContainsVariability(d))
    println("E6 next level variablity: " + i.nextLevelContainsVariability(e6.entry))
  }

  @Test def test_replace_same() {
    val e1 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e1"), List()), List(), None))))))
    val e2 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e2"), List()), List(), None))))))
    val e21 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e21"), List()), List(), None))))))
    val e22 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e22"), List()), List(), None))))))
    val e3 = Opt(fx,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e3"), List()), List(), None))))))
    val e4 = Opt(fx.not(),
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e4"), List()), List(), None))))))
    val e5 = Opt(True,
      DeclarationStatement(
        Declaration(
          List(Opt(True, IntSpecifier())),
          List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("e5"), List()), List(), None))))))

    val c = One(CompoundStatement(List(e1, e2, e3, e4, e5)))

    val i = new IfdefToIf()
    println(PrettyPrinter.print(c.value))
    println(PrettyPrinter.print(i.replaceSame(c.value, e2, e4)))

  }

  @Test def test_function() {
    val ast = getAST( """
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
        foo(3);
        foo2(3);
      }

       bar5() {
        foo2(5);
        foo(5);
      }
                      """)
    println(testAst(ast))
  }

  @Test def test_text2() {
    val ast = getAST( """
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
    println(testAst(ast))
  }

  @Test def test_switch1 {
    val source_ast = getAST( """
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
    val target_ast = getAST( """
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
    println(testAst(source_ast))
  }


  @Test def test_switch2 {
    val source_ast = getAST( """
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
    val target_ast = getAST( """
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

    println(testAst(source_ast))
  }


  @Test def test_switch3 {
    val source_ast = getAST( """
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
    val target_ast = getAST( """
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
    println(testAst(source_ast))
  }


  @Test def test_switch4 {
    val source_ast = getAST( """
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
    val target_ast = getAST( """
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
    println(testAst(source_ast))
  }

  @Test def if_test {
    val source_ast = getAST( """
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
    println(testAst(source_ast))

  }

  @Test def if_test2 {
    val source_ast = getAST( """
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
    val target_ast = getAST( """
      void foo_04(int a) {
        int i = 0;
        if (i) {
          i = 32;
        } else if (options.a) {
          i = 64;
        }
      }
                             """)
    println(testAst(source_ast))

  }

  @Test def test_jump {
    val source_ast = getAST( """
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
    val target_ast = getAST( """
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
    println(testAst(source_ast))
  }

  @Test def test_int {
    val source_ast = getAST( """
      int main(void) {
        #if definedEx(A)
        int i = 8;
        #elif definedEx(B)
        int i = 16;
        #elif definedEx(C)
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
    println(testAst(source_ast))
  }

  @Test def test_int2 {
    val source_ast = getAST( """
      int main(void) {
        #if definedEx(A)
        int i = 8;
        #endif
        #if definedEx(B)
        int i = 16;
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
    println(testAst(source_ast))
  }

  @Test def ac_test {
    val source_ast = getAST( """
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
    println(testAst(source_ast))
  }

  @Test def normal_struct {
    val source_ast = getAST( """
      static const struct file_operations acpi_ac_fops = {
        .owner = THIS_MODULE,
        .open = acpi_ac_open_fs,
        .read = seq_read,
        .llseek = seq_lseek,
        .release = single_release,
        };
                             """);
    println(testAst(source_ast))
  }

  @Test def test_opt_in_struct {
    val source_ast = getAST( """
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
    println(testAst(source_ast))
  }

  @Test def test_opt_struct {
    val source_ast = getAST( """
      #ifdef ENABLE_COMPRESSION
      const unsigned int e2attr_flags_value[] = {
	      EXT2_COMPRBLK_FL,
	      EXT2_DIRTY_FL,
	      EXT2_NOCOMPR_FL,
      	EXT2_ECOMPR_FL,
      };
      #endif
                             """);
    println(testAst(source_ast))
  }

  @Test def test_opt_int {
    val source_ast = getAST( """
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
    println(testAst(source_ast))
  }

  @Test def test_int_def_use {
    val source_ast = getAST( """
      int foo(int *x, int z) {
        int i2 = x + 5;
        i2 = 5;
        int y;
        y = 5;
        return x + i2 + z;
      }
      int main(void) {
        int i = 0;
        i = i + 1;
        foo(i);
        int b = 666;
        foo(b);

        int if3 = 5;
        if (if3 == 5) {
          if3 = 10;
        } else {
          if3 = 30;
        }
        int for4;
        for (for4 = 0; for4 < 10; for4++) {
          println(for4);
        }
        int j;
        j = 10;
        i = (j * (j*(j-(j+j)))) - (j*j) + j;
        return (i > j) ? i : j;
      }
                             """);
    println(testAst(source_ast))
  }

  @Test def test_array_def_use {
    val source_ast = getAST( """
      #ifdef awesome
        #define quadrat(q) ((q)*(q))
      #endif
      const int konst = 55;
      int foo(int arr[5], int z) {
        arr[0] = 10;
        arr[1] = 5;
        arr[2] = (arr[0] + arr[1]) * arr[0];
        int x = 5;
        int i2 = x + 5;
        i2 = z;
        int y;
        y = konst;
        konst = 5;
        int missing = 3;
        y = missing;
        int variable;
        #ifdef awesome
          variable = 4;
          int noType = 3;
          int onlyHere = 3;
          z = onlyHere;
          y = quadrat(z);
        #else
          variable = 7;
          float noType = 7;
        #endif
        noType += noType;
        return variable;
      }
      int main(void) {
        int a[5];
        char c;
        c = 'a';



        a[konst] = 0;
        int plusgleich = 10;
        plusgleich += 5;
        int funktion;
        foo(a[5], funktion);
        int plusplus = 1;
        plusplus++;
        return plusgleich;
      }
                             """);
    println(testAst(source_ast))
  }

  @Test def test_struct_def_use {
    // TODO Verwendung struct variablen.
    val source_ast = getAST( """
      struct leer;

      struct student {
        int id;
        char *name;
        float percentage;
      } student1, student2, student3;

      struct withInnerStruct {
      struct innerStruct{
      int inner;
      };
      int outer;
      };

      int main(void) {
        struct student st;
        struct student st2 = {10, "Joerg Liebig", 0.99};

        st.id = 5;
        student3.id = 10;
        int i = student1.id;

        student2.name = "Joerg";
        student3.name = "Andi";

        student3.percentage = 90.0;


        return 0;
      }
                             """);
    println(testAst(source_ast))
  }

  @Test def test_opt_def_use {
    val source_ast = getAST( """
      int o = 32;
      int fooZ() {
        #if definedEx(A)
        const int konst = 55;
        int c = 32;
        #else
        int c = 64;
        const int konst = 100;
        #endif
        o = c+o;
        return c;
      }
      int foo(int z) {
        return z;
      }
      int fooVariableArgument(
      #if definedEx(A)
      int
      #else
      float
      #endif
      a) {
        return 0;
      }
      #if definedEx(A)
      int fooA(int a) {
        return a;
      }
      #else
      void fooA(int a) {

      }
      #endif
      int main(void) {
        #if definedEx(A)
        int b = fooA(0);
        int argInt = 2;
        fooVariableArgument(argInt);
        #else
        float argFloat = 2.0;
        fooVariableArgument(argFloat);
        fooA(0);
        #endif

        return 0;
      }
                             """);
    println(testAst(source_ast))
  }

  @Test def test_applets_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\busybox\\TypeChef-BusyboxAnalysis\\busybox-1.18.5\\applets\\applets.pi")
    testFile(file, i)
  }

  @Test def test_cpio_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\busybox\\TypeChef-BusyboxAnalysis\\busybox-1.18.5\\archival\\cpio.pi")
    testFile(file, i)
  }

  @Test def test_tar_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\busybox\\TypeChef-BusyboxAnalysis\\busybox-1.18.5\\archival\\tar.pi")
    testFile(file, i)
  }

  @Test def test_lzop_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\busybox\\TypeChef-BusyboxAnalysis\\busybox-1.18.5\\archival\\lzop.pi")
    testFile(file, i)
  }

  @Test def test_rpm2cpio_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\busybox\\TypeChef-BusyboxAnalysis\\busybox-1.18.5\\archival\\rpm2cpio.pi")
    testFile(file, i)
  }

  @Test def test_ar_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\busybox\\TypeChef-BusyboxAnalysis\\busybox-1.18.5\\archival\\ar.pi")
    testFile(file, i)
  }

  @Test def test_bbunzip_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\busybox\\TypeChef-BusyboxAnalysis\\busybox-1.18.5\\archival\\bbunzip.pi")
    testFile(file, i)
  }

  @Test def test_chpst_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\busybox\\TypeChef-BusyboxAnalysis\\busybox-1.18.5\\runit\\chpst.pi")
    testFile(file, i)
  }

  @Test def test_cdrom_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\flo\\pifiles\\cdrom.pi")
    testFile(file, i)
  }

  @Test def test_mpt2sas_base_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\flo\\pifiles\\cdrom.pi")
    testFile(file, i)
  }

  @Test def test_mpt2sas_config_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\flo\\TypeChef\\ifdeftoif\\cdrom.pi")
    testFile(file, i)
  }

  @Test def test_bbunzuo_pi() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\busybox\\TypeChef-BusyboxAnalysis\\busybox-1.18.5\\archival\\bbunzip.pi")
    testFile(file, i)
  }

  @Test def test_if_conditional() {
    val source_ast = getAST( """
    int main(void) {
    int a = 0;
    int b = -2;
    if (a < 0
    #if definedEx(A)
    && b < 0
    #endif
    #if !definedEx(A)
    && b < 10
    #endif
    #if definedEx(B)
    && b < 20
    #endif
    #if definedEx(C)
                               || b < 12
    #endif
    ) {
      int i = 1;
    #if definedEx(A)
      i = 5;
    #endif
    #if definedEx(C)
      i = 10;
    #endif
    i = i*i;
    }}""")
    println(testAst(source_ast))
  }

  @Test def test_if_choice() {
    val source_ast = getAST( """
    int main(void) {
    int a = 0;
    int b = -2;
    #if definedEx(B)
    if (
    #if definedEx(A)
    b < 0
    #else
    b > 0
    #endif
    ) {
      int i = 1;
      #if definedEx(A)
      i = 2 + i;
      #endif
      #if definedEx(C)
      i = i*i;
      #endif
    }
    #endif
    }""")
    println(testAst(source_ast))
  }

  @Test def enum_test() {
    val source_ast = getAST(
      """
        enum  {
          PSSCAN_PID = (1 << 0),
          PSSCAN_PPID = (1 << 1),
          PSSCAN_PGID = (1 << 2),
          PSSCAN_SID = (1 << 3),
          PSSCAN_UIDGID = (1 << 4),
          PSSCAN_COMM = (1 << 5),
          PSSCAN_ARGV0 = (1 << 7),
          PSSCAN_EXE = (1 << 8),
          PSSCAN_STATE = (1 << 9),
          PSSCAN_VSZ = (1 << 10),
          PSSCAN_RSS = (1 << 11),
          PSSCAN_STIME = (1 << 12),
          PSSCAN_UTIME = (1 << 13),
          PSSCAN_TTY = (1 << 14),
          PSSCAN_SMAPS = ((1 << 15)
          #if definedEx(CONFIG_FEATURE_TOPMEM)
          * 1
          #endif

          #if !definedEx(CONFIG_FEATURE_TOPMEM)
          * 0
          #endif
          ),
          PSSCAN_ARGVN = ((1 << 16)
          #if definedEx(CONFIG_KILLALL)
          * (1
          #if (definedEx(CONFIG_KILLALL) && definedEx(CONFIG_PGREP))
        || 1
          #endif

          #if (definedEx(CONFIG_KILLALL) && !definedEx(CONFIG_PGREP) && (!definedEx(CONFIG_KILLALL) || !definedEx(CONFIG_PGREP)))
        || 0
          #endif

          #if (definedEx(CONFIG_KILLALL) && definedEx(CONFIG_PKILL))
        || 1
          #endif

          #if (definedEx(CONFIG_KILLALL) && !definedEx(CONFIG_PKILL) && (!definedEx(CONFIG_KILLALL) || !definedEx(CONFIG_PKILL)))
        || 0
          #endif

          #if (definedEx(CONFIG_KILLALL) && definedEx(CONFIG_PIDOF))
        || 1
          #endif

          #if (definedEx(CONFIG_KILLALL) && !definedEx(CONFIG_PIDOF) && (!definedEx(CONFIG_KILLALL) || !definedEx(CONFIG_PIDOF)))
        || 0
          #endif

          #if (definedEx(CONFIG_KILLALL) && definedEx(CONFIG_SESTATUS))
        || 1
          #endif

          #if (definedEx(CONFIG_KILLALL) && !definedEx(CONFIG_SESTATUS) && (!definedEx(CONFIG_KILLALL) || !definedEx(CONFIG_SESTATUS)))
        || 0
          #endif
          )
          #endif

          #if !definedEx(CONFIG_KILLALL)
          * (0
          #if (!definedEx(CONFIG_KILLALL) && definedEx(CONFIG_PGREP))
        || 1
          #endif

          #if (!definedEx(CONFIG_KILLALL) && !definedEx(CONFIG_PGREP) && (definedEx(CONFIG_KILLALL) || !definedEx(CONFIG_PGREP)))
        || 0
          #endif

          #if (!definedEx(CONFIG_KILLALL) && definedEx(CONFIG_PKILL))
        || 1
          #endif

          #if (!definedEx(CONFIG_KILLALL) && !definedEx(CONFIG_PKILL) && (definedEx(CONFIG_KILLALL) || !definedEx(CONFIG_PKILL)))
        || 0
          #endif

          #if (!definedEx(CONFIG_KILLALL) && definedEx(CONFIG_PIDOF))
        || 1
          #endif

          #if (!definedEx(CONFIG_KILLALL) && !definedEx(CONFIG_PIDOF) && (definedEx(CONFIG_KILLALL) || !definedEx(CONFIG_PIDOF)))
        || 0
          #endif

          #if (!definedEx(CONFIG_KILLALL) && definedEx(CONFIG_SESTATUS))
        || 1
          #endif

          #if (!definedEx(CONFIG_KILLALL) && !definedEx(CONFIG_SESTATUS) && (definedEx(CONFIG_KILLALL) || !definedEx(CONFIG_SESTATUS)))
        || 0
          #endif
          )
          #endif
          ),
          PSSCAN_CONTEXT = ((1 << 17)
          #if (definedEx(CONFIG_FEATURE_FIND_CONTEXT) || definedEx(CONFIG_SELINUX))
          * 1
          #endif

          #if (!definedEx(CONFIG_FEATURE_FIND_CONTEXT) && !definedEx(CONFIG_SELINUX))
          * 0
          #endif
          ),
          PSSCAN_START_TIME = (1 << 18),
          PSSCAN_CPU = ((1 << 19)
          #if definedEx(CONFIG_FEATURE_TOP_SMP_PROCESS)
          * 1
          #endif

          #if !definedEx(CONFIG_FEATURE_TOP_SMP_PROCESS)
          * 0
          #endif
          ),
          PSSCAN_NICE = ((1 << 20)
          #if definedEx(CONFIG_FEATURE_PS_ADDITIONAL_COLUMNS)
          * 1
          #endif

          #if !definedEx(CONFIG_FEATURE_PS_ADDITIONAL_COLUMNS)
          * 0
          #endif
          ),
          PSSCAN_RUIDGID = ((1 << 21)
          #if definedEx(CONFIG_FEATURE_PS_ADDITIONAL_COLUMNS)
          * 1
          #endif

          #if !definedEx(CONFIG_FEATURE_PS_ADDITIONAL_COLUMNS)
          * 0
          #endif
          ),
          PSSCAN_TASKS = ((1 << 22)
          #if definedEx(CONFIG_FEATURE_SHOW_THREADS)
          * 1
          #endif

          #if !definedEx(CONFIG_FEATURE_SHOW_THREADS)
          * 0
          #endif
          ),
          PSSCAN_STAT = (PSSCAN_PPID | PSSCAN_PGID | PSSCAN_SID | PSSCAN_COMM | PSSCAN_STATE | PSSCAN_VSZ | PSSCAN_RSS | PSSCAN_STIME | PSSCAN_UTIME | PSSCAN_START_TIME | PSSCAN_TTY | PSSCAN_NICE | PSSCAN_CPU)
        } ;
      """)
    println(testAst(source_ast))
  }

  @Test def struct_test() {
    val i = new IfdefToIf
    val file = new File("C:\\users\\flo\\dropbox\\hiwi\\flo\\random\\struct.pi")
    testFile(file, i)
  }

  private def writeToTextFile(name: String, content: String) {
    val fw = new FileWriter(name)
    fw.write(content)
    fw.close()
  }

  @Test def function_test() {
    val source_ast = getAST( """
    void open_transformer(int fd,
    #if definedEx(CONFIG_DESKTOP)
    long long
    #endif
    #if !definedEx(CONFIG_DESKTOP)

    #endif
     int (*transformer)(int src_fd, int dst_fd)) ;
                             """)
    println(testAst(source_ast))
  }

  private def analyseDir(dirToAnalyse: File): List[Tuple2[TranslationUnit, String]] = {
    // retrieve all pi from dir first
    if (dirToAnalyse.isDirectory) {
      val piFiles = dirToAnalyse.listFiles(new FilenameFilter {
        def accept(dir: File, file: String): Boolean = file.endsWith(".pi")
      })
      val dirs = dirToAnalyse.listFiles(new FilenameFilter {
        def accept(dir: File, file: String) = dir.isDirectory
      })
      piFiles.toList.map(x => {
        val fis = new FileInputStream(x)
        val ast = parseFile(fis, x.getName, x.getParent)
        fis.close()
        (ast, x.getName)
      }) ++ dirs.flatMap(x => analyseDir(x))
    } else {
      List()
    }
  }

  private def transformDir(dirToAnalyse: File, printAst: Boolean = false) {
    // retrieve all pi from dir first
    if (dirToAnalyse.isDirectory) {
      val piFiles = dirToAnalyse.listFiles(new FilenameFilter {
        def accept(dir: File, file: String): Boolean = file.endsWith(".pi")
      })
      val dirs = dirToAnalyse.listFiles(new FilenameFilter {
        def accept(dir: File, file: String) = dir.isDirectory
      })
      for (piFile <- piFiles) {
        runIfdefToIfOnPi(piFile, printAst)
      }
      for (dir <- dirs) {
        transformDir(dir)
      }
    }
  }

  private def runIfdefToIfOnPi(fileToAnalyse: File, printAst: Boolean = false) {
    println("++Analyse: " + fileToAnalyse.getName + "++")
    val fis = new FileInputStream(fileToAnalyse)
    val ast = parseFile(fis, fileToAnalyse.getName, fileToAnalyse.getParent)
    fis.close()
    if (printAst) {
      println("Ast:\n" + ast)
    }
    val i = new IfdefToIf
    val startTypeChecking = System.currentTimeMillis()
    typecheckTranslationUnit(ast)
    val endTypeChecking = System.currentTimeMillis()
    println((endTypeChecking - startTypeChecking) / 1000.0 + "s used for TypeChecking.")

    val defuse = getDeclUseMap()

    val startTransformation = System.currentTimeMillis()
    val newAst = i.transformAst(ast, createASTEnv(ast), defuse)
    val endTransformation = System.currentTimeMillis()
    println((endTransformation - startTransformation) / 1000.0 + "s used for transformation.")

    val startPrinting = System.currentTimeMillis()
    writeToFile(getFileNameWithoutExtension(fileToAnalyse) ++ "_tmp.txt", PrettyPrinter.print(newAst))
    val endPrinting = System.currentTimeMillis()
    println((endPrinting - startPrinting) / 1000.0 + "s used for writing the file.\n\n")
  }

  @Test def funct_test() {
    val source_ast = getAST(
      """
        #if definedEx(CONFIG_UNCOMPRESS)
        static
        #if (definedEx(CONFIG_DESKTOP) && definedEx(CONFIG_UNCOMPRESS))
        long
        #endif

        #if (definedEx(CONFIG_DESKTOP) && definedEx(CONFIG_UNCOMPRESS))
        long
        #endif
         int unpack_uncompress(int info)  {


          #if (definedEx(CONFIG_DESKTOP) && definedEx(CONFIG_UNCOMPRESS))
          long
          #endif

          #if (definedEx(CONFIG_DESKTOP) && definedEx(CONFIG_UNCOMPRESS))
          long
          #endif
           int status =  (- 1);
          if (3 < 5) {
            status = 0;
          }
          else {
            (status = 0);
          }
          return status;
        }
        #endif
      """)
    println(testAst(source_ast))
  }

  @Test def lift_opt_test() {
    val source_ast = getAST( """
    void main {
    int i = 0;
    int j = 0;
    j = i +
    #if definedEx(A)
    2
    #endif
    #if !definedEx(A)
    3
    #endif
    ;}
                             """)

    println(source_ast)
    val env = createASTEnv(source_ast)
    val i = new IfdefToIf
    val newAst = i.liftOpts(source_ast, env)
    println("Single lifted:\n" + PrettyPrinter.print(newAst))
    val newNewAst = i.liftOpts(newAst, createASTEnv(newAst))
    println("\n\nDouble lifted:\n" + PrettyPrinter.print(newNewAst))
  }

  @Test def lift_opt2_test() {
    val source_ast = getAST( """
    void main {
    int i = 0;
    int j = 0;
    j = i +
    #if definedEx(A)
    2
    #endif
    #if definedEx(B) && definedEx(A)
    * 32
    #endif
    #if !definedEx(A)
    3
    #endif
    ;}
                             """)

    println(source_ast)
    val env = createASTEnv(source_ast)
    val i = new IfdefToIf
    val newAst = i.liftOpts(source_ast, env)
    println("Single lifted:\n" + PrettyPrinter.print(newAst))
    val newNewAst = i.liftOpts(newAst, createASTEnv(newAst))
    println("\n\nDouble lifted:\n" + PrettyPrinter.print(newNewAst))
  }

  @Test def option_file_test() {
    val source_ast = getAST( """
      #include "opt.h"
      extern struct sOpt opt;
      extern void initOpt();""")
    println("Source: " + source_ast)
    println("+++Pretty printed+++\n" + PrettyPrinter.print(source_ast))
  }

  @Test def pretty_printer_test() {
    val declaration = Declaration(List(Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("printf_main"), List(Opt(True, DeclParameterDeclList(List(Opt(True, ParameterDeclarationD(List(Opt(True, IntSpecifier())), AtomicNamedDeclarator(List(), Id("argc"), List()))), Opt(True, ParameterDeclarationD(List(Opt(True, CharSpecifier())), AtomicNamedDeclarator(List(Opt(True, Pointer(List())), Opt(True, Pointer(List()))), Id("argv"), List())))))))), List(Opt(fx, GnuAttributeSpecifier(List(Opt(True, AttributeSequence(List(Opt(fx, AtomicAttribute("visibility")), Opt(fx, CompoundAttribute(List(Opt(True, AttributeSequence(List(Opt(fx, AtomicAttribute("default"))))))))))))))), None))))
    println("+++Pretty printed+++\n" + PrettyPrinter.print(declaration))
    val declaration2 = Declaration(List(Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("printf_main"), List(Opt(True, DeclParameterDeclList(List(Opt(True, ParameterDeclarationD(List(Opt(True, IntSpecifier())), AtomicNamedDeclarator(List(), Id("argc"), List()))), Opt(True, ParameterDeclarationD(List(Opt(True, CharSpecifier())), AtomicNamedDeclarator(List(Opt(True, Pointer(List())), Opt(True, Pointer(List()))), Id("argv"), List())))))))), List(), None))))
    println("+++Pretty printed+++\n" + PrettyPrinter.print(declaration2))
  }

  @Test def flo_ast_test() {
    val source_ast = getAstFromPi(new File("C:\\users\\flo\\dropbox\\hiwi\\flo\\test\\test.pi"))
    println(source_ast)
  }

  @Test def busy_box_test() {
    transformDir(new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/"))
  }

  @Test def random_test() {
    val source_ast = getAST( """
    #if definedEx(CONFIG_BUILD_LIBBUSYBOX)
    #if definedEx(CONFIG_FEATURE_SHARED_BUSYBOX)
    int lbb_main(char **argv) __attribute__(( visibility("default") ));
    #endif
    #if !definedEx(CONFIG_FEATURE_SHARED_BUSYBOX)
    int lbb_main(char **argv);
    #endif
    #endif
                             """)

    val source_ast2 = getAST( """
    #if definedEx(CONFIG_BUILD_LIBBUSYBOX)
    #if definedEx(CONFIG_FEATURE_SHARED_BUSYBOX)
    int lbb_main(char **argv) __attribute__(( visibility("default") ));
    #endif
    #if !definedEx(CONFIG_FEATURE_SHARED_BUSYBOX)
    double lbb_main(char **argv);
    #endif
    #endif
                              """)

    val source_ast3 = getAST( """
    void foo() {
      write_ar_archive(archive_handle);
      if (options.config_feature_ar_Create) {
        if ((opt & (1 << 6))) {
          return write_ar_archive(archive_handle);
        }
      } else if (opt) {
        if ((opt)) {
          return write_ar_archive();
        }
      } else if (opt) {
        if (opt) {
          return write_ar_archive();
        }
      } else {
        return write_ar_archive();
      }
    }
    void main() {
    int i = 0, x = 10;
    while (i < x) {
    i++;
    #if definedEx(x64)
    i++;
    #endif
    }
    int j;
    for (j = 0; j < 10; j++) {
    j = j
    #if definedEx(A)
    * 2
    #endif
    ;
    #if definedEx(x64)
    j++;
    #endif
    }
        while (j < 20) {
          j++;
        }
    }
                              """)
    val i = new IfdefToIf
    val i1 = Id("i")
    val i2 = Id("i")
    val i3 = Id("i")
    val i4 = Id("i")

    println("Source: " + source_ast)
    println("+++Pretty printed+++\n" + PrettyPrinter.print(source_ast))

    println("Source2: " + source_ast2)
    println("+++Pretty printed+++\n" + PrettyPrinter.print(source_ast2))

    println("Source3: " + source_ast3)
    println("+++Pretty printed+++\n" + PrettyPrinter.print(source_ast3))

    println("Eq: " + i1.eq(i2))
    println("Equals: " + i1.equals(i2))

    var lb = ListBuffer(i1, i2, i4)
    println("LB contains: " + i.eqContains(lb, i4))

    val m: util.IdentityHashMap[Product, Product] = new IdentityHashMap()
    m.put(i1, i2)
    println("Map contains: " + m.containsKey(i4))

    println(source_ast3)
    println(PrettyPrinter.print(source_ast3))

    val r = breadthfirst(query {
      case k => println(k)
    })

    val decl = Opt(True, Declaration(List(Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("printf_main"), List()), List(), None)))))
    r(decl).get
    val forTrue = Opt(True, ForStatement(Some(AssignExpr(Id("j"), "=", Constant("0"))), Some(NAryExpr(Id("j"), List(Opt(True, NArySubExpr("<", Constant("10")))))), Some(PostfixExpr(Id("j"), SimplePostfixSuffix("++"))), One(CompoundStatement(List(Opt(True, ExprStatement(AssignExpr(Id("j"), "=", NAryExpr(Id("j"), List(Opt(fx, NArySubExpr("*", Constant("2")))))))), Opt(fx, ExprStatement(PostfixExpr(Id("j"), SimplePostfixSuffix("++")))))))))
    val forFalse = Opt(True, ForStatement(Some(AssignExpr(Id("j"), "=", Constant("0"))), Some(NAryExpr(Id("j"), List(Opt(True, NArySubExpr("<", Constant("10")))))), Some(PostfixExpr(Id("j"), SimplePostfixSuffix("++"))), One(CompoundStatement(List(Opt(True, ExprStatement(AssignExpr(Id("j"), "=", NAryExpr(Id("j"), List(Opt(True, NArySubExpr("*", Constant("2")))))))), Opt(True, ExprStatement(PostfixExpr(Id("j"), SimplePostfixSuffix("++")))))))))
    val forFalseButDeeper = Opt(True, ForStatement(Some(AssignExpr(Id("j"), "=", Constant("0"))), Some(NAryExpr(Id("j"), List(Opt(True, NArySubExpr("<", Constant("10")))))), Some(PostfixExpr(Id("j"), SimplePostfixSuffix("++"))), One(CompoundStatement(List(Opt(True, ExprStatement(AssignExpr(Id("j"), "=", NAryExpr(Id("j"), List(Opt(fx, NArySubExpr("*", Constant("2")))))))), Opt(True, ExprStatement(PostfixExpr(Id("j"), SimplePostfixSuffix("++")))))))))
    println("ForStatement variability: " + i.nextLevelContainsVariability(forTrue.entry))
    println("ForStatement variability: " + i.nextLevelContainsVariability(forFalse.entry))
    println("ForStatement variability: " + i.nextLevelContainsVariability(forFalseButDeeper.entry))

    println("\nNext Level: " + i.getNextOptList(forTrue.entry))
    println("\nNext Level: " + i.getNextOptList(forFalse.entry))
    println("\nNext Level: " + i.getNextOptList(forFalseButDeeper.entry))
  }
}