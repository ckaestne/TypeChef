package de.fost.typechef.parser.javascript


import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import java.io.StringReader
import de.fosd.typechef.parser.javascript.JSParser
import de.fosd.typechef.parser.TokenReader
import org.junit.Assert._
import org.junit.Test
import de.fosd.typechef.parser.common.{CharacterToken, CharacterLexer}

class JSParserTest {


    val p = new JSParser()

    def assertNotParseable(code: String, mainProduction: (TokenReader[CharacterToken, Null], FeatureExpr) => p.MultiParseResult[Any]) = assertParseable(code, mainProduction, false)
    def assertParseable(code: String, mainProduction: (TokenReader[CharacterToken, Null], FeatureExpr) => p.MultiParseResult[Any], expectSuccess: Boolean = true) {
        var tokens = CharacterLexer.lex(new StringReader(code))

        val actual = mainProduction(tokens, FeatureExprFactory.True)
        System.out.println(actual)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                if (expectSuccess)
                    assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                else
                    assertFalse("parser did reach end of token stream: " + unparsed + " with result " + ast, unparsed.atEnd)
                //succeed
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                if (expectSuccess)
                    fail(msg + " at " + unparsed + " " + inner)
        }
    }


    @Test def testLiteral = assertParseable("1", p.Literal)
    @Test def testUnaryExpr1 = assertParseable("1", p.AssignmentExpression)
    @Test def testUnaryExpr = assertParseable("typeof 1", p.UnaryExpression)
    @Test def testVarDecl = assertParseable("var core_strundefined = 1;", p.VariableStatement)
    @Test def testVarDecl2 = assertParseable("var core_strundefined = typeof undefined;", p.VariableStatement)
    @Test def testExpr1 = assertParseable("new jQuery.fn.init( selector, context, rootjQuery )", p.Expression)
    @Test def testStmt1 = assertParseable("return new jQuery.fn.init( selector, context, rootjQuery );", p.Statement)
    @Test def testRegex1 = assertParseable("/x/", p.RegularExpressionLiteral)
    @Test def testRegex2 = assertParseable("/[+-]?(?:\\d*\\.|)\\d+(?:[eE][+-]?\\d+|)/", p.Literal)
    @Test def testRegex3 = assertParseable("/-([\\da-z])/gi", p.Literal)
    @Test def testRegex4 = assertParseable("/^<(\\w+)\\s*\\/?>(?:<\\/\\1>|)$/", p.Literal)
    @Test def testRegex5 = assertParseable("/'|\\\\/g", p.Literal)
    @Test def testExpr3 = assertParseable("letter.toUpperCase()", p.LeftHandSideExpression)
    @Test def testStmt2 = assertParseable("return letter.toUpperCase();", p.ReturnStatement)
    @Test def testExpr6 = assertParseable("jQuery.prototype", p.MemberExpression)
    @Test def testExpr5 = assertParseable("jQuery.prototype = 1", p.AssignmentExpression)
    @Test def testExpr4 = assertParseable("jQuery.fn = jQuery.prototype = 1", p.AssignmentExpression)
    @Test def testStmt3 = assertParseable("jQuery.fn = jQuery.prototype = 1;", p.Statement)
    @Test def testExpr7 = assertParseable("{a:b}", p.ObjectLiteral)
    @Test def testProp = assertParseable("a:b", p.PropertyAssignment)
    @Test def testProp2 = assertParseable("b", p.AssignmentExpression)
    @Test def testIfStmt2 = assertParseable("1 === 2", p.Expression)
    @Test def testIfStmt3 = assertParseable("1 == 2", p.EqualityExpression(false))
    @Test def testIfStmt = assertParseable("if ( typeof selector === \"string\" ) {}", p.IfStatement)
    @Test def testForStmt4 = assertParseable("for ( ; ; ) {}", p.Statement)
    @Test def testForStmt = assertParseable("for ( ; i < length; i++ ) {}", p.Statement)
    @Test def testForStmt2 = assertParseable("i < length", p.Expression)
    @Test def testForStmt3 = assertParseable("i++", p.Expression)
    @Test def testExpr8d = assertParseable("(  diff / first )", p.Expression)
    @Test def testExpr8c = assertParseable("(  diff / first >= 0 )", p.Expression)
    @Test def testExpr8a = assertParseable("( diff % first === 0 && diff / first >= 0 )", p.Expression)
    @Test def testExpr8 = assertParseable("diff === first || ( diff % first === 0 && diff / first >= 0 )", p.Expression)
    @Test def testExpr8b = assertParseable("return diff === first || ( diff % first === 0 && diff / first >= 0 );", p.Statement)

    @Test def testKeyword {
        assertParseable("instanceof", p.InstanceOf)
        assertNotParseable("instanceo", p.InstanceOf)
        assertNotParseable("instanceoff", p.InstanceOf)
    }

    @Test def testIdentifier {
        assertParseable("instanceof", p.InstanceOf)
        assertParseable("id", p.Identifier)
        assertParseable("$id", p.Identifier)
        assertParseable("i1", p.Identifier)
        assertNotParseable("do", p.Identifier)
    }


    @Test def testId2Id {
        assertParseable("id id", p.Id2Id)
        assertParseable("$id a", p.Id2Id)
        assertParseable("i1\nb", p.Id2Id)
        assertNotParseable("doait", p.Id2Id)
    }

    @Test def testNumericLiteral {
        assertParseable("0", p.NumericLiteral)
        assertParseable("1", p.NumericLiteral)
        assertParseable("12", p.NumericLiteral)
        assertParseable("12.34", p.NumericLiteral)
        assertParseable(".34", p.NumericLiteral)
        assertParseable("12e-3", p.NumericLiteral)
        assertParseable("12e3", p.NumericLiteral)
        assertParseable("12e+3", p.NumericLiteral)
        assertNotParseable("1a", p.NumericLiteral)
        assertParseable("0x4A", p.NumericLiteral)
        assertParseable("0X4a", p.NumericLiteral)
    }

    @Test def testStringLiteral {
        assertParseable("'abc'", p.StringLiteral)
        assertParseable("''", p.StringLiteral)
        assertParseable("\"abc\"", p.StringLiteral)
        assertParseable("'a\"b'", p.StringLiteral)
        assertParseable( """ "ab\"x" """.trim, p.StringLiteral)
        assertNotParseable(
            """ "ab
              ef"
            """.trim, p.StringLiteral)
        assertParseable(
            """ "ab \
              dfef"
            """.trim, p.StringLiteral)

    }


    @Test def testExpressions {
        assertParseable("0", p.PrimaryExpression)
        assertParseable("1", p.PrimaryExpression)
        assertParseable("1", p.MemberExpression)
        assertParseable("1", p.LeftHandSideExpression)
        assertParseable("1", p.PostfixExpression)
        assertParseable("1", p.UnaryExpression)
        assertParseable("1", p.AssignmentExpression)
        assertParseable("++1", p.UnaryExpression)
        assertParseable("++ 1", p.UnaryExpression)

        assertParseable("'abc'", p.PrimaryExpression)
        assertParseable("''", p.PrimaryExpression)

        assertParseable("1 | 2", p.Expression)
        assertParseable("1 || 2", p.Expression)
        assertParseable("1===1 || 2", p.Expression)

        assertParseable("1 == 2", p.EqualityExpression(false))
        assertParseable("1 >= 2", p.RelationalExpression(false))
        assertParseable("1 >= 2", p.RelationalExpression(true))
        assertParseable("1 >= 2", p.Expression)
        assertParseable("1 == 2", p.AssignmentExpression)
        assertParseable("1 === 2", p.EqualityExpression(false))
        assertParseable("1 === 2", p.AssignmentExpression(false))
        assertParseable("1 === 'test'", p.AssignmentExpression(false))
        assertParseable("typeof selector = \"string\"", p.AssignmentExpression(false))
        assertParseable("typeof selector === \"string\"", p.EqualityExpression(false))
        assertParseable("typeof selector === \"string\"", p.AssignmentExpression(false))
    }


    @Test def testSpacing {
        assertParseable("1 instanceof 2", p.Expression)
        assertNotParseable("1 instanceof2", p.Expression)
        assertNotParseable("1instanceof2", p.Expression)
        assertParseable("ainstanceof2", p.Expression)
    }
}