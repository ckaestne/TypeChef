/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004-2006 Daniel Le
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU Lesser General Public License Version 2.1 or later (the
 * "LGPL"), in which case the provisions of the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL, and not to allow others to use your version of
 * this file under the terms of the EPL, indicate your decision by deleting
 * the provisions above and replace them with the notice and other provisions
 * required by the LGPL. If you do not delete the provisions above, a recipient
 * may use your version of this file under the terms of the EPL or the LGPL.
 *******************************************************************************/
package org.sat4j.minisat;

import org.sat4j.core.ASolverFactory;
import org.sat4j.minisat.constraints.MixedDataStructureDanielHT;
import org.sat4j.minisat.constraints.MixedDataStructureDanielWL;
import org.sat4j.minisat.constraints.MixedDataStructureSingleWL;
import org.sat4j.minisat.core.DataStructureFactory;
import org.sat4j.minisat.core.IOrder;
import org.sat4j.minisat.core.SearchParams;
import org.sat4j.minisat.core.Solver;
import org.sat4j.minisat.learning.LimitedLearning;
import org.sat4j.minisat.learning.MiniSATLearning;
import org.sat4j.minisat.learning.NoLearningButHeuristics;
import org.sat4j.minisat.learning.PercentLengthLearning;
import org.sat4j.minisat.orders.PhaseCachingAutoEraseStrategy;
import org.sat4j.minisat.orders.PureOrder;
import org.sat4j.minisat.orders.RSATLastLearnedClausesPhaseSelectionStrategy;
import org.sat4j.minisat.orders.RSATPhaseSelectionStrategy;
import org.sat4j.minisat.orders.RandomWalkDecorator;
import org.sat4j.minisat.orders.VarOrderHeap;
import org.sat4j.minisat.restarts.ArminRestarts;
import org.sat4j.minisat.restarts.LubyRestarts;
import org.sat4j.minisat.restarts.MiniSATRestarts;
import org.sat4j.minisat.restarts.NoRestarts;
import org.sat4j.opt.MinOneDecorator;
import org.sat4j.specs.ISolver;
import org.sat4j.tools.DimacsOutputSolver;
import org.sat4j.tools.OptToSatAdapter;

/**
 * User friendly access to pre-constructed solvers.
 * 
 * @author leberre
 */
