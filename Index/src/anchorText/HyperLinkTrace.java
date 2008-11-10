package anchorText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Hashtable;

import org.htmlparser.Parser;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import Single.HtmlFilter;
import Single.Utils;

public class HyperLinkTrace {
	private BufferedWriter totalAnchorText;
	private Hashtable<String, String> hash = new Hashtable<String, String>(
			180000);

	public void init(String totalAT) throws IOException {
		totalAnchorText = new BufferedWriter(new FileWriter(new File(totalAT)));
	}

	public void close() throws IOException {
		totalAnchorText.close();
		hash.clear();
		hash = null;
	}

	// public static void main(String[] args) throws Exception {
	// HyperLinkTrace hlt = new HyperLinkTrace();
	// hlt.init();
	// File save = new File(source);
	// int dirnum = 0;
	// String[] subdirs = null;
	// if (save.isDirectory()) {
	// subdirs = save.list();
	// Arrays.sort(subdirs);
	// dirnum = subdirs.length;
	// }
	// long startTime = System.currentTimeMillis();
	// for (int i = 0; i < dirnum; i++) {
	// hlt.getAnchorText(source + "/" + subdirs[i].toString());
	// }
	// totalAnchorText.close();
	// hash.clear();
	// hash = null;
	// long endTime = System.currentTimeMillis();
	// System.out.println("耗时" + (endTime - startTime) + "ms");
	//
	// }

	public void getAnchorText(String dir) throws IOException, ParserException {

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

	public void hashinput(String totallinkfile) throws IOException {
		String[] linesarr;
		File linkfile = new File(totallinkfile);
		BufferedReader linkinput = new BufferedReader(new FileReader(linkfile));
		String line;
		while ((line = linkinput.readLine()) != null) {
			linesarr = line.split(" ");
			if (hash.containsKey(linesarr[0])
					&& hash.containsValue(linesarr[1]))
				continue;
			hash.put(linesarr[0], linesarr[1]);
		}
		linkinput.close();
	}

	private void addAnchorText(File html, String htmlnum) throws IOException,
			ParserException {
		URL sjtu, url;
		String charsetname;
		Utils tool = new Utils();
		Parser parser = new Parser();

		Charset charset = tool.autoDetectCharset(html.toURL());
		charsetname = (charset.name().toLowerCase().equals("big5")) ? "GB2312"
				: charset.name();
		parser.setEncoding(charsetname);
		parser.setInputHTML(tool.readTextFile(html, charsetname));
		NodeList nlist = parser.extractAllNodesThatMatch(new NodeFilter() {
			public boolean accept(Node node) {
				if (node instanceof LinkTag)
					return true;
				return false;
			}
		});
		for (int i = 0; i < nlist.size(); i++) {
			CompositeTag node = (CompositeTag) nlist.elementAt(i);
			if (node instanceof LinkTag) {
				LinkTag link = (LinkTag) node;
				String linkText = link.getLinkText();
				linkText = linkText.replaceAll("&gt;", "").replaceAll("&nbsp;",
						"").replaceAll("\\s{1,}", "");
				String linkUrl = link.getLink().toLowerCase();
				if ((linkText.length() <= 50) && (linkText.length() > 0)) {
					if (linkUrl.contains("#"))
						linkUrl = linkUrl.substring(0, linkUrl.indexOf('#'));
					// 如果link不是.sjtu.edu.cn的url，则过滤 这里需要做改进
					if (linkUrl.startsWith("http://")) {
						if (!domainfilter(linkUrl))
							continue;
					} else {
						if (urlfilter(linkUrl))
							continue;
					}
					try {
						if (hash.containsKey(htmlnum)) {
							String htmlurl = hash.get(htmlnum);
							sjtu = new URL(htmlurl.substring(0, htmlurl
									.lastIndexOf('/')));
							url = new URL(sjtu, linkUrl);
						} else {
							url = null;
						}
					} catch (MalformedURLException e) {
						continue;
					}
					if (url != null) {
						// 保存anchor text至totalAnchorText文件
						StringBuffer sb = new StringBuffer();
						sb.append(url.toString()).append(' ').append(linkText);
						totalAnchorText.write(sb.toString());
						totalAnchorText.newLine();
						sb = null;
						linkText = null;
						linkUrl = null;
						sjtu = null;
						url = null;
					}
				}
			}
		}
		totalAnchorText.flush();
		nlist = null;
		parser = null;
	}

	private boolean domainfilter(String domain) {
		if (domain.contains(".sjtu.edu.cn") || domain.contains("202.120.33.84")
				|| domain.contains(".sjtu.org"))
			return true;
		return false;
	}

	/**
	 * URL过滤 .tar .gz .tgz .zip .Z .rpm .deb .rar .ps .dvi .pdf .png .jpg .jpeg
	 * .bmp .smi .tiff .gif .mov .avi .mpeg .mpg .mp3 .qt .wav .ram .rm .rmvb
	 * .jar .java .class .diff .c .cpp .h .doc .xls .ppt .mdb .rtf .exe .pps .so
	 * .psd .css .js
	 */
	private boolean urlfilter(String linkUrl) {
		if (linkUrl.contains("mailto:") || linkUrl.contains("@")
				|| linkUrl.contains("jsessionid")) {
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
		// .css .js
		if (linkUrl.endsWith(".css") || linkUrl.endsWith(".js")) {
			return true;
		}

		return false;
	}
}
