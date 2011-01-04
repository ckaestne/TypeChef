package org.sat4j.reader;

import java.io.IOException;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.tools.xplain.HighLevelXplain;

public class StructuredCNFReader extends DimacsReader {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int numberOfComponents;

	private final HighLevelXplain<ISolver> hlxplain;

	private int currentComponentIndex;

	public StructuredCNFReader(HighLevelXplain<ISolver> solver) {
		super(solver, "scnf");
		hlxplain = solver;
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
	@Override
	protected void readProblemLine() throws IOException, ParseFormatException {

		String line = scanner.nextLine().trim();

		if (line == null) {
			throw new ParseFormatException(
					"premature end of file: <p scnf ...> expected");
		}
		String[] tokens = line.split("\\s+");
		if (tokens.length < 5 || !"p".equals(tokens[0])
				|| !formatString.equals(tokens[1])) {
			throw new ParseFormatException("problem line expected (p scnf ...)");
		}

		int vars;

		// reads the max var id
		vars = Integer.parseInt(tokens[2]);
		assert vars > 0;
		solver.newVar(vars);
		// reads the number of clauses
		expectedNbOfConstr = Integer.parseInt(tokens[3]);
		assert expectedNbOfConstr > 0;
		numberOfComponents = Integer.parseInt(tokens[4]);
		solver.setExpectedNumberOfClauses(expectedNbOfConstr);
	}

	/**
	 * @since 2.1
	 */
	@Override
	protected boolean handleLine() throws ContradictionException, IOException,
			ParseFormatException {
		int lit;
		boolean added = false;
		String component = scanner.next();
		if (!component.startsWith("{") || !component.endsWith("}")) {
			throw new ParseFormatException(
					"Component index required at the beginning of the clause");
		}
		currentComponentIndex = Integer.valueOf(component.substring(1,
				component.length() - 1));
		if (currentComponentIndex < 0
				|| currentComponentIndex > numberOfComponents) {
			throw new ParseFormatException("wrong component index: "
					+ currentComponentIndex);
		}
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

	/**
	 * 
	 * @throws ContradictionException
	 * @since 2.1
	 */
	@Override
	protected void flushConstraint() throws ContradictionException {
		try {
			if (currentComponentIndex == 0) {
				hlxplain.addClause(literals);
			} else {
				hlxplain.addClause(literals, currentComponentIndex);
			}
		} catch (IllegalArgumentException ex) {
			if (isVerbose()) {
				System.err.println("c Skipping constraint " + literals);
			}
		}
	}
}
