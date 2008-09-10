package anchorText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

/*
 * 统计网页的链接描述文字
 * */
public class InAnchorText {
	
	/**
	 * 初始化文件读写等参数
	 * @throws IOException 
	 * @throws Exception
	 */
	public void init() throws IOException {
		FileInputStream ios = new FileInputStream(System
				.getProperty("user.dir")
				+ "/path.property");
		Properties prop = new Properties();
		prop.load(ios);
		ios.close();
		totalATReader = new BufferedReader(new FileReader(new File(prop.getProperty("totalAT"))));
		hashinput(prop.getProperty("totallink"));
		inATWriter = new BufferedWriter(new FileWriter(new File(prop
				.getProperty("inAT"))));
	}
	
	/**
	 * 根据输入文件输出InAnchorText文件
	 * @throws IOException 
	 * @throws Exception
	 */
	public void putInAnchorText2Memory() throws IOException{

		String[] linkWithAT;
		String dataLine = "";
		
		while (null != (dataLine = totalATReader.readLine())) {
			linkWithAT = dataLine.split(" ");
			if (linkWithAT.length > 0) {
				String link_docid = linkWithAT[0].toLowerCase();
				if (link2docid.containsKey(link_docid)) {
					String linktemp = (linkWithAT.length > 1 ) ? linkWithAT[1] : " ";
					lineprocess(link_docid, linktemp);
				} else {
					if (!link_docid.endsWith("/")) {
						StringBuffer sb = new StringBuffer(link_docid);
						sb.append('/');
						if (link2docid.containsKey(sb.toString())) {
							String linktemp = (linkWithAT.length > 1 ) ? linkWithAT[1] : " ";
							lineprocess(sb.toString(), linktemp);
						}
					}
				}
			}
		}
		totalATReader.close();
		link2docid.clear();
		link2docid = null;
		
		saveInAnchorTextFileFromMemory();
	
	}

	public static void main(String args[]) throws IOException {
		InAnchorText inanchortext = new InAnchorText();
		inanchortext.init();
		inanchortext.putInAnchorText2Memory();
		
//		long startTime = System.currentTimeMillis();
//		String[] linkWithAT;
//		InAnchorText inAT = new InAnchorText();
//		inAT.init();
//		StringBuffer sbStr = new StringBuffer();
//		String dataLine = "";
//		while (null != (dataLine = totalATReader.readLine())) {
//			linkWithAT = dataLine.split(" ");
//			if (linkWithAT.length > 0) {
//				String link_docid = linkWithAT[0].toLowerCase();
//				if (link2docid.containsKey(link_docid)) {
//					if (linkWithAT.length > 1) {
//						lineprocess(link_docid, linkWithAT[1]);
//					} else {
//						lineprocess(link_docid, " ");
//					}
//				} else {
//					if (!link_docid.endsWith("/")) {
//						StringBuffer sb = new StringBuffer(link_docid);
//						sb.append('/');
//						if (link2docid.containsKey(sb.toString())) {
//							if (linkWithAT.length > 1) {
//								lineprocess(sb.toString(), linkWithAT[1]);
//							} else {
//								lineprocess(sb.toString(), " ");
//							}
//						}
//					}
//				}
//			}
//		}
//		link2docid.clear();
//		link2docid = null;
//		totalATReader.close();
//		System.out.println("InAnchorText耗时"
//				+ (System.currentTimeMillis() - startTime) + "ms，正在保存为文件...");		
//		Iterator it = docid2AT.entrySet().iterator();
//		while (it.hasNext()) {
//			Entry entry = (Entry) it.next();
//			StringBuffer sb = new StringBuffer(entry.getKey().toString().trim());
//			sb.append('\t').append(entry.getValue().toString().trim());
////			System.out.println(sb.toString());
//			inATWriter.write(sb.toString());
//			inATWriter.newLine();
//			inATWriter.flush();
//			sb = null;
//		}
//		docid2AT.clear();
//		docid2AT = null;
//		inATWriter.close();
//		long endTime = System.currentTimeMillis();
//		System.out.println("总耗时" + (endTime - startTime) + "ms");
	}

	private BufferedReader totalATReader;
	private BufferedWriter inATWriter;
	private HashMap<String, String> link2docid = new HashMap<String, String>(300000);
	private HashMap<String, String> docid2AT = new HashMap<String, String>(300000);

	/**
	 * 将total links载入到内存中
	 * @param dir
	 * @throws IOException 
	 * @throws Exception
	 */
	private void hashinput(String dir) throws IOException {
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
	
	/**
	 * 对应处理每一行
	 * @param link_docid
	 * @param linkWithAT
	 */
	private void lineprocess(String link_docid, String linkWithAT) {

		String docid = link2docid.get(link_docid);
		if (docid2AT.isEmpty()) {
			docid2AT.put(docid, linkWithAT);
		} else {
			if (docid2AT.containsKey(docid)) {
				StringBuffer sb = new StringBuffer(docid2AT.remove(docid).trim());
				sb.append(' ').append(linkWithAT);
				docid2AT.put(docid, sb.toString());
			} else {
				docid2AT.put(docid, linkWithAT);
			}
		}
	}
	
	/**
	 * 将内存中的内容保存到文件
	 * @throws IOException
	 */
	private void saveInAnchorTextFileFromMemory() throws IOException {
		Iterator it = docid2AT.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			StringBuffer sb = new StringBuffer(entry.getKey().toString().trim());
			sb.append('\t').append(entry.getValue().toString().trim());
			inATWriter.write(sb.toString());
			inATWriter.newLine();
			inATWriter.flush();
			sb = null;
		}
		docid2AT.clear();
		docid2AT = null;
		inATWriter.close();
	}
}
