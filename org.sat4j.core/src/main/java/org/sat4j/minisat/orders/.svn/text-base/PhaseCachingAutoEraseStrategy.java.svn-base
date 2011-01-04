package org.sat4j.minisat.orders;

import static org.sat4j.core.LiteralsUtils.var;

/**
 * @since 2.2
 */
public final class PhaseCachingAutoEraseStrategy extends
		AbstractPhaserecordingSelectionStrategy {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void assignLiteral(int p) {
		phase[var(p)] = p;
	}

	public void updateVar(int p) {
		phase[var(p)] = p;
	}

	@Override
	public String toString() {
		return "Phase caching with auto forget feature";
	}

	public void updateVarAtDecisionLevel(int q) {
	}
}
