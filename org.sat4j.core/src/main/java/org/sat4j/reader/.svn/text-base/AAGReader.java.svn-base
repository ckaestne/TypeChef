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
import java.io.PrintWriter;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.tools.GateTranslator;

/**
 * Reader for the ASCII And Inverter Graph format defined by Armin Biere.
 * 
 * @author daniel
 * 
 */
public class AAGReader extends Reader {

	private final static int FALSE = 0;

	private final static int TRUE = 1;

	private final GateTranslator solver;

	private int maxvarid;

	private int nbinputs;

	AAGReader(ISolver s) {
		solver = new GateTranslator(s);
	}

	@Override
	public String decode(int[] model) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < nbinputs; i++) {
			stb.append(model[i] > 0 ? 1 : 0);
		}
		return stb.toString();
	}

	@Override
	public void decode(int[] model, PrintWriter out) {
		for (int i = 0; i < nbinputs; i++) {
			out.print(model[i] > 0 ? 1 : 0);
		}
	}

	@Override
	public IProblem parseInstance(java.io.InputStream in)
			throws ParseFormatException, ContradictionException, IOException {
		EfficientScanner scanner = new EfficientScanner(in);
		String prefix = scanner.next();
		if (!"aag".equals(prefix)) {
			throw new ParseFormatException("AAG format only!");
		}
		maxvarid = scanner.nextInt();
		nbinputs = scanner.nextInt();
		int nblatches = scanner.nextInt();
		int nboutputs = scanner.nextInt();
		if (nboutputs > 1) {
			throw new ParseFormatException(
					"CNF conversion allowed for single output circuit only!");
		}
		int nbands = scanner.nextInt();
		solver.newVar(maxvarid + 1);
		solver.setExpectedNumberOfClauses(3 * nbands + 2);
		readInput(nbinputs, scanner);
		assert nblatches == 0;
		if (nboutputs > 0) {
			int output0 = readOutput(nboutputs, scanner);
			readAnd(nbands, output0, scanner);
		}
		return solver;
	}

	private void readAnd(int nbands, int output0, EfficientScanner scanner)
			throws ContradictionException, IOException, ParseFormatException {

		for (int i = 0; i < nbands; i++) {
			int lhs = scanner.nextInt();
			int rhs0 = scanner.nextInt();
			int rhs1 = scanner.nextInt();
			solver.and(toDimacs(lhs), toDimacs(rhs0), toDimacs(rhs1));
		}
		solver.gateTrue(maxvarid + 1);
		solver.gateTrue(toDimacs(output0));
	}

	private int toDimacs(int v) {
		if (v == FALSE) {
			return -(maxvarid + 1);
		}
		if (v == TRUE) {
			return maxvarid + 1;
		}
		int var = v >> 1;
		if ((v & 1) == 0) {
			return var;
		}
		return -var;
	}

	private int readOutput(int nboutputs, EfficientScanner scanner)
			throws IOException, ParseFormatException {
		IVecInt outputs = new VecInt(nboutputs);
		for (int i = 0; i < nboutputs; i++) {
			outputs.push(scanner.nextInt());
		}
		return outputs.get(0);
	}

	private IVecInt readInput(int numberOfInputs, EfficientScanner scanner)
			throws IOException, ParseFormatException {
		IVecInt inputs = new VecInt(numberOfInputs);
		for (int i = 0; i < numberOfInputs; i++) {
			inputs.push(scanner.nextInt());
		}
		return inputs;
	}

	@Override
	public IProblem parseInstance(java.io.Reader in)
			throws ParseFormatException, ContradictionException, IOException {
		throw new UnsupportedOperationException();
	}

}
