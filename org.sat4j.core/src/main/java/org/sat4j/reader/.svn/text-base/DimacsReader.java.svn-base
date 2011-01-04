/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004-2008 Daniel Le Berre
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
 * 
 * Based on the original MiniSat specification from:
 * 
 * An extensible SAT solver. Niklas Een and Niklas Sorensson. Proceedings of the
 * Sixth International Conference on Theory and Applications of Satisfiability
 * Testing, LNCS 2919, pp 502-518, 2003.
 *
 * See www.minisat.se for the original solver in C++.
 * 
 *******************************************************************************/
package org.sat4j.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;

/**
 * Very simple Dimacs file parser. Allow solvers to read the constraints from a
 * Dimacs formatted file. It should be used that way:
 * 
 * <pre>
 * DimacsReader solver = new DimacsReader(SolverFactory.OneSolver());
 * solver.readInstance(&quot;mybench.cnf&quot;);
 * if (solver.isSatisfiable()) {
 * 	// SAT case
 * } else {
 * 	// UNSAT case
 * }
 * </pre>
 * 
 * That parser is not used for efficiency reasons. It will be updated with Java
 * 1.5 scanner feature.
 * 
 * @version 1.0
 * @author dlb
 * @author or
 */
public class DimacsReader extends Reader implements Serializable {

	private static final long serialVersionUID = 1L;

	protected int expectedNbOfConstr; // as announced on the p cnf line

	protected final ISolver solver;

	private boolean checkConstrNb = true;

	protected final String formatString;

	/**
	 * @since 2.1
	 */
	protected EfficientScanner scanner;

	public DimacsReader(ISolver solver) {
		this(solver, "cnf");
	}

	public DimacsReader(ISolver solver, String format) {
		this.solver = solver;
		formatString = format;
	}

	public void disableNumberOfConstraintCheck() {
		checkConstrNb = false;
	}

	/**
	 * Skip comments at the beginning of the input stream.
	 * 
	 * @param in
	 *            the input stream
	 * @throws IOException
	 *             if an IO problem occurs.
	 * @since 2.1
	 */
	protected void skipComments() throws IOException {
		scanner.skipComments();
	}

	/**
	 * @param in
	 *            the input stream
	 * @throws IOException
	 *             iff an IO occurs
	 * @throws ParseFormatException
	 *             if the input stream does not comply with the DIMACS format.
	 * @since 2.1
	 */
	protected void readProblemLine() throws IOException, ParseFormatException {

		String line = scanner.nextLine().trim();

		if (line == null) {
			throw new ParseFormatException(
					"premature end of file: <p cnf ...> expected");
		}
		String[] tokens = line.split("\\s+");
		if (tokens.length < 4 || !"p".equals(tokens[0])
				|| !formatString.equals(tokens[1])) {
			throw new ParseFormatException("problem line expected (p cnf ...)");
		}

		int vars;

		// reads the max var id
		vars = Integer.parseInt(tokens[2]);
		assert vars > 0;
		solver.newVar(vars);
		// reads the number of clauses
		expectedNbOfConstr = Integer.parseInt(tokens[3]);
		assert expectedNbOfConstr > 0;
		solver.setExpectedNumberOfClauses(expectedNbOfConstr);
	}

	/**
	 * @since 2.1
	 */
	protected IVecInt literals = new VecInt();

	/**
	 * @param in
	 *            the input stream
	 * @throws IOException
	 *             iff an IO problems occurs
	 * @throws ParseFormatException
	 *             if the input stream does not comply with the DIMACS format.
	 * @throws ContradictionException
	 *             si le probl?me est trivialement inconsistant.
	 * @since 2.1
	 */
	protected void readConstrs() throws IOException, ParseFormatException,
			ContradictionException {
		int realNbOfConstr = 0;

		literals.clear();
		boolean needToContinue = true;

		while (needToContinue) {
			boolean added = false;
			if (scanner.eof()) {
				// end of file
				if (literals.size() > 0) {
					// no 0 end the last clause
					flushConstraint();
					added = true;
				}
				needToContinue = false;
			} else {
				if (scanner.currentChar() == 'c') {
					// ignore comment line
					scanner.skipRestOfLine();
					continue;
				}
				if (scanner.currentChar() == '%'
						&& expectedNbOfConstr == realNbOfConstr) {
					if (solver.isVerbose()) {
						System.out
								.println("Ignoring the rest of the file (SATLIB format");
					}
					break;
				}
				added = handleLine();
			}
			if (added) {
				realNbOfConstr++;
			}
		}
		if (checkConstrNb && expectedNbOfConstr != realNbOfConstr) {
			throw new ParseFormatException("wrong nbclauses parameter. Found "
					+ realNbOfConstr + ", " + expectedNbOfConstr + " expected");
		}
	}

	/**
	 * 
	 * @throws ContradictionException
	 * @since 2.1
	 */
	protected void flushConstraint() throws ContradictionException {
		try {
			solver.addClause(literals);
		} catch (IllegalArgumentException ex) {
			if (isVerbose()) {
				System.err.println("c Skipping constraint " + literals);
			}
		}
	}

	/**
	 * @since 2.1
	 */
	protected boolean handleLine() throws ContradictionException, IOException,
			ParseFormatException {
		int lit;
		boolean added = false;
		while (!scanner.eof()) {
			lit = scanner.nextInt();
			if (lit == 0) {
				if (literals.size() > 0) {
					flushConstraint();
					literals.clear();
					added = true;
				}
				break;
			}
			literals.push(lit);
		}
		return added;
	}

	@Override
	public IProblem parseInstance(InputStream in) throws ParseFormatException,
			ContradictionException, IOException {
		scanner = new EfficientScanner(in);
		return parseInstance();
	}

	@Override
	public final IProblem parseInstance(final java.io.Reader in)
			throws ParseFormatException, ContradictionException, IOException {
		throw new UnsupportedOperationException();

	}

	/**
	 * @param in
	 *            the input stream
	 * @throws ParseFormatException
	 *             if the input stream does not comply with the DIMACS format.
	 * @throws ContradictionException
	 *             si le probl?me est trivialement inconsitant
	 */
	private IProblem parseInstance() throws ParseFormatException,
			ContradictionException {
		solver.reset();
		try {
			skipComments();
			readProblemLine();
			readConstrs();
			scanner.close();
			return solver;
		} catch (IOException e) {
			throw new ParseFormatException(e);
		} catch (NumberFormatException e) {
			throw new ParseFormatException("integer value expected ");
		}
	}

	@Override
	public String decode(int[] model) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < model.length; i++) {
			stb.append(model[i]);
			stb.append(" ");
		}
		stb.append("0");
		return stb.toString();
	}

	@Override
	public void decode(int[] model, PrintWriter out) {
		for (int i = 0; i < model.length; i++) {
			out.print(model[i]);
			out.print(" ");
		}
		out.print("0");
	}

	protected ISolver getSolver() {
		return solver;
	}
}
