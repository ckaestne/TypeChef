package de.fosd.typechef.typesystem

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.conditional.Conditional


/**
 * bundles all environments during type checking
 */
trait CEnv {

    object EmptyEnv extends Env(new ConditionalTypeMap(), new VarTypingContext(), new StructEnv(), Map(), Map(), None, 0)

    protected class Env(
                           val typedefEnv: ConditionalTypeMap,
                           val varEnv: VarTypingContext,
                           val structEnv: StructEnv,
                           val enumEnv: EnumEnv,
                           val labelEnv: LabelEnv,
                           val expectedReturnType: Option[Conditional[CType]], //for a function
                           val scope: Int
                           ) {


        //varenv
        def updateVarEnv(newVarEnv: VarTypingContext) = if (newVarEnv == varEnv) this else new Env(typedefEnv, newVarEnv, structEnv, enumEnv, labelEnv, expectedReturnType, scope)
        def addVar(name: String, f: FeatureExpr, t: Conditional[CType], kind: DeclarationKind, scope: Int) = updateVarEnv(varEnv +(name, f, t, kind, scope))
        def addVars(vars: Seq[(String, FeatureExpr, Conditional[CType], DeclarationKind)], scope: Int) =
            updateVarEnv(vars.foldLeft(varEnv)((ve, v) => ve.+(v._1, v._2, v._3, v._4, scope)))
        def addVars(vars: Seq[(String, FeatureExpr, Conditional[CType])], kind: DeclarationKind, scope: Int) =
            updateVarEnv(vars.foldLeft(varEnv)((ve, v) => ve.+(v._1, v._2, v._3, kind, scope)))

        //structenv
        def updateStructEnv(s: StructEnv) = if (s == structEnv) this else new Env(typedefEnv, varEnv, s, enumEnv, labelEnv, expectedReturnType, scope)
        //enumenv
        def updateEnumEnv(s: EnumEnv) = if (s == enumEnv) this else new Env(typedefEnv, varEnv, structEnv, s, labelEnv, expectedReturnType, scope)

        //enumenv
        def updateLabelEnv(s: LabelEnv) = if (s == labelEnv) this else new Env(typedefEnv, varEnv, structEnv, enumEnv, s, expectedReturnType, scope)

        //typedefenv
        private def updateTypedefEnv(newTypedefEnv: ConditionalTypeMap) = if (newTypedefEnv == typedefEnv) this else new Env(newTypedefEnv, varEnv, structEnv, enumEnv, labelEnv, expectedReturnType, scope)
        def addTypedefs(typedefs: ConditionalTypeMap) = updateTypedefEnv(typedefEnv ++ typedefs)
        def addTypedefs(typedefs: Seq[(String, FeatureExpr, Conditional[CType])]) = updateTypedefEnv(typedefEnv ++ typedefs)
        def addTypedef(name: String, f: FeatureExpr, t: Conditional[CType]) = updateTypedefEnv(typedefEnv +(name, f, t))

        //expectedReturnType
        def setExpectedReturnType(newExpectedReturnType: Conditional[CType]) = new Env(typedefEnv, varEnv, structEnv, enumEnv, labelEnv, Some(newExpectedReturnType), scope)

        def incScope() = new Env(typedefEnv, varEnv, structEnv, enumEnv, labelEnv, expectedReturnType, scope + 1)
    }


    /*****
     * Variable-Typing context (collects all top-level and local declarations)
     * variables with local scope overwrite variables with global scope
     */
    //Variable-Typing Context: identifier to its non-void wellformed type
    type VarTypingContext = ConditionalVarEnv

    //    possible changes:
    //      case e: Declaration => outerVarEnv(e) ++ declType(e)
    //        case fun: FunctionDef => outerVarEnv(fun) + (fun.getName, fun -> featureExpr, ctype(fun))
    //        case e@DeclarationStatement(decl) => outerVarEnv(e) ++ declType(decl)
    //        //parameters in the body of functions
    //        case c@CompoundStatement(_) => c -> parentAST match {                     TODO
    //            case FunctionDef(_, decl, _, _) => outerVarEnv(c) ++ parameterTypes(decl)
    //            case NestedFunctionDef(_, _, decl, _, _) => outerVarEnv(c) ++ parameterTypes(decl)
    //            case _ => outerVarEnv(c)
    //        }
    //        TODO case nfun: NestedFunctionDef => outerVarEnv(nfun) + (nfun.getName, nfun -> featureExpr, ctype(nfun))


    /**
     * for struct and union
     * ConditionalTypeMap represents for the fields of the struct
     *
     * we store whether a structure with this name is defined (FeatureExpr) whereas
     * we do not distinguish between alternative structures. fields are merged in
     * one ConditionalTypeMap entry, but by construction they cannot overlap if
     * the structure declarations do not overlap variant-wise
     */
    class StructEnv(val env: Map[(String, Boolean), (FeatureExpr, ConditionalTypeMap)]) {
        def this() = this(Map())
        //returns the condition under which a structure is defined
        def someDefinition(name: String, isUnion: Boolean): Boolean = env contains(name, isUnion)
        def isDefined(name: String, isUnion: Boolean): FeatureExpr = env.getOrElse((name, isUnion), (FeatureExpr.dead, null))._1
        def isDefinedUnion(name: String) = isDefined(name, true)
        def isDefinedStruct(name: String) = isDefined(name, false)
        def add(name: String, isUnion: Boolean, condition: FeatureExpr, fields: ConditionalTypeMap) = {
            //TODO check distinct attribute names in each variant
            //TODO check that there is not both a struct and a union with the same name
            val oldCondition = isDefined(name, isUnion)
            val oldFields = env.getOrElse((name, isUnion), (null, new ConditionalTypeMap()))._2
            val key = (name, isUnion)
            val value = (oldCondition or condition, oldFields ++ fields)
            new StructEnv(env + (key -> value))
        }
        def get(name: String, isUnion: Boolean): ConditionalTypeMap = env((name, isUnion))._2
        override def toString = env.toString
    }

    //    two possible places:
    //           case e@DeclarationStatement(d) => addDeclaration(d, e)
    //            case e: Declaration => addDeclaration(e, e)


    /**
     * Enum Environment: Just a set of names that are valid enums.
     * No need to remember fields etc, because they are integers anyway and no further checking is done in C
     */

    type EnumEnv = Map[String, FeatureExpr]

    /**
     * label environment: stores which labels are reachable from a goto.
     *
     * the environment is filled upon function entry for the entire function
     * and just stores under which condition a label is defined
     */
    type LabelEnv = Map[String, FeatureExpr]

    /**
     * Typedef env
     *
     * possible in declaration and declaration statement
     */


}