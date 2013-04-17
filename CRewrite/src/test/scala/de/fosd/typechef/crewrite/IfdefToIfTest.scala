package de.fosd.typechef.crewrite

import org.junit.Test
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.featureexpr.sat._
import de.fosd.typechef.parser.c._
import de.fosd.typechef.crewrite.CASTEnv._
import de.fosd.typechef.typesystem._
import java.io._
import java.util
import util.IdentityHashMap
import scala.Some
import scala.Tuple2
import io.Source
import de.fosd.typechef.conditional.{Choice, One, Opt}
import de.fosd.typechef.featureexpr.{FeatureExprParser, FeatureExprFactory}
import de.fosd.typechef.lexer.FeatureExprLib

class IfdefToIfTest extends ConditionalNavigation with ASTNavigation with CDeclUse with CTypeSystem with TestHelper {
  val makeAnalysis = true
  val writeFilesIntoIfdeftoifFolder = true
  val checkForExistingFiles = true
  val typeCheckResult = true

  val filesToAnalysePerRun = 15
  var filesTransformed = 0

  val i = new IfdefToIf
  val path = new File("..").getCanonicalPath() ++ "/ifdeftoif/"
  val singleFilePath = new File("..").getCanonicalPath() ++ "/single_files/"

  /* val tb = java.lang.management.ManagementFactory.getThreadMXBean
val time = tb.getCurrentThreadCpuTime // Type long; beware in nanoseconds */

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

  def getCSVHeader(): String = {
    "File name,Number of AST nodes before,Number of AST nodes after,AST node difference,Features,Declarations,Optional declarations,Declarations duplicated,Functions,Optional functions,Functions duplicated,If statements before,If statements after,Renamings,Renaming usages,Parsing,Transformation,PrettyPrinting\n"
  }

  def getTypeSystem(ast: AST): CTypeSystemFrontend = {
    new CTypeSystemFrontend(ast.asInstanceOf[TranslationUnit])
  }

  def writeToFile(fileName: String, data: String) =
    using(new FileWriter(fileName)) {
      fileWriter => fileWriter.write(data)
    }

  def appendToFile(fileName: String, textData: String) = {
    using(new FileWriter(fileName, true)) {
      fileWriter => using(new PrintWriter(fileWriter)) {
        printWriter => printWriter.print(textData)
      }
    }
  }

  def getFileNameWithoutExtension(file: File): String = {
    file.getName().replaceFirst("[.][^.]+$", "")
  }

  def getFileNameWithoutExtension(strg: String): String = {
    strg.replaceFirst("[.][^.]+$", "")
  }

  def testFile(file: File, writeAst: Boolean = false) {
    new File(singleFilePath).mkdirs()
    val fileNameWithoutExtension = getFileNameWithoutExtension(file)
    val analyseString = "++Analyse: " + file.getName + "++"
    print(analyseString)
    for (i <- (analyseString.size / 4) until 15) {
      print("\t")
    }
    val startParsingAndTypeChecking = System.currentTimeMillis()
    val source_ast = i.fixTypeChefsFeatureExpressions(getAstFromPi(file))
    //val env = createASTEnv(source_ast)
    typecheckTranslationUnit(source_ast)
    val defUseMap = getDeclUseMap
    val timeToParseAndTypeCheck = System.currentTimeMillis() - startParsingAndTypeChecking
    print("--Parsed--")

    val startTransformation = System.currentTimeMillis()
    val new_ast = i.transformAst(source_ast, defUseMap)
    val timeToTransform = System.currentTimeMillis() - startTransformation
    print("\t--Transformed--")

    val startPrettyPrinting = System.currentTimeMillis()
    PrettyPrinter.printF(new_ast._1, singleFilePath ++ fileNameWithoutExtension ++ ".ifdeftoif")
    val timeToPrettyPrint = System.currentTimeMillis() - startPrettyPrinting
    print("\t--Printed--\n")
    if (writeAst) {
      writeToTextFile(fileNameWithoutExtension ++ "_ast.txt", source_ast.toString())
    }

    if (makeAnalysis) {
      //if (!(new File(singleFilePath ++ fileNameWithoutExtension ++ ".src")).exists) {
      PrettyPrinter.printF(source_ast, singleFilePath ++ fileNameWithoutExtension ++ ".src")
      //}
      /*val linesOfCodeBefore = Source.fromFile(new File(singleFilePath ++ fileNameWithoutExtension ++ ".src")).getLines().size
      val linesOfCodeAfter = Source.fromFile(new File(singleFilePath ++ fileNameWithoutExtension ++ ".ifdeftoif")).getLines().size
      val codeDifference = computeDifference(linesOfCodeBefore, linesOfCodeAfter)
      val csvBeginning = file.getName() + "," + linesOfCodeBefore + "," + linesOfCodeAfter + "," + codeDifference + ","*/

      val astElementsBefore = i.countNumberOfASTElements(source_ast)
      val astElementsAfter = i.countNumberOfASTElements(new_ast._1)
      val astElementDifference = i.computeDifference(astElementsBefore, astElementsAfter)
      val csvBeginning = file.getName() + "," + astElementsBefore + "," + astElementsAfter + "," + astElementDifference + ","

      val csvEnding = "," + timeToParseAndTypeCheck + "," + timeToTransform + "," + timeToPrettyPrint
      writeToTextFile(singleFilePath ++ fileNameWithoutExtension ++ ".csv", getCSVHeader() + csvBeginning + new_ast._2 + csvEnding)
    }
  }

