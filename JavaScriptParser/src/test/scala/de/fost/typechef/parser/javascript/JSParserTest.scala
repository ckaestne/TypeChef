package de.fost.typechef.parser.javascript


import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import java.io.{StringReader, ByteArrayInputStream}
import de.fosd.typechef.parser.javascript.{JSParser, JSToken, ParserMain}
import de.fosd.typechef.parser.TokenReader
import org.junit.Assert._
import org.junit.Test

class JSParserTest {



    val p = new JSParser()

    def assertParseable(code: String, mainProduction: (TokenReader[JSToken, Null], FeatureExpr) => p.MultiParseResult[Any]) {
        var tokens=ParserMain.lex(new StringReader(code))

        val actual = mainProduction(tokens, FeatureExprFactory.True)
        System.out.println(actual)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                //succeed
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                fail(msg + " at " + unparsed + " " + inner)
        }
    }


    @Test def testLiteral = assertParseable("1",p.Literal)
    @Test def testUnaryExpr1 = assertParseable("1",p.AssignmentExpression)
    @Test def testUnaryExpr = assertParseable("typeof undefined",p.UnaryExpression)
    @Test def testVarDecl = assertParseable("var core_strundefined = 1;",p.VariableStatement)
    @Test def testVarDecl2 = assertParseable("var core_strundefined = typeof undefined;",p.VariableStatement)
    @Test def testExpr1 = assertParseable("new jQuery.fn.init( selector, context, rootjQuery )",p.Expression)
    @Test def testStmt1 = assertParseable("return new jQuery.fn.init( selector, context, rootjQuery );",p.Statement)
    @Test def testRegex1 = assertParseable("/x/",p.RegularExpressionLiteral)
    @Test def testRegex2 = assertParseable("/[+-]?(?:\\d*\\.|)\\d+(?:[eE][+-]?\\d+|)/",p.Literal)
    @Test def testRegex3 = assertParseable("/-([\\da-z])/gi",p.Literal)
    @Test def testRegex4 = assertParseable("/^<(\\w+)\\s*\\/?>(?:<\\/\\1>|)$/",p.Literal)
//    @Test def testRegex5 = assertParseable("/'|\\\\/g",p.Literal)
    @Test def testExpr3= assertParseable("letter.toUpperCase()",p.LeftHandSideExpression)
    @Test def testStmt2= assertParseable("return letter.toUpperCase();",p.ReturnStatement)
    @Test def testStmt3= assertParseable("jQuery.fn = jQuery.prototype = 1;",p.Statement)
    @Test def testExpr4= assertParseable("jQuery.fn = jQuery.prototype = 1",p.AssignmentExpression)
    @Test def testExpr5= assertParseable("jQuery.prototype = 1",p.AssignmentExpression)
    @Test def testExpr6= assertParseable("jQuery.prototype",p.MemberExpression)
    @Test def testExpr7= assertParseable("{a:b}",p.ObjectLiteral)
    @Test def testProp= assertParseable("a:b",p.PropertyAssignment)
    @Test def testProp2= assertParseable("b",p.AssignmentExpression)
    @Test def testIfStmt= assertParseable("if ( typeof selector === \"string\" ) {}",p.IfStatement)
    @Test def testIfStmt2= assertParseable("1 === 2",p.Expression)
    @Test def testIfStmt3= assertParseable("1 == 2",p.EqualityExpression)
    @Test def testForStmt= assertParseable("for ( ; i < length; i++ ) {}",p.Statement)
    @Test def testForStmt4= assertParseable("for ( ; ; ) {}",p.Statement)
    @Test def testForStmt2= assertParseable("i < length",p.Expression)
    @Test def testForStmt3= assertParseable("i++",p.Expression)
    @Test def testExpr8= assertParseable("diff === first || ( diff % first === 0 && diff / first >= 0 )",p.Expression)
    @Test def testExpr8b= assertParseable("return diff === first || ( diff % first === 0 && diff / first >= 0 );",p.Statement)

}