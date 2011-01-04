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

import java.io.BufferedInputStream;
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
 * Dimacs Reader written by Frederic Laihem. It is much faster than DimacsReader
 * because it does not split the input file into strings but reads and interpret
 * the input char by char. Hence, it is a bit more difficult to read and to
 * modify than DimacsReader.
 * 
 * This reader is used during the SAT Competitions.
 * 
 * @author laihem
 * @author leberre
 */
public class LecteurDimacs extends Reader implements Serializable {

	private static final long serialVersionUID = 1L;

	/* taille du buffer */
	private final static int TAILLE_BUF = 16384;

	private final ISolver s;

	private transient BufferedInputStream in;

	/* nombre de literaux dans le fichier */
	private int nbVars = -1;

	private int nbClauses = -1;

	private static final char EOF = (char) -1;

	/*
	 * nomFichier repr?sente le nom du fichier ? lire
	 */
	public LecteurDimacs(ISolver s) {
		this.s = s;
	}

	@Override
	public final IProblem parseInstance(final InputStream input)
			throws ParseFormatException, ContradictionException, IOException {

		this.in = new BufferedInputStream(input, LecteurDimacs.TAILLE_BUF);
		s.reset();
		passerCommentaire();
		if (nbVars < 0)
			throw new ParseFormatException(
					"DIMACS error: wrong max number of variables");
		s.newVar(nbVars);
		s.setExpectedNumberOfClauses(nbClauses);
		char car = passerEspaces();
		if (nbClauses > 0) {
			if (car == EOF)
				throw new ParseFormatException(
						"DIMACS error: the clauses are missing");
			ajouterClauses(car);
		}
		input.close();
		return s;
	}

	@Override
	public IProblem parseInstance(java.io.Reader input) throws IOException,
			ContradictionException {
		throw new UnsupportedOperationException();
	}

	/** on passe les commentaires et on lit le nombre de literaux */
	private char passerCommentaire() throws IOException {
		char car;
		for (;;) {
			car = passerEspaces();
			if (car == 'p') {
				car = lectureNombreLiteraux();
			}
			if (car != 'c' && car != 'p')
				break; /* fin des commentaires */
			car = nextLine(); /* on passe la ligne de commentaire */
			if (car == EOF)
				break;
		}
		return car;
	}

	/** lit le nombre repr?sentant le nombre de literaux */
	private char lectureNombreLiteraux() throws IOException {
		char car = nextChiffre(); /* on lit le prchain chiffre */
		if (car != EOF) {
			nbVars = car - '0';
			for (;;) { /* on lit le chiffre repr?sentant le nombre de literaux */
				car = (char) in.read();
				if (car < '0' || car > '9')
					break;
				nbVars = 10 * nbVars + (car - '0');
			}
			car = nextChiffre();
			nbClauses = car - '0';
			for (;;) { /* on lit le chiffre repr?sentant le nombre de literaux */
				car = (char) in.read();
				if (car < '0' || car > '9')
					break;
				nbClauses = 10 * nbClauses + (car - '0');
			}
			if (car != '\n' && car != EOF)
				nextLine(); /* on lit la fin de la ligne */
		}
		return car;
	}

	/**
	 * lit les clauses et les ajoute dans le vecteur donn? en param?tre
	 * 
	 * @throws ParseFormatException
	 */
	private void ajouterClauses(char car) throws IOException,
			ContradictionException, ParseFormatException {
		final IVecInt lit = new VecInt();
		int val = 0;
		boolean neg = false;
		for (;;) {
			/* on lit le signe du literal */
			if (car == '-') {
				neg = true;
				car = (char) in.read();
			} else if (car == '+')
				car = (char) in.read();
			else /* on le 1er chiffre du literal */
			if (car >= '0' && car <= '9') {
				val = car - '0';
				car = (char) in.read();
			} else
				throw new ParseFormatException("Unknown character " + car);
			/* on lit la suite du literal */
			while (car >= '0' && car <= '9') {
				val = (val * 10) + car - '0';
				car = (char) in.read();
			}
			if (val == 0) { // on a lu toute la clause
				s.addClause(lit);
				lit.clear();
			} else {
				/* on ajoute le literal au vecteur */
				// s.newVar(val-1);
				lit.push(neg ? -val : val);
				neg = false;
				val = 0; /* on reinitialise les variables */
			}
			if (car != EOF)
				car = passerEspaces();
			if (car == EOF) {
				if (!lit.isEmpty()) {
					s.addClause(lit);
				}
				break; /* on a lu tout le fichier */
			}
		}
	}

	/** passe tout les caract?res d'espacement (espace ou \n) */
	private char passerEspaces() throws IOException {
		char car;

		do {
			car = (char) in.read();
		} while (car == ' ' || car == '\n');

		return car;
	}

	/** passe tout les caract?res jusqu? rencontrer une fin de la ligne */
	private char nextLine() throws IOException {
		char car;
		do {
			car = (char) in.read();
		} while ((car != '\n') && (car != EOF));
		return car;
	}

	/** passe tout les caract?re jusqu'? rencontrer un chiffre */
	private char nextChiffre() throws IOException {
		char car;
		do {
			car = (char) in.read();
		} while ((car < '0') || (car > '9') && (car != EOF));
		return car;
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
}
