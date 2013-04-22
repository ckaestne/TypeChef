package de.fosd.typechef.crewrite

import org.junit.Test
import de.fosd.typechef.parser.c._
import de.fosd.typechef.crewrite.CASTEnv._
import de.fosd.typechef.typesystem._
import java.util.IdentityHashMap
import collection.mutable.ListBuffer
import java.io.{FilenameFilter, FileInputStream, File}
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.parser.c.GnuAsmExpr
import de.fosd.typechef.parser.c.TranslationUnit

class DeclUseTest extends ConditionalNavigation with ASTNavigation with CDeclUse with CTypeSystem with TestHelper {
  private def checkDefuse(ast: AST, declUseMap: IdentityHashMap[Id, List[Id]], useDeclMap: IdentityHashMap[Id, List[Id]]): String = {
    def getAllRelevantIds(a: Any): List[Id] = {
      a match {
        case id: Id => if (!(id.name.startsWith("__builtin"))) List(id) else List()
        case gae: GnuAsmExpr => List()
        case l: List[_] => l.flatMap(x => getAllRelevantIds(x))
        case p: Product => p.productIterator.toList.flatMap(x => getAllRelevantIds(x))
        case k => List()
      }
    }

    val resultString = new StringBuilder()
    var relevantIds = getAllRelevantIds(ast)

    val missingLB: ListBuffer[Id] = ListBuffer()
    val duplicateLB: ListBuffer[Id] = ListBuffer()
    val allIds: IdentityHashMap[Id, Id] = new IdentityHashMap()
    val defuseKeyList = declUseMap.keySet().toArray().toList

    defuseKeyList.foreach(x => {
      allIds.put(x.asInstanceOf[Id], null)
      declUseMap.get(x).foreach(y => {
        if (allIds.containsKey(y)) {
          duplicateLB += y
        }
        allIds.put(y, null)
      })
    })

    val numberOfIdsInAst = relevantIds.size
    val numberOfIdsInDefuse = allIds.keySet().size()

    relevantIds.foreach(x => {
      if (!allIds.containsKey(x)) {
        missingLB += x
      }
    })
    if (!missingLB.isEmpty) {
      resultString.append("Ids in decluse: " + numberOfIdsInDefuse)
      resultString.append("\nAmount of ids missing: " + missingLB.size + "\n" + missingLB.toList.map(x => (x + "@ " + x.range.get._1.getLine)) + "\n")
    }
    resultString.append("Filtered list size is: " + numberOfIdsInAst + ", the defuse map contains " + numberOfIdsInDefuse + " Ids." + " containing " + duplicateLB.size + " variable IDs.")
    if (!duplicateLB.isEmpty) {
      resultString.append("\nVariable Ids are: " + duplicateLB.toList.map(x => (x.name + "@ " + x.range.get._1.getLine + " from @ " + useDeclMap.get(x).map(y => y.range.get._1.getLine))))
    }
    // duplicateLB.foreach(x => resultString.append("\n"  + x + "@ " + x.range))
    return (resultString.toString())
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
    val env = createASTEnv(source_ast)

    typecheckTranslationUnit(source_ast)
    val defUseMap = getDeclUseMap
    val useDeclMap = getUseDeclMap

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println("Ids:\n" + filterASTElems[Id](source_ast))
    println("\nDef Use Map:\n" + defUseMap)
    println(checkDefuse(source_ast, defUseMap, useDeclMap))
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
    val env = createASTEnv(source_ast)

    typecheckTranslationUnit(source_ast)
    val defUseMap = getDeclUseMap

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println("\nDef Use Map:\n" + defUseMap)
  }

  private def getAstFromPi(fileToAnalyse: File): TranslationUnit = {
    println("++Analyse: " + fileToAnalyse.getName + "++")
    val fis = new FileInputStream(fileToAnalyse)
    val ast = parseFile(fis, fileToAnalyse.getName, fileToAnalyse.getParent)
    fis.close()
    ast
  }

  @Test def test_random_stuff {
    val source_ast = getAstFromPi(new File("/Users/flo/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/archival/tar.pi"))
    runDefUseOnAst(source_ast)
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
        i = st2.id;

        student2.name = "Joerg";
        student3.name = "Andi";

        student3.percentage = 90.0;


        return i;
      }
                             """);
    val env = createASTEnv(source_ast)

    typecheckTranslationUnit(source_ast)

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)

    println("Ids:\n" + filterASTElems[Id](source_ast))
    println("\nDef Use Map:\n" + getDeclUseMap)
    val useDeclMap = getUseDeclMap
    println(checkDefuse(source_ast, getDeclUseMap, useDeclMap))
  }

  @Test def test_int {
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
    println("Source:\n" + source_ast + "\n")
    println("\nPrettyPrinted:\n" + PrettyPrinter.print(source_ast))

    runDefUseOnAst(source_ast)
  }

  @Test def test_typedef_def_use {
    val ast = getAST( """
      #define MAX 30
      static int x;
      struct adres {
        char vname[MAX];
        char nname[MAX];
        long PLZ;
        char ort[MAX];
        int geburtsjahr;
      } adressen[100];
      typedef struct adres ADRESSE;

      typedef unsigned int WORD;
      typedef unsigned long DWORD;

      static void bar()  {
        WORD w2 = 3;
        w2 = w2+1;
        }