public final class SolverFactory extends ASolverFactory<ISolver> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	// thread safe implementation of the singleton design pattern
	private static SolverFactory instance;

	/**
	 * Private constructor. Use singleton method instance() instead.
	 * 
	 * @see #instance()
	 */
	private SolverFactory() {
		super();
	}

	private static synchronized void createInstance() {
		if (instance == null) {
			instance = new SolverFactory();
		}
	}

	/**
	 * Access to the single instance of the factory.
	 * 
	 * @return the singleton of that class.
	 */
	public static SolverFactory instance() {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}

	/**
	 * @return a "default" "minilearning" solver learning clauses of size
	 *         smaller than 10 % of the total number of variables with a heap
	 *         based var order.
	 */
	public static Solver<DataStructureFactory> newMiniLearningHeap() {
		return newMiniLearningHeap(new MixedDataStructureDanielWL());
	}

	public static Solver<DataStructureFactory> newMiniLearningHeapEZSimp() {
		Solver<DataStructureFactory> solver = newMiniLearningHeap();
		solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
		return solver;
	}

	public static Solver<DataStructureFactory> newMiniLearningHeapExpSimp() {
		Solver<DataStructureFactory> solver = newMiniLearningHeap();
		solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
		return solver;
	}

	public static Solver<DataStructureFactory> newMiniLearningHeapRsatExpSimp() {
		Solver<DataStructureFactory> solver = newMiniLearningHeapExpSimp();
		solver.setOrder(new VarOrderHeap(new RSATPhaseSelectionStrategy()));
		return solver;
	}

	public static Solver<DataStructureFactory> newMiniLearningHeapRsatExpSimpBiere() {
		Solver<DataStructureFactory> solver = newMiniLearningHeapRsatExpSimp();
		solver.setRestartStrategy(new ArminRestarts());
		solver.setSearchParams(new SearchParams(1.1, 100));
		return solver;
	}

	public static Solver<DataStructureFactory> newMiniLearningHeapRsatExpSimpLuby() {
		Solver<DataStructureFactory> solver = newMiniLearningHeapRsatExpSimp();
		solver.setRestartStrategy(new LubyRestarts());
		return solver;
	}

	private static Solver<DataStructureFactory> newBestCurrentSolverConfiguration(
			DataStructureFactory dsf) {
		MiniSATLearning<DataStructureFactory> learning = new MiniSATLearning<DataStructureFactory>();
		Solver<DataStructureFactory> solver = new Solver<DataStructureFactory>(
				learning, dsf, new VarOrderHeap(
						new RSATPhaseSelectionStrategy()), new ArminRestarts());
		solver.setSearchParams(new SearchParams(1.1, 100));
		learning.setSolver(solver);
		solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
		return solver;
	}

	/**
	 * 
	 * @since 2.2
	 */
	public static Solver<DataStructureFactory> newGreedySolver() {
		MiniSATLearning<DataStructureFactory> learning = new MiniSATLearning<DataStructureFactory>();
		Solver<DataStructureFactory> solver = new Solver<DataStructureFactory>(
				learning, new MixedDataStructureDanielWL(),
				new RandomWalkDecorator(new VarOrderHeap(
						new RSATPhaseSelectionStrategy())), new NoRestarts());
		// solver.setSearchParams(new SearchParams(1.1, 100));
		learning.setSolver(solver);
		solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
		return solver;
	}

	/**
	 * @since 2.2
	 */
	public static Solver<DataStructureFactory> newDefaultAutoErasePhaseSaving() {
		Solver<DataStructureFactory> solver = newBestWL();
		solver.setOrder(new VarOrderHeap(new PhaseCachingAutoEraseStrategy()));
		return solver;
	}

	/**
	 * @since 2.2.3
	 */
	public static Solver<DataStructureFactory> newDefaultMS21PhaseSaving() {
		Solver<DataStructureFactory> solver = newBestWL();
		solver.setOrder(new VarOrderHeap(
				new RSATLastLearnedClausesPhaseSelectionStrategy()));
		return solver;
	}

	/**
	 * @since 2.1
	 */
	public static Solver<DataStructureFactory> newBestWL() {
		return newBestCurrentSolverConfiguration(new MixedDataStructureDanielWL());
	}

	/**
	 * 
	 * @since 2.1
	 */
	public static Solver<DataStructureFactory> newBestHT() {
		return newBestCurrentSolverConfiguration(new MixedDataStructureDanielHT());
	}

	/**
	 * 
	 * @since 2.2
	 */
	public static Solver<DataStructureFactory> newBestSingleWL() {
		return newBestCurrentSolverConfiguration(new MixedDataStructureSingleWL());
	}

	/**
	 * 
	 * @since 2.2
	 */
	public static Solver<DataStructureFactory> newBest17() {
		Solver<DataStructureFactory> solver = newBestCurrentSolverConfiguration(new MixedDataStructureSingleWL());
		solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION_WLONLY);
		solver.setLearnedConstraintsDeletionStrategy(solver.memory_based);
		LimitedLearning<DataStructureFactory> learning = new PercentLengthLearning<DataStructureFactory>(
				10);
		solver.setLearner(learning);
		learning.setSolver(solver);
		return solver;
	}

	/**
	 * @since 2.1
	 */
	public static Solver<DataStructureFactory> newGlucose() {
		Solver<DataStructureFactory> solver = newBestWL();
		solver.setLearnedConstraintsDeletionStrategy(solver.glucose);
		solver.setRestartStrategy(new LubyRestarts(512));
		return solver;
	}

	/**
	 * @param dsf
	 *            a specific data structure factory
	 * @return a default "minilearning" solver using a specific data structure
	 *         factory, learning clauses of length smaller or equals to 10 % of
	 *         the number of variables and a heap based VSIDS heuristics
	 */
	public static Solver<DataStructureFactory> newMiniLearningHeap(
			DataStructureFactory dsf) {
		return newMiniLearning(dsf, new VarOrderHeap());
	}

	/**
	 * @return a default minilearning SAT solver choosing periodically to branch
	 *         on "pure watched" literals if any. (a pure watched literal l is a
	 *         literal that is watched on at least one clause such that its
	 *         negation is not watched at all. It is not necessarily a watched
	 *         literal.)
	 */
	public static Solver<DataStructureFactory> newMiniLearningPure() {
		return newMiniLearning(new MixedDataStructureDanielWL(),
				new PureOrder());
	}

	/**
	 * @param dsf
	 *            the data structure factory used to represent literals and
	 *            clauses
	 * @param order
	 *            the heuristics
	 * @return a SAT solver with learning limited to clauses of length smaller
	 *         or equal to 10 percent of the total number of variables, the dsf
	 *         data structure, the FirstUIP clause generator and order as
	 *         heuristics.
	 */
	public static Solver<DataStructureFactory> newMiniLearning(
			DataStructureFactory dsf, IOrder order) {
		// LimitedLearning<DataStructureFactory> learning = new
		// PercentLengthLearning<DataStructureFactory>(10);
		MiniSATLearning<DataStructureFactory> learning = new MiniSATLearning<DataStructureFactory>();
		Solver<DataStructureFactory> solver = new Solver<DataStructureFactory>(
				learning, dsf, order, new MiniSATRestarts());
		learning.setSolver(solver);
		return solver;
	}

	/**
	 * @return a default MiniLearning without restarts.
	 */
	public static Solver<DataStructureFactory> newMiniLearningHeapEZSimpNoRestarts() {
		LimitedLearning<DataStructureFactory> learning = new PercentLengthLearning<DataStructureFactory>(
				10);
		Solver<DataStructureFactory> solver = new Solver<DataStructureFactory>(
				learning, new MixedDataStructureDanielWL(), new SearchParams(
						Integer.MAX_VALUE), new VarOrderHeap(),
				new MiniSATRestarts());
		learning.setSolver(solver);
		solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
		return solver;
	}

	/**
	 * @return a default MiniLearning with restarts beginning at 1000 conflicts.
	 */
	public static Solver<DataStructureFactory> newMiniLearningHeapEZSimpLongRestarts() {
		LimitedLearning<DataStructureFactory> learning = new PercentLengthLearning<DataStructureFactory>(
				10);
		Solver<DataStructureFactory> solver = new Solver<DataStructureFactory>(
				learning, new MixedDataStructureDanielWL(), new SearchParams(
						1000), new VarOrderHeap(), new MiniSATRestarts());
		learning.setSolver(solver);
		solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
		return solver;
	}

	/**
	 * @return a SAT solver very close to the original MiniSAT sat solver.
	 */
	public static Solver<DataStructureFactory> newMiniSATHeap() {
		return newMiniSATHeap(new MixedDataStructureDanielWL());
	}

	/**
	 * @return a SAT solver very close to the original MiniSAT sat solver
	 *         including easy reason simplification.
	 */
	public static Solver<DataStructureFactory> newMiniSATHeapEZSimp() {
		Solver<DataStructureFactory> solver = newMiniSATHeap();
		solver.setSimplifier(solver.SIMPLE_SIMPLIFICATION);
		return solver;
	}

	public static Solver<DataStructureFactory> newMiniSATHeapExpSimp() {
		Solver<DataStructureFactory> solver = newMiniSATHeap();
		solver.setSimplifier(solver.EXPENSIVE_SIMPLIFICATION);
		return solver;
	}

	public static Solver<DataStructureFactory> newMiniSATHeap(
			DataStructureFactory dsf) {
		MiniSATLearning<DataStructureFactory> learning = new MiniSATLearning<DataStructureFactory>();
		Solver<DataStructureFactory> solver = new Solver<DataStructureFactory>(
				learning, dsf, new VarOrderHeap(), new MiniSATRestarts());
		learning.setDataStructureFactory(solver.getDSFactory());
		learning.setVarActivityListener(solver);
		return solver;
	}

	/**
	 * @return MiniSAT with VSIDS heuristics, FirstUIP clause generator for
	 *         backjumping but no learning.
	 */
	public static Solver<MixedDataStructureDanielWL> newBackjumping() {
		NoLearningButHeuristics<MixedDataStructureDanielWL> learning = new NoLearningButHeuristics<MixedDataStructureDanielWL>();
		Solver<MixedDataStructureDanielWL> solver = new Solver<MixedDataStructureDanielWL>(
				learning, new MixedDataStructureDanielWL(), new VarOrderHeap(),
				new MiniSATRestarts());
		learning.setVarActivityListener(solver);
		return solver;
	}

	/**
	 * @return a solver computing models with a minimum number of satisfied
	 *         literals.
	 */
	public static ISolver newMinOneSolver() {
		return new OptToSatAdapter(new MinOneDecorator(newDefault()));
	}

	/**
	 * Default solver of the SolverFactory. This solver is meant to be used on
	 * challenging SAT benchmarks.
	 * 
	 * @return the best "general purpose" SAT solver available in the factory.
	 * @see #defaultSolver() the same method, polymorphic, to be called from an
	 *      instance of ASolverFactory.
	 */
	public static ISolver newDefault() {
		return newMiniLearningHeapRsatExpSimpBiere();
	}

	@Override
	public ISolver defaultSolver() {
		return newDefault();
	}

	/**
	 * Small footprint SAT solver.
	 * 
	 * @return a SAT solver suitable for solving small/easy SAT benchmarks.
	 * @see #lightSolver() the same method, polymorphic, to be called from an
	 *      instance of ASolverFactory.
	 */
	public static ISolver newLight() {
		return newMiniLearningHeap();
	}

	@Override
	public ISolver lightSolver() {
		return newLight();
	}

	public static ISolver newDimacsOutput() {
		return new DimacsOutputSolver();
	}

}
