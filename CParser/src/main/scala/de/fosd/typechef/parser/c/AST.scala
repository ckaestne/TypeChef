package de.fosd.typechef.parser.c

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser._
import org.kiama.attribution.Attributable

/**
 * AST for C
 */

//Expressions
trait AST extends Attributable with Cloneable {
    override def clone() = super.clone().asInstanceOf[AST]
}

trait Choice[+T <: AST] extends AST {
    def thenBranch: T
    def elseBranch: T
    def feature: FeatureExpr
    override def toString = "Choice(" + feature + "," + thenBranch + "," + elseBranch + ")"
}

trait ASTVisitor {
    def visit(node: AST, feature: FeatureExpr)
    def postVisit(node: AST, feature: FeatureExpr)
}

abstract class Expr extends AST

sealed abstract class PrimaryExpr extends Expr

case class Id(name: String) extends PrimaryExpr

case class Constant(value: String) extends PrimaryExpr

case class StringLit(name: List[Opt[String]]) extends PrimaryExpr

abstract class PostfixSuffix extends AST

case class SimplePostfixSuffix(t: String) extends PostfixSuffix

case class PointerPostfixSuffix(kind: String, id: Id) extends PostfixSuffix {
}

case class FunctionCall(params: ExprList) extends PostfixSuffix {
}

case class ArrayAccess(expr: Expr) extends PostfixSuffix {
}

case class PostfixExpr(p: Expr, s: PostfixSuffix) extends Expr

case class UnaryExpr(kind: String, e: Expr) extends Expr {
}

case class SizeOfExprT(typeName: TypeName) extends Expr {
}

case class SizeOfExprU(expr: Expr) extends Expr {
}

case class CastExpr(typeName: TypeName, expr: Expr) extends Expr {
}

case class PointerDerefExpr(castExpr: Expr) extends Expr

case class PointerCreationExpr(castExpr: Expr) extends Expr

case class UnaryOpExpr(kind: String, castExpr: Expr) extends Expr {
}

case class NAryExpr(e: Expr, others: List[Opt[NArySubExpr]]) extends Expr

case class NArySubExpr(op: String, e: Expr) extends AST

case class ConditionalExpr(condition: Expr, thenExpr: Option[Expr], elseExpr: Expr) extends Expr

case class AssignExpr(target: Expr, operation: String, source: Expr) extends Expr

case class ExprList(exprs: List[Opt[Expr]]) extends Expr

//Statements
abstract sealed class Statement extends AST

case class CompoundStatement(innerStatements: List[Opt[Statement]]) extends Statement {
}

case class EmptyStatement() extends Statement

case class ExprStatement(expr: Expr) extends Statement {
}

case class WhileStatement(expr: Expr, s: Statement) extends Statement {
}

case class DoStatement(expr: Expr, s: Statement) extends Statement {
}

case class ForStatement(expr1: Option[Expr], expr2: Option[Expr], expr3: Option[Expr], s: Statement) extends Statement {
}

case class GotoStatement(target: Expr) extends Statement {
}

case class ContinueStatement() extends Statement

case class BreakStatement() extends Statement

case class ReturnStatement(expr: Option[Expr]) extends Statement {
}

case class LabelStatement(id: Id, attribute: Option[AttributeSpecifier]) extends Statement {
}

case class CaseStatement(c: Expr, s: Option[Statement]) extends Statement {
}

case class DefaultStatement(s: Option[Statement]) extends Statement {
}

case class IfStatement(condition: Expr, thenBranch: Statement, elifs: List[Opt[ElifStatement]], elseBranch: Option[Statement]) extends Statement {
}

case class ElifStatement(condition: Expr, thenBranch: Statement) extends AST {
}

case class SwitchStatement(expr: Expr, s: Statement) extends Statement {
}

abstract sealed class CompoundDeclaration extends Statement

case class DeclarationStatement(decl: Declaration) extends CompoundDeclaration

case class NestedFunctionDef(isAuto: Boolean, specifiers: List[Opt[Specifier]], declarator: Declarator, parameters: List[Opt[Declaration]], stmt: Statement) extends CompoundDeclaration

case class LocalLabelDeclaration(ids: List[Opt[Id]]) extends CompoundDeclaration

case class AltStatement(feature: FeatureExpr, thenBranch: Statement, elseBranch: Statement) extends Statement with Choice[Statement] {
    override def equals(x: Any) = x match {
        case AltStatement(f, t, e) => f.equivalentTo(feature) && (thenBranch == t) && (elseBranch == e)
        case _ => false
    }
}

object AltStatement {
    def join = (f: FeatureExpr, x: Statement, y: Statement) => if (x == y) x else AltStatement(f, x, y)
}

abstract class Specifier() extends AST

abstract sealed class TypeSpecifier() extends Specifier()

abstract sealed class PrimitiveTypeSpecifier() extends TypeSpecifier()

abstract sealed class OtherSpecifier() extends Specifier()


case class OtherPrimitiveTypeSpecifier(typeName: String) extends TypeSpecifier()

