package org.cc.hello;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class MetaTest {
	
	public static void main(String[] argus) {
		MetaTest metatest = new MetaTest();
		File file = new File("F:/test.htm");
		metatest.parseHtml(file, "GB2312");
	}

	private String readTextFile(File sFileName, String charset) {
		StringBuffer sbStr = new StringBuffer();
		try {
			Reader read = new InputStreamReader(new FileInputStream(
					sFileName), charset);
			BufferedReader ins = new BufferedReader(read);
			String dataLine = "";
			while (null != (dataLine = ins.readLine())) {
				sbStr.append(dataLine);
				sbStr.append("\r\n");
			}
			ins.close();
			read.close();
		} catch (Exception e) {
		}
		return sbStr.toString();
	}

	public void parseHtml(File sFileName, String charset) {
		String result[] = null;
		String content = null;
		
		content = readTextFile(sFileName, charset);

		if (content != null) {
			Parser myParser = Parser.createParser(content, charset);
			try {
				String body = null;
				String title = null;
				// 遍历body 和 title节点
				NodeList nodes = myParser
						.extractAllNodesThatMatch(new NodeFilter() {
							public boolean accept(Node node) {
								if (node instanceof BodyTag)
									return true;
								if (node instanceof TitleTag)
									return true;
								if (node instanceof MetaTag)
									return true;
								return false;
							}
						});
				String str = null;
				for (int i = 0; i < nodes.size(); i++) {
					if (nodes.elementAt(i) instanceof MetaTag){
						MetaTag metaTag = (MetaTag) nodes.elementAt(i);
						if (metaTag.getMetaTagName() != null){
							System.out.println(metaTag.getMetaTagName());

							str = metaTag.getMetaContent().replaceAll("\\s{1,}", " ").replaceAll(
									",", " ").replaceAll("，", " ");
							
							if (str.split(" ").length == 1)
								System.out.println(str);							
						}
					}
//					CompositeTag ctag = (CompositeTag) nodes.elementAt(i);
//					if (ctag instanceof BodyTag) {
//						BodyTag bodytag = (BodyTag) ctag;
//						body = filter(bodytag.getBody());
//					}
//					if (ctag instanceof TitleTag) {
//						TitleTag titletag = (TitleTag) ctag;
//						title = titletag.getTitle();
//						title = title.replaceAll("\\s{1,}", "");
//					}
				}
//				result = new String[] { body, title };
			} catch (ParserException pe) {
				pe.printStackTrace();
			}
		}
//		return result;
	}

	/**
	 * html2text
	 * @param string
	 * @return
	 */
	private String filter(String string) {
		//删除“<”，“>”之间（包括“<”和“>”）的字符
//		string = string.replaceAll("<[^>]*>", " ");
		
		//删除&nbsp;类似的字符
//		string = string.replaceAll("&[^;]*;", " ");
		
		//删除空格、回车符、换行符、制表符等非字母，数字字符
//		string = string.replaceAll("\\s{1,}", " ");
		
		//删除每行开头的空格。必须是在 MULTILINE 模式下:
//		string = string.replaceAll("(?m)^ +", "");
//		return string;
        return string.replaceAll("&nbsp;", " ").replaceAll("&gt;", ">").replaceAll(
                "&lt;", "<").replaceAll("&amp;", "&").replaceAll("&quot", "\"")
                .replaceAll("&apos", "'").replaceAll("&[^;]*;", " ").replaceAll(
                		"<[^>]*>", " ").replaceAll("\\s{1,}", " ");
	}

}
