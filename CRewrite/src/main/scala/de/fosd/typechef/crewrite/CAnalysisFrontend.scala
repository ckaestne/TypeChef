package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.conditional.{Opt, Choice}
import de.fosd.typechef.parser.c.{PrettyPrinter, TranslationUnit, FunctionDef, AST}

import sat.DefinedMacro
import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.WithPosition
import de.fosd.typechef.parser.c._

class CAnalysisFrontend(tunit: AST, fm: FeatureModel = FeatureExprFactory.default.featureModelFactory.empty) extends ConditionalNavigation with ConditionalControlFlow with IOUtilities with Liveness with EnforceTreeHelper {

  // derive a specific product from a given configuration
  def deriveProductFromConfiguration[T <: Product](a: T, c: Configuration, env: ASTEnv): T = {
    // manytd is crucial here; consider the following example
    // Product1( c1, c2, c3, c4, c5)
    // all changes the elements top down, so the parent is changed before the children and this
    // way the lookup env.featureExpr(x) will not fail. Using topdown or everywherebu changes the children and so also the
    // parent before the parent is processed so we get a NullPointerExceptions calling env.featureExpr(x). Reason is
    // changed children lead to changed parent and a new hashcode so a call to env fails.
    val pconfig = manytd(rule {
      case Choice(f, x, y) => if (c.config implies (if (env.containsASTElem(x)) env.featureExpr(x) else FeatureExprFactory.True) isTautology()) x else y
      case l: List[Opt[_]] => {
        var res: List[Opt[_]] = List()
        // use l.reverse here to omit later reverse on res or use += or ++= in the thenBranch
        for (o <- l.reverse)
          if (o.feature == FeatureExprFactory.True)
            res ::= o
          else if (c.config implies (if (env.containsASTElem(o.entry)) env.featureExpr(o.entry) else FeatureExprFactory.True) isTautology()) {
            res ::= o.copy(feature = FeatureExprFactory.True)
          }
        res
      }
      case x => x
    })

    val x = pconfig(a).get.asInstanceOf[T]
    //appendToFile("output.c", PrettyPrinter.print(x.asInstanceOf[AST]))
    x
  }


    private def deriveConditional[T <: AST](e:Conditional[T], c: Configuration):One[T]=e match {
        case Choice(f, x, y) => {
            //if (c~.config.implies(env.featureExpr(x)).isTautology()) { //!! this does not work??
            if (c.config.implies(f).isTautology()) { // this probably only works for complete configurations
                val res = deriveConditional(x,c)
                //val res = new One(deriveProd(x,c))
                res
                //} else if (c.config.implies(env.featureExpr(y)).isTautology()) {
            } else if (c.config.implies(f.not()).isTautology()) {
                val res = deriveConditional(y,c)
                //val res = new One(deriveProd(y,c))
                res
            } else {
                //println("failure!!!!")
                assert(false,"variability in this configuration!")
                deriveConditional(y,c)
            }
        }
        case One(value) => new One(deriveProd(value,c))
    }
    private def deriveOptionalAST[T <: AST](e:Option[T], c: Configuration) : Option[T] = e match {
        case None => None
        case Some(x) => Some(deriveProd(x,c))
    }
    private def deriveOptionalConditional[T<:AST](e:Option[Conditional[T]], c: Configuration) : Option[Conditional[T]] = e match {
        case None => None
        case Some(cond) => Some(deriveConditional(cond,c))
    }
    private def deriveOptionalOptList[T<:AST](e:Option[List[Opt[T]]], c: Configuration) : Option[List[Opt[T]]] = e match {
        case None => None
        case Some(x) => Some(deriveOptList(x,c))
    }
    private def deriveASTList[T<:AST](e:List[T], c: Configuration) : List[T] =  {
        e.map(deriveProd(_,c))
    }
    private def deriveOptList[T<:AST](e:List[Opt[T]], c: Configuration) : List[Opt[T]] =  {
        var res: List[Opt[T]] = List()
        for (o <- e.reverse) {
            if (c.config.implies(o.feature).isTautology()) {// this probably only works for complete configurations
            val derived = o.copy(feature = FeatureExprFactory.True, entry = deriveProd(o.entry,c))
                res ::= derived
            } else {
                //println("----omitting opt (not implied) with feature " + o.feature.toTextExpr +" : " + o)
            }
        }
        res
    }

