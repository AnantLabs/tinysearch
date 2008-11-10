// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2007-12-14 17:42:47
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Hit.java

package org.mira.lucene.analysis.dict;

// Referenced classes of package org.mira.lucene.analysis.dict:
//            WordType

public class Hit {

	public Hit() {
		hitState = 0;
	}

	public boolean isMatch() {
		return (hitState & 1) > 0;
	}

	public void setMatch() {
		hitState = hitState | 1;
	}

	public boolean isMatchAndContinue() {
		return (hitState & 1) > 0 && (hitState & 0x10) > 0;
	}

	public boolean isPrefixMatch() {
		return (hitState & 0x10) > 0;
	}

	public void setPrefixMatch() {
		hitState = hitState | 0x10;
	}

	public boolean isUnmatch() {
		return (hitState & 0x100) > 0;
	}

	public void setUnmatch() {
		hitState = 256;
	}

	public WordType getWordType() {
		return wordType;
	}

	public void setWordType(WordType wordType) {
		this.wordType = wordType;
	}

	public int getHitState() {
		return hitState;
	}

	private static final int MATCH = 1;
	private static final int PREFIX_MATCH = 16;
	private static final int UNMATCH = 256;
	private int hitState;
	private WordType wordType;
}