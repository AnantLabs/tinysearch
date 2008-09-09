package org.cc.hello;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * 用于处理HTML信息的工具集合
 * @author liudong
 */
public class HtmlUtils {

	/**
	 * 抽取纯文本信息
	 * @param inputHtml
	 * @return
	 */
	public static String extractText(String inputHtml) throws Exception {
		StringBuffer text = new StringBuffer();

		Parser parser = Parser.createParser(inputHtml, "GB2312");
		//遍历所有的节点
		NodeList nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
			public boolean accept(Node node) {
				return true;
			}
		});
		Node node = nodes.elementAt(0);
		text.append(new String(node.toPlainTextString().getBytes("GB2312")));
		return text.toString();
	}

	/**
	 * 读取一个文件到字符串里.
	 * 
	 * @param sFileName  文件名
	 * @param sEncode   String
	 * @return 文件内容
	 */
	public static String readTextFile(String sFileName, String sEncode) {
		StringBuffer sbStr = new StringBuffer();
		try {
			File ff = new File(sFileName);
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					ff), sEncode);
			BufferedReader ins = new BufferedReader(read);
			String dataLine = "";
			while (null != (dataLine = ins.readLine())) {
				sbStr.append(dataLine);
				sbStr.append("\r\n");
			}

			ins.close();
		} catch (Exception e) {
			System.out.println("read Text File Error");
		}

		return sbStr.toString();
	}

	public static void main(String[] args) throws Exception {
		String aFile = "F:/DataDir/1/6.html";
		String content = readTextFile(aFile, "GB2312");

		//System.out.println(content);
		String text = extractText("<FONT CLASS=\"FrameItemFont\"><A HREF=\"org/htmlparser/lexer/package-frame.html\" target=\"packageFrame\">org.htmlparser.lexer</A></FONT><BR><FONT CLASS=\"FrameItemFont\"><A HREF=\"org/htmlparser/lexerapplications/tabby/package-frame.html\" target=\"packageFrame\">org.htmlparser.lexerapplications.tabby</A></FONT><BR><FONT CLASS=\"FrameItemFont\"><A HREF=\"org/htmlparser/lexerapplications/thumbelina/package-frame.html\" target=\"packageFrame\">org.htmlparser.lexerapplications.thumbelina</A></FONT><BR><FONT CLASS=\"FrameItemFont\"><A HREF=\"org/htmlparser/nodes/package-frame.html\" target=\"packageFrame\">org.htmlparser.nodes</A></FONT>");
		System.out.println(text);

		System.out.println("over");
	}

}
