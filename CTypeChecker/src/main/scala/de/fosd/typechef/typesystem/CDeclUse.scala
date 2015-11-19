package de.fosd.typechef.typesystem

import java.util
import java.util.{Collections, IdentityHashMap}

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory, FeatureModel}
import de.fosd.typechef.lexer.FeatureExprLib
import de.fosd.typechef.parser.c._
import org.apache.logging.log4j.LogManager

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag


/**
 * Wrapper case class for IdentityHashMap as java's IdentityHashMap allows get(Anyref)
 *
 * @param iIdHashMap the wrapped map
 */
case class IdentityIdHashMap(iIdHashMap: util.IdentityHashMap[Id, List[Id]]) extends Iterable[(Id, List[Id])] {

    def get(id: Id) = iIdHashMap.get(id)

    def containsKey(id: Id) = iIdHashMap.containsKey(id)

    def keySet = iIdHashMap.keySet

    def keys = keySet.toArray(Array[Id]()).toList

    def values = iIdHashMap.values

    def iterator = iIdHashMap.iterator
}

// this trait is a hook into the typesystem to preserve typing informations
// of declarations and usages
// the trait basically provides two maps: declaration -> usage and usages -> declarations
// for all identifiers that occur in a translation unit
// to do so typed elements are passed during typechecking to CDeclUse which
// stores the required information
trait CDeclUseInterface extends CEnv {

    def clearDeclUseMap() {}

    def init() {}

    def addStructRedeclaration(env: Env, declaration: Id, feature: FeatureExpr, isUnion: Boolean) {}

    def addDefinition(definition: AST, env: Env, feature: FeatureExpr = FeatureExprFactory.True, isFunctionDeclarator: Boolean = false) {}

    def addEnumUse(entry: AST, env: Env, feature: FeatureExpr) {}

    def addTypeUse(entry: AST, env: Env, feature: FeatureExpr) {}

    def addUse(entry: AST, feature: FeatureExpr, env: Env) {}

    def addOldStyleParameters(oldStyleParameters: List[Opt[OldParameterDeclaration]], declarator: Declarator, expr: FeatureExpr, env: Env) = {}

    def addStructUse(entry: AST, featureExpr: FeatureExpr, env: Env, structName: String, isUnion: Boolean) {}

    def addAnonStructUse(id: Id, fields: ConditionalTypeMap) {}

    def addStructDeclUse(entry: Id, env: Env, isUnion: Boolean, feature: FeatureExpr) {}

    def addDecl(current: Any, featureExpr: FeatureExpr, env: Env, isDefinition: Boolean = true) {}

    def addStructDefinition(definition: AST, env: Env, feature: FeatureExpr) {}

    def addJumpStatements(compoundStatement: CompoundStatement) {}
}

trait CDeclUse extends CDeclUseInterface with CEnv with CEnvCache {

    // TODO FeatureModel instead of FeatureExprFactory
    // TODO ASTEnv Caching
    private lazy val logger = LogManager.getLogger(this.getClass.getName)

    val numberOfBuiltinFunctions = 65

    private var declUseMap: util.IdentityHashMap[Id, util.Set[Id]] = new util.IdentityHashMap()
    private var useDeclMap: util.IdentityHashMap[Id, List[Id]] = new util.IdentityHashMap()
    private var structUsage: util.IdentityHashMap[Id, FeatureExpr] = new util.IdentityHashMap()

    private var stringToIdMap: Map[String, Id] = Map()

    private[typesystem] def clear() = clearDeclUseMap()

    private def putToDeclUseMap(decl: Id) = {
        if (!declUseMap.contains(decl)) {
            declUseMap.put(decl, Collections.newSetFromMap[Id](new util.IdentityHashMap()))
        }
    }

    private def addToDeclUseMap(decl: Id, use: Id): Any = {
        if (decl.eq(use) && !declUseMap.containsKey(decl)) {
            putToDeclUseMap(decl)
        }

        if (declUseMap.containsKey(decl) && !declUseMap.get(decl).contains(use) && !decl.eq(use)) {
            declUseMap.get(decl).add(use)
            addToUseDeclMap(use, decl)
        }
    }

