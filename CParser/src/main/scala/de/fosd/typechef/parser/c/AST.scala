package de.fosd.typechef.parser.c

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser._

//Expressions
trait AST {
    def accept(visitor: ASTVisitor) { accept(visitor, FeatureExpr.base) }
    def accept(visitor: ASTVisitor, feature: FeatureExpr) {
        visitor.visit(this, feature)
        for (a <- getInnerOpt)
            a.entry.accept(visitor, feature.and(a.feature))
        visitor.postVisit(this, feature)
    }
    protected def getInnerOpt: List[Opt[AST]] = getInner.map(Opt(FeatureExpr.base, _))
    protected def getInner: List[AST] = List()
}

trait ASTVisitor {
    def visit(node: AST, feature: FeatureExpr)
    def postVisit(node: AST, feature: FeatureExpr)
}

abstract class Expr extends AST
abstract class PrimaryExpr extends Expr
case class Id(name: String) extends PrimaryExpr
case class Constant(value: String) extends PrimaryExpr
case class StringLit(name: List[Opt[String]]) extends PrimaryExpr

abstract class PostfixSuffix extends AST
case class SimplePostfixSuffix(t: String) extends PostfixSuffix
case class PointerPostfixSuffix(kind: String, id: Id) extends PostfixSuffix {
    override def getInner = List(id)
}
case class FunctionCall(params: ExprList) extends PostfixSuffix {
    override def getInner = List(params)
}
case class ArrayAccess(expr: Expr) extends PostfixSuffix {
    override def getInner = List(expr)
}

case class PostfixExpr(p: Expr, s: List[Opt[PostfixSuffix]]) extends Expr {
    override def getInnerOpt = super.getInnerOpt ++ s
    override def getInner = List(p)
}
case class UnaryExpr(kind: String, e: Expr) extends Expr {
    override def getInner = List(e)
}
case class SizeOfExprT(typeName: TypeName) extends Expr {
    override def getInner = List(typeName)
}
case class SizeOfExprU(expr: Expr) extends Expr {
    override def getInner = List(expr)
}
case class CastExpr(typeName: TypeName, expr: Expr) extends Expr {
    override def getInner = List(typeName, expr)
}
case class UCastExpr(kind: String, castExpr: Expr) extends Expr {
    override def getInner = List(castExpr)
}

case class NAryExpr(e: Expr, others: List[Opt[(String, Expr)]]) extends Expr {
    override def getInnerOpt = super.getInnerOpt ++ others.map(o => Opt(o.feature, o.entry._2))
    override def getInner = List(e)
}
case class ConditionalExpr(condition: Expr, thenExpr: Option[Expr], elseExpr: Expr) extends Expr {
    override def getInner = List(condition) ++ thenExpr.toList ++ List(elseExpr)
}
case class AssignExpr(target: Expr, operation: String, source: Expr) extends Expr {
    override def getInner = List(target, source)
}
case class ExprList(exprs: List[Opt[Expr]]) extends Expr {
    override def getInnerOpt = exprs
}

//Statements
abstract class Statement extends AST
case class CompoundStatement(innerStatements: List[Opt[Statement]]) extends Statement {
    override def getInnerOpt = innerStatements
}
case class EmptyStatement() extends Statement
case class ExprStatement(expr: Expr) extends Statement {
    override def getInner = List(expr)
}
case class WhileStatement(expr: Expr, s: Statement) extends Statement {
    override def getInner = List(expr, s)
}
case class DoStatement(expr: Expr, s: Statement) extends Statement {
    override def getInner = List(expr, s)
}
case class ForStatement(expr1: Option[Expr], expr2: Option[Expr], expr3: Option[Expr], s: Statement) extends Statement {
    override def getInner = expr1.toList ++ expr2.toList ++ expr3.toList ++ List(s)
}
case class GotoStatement(target: Expr) extends Statement {
    override def getInner = List(target)
}
case class ContinueStatement() extends Statement
case class BreakStatement() extends Statement
case class ReturnStatement(expr: Option[Expr]) extends Statement {
    override def getInner = expr.toList
}
case class LabelStatement(id: Id) extends Statement {
    override def getInner = List(id)
}
case class CaseStatement(c: Expr, s: Option[Statement]) extends Statement {
    override def getInner = List(c) ++ s.toList
}
case class DefaultStatement(s: Option[Statement]) extends Statement {
    override def getInner = s.toList
}
case class IfStatement(condition: Expr, thenBranch: Statement, elseBranch: Option[Statement]) extends Statement {
    override def getInner = List(condition, thenBranch) ++ elseBranch.toList
}
case class SwitchStatement(expr: Expr, s: Statement) extends Statement {
    override def getInner = List(expr, s)
}
case class DeclarationStatement(decl: Declaration) extends Statement {
    override def getInner = List(decl)
}
case class AltStatement(feature: FeatureExpr, thenBranch: Statement, elseBranch: Statement) extends Statement {
    override def getInnerOpt = List(Opt(feature, thenBranch), Opt(feature.not, elseBranch))
}
object AltStatement {
    def join = (f: FeatureExpr, x: Statement, y: Statement) => if (x == y) x else AltStatement(f, x, y)
}

