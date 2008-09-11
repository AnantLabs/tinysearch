package total;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.htmlparser.util.ParserException;

import Single.LuceneIndex;
import anchorText.HyperLinkTrace;
import anchorText.InAnchorText;

import pagerank.PageRank;

import link.Id2Url;
import link.LinkCreate;

public class Index {
	public static void main(String args[]) throws IOException {
		String prFile, linkMapFile, inATFile, totalLinkFile, totalATFile, source, dest;
		FileInputStream ios = new FileInputStream(System.getProperty("user.dir")
				+ "/path.property");
		Properties prop = new Properties();
		prop.load(ios);
		ios.close();
		prFile = prop.getProperty("pr");
		linkMapFile = prop.getProperty("linkmap");
		inATFile = prop.getProperty("inAT");
		totalLinkFile = prop.getProperty("totallink");
		totalATFile = prop.getProperty("totalAT");
		source = prop.getProperty("save");
		dest = prop.getProperty("index");
		
		File save = new File(source);
		int dirnum = 0;
		String[] subdirs = null;
		if (save.isDirectory()) {
			subdirs = save.list();
			Arrays.sort(subdirs);
			dirnum = subdirs.length;
		} else {
			System.out.println(save.toString() + " is not a direcotry~");
			return ;
		}
		
		/**
		 * id2url.txt��totalLink.txt
		 */
		Id2Url id2url = new Id2Url();
		id2url.init(totalLinkFile);		
		for (int i = 0; i < dirnum; i++) {
			// ��һ�����Կ����������������anchor text�Ĵ���
			id2url.computer(source + "/" + subdirs[i].toString());
			System.out.println(subdirs[i].toString() + "��id2url.txt ���");
		}
		id2url.close();
		
		id2url = null;
		System.out.println(totalLinkFile + " ���");
		
		// ��totalLink.txt���뵽�ڴ��У����Ժ�ͬһ���ã��������Ժ�ÿ��С�����Լ�ȥ��ȡ�������أ�
		
		/**
		 * linkMap.txt ������ totalLink.txt
		 */
		LinkCreate linkcreate = new LinkCreate();
		linkcreate.hashinput(totalLinkFile); // ��totalLink.txt���뵽�ڴ���
		linkcreate.init(linkMapFile);
		for (int i = 0; i < dirnum; i++) {
			linkcreate.computer(source + "/" + subdirs[i].toString());
		}
		linkcreate.close();
		
		linkcreate = null;
		System.out.println(linkMapFile + " ���");
		
		/**
		 * pageRank.txt ������ linkMap.txt
		 */
		PageRank pr = new PageRank();
		pr.computer(linkMapFile, prFile);
		
		pr = null;
		System.out.println(prFile + " ���");
		
		/**
		 * ����totalAnchorText.txt ������ totalLink.txt��source�ļ���
		 */
		HyperLinkTrace hlt = new HyperLinkTrace();
		hlt.init(totalATFile);
		hlt.hashinput(totalLinkFile);
		try {
			for (int i = 0; i < dirnum; i++) {
				hlt.getAnchorText(source + "/" + subdirs[i].toString());
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		hlt.close();
		hlt = null;
		System.out.println(totalATFile + " ���");
		
		/**
		 * ����inAnchorText.txt ������totalAnchorText.txt �� totalLink.txt
		 */
		InAnchorText inanchortext = new InAnchorText();
		inanchortext.init(totalATFile, inATFile);
		inanchortext.hashinput(totalLinkFile);
		inanchortext.putInAnchorText2Memory();
		inanchortext.saveInAnchorTextFileFromMemory();
		
		inanchortext = null;
		System.out.println(inATFile + " ���");
		
		/**
		 * ��ʼ����������,����inAnchorText.txt, pageRank.txt, source�ļ��к�index�ļ���
		 */
		LuceneIndex luceneindex = new LuceneIndex(prFile, inATFile, source, dest);
		luceneindex.setAT();
		luceneindex.setPR();
		for (int i = 0; i < dirnum; i++) {
			luceneindex.runnny(source + "/" + subdirs[i].toString());
			System.out.println(subdirs[i].toString() + " �������");
		}
		luceneindex.optimize();
		System.out.println(dest + " ���");
	}
}
