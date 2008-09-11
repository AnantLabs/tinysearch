package anchorText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/*
 * ͳ����ҳ��������������
 * */
public class InAnchorText {
	
	/**
	 * ��ʼ���ļ���д�Ȳ���
	 * @throws IOException 
	 * @throws Exception
	 */
	public void init(String totalAT, String inAT) throws IOException {
		
		totalATReader = new BufferedReader(new FileReader(new File(totalAT)));
		inATWriter = new BufferedWriter(new FileWriter(new File(inAT)));
	}
	
	/**
	 * ���������ļ����InAnchorText�ļ�
	 * @throws IOException 
	 * @throws Exception
	 */
	public void putInAnchorText2Memory() throws IOException{

		String[] linkWithAT;
		String dataLine = "";
		
		while (null != (dataLine = totalATReader.readLine())) {
			linkWithAT = dataLine.split(" ");
			if (linkWithAT.length > 0) {
				String link = linkWithAT[0].toLowerCase();
				if (link2docid.containsKey(link)) {
					String linktemp = (linkWithAT.length > 1) ? linkWithAT[1] : " ";
					lineprocess(link, linktemp);
					linktemp = null;
				} else {
					if (!link.endsWith("/")) {
						StringBuffer sb = new StringBuffer(link);
						sb.append('/');
						if (link2docid.containsKey(sb.toString())) {
							String linktemp = (linkWithAT.length > 1) ? linkWithAT[1] : " ";
							lineprocess(sb.toString(), linktemp);
							linktemp = null;
						}
						sb = null;
					}
				}
				link = null;
			}
		}
		totalATReader.close();
		link2docid.clear();
		link2docid = null;
	}
	
	/**
	 * ��total links���뵽�ڴ���
	 * @param dir
	 * @throws IOException 
	 * @throws Exception
	 */
	public void hashinput(String totallink) throws IOException {
		String[] linesarr;
		File linkfile = new File(totallink);
		BufferedReader linkinput = new BufferedReader(new FileReader(linkfile));
		String line;
		while ((line = linkinput.readLine()) != null) {
			linesarr = line.split(" ");
			if(link2docid.containsKey(linesarr[1].toLowerCase()) &&
					link2docid.containsValue(linesarr[0]))
				continue;
			link2docid.put(linesarr[1].toLowerCase(), linesarr[0]);
		}
		linkinput.close();
		linkfile = null;
		linesarr = null;
	}
	
	/**
	 * ���ڴ��е����ݱ��浽�ļ�
	 * @throws IOException
	 */
	public void saveInAnchorTextFileFromMemory() throws IOException {
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

	private BufferedReader totalATReader;
	private BufferedWriter inATWriter;
	private HashMap<String, String> link2docid = new HashMap<String, String>(180000);
	private HashMap<String, String> docid2AT = new HashMap<String, String>(180000);
	
	/**
	 * ��Ӧ����ÿһ��
	 * @param link_docid
	 * @param linkWithAT
	 */
	private void lineprocess(String link, String linkWithAT) {
		String docid = link2docid.get(link);
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
	
//	public static void main(String args[]) throws IOException {
//		InAnchorText inanchortext = new InAnchorText();
//		inanchortext.init(totalAT, inAT);
//		inanchortext.hashinput(totallink);
//		inanchortext.putInAnchorText2Memory();
//		inanchortext.saveInAnchorTextFileFromMemory();
		
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
//		System.out.println("InAnchorText��ʱ"
//				+ (System.currentTimeMillis() - startTime) + "ms�����ڱ���Ϊ�ļ�...");		
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
//		System.out.println("�ܺ�ʱ" + (endTime - startTime) + "ms");
//	}
}