abstract class Specifier extends AST
abstract class TypeSpecifier extends Specifier
case class PrimitiveTypeSpecifier(typeName: String) extends TypeSpecifier
case class TypeDefTypeSpecifier(name: Id) extends TypeSpecifier {
    override def getInner = List(name)
}
case class TypedefSpecifier() extends Specifier
case class OtherSpecifier(name: String) extends Specifier

abstract class Attribute extends AST
case class AtomicAttribute(n: String) extends Attribute
case class CompoundAttribute(inner: List[List[Attribute]]) extends Attribute {
    override def getInner = inner.flatten
}

trait Declaration extends AST with ExternalDef
case class ADeclaration(declSpecs: List[Opt[Specifier]], init: Option[List[Opt[InitDeclarator]]]) extends Declaration {
    override def getInnerOpt = declSpecs ++ init.toList.flatten
}
case class AltDeclaration(feature: FeatureExpr, thenBranch: Declaration, elseBranch: Declaration) extends Declaration {
    override def getInnerOpt = List(Opt(feature, thenBranch), Opt(feature.not, elseBranch))
}
object AltDeclaration {
    def join = (f: FeatureExpr, x: Declaration, y: Declaration) => if (x == y) x else AltDeclaration(f, x, y)
}

abstract class InitDeclarator(val declarator: Declarator, val attributes: List[Opt[Specifier]]) extends AST
case class InitDeclaratorI(override val declarator: Declarator, override val attributes: List[Opt[Specifier]], i: Option[Initializer]) extends InitDeclarator(declarator, attributes) {
    override def getInnerOpt = super.getInnerOpt ++ attributes
    override def getInner = List(declarator) ++ i.toList
}
case class InitDeclaratorE(override val declarator: Declarator, override val attributes: List[Opt[Specifier]], e: Expr) extends InitDeclarator(declarator, attributes) {
    override def getInnerOpt = super.getInnerOpt ++ attributes
    override def getInner = List(declarator) ++ List(e)
}

abstract class Declarator(pointer: List[Opt[Pointer]], extensions: List[Opt[DeclaratorExtension]]) extends AST {
    def getName: String
}
case class DeclaratorId(pointer: List[Opt[Pointer]], id: Id, extensions: List[Opt[DeclaratorExtension]]) extends Declarator(pointer, extensions) {
    def getName = id.name
    override def getInnerOpt = super.getInnerOpt ++ extensions ++ pointer
    override def getInner = List(id)
}
case class DeclaratorDecl(pointer: List[Opt[Pointer]], attrib: Option[AttributeSpecifier], decl: Declarator, extensions: List[Opt[DeclaratorExtension]]) extends Declarator(pointer, extensions) {
    def getName = decl.getName
}
abstract class DeclaratorExtension extends AST
case class DeclIdentifierList(idList: List[Opt[Id]]) extends DeclaratorExtension {
    override def getInnerOpt = idList
}
case class DeclParameterTypeList(parameterTypes: List[Opt[ParameterDeclaration]]) extends DeclaratorExtension with DirectAbstractDeclarator {
    override def getInnerOpt = parameterTypes
}
case class DeclArrayAccess(expr: Option[Expr]) extends DeclaratorExtension with DirectAbstractDeclarator {
    override def getInner = expr.toList
}

trait DirectAbstractDeclarator extends AST
case class AbstractDeclarator(pointer: List[Opt[Pointer]], extensions: List[Opt[DirectAbstractDeclarator]]) extends AST with DirectAbstractDeclarator {
    override def getInnerOpt = pointer ++ extensions
}

case class Initializer(initializerElementLabel: Option[InitializerElementLabel], expr: Expr) extends AST {
    override def getInner = initializerElementLabel.toList ++ List(expr)
}

case class Pointer(specifier: List[Opt[Specifier]]) extends AST {
    override def getInnerOpt = specifier
}
class ParameterDeclaration(val specifiers: List[Opt[Specifier]]) extends AST
case class PlainParameterDeclaration(override val specifiers: List[Opt[Specifier]]) extends ParameterDeclaration(specifiers) {
    override def getInnerOpt = specifiers
}
case class ParameterDeclarationD(override val specifiers: List[Opt[Specifier]], decl: Declarator) extends ParameterDeclaration(specifiers) {
    override def getInnerOpt = super.getInnerOpt ++ specifiers
    override def getInner = List(decl)
}
case class ParameterDeclarationAD(override val specifiers: List[Opt[Specifier]], decl: AbstractDeclarator) extends ParameterDeclaration(specifiers) {
    override def getInnerOpt = super.getInnerOpt ++ specifiers
    override def getInner = List(decl)
}
case class VarArgs() extends ParameterDeclaration(List()) with Declaration