    private def addToUseDeclMap(use: Id, decl: Id) = {
        if (useDeclMap.contains(use)) {
            useDeclMap.put(use, decl :: useDeclMap.get(use))
        } else {
            useDeclMap.put(use, List(decl))
        }
    }

    override def clearDeclUseMap() {
        declUseMap.clear()
        useDeclMap.clear()
    }

    override def init() {
        if (declUseMap == null) {
            declUseMap = new util.IdentityHashMap()
        }
        if (useDeclMap == null) {
            useDeclMap = new util.IdentityHashMap()
        }
        if (structUsage == null) {
            structUsage = new util.IdentityHashMap()
        }
    }

    def getDeclUseMap: IdentityIdHashMap = {
        val morphedDeclUsedMap = new util.IdentityHashMap[Id, List[Id]]()
        declUseMap.keySet().foreach(x => morphedDeclUsedMap.put(x, declUseMap.get(x).toList))
        IdentityIdHashMap(morphedDeclUsedMap)
    }

    def getUseDeclMap = IdentityIdHashMap(useDeclMap)


    // add definition:
    //   - function: function declarations (forward declarations) and function definitions are handled
    //               if a function declaration exists, we add it as def and the function definition as its use
    //               if no function declaration exists, we add the function definition as def
    override def addDefinition(definition: AST, env: Env, feature: FeatureExpr, isFunctionDeclarator: Boolean = false) {
        definition match {
            case id: Id =>
                if (isFunctionDeclarator) addFunctionDeclaration(env, id, feature)
                else putToDeclUseMap(id)
            case _ =>
        }
    }

    override def addStructDefinition(definition: AST, env: Env, feature: FeatureExpr) {
        definition match {
            case id: Id =>
                val relevantIds = structUsage.filter(x => x._2.equals(FeatureExprFactory.True) || x._2.implies(feature).isTautology())
                addDefinition(id, env, feature)
                relevantIds.foreach(x => {
                    addToDeclUseMap(id, x._1)
                    structUsage.remove(x._1)
                })
            case _ =>
        }
    }

    private def addFunctionDeclaration(env: Env, declaration: Id, feature: FeatureExpr) {

        def swapDeclaration(originalDecl: Id, newDecl: Id) = {
            if (!originalDecl.eq(newDecl)) {
                putToDeclUseMap(newDecl)
                addToDeclUseMap(newDecl, originalDecl)
                declUseMap.get(originalDecl).foreach(x => addToDeclUseMap(newDecl, x))
                declUseMap.remove(originalDecl)
            }
        }

        // Forward Declaration of functions:
        // in case of:
        // (1) int foo();
        // (2) int foo(){}
        // we have to swap the definition of (2) with (1) as declaration and (1) as use,
        // because all further calls are refered to (2)
        def addForwardDeclartion(original: Id, x: (FeatureExpr, AST)): Any = {
            if (declUseMap.containsKey(original)) {
                if (feature.equivalentTo(FeatureExprFactory.True) || (feature.implies(x._1).isTautology())) swapDeclaration(original, declaration)
                else putToDeclUseMap(declaration)
            } else {
                putToDeclUseMap(declaration)
                addToDeclUseMap(declaration, original)
            }
        }

        env.varEnv.getAstOrElse(declaration.name, null) match {
            case One(null) => putToDeclUseMap(declaration)
            case One(i: InitDeclarator) => swapDeclaration(i.getId, declaration)
            case c@Choice(_, _, _) =>
                conditionalToTuple(c).foreach(x => {
                    x._2 match {
                        case i: InitDeclarator => addForwardDeclartion(i.getId, x)
                        case f: FunctionDef => addForwardDeclartion(f.declarator.getId, x)
                        case k =>
                    }
                })
            case One(f: FunctionDef) => swapDeclaration(f.declarator.getId, declaration)
            case x =>
        }
    }