    /**
   * Generates a second AST that has only the AST nodes belonging to the given configuration.
   * It is a projection of the original AST.
   * This method might work incorrectly if the given configuration still allows variability for features
   * used in the expanded file.
   * @param a The root node of the AST
   * @param c A configuration
   * @tparam T Return variable is of the same type as the root node.
   * @return
   */
  def deriveProd[T <: AST](a: T, c: Configuration): T = {
    val returnValue : Any = a match {
      case i:Id => i
      case x@Constant(s) => new Constant(s)
      case StringLit(s) => new StringLit(s);
      case BuiltinOffsetof(typeName,od) => new BuiltinOffsetof(typeName, deriveOptList(od,c))
      case BuiltinTypesCompatible(t1,t2) => new BuiltinTypesCompatible(deriveProd(t1,c), deriveProd(t2,c))
      case BuiltinVaArgs(ex,tn) => new BuiltinVaArgs(deriveProd(ex,c),deriveProd(tn,c))
      case CompoundStatementExpr(ent) => new CompoundStatementExpr(deriveProd(ent,c))
      case PostfixExpr(p, s) => new PostfixExpr(deriveProd(p,c),deriveProd(s,c))
      case UnaryExpr(kind,e) => new UnaryExpr(kind, deriveProd(e, c))
      case SizeOfExprT(tn) => new SizeOfExprT(deriveProd(tn, c))
      case SizeOfExprU(tn) => new SizeOfExprU(deriveProd(tn, c))
      case CastExpr(tn, expr) => new CastExpr(deriveProd(tn,c), deriveProd(expr, c))
      case PointerDerefExpr(expr) => new PointerDerefExpr(deriveProd(expr, c))
      case PointerCreationExpr(ex) => new PointerCreationExpr(deriveProd(ex,c))
      case UnaryOpExpr(kind,ex) => new UnaryOpExpr(kind,deriveProd(ex,c))
      case NAryExpr(e,others) => new NAryExpr(deriveProd(e,c), deriveOptList(others,c))
      case ConditionalExpr(cond,then,elseEx) => new ConditionalExpr(deriveProd(cond,c),deriveOptionalAST(then,c),deriveProd(elseEx,c))
      case AssignExpr(tgt,op,src) => new AssignExpr(deriveProd(tgt,c),op, deriveProd(src,c))
      case ExprList(exs) => new ExprList(deriveOptList(exs,c))
      case LcurlyInitializer(inits) => new LcurlyInitializer(deriveOptList(inits,c))
      case AlignOfExprT(tn) => new AlignOfExprT(deriveProd(tn,c))
      case AlignOfExprU(tn) => new AlignOfExprU(deriveProd(tn,c))
      case GnuAsmExpr(iv,ig,expr,stuff) => new GnuAsmExpr(iv,ig,deriveProd(expr,c), stuff)
      case RangeExpr(from, to) => new RangeExpr(deriveProd(from,c),deriveProd(to,c))
      case x : SimplePostfixSuffix => x
      case PointerPostfixSuffix(kind, id) => new PointerPostfixSuffix(kind,deriveProd(id,c))
      case FunctionCall(params) => new FunctionCall(deriveProd(params,c))
      case ArrayAccess(expr) => new ArrayAccess(deriveProd(expr,c))
      case NArySubExpr(op, ex) => new NArySubExpr(op, deriveProd(ex,c))
      case CompoundStatement(inner) => new CompoundStatement(deriveOptList(inner,c))
      case x:EmptyStatement => x
      case ExprStatement(ex) => new ExprStatement(deriveProd(ex,c))
      case WhileStatement(ex, s) => new WhileStatement(deriveProd(ex,c), deriveConditional(s,c))
      case DoStatement(ex, s) => new DoStatement(deriveProd(ex,c),deriveConditional(s,c))
      case ForStatement(ex1,ex2,ex3,s) => new ForStatement(deriveOptionalAST(ex1,c),deriveOptionalAST(ex2,c),deriveOptionalAST(ex3,c),deriveConditional(s,c))
      case GotoStatement(ex) => new GotoStatement(deriveProd(ex,c))
      case x:ContinueStatement => x
      case x:BreakStatement => x
      case ReturnStatement(ex)=> new ReturnStatement(deriveOptionalAST(ex,c))
      case LabelStatement(id,attr) => new LabelStatement(deriveProd(id,c),deriveOptionalAST(attr,c))
      case CaseStatement(cs,s) => new CaseStatement(deriveProd(cs,c), deriveOptionalConditional(s,c))
      case DefaultStatement(s) => new DefaultStatement(deriveOptionalConditional(s,c))
      case IfStatement(cond,then,elifs,elseBr) => new IfStatement(deriveConditional(cond,c),deriveConditional(then,c),deriveOptList(elifs,c),deriveOptionalConditional(elseBr,c))
      case SwitchStatement(ex,s) => new SwitchStatement(deriveProd(ex,c),deriveConditional(s,c))
      case DeclarationStatement(dec) => new DeclarationStatement(deriveProd(dec,c))
      case LocalLabelDeclaration(ids) => new LocalLabelDeclaration(deriveOptList(ids,c))
      case NestedFunctionDef(isAuto,specs,dec,par,stmt) => new NestedFunctionDef(isAuto,deriveOptList(specs,c),deriveProd(dec,c),deriveOptList(par,c),deriveProd(stmt,c))
      case ElifStatement(cond,branch) => new ElifStatement(deriveConditional(cond,c),deriveConditional(branch,c))
      // all primitiveTypeSpecifier - subclasses do not have individual properties or sub-elements. So we can reuse them directly.
      case x : PrimitiveTypeSpecifier => x
      case OtherPrimitiveTypeSpecifier(tn) => new OtherPrimitiveTypeSpecifier(tn)
      case TypeDefTypeSpecifier(name) => new TypeDefTypeSpecifier(deriveProd(name,c))
      case x : SignedSpecifier => x
      case x : UnsignedSpecifier => x
      case EnumSpecifier(id,enums) => new EnumSpecifier(deriveOptionalAST(id,c),deriveOptionalOptList(enums,c))
      case StructOrUnionSpecifier(isu,id,enums) => new StructOrUnionSpecifier(isu,deriveOptionalAST(id,c),deriveOptList(enums,c))
      case TypeOfSpecifierT(tn) => new TypeOfSpecifierT(deriveProd(tn,c))
      case TypeOfSpecifierU(tn) => new TypeOfSpecifierU(deriveProd(tn,c))
      // all OtherSpecifier - subclasses do not have individual properties or sub-elements. So we can reuse them directly.
      case x : OtherSpecifier => x
      case x : TypedefSpecifier => x
      case GnuAttributeSpecifier(attrList) => new GnuAttributeSpecifier(deriveOptList(attrList,c))
      case AsmAttributeSpecifier(strconst) => new AsmAttributeSpecifier(deriveProd(strconst,c))
      case AtomicAttribute(n) => new AtomicAttribute(n)
      case CompoundAttribute(inner) => new CompoundAttribute(deriveOptList(inner,c))
      case AttributeSequence(attrs) => new AttributeSequence(deriveOptList(attrs,c))
      case InitDeclaratorI(dec,attrs,i) => new InitDeclaratorI(deriveProd(dec,c),deriveOptList(attrs,c),deriveOptionalAST(i,c))
      case InitDeclaratorE(dec,attrs,ex) => new InitDeclaratorE(deriveProd(dec,c),deriveOptList(attrs,c),deriveProd(ex,c))
      case AtomicAbstractDeclarator(pointers, exts) => new AtomicAbstractDeclarator(deriveOptList(pointers,c),deriveOptList(exts,c))
      case NestedAbstractDeclarator(pointers,nest,exts) => new NestedAbstractDeclarator(deriveOptList(pointers,c),deriveProd(nest,c),deriveOptList(exts,c))
      case AtomicNamedDeclarator(po,id,exts) => new AtomicNamedDeclarator(deriveOptList(po,c),deriveProd(id,c),deriveOptList(exts,c))
      case NestedNamedDeclarator(po,nest,exts) => new NestedNamedDeclarator(deriveOptList(po,c),deriveProd(nest,c),deriveOptList(exts,c))
      case DeclParameterDeclList(decls) => new DeclParameterDeclList(deriveOptList(decls,c))
      case DeclArrayAccess(ex) => new DeclArrayAccess(deriveOptionalAST(ex,c))
      case DeclIdentifierList(idList) => new DeclIdentifierList(deriveOptList(idList,c))
      case Initializer(iel,ex) => new Initializer(deriveOptionalAST(iel,c), deriveProd(ex,c))
      case Pointer(spec) => new Pointer(deriveOptList(spec,c))
      case PlainParameterDeclaration(specs) => new PlainParameterDeclaration(deriveOptList(specs,c))
      case ParameterDeclarationD(specs,decl) => new ParameterDeclarationD(deriveOptList(specs,c), deriveProd(decl,c))
      case ParameterDeclarationAD(specs,decl) => new ParameterDeclarationAD(deriveOptList(specs,c), deriveProd(decl,c))
      case x : VarArgs => x
      case Enumerator(id,ass) => new Enumerator(deriveProd(id,c),deriveOptionalAST(ass,c))
      case StructDeclaration(qlist,declist) => new StructDeclaration(deriveOptList(qlist,c),deriveOptList(declist,c))
      case StructDeclarator(decl,init,attrs) => new StructDeclarator(deriveProd(decl,c),deriveOptionalAST(init,c),deriveOptList(attrs,c))
      case StructInitializer(ex,attrs) => new StructInitializer(deriveProd(ex,c),deriveOptList(attrs,c))
      case AsmExpr(isv,ex) => new AsmExpr(isv,deriveProd(ex,c))
      case FunctionDef(specs,dec,osp,stmt) => new FunctionDef(deriveOptList(specs,c),deriveProd(dec,c),deriveOptList(osp,c),deriveProd(stmt,c))
      case Declaration(decSpecs,init) => new Declaration(deriveOptList(decSpecs,c),deriveOptList(init,c))
      // AsmExpr already handled above
      //case AsmExpr(isv,ex) => new AsmExpr(isv,deriveProd(ex,c))
      // omit FunctionDef, i already handled that (four lines above)
      case x : EmptyExternalDef => x
      case TypelessDeclaration(declList) => new TypelessDeclaration(deriveOptList(declList,c))
      case Pragma(cmd) => new Pragma(deriveProd(cmd,c))
      case TypeName(specs,decl) => new TypeName(deriveOptList(specs,c),deriveOptionalAST(decl,c))
      case TranslationUnit(defs) => new TranslationUnit(deriveOptList(defs,c))
      case InitializerArrayDesignator(ex) => new InitializerArrayDesignator(deriveProd(ex,c))
      case InitializerDesignatorC(id) => new InitializerDesignatorC(deriveProd(id,c))
      case InitializerDesignatorD(id) => new InitializerDesignatorD(deriveProd(id,c))
      case InitializerAssigment(des) => new InitializerAssigment(deriveOptList(des,c))
      case OffsetofMemberDesignatorID(id) => new OffsetofMemberDesignatorID(deriveProd(id,c))
      case OffsetofMemberDesignatorExpr(ex) => new OffsetofMemberDesignatorExpr(deriveProd(ex,c))
      case y  => {assert(false, "unhandled type: " + y.getClass.getSimpleName); y}
    }
    if (returnValue.isInstanceOf[WithPosition]) {// should always be true, because AST has WithPosition
        returnValue.asInstanceOf[WithPosition].setPositionRange(a.getPositionFrom,a.getPositionTo)
    }
    returnValue.asInstanceOf[T]
  }

