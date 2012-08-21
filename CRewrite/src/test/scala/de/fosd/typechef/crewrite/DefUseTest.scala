package de.fosd.typechef.crewrite

import org.junit.Test
import de.fosd.typechef.parser.c._
import de.fosd.typechef.crewrite.CASTEnv._
import de.fosd.typechef.typesystem._
import java.util.IdentityHashMap
import collection.mutable.ListBuffer
import java.io.{FilenameFilter, FileInputStream, File}

class DefUseTest extends ConditionalNavigation with ASTNavigation with CDefUse with CTypeSystem with TestHelper {
  private def checkDefuse(ast: AST, defUseMap: IdentityHashMap[Id, List[Id]]): Boolean = {
    var idLB: ListBuffer[Id] = ListBuffer()
    val lst = filterASTElems[Id](ast)
    var missingLB: ListBuffer[Id] = ListBuffer()

    defUseMap.keySet().toArray().foreach(x => {
      idLB += x.asInstanceOf[Id]
      idLB = idLB ++ defUseMap.get(x)
    })
    val idLst = idLB.toList

    def filterDuplicates(lst: List[Id]): List[Id] = {
      var tmpLB: ListBuffer[Id] = ListBuffer()
      lst.foreach(x => {
        if (!tmpLB.exists(y => x.eq(y))) {
          tmpLB += x
        } else {
          //println("Duplicate " + x)
        }
      })
      return tmpLB.toList
    }

    println("FD: " + filterDuplicates(idLst).size)
    var countMissing = 0
    lst.foreach(x => {
      var contains = false
      idLst.foreach(y => {
        if (y.eq(x)) {
          contains = true
        }
      })
      if (!contains) {
        missingLB += x
      }
    })
    println("Amount of ids missing: " + missingLB.size + "\n" + missingLB)
    println("Filtered list size is: " + lst.size + ", the defuse map contains " + idLst.size + " Ids." + " containing " + (idLst.size - filterDuplicates(idLst).size) + " duplicate IDs.")

    //println(PrettyPrinter.print(ast))
    return (lst.size == filterDuplicates(idLst).size)
  }


  @Test def test_int_def_use {
    val source_ast = getAST("""
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
    val defUseMap = getDefUseMap

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println("Ids:\n" + filterASTElems[Id](source_ast))
    println("\nDef Use Map:\n" + defUseMap)
    checkDefuse(source_ast, defUseMap)
  }

  @Test def test_array_def_use {
    val source_ast = getAST("""
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
    val defUseMap = getDefUseMap

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println("\nDef Use Map:\n" + defUseMap)
  }

  @Test def test_struct_def_use {
    // TODO Verwendung struct variablen.
    val source_ast = getAST("""
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
    val defUseMap = getDefUseMap

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)

    println("Ids:\n" + filterASTElems[Id](source_ast))
    println("\nDef Use Map:\n" + defUseMap)
    checkDefuse(source_ast, defUseMap)
  }

  @Test def test_typedef_def_use {
    val ast = getAST("""
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
    val success = checkDefuse(ast, getDefUseMap)
    println("DefUse" + getDefUseMap)
    println("Success " + success)
  }

  @Test def test_opt_def_use {
    val source_ast = getAST("""
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
    val env = createASTEnv(source_ast)
    println("TypeChef Code:\n" + PrettyPrinter.print(source_ast))

    typecheckTranslationUnit(source_ast)
    val defUseMap = getDefUseMap

    println("+++PrettyPrinted+++\n" + PrettyPrinter.print(source_ast))
    println("Source:\n" + source_ast)
    println("\nDef Use Map:\n" + defUseMap)
  }

  @Test def test_busybox_verfication_of_defUse {
    // path to busybox dir with pi files to analyse
    val folderPath = "/Users/andi/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/"
    val folder = new File(folderPath)
    analyseDir(folder)

    val folderPath2 = "C:/users/flo/dropbox/hiwi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/"
    val folder2 = new File(folderPath2)
    analyseDir(folder2)
  }

  private def runDefUseOnPi(fileToAnalyse: File) {
    println("Analyse: " + fileToAnalyse.getName)
    val fis = new FileInputStream(fileToAnalyse)
    val ast = parseFile(fis, fileToAnalyse.getName, fileToAnalyse.getParent)
    fis.close()
    typecheckTranslationUnit(ast)
    val success = checkDefuse(ast, getDefUseMap)
    println("DefUse" + getDefUseMap)
    println("Success " + success)
    Thread.sleep(5999999)
    //println("AST" + ast)
  }

  private def analyseDir(dirToAnalyse: File) {
    // retrieve all pi from dir first
    if (dirToAnalyse.isDirectory) {
      val piFiles = dirToAnalyse.listFiles(new FilenameFilter {
        def accept(dir: File, file: String): Boolean = file.endsWith(".pi")
      })
      val dirs = dirToAnalyse.listFiles(new FilenameFilter {
        def accept(dir: File, file: String) = dir.isDirectory
      })
      for (piFile <- piFiles) {
        runDefUseOnPi(piFile)
      }
      for (dir <- dirs) {
        analyseDir(dir)
      }
    }
  }

}