package org.sat4j.minisat.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.opt.MaxSatDecorator;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;
import org.sat4j.tools.OptToSatAdapter;

public class Bug275101 {

	@Test
	public void testMaxSAtIteratorIfSat() throws ContradictionException,
			TimeoutException {
		ISolver solver = new ModelIterator(new OptToSatAdapter(
				new MaxSatDecorator(SolverFactory.newDefault())));
		solver.newVar(3);
		IVecInt literals = new VecInt();
		literals.push(-1).push(-2).push(3);
		solver.addClause(literals);
		literals.clear();
		literals.push(-1).push(2);
		solver.addClause(literals);
		literals.clear();
		literals.push(-1).push(-3);
		solver.addClause(literals);
		literals.clear();
		assertTrue(solver.isSatisfiable());
		assertEquals(3, solver.model().length);
		System.out.println("" + solver.model(1) + solver.model(2)
				+ solver.model(3));
		assertTrue(solver.isSatisfiable());
		assertEquals(3, solver.model().length);
		System.out.println("" + solver.model(1) + solver.model(2)
				+ solver.model(3));
		assertTrue(solver.isSatisfiable());
		assertEquals(3, solver.model().length);
		System.out.println("" + solver.model(1) + solver.model(2)
				+ solver.model(3));
		assertTrue(solver.isSatisfiable());
		assertEquals(3, solver.model().length);
		System.out.println("" + solver.model(1) + solver.model(2)
				+ solver.model(3));
		assertFalse(solver.isSatisfiable());
	}

	@Test
	public void testMaxSAtIterator() throws ContradictionException,
			TimeoutException {
		ISolver solver = new ModelIterator(new OptToSatAdapter(
				new MaxSatDecorator(SolverFactory.newDefault())));
		solver.newVar(2);
		IVecInt literals = new VecInt();
		literals.push(-1).push(-2);
		solver.addClause(literals);
		literals.clear();
		literals.push(-1).push(2);
		solver.addClause(literals);
		literals.clear();
		literals.push(1).push(-2);
		solver.addClause(literals);
		literals.clear();
		literals.push(1).push(2);
		solver.addClause(literals);
		assertTrue(solver.isSatisfiable());
		assertEquals(2, solver.model().length);
		System.out.println("" + solver.model(1) + solver.model(2));
		assertTrue(solver.isSatisfiable());
		assertEquals(2, solver.model().length);
		System.out.println("" + solver.model(1) + solver.model(2));
		assertTrue(solver.isSatisfiable());
		assertEquals(2, solver.model().length);
		System.out.println("" + solver.model(1) + solver.model(2));
		assertTrue(solver.isSatisfiable());
		assertEquals(2, solver.model().length);
		System.out.println("" + solver.model(1) + solver.model(2));
		assertFalse(solver.isSatisfiable());
	}
}
