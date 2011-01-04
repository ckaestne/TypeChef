package org.sat4j.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.sat4j.specs.IConstr;
import org.sat4j.specs.Lbool;
import org.sat4j.specs.SearchListener;

/**
 * @since 2.2
 */
public class DecisionTracing implements SearchListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String filename;
	private PrintStream out;

	public DecisionTracing(String filename) {
		this.filename = filename;
	}

	private void updateWriter() {
		try {
			out = new PrintStream(new FileOutputStream(filename + ".dat"));
		} catch (FileNotFoundException e) {
			out = System.out;
		}
	}

	public void adding(int p) {
		// TODO Auto-generated method stub

	}

	public void assuming(int p) {
		out.println(Math.abs(p));
	}

	public void backtracking(int p) {
		// TODO Auto-generated method stub

	}

	public void beginLoop() {
		// TODO Auto-generated method stub

	}

	public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
		// TODO Auto-generated method stub

	}

	public void conflictFound(int p) {
		// TODO Auto-generated method stub

	}

	public void delete(int[] clause) {
		// TODO Auto-generated method stub

	}

	public void end(Lbool result) {
		out.close();

	}

	public void learn(IConstr c) {
		// TODO Auto-generated method stub

	}

	public void propagating(int p, IConstr reason) {
		// TODO Auto-generated method stub

	}

	public void solutionFound() {
		// TODO Auto-generated method stub

	}

	public void start() {
		updateWriter();
	}

	public void restarting() {
		// out.close();
		// restartNumber++;
		// updateWriter();
	}

	public void backjump(int backjumpLevel) {
	}

}