  def testAst(source_ast: TranslationUnit): String = {
    typecheckTranslationUnit(source_ast)
    val defUseMap = getDeclUseMap

    val optionsAst = i.getOptionFile(source_ast)
    ("+++New Code+++\n" + PrettyPrinter.print(i.transformAst(source_ast, defUseMap)._1))
  }

  def testFolder(path: String) {
    val folder = new File(path)
    val asts = analyseDir(folder)

    asts.foreach(x => writeToTextFile(x._2 ++ "_tmp", PrettyPrinter.print(i.transformAst(x._1, getDefUse(x._1))._1)))

    /*val quad = asts.map(x => (x._1, createASTEnv(x._1), getDefUse(x._1), x._2))
    val newAsts = i.transformAsts(quad)
    newAsts.foreach(x => writeToTextFile(PrettyPrinter.print(x._1), x._2 ++ "_tmp"))*/
  }

  private def getAstFromPi(fileToAnalyse: File): TranslationUnit = {
    val fis = new FileInputStream(fileToAnalyse)
    val ast = parseFile(fis, fileToAnalyse.getName, fileToAnalyse.getParent)
    fis.close()
    ast
  }

  private def compareTypeChecking(file: File): Tuple2[Long, Long] = {
    val source_ast = getAstFromPi(file)
    val result_ast = i.transformAst(source_ast, getDefUse(source_ast))._1
    val ts_source = getTypeSystem(source_ast)
    val ts_result = getTypeSystem(result_ast)

    val typeCheckSourceStart = System.currentTimeMillis()
    ts_source.checkASTSilent
    val typeCheckSourceDuration = System.currentTimeMillis() - typeCheckSourceStart
    print("++" + file.getName() + "++\n" + "TypeCheck Source: \t\t" + typeCheckSourceDuration + "\t\t\t\t")

    val typeCheckResultStart = System.currentTimeMillis()
    ts_result.checkASTSilent
    val typeCheckResultDuration = System.currentTimeMillis() - typeCheckResultStart
    print("TypeCheck Result: \t\t" + typeCheckResultDuration + "\n\n")

    (typeCheckSourceDuration, typeCheckResultDuration)
  }

  private def getDefUse(ast: TranslationUnit): IdentityHashMap[Id, List[Id]] = {
    typecheckTranslationUnit(ast)
    getDeclUseMap
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
    println(ast)
    println(testAst(ast))
  }

