package de.fost.typechef.parser.javascript


import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}

class ParserTest {
//
//    private def createLexer(str: String) = {
//        val input = str.getBytes
//        val a = new ByteArrayInputStream(input)
//        val o = new OffsetCharStream(a)
//        new Java15ParserTokenManager(o)
//    }
//
//    val p = new JavaScriptParser()
//
//    def assertParseable(code: String, mainProduction: (TokenReader[TokenWrapper, Null], FeatureExpr) => p.MultiParseResult[Any]) {
//        val actual = mainProduction(JavaLexer.lex(code.stripMargin), FeatureExprFactory.True)
//        System.out.println(actual)
//        (actual: @unchecked) match {
//            case p.Success(ast, unparsed) => {
//                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
//                //succeed
//            }
//            case p.NoSuccess(msg, unparsed, inner) =>
//                fail(msg + " at " + unparsed + " " + inner)
//        }
//    }
//
//    @Test
//    def testParserSimple6 = assertParseable("this.getClass().getName()", p.phrase(p.EqualityExpression))
//    @Test
//    def testParserSimple5 = assertParseable("System.out.println(\"AbstractController::postCommand - Current controller is: \" + this.getClass().getName());", p.phrase(p.Statement))
//    @Test
//    def testParserSimple4 = assertParseable("boolean", p.phrase(p.ResultType))
//    @Test
//    def testParserSimple() = assertParseable("class X {}", p.phrase(p.CompilationUnit))
//    @Test
//    def testParserSimple2 = assertParseable("package lancs.mobilemedia.core.comms;", p.PackageDeclaration)
//    @Test
//    def testParserSimple3 = assertParseable("public abstract boolean sendImage(byte[] imageData);", p.phrase(p.ClassOrInterfaceBodyDeclaration))
}