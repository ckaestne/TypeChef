package org.sat4j.tools;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.SearchListener;
import org.sat4j.specs.TimeoutException;

public abstract class AbstractOutputSolver implements ISolver {

	protected int nbvars;

	protected int nbclauses;

	protected boolean fixedNbClauses = false;

	protected boolean firstConstr = true;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean removeConstr(IConstr c) {
		throw new UnsupportedOperationException();
	}

	public void addAllClauses(IVec<IVecInt> clauses)
			throws ContradictionException {
		throw new UnsupportedOperationException();
	}

	public void setTimeout(int t) {
		// TODO Auto-generated method stub

	}

	public void setTimeoutMs(long t) {
		// TODO Auto-generated method stub
	}

	public int getTimeout() {
		return 0;
	}

	/**
	 * @since 2.1
	 */
	public long getTimeoutMs() {
		return 0L;
	}

	public void expireTimeout() {
		// TODO Auto-generated method stub

	}

	public boolean isSatisfiable(IVecInt assumps, boolean global)
			throws TimeoutException {
		throw new TimeoutException("There is no real solver behind!");
	}

	public boolean isSatisfiable(boolean global) throws TimeoutException {
		throw new TimeoutException("There is no real solver behind!");
	}

	public void printInfos(PrintWriter output, String prefix) {
	}

	public void setTimeoutOnConflicts(int count) {

	}

	public boolean isDBSimplificationAllowed() {
		return false;
	}

	public void setDBSimplificationAllowed(boolean status) {

	}

	public void printStat(PrintStream output, String prefix) {
		// TODO Auto-generated method stub
	}

	public void printStat(PrintWriter output, String prefix) {
		// TODO Auto-generated method stub

	}

	public Map<String, Number> getStat() {
		// TODO Auto-generated method stub
		return null;
	}

	public void clearLearntClauses() {
		// TODO Auto-generated method stub

	}

	public int[] model() {
		throw new UnsupportedOperationException();
	}

	public boolean model(int var) {
		throw new UnsupportedOperationException();
	}

	public boolean isSatisfiable() throws TimeoutException {
		throw new TimeoutException("There is no real solver behind!");
	}

	public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
		throw new TimeoutException("There is no real solver behind!");
	}

	public int[] findModel() throws TimeoutException {
		throw new UnsupportedOperationException();
	}

	public int[] findModel(IVecInt assumps) throws TimeoutException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @since 2.1
	 */
	public boolean removeSubsumedConstr(IConstr c) {
		return false;
	}

	/**
	 * @since 2.1
	 */
	public IConstr addBlockingClause(IVecInt literals)
			throws ContradictionException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @since 2.2
	 */
	public SearchListener getSearchListener() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @since 2.1
	 */
	public void setSearchListener(SearchListener sl) {
	}

	/**
	 * @since 2.2
	 */
	public boolean isVerbose() {
		return true;
	}

	/**
	 * @since 2.2
	 */
	public void setVerbose(boolean value) {
		// do nothing
	}

	/**
	 * @since 2.2
	 */
	public void setLogPrefix(String prefix) {
		// do nothing

	}

	/**
	 * @since 2.2
	 */
	public String getLogPrefix() {
		return "";
	}

	/**
	 * @since 2.2
	 */
	public IVecInt unsatExplanation() {
		throw new UnsupportedOperationException();
	}
}