    override def addStructRedeclaration(env: Env, declaration: Id, feature: FeatureExpr, isUnion: Boolean) {

        def swapDeclaration(originalDecl: Id, newDecl: Id) = {
            putToDeclUseMap(newDecl)
            addToDeclUseMap(newDecl, originalDecl)
            if (declUseMap.contains(originalDecl)) {
                declUseMap.get(originalDecl).foreach(x => addToDeclUseMap(newDecl, x))
            }
            declUseMap.remove(originalDecl)
        }

        // Forward Declaration of functions:
        // in case of:
        // (1) int foo();
        // (2) int foo(){}
        // we have to swap the definition of (2) with (1) as declaration and (1) as use,
        // because all further calls are refered to (2)
        def addForwardDeclaration(original: Id, x: (FeatureExpr, AST)): Any = {
            if (declUseMap.containsKey(original)) {
                if (feature.equivalentTo(FeatureExprFactory.True) || (feature.implies(x._1).isTautology())) swapDeclaration(original, declaration)
                else putToDeclUseMap(declaration)
            } else {
                putToDeclUseMap(declaration)
                addToDeclUseMap(declaration, original)
            }
        }

        if (env.structEnv.someDefinition(declaration.name, isUnion)) {
            val originalId = env.structEnv.getId(declaration.name, isUnion)
            originalId match {
                case One(i: Id) =>
                    if (!i.eq(declaration)) {
                        swapDeclaration(i, declaration)
                    }
                case c@Choice(_, _, _) =>
                    conditionalToTuple(c).foreach(x => {
                        x._2 match {
                            case i: Id =>
                                if (x._1.implies(feature).isTautology) {
                                    addForwardDeclaration(i, x)
                                }
                            case k =>
                        }
                    })
            }
        }
    }

    override def addEnumUse(entry: AST, env: Env, feature: FeatureExpr) {
        entry match {
            case i@Id(name) =>
                if (env.enumEnv.containsKey(name)) {
                    val enumDeclarationFeature = env.enumEnv.get(name).get._1
                    val enumDeclarationId = env.enumEnv.get(name).get._2
                    if (feature.equivalentTo(FeatureExprFactory.True) || (feature.implies(enumDeclarationFeature).isTautology())) {
                        addToDeclUseMap(enumDeclarationId, i)
                    }
                }
            case x =>
        }
    }

    override def addTypeUse(entry: AST, env: Env, feature: FeatureExpr) {
        def addOne(one: One[AST], use: Id, env: Env) {
            one match {
                case One(InitDeclaratorI(declarator, _, _)) => addToDeclUseMap(declarator.getId, use)
                case One(AtomicNamedDeclarator(_, key, _)) => addToDeclUseMap(key, use)
                case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) => addToDeclUseMap(key, use)
                case One(Enumerator(key, _)) => addToDeclUseMap(key, use)
                case One(Declaration(specifiers, inits)) =>
                    inits.foreach(x => x match {
                        case Opt(_, entry) => addOne(One(entry), use, env)
                        case _ =>
                    })
                case _ =>
            }
        }

