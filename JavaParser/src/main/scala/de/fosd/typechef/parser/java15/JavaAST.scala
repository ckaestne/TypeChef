package de.fosd.typechef.parser.java15

import de.fosd.typechef.conditional._
import de.fosd.typechef.error.WithPosition

/**
 * an incomplete AST for Java
 */

trait AST extends Product with Cloneable with WithPosition {
    override def clone(): AST.this.type = super.clone().asInstanceOf[AST.this.type]
}


case class JCompilationUnit(packageDecl: Conditional[Option[JPackageDecl]], imports: List[Opt[JImport]], typeDecl: List[Opt[JTypeDecl]]) extends AST

case class JPackageDecl(name: JName) extends AST

case class JName(name: List[Opt[JId]]) extends AST

case class JId(name: String) extends AST

case class JImport(isStatic: Boolean, name: JName, dotStar: Boolean) extends AST


trait JTypeDecl extends AST

case class JEmptyTypeDecl() extends JTypeDecl

case class JClassOrInterfaceDecl(
                                    mod: List[Opt[JModifier]],
                                    isInterface: Boolean,
                                    name: JId,
                                    typeParameter: Any,
                                    extendsList: List[Opt[JClassOrInterfaceType]],
                                    implementsList: List[Opt[JClassOrInterfaceType]],
                                    members: List[Opt[JBodyDeclaration]]) extends JTypeDecl with JBodyDeclaration

case class JEnumDecl(mod: List[Opt[JModifier]], name: JId, implementsList: List[Opt[JClassOrInterfaceType]], enumBody: Any) extends JTypeDecl with JBodyDeclaration

case class JAnnotationTypeDecl(mod: List[Opt[JModifier]], cl: Any) extends JTypeDecl


trait JBodyDeclaration extends AST

case class JEmptyBodyDecl() extends JBodyDeclaration

case class JInitializer(isStatic: Boolean, block: JBlock) extends JBodyDeclaration

case class JConstructorDecl(mod: List[Opt[JModifier]], typeParameters: List[Opt[Any]], id: JId, params: List[Opt[Any]], exceptions: List[Opt[JName]], explConstructorInvoc: Any, stmts: List[Opt[Any]]) extends JBodyDeclaration

case class JFieldDecl(mod: List[Opt[JModifier]], typ: JType, vars: List[Opt[JVariableDeclarator]]) extends JBodyDeclaration

case class JMethodDecl(mod: List[Opt[JModifier]], typeParameters: List[Opt[Any]], resultType: JType, name: JId, params: List[Opt[Any]], arrays: Int, exceptions: List[Opt[JName]], body: Option[JBlock]) extends JBodyDeclaration

trait JModifier extends AST

case class JAtomicModifier(s: String) extends JModifier

case class JAnnotation(s: Any) extends JModifier


case class JClassOrInterfaceType(t: JParamType, inner: List[JParamType]) extends AST

case class JParamType(id: JId, typeArguments: Option[Any]) extends AST


case class JBlock(statements: List[Opt[Any]]) extends AST


case class JType(any: Any) extends AST


case class JVariableDeclarator(id: JId, arrays: Int, initializer: Option[Any])