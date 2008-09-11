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
		 * id2url.txt和totalLink.txt
		 */
		Id2Url id2url = new Id2Url();
		id2url.init(totalLinkFile);		
		for (int i = 0; i < dirnum; i++) {
			// 下一步可以考虑在这里加上生成anchor text的代码
			id2url.computer(source + "/" + subdirs[i].toString());
			System.out.println(subdirs[i].toString() + "的id2url.txt 完成");
		}
		id2url.close();
		
		id2url = null;
		System.out.println(totalLinkFile + " 完成");
		
		// 将totalLink.txt载入到内存中，供以后同一调用？还是让以后每个小程序自己去读取、销毁呢？
		
		/**
		 * linkMap.txt 依赖于 totalLink.txt
		 */
		LinkCreate linkcreate = new LinkCreate();
		linkcreate.hashinput(totalLinkFile); // 将totalLink.txt载入到内存中
		linkcreate.init(linkMapFile);
		for (int i = 0; i < dirnum; i++) {
			linkcreate.computer(source + "/" + subdirs[i].toString());
		}
		linkcreate.close();
		
		linkcreate = null;
		System.out.println(linkMapFile + " 完成");
		
		/**
		 * pageRank.txt 依赖于 linkMap.txt
		 */
		PageRank pr = new PageRank();
		pr.computer(linkMapFile, prFile);
		
		pr = null;
		System.out.println(prFile + " 完成");
		
		/**
		 * 生成totalAnchorText.txt 依赖于 totalLink.txt和source文件夹
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
		System.out.println(totalATFile + " 完成");
		
		/**
		 * 生成inAnchorText.txt 依赖于totalAnchorText.txt 和 totalLink.txt
		 */
		InAnchorText inanchortext = new InAnchorText();
		inanchortext.init(totalATFile, inATFile);
		inanchortext.hashinput(totalLinkFile);
		inanchortext.putInAnchorText2Memory();
		inanchortext.saveInAnchorTextFileFromMemory();
		
		inanchortext = null;
		System.out.println(inATFile + " 完成");
		
		/**
		 * 开始索引过程了,依赖inAnchorText.txt, pageRank.txt, source文件夹和index文件夹
		 */
		LuceneIndex luceneindex = new LuceneIndex(prFile, inATFile, source, dest);
		luceneindex.setAT();
		luceneindex.setPR();
		for (int i = 0; i < dirnum; i++) {
			luceneindex.runnny(source + "/" + subdirs[i].toString());
			System.out.println(subdirs[i].toString() + " 索引完成");
		}
		luceneindex.optimize();
		System.out.println(dest + " 完成");
	}
}
