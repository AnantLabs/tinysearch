// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2007-12-14 17:41:43
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   MTokenDelegate.java

package org.mira.lucene.analysis;

// Referenced classes of package org.mira.lucene.analysis:
//            TokenDelegate

public class MTokenDelegate extends TokenDelegate {

	MTokenDelegate(int offset, int begin, int end) {
		super(offset, begin, end);
	}

	public int compareTo(Object o) {
		MTokenDelegate mtd = (MTokenDelegate) o;
		if (getBegin() < mtd.getBegin())
			return -1;
		if (getBegin() == mtd.getBegin())
			return 0;
		return getEnd() > mtd.getEnd() ? 1 : 0;
	}
}