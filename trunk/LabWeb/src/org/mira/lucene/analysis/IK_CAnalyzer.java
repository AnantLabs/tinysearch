// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2007-12-14 17:41:31
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IK_CAnalyzer.java

package org.mira.lucene.analysis;

import java.io.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;


// Referenced classes of package org.mira.lucene.analysis:
//            IKTokenizer

public final class IK_CAnalyzer extends Analyzer {

	public IK_CAnalyzer() {
		mircoSupported = true;
	}

	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new IKTokenizer(reader, mircoSupported);
	}

	public static void main(String args[]) {
		String testString = "\u5929\u798F\u5927\u9152\u5E97 \u798F\u5EFA\u7701\u90AE\u653F\u50A8\u84C4\u5C40\u534E\u6797\u50A8\u84C4\u6240 \u798F\u5DDE\u5E02\u653F\u5E9C \u8BAF\u901A\u5929\u4E0B\u4F20\u5A92 \u4E2D\u534E\u4EBA\u6C11\u5171\u548C\u56FD\u9999\u6E2F\u7279\u522B\u884C\u653F\u533A \u4E2D\u6587\u5206\u8BCD\u6027\u80FD\u4E0D\u9519  \u6BCF\u65E5\u751F\u9C9C\u6709\u9650\u516C\u53F8 \u52C7\u6562\u7684\u58EB\u5175 \u7EBA\u7EC7\u4EA7\u54C1\u548C\u670D\u88C5\u9970\u54C1 \u822A\u73ED\u8F66\u884C\u674E\u7968 \u7535\u5F71\u9662";
		IK_CAnalyzer ika = new IK_CAnalyzer();
		try {
			System.out.println("Length = " + testString.length());
			Reader r = new StringReader(testString);
			TokenStream ts = ika.tokenStream("TestField", r);
			long begin = System.currentTimeMillis();
			for (org.apache.lucene.analysis.Token t = ts.next(); t != null; t = ts
					.next()) {
				String tmp = t.termText();
				if (!"".equals(tmp))
					System.out.println(tmp);
			}
			long end = System.currentTimeMillis();
			System.out.println("\u8017\u65F6 : " + (end - begin) + "ms");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean mircoSupported;
}