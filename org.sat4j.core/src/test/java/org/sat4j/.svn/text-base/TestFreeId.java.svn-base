package org.sat4j;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;

public class TestFreeId {

	private ISolver solver;
	
	@Before
	public void setUp() {
		solver = SolverFactory.newDefault();
	}
	
	@Test
	public void testEmptySolver() {
		assertEquals(1,solver.nextFreeVarId(false));
		solver.newVar(100);
		assertEquals(101,solver.nextFreeVarId(false));
	}
	
	@Test
	public void testIncrementalFeed() throws ContradictionException {
		assertEquals(1,solver.nextFreeVarId(false));
		IVecInt clause = new VecInt();
		clause.push(3).push(-5);
		solver.addClause(clause);
		assertEquals(6,solver.nextFreeVarId(false));
		clause.clear();
		clause.push(1).push(-2);
		solver.addClause(clause);
		assertEquals(6,solver.nextFreeVarId(false));
		clause.clear();
		clause.push(1000).push(-31);
		solver.addClause(clause);
		assertEquals(1001,solver.nextFreeVarId(false));
	}
	
	@Test
	public void testReserveParameter() {
		assertEquals(1,solver.nextFreeVarId(false));
		assertEquals(1,solver.nextFreeVarId(false));
		assertEquals(1,solver.nextFreeVarId(false));
		assertEquals(1,solver.nextFreeVarId(false));
		assertEquals(1,solver.nextFreeVarId(true));
		assertEquals(2,solver.nextFreeVarId(true));
		assertEquals(3,solver.nextFreeVarId(false));
		assertEquals(3,solver.nextFreeVarId(false));
	}
}
