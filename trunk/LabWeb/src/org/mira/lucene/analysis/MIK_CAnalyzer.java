// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2007-12-14 17:41:40
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   MIK_CAnalyzer.java

package org.mira.lucene.analysis;

import java.io.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

// Referenced classes of package org.mira.lucene.analysis:
//            IKTokenizer

public class MIK_CAnalyzer extends Analyzer {

	public MIK_CAnalyzer() {
		mircoSupported = false;
	}

	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new IKTokenizer(reader, mircoSupported);
	}

	public static void main(String args[]) {
		String testString = "\u636E\u8DEF\u900F\u793E\u62A5\u9053\uFF0C\u5370\u5EA6\u5C3C\u897F\u4E9A\u793E\u4F1A\u4E8B\u52A1\u90E8\u4E00\u5B98\u5458\u661F\u671F\u4E8C(29\u65E5)\u8868\u793A\uFF0C\u65E5\u60F9\u5E02\u9644\u8FD1\u5F53\u5730\u65F6\u95F427\u65E5\u66685\u65F653\u5206\u53D1\u751F\u7684\u91CC\u6C0F6.2\u7EA7\u5730\u9707\u5DF2\u7ECF\u9020\u6210\u81F3\u5C115427\u4EBA\u6B7B\u4EA1\uFF0C20000\u4F59\u4EBA\u53D7\u4F24\uFF0C\u8FD120\u4E07\u4EBA\u65E0\u5BB6\u53EF\u5F52\u3002";
		testString += "决策科学，管理科学与决策科学，管理科学，以及管理科学与工程";
		MIK_CAnalyzer ika = new MIK_CAnalyzer();
		try {
			Reader r = new StringReader(testString);
			TokenStream ts = ika.tokenStream(null, r);
			long begin = System.currentTimeMillis();
			for (Token t = ts.next(); t != null; t = ts.next())
				System.out.println(t.startOffset() + " - " + t.endOffset()
						+ " = " + t.termText());
			long end = System.currentTimeMillis();
			System.out.println("\u8017\u65F6 : " + (end - begin) + "ms");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean mircoSupported;
}