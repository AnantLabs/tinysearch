package Single;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;

public class Utils {
	private final int metaMAX = 100;

	@SuppressWarnings("finally")
	public Charset autoDetectCharset(URL url) {
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		detector.add(JChardetFacade.getInstance()); // Another singleton
		detector.add(ASCIIDetector.getInstance()); // a good fallback
		detector.add(new ParsingDetector(false)); // be verbose about parsing

		Charset charset = null;
		try {
			charset = detector.detectCodepage(url);
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (IllegalCharsetNameException icne) {
			icne.printStackTrace();
		} finally {
			return charset;
		}
	}

	public String[] parseHtml(File sFileName) throws IOException {
		String result[] = null;
		String content = null, charsetname = null;
		Charset charset1 = null;
		charset1 = autoDetectCharset(sFileName.toURL());
		charsetname = (charset1 == null) ? "GB2312" : charset1.name();
		charsetname = charsetname.toLowerCase().equals("big5") ? "GB2312" : charsetname;
		
		content = readTextFile(sFileName, charsetname);

		if (content != null) {
			Parser myParser = Parser.createParser(content, charsetname);
			try {
				String body = null;
				String title = null;
				StringBuffer metaBuffer = new StringBuffer(metaMAX);
				String meta = null;
				// 遍历body 和 title节点
				NodeList nodes = myParser
						.extractAllNodesThatMatch(new NodeFilter() {
							public boolean accept(Node node) {
								if (node instanceof MetaTag)
									return true;
								if (node instanceof BodyTag)
									return true;
								if (node instanceof TitleTag)
									return true;
								return false;
							}
						});

				for (int i = 0; i < nodes.size(); i++) {
					if (nodes.elementAt(i) instanceof MetaTag) {
						MetaTag metaTag = (MetaTag) nodes.elementAt(i);
						if (metaTag.getMetaTagName() != null) {
							meta = (metaTag.getMetaContent() == null) ? " "
									: metaTag.getMetaContent();
							metaBuffer.append(
									meta.replaceAll("\\s{1,}", " ").replaceAll(
											",", " ").replaceAll("，", " ")
											.replaceAll("、", " ")).append(' ');
							meta = metaBuffer.toString();
						}
						continue;
					}

					CompositeTag ctag = (CompositeTag) nodes.elementAt(i);

					if (ctag instanceof BodyTag) {
						BodyTag bodytag = (BodyTag) ctag;
						body = (bodytag.getBody() == null) ? " "
								: filter(bodytag.getBody());
						continue;
					}

					if (ctag instanceof TitleTag) {
						TitleTag titletag = (TitleTag) ctag;
						title = titletag.getTitle();
						// title = (title == null) ? " " :
						// title.replaceAll("\\s{1,}", "");
						continue;
					}
				}

				if (title == null) {
					if (meta != null) {
						String[] metaarr = meta.split(" ");
						int maxlen = 0, pos = 0;
						for (int i = 0; i < metaarr.length; i++) {
							if (metaarr[i].length() > maxlen) {
								maxlen = metaarr[i].length();
								pos = i;
							}
						}
						title = metaarr[pos];
					}
				}

				result = new String[] { body, title, meta };
			} catch (ParserException pe) {
				pe.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 以charset编码方式读取文件sFileName内容
	 * 
	 * @param sFileName
	 * @param charset
	 * @return String
	 */
	@SuppressWarnings("finally")
	public String readTextFile(File sFileName, String charset) {
		StringBuffer sbStr = new StringBuffer();
		Reader read = null;
		BufferedReader ins;
		String dataLine = " ";
		try {
			read = new InputStreamReader(new FileInputStream(sFileName),
					charset);
			ins = new BufferedReader(read);
			while (null != (dataLine = ins.readLine())) {
				sbStr.append(dataLine);
				sbStr.append("\r\n");
			}
			ins.close();
			read.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return sbStr.toString();
		}
	}

	/**
	 * html2text过滤
	 * 
	 * @param string
	 * @return String
	 */
	private String filter(String string) {
		// 删除“<”，“>”之间（包括“<”和“>”）的字符
		// string = string.replaceAll("<[^>]*>", " ");

		// 删除&nbsp;类似的字符
		// string = string.replaceAll("&[^;]*;", " ");

		// 删除空格、回车符、换行符、制表符等非字母，数字字符
		// string = string.replaceAll("\\s{1,}", " ");

		// 删除每行开头的空格。必须是在 MULTILINE 模式下:
		// string = string.replaceAll("(?m)^ +", "");
		// return string;
		return string.replaceAll("&nbsp;", " ").replaceAll("&gt;", ">")
				.replaceAll("&lt;", "<").replaceAll("&amp;", "&").replaceAll(
						"&quot", "\"").replaceAll("&apos", "'").replaceAll(
						"<[^>]*>", " ").replaceAll("\\s{1,}", " ");
	}

}
