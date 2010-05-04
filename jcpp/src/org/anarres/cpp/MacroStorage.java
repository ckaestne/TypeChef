package org.anarres.cpp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureProvider;

public class MacroStorage extends FeatureProvider {

	List<Macro> macros = new ArrayList<Macro>();

	public void put(String name, FeatureExpr feature, Macro macro) {
		macros.add(macro);
	}

	public void remove(String name, FeatureExpr currentFeatureExpr) {

	}

	/**
	 * todo, handle multiple macros
	 * 
	 * @param text
	 * @return
	 */
	public Macro get(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isFeatureDefined(String featureName) {
		return true;
	}

	@Override
	public String toString() {
		String result = "";
		Iterator<Macro> mt = macros.iterator();
		while (mt.hasNext()) {
			Macro macro = mt.next();
			result += ("#") + ("macro ") + (macro) + ("\n");
		}
		return result;
	}

}
