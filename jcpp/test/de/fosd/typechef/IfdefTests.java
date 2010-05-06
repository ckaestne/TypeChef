package de.fosd.typechef;

import org.anarres.cpp.Main;
import org.junit.Test;

import de.fosd.typechef.featureexpr.*;
import de.fosd.typechef.featureexpr.Not;


public class IfdefTests {

	@Test
	public void testFeatureExprLib(){
//		Defined$ d=Defined$.MODULE$;
//		System.out.println(new FeatureExpr().test());
//		System.out.println(new Not(new FeatureExpr$().createDefined("test",new MacroContext())));
	}
	
//	@Test
//	public void testIfdef1() throws Exception {
//		Main.main(new String[] { "test/tc_data/in1.c","-I","test/tc_data/"// ,"--debug"
//				});
//
//	}
 
 @Test
	public void testIfdef2() throws Exception {
		Main.main(new String[] { "test/tc_data/undef.c","-I","test/tc_data/"// ,"--debug"
				});

	}

}
