// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2007-12-14 17:42:52
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   WordType.java

package org.mira.lucene.analysis.dict;

public class WordType {

	public WordType() {
		wordTypeValue = 0;
	}

	private WordType(int wordTypeValue) {
		this.wordTypeValue = 0;
		this.wordTypeValue = wordTypeValue;
	}

	public void addWordType(WordType wordType) {
		wordTypeValue = wordTypeValue | wordType.wordTypeValue;
	}

	public boolean isNormWord() {
		return (wordTypeValue & 1) > 0;
	}

	public boolean isSuffix() {
		return (wordTypeValue & 0x10) > 0;
	}

	public boolean isCount() {
		return (wordTypeValue & 0x100) > 0;
	}

	private static final int NORMWORD = 1;
	private static final int SUFFIX = 16;
	private static final int COUNT = 256;
	private int wordTypeValue;
	public static final WordType WT_NORMWORD = new WordType(1);
	public static final WordType WT_SUFFIX = new WordType(16);
	public static final WordType WT_COUNT = new WordType(256);

}