package org.anarres.cpp;

import java.io.IOException;

public class UnnumberedUnexpandingStringLexerSource extends StringLexerSource {
	public UnnumberedUnexpandingStringLexerSource(String string) throws IOException {
		super(string,true);
	}

	@Override
	boolean isNumbered() {
		return false;
	}
	
	@Override
	boolean isExpanding(String macroName) {
		return true;
	}
}
