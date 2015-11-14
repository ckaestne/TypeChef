package de.fosd.typechef.typesystem


import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c.{Declaration, TestHelper}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class DeclTypingTest extends FunSuite with CTypeSystem with Matchers with TestHelper {


    private def declTL(code: String) = {
        val ast: Declaration = parseDecl(code)
        val r = getDeclaredVariables(ast, FeatureExprFactory.True, EmptyEnv)._2.map(e => (e._1, e._4))
//        println(r)
        r
    }

    private def declCT(code: String): Conditional[AType] = declTL(code)(0)._2.map(_.atype)

    private def declT(code: String): AType = declCT(code) match {
        case One(e) => e
        case e => CUnknown("Multiple types not expected " + e)
    }

    test("recognizing basic types") {
        declT("int a;") should be(CSigned(CInt()))
        declT("signed int a;") should be(CSigned(CInt()))
        declT("unsigned int a;") should be(CUnsigned(CInt()))
        declT("unsigned char a;") should be(CUnsigned(CChar()))
        declT("unsigned a;") should be(CUnsigned(CInt()))
        declT("signed a;") should be(CSigned(CInt()))
        declT("double a;") should be(CDouble())
        declT("long double a;") should be(CLongDouble())

        //allow also uncommon but correct notations
        declT("char a;") should be(CSignUnspecified(CChar()))
        declT("signed char a;") should be(CSigned(CChar()))
        declT("unsigned char a;") should be(CUnsigned(CChar()))
        declT("short a;") should be(CSigned(CShort()))
        declT("short int a;") should be(CSigned(CShort()))
        declT("unsigned short a;") should be(CUnsigned(CShort()))
        declT("int a;") should be(CSigned(CInt()))
        declT("unsigned int a;") should be(CUnsigned(CInt()))
        declT("long int a;") should be(CSigned(CLong()))
        declT("unsigned long int a;") should be(CUnsigned(CLong()))
        declT("long a;") should be(CSigned(CLong()))
        declT("unsigned long a;") should be(CUnsigned(CLong()))
        declT("__int128 a;") should be(CSigned(CInt128()))
        declT("unsigned __int128 a;") should be(CUnsigned(CInt128()))
        declT("long long int a;") should be(CSigned(CLongLong()))
        declT("unsigned long long int a;") should be(CUnsigned(CLongLong()))
        declT("long long a;") should be(CSigned(CLongLong()))
        declT("unsigned long long a;") should be(CUnsigned(CLongLong()))
        declT("float a;") should be(CFloat())
        declT("double a;") should be(CDouble())
        declT("long double a;") should be(CLongDouble())
        declT("_Bool a;") should be(CBool())

        declT("int double a;").isUnknown should be(true)
        declT("signed unsigned char a;").isUnknown should be(true)

        //default to int
        declT("auto a;") should be(CSigned(CInt()))
        declT("static a;") should be(CSigned(CInt()))
    }

    test("variable declarations") {
        declT("double a;") should be(CDouble())
        declTL("double a,b;") should be(List(("a", One(CDouble().toCType)), ("b", One(CDouble().toCType))))
        declT("double a[];") should be(CArray(CDouble()))
        declT("double **a;") should be(CPointer(CPointer(CDouble())))
        declT("double *a[];") should be(CArray(CPointer(CDouble())))
        declT("double a[][];") should be(CArray(CArray(CDouble())))
        declT("double *a[][];") should be(CArray(CArray(CPointer(CDouble()))))
        declT("double (*a)[];") should be(CPointer(CArray(CDouble())))
        declT("double *(*a[1])();") should be(CArray(CPointer(CFunction(Seq(), CPointer(CDouble())))))

        declT("float (*a)(double*);") should be(CPointer(CFunction(Seq(CPointer(CDouble())), CFloat())))
        declT("float (*a())(double*);") should be(CFunction(Seq(), CPointer(CFunction(Seq(CPointer(CDouble())), CFloat()))))

        declT("unsigned *out_len;") should be(CPointer(CUnsigned(CInt())))
    }

    test("function declarations") {
        declT("void main();") should be(CFunction(Seq(), CVoid()))
        declT("double (*fp)();") should be(CPointer(CFunction(Seq(), CDouble())))
        declT("double *fp();") should be(CFunction(Seq(), CPointer(CDouble())))
        declT("void main(double a);") should be(CFunction(Seq(CDouble()), CVoid()))
    }

    test("function declarations with abstract declarators") {
        declT("void main(double*, double);") should be(CFunction(Seq(CPointer(CDouble()), CDouble()), CVoid()))
        declT("void main(double*(), double);") should be(CFunction(Seq(CFunction(Seq(), CPointer(CDouble())), CDouble()), CVoid()))
        declT("void main(double(*(*)())());") should be(CFunction(Seq(
            CPointer(CFunction(Seq(), CPointer(CFunction(Seq(), CDouble()))))
        ), CVoid()))
    }

    test("struct declarations") {
        declT("struct { double a;} foo;").isInstanceOf[CAnonymousStruct] should be(true)
        declT("struct a foo;") should be(CStruct("a"))
        declT("struct a { double a;} foo;") should be(CStruct("a"))
        declTL("struct a;").size should be(0)

        declT( """struct mtab_list {
                    char *dir;
                    char *device;
                    struct mtab_list *next;
                } *mtl;""") should be(CPointer(CStruct("mtab_list", false)))

    }
    test("union declarations") {
        declT("union { double a;} foo;").isInstanceOf[CAnonymousStruct] should be(true)
        declT("union a foo;") should be(CStruct("a", true))
        declT("union a { double a;} foo;") should be(CStruct("a", true))
        declTL("union a;").size should be(0)
        //not checking __attribute__ ((__transparent_union__)), just ignoring those cases,
        //cf. http://www.delorie.com/gnu/docs/gcc/gcc_63.html
        declT("union a { double a;} foo __attribute__ ((__transparent_union__));") should be(CIgnore())
    }

    ignore("transparent union exception2 -- not relevant yet") {
        //not checking __attribute__ ((__transparent_union__)), just ignoring those cases,
        //cf. http://www.delorie.com/gnu/docs/gcc/gcc_63.html
        declT("union a  __attribute__ ((__transparent_union__)) { double a;} foo;") should be(CIgnore())
    }

    test("typeof declarations") {
        declT("typeof(int *) a;") should be(CPointer(CSigned(CInt())))
        declT("typeof(1) a;") should be(CSigned(CInt()))
    }

    test("conditional declarations") {
        declCT("int a;") should be(One(CSigned(CInt())))
        declCT("#ifdef X\nint\n#else\nlong\n#endif\n a;") should be(Choice(fx.not, One(CSigned(CLong())), One(CSigned(CInt()))))
        declCT("#ifdef X\nlong\n#endif\nlong a;") should be(Choice(fx, One(CSigned(CLongLong())), One(CSigned(CLong()))))
        declCT("long \n#ifdef X\n*\n#endif\n a;") should be(Choice(fx, One(CPointer(CSigned(CLong()))), One(CSigned(CLong()))))
        declCT("long \n#ifdef X\n*\n#endif\n#ifdef Y\n*\n#endif\n a;") should be(
            Choice(fy, Choice(fx, One(CPointer(CPointer(CSigned(CLong())))), One(CPointer(CSigned(CLong())))),
                Choice(fx, One(CPointer(CSigned(CLong()))), One(CSigned(CLong())))))
    }

    val ui = CUnsigned(CInt()).toCType
    test("attributes") {
        declTL("unsigned int a, __attribute__((unused)) b;") should be(List(("a", One(ui)), ("b", One(ui))))

    }

}