case class VoidSpecifier() extends PrimitiveTypeSpecifier()

case class ShortSpecifier() extends PrimitiveTypeSpecifier()

case class IntSpecifier() extends PrimitiveTypeSpecifier()

case class FloatSpecifier() extends PrimitiveTypeSpecifier()

case class DoubleSpecifier() extends PrimitiveTypeSpecifier()

case class LongSpecifier() extends PrimitiveTypeSpecifier()

case class CharSpecifier() extends PrimitiveTypeSpecifier()

case class TypedefSpecifier() extends Specifier()

case class TypeDefTypeSpecifier(name: Id) extends TypeSpecifier()

case class SignedSpecifier() extends TypeSpecifier()

case class UnsignedSpecifier() extends TypeSpecifier()


case class InlineSpecifier() extends OtherSpecifier()

case class AutoSpecifier() extends OtherSpecifier()

case class RegisterSpecifier() extends OtherSpecifier()

case class VolatileSpecifier() extends OtherSpecifier()

case class ExternSpecifier() extends OtherSpecifier()

case class ConstSpecifier() extends OtherSpecifier()

case class RestrictSpecifier() extends OtherSpecifier()

case class StaticSpecifier() extends OtherSpecifier()


abstract class Attribute() extends AST

case class AtomicAttribute(n: String) extends Attribute

case class AttributeSequence(attributes: List[Opt[Attribute]]) extends AST {
}

case class CompoundAttribute(inner: List[Opt[AttributeSequence]]) extends Attribute {
}

case class Declaration(declSpecs: List[Opt[Specifier]], init: List[Opt[InitDeclarator]]) extends ExternalDef with OldParameterDeclaration


abstract class InitDeclarator(val declarator: Declarator, val attributes: List[Opt[AttributeSpecifier]]) extends AST {def getName = declarator.getName;}

case class InitDeclaratorI(override val declarator: Declarator, override val attributes: List[Opt[AttributeSpecifier]], i: Option[Initializer]) extends InitDeclarator(declarator, attributes) {
}

case class InitDeclaratorE(override val declarator: Declarator, override val attributes: List[Opt[AttributeSpecifier]], e: Expr) extends InitDeclarator(declarator, attributes) {
}


/**
 * A declaration has two parts
 *   specifier+ declarator+
 * The specifier describes the basic type (which is modified by information in the declarator)
 *
 * A declarator is either an atomic declarator with a name, pointers and extensions or
 * a nested declarator.
 *
 * All declarators are available also as AbstractDeclarators, which do not have a name and
 * do not support the DeclIdentifierList extension
 *
 */


sealed abstract class AbstractDeclarator(val pointers: List[Opt[Pointer]], val extensions: List[Opt[DeclaratorAbstrExtension]]) extends AST

sealed abstract class Declarator(val pointers: List[Opt[Pointer]], val extensions: List[Opt[DeclaratorExtension]]) extends AST {def getName: String}


case class AtomicNamedDeclarator(override val pointers: List[Opt[Pointer]], id: Id, override val extensions: List[Opt[DeclaratorExtension]]) extends Declarator(pointers, extensions) {def getName = id.name}

case class NestedNamedDeclarator(override val pointers: List[Opt[Pointer]], nestedDecl: Declarator, override val extensions: List[Opt[DeclaratorExtension]]) extends Declarator(pointers, extensions) {def getName = nestedDecl.getName}

case class AtomicAbstractDeclarator(override val pointers: List[Opt[Pointer]], override val extensions: List[Opt[DeclaratorAbstrExtension]]) extends AbstractDeclarator(pointers, extensions)

case class NestedAbstractDeclarator(override val pointers: List[Opt[Pointer]], nestedDecl: AbstractDeclarator, override val extensions: List[Opt[DeclaratorAbstrExtension]]) extends AbstractDeclarator(pointers, extensions)


sealed abstract class DeclaratorExtension extends AST

sealed abstract class DeclaratorAbstrExtension extends DeclaratorExtension

case class DeclIdentifierList(idList: List[Opt[Id]]) extends DeclaratorExtension

case class DeclParameterDeclList(parameterDecls: List[Opt[ParameterDeclaration]]) extends DeclaratorAbstrExtension

case class DeclArrayAccess(expr: Option[Expr]) extends DeclaratorAbstrExtension

//
//sealed abstract class Declarator(val pointer: List[Opt[Pointer]], val extensions: List[Opt[DeclaratorExtension]]) extends AST {
//    def getName: String
//}
//
//case class DeclaratorId(override val pointer: List[Opt[Pointer]], id: Id, override val extensions: List[Opt[DeclaratorExtension]]) extends Declarator(pointer, extensions) {
//    def getName = id.name
//}
//
//case class DeclaratorDecl(override val pointer: List[Opt[Pointer]], attrib: Option[AttributeSpecifier], decl: Declarator,override val  extensions: List[Opt[DeclaratorExtension]]) extends Declarator(pointer, extensions) {
//
//}


