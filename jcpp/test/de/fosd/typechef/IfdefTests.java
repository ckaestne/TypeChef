package de.fosd.typechef;

import org.anarres.cpp.Main;
import org.junit.*;

import scala.actors.threadpool.Arrays;

import de.fosd.typechef.featureexpr.*;


public class IfdefTests {

	@Test @Ignore
	public void testMacroContext(){
		MacroContext c = new MacroContext();
		System.out.println(c);
		c=c.define("test",new BaseFeature(),"=>0");
		System.out.println(Arrays.toString(c.getMacroExpansions("test")));//0 if BASE
		c=c.define("test",new BaseFeature(),"=>1");
		System.out.println(Arrays.toString(c.getMacroExpansions("test")));//1 if BASE; 0 if DEAD
		c=c.define("test",new DefinedExternal("X"),"=>2");
		System.out.println(Arrays.toString(c.getMacroExpansions("test")));//2 if X; 1 if !X; 0 if DEAD
		c=c.define("test",new BaseFeature(),"=>3");
		System.out.println(Arrays.toString(c.getMacroExpansions("test")));//3 if BASE
		
		c = new MacroContext();
		System.out.println(c);
		c=c.define("test",new DefinedExternal("X"),"=>0");
		System.out.println(Arrays.toString(c.getMacroExpansions("test")));//0 if X
	}
  
	@Test @Ignore
	public void testFeatureExprLib(){
//		Defined$ d=Defined$.MODULE$;
//		System.out.println(new FeatureExpr().test());
		System.out.println(new Not(new FeatureExpr$().createDefined("test",new MacroContext())));
	}
	
	@Test @Ignore
	public void testIfdef1() throws Exception {
		Main.main(new String[] { "test/tc_data/in1.c","-I","test/tc_data/"// ,"--debug"
				});

	}
 
 @Test @Ignore
	public void testIfdef2() throws Exception {
		Main.main(new String[] { "test/tc_data/undef.c","-I","test/tc_data/"// ,"--debug"
				});

	}
 
  @Test 
	public void testMacros() throws Exception {
		Main.main(new String[] { "test/tc_data/macro.c","-I","test/tc_data/"// ,"--debug"
				});

	}
  
  @Test@Ignore
	public void testIfSimplify() throws Exception {
		FeatureExpr expr=new IfExpr(new DefinedExternal("a"),new IntegerLit(2),new IntegerLit(0));
		System.out.println(expr);
		System.out.println(expr.simplify());

		FeatureExpr expr2=new IfExpr(new DefinedExternal("b"),new IntegerLit(1),expr);
		System.out.println(expr2);
		System.out.println(expr2.simplify());

		FeatureExpr expr3=new FeatureExpr$().createEquals(expr2,new IntegerLit(1));
		System.out.println(expr3);
		System.out.println(expr3.simplify());
	}

}