  class CCFGError(msg: String, s: AST, sfexp: FeatureExpr, t: AST, tfexp: FeatureExpr) {
    override def toString =
      "[" + sfexp + "]" + s.getClass() + "(" + s.getPositionFrom + "--" + s.getPositionTo + ")" + // print source
        "--> " +
        "[" + tfexp + "]" + t.getClass() + "(" + t.getPositionFrom + "--" + t.getPositionTo + ")" + // print target
        "\n" + msg + "\n\n\n"
  }

  var errors = List[CCFGError]()
  val liveness = "liveness.csv"

  def checkCfG(fileName: String) = {

    // file-output
    appendToFile(liveness, "filename;family-based;full-coverage;full-coverage-configs")

    // family-based
    println("checking family-based")
    val family_ast = prepareAST[TranslationUnit](tunit.asInstanceOf[TranslationUnit])
    val family_env = CASTEnv.createASTEnv(family_ast)
    val family_function_defs = filterASTElems[FunctionDef](family_ast)

    val tfams = System.currentTimeMillis()
    family_function_defs.map(intraCfGFunctionDef(_, family_env))
    val tfame = System.currentTimeMillis()

    val tfam = tfame - tfams

    // base variant
    println("checking base variant")
    val base_ast = deriveProductFromConfiguration[TranslationUnit](family_ast.asInstanceOf[TranslationUnit], new Configuration(FeatureExprFactory.True, fm), family_env)
    val base_env = CASTEnv.createASTEnv(base_ast)
    val base_function_defs = filterASTElems[FunctionDef](base_ast)

    val tbases = System.currentTimeMillis()
    base_function_defs.map(intraCfGFunctionDef(_, base_env))
    val tbasee = System.currentTimeMillis()

    val tbase = tbasee - tbases

    // full coverage
    println("checking full coverage")
    val configs = ConfigurationCoverage.naiveCoverageAny(family_ast, fm, family_env)
    var current_config = 1
    var tfullcoverage: Long = 0

    for (config <- configs) {
      println("checking configuration " + current_config + " of " + configs.size)
      current_config += 1
      val product_ast = deriveProductFromConfiguration[TranslationUnit](family_ast, new Configuration(config, fm), family_env)
      val product_env = CASTEnv.createASTEnv(product_ast)
      val product_function_defs = filterASTElems[FunctionDef](product_ast)
      appendToFile("test.c", PrettyPrinter.print(product_ast))

      val tfullcoverages = System.currentTimeMillis()
      product_function_defs.map(intraCfGFunctionDef(_, product_env))
      val tfullcoveragee = System.currentTimeMillis()

      tfullcoverage += (tfullcoveragee - tfullcoverages)
    }

    println("family-based: " + tfam + "ms")
    println("base variant: " + tbase + "ms")
    println("full coverage: " + tfullcoverage + "ms")

    appendToFile(liveness, fileName + ";" + tfam + ";" + tbase + ";" + tfullcoverage + ";" + configs.size + "\n")
  }

  private def intraCfGFunctionDef(f: FunctionDef, env: ASTEnv) = {
    val myenv = CASTEnv.createASTEnv(f)

    val ss = if (f.stmt.innerStatements.isEmpty) List() else getAllSucc(f.stmt.innerStatements.head.entry, myenv).map(_._1).filterNot(_.isInstanceOf[FunctionDef])
    for (s <- ss.reverse) {
      in(s, myenv)
      out(s, myenv)
    }

    true
  }
}
