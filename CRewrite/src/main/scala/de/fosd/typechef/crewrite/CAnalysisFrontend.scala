package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.conditional._


class CAnalysisFrontend(tunit: AST, fm: FeatureModel = NoFeatureModel) extends CASTEnv with ConditionalNavigation with ConditionalControlFlow with IOUtilities with Liveness with EnforceTreeHelper {

  // derive a specific product from a given configuration
  def deriveProductFromConfiguration[T <: Product](a: T, c: Configuration, env: ASTEnv): T = {
    // all is crucial here; consider the following example
    // Product1( c1, c2, c3, c4, c5)
    // all changes the elements top down, so the parent is changed before the children and this
    // way the lookup env.featureExpr(x) will not fail. Using topdown or everywherebu changes the children and so also the
    // parent resulting in NullPointerExceptions calling env.featureExpr(x) because the parent is changed, has a different
    // hashcode and is not part of the environment.
    val pconfig = all(rule {
      case Choice(f, x, y) => if (c.config implies env.featureExpr(x) isTautology()) x else y
      case l: List[Opt[_]] => {
        var res: List[Opt[_]] = List()
        // use l.reverse here to omit later reverse on res or use += or ++= in the thenBranch
        for (o <- l.reverse)
          if (o.feature == FeatureExpr.base)
            res ::= o
          else if (c.config implies env.featureExpr(o.entry) isTautology()) {
            res ::= o.copy(feature = FeatureExpr.base)
          }
        res
      }
      case x => x
    })

    val x = pconfig(a).get.asInstanceOf[T]
    val env2 = createASTEnv(x)
    /*filterAllOptElems(x).map(_.entry).map(
      (e:Any) => if (!env2.containsASTElem(e)){println("NOT in Env: " + e.toString()); e}
      else if (env2.featureExpr(e)!= FeatureExpr.base) {"Fexp != base: " + println(e.toString() + "\tFexp: " + env2.featureExpr(e).toTextExpr); e}
    )
    println("Opt Elements left: " + filterAllOptElems(x).size)
    */
    //assert(filterAllOptElems(x).map(_.entry).map(isVariable(_, env2)).fold(false)(_ || _) == false, "Fehler")
    x
  }