        entry match {
            case i@Id(name) =>
                if (env.typedefEnv.contains(name)) {
                    env.typedefEnv.getAstOrElse(name, null) match {
                        case o@One(_) =>
                            addOne(o, i, env)
                        case c@Choice(_, _, _) =>
                            addChoice(c, feature, i, env, addOne)
                        case _ =>
                    }
                }
        }
    }

    private def addChoice(choice: Choice[AST], featureExpr: FeatureExpr, use: Id, env: Env, oneFunc: (One[AST], Id, Env) => Unit) {
        choice match {
            case Choice(feature1, o1@One(_), o2@One(_)) =>
                if (featureExpr.equivalentTo(FeatureExprFactory.True)) {
                    oneFunc(o1, use, env)
                    oneFunc(o2, use, env)
                } else if (featureExpr.implies(feature1).isTautology()) {
                    oneFunc(o1, use, env)
                } else if (featureExpr.implies(feature1.not()).isTautology()) {
                    oneFunc(o2, use, env)
                } else {
                    oneFunc(o1, use, env)
                    oneFunc(o2, use, env)
                }
            case Choice(feature1, o@One(_), c@Choice(feature2, _, _)) =>
                oneFunc(o, use, env)
                if (!featureExpr.equivalentTo(feature1)) {
                    addChoice(c, featureExpr, use, env, oneFunc)
                }
            case Choice(_, c1@Choice(_, _, _), c2@Choice(_, _, _)) =>
                addChoice(c1, featureExpr, use, env, oneFunc)
                addChoice(c2, featureExpr, use, env, oneFunc)
            case Choice(_, c@Choice(_, _, _), o@One(_)) =>
                oneFunc(o, use, env)
                addChoice(c, featureExpr, use, env, oneFunc)
            case _ =>
        }
    }

    private def addDefChoice(entry: Conditional[AST]) {
        def addOne(entry: One[AST]) {
            entry match {
                case One(InitDeclaratorI(declarator, _, _)) => putToDeclUseMap(declarator.getId)
                case One(AtomicNamedDeclarator(_, key, _)) => putToDeclUseMap(key)
                case One(Enumerator(key, _)) => putToDeclUseMap(key)
                case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) => putToDeclUseMap(key)
                case One(null) =>
                case _ =>
            }
        }

        entry match {
            case o@One(one: AST) => addOne(o)
            case Choice(feature, c1, c2) =>
                addDefChoice(c1)
                addDefChoice(c2)
            case _ =>
        }
    }


    override def addUse(entry: AST, feature: FeatureExpr, env: Env) {
        def addUseOne(one: One[AST], use: Id, env: Env) {
            one match {
                case One(InitDeclaratorI(declarator, _, _)) => addToDeclUseMap(declarator.getId, use)
                case One(AtomicNamedDeclarator(_, key, _)) => addToDeclUseMap(key, use)
                case One(FunctionDef(_, NestedNamedDeclarator(_, declarator, _, _), _, _)) => addToDeclUseMap(declarator.getId, use)
                case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) => addToDeclUseMap(key, use)
                case One(Enumerator(key, _)) => addToDeclUseMap(key, use)
                case One(NestedNamedDeclarator(_, declarator, _, _)) => addToDeclUseMap(declarator.getId, use)
                case One(NestedFunctionDef(_, _, AtomicNamedDeclarator(_, key, _), _, _)) => addToDeclUseMap(key, use)
                case One(key: Id) => addToDeclUseMap(key, use)
                case One(null) =>
                    // Workaround for typedefs in sizeof expressions, current typesystem does not check these
                    if (env.typedefEnv.getAstOrElse(use.name, null) != null) {
                        addTypeUse(use, env, feature)
                    } else if (env.structEnv.someDefinition(use.name, true)) {
                        addStructDeclUse(use, env, true, feature)
                    } else if (env.structEnv.someDefinition(use.name, false)) {
                        addStructDeclUse(use, env, false, feature)
                    }
                // TODO Enums, TypeDefs and Structs
                case _ =>
            }
        }


        // TODO andreas: engine code looks a little messy
        def addUseCastExpr(typ: TypeName, addUse: (AST, FeatureExpr, CDeclUse.this.type#Env) => Unit, feature: FeatureExpr, env: CDeclUse.this.type#Env, lst: List[Opt[Initializer]]) {
            var typedefspecifier: Id = null
            typ match {
                case TypeName(ls, _) =>
                    ls.foreach(x => x.entry match {
                        case StructOrUnionSpecifier(_, _, Some(innerLst), _, _) =>
                            innerLst.foreach(y => y.entry match {
                                case StructDeclaration(inits, decls) =>
                                    inits.foreach(j => addUse(j.entry, feature, env))
                                    decls.foreach(z => z.entry match {
                                        case StructDeclarator(a: AtomicNamedDeclarator, _, _) =>
                                            if (!declUseMap.containsKey(a.getId)) {
                                                putToDeclUseMap(a.getId)
                                            }
                                            stringToIdMap += (a.getName -> a.getId)
                                        case k => addUse(k, feature, env)
                                    })
                            })
                        case TypeDefTypeSpecifier(i@Id(name)) =>
                            typedefspecifier = i
                        //addTypeUse(i, env, x.feature)
                        case k => addUse(k, feature, env)
                    })
            }
            lst.foreach(x => x.entry match {
                case Initializer(Some(InitializerAssigment(lst2)), expr) =>
                    addUse(expr, feature, env)
                    lst2.foreach(y => y.entry match {
                        case idd@InitializerDesignatorD(i) =>
                            env.varEnv.getAstOrElse(i.name, null) match {
                                case One(InitDeclaratorI(declarator, _, _)) =>
                                    if (!declUseMap.contains(declarator.getId)) {
                                        putToDeclUseMap(declarator.getId)
                                    }
                                    addToDeclUseMap(declarator.getId, i)
                                case One(AtomicNamedDeclarator(_, key, _)) => addToDeclUseMap(key, i)
                                case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) =>
                                    if (!declUseMap.contains(key)) {
                                        putToDeclUseMap(key)
                                    }
                                    addToDeclUseMap(key, i)
                                case One(Enumerator(key, _)) => addToDeclUseMap(key, i)
                                case One(NestedNamedDeclarator(_, nestedDecl, _, _)) => addToDeclUseMap(nestedDecl.getId, i)
                                case c@Choice(_, _, _) => addChoice(c, feature, i, env, addUseOne)
                                case One(null) =>
                                    if (stringToIdMap.containsKey(i.name) && declUseMap.containsKey(stringToIdMap.get(i.name).get)) {
                                        addToDeclUseMap(stringToIdMap.get(i.name).get, i)
                                    } else if (!(typedefspecifier == null)) {
                                        env.typedefEnv.getAstOrElse(typedefspecifier.name, null) match {
                                            case One(Declaration(specs, decl)) =>
                                                for (Opt(_, StructOrUnionSpecifier(_, None, Some(lst), _, _)) <- specs) {
                                                    for (Opt(_, StructDeclaration(qualis, structDecls)) <- lst) {
                                                        for (Opt(innerFeature, StructDeclarator(a: AtomicNamedDeclarator, _, _)) <- structDecls) {
                                                            val declaration = a.getId
                                                            if (declaration.name.equals(i.name) && feature.implies(innerFeature).isTautology()) {
                                                                addToDeclUseMap(declaration, i)
                                                            }
                                                        }
                                                    }
                                                }
                                            case _ =>
                                        }
                                    }
                                case _ =>
                            }
                        case _ =>
                    })
                case k =>
                    addUse(k, feature, env)
            })
            stringToIdMap = stringToIdMap.empty
        }

        // For each incoming use of an identifier, we look up in the varEnv the corresponding declaration.
        // Furthermore, known special cases are filtered before.

        def getIdElements(ast: Any, current: List[AST] = List()): List[AST] = {
            ast match {
                case t: TypeDefTypeSpecifier => t :: current
                case t: CastExpr => t :: (t.productIterator.toList.flatMap(getIdElements(_, current)) ++ current)
                case t: StructOrUnionSpecifier => t :: current
                case t: BuiltinOffsetof => t :: current
                case id: Id => id :: current
                case pps: PointerPostfixSuffix => current
                case p: Product => p.productIterator.toList.flatMap(getIdElements(_, current))
                case _ => current
            }
        }

        val idElements = getIdElements(entry)
        idElements.foreach(x => x match {
            case TypeDefTypeSpecifier(id) =>
            case CastExpr(typ, LcurlyInitializer(lst)) =>
                addUseCastExpr(typ, addUse _, feature, env, lst)
            case PostfixExpr(id: Id, suffix: PointerPostfixSuffix) =>
                env.varEnv.getAstOrElse(id.name, null) match {
                    case o@One(_) => addUseOne(o, id, env)
                    case c@Choice(_, _, _) =>
                        addChoice(c, feature, id, env, addUseOne)
                    case _ =>
                }
            case StructOrUnionSpecifier(union, Some(i: Id), _, _, _) =>
                addStructDeclUse(i, env, union, feature)
            case BuiltinOffsetof(typeName, offsetDesignators) =>
                typeName.specifiers.foreach(x => {
                    x match {
                        case Opt(ft, TypeDefTypeSpecifier(name)) =>
                            offsetDesignators.foreach(x => x match {
                                case Opt(ft, OffsetofMemberDesignatorID(offsetId: Id)) =>
                                    addStructUse(offsetId, ft, env, name.name, false)
                            })
                        case Opt(ft, StructOrUnionSpecifier(isUnion, Some(i: Id), _, _, _)) =>
                            offsetDesignators.foreach(x => x match {
                                case Opt(ft, OffsetofMemberDesignatorID(offsetId: Id)) =>
                                    addStructUse(offsetId, ft, env, offsetId.name, isUnion)
                            })
                            addStructDeclUse(i, env, isUnion, ft)
                        case _ =>
                    }
                })
            case id: Id =>
                if (!useDeclMap.containsKey(id)) {
                    env.varEnv.getAstOrElse(id.name, null) match {
                        case o@One(_) => addUseOne(o, id, env)
                        case c@Choice(_, _, _) =>
                            addChoice(c, feature, id, env, addUseOne)
                        case x =>
                    }
                }
            case k =>
        })
    }


    override def addOldStyleParameters(oldStyleParameters: List[Opt[OldParameterDeclaration]], declarator: Declarator, expr: FeatureExpr, env: Env) = {

        def addDeclIdList(d: DeclIdentifierList, oldStyleId: Id, expr: FeatureExpr) {
            for (Opt(idFeature, id) <- d.idList)
                if (id.name.equals(oldStyleId.name) && (idFeature.equivalentTo(FeatureExprFactory.True) || idFeature.implies(expr).isTautology))
                    addToDeclUseMap(id, oldStyleId)
        }

        def addOldStyleParameterDeclarator(oldStyleId: Id, expr: FeatureExpr, env: Env) {
            declarator.extensions.foreach(x => x.entry match {
                case d: DeclIdentifierList => addDeclIdList(d, oldStyleId, expr)
                case _ =>
            })
        }

        for (Opt(f, osp) <- oldStyleParameters) {
            osp match {
                case d: Declaration => d.init.foreach(decl => addOldStyleParameterDeclarator(decl.entry.getId, decl.condition, env))
                case VarArgs() =>
                case x =>
            }
        }
    }


    private def conditionalToTuple[T <: Any](cond: Conditional[T], fexp: FeatureExpr = FeatureExprFactory.True): List[(FeatureExpr, T)] = {
        cond match {
            case One(a) if a.isInstanceOf[T] => List((fexp, a))
            case Choice(ft, thenExpr, elseBranch) => conditionalToTuple(thenExpr, ft) ++ conditionalToTuple(elseBranch, ft.not())
            case _ => List()
        }
    }

    private def getFieldsForFeature(structEnv: StructEnv, structName: String, isUnion: Boolean, context: FeatureExpr): List[ConditionalTypeMap] = {
        structEnv.getFields(structName, isUnion) match {
            case One(x) =>
                List(x)
            case c@Choice(ft, thenBranch, elseBranch) =>
                if (context.equivalentTo(FeatureExprFactory.True)) {
                    conditionalToTuple(c).map(x => x._2)
                } else {
                    conditionalToTuple(c).filter(x => context.implies(x._1).isTautology()).map(x => x._2)
                }
        }
    }

    override def addStructUse(entry: AST, featureExpr: FeatureExpr, env: Env, structName: String, isUnion: Boolean) {
        entry match {
            case i@Id(name) => {
                if (env.structEnv.someDefinition(structName, isUnion)) {
                    val validFields = getFieldsForFeature(env.structEnv, structName, isUnion, featureExpr).map(x => x.getAstOrElse(i.name, null))
                    validFields.foreach(x => x match {
                        case One(null) =>
                            addStructDeclUse(i, env, isUnion, featureExpr)
                        case One(AtomicNamedDeclarator(_, i2: Id, _)) =>
                            addToDeclUseMap(i2, i)
                        case One(i2: Id) =>
                            addToDeclUseMap(i2, i)
                        case c@Choice(_, _, _) =>
                            addStructUseChoice(c, i)
                        case One(NestedNamedDeclarator(_, AtomicNamedDeclarator(_, i2: Id, _), _, _)) =>
                            addToDeclUseMap(i2, i)
                        case k =>
                    })
                } else {
                    env.typedefEnv.getAstOrElse(i.name, null) match {
                        case One(i2: Id) =>
                            addToDeclUseMap(i2, i)
                        case One(null) =>
                        // addDefinition(i, env)
                        case c@Choice(_, _, _) =>
                        case One(Declaration(List(Opt(_, _), Opt(_, s@StructOrUnionSpecifier(_, Some(id), _, _, _))), _)) =>
                            // TODO andreas: typedef name name // comment not specific
                            putToDeclUseMap(i)
                        case k =>
                    }
                }
            }
            case OffsetofMemberDesignatorID(id) =>
                addStructUse(id, featureExpr, env, structName, isUnion)
            case k =>
        }
    }

    override def addAnonStructUse(id: Id, fields: ConditionalTypeMap) {
        fields.getAstOrElse(id.name, null) match {
            case c@Choice(_, _, _) => addStructUseChoice(c, id)
            case One(AtomicNamedDeclarator(_, key, _)) => addToDeclUseMap(key, id)
            case One(NestedNamedDeclarator(_, declarator, _, _)) => addToDeclUseMap(declarator.getId, id)
            case k =>
        }
    }

    private def addStructUseChoice(cond: Conditional[AST], use: Id) {
        def addOne(one: One[AST], use: Id) {
            one match {
                case One(AtomicNamedDeclarator(_, key, _)) => addToDeclUseMap(key, use)
                case One(NestedNamedDeclarator(_, declarator, _, _)) => addToDeclUseMap(declarator.getId, use)
                case One(i@Id(_)) => addToDeclUseMap(i, use)
                case _ =>
            }
        }

        cond match {
            case o@One(_) => addOne(o, use)
            case Choice(_, c1, c2) =>
                addStructUseChoice(c1, use)
                addStructUseChoice(c2, use)
            case _ =>
        }
    }

    override def addStructDeclUse(entry: Id, env: Env, isUnion: Boolean, feature: FeatureExpr) {
        def addOne(one: One[AST], use: Id) = {
            one match {
                case One(id: Id) =>
                    addToDeclUseMap(id, use)
                case _ =>
            }
        }

        entry match {
            case use@Id(name) => {
                if (env.structEnv.someDefinition(name, isUnion)) {
                    env.structEnv.getId(name, isUnion) match {
                        case o@One(key: Id) =>
                            addOne(o, use)
                        case c@Choice(_, _, _) =>
                            val condTuple = c.toList
                            // TODO
                            val tuple = condTuple.filter(x => x._1.equivalentTo(FeatureExprFactory.True) || !feature.and(x._1).isContradiction())
                            val tupleVariableDef = condTuple.filter(x => x._1.implies(feature).isTautology).diff(tuple)
                            if (feature.implies(tupleVariableDef.foldLeft(FeatureExprFactory.False)((a, b) => a.or(b._1))).isTautology) {
                                tupleVariableDef.foreach(x => {
                                    addToDeclUseMap(x._2.asInstanceOf[Id], use)
                                })
                            }
                            tuple.foreach(x => {
                                addToDeclUseMap(x._2.asInstanceOf[Id], use)
                            })
                        case x => // match error, causes exception @ openssl - TODO analyse
                    }
                } else {
                    structUsage.put(use, feature)
                }
            }
            case _ =>
        }
    }

    override def addDecl(current: Any, featureExpr: FeatureExpr, env: Env, isDefinition: Boolean = true) {
        current match {
            case AtomicNamedDeclarator(pointers, id, extension) =>
                if (!isDefinition) {
                    addDefinition(id, env, featureExpr, isFunctionDeclarator = true)
                }
            case k =>
        }
    }

    private def addLcurlyInitializer(id: Id, init: Initializer, feature: FeatureExpr, env: Env) {
        init match {
            case Initializer(_, LcurlyInitializer(lst)) =>
                lst.foreach(x => x.entry match {
                    case Initializer(Some(InitializerAssigment(lst2)), _) =>
                        lst2.foreach(y => y.entry match {
                            case idd@InitializerDesignatorD(i) =>
                                addStructUse(i, feature, env, id.name, isUnion = false)
                            case _ =>
                        })
                    case _ =>
                })
            case _ =>
        }
    }

    override def addJumpStatements(compoundStatement: CompoundStatement) {
        addGotoStatements(compoundStatement)
    }

    private def addGotoStatements(f: AST) {
        val labelMap: IdentityHashMap[Id, FeatureExpr] = new IdentityHashMap()

        def get[T](a: Any)(implicit m: ClassTag[T]): List[Opt[T]] = {
            a match {
                // TODO: Feature does not have to be true
                case c: One[_] if (m.runtimeClass.isInstance(c.value)) => List(Opt(FeatureExprFactory.True, c.value.asInstanceOf[T]))
                case o: Opt[_] if (m.runtimeClass.isInstance(o.entry)) => List(o.asInstanceOf[Opt[T]])
                case l: List[_] => l.flatMap(x => get[T](x))
                case p: Product => p.productIterator.toList.flatMap(x => get[T](x))
                case _ => List()
            }
        }

        get[LabelStatement](f).foreach(label => {
            putToDeclUseMap(label.entry.id)
            labelMap.put(label.entry.id, label.condition)
        })
        get[GotoStatement](f).foreach(goto =>
            goto.entry.target match {
                case usage@Id(name) => labelMap.keySet().toArray.foreach(declaration =>
                    if (declaration.asInstanceOf[Id].name.equals(name) && (goto.condition.equivalentTo(FeatureExprFactory.True) || labelMap.get(declaration).implies(goto.condition).isTautology))
                        addToDeclUseMap(declaration.asInstanceOf[Id], usage))
                case k => logger.error("Missing GotoStatement: " + k)
            })
    }

    // method recursively filters all AST elements for a given type T
    // Copy / Pasted from ASTNavigation -> unable to include ASTNavigation because of dependencies
    private def filterASTElements[T <: AST](a: Any)(implicit m: ClassTag[T]): List[T] = {
        a match {
            case p: Product if (m.runtimeClass.isInstance(p)) => List(p.asInstanceOf[T])
            case l: List[_] => l.flatMap(filterASTElements[T])
            case p: Product => p.productIterator.toList.flatMap(filterASTElements[T])
            case _ => List()
        }
    }


    def checkDefuse(ast: AST, declUseMap: IdentityIdHashMap, useDeclMap: IdentityIdHashMap, fm: FeatureModel = FeatureExprLib.featureModelFactory().empty): (String, Int, Int, Int) = {
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
        val relevantIds: IdentityHashMap[Id, Id] = new IdentityHashMap()
        getAllRelevantIds(ast).foreach(x => {
            relevantIds.put(x, null)
        })

        val missingLB: ListBuffer[Id] = ListBuffer()
        val duplicateLB: ListBuffer[Id] = ListBuffer()
        val allIds: IdentityHashMap[Id, Id] = new IdentityHashMap()
        val defuseKeyList = declUseMap.keySet.toArray().toList

        declUseMap.flatMap(x => x._1 :: x._2).foreach(x => {
            if (allIds.contains(x)) {
                duplicateLB += x
            } else {
                allIds.put(x, null)
            }
        })

        val numberOfIdsInAst = relevantIds.size + numberOfBuiltinFunctions
        val numberOfIdsInDefuse = allIds.keySet().size()

        relevantIds.foreach(x => {
            if (!allIds.containsKey(x._1)) {
                missingLB += x._1
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
        /*if (!missingLB.isEmpty) {
            val ts = new CTypeSystemFrontend(renameMissingIdentifiers(ast, missingLB.toList).asInstanceOf[TranslationUnit], fm)
            if (ts.checkASTSilent) {
                resultString.append("\nAfter renaming missing identifiers the TypeCheck is still valid..")
            } else {
                // TODO
            }
        }*/
        (resultString.toString(), declUseMap.keySet.size, (numberOfIdsInDefuse - declUseMap.keySet.size), duplicateLB.size)
    }
}