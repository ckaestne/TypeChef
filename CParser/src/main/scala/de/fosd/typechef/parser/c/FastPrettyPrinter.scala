package de.fosd.typechef.parser.c

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import java.io.Writer

class FastPrettyPrinter(w: => Writer) {

  var indentation = 0

  private def newLinePlusIndentation = "\n" + (" " * indentation)

  def pretty(ast: AST, list_feature_expr: List[FeatureExpr] = List(FeatureExprFactory.True)): Unit = {
    prettyPrint(ast, list_feature_expr)
    w.flush()
  }

  private def ppConditional(e: Conditional[_], list_feature_expr: List[FeatureExpr]): Unit = {
    e match {
      case One(c: AST) => prettyPrint(c, list_feature_expr)
      case Choice(f, a: Conditional[_], b: Conditional[_]) => {
        w.append(newLinePlusIndentation)
        w.append("#if ")
        w.append(f.toTextExpr)
        w.append(newLinePlusIndentation)
        ppConditional(a, f :: list_feature_expr)
        w.append(newLinePlusIndentation)
        w.append("#else")
        w.append(newLinePlusIndentation)
        ppConditional(b, f.not :: list_feature_expr)
        w.append(newLinePlusIndentation)
        w.append("#endif")
        w.append(newLinePlusIndentation)
      }
    }
  }

  private def optConditional(e: Opt[AST], list_feature_expr: List[FeatureExpr]) = {
    if (e.feature == FeatureExprFactory.True ||
      list_feature_expr.foldLeft(FeatureExprFactory.True)(_ and _).implies(e.feature).isTautology())
      prettyPrint(e.entry, list_feature_expr)
    else {
      w.append(newLinePlusIndentation)
      w.append("#if ")
      w.append(e.feature.toTextExpr)
      w.append(newLinePlusIndentation)
      prettyPrint(e.entry, e.feature :: list_feature_expr)
      w.append(newLinePlusIndentation)
      w.append("#endif")
      w.append(newLinePlusIndentation)
    }
  }

  private def optString(e: Opt[String], list_feature_expr: List[FeatureExpr]) = {
    if (e.feature == FeatureExprFactory.True ||
      list_feature_expr.foldLeft(FeatureExprFactory.True)(_ and _).implies(e.feature).isTautology())
      w.append(e.entry)
    else {
      w.append(newLinePlusIndentation)
      w.append("#if ")
      w.append(e.feature.toTextExpr)
      w.append(newLinePlusIndentation)
      w.append(e.entry)
      w.append(newLinePlusIndentation)
      w.append("#endif")
      w.append(newLinePlusIndentation)
    }
  }

