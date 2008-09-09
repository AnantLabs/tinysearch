package Multi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;

public class Utils {

	public static String autoDetectCharset(URL url) {
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
//		detector.add(new ParsingDetector(false));
		detector.add(JChardetFacade.getInstance());
		detector.add(ASCIIDetector.getInstance());
		
		Charset charset = null;
		try {
			charset = detector.detectCodepage(url);
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (IllegalCharsetNameException icne){
			charset = null;
			icne.printStackTrace();
		}
		if (charset == null)
//			charset = Charset.defaultCharset();
			return "GB2312";
		return charset.name();
	}

	public static String readTextFile(File sFileName, String charset) {
		StringBuffer sbStr = new StringBuffer();
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
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
			/*
			InputStreamReader read;
			try {
				read = new InputStreamReader(new FileInputStream(sFileName), "utf-8");
				BufferedReader ins = new BufferedReader(read);
				String dataLine = "";
				while (null != (dataLine = ins.readLine())) {
					sbStr.append(dataLine);
					sbStr.append("\r\n");
				}
				ins.close();
				read.close();
			} catch (Exception e1) {
				System.out.println("read Text File Error");
				e1.printStackTrace();
			}
			*/
		}
		return sbStr.toString();
	}

	public static String[] parseHtml(File sFileName, String charset) {
		String result[] = null;
		String content = null;
//		StringBuffer text = new StringBuffer();
		
		content = readTextFile(sFileName, charset);

		if (content != null) {
			Parser myParser = Parser.createParser(content, charset);
//			if (myParser == null) myParser = Parser.createParser(content, "utf-8");
			try {
				String body = null;
				String title = "Untitled";
				// 遍历body 和 title节点
				NodeList nodes = myParser
						.extractAllNodesThatMatch(new NodeFilter() {
							public boolean accept(Node node) {
								if (node instanceof BodyTag)
									return true;
								if (node instanceof TitleTag)
									return true;
								return false;
							}
						});
				for (int i = 0; i < nodes.size(); i++) {
					CompositeTag ctag = (CompositeTag) nodes.elementAt(i);
					if (ctag instanceof BodyTag) {
						BodyTag bodytag = (BodyTag) ctag;
						body = filter(bodytag.getBody());
					}
					if (ctag instanceof TitleTag) {
						TitleTag titletag = (TitleTag) ctag;
						title = titletag.getTitle();
						title = title.replaceAll("\\s{1,}", "");
					}
				}
				result = new String[] { body, title };
			} catch (ParserException pe) {
				pe.printStackTrace();
			}
		}
		return result;
	}

	private static String filter(String string) {
		//删除“<”，“>”之间（包括“<”和“>”）的字符
		string = string.replaceAll("<[^>]*>", " ");
		
		//删除&nbsp;类似的字符
		string = string.replaceAll("&[^;]*;", " ");
		
		//删除空格、回车符、换行符、制表符等非字母，数字字符
		string = string.replaceAll("\\s{1,}", " ");
		
		//删除每行开头的空格。必须是在 MULTILINE 模式下:
//		string = string.replaceAll("(?m)^ +", "");
		return string;
	}

}