      static void foo(WORD w) {
        w = 3;
        int d = 2;
        w= 3;
      }
                      """);
    println("AST:\n" + ast)
    println("\nPrettyPrinted:\n" + PrettyPrinter.print(ast))
    typecheckTranslationUnit(ast)
    val useDeclMap = getUseDeclMap
    val success = checkDefuse(ast, getDeclUseMap, useDeclMap)
    println("DefUse" + getDeclUseMap)
    println(success)
  }

  @Test def test_opt_def_use {
    val source_ast = getAST( """
      int o = 32;
      int fooZ() {
        #if definedEx(A)
        const int konst = 55;
        int c = 32;
        #elif definedEx(B)
        int c = 42;
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
    val env = createASTEnv(source_ast)
    println("TypeChef Code:\n" + PrettyPrinter.print(source_ast))

    typecheckTranslationUnit(source_ast)
    val defUseMap = getDeclUseMap

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println("\nDef Use Map:\n" + defUseMap)
  }

  @Test def test_busybox_verification_of_defUse {
    // path to busybox dir with pi files to analyse
    val folderPath = "/Users/and1/Dropbox/HiWi/casestudies/busybox-1.18.5"
    val folder = new File(folderPath)
    analyseDir(folder)

    val folderPath2 = "C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/"
    val folder2 = new File(folderPath2)
    analyseDir(folder2)
  }

  @Test def test_cdrompi {
    // path to busybox dir with pi files to analyse
    val folderPath = "/Users/andi/Dropbox/HiWi/Flo/PiFiles/"
    val folder = new File(folderPath)
    analyseDir(folder)

    val folderPath2 = "C:/users/flo/dropbox/hiwi/flo/pifiles/"
    val folder2 = new File(folderPath2)
    analyseDir(folder2)
  }

  @Test def test_tarpi {
    // path to busybox dir with pi files to analyse
    val folderPath = "/Users/andi/Dropbox/HiWi/Flo/gzip/"
    val folder = new File(folderPath)
    analyseDir(folder)

    val folderPath2 = "C:\\Users\\Flo\\Dropbox\\HiWi\\Flo\\gzip"
    val folder2 = new File(folderPath2)
    analyseDir(folder2)
  }

  private def runDefUseOnAst(tu: TranslationUnit, parsingRunTimeString: String = "Parsing done.") {

    /* val fos = new FileOutputStream(fileToAnalyse.getAbsolutePath + ".ast")
       val bytes = ast.toString.getBytes
       fos.write(bytes)
       fos.flush()
       fos.close()  */

    println(parsingRunTimeString)
    val starttime = System.currentTimeMillis()
    typecheckTranslationUnit(tu)
    val endtime = System.currentTimeMillis()

    val declUse = getDeclUseMap
    val useDeclMap = getUseDeclMap
    val success = checkDefuse(tu, declUse, useDeclMap)

    /*val sb = new StringBuilder
    defuse.keySet().toArray().foreach(x => sb.append(x + "@" + x.asInstanceOf[Id].range + "\n"))
    val fw = new FileWriter(fileToAnalyse.getAbsolutePath + ".defuse")
    fw.write(sb.toString)
    fw.close()*/
    println("TypeChecking Runtime:\t" + (endtime - starttime) / 1000.0 + " seconds.\n")
    println("DeclUseMap: " + declUse)
    println(success + "\n\n")
    Thread.sleep(2000)
  }

  private def runDefUseOnPi(fileToAnalyse: File, printAst: Boolean = true) {
    println("++Analyse: " + fileToAnalyse.getName + "++")
    val starttime = System.currentTimeMillis()
    val fis = new FileInputStream(fileToAnalyse)
    val ast = parseFile(fis, fileToAnalyse.getName, fileToAnalyse.getParent)
    fis.close()
    if (printAst) {
      println("Ast: " + ast)
    }
    val endtime = System.currentTimeMillis()
    val parsingRuntimeString = "Parsing Runtime:\t\t" + (endtime - starttime) / 1000.0 + " seconds."
    runDefUseOnAst(ast, parsingRuntimeString)
  }

  private def analyseDir(dirToAnalyse: File, printAst: Boolean = false) {
    // retrieve all pi from dir first
    if (dirToAnalyse.isDirectory) {
      val piFiles = dirToAnalyse.listFiles(new FilenameFilter {
        def accept(dir: File, file: String): Boolean = file.endsWith(".pi")
      })
      val dirs = dirToAnalyse.listFiles(new FilenameFilter {
        def accept(dir: File, file: String) = dir.isDirectory
      })
      for (piFile <- piFiles) {
        runDefUseOnPi(piFile, printAst)
      }
      for (dir <- dirs) {
        analyseDir(dir)
      }
    }
  }

  @Test def test_minimal_stuff {
    val folderPath = "/Users/andi/Dropbox/hiwi/busybox/minimalbeispiel/"
    val folder = new File(folderPath)
    analyseDir(folder)
    val folderPath2 = "C:/users/flo/dropbox/hiwi/busybox/minimalbeispiel/"
    val folder2 = new File(folderPath2)
    analyseDir(folder2)
  }

  @Test def test_testpi {
    // path to busybox dir with pi files to analyse
    val folderPath = "/Users/andi/Dropbox/HiWi/Flo/test/"
    val folder = new File(folderPath)
    analyseDir(folder, false)
    val folderPath2 = "C:/users/flo/dropbox/hiwi/flo/test/"
    val folder2 = new File(folderPath2)
    analyseDir(folder2, true)
  }
}