package anchorText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

/*
 * 统计网页的链接描述文字
 * */
public class InAnchorText {

	public void init() throws Exception {
		FileInputStream ios = new FileInputStream(System
				.getProperty("user.dir")
				+ "/path.property");
		Properties prop = new Properties();
		prop.load(ios);
		ios.close();

		// totalAnchorText = new BufferedWriter(new FileWriter(new File(prop
		// .getProperty("totalAT"))));
		totalATReader = new BufferedReader(new FileReader(new File(prop.getProperty("totalAT"))));
		hashinput(prop.getProperty("totallink"));
		// source = prop.getProperty("save");
		inATWriter = new BufferedWriter(new FileWriter(new File(prop
				.getProperty("inAT"))));
	}

	public static void main(String args[]) throws Exception {
		long startTime = System.currentTimeMillis();

		String[] linkWithAT;
		InAnchorText inAT = new InAnchorText();
		inAT.init();
		StringBuffer sbStr = new StringBuffer();
		String dataLine = "";
		
		while (null != (dataLine = totalATReader.readLine())) {
			linkWithAT = dataLine.split(" ");
			if (linkWithAT.length > 0) {
				String link_docid = linkWithAT[0].toLowerCase();
				if (link2docid.containsKey(link_docid)) {
					if (linkWithAT.length > 1) {
						lineprocess(link_docid, linkWithAT[1]);
					} else {
						lineprocess(link_docid, " ");
					}
				} else {
					if (!link_docid.endsWith("/")) {
						StringBuffer sb = new StringBuffer(link_docid);
						sb.append('/');
						if (link2docid.containsKey(sb.toString())) {
							if (linkWithAT.length > 1) {
								lineprocess(sb.toString(), linkWithAT[1]);
							} else {
								lineprocess(sb.toString(), " ");
							}
						}
					}
				}
			}
		}
//		link2docid.clear();
//		link2docid = null;
		totalATReader.close();
		System.out.println("G:/totalAnchorText102new.txt完成");
		totalATReader = new BufferedReader(new FileReader(new File("G:/totalAnchorTextnew.txt")));
		while (null != (dataLine = totalATReader.readLine())) {
			linkWithAT = dataLine.split(" ");
			if (linkWithAT.length > 0) {
				String link_docid = linkWithAT[0].toLowerCase();
				if (link2docid.containsKey(link_docid)) {
					if (linkWithAT.length > 1) {
						lineprocess(link_docid, linkWithAT[1]);
					} else {
						lineprocess(link_docid, " ");
					}
				} else {
					if (!link_docid.endsWith("/")) {
						StringBuffer sb = new StringBuffer(link_docid);
						sb.append('/');
						if (link2docid.containsKey(sb.toString())) {
							if (linkWithAT.length > 1) {
								lineprocess(sb.toString(), linkWithAT[1]);
							} else {
								lineprocess(sb.toString(), " ");
							}
						}
					}
				}
			}
		}
		
		System.out.println("InAnchorText耗时"
				+ (System.currentTimeMillis() - startTime) + "ms，正在保存为文件...");
		
		Iterator it = docid2AT.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			// entry.getKey() 返回与此项对应的键
			// entry.getValue() 返回与此项对应的值
			StringBuffer sb = new StringBuffer(entry.getKey().toString().trim());
			sb.append(' ').append(entry.getValue().toString().trim());
//			System.out.println(sb.toString());
			inATWriter.write(sb.toString());
			inATWriter.newLine();
			inATWriter.flush();
			sb = null;
		}
		docid2AT.clear();
		docid2AT = null;
		inATWriter.close();
		long endTime = System.currentTimeMillis();
		System.out.println("总耗时" + (endTime - startTime) + "ms");
	}

	private static BufferedReader totalATReader;
	private static BufferedWriter inATWriter;
	private static HashMap<String, String> link2docid = new HashMap<String, String>();
	private static HashMap<String, String> docid2AT = new HashMap<String, String>();

	private static void hashinput(String dir) throws Exception {
		String[] linesarr;
		File linkfile = new File(dir);
		BufferedReader linkinput = new BufferedReader(new FileReader(linkfile));
		String line = linkinput.readLine();
		while (line != null) {
			linesarr = line.split(" ");
			link2docid.put(linesarr[1], linesarr[0]);
			line = linkinput.readLine();
		}
		linkinput.close();
		linkfile = null;
		linesarr = null;
	}

	private static void lineprocess(String link_docid, String linkWithAT) {

		String docid = link2docid.get(link_docid);
		if (docid2AT.isEmpty()) {
			docid2AT.put(docid, linkWithAT);
		} else {
			if (docid2AT.containsKey(docid)) {
				StringBuffer sb = new StringBuffer(docid2AT.remove(docid)
						.trim());
				sb.append(' ').append(linkWithAT);
				docid2AT.put(docid, sb.toString());
				sb = null;
			} else {
				docid2AT.put(docid, linkWithAT);
			}
		}

	}
}
