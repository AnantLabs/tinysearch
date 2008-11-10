// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2007-12-14 17:41:46
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TokenDelegate.java

package org.mira.lucene.analysis;

import org.apache.lucene.analysis.Token;

public class TokenDelegate implements Comparable {

	TokenDelegate(int offset, int begin, int end) {
		this.offset = offset;
		this.begin = begin;
		this.end = end;
	}

	public void setTerm(String term) {
		token = new Token(term, offset + begin, offset + end + 1);
	}

	public boolean equals(Object o) {
		if (o instanceof TokenDelegate) {
			TokenDelegate ntd = (TokenDelegate) o;
			if (begin == ntd.getBegin() && end == ntd.getEnd())
				return true;
		}
		return false;
	}

	public int hashCode() {
		return begin * 17 + end * 23;
	}

	public int compareTo(Object o) {
		TokenDelegate ntd = (TokenDelegate) o;
		if (begin < ntd.begin)
			return -1;
		if (begin == ntd.begin) {
			if (end > ntd.end)
				return -1;
			if (end == ntd.end)
				return 0;
			if (end < ntd.end)
				return 1;
		}
		return 1;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public Token getToken() {
		return token;
	}

	public int getOffset() {
		return offset;
	}

	private int offset;
	private int begin;
	private int end;
	private Token token;
}