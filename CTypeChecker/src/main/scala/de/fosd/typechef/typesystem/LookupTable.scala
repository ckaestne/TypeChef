package de.fosd.typechef.typesystem

import de.fosd.typechef.featureexpr.FeatureExpr

/**
 * stores name|type|scope|featureexpr
 *
 * @author kaestner
 *
 */
class LookupTable(functions: List[Entry] = List()) {
    def add(f: Entry) = new LookupTable(f :: functions)
    def find(name: String): List[Entry] = functions.filter(_.name == name)
    override def toString = functions.mkString("\n") + "\n" + functions.size + " entries."
}

//abstract class Entry(name:String,typeSig:String,scope:Int,feature:FeatureExpr) {}
abstract class Entry(val name: String, typeSig: String, scope: Int, val feature: FeatureExpr) {
    override def toString = getClass.getName + " " + name + " (" + typeSig + "), " + scope + ": " + feature
}

class LFunctionDef(name: String, typeSig: String, scope: Int, feature: FeatureExpr) extends Entry(name, typeSig, scope, feature)

class LDeclaration(name: String, typeSig: String, scope: Int, feature: FeatureExpr) extends Entry(name, typeSig, scope, feature)