case class Initializer(initializerElementLabel: List[Opt[InitializerElementLabel]], expr: Expr) extends AST {
}

case class Pointer(specifier: List[Opt[Specifier]]) extends AST {
}

abstract class ParameterDeclaration(val specifiers: List[Opt[Specifier]]) extends AST

case class PlainParameterDeclaration(override val specifiers: List[Opt[Specifier]]) extends ParameterDeclaration(specifiers) {
}

case class ParameterDeclarationD(override val specifiers: List[Opt[Specifier]], decl: Declarator) extends ParameterDeclaration(specifiers) {
}

case class ParameterDeclarationAD(override val specifiers: List[Opt[Specifier]], decl: AbstractDeclarator) extends ParameterDeclaration(specifiers) {
}

trait OldParameterDeclaration extends AST

case class VarArgs() extends ParameterDeclaration(List()) with OldParameterDeclaration

case class EnumSpecifier(id: Option[Id], enumerators: List[Opt[Enumerator]]) extends TypeSpecifier {
}

case class Enumerator(id: Id, assignment: Option[Expr]) extends AST {
}

case class StructOrUnionSpecifier(kind: String, id: Option[Id], enumerators: List[Opt[StructDeclaration]]) extends TypeSpecifier {
}

case class StructDeclaration(qualifierList: List[Opt[Specifier]], declaratorList: List[Opt[StructDecl]]) extends AST {
}

sealed abstract class StructDecl extends AST

case class StructDeclarator(decl: Declarator, initializer: Option[Expr], attributes: List[Opt[AttributeSpecifier]]) extends StructDecl

case class StructInitializer(expr: Expr, attributes: List[Opt[AttributeSpecifier]]) extends StructDecl

case class AsmExpr(isVolatile: Boolean, expr: Expr) extends AST with ExternalDef {
}

case class FunctionDef(specifiers: List[Opt[Specifier]], declarator: Declarator, oldStyleParameters: List[Opt[OldParameterDeclaration]], stmt: Statement) extends AST with ExternalDef {
    def getName = declarator.getName
}

trait ExternalDef extends AST

case class EmptyExternalDef() extends ExternalDef

case class TypelessDeclaration(declList: List[Opt[InitDeclarator]]) extends ExternalDef {
}

case class AltExternalDef(feature: FeatureExpr, thenBranch: ExternalDef, elseBranch: ExternalDef) extends ExternalDef with Choice[ExternalDef] {
    override def equals(x: Any) = x match {
        case AltExternalDef(f, t, e) => f.equivalentTo(feature) && (thenBranch == t) && (elseBranch == e)
        case _ => false
    }
}

object AltExternalDef {
    def join = (f: FeatureExpr, x: ExternalDef, y: ExternalDef) => if (x == y) x else AltExternalDef(f, x, y)
}

case class TypeName(specifiers: List[Opt[Specifier]], decl: Option[AbstractDeclarator]) extends AST {
}

case class TranslationUnit(defs: List[Opt[ExternalDef]]) extends AST {
}

//GnuC stuff here:
abstract class AttributeSpecifier() extends Specifier()

case class GnuAttributeSpecifier(attributeList: List[Opt[AttributeSequence]]) extends AttributeSpecifier {
}

case class AsmAttributeSpecifier(stringConst: StringLit) extends AttributeSpecifier {
}

case class LcurlyInitializer(inits: List[Opt[Initializer]]) extends Expr {
}

case class AlignOfExprT(typeName: TypeName) extends Expr {
}

case class AlignOfExprU(expr: Expr) extends Expr {
}

case class GnuAsmExpr(isVolatile: Boolean, expr: StringLit, stuff: Any) extends Expr {
}

case class RangeExpr(from: Expr, to: Expr) extends Expr {
}


case class TypeOfSpecifierT(typeName: TypeName) extends TypeSpecifier {
}

case class TypeOfSpecifierU(expr: Expr) extends TypeSpecifier {
}


abstract class InitializerElementLabel() extends AST

case class InitializerArrayDesignator(expr: Expr) extends InitializerElementLabel {
}

case class InitializerDesignator(id: Id) extends InitializerElementLabel {
}

case class BuildinOffsetof(typeName: TypeName, offsetofMemberDesignator: List[Opt[OffsetofMemberDesignator]]) extends PrimaryExpr {
}

abstract class OffsetofMemberDesignator() extends AST

case class OffsetofMemberDesignatorID(id: Id) extends OffsetofMemberDesignator

case class OffsetofMemberDesignatorExpr(expr: Expr) extends OffsetofMemberDesignator

case class BuiltinTypesCompatible(typeName1: TypeName, typeName2: TypeName) extends PrimaryExpr {
}

case class BuiltinVaArgs(expr: Expr, typeName: TypeName) extends PrimaryExpr {
}

case class CompoundStatementExpr(compoundStatement: CompoundStatement) extends PrimaryExpr {
}

case class Pragma(command: StringLit) extends ExternalDef {
}

