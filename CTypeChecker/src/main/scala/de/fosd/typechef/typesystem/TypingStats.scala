package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c.Expr
import de.fosd.typechef.conditional.Conditional
import de.fosd.typechef.featureexpr.FeatureExpr
import java.io.FileWriter


trait TypingStats extends CTypeSystemInterface {

    val statsOutputFile = new FileWriter("exprtypes.lst", true)
    val statsOutputFile2 = new FileWriter("expralternatives.lst", true)

    override protected def typedExpr(expr: Expr, ctype: Conditional[CType], featureExpr: FeatureExpr, env: Env) {
        super.typedExpr(expr, ctype, featureExpr, env)

        val typeList = ctype.simplify(featureExpr).toList

        for ((f, t) <- typeList) {
            statsOutputFile.write("%s;%s;%s;%d\n".format(/*expr.toString.take(50).replace(";", " ") + " -> " +*/ t.toText, featureExpr and f, expr.getPositionFrom.getFile, expr.getPositionFrom.getLine))
            statsOutputFile.flush()
        }
        statsOutputFile2.write("%d;%s;%s;%d\n".format(typeList.length, /*expr.toString.take(50).replace(";", " ") + " -> " +*/ ctype, expr.getPositionFrom.getFile, expr.getPositionFrom.getLine))
        statsOutputFile2.flush()

    }

}
