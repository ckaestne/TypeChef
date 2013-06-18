package de.fosd.typechef

import org.junit.Test
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.Opt
import FeatureExprFactory._
import java.io.File


class SerializationTest {


    @Test
    def testSerializationBDD() {
        FeatureExprFactory.setDefault(FeatureExprFactory.bdd)

        val fa = FeatureExprFactory.createDefinedExternal("a")
        val fb = FeatureExprFactory.createDefinedExternal("b")

        val ast = TranslationUnit(List(
            Opt(True, EmptyExternalDef()),
            Opt(fa and fb, EmptyExternalDef())
        ))

        val file = File.createTempFile("serialize", "ast")
        file.deleteOnExit()

        Frontend.serializeAST(ast, file.getAbsolutePath)

        val newAST = Frontend.loadSerializedAST(file.getAbsolutePath)

        assert(ast == newAST)
    }


    @Test
    def testSerializationSAT() {
        FeatureExprFactory.setDefault(FeatureExprFactory.sat)

        val fa = FeatureExprFactory.createDefinedExternal("a")
        val fb = FeatureExprFactory.createDefinedExternal("b")

        val ast = TranslationUnit(List(
            Opt(True, EmptyExternalDef()),
            Opt(fa and fb, EmptyExternalDef())
        ))

        val file = File.createTempFile("serialize", "ast")
        file.deleteOnExit()

        Frontend.serializeAST(ast, file.getAbsolutePath)

        val newAST = Frontend.loadSerializedAST(file.getAbsolutePath)

        assert(ast == newAST)
    }

    //    @Test
    //    def testSerializationMixed () {
    //        FeatureExprFactory.setDefault(FeatureExprFactory.sat)
    //
    //        val fa=FeatureExprFactory.createDefinedExternal("a")
    //        val fb=FeatureExprFactory.createDefinedExternal("b")
    //
    //        val ast = TranslationUnit(List(
    //            Opt(True, EmptyExternalDef()),
    //            Opt(fa and fb, EmptyExternalDef())
    //        ))
    //
    //        val file=File.createTempFile("serialize","ast")
    //        file.deleteOnExit()
    //
    //        Frontend.serializeAST(ast, file.getAbsolutePath)
    //
    //        FeatureExprFactory.setDefault(FeatureExprFactory.bdd)
    //
    //        val newAST = Frontend.loadSerializedAST(file.getAbsolutePath)
    //
    //        assert(ast==newAST)
    //    }

}
