package org.sat4j.minisat.core;

import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class BugThomas {

	@Test
	public void testBugReport() throws ContradictionException, TimeoutException {
		ISolver solver = SolverFactory.newDefault();
		solver.newVar(3);
		solver.addClause(new VecInt(new int[] { 1 }));
		solver.addClause(new VecInt(new int[] { -1, 2 }));
		solver.addClause(new VecInt(new int[] { 1, -2 }));
		solver.addClause(new VecInt(new int[] { -3 }));
		solver.newVar(1);
		// solver.addClause(new VecInt(new int[] {4, -4}));
		solver.isSatisfiable(new VecInt(new int[] { -4 }));
	}
}