case class EnumSpecifier(id: Option[Id], enumerators: List[Opt[Enumerator]]) extends TypeSpecifier {
    override def getInnerOpt = super.getInnerOpt ++ enumerators
    override def getInner = id.toList
}
case class Enumerator(id: Id, assignment: Option[Expr]) extends AST {
    override def getInner = List(id) ++ assignment.toList
}
case class StructOrUnionSpecifier(kind: String, id: Option[Id], enumerators: List[Opt[StructDeclaration]]) extends TypeSpecifier {
    override def getInnerOpt = super.getInnerOpt ++ enumerators
    override def getInner = id.toList
}
case class StructDeclaration(qualifierList: List[Opt[Specifier]], declaratorList: List[Opt[StructDeclarator]]) extends AST {
    override def getInnerOpt = qualifierList ++ declaratorList
}
case class StructDeclarator(declarator: Option[Declarator], expr: Option[Expr], attributes: List[Opt[Specifier]]) extends AST {
    override def getInnerOpt = super.getInnerOpt ++ attributes
    override def getInner = declarator.toList ++ expr.toList
}

case class AsmExpr(isVolatile: Boolean, expr: Expr) extends AST with ExternalDef {
    override def getInner = List(expr)
}

case class FunctionDef(specifiers: List[Opt[Specifier]], declarator: Declarator, parameters: List[Opt[Declaration]], stmt: Statement) extends AST with ExternalDef {
    override def getInnerOpt = super.getInnerOpt ++ specifiers ++ parameters
    override def getInner = List(declarator) ++ List(stmt)
}
trait ExternalDef extends AST
case class EmptyExternalDef() extends ExternalDef
case class TypelessDeclaration(declList: List[Opt[InitDeclarator]]) extends ExternalDef {
    override def getInnerOpt = declList
}
case class AltExternalDef(feature: FeatureExpr, thenBranch: ExternalDef, elseBranch: ExternalDef) extends ExternalDef {
    override def getInnerOpt = List(Opt(feature, thenBranch), Opt(feature.not, elseBranch))
}
object AltExternalDef {
    def join = (f: FeatureExpr, x: ExternalDef, y: ExternalDef) => if (x == y) x else AltExternalDef(f, x, y)
}

case class TypeName(specifiers: List[Opt[Specifier]], decl: Option[AbstractDeclarator]) extends AST {
    override def getInnerOpt = super.getInnerOpt ++ specifiers
    override def getInner = decl.toList
}

case class TranslationUnit(defs: List[Opt[ExternalDef]]) extends AST {
    override def getInnerOpt = defs
}

//GnuC stuff here:
class AttributeSpecifier extends Specifier
case class GnuAttributeSpecifier(attributeList: List[List[Attribute]]) extends AttributeSpecifier {
    override def getInner = attributeList.flatten
}
case class AsmAttributeSpecifier(stringConst: StringLit) extends AttributeSpecifier {
    override def getInner = List(stringConst)
}
case class LcurlyInitializer(inits: List[Opt[Initializer]]) extends Expr {
    override def getInnerOpt = inits
}
case class AlignOfExprT(typeName: TypeName) extends Expr {
    override def getInner = List(typeName)
}
case class AlignOfExprU(expr: Expr) extends Expr {
    override def getInner = List(expr)
}
case class GnuAsmExpr(isVolatile: Boolean, expr: StringLit, stuff: Any) extends Expr {
    override def getInner = List(expr)
}
case class RangeExpr(from: Expr, to: Expr) extends Expr {
    override def getInner = List(from, to)
}
case class NestedFunctionDef(isAuto: Boolean, specifiers: List[Opt[Specifier]], declarator: Declarator, parameters: List[Opt[Declaration]], stmt: Statement) extends Declaration {
    override def getInnerOpt = super.getInnerOpt ++ specifiers ++ parameters
    override def getInner = List(declarator) ++ List(stmt)
}
case class TypeOfSpecifierT(typeName: TypeName) extends TypeSpecifier {
    override def getInner = List(typeName)
}
case class TypeOfSpecifierU(expr: Expr) extends TypeSpecifier {
    override def getInner = List(expr)
}
case class LocalLabelDeclaration(ids: List[Opt[Id]]) extends Declaration {
    override def getInnerOpt = ids
}
abstract class InitializerElementLabel() extends AST
case class InitializerElementLabelExpr(expr: Expr, isAssign: Boolean) extends InitializerElementLabel {
    override def getInner = List(expr)
}
case class InitializerElementLabelColon(id: Id) extends InitializerElementLabel {
    override def getInner = List(id)
}
case class InitializerElementLabelDotAssign(id: Id) extends InitializerElementLabel {
    override def getInner = List(id)
}
case class BuildinOffsetof(typeName: TypeName, offsetofMemberDesignator: List[Opt[Id]]) extends PrimaryExpr {
    override def getInner = List(typeName)
    override def getInnerOpt = super.getInnerOpt ++ offsetofMemberDesignator
}
case class BuiltinTypesCompatible(typeName1: TypeName, typeName2: TypeName) extends PrimaryExpr {
    override def getInner = List(typeName1, typeName2)
}
case class CompoundStatementExpr(compoundStatement: CompoundStatement) extends PrimaryExpr {
    override def getInner = List(compoundStatement)
}

