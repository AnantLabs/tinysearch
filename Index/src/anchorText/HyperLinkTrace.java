package anchorText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Properties;

import org.htmlparser.Parser;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import Single.HtmlFilter;
import Single.Utils;

public class HyperLinkTrace {
	private static BufferedWriter totalAnchorText;
	private static BufferedWriter outAnchorText;
	private static BufferedWriter inAnchorText;
	protected static Hashtable<String, String> hash = new Hashtable<String, String>();
	private static String source;

	public void init() throws Exception {
		FileInputStream ios = new FileInputStream(System
				.getProperty("user.dir")
				+ "/path.property");
		Properties prop = new Properties();
		prop.load(ios);
		ios.close();

		totalAnchorText = new BufferedWriter(new FileWriter(new File(prop
				.getProperty("totalAT"))));
		outAnchorText = new BufferedWriter(new FileWriter(new File(prop
				.getProperty("outAT"))));
		hashinput(prop.getProperty("totallink"));
		source = prop.getProperty("save");

		// inAnchorText = new BufferedWriter(new FileWriter(new File("")));
		// newlink = new BufferedWriter(new FileWriter(new
		// File("E:/larbin/linkmap.txt")));
	}

	public static void main(String[] args) throws Exception {
		HyperLinkTrace hlt = new HyperLinkTrace();
		hlt.init();
		File save = new File(source);
		int dirnum = 0;
		String[] subdirs = null;
		if (save.isDirectory()) {
			subdirs = save.list();
			Arrays.sort(subdirs);
			dirnum = subdirs.length;
		}
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < dirnum; i++) {
			getAnchorText(source + "/" + subdirs[i].toString());
			// System.out.println(subdirs[i].toString() + "完成！！！！！！！！！！");
		}
		totalAnchorText.close();
		outAnchorText.close();
		hash.clear();
		hash = null;
		long endTime = System.currentTimeMillis();
		System.out.println("耗时" + (endTime - startTime) + "ms");

	}

	private static void getAnchorText(String dir) throws Exception {

		String[] subdirsarr;
		File subdir = new File(dir);
		if (subdir.isDirectory()) {
			subdirsarr = subdir.list(new HtmlFilter());
			Arrays.sort(subdirsarr);
			String parsent = dir.substring(dir.lastIndexOf("/") + 1);
			for (int i = 0, j = 0; i < subdirsarr.length; i++) {
				for (j = 0; j < subdirsarr[i].length(); j++)
					if (subdirsarr[i].charAt(j) != '0'
							&& subdirsarr[i].charAt(j) != 'f')
						break;
					else
						continue;
				if (j >= subdirsarr[i].length())
					j = subdirsarr[i].length() - 1;
				String htmlnum = subdirsarr[i].substring(j);
				addAnchorText(new File(subdir, subdirsarr[i]), parsent + "-"
						+ htmlnum);
			}
			subdirsarr = null;
			subdir = null;
			parsent = null;
		}
	}

	public static void hashinput(String dir) throws Exception {
		String[] linesarr;
		File linkfile = new File(dir);
		BufferedReader linkinput = new BufferedReader(new FileReader(linkfile));
		String line = linkinput.readLine();
		while (line != null) {
			linesarr = line.split("\t");
			hash.put(linesarr[0], linesarr[1]);
			// hash.put(linesarr[1], linesarr[0]);
			line = linkinput.readLine();
		}
		linkinput.close();
	}

	private static void addAnchorText(File html, String htmlnum) throws Exception{
		int j = 0;
		URL sjtu, url;
		StringBuffer outAT = new StringBuffer();
		Utils tool = new Utils();
		Charset charset = tool.autoDetectCharset(html.toURL());
		System.out.println(htmlnum);
		Parser parser = new Parser();

		if (charset.name().equals("Big5")) {
			parser.setEncoding("GB2312");
			parser.setInputHTML(tool.readTextFile(html, "GB2312"));
		} else {
			parser.setEncoding(charset.name());
			parser.setInputHTML(tool.readTextFile(html, charset.name()));
		}
		// System.out.println(parser.getEncoding());
		NodeList nlist = parser.extractAllNodesThatMatch(new NodeFilter() {
			public boolean accept(Node node) {
				if (node instanceof LinkTag)
					return true;
				return false;
			}
		});
		outAT.append(htmlnum).append(' ');
		for (int i = 0; i < nlist.size(); i++) {
			CompositeTag node = (CompositeTag) nlist.elementAt(i);
			if (node instanceof LinkTag) {
				LinkTag link = (LinkTag) node;
				String linkText = link.getLinkText();
				linkText = linkText.replaceAll("&lt;", "<")
				.replaceAll("&gt;", ">")
				.replaceAll("&amp;", "&")
				.replaceAll("&nbsp;","")
				.replaceAll("\\s{1,}", "");
				String linkUrl = link.getLink().toLowerCase();
				if ((linkText.length() <= 50) && (linkText.length() > 0)) {
					if (linkUrl.contains("#"))
						linkUrl = linkUrl.substring(0, linkUrl.indexOf('#'));
					// 如果link不是.sjtu.edu.cn的url，则过滤
					if (linkUrl.startsWith("http://")) {
						if (!linkUrl.contains(".sjtu.edu.cn")) {
							continue;
						}
					} else {
						if (urlfilter(linkUrl)) {
							continue;
						}
					}
					try {
						String htmlurl = hash.get(htmlnum);
						sjtu = new URL(htmlurl.substring(0, htmlurl
								.lastIndexOf('/')));
						htmlurl = null;
						url = new URL(sjtu, linkUrl);
					} catch (MalformedURLException e) {
						continue;
					}
					// 保存anchor text至totalAnchorText文件
					StringBuffer sb = new StringBuffer();
					sb.append(url.toString()).append(' ').append(linkText);
					totalAnchorText.write(sb.toString());
					totalAnchorText.newLine();
					sb = null;
					System.out.println((j++) + " Link: " + url.toString());
					System.out.println("   Anchor Text: " + linkText);

					// 保存至outAnchorText文件
					outAT.append(linkText).append(' ');

					linkText = null;
					linkUrl = null;
					sjtu = null;
					url = null;
				}
			}
		}
		// 保存至outAnchorText文件
		outAnchorText.write(outAT.toString());
		outAnchorText.newLine();
		outAnchorText.flush();
		outAT = null;
		totalAnchorText.flush();
		nlist = null;
		parser = null;
	}

	/**
	 * URL过滤 .tar .gz .tgz .zip .Z .rpm .deb .rar .ps .dvi .pdf .png .jpg .jpeg
	 * .bmp .smi .tiff .gif .mov .avi .mpeg .mpg .mp3 .qt .wav .ram .rm .rmvb
	 * .jar .java .class .diff .c .cpp .h .doc .xls .ppt .mdb .rtf .exe .pps .so
	 * .psd .css .js
	 */
	private static boolean urlfilter(String linkUrl) {
		if (linkUrl.contains("mailto:") || linkUrl.contains("@")) {
			return true;
		}
		if (linkUrl.contains("javascript:") || linkUrl.contains("ftp:")) {
			return true;
		}
		if (linkUrl.contains("mms:") || linkUrl.contains("telnet:")) {
			return true;
		}
		// .tar .gz .tgz .zip .Z .rpm .deb .rar
		if (linkUrl.endsWith(".tar") || linkUrl.endsWith(".gz")) {
			return true;
		}
		if (linkUrl.endsWith(".tgz") || linkUrl.endsWith(".zip")) {
			return true;
		}
		if (linkUrl.endsWith(".z") || linkUrl.endsWith(".rpm")) {
			return true;
		}
		if (linkUrl.endsWith(".deb") || linkUrl.endsWith(".rar")) {
			return true;
		}
		// .ps .dvi .pdf
		if (linkUrl.endsWith(".ps") || linkUrl.endsWith(".dvi")) {
			return true;
		}
		if (linkUrl.endsWith(".chm") || linkUrl.endsWith(".pdf")) {
			return true;
		}
		// .png .jpg .jpeg .bmp .smi .tiff .gif
		if (linkUrl.endsWith(".png") || linkUrl.endsWith(".jpg")) {
			return true;
		}
		if (linkUrl.endsWith(".jpeg") || linkUrl.endsWith(".bmp")) {
			return true;
		}
		if (linkUrl.endsWith(".smi") || linkUrl.endsWith(".tiff")) {
			return true;
		}
		if (linkUrl.endsWith(".gif")) {
			return true;
		}
		// .mov .avi .mpeg .mpg .mp3 .qt .wav .ram .rm .rmvb
		if (linkUrl.endsWith(".mov") || linkUrl.endsWith(".avi")) {
			return true;
		}
		if (linkUrl.endsWith(".mpeg") || linkUrl.endsWith(".mpg")) {
			return true;
		}
		if (linkUrl.endsWith(".mp3") || linkUrl.endsWith(".gt")) {
			return true;
		}
		if (linkUrl.endsWith(".wav") || linkUrl.endsWith(".ram")) {
			return true;
		}
		if (linkUrl.endsWith(".rm") || linkUrl.endsWith(".rmvb")) {
			return true;
		}
		// .jar .java .class .diff .c .cpp .h
		if (linkUrl.endsWith(".jar") || linkUrl.endsWith(".java")) {
			return true;
		}
		if (linkUrl.endsWith(".class") || linkUrl.endsWith(".diff")) {
			return true;
		}
		if (linkUrl.endsWith(".c") || linkUrl.endsWith(".cpp")) {
			return true;
		}
		if (linkUrl.endsWith(".h") || linkUrl.endsWith(".xml")) {
			return true;
		}
		// .doc .xls .ppt .mdb .rtf .exe .pps .so .psd
		if (linkUrl.endsWith(".doc") || linkUrl.endsWith(".xls")) {
			return true;
		}
		if (linkUrl.endsWith(".ppt") || linkUrl.endsWith(".mdb")) {
			return true;
		}
		if (linkUrl.endsWith(".rtf") || linkUrl.endsWith(".exe")) {
			return true;
		}
		if (linkUrl.endsWith(".pps") || linkUrl.endsWith(".so")) {
			return true;
		}
		if (linkUrl.endsWith(".psd") || linkUrl.endsWith(".xsl")) {
			return true;
		}
		//		.css .js
		if (linkUrl.endsWith(".css") || linkUrl.endsWith(".js")) {
			return true;
		}

		return false;
	}
}