  /**
   * Generates a second AST that has only the AST nodes belonging to the given configuration.
   * It is a projection of the original AST.
   * This method might work incorrectly if the given configuration still allows variability for features
   * used in the expanded file.
   * @param a The root node of the AST
   * @param c A configuration
   * @param env ASTEnv
   * @tparam T Return variable is of the same type as the root node.
   * @return
   */
  def deriveProd[T <: Product](a: T, c: Configuration, env: ASTEnv): T = {
    val returnValue : Any = a match {
      case Choice(f, x, y) => {
        //if (c.config.implies(env.featureExpr(x)).isTautology()) { //!! this does not work??
          if (c.config.implies(f).isTautology()) { // this probably only works for complete configurations
          val res = new Choice(FeatureExpr.base, deriveProd(x,c,env), One(new EmptyStatement()))
          //val res = new One(deriveProd(x,c,env))
          res
        //} else if (c.config.implies(env.featureExpr(y)).isTautology()) {
      } else if (c.config.implies(f.not()).isTautology()) {
          val res = new Choice(FeatureExpr.base, deriveProd(y,c,env), One(new EmptyStatement()))
          //val res = new One(deriveProd(y,c,env))
          res
        } else {
          //println("failure!!!!")
          assert(false,"variability in this configuration!")
          Choice(f,x,y)
        }
      }
      case One(One(value:Product)) => new One(deriveProd(value,c,env))
      case One(value:Product) => new One(deriveProd(value,c,env))
      case l: List[Opt[_]] => {
        var res: List[Opt[_]] = List()
        for (o <- l.reverse) {
          //if (c.config.implies(env.featureExpr(o.entry)).isTautology()) {
          if (c.config.implies(o.feature).isTautology()) {// this probably only works for complete configurations
            val derived = deriveProd(o.copy(feature = FeatureExpr.base), c, env)
            res ::= derived
          } else {
            //println("----omitting opt("+thisId+") " + o)
          }
        }
        res
      }
      case Opt(fex, entry : Product) =>
        assert(c.config.implies(env.featureExpr(entry)).isTautology());
        assert(c.config.implies(fex).isTautology());
        new Opt(FeatureExpr.base,deriveProd(entry,c,env)) // the variability should be handled be the List[Opt[_]] - code
      case l : List[Product] => l.map(deriveProd(_,c,env))
      case Some(x : Product) => new Some(deriveProd(x,c,env))

      case None => None

      case Id(s) => new Id(s);
      case Constant(s) => new Constant(s);
      case StringLit(s) => new StringLit(s);
      case BuiltinOffsetof(typeName,od) => new BuiltinOffsetof(deriveProd(typeName,c, env), deriveProd(od,c,env))
      case BuiltinTypesCompatible(t1,t2) => new BuiltinTypesCompatible(deriveProd(t1,c, env), deriveProd(t2,c,env))
      case BuiltinVaArgs(ex,tn) => new BuiltinVaArgs(deriveProd(ex,c,env),deriveProd(tn,c,env))
      case CompoundStatementExpr(ent) => new CompoundStatementExpr(deriveProd(ent,c, env))
      case PostfixExpr(p, s) => new PostfixExpr(deriveProd(p,c,env),deriveProd(s,c,env))
      case UnaryExpr(kind,e) => new UnaryExpr(kind, deriveProd(e, c, env))
      case SizeOfExprT(tn) => new SizeOfExprT(deriveProd(tn, c, env))
      case SizeOfExprU(tn) => new SizeOfExprU(deriveProd(tn, c, env))
      case CastExpr(tn, expr) => new CastExpr(deriveProd(tn,c, env), deriveProd(expr, c,env))
      case PointerDerefExpr(expr) => new PointerDerefExpr(deriveProd(expr, c,env))
      case PointerCreationExpr(ex) => new PointerCreationExpr(deriveProd(ex,c,env))
      case UnaryOpExpr(kind,ex) => new UnaryOpExpr(kind,deriveProd(ex,c,env))
      case NAryExpr(e,others) => new NAryExpr(deriveProd(e,c,env), deriveProd(others,c,env))
      case ConditionalExpr(cond,then,elseEx) => new ConditionalExpr(deriveProd(cond,c,env),deriveProd(then,c,env),deriveProd(elseEx,c,env))
      case AssignExpr(tgt,op,src) => new AssignExpr(deriveProd(tgt,c,env),op, deriveProd(src,c,env))
      case ExprList(exs) => new ExprList(deriveProd(exs,c,env))
      case LcurlyInitializer(inits) => new LcurlyInitializer(deriveProd(inits,c,env))
      case AlignOfExprT(tn) => new AlignOfExprT(deriveProd(tn,c,env))
      case AlignOfExprU(tn) => new AlignOfExprU(deriveProd(tn,c,env))
      case GnuAsmExpr(iv,ig,expr,stuff) => new GnuAsmExpr(iv,ig,deriveProd(expr,c,env), stuff)
      case RangeExpr(from, to) => new RangeExpr(deriveProd(from,c,env),deriveProd(to,c,env))
      case x : SimplePostfixSuffix => x
      case PointerPostfixSuffix(kind, id) => new PointerPostfixSuffix(kind,deriveProd(id,c,env))
      case FunctionCall(params) => new FunctionCall(deriveProd(params,c,env))
      case ArrayAccess(expr) => new ArrayAccess(deriveProd(expr,c,env))
      case NArySubExpr(op, ex) => new NArySubExpr(op, deriveProd(ex,c,env))
      case CompoundStatement(inner) => new CompoundStatement(deriveProd(inner,c,env))
      case x:EmptyStatement => x
      case ExprStatement(ex) => new ExprStatement(deriveProd(ex,c,env))
      case WhileStatement(ex, s) => new WhileStatement(deriveProd(ex,c,env), deriveProd(s,c,env))
      case DoStatement(ex, s) => new DoStatement(deriveProd(ex,c,env),deriveProd(s,c,env))
      case ForStatement(ex1,ex2,ex3,s) => new ForStatement(deriveProd(ex1,c,env),deriveProd(ex2,c,env),deriveProd(ex3,c,env),deriveProd(s,c,env))
      case GotoStatement(ex) => new GotoStatement(deriveProd(ex,c,env))
      case x:ContinueStatement => x
      case x:BreakStatement => x
      case ReturnStatement(ex)=> new ReturnStatement(deriveProd(ex,c,env))
      case LabelStatement(id,attr) => new LabelStatement(deriveProd(id,c,env),deriveProd(attr,c,env))
      case CaseStatement(cs,s) => new CaseStatement(deriveProd(cs,c,env), deriveProd(s,c,env))
      case DefaultStatement(s) => new DefaultStatement(deriveProd(s,c,env))
      case IfStatement(cond,then,elifs,elseBr) => new IfStatement(deriveProd(cond,c,env),deriveProd(then,c,env),deriveProd(elifs,c,env),deriveProd(elseBr,c,env))
      case SwitchStatement(ex,s) => new SwitchStatement(deriveProd(ex,c,env),deriveProd(s,c,env))
      case DeclarationStatement(dec) => new DeclarationStatement(deriveProd(dec,c,env))
      case LocalLabelDeclaration(ids) => new LocalLabelDeclaration(deriveProd(ids,c,env))
      case NestedFunctionDef(isAuto,specs,dec,par,stmt) => new NestedFunctionDef(isAuto,deriveProd(specs,c,env),deriveProd(dec,c,env),deriveProd(par,c,env),deriveProd(stmt,c,env))
      case ElifStatement(cond,branch) => new ElifStatement(deriveProd(cond,c,env),deriveProd(branch,c,env))
      // all primitiveTypeSpecifier - subclasses do not have individual properties or sub-elements. So we can reuse them directly.
      case x : PrimitiveTypeSpecifier => x
      case OtherPrimitiveTypeSpecifier(tn) => new OtherPrimitiveTypeSpecifier(tn)
      case TypeDefTypeSpecifier(name) => new TypeDefTypeSpecifier(deriveProd(name,c,env))
      case x : SignedSpecifier => x
      case x : UnsignedSpecifier => x
      case EnumSpecifier(id,enums) => new EnumSpecifier(deriveProd(id,c,env),deriveProd(enums,c,env))
      case StructOrUnionSpecifier(isu,id,enums) => new StructOrUnionSpecifier(isu,deriveProd(id,c,env),deriveProd(enums,c,env))
      case TypeOfSpecifierT(tn) => new TypeOfSpecifierT(deriveProd(tn,c,env))
      case TypeOfSpecifierU(tn) => new TypeOfSpecifierU(deriveProd(tn,c,env))
      // all OtherSpecifier - subclasses do not have individual properties or sub-elements. So we can reuse them directly.
      case x : OtherSpecifier => x
      case x : TypedefSpecifier => x
      case GnuAttributeSpecifier(attrList) => new GnuAttributeSpecifier(deriveProd(attrList,c,env))
      case AsmAttributeSpecifier(strconst) => new AsmAttributeSpecifier(deriveProd(strconst,c,env))
      case AtomicAttribute(n) => new AtomicAttribute(n)
      case CompoundAttribute(inner) => new CompoundAttribute(deriveProd(inner,c,env))
      case AttributeSequence(attrs) => new AttributeSequence(deriveProd(attrs,c,env))
      case InitDeclaratorI(dec,attrs,i) => new InitDeclaratorI(deriveProd(dec,c,env),deriveProd(attrs,c,env),deriveProd(i,c,env))
      case InitDeclaratorE(dec,attrs,ex) => new InitDeclaratorE(deriveProd(dec,c,env),deriveProd(attrs,c,env),deriveProd(ex,c,env))
      case AtomicAbstractDeclarator(pointers, exts) => new AtomicAbstractDeclarator(deriveProd(pointers,c,env),deriveProd(exts,c,env))
      case NestedAbstractDeclarator(pointers,nest,exts) => new NestedAbstractDeclarator(deriveProd(pointers,c,env),deriveProd(nest,c,env),deriveProd(exts,c,env))
      case AtomicNamedDeclarator(po,id,exts) => new AtomicNamedDeclarator(deriveProd(po,c,env),deriveProd(id,c,env),deriveProd(exts,c,env))
      case NestedNamedDeclarator(po,nest,exts) => new NestedNamedDeclarator(deriveProd(po,c,env),deriveProd(nest,c,env),deriveProd(exts,c,env))
      case DeclParameterDeclList(decls) => new DeclParameterDeclList(deriveProd(decls,c,env))
      case DeclArrayAccess(ex) => new DeclArrayAccess(deriveProd(ex,c,env))
      case DeclIdentifierList(idList) => new DeclIdentifierList(deriveProd(idList,c,env))
      case Initializer(iel,ex) => new Initializer(deriveProd(iel,c,env), deriveProd(ex,c,env))
      case Pointer(spec) => new Pointer(deriveProd(spec,c,env))
      case PlainParameterDeclaration(specs) => new PlainParameterDeclaration(deriveProd(specs,c,env))
      case ParameterDeclarationD(specs,decl) => new ParameterDeclarationD(deriveProd(specs,c,env), deriveProd(decl,c,env))
      case ParameterDeclarationAD(specs,decl) => new ParameterDeclarationAD(deriveProd(specs,c,env), deriveProd(decl,c,env))
      case x : VarArgs => x
      case Enumerator(id,ass) => new Enumerator(deriveProd(id,c,env),deriveProd(ass,c,env))
      case StructDeclaration(qlist,declist) => new StructDeclaration(deriveProd(qlist,c,env),deriveProd(declist,c,env))
      case StructDeclarator(decl,init,attrs) => new StructDeclarator(deriveProd(decl,c,env),deriveProd(init,c,env),deriveProd(attrs,c,env))
      case StructInitializer(ex,attrs) => new StructInitializer(deriveProd(ex,c,env),deriveProd(attrs,c,env))
      case AsmExpr(isv,ex) => new AsmExpr(isv,deriveProd(ex,c,env))
      case FunctionDef(specs,dec,osp,stmt) => new FunctionDef(deriveProd(specs,c,env),deriveProd(dec,c,env),deriveProd(osp,c,env),deriveProd(stmt,c,env))
      case Declaration(decSpecs,init) => new Declaration(deriveProd(decSpecs,c,env),deriveProd(init,c,env))
      // AsmExpr already handled above
      //case AsmExpr(isv,ex) => new AsmExpr(isv,deriveProd(ex,c,env))
      // omit FunctionDef, i already handled that (four lines above)
      case x : EmptyExternalDef => x
      case TypelessDeclaration(declList) => new TypelessDeclaration(deriveProd(declList,c,env))
      case Pragma(cmd) => new Pragma(deriveProd(cmd,c,env))
      case TypeName(specs,decl) => new TypeName(deriveProd(specs,c,env),deriveProd(decl,c,env))
      case TranslationUnit(defs) => new TranslationUnit(deriveProd(defs,c,env))
      case InitializerArrayDesignator(ex) => new InitializerArrayDesignator(deriveProd(ex,c,env))
      case InitializerDesignatorC(id) => new InitializerDesignatorC(deriveProd(id,c,env))
      case InitializerDesignatorD(id) => new InitializerDesignatorD(deriveProd(id,c,env))
      case InitializerAssigment(des) => new InitializerAssigment(deriveProd(des,c,env))
      case OffsetofMemberDesignatorID(id) => new OffsetofMemberDesignatorID(deriveProd(id,c,env))
      case OffsetofMemberDesignatorExpr(ex) => new OffsetofMemberDesignatorExpr(deriveProd(ex,c,env))
      case y  => {assert(false, "unhandled type: " + y.getClass.getSimpleName); y}
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

  // given an ast element x and its successors lx: x should be in pred(lx)
  private def compareSuccWithPred(lsuccs: List[(AST, List[AST])], lpreds: List[(AST, List[AST])], env: ASTEnv): Boolean = {
    // check that number of nodes match
    if (lsuccs.size != lpreds.size) {
      println("number of nodes in ccfg does not match")
      return false
    }

    // check that number of edges match
    var res = true
    var succ_edges: List[(AST, AST)] = List()
    for ((ast_elem, succs) <- lsuccs) {
      for (succ <- succs) {
        succ_edges = (ast_elem, succ) :: succ_edges
      }
    }

    var pred_edges: List[(AST, AST)] = List()
    for ((ast_elem, preds) <- lpreds) {
      for (pred <- preds) {
        pred_edges = (ast_elem, pred) :: pred_edges
      }
    }

    // check succ/pred connection and print out missing connections
    // given two ast elems:
    //   a
    //   b
    // we check (a1, b1) successor
    // against  (b2, a2) predecessor
    for ((a1, b1) <- succ_edges) {
      var isin = false
      for ((b2, a2) <- pred_edges) {
        if (a1.eq(a2) && b1.eq(b2))
          isin = true
      }
      if (!isin) {
        errors = new CCFGError("is missing in preds", b1, env.featureExpr(b1), a1, env.featureExpr(a1)) :: errors
        res = false
      }
    }

    // check pred/succ connection and print out missing connections
    // given two ast elems:
    //  a
    //  b
    // we check (b1, a1) predecessor
    // against  (a2, b2) successor
    for ((b1, a1) <- pred_edges) {
      var isin = false
      for ((a2, b2) <- succ_edges) {
        if (a1.eq(a2) && b1.eq(b2))
          isin = true
      }
      if (!isin) {
        errors = new CCFGError("is missing in succs", a1, env.featureExpr(a1), b1, env.featureExpr(b1)) :: errors
        res = false
      }
    }

    res
  }

  var errors = List[CCFGError]()
  val liveness = "liveness.csv"

  def checkCfG(fileName: String) = {

    // file-output
    appendToFile(liveness, "filename;family-based;full-coverage;full-coverage-configs")

    // family-based
    println("checking family-based")
    val family_ast = prepareAST[TranslationUnit](tunit.asInstanceOf[TranslationUnit])
    val family_env = createASTEnv(family_ast)
    val family_function_defs = filterASTElems[FunctionDef](family_ast)

    val tfams = System.currentTimeMillis()
    family_function_defs.map(intraCfGFunctionDef(_, family_env))
    val tfame = System.currentTimeMillis()

    val tfam = tfame - tfams

    // base variant
    println("checking base variant")
    val base_ast = prepareAST[TranslationUnit](
      deriveProductFromConfiguration[TranslationUnit](family_ast.asInstanceOf[TranslationUnit], new Configuration(FeatureExpr.base, fm), family_env))
    val base_env = createASTEnv(base_ast)
    val base_function_defs = filterASTElems[FunctionDef](base_ast)

    val tbases = System.currentTimeMillis()
    base_function_defs.map(intraCfGFunctionDef(_, base_env))
    val tbasee = System.currentTimeMillis()

    val tbase = tbasee - tbases

    // full coverage
    println("checking full coverage")
    val configs = ConfigurationCoverage.naiveCoverageAny(family_ast, fm, family_env.asInstanceOf[ConfigurationCoverage.ASTEnv])
    var current_config = 1
    var tfullcoverage: Long = 0

    for (config <- configs) {
      println("checking configuration " + current_config + " of " + configs.size)
      current_config += 1
      val product_ast = prepareAST[TranslationUnit](deriveProductFromConfiguration[TranslationUnit](family_ast, new Configuration(config, fm), family_env))
      val product_env = createASTEnv(product_ast)
      val product_function_defs = filterASTElems[FunctionDef](product_ast)

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
    val myenv = createASTEnv(f)

    val ss = if (f.stmt.innerStatements.isEmpty) List() else getAllSucc(f.stmt.innerStatements.head.entry, myenv).map(_._1).filterNot(_.isInstanceOf[FunctionDef])
    for (s <- ss.reverse) {
      in(s, myenv)
      out(s, myenv)
    }

    true
  }
}
