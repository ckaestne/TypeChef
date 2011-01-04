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
package org.sat4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.InstanceReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.RemiUtils;
import org.sat4j.tools.SolutionCounter;

/**
 * This is an example of use of the SAT4J library for computing the backbone of
 * a CNF or to compute the number of solutions of a CNF. We do not claim that
 * those tools are very efficient: they were simple to write and they helped us
 * on small examples.
 * 
 * @author leberre
 */
public final class MoreThanSAT {

	/**
	 * This constructor is private to prevent people to use instances of that
	 * class.
	 * 
	 */
	private MoreThanSAT() {
		// to silent PMD audit
	}

	public static void main(final String[] args) {
		final ISolver solver = SolverFactory.newDefault();
		final SolutionCounter sc = new SolutionCounter(solver);
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new InstanceReader(solver);

		// filename is given on the command line
		try {
			final IProblem problem = reader.parseInstance(args[0]);
			if (problem.isSatisfiable()) {
				System.out.println(Messages.getString("MoreThanSAT.0")); //$NON-NLS-1$
				reader.decode(problem.model(), new PrintWriter(System.out));
				IVecInt backbone = RemiUtils.backbone(solver);
				System.out
						.println(Messages.getString("MoreThanSAT.1") + backbone); //$NON-NLS-1$
				System.out.println(Messages.getString("MoreThanSAT.2")); //$NON-NLS-1$
				System.out.println(Messages.getString("MoreThanSAT.3") //$NON-NLS-1$
						+ sc.countSolutions());
			} else {
				System.out.println(Messages.getString("MoreThanSAT.4")); //$NON-NLS-1$
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ContradictionException e) {
			System.out.println(Messages.getString("MoreThanSAT.5")); //$NON-NLS-1$
		} catch (TimeoutException e) {
			System.out.println(Messages.getString("MoreThanSAT.6")); //$NON-NLS-1$
		}
	}
}
