package org.sat4j.tools;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

public class TestGateTranslator {

	private ISolver solver;
	private GateTranslator gator;

	@Before
	public void startUp() {
		solver = SolverFactory.newDefault();
		gator = new GateTranslator(solver);
	}

	@Test
	public void testTwoValues() throws ContradictionException {
		IVecInt literals = new VecInt().push(1).push(2);
		IVec<BigInteger> coefs = new Vec<BigInteger>().push(
				BigInteger.valueOf(3)).push(BigInteger.valueOf(6));
		IVecInt result = new VecInt();
		gator.optimisationFunction(literals, coefs, result);
		System.out.println(result);
		assertEquals(4, result.size());

	}

}