  private def prettyPrint(ast: AST, list_feature_expr: List[FeatureExpr] = List(FeatureExprFactory.True)) {
    implicit def pretty(a: AST) { prettyPrint(a, list_feature_expr) }
    implicit def prettyOpt(a: Opt[AST]) = optConditional(a, list_feature_expr)
    implicit def prettyOptString(a: Opt[String]) = optString(a, list_feature_expr)
    implicit def prettyCond(a: Conditional[_]) { ppConditional(a, list_feature_expr) }
    implicit def prettyOptStr(a: Opt[String]) = w.append(a.entry)

    def sep(l: List[Opt[AST]], ad: String) {
      l match {
        case Nil =>
        case x :: Nil => prettyOpt(x)
        case x :: rl  => { prettyOpt(x); w.append(ad); sep(rl, ad) }
      }
    }
    def seps(l: List[Opt[String]], ad: String) {
      l match {
        case Nil =>
        case x :: Nil => prettyOptString(x)
        case x :: rl  => { prettyOptString(x); w.append(ad); seps(rl, ad) }
      }
    }
    def commaSep(l: List[Opt[AST]]) { sep(l, ", ") }
    def spaceSep(l: List[Opt[AST]]) { sep(l, " ") }
    def opt(o: Option[AST]) { if (o.isDefined) prettyPrint(o.get) }
    def optExt(o: Option[AST], ad: String) { if (o.isDefined) {w.append(ad); prettyPrint(o.get)} }

    ast match {
      case TranslationUnit(ext) => sep(ext, newLinePlusIndentation)
      case Id(name) => w.append(name)
      case Constant(v) => w.append(v)
      case StringLit(v) => seps(v, "")
      case SimplePostfixSuffix(t) => w.append(t)
      case PointerPostfixSuffix(kind, id) => { w.append(kind); prettyPrint(id) }
      case FunctionCall(params) => { w.append("("); prettyPrint(params); w.append(")") }
      case ArrayAccess(e) => { w.append("["); prettyPrint(e); w.append("]") }
      case PostfixExpr(p, st) => { prettyPrint(p); prettyPrint(st) }
      case UnaryExpr(p, st) => { w.append(p); prettyPrint(st) }
      case SizeOfExprT(typeName) => { w.append("sizeof("); prettyPrint(typeName); w.append(")") }
      case SizeOfExprU(e) => { w.append("sizeof("); prettyPrint(e); w.append(")") }
      case CastExpr(typeName, expr) => { w.append("(("); prettyPrint(typeName); w.append(") "); prettyPrint(expr); w.append(")") }

      case PointerDerefExpr(castExpr) => { w.append("(*"); prettyPrint(castExpr); w.append(")") }
      case PointerCreationExpr(castExpr) => { w.append("(&"); prettyPrint(castExpr); w.append(")") }

      case UnaryOpExpr(kind, castExpr) => { w.append("("); w.append(kind); w.append(" "); prettyPrint(castExpr); w.append(")") }
      case NAryExpr(e, others) => { w.append("("); prettyPrint(e); w.append(" "); sep(others, " "); w.append(")") }
      case NArySubExpr(op: String, e: Expr) => { w.append(op); w.append(" "); prettyPrint(e) }
      case ConditionalExpr(condition: Expr, thenExpr, elseExpr: Expr) => {
        w.append("("); prettyPrint(condition); w.append(" ? "); opt(thenExpr); w.append(" : "); prettyPrint(elseExpr); w.append(")")
      }
      case AssignExpr(target: Expr, operation: String, source: Expr) => {
        w.append("(")
        prettyPrint(target)
        w.append(" ")
        w.append(operation)
        w.append(" ")
        prettyPrint(source)
        w.append(")")
      }
      case ExprList(exprs) => sep(exprs, " , ")

      case CompoundStatement(innerStatements) => {
        w.append("{")
        indentation += 4
        w.append(newLinePlusIndentation)
        sep(innerStatements, newLinePlusIndentation)
        indentation -= 4
        w.append(newLinePlusIndentation)
        w.append("}")
      }
      case EmptyStatement() => w.append(";")
      case ExprStatement(expr: Expr) => { prettyPrint(expr); w.append(";") }
      case WhileStatement(expr: Expr, sb) => {w.append("while ("); prettyPrint(expr); w.append(") "); prettyCond(sb) }
      case DoStatement(expr: Expr, sb) => { w.append("do "); prettyCond(sb); w.append(" while ("); prettyPrint(expr); w.append(")") }
      case ForStatement(expr1, expr2, expr3, sb) => {
        w.append("for (")
        opt(expr1)
        w.append("; ")
        opt(expr2)
        w.append("; ")
        opt(expr3)
        w.append(") ")
        prettyCond(sb)
      }
      case GotoStatement(target) => { w.append("goto "); prettyPrint(target); w.append(";") }
      case ContinueStatement() => w.append("continue;")
      case BreakStatement() => w.append("break;")
      case ReturnStatement(None) => w.append("return;")
      case ReturnStatement(Some(e)) => { w.append("return "); prettyPrint(e); w.append(";") }
      case LabelStatement(id: Id, _) => { prettyPrint(id); w.append(":") }
      case CaseStatement(c: Expr) => { w.append("case "); prettyPrint(c); w.append(":") }
      case DefaultStatement() => w.append("default:")
      case IfStatement(condition, thenBranch, elifs, elseBranch) => {
        w.append("if (")
        prettyCond(condition)
        w.append(") ")
        prettyCond(thenBranch)
        w.append(" ")
        sep(elifs, newLinePlusIndentation)
        w.append(" ")
        if (elseBranch.isDefined) {
          w.append(" else ")
          prettyCond(elseBranch.get)
        }
      }
      case ElifStatement(condition, thenBranch) => {
        w.append(newLinePlusIndentation)
        w.append("else if (")
        prettyCond(condition)
        w.append(") ")
        prettyCond(thenBranch)
      }
      case SwitchStatement(expr, sb) => {
        w.append("switch (")
        prettyPrint(expr)
        w.append(") ")
        prettyCond(sb)
      }
      case DeclarationStatement(decl: Declaration) => prettyPrint(decl)
      case NestedFunctionDef(isAuto, specifiers, declarator, parameters, stmt) => {
        if (isAuto) w.append("auto ")
        sep(specifiers, " ")
        w.append(" ")
        prettyPrint(declarator)
        w.append(" ")
        sep(parameters, " ")
        w.append(" ")
        prettyPrint(stmt)
      }
      case LocalLabelDeclaration(ids) => {
        w.append("__label__ ")
        sep(ids, ", ")
        w.append(";")
      }
      case OtherPrimitiveTypeSpecifier(typeName: String) => w.append(typeName)
      case VoidSpecifier() => w.append("void")
      case ShortSpecifier() => w.append("short")
      case IntSpecifier() => w.append("int")
      case FloatSpecifier() => w.append("float")
      case LongSpecifier() => w.append("long")
      case CharSpecifier() => w.append("char")
      case DoubleSpecifier() => w.append("double")

      case TypedefSpecifier() => w.append("typedef")
      case TypeDefTypeSpecifier(name: Id) => prettyPrint(name)
      case SignedSpecifier() => w.append("signed")
      case UnsignedSpecifier() => w.append("unsigned")

      case InlineSpecifier() => w.append("inline")
      case AutoSpecifier() => w.append("auto")
      case RegisterSpecifier() => w.append("register")
      case VolatileSpecifier() => w.append("volatile")
      case ExternSpecifier() => w.append("extern")
      case ConstSpecifier() => w.append("const")
      case RestrictSpecifier() => w.append("restrict")
      case StaticSpecifier() => w.append("static")

      case AtomicAttribute(n: String) => w.append(n)
      case AttributeSequence(attributes) => sep(attributes, " ")
      case CompoundAttribute(inner) => { w.append("("); sep(inner, ", "); w.append(")") }

      case Declaration(declSpecs, init) => {
        sep(declSpecs, " ")
        w.append(" ")
        commaSep(init)
        w.append(";")
      }


      case InitDeclaratorI(declarator, _, Some(i)) => { prettyPrint(declarator); w.append(" = "); prettyPrint(i) }
      case InitDeclaratorI(declarator, _, None) => prettyPrint(declarator)
      case InitDeclaratorE(declarator, _, e: Expr) => { prettyPrint(declarator); w.append(": "); prettyPrint(e) }

      case AtomicNamedDeclarator(pointers, id, extensions) => {
        sep(pointers, "")
        prettyPrint(id)
        sep(extensions, "")
      }
      case NestedNamedDeclarator(pointers, nestedDecl, extensions) => {
        sep(pointers, "")
        w.append("(")
        prettyPrint(nestedDecl)
        w.append(")")
        sep(extensions, "")
      }
      case AtomicAbstractDeclarator(pointers, extensions) => {
        sep(pointers, "")
        sep(extensions, "")
      }

      case NestedAbstractDeclarator(pointers, nestedDecl, extensions) => {
        sep(pointers, "")
        w.append("(")
        prettyPrint(nestedDecl)
        w.append(")")
        sep(extensions, "")
      }

      case DeclIdentifierList(idList) => {
        w.append("(")
        commaSep(idList)
        w.append(")")
      }
      case DeclParameterDeclList(parameterDecls) => {
        w.append("(")
        commaSep(parameterDecls)
        w.append(")")
      }
      case DeclArrayAccess(expr) => {
        w.append("[")
        opt(expr)
        w.append("]")
      }
      case Initializer(initializerElementLabel, expr: Expr) => {
        opt(initializerElementLabel)
        w.append(" ")
        prettyPrint(expr)
      }
      case Pointer(specifier) => {
        w.append("*")
        spaceSep(specifier)
      }
      case PlainParameterDeclaration(specifiers) => spaceSep(specifiers)
      case ParameterDeclarationD(specifiers, decl) => {
        spaceSep(specifiers)
        w.append(" ")
        prettyPrint(decl)
      }
      case ParameterDeclarationAD(specifiers, decl) => {
        spaceSep(specifiers)
        prettyPrint(decl)
      }
      case VarArgs() => w.append("...")
      case EnumSpecifier(id, Some(enums)) => {
        w.append("enum ")
        opt(id)
        w.append(" { ")
        sep(enums, ", ")
        w.append("}")
      }
      case EnumSpecifier(Some(id), None) => { w.append("enum "); prettyPrint(id) }
      case Enumerator(id, Some(init)) => { prettyPrint(id); w.append(" = "); prettyPrint(init) }
      case Enumerator(id, None) => prettyPrint(id)
      case StructOrUnionSpecifier(isUnion, id, enumerators) => { w.append((if (isUnion) "union " else "struct "))
        opt(id)
        if (enumerators.isDefined) {
          w.append(" {")
          indentation += 4
          w.append(newLinePlusIndentation)
          sep(enumerators.get, newLinePlusIndentation)
          indentation -= 4
          w.append(newLinePlusIndentation)
          w.append("}")
        }
      }
      case StructDeclaration(qualifierList, declaratorList) => {
        spaceSep(qualifierList)
        w.append(" ")
        commaSep(declaratorList)
        w.append(";")
      }
      case StructDeclarator(decl, initializer, _) => {
        prettyPrint(decl)
        optExt(initializer, ": ")
      }
      case StructInitializer(expr, _) => { w.append(": "); prettyPrint(expr) }
      case AsmExpr(isVolatile, expr) => {
        w.append("asm ")
        w.append(if (isVolatile) "volatile) " else "")
        w.append("{")
        prettyPrint(expr)
        w.append("};")
      }
      case FunctionDef(specifiers, declarator, oldStyleParameters, stmt) => {
        spaceSep(specifiers)
        w.append(" ")
        prettyPrint(declarator)
        w.append(" ")
        spaceSep(oldStyleParameters)
        w.append(" ")
        prettyPrint(stmt)
      }
      case EmptyExternalDef() => w.append(";")
      case TypelessDeclaration(declList) => { commaSep(declList); w.append(";") }
      case TypeName(specifiers, decl) => { spaceSep(specifiers); w.append(" "); opt(decl) }

      case GnuAttributeSpecifier(attributeList) => { w.append("__attribute__(("); commaSep(attributeList); w.append("))") }
      case AsmAttributeSpecifier(stringConst) => prettyPrint(stringConst)
      case LcurlyInitializer(inits) => { w.append("{"); commaSep(inits); w.append("}") }
      case AlignOfExprT(typeName: TypeName) => { w.append("__alignof__("); prettyPrint(typeName); w.append(")") }
      case AlignOfExprU(expr: Expr) => { w.append("__alignof__ "); prettyPrint(expr) }
      case GnuAsmExpr(isVolatile: Boolean, isAuto, expr: StringLit, stuff: Any) => w.append("asm")
      case RangeExpr(from: Expr, to: Expr) => { prettyPrint(from); w.append(" ... "); prettyPrint(to) }
      case TypeOfSpecifierT(typeName: TypeName) => { w.append("typeof("); prettyPrint(typeName); w.append(")") }
      case TypeOfSpecifierU(e: Expr) => { w.append("typeof("); prettyPrint(e); w.append(")") }
      case InitializerArrayDesignator(expr: Expr) => { w.append("["); prettyPrint(expr); w.append("]") }
      case InitializerDesignatorD(id: Id) => { w.append("."); prettyPrint(id) }
      case InitializerDesignatorC(id: Id) => { prettyPrint(id); w.append(":") }
      case InitializerAssigment(desgs) => { spaceSep(desgs); w.append(" =") }
      case BuiltinOffsetof(typeName: TypeName, offsetofMemberDesignator) => { w.append("__builtin_offsetof("); prettyPrint(typeName); w.append(", "); spaceSep(offsetofMemberDesignator); w.append(")") }
      case OffsetofMemberDesignatorID(id: Id) => { w.append("."); prettyPrint(id) }
      case OffsetofMemberDesignatorExpr(expr: Expr) => { w.append("["); prettyPrint(expr); w.append("]") }
      case BuiltinTypesCompatible(typeName1: TypeName, typeName2: TypeName) => { w.append("__builtin_types_compatible_p("); prettyPrint(typeName1); w.append(", "); prettyPrint(typeName2); w.append(")") }
      case BuiltinVaArgs(expr: Expr, typeName: TypeName) => { w.append("__builtin_va_arg("); prettyPrint(expr); w.append(", "); prettyPrint(typeName); w.append(")") }
      case CompoundStatementExpr(compoundStatement: CompoundStatement) => { w.append("("); prettyPrint(compoundStatement); w.append(")") }
      case Pragma(command: StringLit) => { w.append("_Pragma("); prettyPrint(command); w.append(")") }

      case e => assert(assertion = false, message = "match not exhaustive: " + e); w.append("")
    }
  }
}