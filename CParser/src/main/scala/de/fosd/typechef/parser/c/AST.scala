package de.fosd.typechef.parser.c

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser._

//Expressions
trait AST {
    def accept(visitor: ASTVisitor) {
        visitor.visit(this)
        for (a <- getInner)
            a.accept(visitor)
        visitor.postVisit(this)
    }
    protected def getInner: List[AST] = List()
}

trait ASTVisitor {
    def visit(node: AST)
    def postVisit(node: AST)
}

abstract class Expr extends AST
abstract class PrimaryExpr extends Expr
case class Id(name: String) extends PrimaryExpr
case class Constant(value: String) extends PrimaryExpr
case class StringLit(name: List[String]) extends PrimaryExpr

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

case class PostfixExpr(p: Expr, s: List[PostfixSuffix]) extends Expr {
    override def getInner = List(p) ++ s
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

case class NAryExpr(e: Expr, others: List[(String, Expr)]) extends Expr {
    override def getInner = List(e) ++ others.map(_._2)
}
case class ConditionalExpr(condition: Expr, thenExpr: Option[Expr], elseExpr: Expr) extends Expr {
    override def getInner = List(condition) ++ thenExpr.toList ++ List(elseExpr)
}
case class AssignExpr(target: Expr, operation: String, source: Expr) extends Expr {
    override def getInner = List(target, source)
}
case class ExprList(exprs: List[Expr]) extends Expr {
    override def getInner = exprs
}

//Statements
abstract class Statement extends AST
case class CompoundStatement(decl: List[Opt[Declaration]], innerStatements: List[Opt[Statement]]) extends Statement {
    override def getInner = decl.map(_.entry) ++ innerStatements.map(_.entry)
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
case class AltStatement(feature: FeatureExpr, thenBranch: Statement, elseBranch: Statement) extends Statement {
    override def getInner = List(thenBranch, elseBranch)
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
case class ADeclaration(declSpecs: List[Specifier], init: Option[List[InitDeclarator]]) extends Declaration {
    override def getInner = declSpecs ++ init.toList.flatten
}
case class AltDeclaration(feature: FeatureExpr, thenBranch: Declaration, elseBranch: Declaration) extends Declaration {
    override def getInner = List(thenBranch, elseBranch)
}
object AltDeclaration {
    def join = (f: FeatureExpr, x: Declaration, y: Declaration) => if (x == y) x else AltDeclaration(f, x, y)
}

abstract class InitDeclarator(val declarator: Declarator, val attributes: List[Specifier]) extends AST
case class InitDeclaratorI(override val declarator: Declarator, override val attributes: List[Specifier], i: Option[Initializer]) extends InitDeclarator(declarator, attributes) {
    override def getInner = List(declarator) ++ attributes ++ i.toList
}
case class InitDeclaratorE(override val declarator: Declarator, override val attributes: List[Specifier], e: Expr) extends InitDeclarator(declarator, attributes) {
    override def getInner = List(declarator) ++ attributes ++ List(e)
}

abstract class Declarator(pointer: List[Pointer], extensions: List[DeclaratorExtension]) extends AST {
    def getName: String
}
case class DeclaratorId(pointer: List[Pointer], id: Id, extensions: List[DeclaratorExtension]) extends Declarator(pointer, extensions) {
    def getName = id.name
    override def getInner = pointer ++ List(id) ++ extensions
}
case class DeclaratorDecl(pointer: List[Pointer], attrib: Option[AttributeSpecifier], decl: Declarator, extensions: List[DeclaratorExtension]) extends Declarator(pointer, extensions) {
    def getName = decl.getName
}
abstract class DeclaratorExtension extends AST
case class DeclIdentifierList(idList: List[Id]) extends DeclaratorExtension {
    override def getInner = idList
}
case class DeclParameterTypeList(parameterTypes: List[ParameterDeclaration]) extends DeclaratorExtension with DirectAbstractDeclarator {
    override def getInner = parameterTypes
}
case class DeclArrayAccess(expr: Option[Expr]) extends DeclaratorExtension with DirectAbstractDeclarator {
    override def getInner = expr.toList
}

trait DirectAbstractDeclarator extends AST
case class AbstractDeclarator(pointer: List[Pointer], extensions: List[DirectAbstractDeclarator]) extends AST with DirectAbstractDeclarator {
    override def getInner = pointer ++ extensions
}

case class Initializer(initializerElementLabel: Option[InitializerElementLabel], expr: Expr) extends AST {
    override def getInner = initializerElementLabel.toList ++ List(expr)
}

case class Pointer(specifier: List[Specifier]) extends AST {
    override def getInner = specifier
}
class ParameterDeclaration(val specifiers: List[Specifier]) extends AST
case class PlainParameterDeclaration(override val specifiers: List[Specifier]) extends ParameterDeclaration(specifiers) {
    override def getInner = specifiers
}
case class ParameterDeclarationD(override val specifiers: List[Specifier], decl: Declarator) extends ParameterDeclaration(specifiers) {
    override def getInner = specifiers ++ List(decl)
}
case class ParameterDeclarationAD(override val specifiers: List[Specifier], decl: AbstractDeclarator) extends ParameterDeclaration(specifiers) {
    override def getInner = specifiers ++ List(decl)
}
case class VarArgs() extends ParameterDeclaration(List()) with Declaration

case class EnumSpecifier(id: Option[Id], enumerators: List[Enumerator]) extends TypeSpecifier {
    override def getInner = id.toList ++ enumerators
}
case class Enumerator(id: Id, assignment: Option[Expr]) extends AST {
    override def getInner = List(id) ++ assignment.toList
}
case class StructOrUnionSpecifier(kind: String, id: Option[Id], enumerators: List[StructDeclaration]) extends TypeSpecifier {
    override def getInner = id.toList ++ enumerators
}
case class StructDeclaration(qualifierList: List[Specifier], declaratorList: List[StructDeclarator]) extends AST {
    override def getInner = qualifierList ++ declaratorList
}
case class StructDeclarator(declarator: Option[Declarator], expr: Option[Expr], attributes: List[Specifier]) extends AST {
    override def getInner = declarator.toList ++ expr.toList ++ attributes
}

case class AsmExpr(isVolatile: Boolean, expr: Expr) extends AST with ExternalDef {
    override def getInner = List(expr)
}

case class FunctionDef(specifiers: List[Specifier], declarator: Declarator, parameters: List[Declaration], stmt: Statement) extends AST with ExternalDef {
    override def getInner = specifiers ++ List(declarator) ++ parameters ++ List(stmt)
}
trait ExternalDef extends AST
case class EmptyExternalDef() extends ExternalDef
case class TypelessDeclaration(declList: List[InitDeclarator]) extends ExternalDef {
    override def getInner = declList
}
case class AltExternalDef(feature: FeatureExpr, thenBranch: ExternalDef, elseBranch: ExternalDef) extends ExternalDef {
    override def getInner = List(thenBranch, elseBranch)
}
object AltExternalDef {
    def join = (f: FeatureExpr, x: ExternalDef, y: ExternalDef) => if (x == y) x else AltExternalDef(f, x, y)
}

case class TypeName(specifiers: List[Specifier], decl: Option[AbstractDeclarator]) extends AST {
    override def getInner = specifiers ++ decl.toList
}

//GnuC stuff here:
class AttributeSpecifier extends Specifier
case class GnuAttributeSpecifier(attributeList: List[List[Attribute]]) extends AttributeSpecifier {
    override def getInner = attributeList.flatten
}
case class AsmAttributeSpecifier(stringConst: StringLit) extends AttributeSpecifier {
    override def getInner = List(stringConst)
}
case class LcurlyInitializer(inits: List[Initializer]) extends Expr {
    override def getInner = inits
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
case class NestedFunctionDef(isAuto: Boolean, specifiers: List[Specifier], declarator: Declarator, parameters: List[Declaration], stmt: Statement) extends Declaration {
    override def getInner = specifiers ++ List(declarator) ++ parameters ++ List(stmt)
}
case class TypeOfSpecifierT(typeName: TypeName) extends TypeSpecifier {
    override def getInner = List(typeName)
}
case class TypeOfSpecifierU(expr: Expr) extends TypeSpecifier {
    override def getInner = List(expr)
}
case class LocalLabelDeclaration(ids: List[Id]) extends Declaration {
    override def getInner = ids
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
case class BuildinOffsetof(typeName: TypeName, offsetofMemberDesignator: List[Id]) extends PrimaryExpr {
    override def getInner = List(typeName) ++ offsetofMemberDesignator
}
case class CompoundStatementExpr(compoundStatement: CompoundStatement) extends PrimaryExpr {
    override def getInner = List(compoundStatement)
}

