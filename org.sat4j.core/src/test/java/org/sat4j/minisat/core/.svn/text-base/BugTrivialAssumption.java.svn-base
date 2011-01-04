package org.sat4j.minisat.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

public class BugTrivialAssumption {

	@Test
	public void testUnitClauseInFormulaUnsat() throws ContradictionException,
			TimeoutException {
		final ISolver solver = SolverFactory.newDefault();
		solver.newVar(1);
		solver.addClause(new VecInt(new int[] { 1 }));
		assertFalse(solver.isSatisfiable(new VecInt(new int[] { -1 })));
		IVecInt explanation = solver.unsatExplanation();
		assertTrue(explanation.contains(-1));
		assertEquals(1, explanation.size());
	}

	@Test
	public void testUnitClauseInFormulaSat() throws ContradictionException,
			TimeoutException {
		final ISolver solver = SolverFactory.newDefault();
		solver.addClause(new VecInt(new int[] { 1 }));
		assertTrue(solver.isSatisfiable(new VecInt(new int[] { 1 })));
	}

	@Test
	public void testBinaryClauseInFormula() throws ContradictionException,
			TimeoutException {
		final ISolver solver = SolverFactory.newDefault();
		solver.addClause(new VecInt(new int[] { 1, 2 }));
		assertFalse(solver.isSatisfiable(new VecInt(new int[] { -1, -2 })));
		IVecInt explanation = solver.unsatExplanation();
		assertTrue(explanation.contains(-1));
		assertTrue(explanation.contains(-2));
		assertEquals(2, explanation.size());
	}

	@Test
	public void testEasyInconsistencyInAssumption()
			throws ContradictionException, TimeoutException {
		final ISolver solver = SolverFactory.newDefault();
		solver.newVar(1);
		assertFalse(solver.isSatisfiable(new VecInt(new int[] { -1, 1 })));
		IVecInt explanation = solver.unsatExplanation();
		assertTrue(explanation.contains(-1));
		assertTrue(explanation.contains(1));
		assertEquals(2, explanation.size());
	}

	@Test
	public void testInconsistencyInAssumption() throws ContradictionException,
			TimeoutException {
		final ISolver solver = SolverFactory.newDefault();
		solver.newVar(3);
		assertFalse(solver.isSatisfiable(new VecInt(new int[] { -1, 2, 3, 1 })));
		IVecInt explanation = solver.unsatExplanation();
		assertTrue(explanation.contains(-1));
		assertTrue(explanation.contains(1));
		assertEquals(2, explanation.size());
	}

	@Test
	public void testVoidFormula() throws ContradictionException,
			TimeoutException {
		final ISolver solver = SolverFactory.newDefault();
		assertTrue(solver.isSatisfiable(new VecInt(new int[] { -1 })));
		assertTrue(solver.isSatisfiable(new VecInt(new int[] { 1 })));
	}
}