  @Test def test_function2() {
    val ast2 = getAST( """
      #if definedEx(A)
      static void
      #if definedEx(B)
      long
      #else
      int
      #endif
      mainz(int one
      #if definedEx(C)
      , int two
      #endif
      ) {
        return 0;
      }
      #endif
                       """)
    println(ast2)
    println(testAst(ast2) + "\n\n")

    val ast3 = getAST( """
      static void
      #if definedEx(B)
      long
      #endif
      #if !definedEx(B)
      int
      #endif
      mainz(int one
      #if definedEx(C)
      , int two
      #endif
      ) {
        return 0;
      }
                       """)
    println(ast3)
    println(testAst(ast3) + "\n\n")

    val ast = getAST( """
      #if definedEx(G) || definedEx(B)
      #if !definedEx(G) || !definedEx(B)
      #endif
      static void
      #if definedEx(C) && (definedEx(G) || definedEx(B)) && (!definedEx(G) || !definedEx(B))
      vfork(int tar_fd)
      #endif
      #if (!definedEx(C) || (!definedEx(G) && !definedEx(B)) || (definedEx(G) && definedEx(B)))
      vfork(int tar_fd, int gzip)
      #endif
      {
      int i;
      }
      #endif
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

  @Test def test_tri() {
    val ast = getAST( """
      main(void) {
        int i = 5
        #if definedEx(Add)
        +
        #else
        -
        #endif
        2;
      }
                      """)
    println(ast)
    println(PrettyPrinter.print(ast))
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
    println(source_ast)
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
    println(source_ast)
    println(testAst(source_ast))

  }

  @Test def if_test3 {
    val source_ast = getAST( """
      void foo_04(int a) {
      #if definedEx(A)
      int
      #else
      short
      #endif
      i = 0;
      if (i < 0) {
        i = 32;
      }
      #if definedEx(A)
      else if (i < 1) {
        i = 64;
      }
      #endif
      else {
        i = 128;
      }
      }
                             """)
    val target_ast = getAST( """
      void foo_04(int a) {
        int _1_i = 0;
        short _2_i = 0;
        if (i < 0) {
          i = 32;
        } else if (options.a) {
          i = 64;
        }
      }
                             """)
    println(source_ast)
    println(target_ast)
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

  @Test def test_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/applets/applets.pi")
    testFile(file)
  }

  @Test def test_applets_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/applets/applets.pi")
    testFile(file)
  }

  @Test def test_stat_pi() {
    val file = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/coreutils/stat.pi")
    testFile(file)
  }

  @Test def test_alex_pi() {
    val file = new File("C:\\Users\\Flo\\Dropbox\\HiWi\\Flo\\Alex\\r8a66597-udc.pi")
    val macroFilter = new util.ArrayList[String]()
    macroFilter.add("x:CONFIG_")
    val fm = new FeatureExprParser(FeatureExprLib.l()).parseFile(new File(":\\Users\\Flo\\Dropbox\\HiWi\\Flo\\Alex\\approx.fm"))
    //testFile(file)

    de.fosd.typechef.featureexpr.FeatureExprFactory.setDefault(de.fosd.typechef.featureexpr.FeatureExprFactory.bdd)

    val parse = System.currentTimeMillis()
    val ast = getAstFromPi(file)
    println("Parsing took: " + ((System.currentTimeMillis() - parse) / 1000) + "s")

    val print = System.currentTimeMillis()
    PrettyPrinter.printF(ast, "C:/alex.src")
    println("Printing took: " + ((System.currentTimeMillis() - print) / 1000) + "s")
  }

  @Test def test_cpio_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/archival/cpio.pi")
    testFile(file)

    /*val ast = getAstFromPi(file)
    val defuse = getDeclUseMap()
    i.ifdeftoif(ast, defuse)*/
  }

  @Test def test_update_passwd_pi() {
    val file = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/libbb/update_passwd.pi")
    testFile(file)
  }

  @Test def test_tr_pi() {
    val file = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/coreutils/tr.pi")
    testFile(file)
  }

  @Test def test_fold_pi() {
    val file = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/coreutils/fold.pi")
    testFile(file)
  }

  @Test def test_lzop_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/archival/lzop.pi")
    testFile(file)
  }

  @Test def test_rpm2cpio_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/archival/rpm2cpio.pi")
    testFile(file)
  }

  @Test def test_filter_accept_all_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/archival/libarchive/filter_accept_all.pi")
    testFile(file)
  }

  @Test def test_decompress_unzip_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/archival/libarchive/decompress_unzip.pi")
    testFile(file)
  }

  @Test def test_ar_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/archival/ar.pi")
    testFile(file)
  }

  @Test def test_tar_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/archival/tar.pi")
    testFile(file)
  }

  @Test def test_bbunzip_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/archival/bbunzip.pi")
    testFile(file)
  }

  @Test def test_chpst_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/runit/chpst.pi")
    testFile(file)
  }

  @Test def test_diff_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/editors/diff.pi")
    testFile(file)
  }

  @Test def test_ls_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/coreutils/ls.pi")
    testFile(file)
  }

  @Test def test_sed_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/editors/sed.pi")
    testFile(file)
  }

  @Test def test_linux_cciss_pi() {
    val file = new File("D:/drivers/block/cciss.pi")
    testFile(file)
  }

  @Test def test_linux_battery_pi() {
    val file = new File("D:/drivers/acpi/battery.pi")
    testFile(file)
  }

  @Test def test_linux_ac_pi() {
    val file = new File("D:/drivers/acpi/ac.pi")
    testFile(file)
  }

  @Test def test_lineedit_pi() {
    val file = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/libbb/lineedit.pi")
    testFile(file)
  }

  @Test def test_cdrom_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/flo/pifiles/cdrom.pi")
    //testFile(file)

    de.fosd.typechef.featureexpr.FeatureExprFactory.setDefault(de.fosd.typechef.featureexpr.FeatureExprFactory.bdd)

    val parse = System.currentTimeMillis()
    val ast = getAstFromPi(file)
    println("Parsing took: " + ((System.currentTimeMillis() - parse) / 1000) + "s")

    val print = System.currentTimeMillis()
    PrettyPrinter.printF(ast, "C:/cdrom.src")
    println("Printing took: " + ((System.currentTimeMillis() - print) / 1000) + "s")
  }

  @Test def test_test_pi() {
    val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/test.pi")
    testFile(file, true)
  }

  @Test def test_mpt2sas_base_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/flo/pifiles/cdrom.pi")
    testFile(file)
  }

  @Test def test_mpt2sas_config_pi() {
    val file = new File("C:/users/flo/dropbox/hiwi/flo/TypeChef/ifdeftoif/cdrom.pi")
    testFile(file)
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
    println(source_ast)
    println(testAst(source_ast))
    println("DECLS: " + i.countNumberOfElements[Declaration](source_ast))
    println("FUNCTIONDEFS: " + i.countNumberOfElements[FunctionDef](source_ast))
    println("Declarations: " + i.countNumberOfDeclarations(source_ast))
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

  @Test def test_if_choice2() {
    val source_ast = getAST( """
		union {
      int a;
		} magic;
    enum {
    BZIP2_MAGIC = 0
    };

    int main(void) {
    int a = 0;
    int b = -2;
    if (
    #if definedEx(A)
    1
    #endif
    #if !definedEx(A)
    0
    #endif
    && magic.a == BZIP2_MAGIC) {
      int i = 1;
    }
    }""")
    println(testAst(source_ast))
    println("Declarations: " + i.countNumberOfDeclarations(source_ast))
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

    val source_ast2 = getAST(
      """
        enum  {
          LSA_SIZEOF_SA = sizeof(union  {
            int  sa;
            int  sin;
            #if definedEx(CONFIG_FEATURE_IPV6)
            int  sin6;
            #endif
          } )
        } ;
      """)
    println(testAst(source_ast2))
    println(source_ast2)
  }

  @Test def struct_test() {
    val file = new File("C:/users/flo/dropbox/hiwi/flo/random/struct.pi")
    testFile(file)
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

  private def countFiles(dirToAnalyse: File, fileExtension: String = ".pi"): Int = {
    def countHelp(file: File): Int = {
      if (file.isDirectory) {
        val piFiles = file.listFiles(new FilenameFilter {
          def accept(dir: File, fileName: String): Boolean = fileName.endsWith(fileExtension)
        })
        val dirs = file.listFiles(new FilenameFilter {
          def accept(dir: File, fileName: String) = dir.isDirectory
        })
        var numberOfFiles = piFiles.size
        for (dir <- dirs) {
          numberOfFiles = numberOfFiles + countHelp(dir)
        }
        numberOfFiles
      } else {
        0
      }
    }
    countHelp(dirToAnalyse)
  }

  private def transformDir(dirToAnalyse: File) {
    def transformPiFiles(dirToAnalyse: File) {
      if (filesTransformed < filesToAnalysePerRun) {
        // retrieve all pi from dir first
        if (dirToAnalyse.isDirectory) {
          val piFiles = dirToAnalyse.listFiles(new FilenameFilter {
            def accept(dir: File, file: String): Boolean = file.endsWith(".pi")
          })
          val dirs = dirToAnalyse.listFiles(new FilenameFilter {
            def accept(dir: File, file: String) = dir.isDirectory
          })
          for (piFile <- piFiles) {
            runIfdefToIfOnPi(piFile)
          }
          for (dir <- dirs) {
            transformPiFiles(dir)
          }
        }
      }
    }
    new File(path).mkdirs()
    if (!checkForExistingFiles || !(new File(path ++ "results.csv").exists)) {
      writeToFile(path ++ "results.csv", getCSVHeader())
    }
    transformPiFiles(dirToAnalyse)
  }

  private def runIfdefToIfOnPi(file: File) {
    if (filesTransformed < filesToAnalysePerRun) {
      val filePathWithoutExtension = getFileNameWithoutExtension(file.getPath())
      val fileNameWithoutExtension = getFileNameWithoutExtension(file)
      val transformedFileExists = (writeFilesIntoIfdeftoifFolder && new File(path ++ fileNameWithoutExtension ++ ".ifdeftoif").exists) || (!writeFilesIntoIfdeftoifFolder && new File(filePathWithoutExtension ++ ".ifdeftoif").exists)
      var fileName = file.getName()

      if (!checkForExistingFiles || !transformedFileExists) {
        /*for (i <- (analyseString.size / 4) until 15) {
          print("\t")
        }*/

        filesTransformed = filesTransformed + 1

        val startParsingAndTypeChecking = System.currentTimeMillis()
        val source_ast = getAstFromPi(file)
        typecheckTranslationUnit(source_ast)
        val defUseMap = getDeclUseMap
        val timeToParseAndTypeCheck = System.currentTimeMillis() - startParsingAndTypeChecking
        //print("--Parsed--")

        val tuple = i.ifdeftoif(source_ast, defUseMap, FeatureExprLib.featureModelFactory.create(new FeatureExprParser(FeatureExprLib.l).parseFile("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox/featureModel")), fileNameWithoutExtension, timeToParseAndTypeCheck)
        tuple._1 match {
          case None =>
            println("!! Transformation of " ++ fileName ++ " unsuccessful because of type errors in transformation result !!")
          case Some(x) =>
            PrettyPrinter.printF(x, fileNameWithoutExtension ++ ".ifdeftoif")
            println("++Transformed: " ++ fileName ++ "++\t\t --in " + tuple._2 ++ " ms--")
        }

        val startTransformation = System.currentTimeMillis()
        val new_ast = i.transformAst(source_ast, defUseMap)
        val timeToTransform = System.currentTimeMillis() - startTransformation
        //print("\t--Transformed--")

        val startPrettyPrinting = System.currentTimeMillis()
        //val transformedCode = PrettyPrinter.print(tempAst._1)
        if (writeFilesIntoIfdeftoifFolder) {
          //writeToTextFile(path ++ fileNameWithoutExtension ++ ".ifdeftoif", transformedCode)
          PrettyPrinter.printF(new_ast._1, path ++ fileNameWithoutExtension ++ ".ifdeftoif")
        } else {
          //writeToTextFile(filePathWithoutExtension ++ ".ifdeftoif", transformedCode)
          PrettyPrinter.printF(new_ast._1, filePathWithoutExtension ++ ".ifdeftoif")
        }
        val timeToPrettyPrint = System.currentTimeMillis() - startPrettyPrinting
        //print("\t--Printed--\n")

        if (makeAnalysis) {
          if (typeCheckResult) {
            val typeCheckStart = System.currentTimeMillis()
            if (!getTypeSystem(new_ast._1).checkASTSilent) {
              return
            }
          }
          if (writeFilesIntoIfdeftoifFolder) {
            if (!((new File(path ++ fileNameWithoutExtension ++ ".src")).exists)) {
              writeToTextFile(path ++ fileNameWithoutExtension ++ ".src", PrettyPrinter.print(source_ast))
            }
            /*val linesOfCodeBefore = Source.fromFile(new File(path ++ fileNameWithoutExtension ++ ".src")).getLines().size
            val linesOfCodeAfter = Source.fromFile(new File(path ++ fileNameWithoutExtension ++ ".ifdeftoif")).getLines().size
            val codeDifference = computeDifference(linesOfCodeBefore, linesOfCodeAfter)
            val csvBeginning = file.getName() + "," + linesOfCodeBefore + "," + linesOfCodeAfter + "," + codeDifference + ","*/
            val astElementsBefore = i.countNumberOfASTElements(source_ast)
            val astElementsAfter = i.countNumberOfASTElements(new_ast._1)
            val astElementDifference = i.computeDifference(astElementsBefore, astElementsAfter)
            val csvBeginning = fileName + "," + astElementsBefore + "," + astElementsAfter + "," + astElementDifference + ","


            val csvEnding = "," + timeToParseAndTypeCheck + "," + timeToTransform + "," + timeToPrettyPrint + "\n"
            appendToFile(path ++ "results.csv", csvBeginning + new_ast._2 + csvEnding)
          } else {
            if (!(new File(filePathWithoutExtension ++ ".src")).exists) {
              writeToTextFile(filePathWithoutExtension ++ ".src", PrettyPrinter.print(source_ast))
            }
            val linesOfCodeBefore = Source.fromFile(new File(filePathWithoutExtension ++ ".src")).getLines().size
            val linesOfCodeAfter = Source.fromFile(new File(filePathWithoutExtension ++ ".ifdeftoif")).getLines().size
            val codeDifference = i.computeDifference(linesOfCodeBefore, linesOfCodeAfter)
            val csvBeginning = file.getName() + "," + linesOfCodeBefore + "," + linesOfCodeAfter + "," + codeDifference + ","
            val csvEnding = "," + timeToParseAndTypeCheck + "," + timeToTransform + "," + timeToPrettyPrint + "\n"
            appendToFile(path ++ "results.csv", csvBeginning + new_ast._2 + csvEnding)
          }
        }
      }
    }
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
    val newAst = i.liftOpts(source_ast)
    println("Single lifted:\n" + PrettyPrinter.print(newAst))
    val newNewAst = i.liftOpts(newAst)
    println("\n\nDouble lifted:\n" + PrettyPrinter.print(newNewAst))
  }

  @Test def feature_test() {
    val oneVariableContradiction = FunctionDef(List(Opt(fa, StaticSpecifier()), Opt(fa.not(), VoidSpecifier())), AtomicNamedDeclarator(List(), Id("main"), List()), List(), CompoundStatement(List()))
    val twoVariableContradiction = FunctionDef(List(Opt(fa, StaticSpecifier()), Opt(fa.not().and(fb.not()), VoidSpecifier()), Opt((fb.and(fa.not()).and(fb.or(fa))), VoidSpecifier())), AtomicNamedDeclarator(List(), Id("main"), List()), List(), CompoundStatement(List()))
    val threeVariableContradiction = FunctionDef(List(Opt(fa, StaticSpecifier()), Opt(fa.not().and(fb.not()), VoidSpecifier()), Opt(fc.and(fa.not()).and(fb).and(fb.or(fa)), IntSpecifier()), Opt(fa.not().and(fb).and(fc.not().or(fa).or(fb.not())).and(fb.or(fa)), DoubleSpecifier())), AtomicNamedDeclarator(List(), Id("main"), List()), List(), CompoundStatement(List()))

    val oneVariableComputation = FunctionDef(List(Opt(fa, StaticSpecifier())), AtomicNamedDeclarator(List(), Id("main"), List()), List(), CompoundStatement(List()))
    val twoVariableComputation = FunctionDef(List(Opt(fb, StaticSpecifier()), Opt(fa.not(), VoidSpecifier())), AtomicNamedDeclarator(List(), Id("main"), List()), List(), CompoundStatement(List()))
    val threeVariableComputation = FunctionDef(List(Opt(fb, StaticSpecifier()), Opt(fc.not(), StaticSpecifier()), Opt(fa.not(), VoidSpecifier())), AtomicNamedDeclarator(List(), Id("main"), List()), List(), CompoundStatement(List()))
    val fiveVariableComputation = FunctionDef(List(Opt(fb, StaticSpecifier()), Opt(fc.not(), StaticSpecifier()), Opt(fa.not(), VoidSpecifier()), Opt(fx.not(), VoidSpecifier()), Opt(fy.not(), VoidSpecifier()), Opt(fc, StaticSpecifier())), AtomicNamedDeclarator(List(), Id("main"), List()), List(), CompoundStatement(List()))

    val mixedVariableContradiction = FunctionDef(List(Opt(fa, StaticSpecifier()), Opt(fa.not().and(fb.not()), VoidSpecifier()), Opt(fc.and(fa.not()).and(fb).and(fb.or(fa)), IntSpecifier()), Opt(fa.not().and(fb).and(fc.not().or(fa).or(fb.not())).and(fb.or(fa)), DoubleSpecifier()), Opt(fx, StaticSpecifier())), AtomicNamedDeclarator(List(), Id("main"), List()), List(), CompoundStatement(List()))


    val oneContradictionResult = i.computeNextRelevantFeatures(oneVariableContradiction)
    val twoContradictionResult = i.computeNextRelevantFeatures(twoVariableContradiction)
    val threeContradictionResult = i.computeNextRelevantFeatures(threeVariableContradiction)

    val oneComputationResult = i.computeNextRelevantFeatures(oneVariableComputation)
    val twoComputationResult = i.computeNextRelevantFeatures(twoVariableComputation)
    val threeComputationResult = i.computeNextRelevantFeatures(threeVariableComputation)

    val mixedComputationResult = i.computeNextRelevantFeatures(mixedVariableContradiction)

    println("Amount of feature expressions: " + oneContradictionResult.size + ", in: " + oneContradictionResult)
    println("Amount of feature expressions: " + twoContradictionResult.size + ", in: " + twoContradictionResult)
    println("Amount of feature expressions: " + threeContradictionResult.size + ", in: " + threeContradictionResult)

    println("Amount of feature expressions: " + oneComputationResult.size + ", in: " + oneComputationResult)
    println("Amount of feature expressions: " + twoComputationResult.size + ", in: " + twoComputationResult)
    println("Amount of feature expressions: " + threeComputationResult.size + ", in: " + threeComputationResult)

    println("Amount of feature expressions: " + mixedComputationResult.size + ", in: " + mixedComputationResult)

    /*val featureList = List(fc.and(fa), fc.and(fa.not().and(fb.not())), fc.and(fa.not().and(fb).and(fb.or(fa))))
    println("C: " + i.debugNextRelevantFeatures(featureList))
    val featureList2 = List(fa, (fa.not().and(fb.not())), fa.not().and(fb).and(fb.or(fa)))
    println(i.debugNextRelevantFeatures(featureList2))
    println((fa.and(fb.not())))*/
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
    val newAst = i.liftOpts(source_ast)
    println("Single lifted:\n" + PrettyPrinter.print(newAst))
    val newNewAst = i.liftOpts(newAst)
    println("\n\nDouble lifted:\n" + PrettyPrinter.print(newNewAst))
  }

  @Test def option_ftest() {
    val source_ast = getAST( """
      #include "opt.h"
      extern struct sOpt opt;
      extern void initOpt();""")
    println("Source: " + source_ast)
    println("+++Pretty printed+++\n" + PrettyPrinter.print(source_ast))
  }

  @Test def flo_ast_test() {
    val source_ast = getAstFromPi(new File("C:/users/flo/dropbox/hiwi/flo/test/test2.pi"))
    println(source_ast)
  }

  @Test def busy_box_test() {
    val busybox = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/")
    transformDir(busybox)
  }

  @Test def directory_test() {
    transformDir(new File("C:/users/flo/dropbox/hiwi/flo/busybox/"))
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

  @Test def test_statements() {
    val source_ast = getAST( """
    void main() {
    int i = 2
    #if definedEx(A)
    +
    #else
    -
    #endif
    2;
    i = 2*i;
    }
                             """)
    println(PrettyPrinter.print(source_ast))
    println(testAst(source_ast))

    val source_ast2 = getAST( """
    void main() {
      int i;
      i = 2
    #if definedEx(A)
      +
    #else
      -
    #endif
      2;
      i = 2*i;
    }
                              """)
    println(testAst(source_ast2))

    val source_ast3 = getAST( """
    void main() {
      int j = 2
      #if definedEx(A)
      +
      #else
      -
      #endif
      2;
      j = 2*j;
      int i = 2
    #if definedEx(B)
      +
    #else
      -
    #endif
      2;
      i = 2*i;
      i = 2 * j;
    }
                              """)
    println(testAst(source_ast3))
  }

  @Test def declaration_test() {
    // val file = new File("C:/users/flo/dropbox/hiwi/flo/pifiles/cdrom.pi")
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/libbb/lineedit.pi")
    println("parsing")
    val parse_time = System.currentTimeMillis()
    val source_ast = getAstFromPi(file)
    println(" took: " + (System.currentTimeMillis() - parse_time) + " ms")
    i.analyseDeclarations(source_ast)
  }

  @Test def feature_explosion() {
    val a = FeatureExprFactory.createDefinedExternal("A")
    val b = FeatureExprFactory.createDefinedExternal("B")
    val c = FeatureExprFactory.createDefinedExternal("C")
    val context = a.and(b)
    val typeChefMistake = a.or(b).or(c)
    val fix = i.fixTypeChefsFeatureExpressions(typeChefMistake, context)
    println("Wrong: " + typeChefMistake.implies(context).isTautology)
    println("Right: " + fix.implies(context).isTautology)
    println("Right: " + fix.implies(FeatureExprFactory.True).isTautology)
  }

  @Test def pretty_printer_test() {
    val file = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/applets/applets.pi")
    //testFile(file)
    val newFullFilePath = singleFilePath ++ getFileNameWithoutExtension(file) ++ ".ifdeftoif"
    val source_ast = getAstFromPi(new File(newFullFilePath))
    typecheckTranslationUnit(source_ast)
  }

  @Test def file_test() {
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/annotated_typedef.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/test_decluse.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/test_type_error.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/test_typedef.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/typedef_struct.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/double_typedef.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/ifdeftoif/test_main.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/struct_typedef_test.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/struct_enum_test.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/declaration_functioncall.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/typedef_usage_in_struct.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/function_variable_specifiers.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/function_call_test.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/single_files/test_specifier.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/variable_struct_member_usage.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/char_trailer.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/enum_test.c")
    //val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/pretty_print.c")
    val file = new File("C:\\Users\\Flo\\Dropbox\\HiWi\\Flo\\test\\struct_usage.c")

    val source_ast = getAstFromPi(file)
    println(source_ast.toString() ++ "\n\n")
    testFile(file)
  }

  @Test def declaration_transformation_test() {
    val c = Choice((FeatureExprFactory.createDefinedExternal("CONFIG_FEATURE_TAR_CREATE").and((FeatureExprFactory.createDefinedExternal("CONFIG_FEATURE_TAR_FROM")).not())), One(Constant("0")), One(PostfixExpr(Id("exclude_file"), FunctionCall(ExprList(List(Opt((FeatureExprFactory.createDefinedExternal("CONFIG_FEATURE_TAR_FROM").and(FeatureExprFactory.createDefinedExternal("CONFIG_FEATURE_TAR_CREATE"))), PostfixExpr(Id("tbInfo"), PointerPostfixSuffix("->", Id("excludeList")))), Opt((FeatureExprFactory.createDefinedExternal("CONFIG_FEATURE_TAR_FROM").and(FeatureExprFactory.createDefinedExternal("CONFIG_FEATURE_TAR_CREATE"))), Id("header_name"))))))))

    val tsts = i.conditionalToTuple(c, FeatureExprFactory.createDefinedExternal("CONFIG_FEATURE_TAR_CREATE"))
    val tu = TranslationUnit(List(Opt(True, Declaration(List(Opt(fx, SignedSpecifier()), Opt(True, IntSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("i"), List()), List(), None))))), Opt(True, Declaration(List(Opt(fx, IntSpecifier()), Opt(fx.not(), LongSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("j"), List()), List(), None))))), Opt(fx, Declaration(List(Opt(fx, IntSpecifier())), List(Opt(fx, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("k"), List()), List(), None))))), Opt(fx.not(), Declaration(List(Opt(fx, IntSpecifier()), Opt(True, LongSpecifier())), List(Opt(fx.not(), InitDeclaratorI(AtomicNamedDeclarator(List(), Id("k"), List()), List(), None)))))))
    //val decl = Opt(True,Declaration(List(Opt(fx,SignedSpecifier()), Opt(True,IntSpecifier())),List(Opt(True,InitDeclaratorI(AtomicNamedDeclarator(List(),Id("i"),List()),List(),None)))))
    //i.handleDeclarations(decl).foreach(x => println(PrettyPrinter.print(x.entry)))
    tu.defs.foreach(x => i.handleDeclarations(x.asInstanceOf[Opt[Declaration]]).foreach(y => println(PrettyPrinter.print(y.entry) ++ "\n")))
  }

  @Test def declaration_count_test() {
    /*val context = fa.and((fb.or(fc))).and(fc.or(fb.or(fa.not())))
    val choice_condition = fa.and((fb.or(fc))).and(fc.not().or(fb.not()))
    val first_statement = One(ExprStatement(PostfixExpr(Id("vfork_compressor"),FunctionCall(ExprList(List(Opt((fa.and(fc.or(fb)).and(fc.or(fb).or(fa.not())).and(fc.not().or(fb.not()))),PostfixExpr(Id("tbInfo"),PointerPostfixSuffix(".",Id("tarFd"))))))))))
    val second_statement = One(ExprStatement(PostfixExpr(Id("vfork_compressor"),FunctionCall(ExprList(List(Opt((fa.and(fc.or(fb)).and(fc.or(fb).or(fa.not())).and(fa.not().or(fc.not().and(fb.not())).or(fc.and(fb)))),PostfixExpr(Id("tbInfo"),PointerPostfixSuffix(".",Id("tarFd")))), Opt((fa.and(fc.or(fb)).and(fc.or(fb).or(fa.not())).and(fa.not().or(fc.not().and(fb.not()).or(fc.and(fb))))),Id("gzip"))))))))
    val c = Choice(choice_condition, first_statement, second_statement)
    val test = i.conditionalToTuple(c, context)
    val debug = 0*/

    val file = new File("C:/Users/Flo/Dropbox/HiWi/Flo/test/function_call_test.c")
    val source_ast = getAstFromPi(file)

    println(i.countNumberOfDeclarations(source_ast))
  }

  @Test def compareTypeCheckingTimes() {
    val applets = new File("C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/applets/applets.pi")
    val tr = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/coreutils/tr.pi")
    val bbunzip = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/archival/bbunzip.pi")
    val cal = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/coreutils/cal.pi")
    val ln = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/coreutils/ln.pi")
    val halt = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/init/halt.pi")
    val dump = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/libbb/dump.pi")
    val dc = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/miscutils/dc.pi")
    val inotifyd = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/miscutils/inotifyd.pi")
    val unzip = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/archival/unzip.pi")
    val hdpam = new File("C:/Users/Flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/miscutils/hdparm.pi")


    val list = List(applets, tr, bbunzip, cal, ln, halt, dump, dc, inotifyd, unzip, hdpam)
    val csvEntries = list.map(x => (x.getName(), compareTypeChecking(x))).map(y => y._1 + "," + y._2._1.toString + "," + y._2._2.toString + "," + i.computeDifference(y._2._1, y._2._2).toString + "\n") mkString
    val csvHeader = "File name, Type check source, Type check result, Difference\n"
    writeToTextFile(path ++ "type_check.csv", csvHeader + csvEntries)
  }
}