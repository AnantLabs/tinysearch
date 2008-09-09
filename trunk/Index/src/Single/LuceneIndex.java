package Single;

/*
 * 形成索引
 * */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;

public class LuceneIndex {
	private HashMap<String, String> docid2AT = new HashMap<String, String>();
	protected Hashtable<String, String> hash = new Hashtable<String, String>();
	protected Hashtable<String, Float> pagerank = new Hashtable<String, Float>();
	protected String subdirectory, prFile, atFile;
	public String source;

	// 获取Paoding中文分词器
	protected Analyzer analyzer = new PaodingAnalyzer();

	// protected MD5_Encoding MD5 = new MD5_Encoding();;
	public IndexWriter FSDWriter = null;

	protected IndexWriter RAMWriter = null;

	public LuceneIndex() {
		try {
			FileInputStream ios = new FileInputStream(System
					.getProperty("user.dir")
					+ "/path.property");
			Properties prop = new Properties();
			prop.load(ios);
			ios.close();
			prFile = prop.getProperty("pr");
			atFile = prop.getProperty("inAT");
			source = prop.getProperty("save");
			FSDWriter = new IndexWriter(prop.getProperty("index"), analyzer, true);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runnny(String subdir) {
		this.subdirectory = subdir;
//		System.out.println(this.getName() + "正在运行..." + subdirectory);
		createUrl(subdirectory);
//		System.out.println(this.getName() + "正在运行..." + subdirectory	+ " createUrl完成！");
		index(subdirectory);
		// System.out.println(this.getName() + "......完成于" + subdirectory);
	}

	private String getName() {
		return this.getClass().getName();
	}

	private void index(String dir) {
//		System.out.println(this.getName() + " 执行index中......");
		try {
			String[] subdirsarr;
			RAMDirectory ramDirectory = new RAMDirectory();
			RAMWriter = new IndexWriter(ramDirectory, analyzer, true);
			File subdir = new File(dir);
			if (subdir.isDirectory()){
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
				Document doc = addDocument(new File(subdir, subdirsarr[i]),
						parsent + "-" + htmlnum);
				RAMWriter.addDocument(doc);
			}
			RAMWriter.close();
			Directory[] dire = new Directory[] { ramDirectory };
			FSDWriter.addIndexes(dire);
			subdirsarr = null;
			subdir = null;
			parsent = null;
			dire = null;
//			ramDirectory.close();
			ramDirectory = null;
			System.out.println();
			}
		} catch (CorruptIndexException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setAT() {
		String atline;
		String[] linearr;
		BufferedReader atInput = null;	
		try {
			File atfile = new File(atFile);
			atInput = new BufferedReader(new FileReader(atfile));
			atline = atInput.readLine();
			while (atline != null) {
				linearr = atline.split("\t");
				if(linearr.length > 0){
					if (linearr.length > 1) {
						if (linearr[1] != null)
							docid2AT.put(linearr[0], linearr[1]);
						else
							docid2AT.put(linearr[0], " ");
					} else {
						docid2AT.put(linearr[0], " ");
					}						
				}
				atline = atInput.readLine();
			}
			atInput.close();
			atline = null;
			linearr = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	private void createUrl(String dir) {
		String[] linearr;
		String urlcount, url, line, prline;
		BufferedReader diskInput = null;
		BufferedReader prInput = null; 
//		BufferedWriter newfile = null;
		try {
			File sourcefile = new File(dir + "/index");
//			File prfile = new File(prFile);
			diskInput = new BufferedReader(new FileReader(sourcefile));
//			prInput = new BufferedReader(new FileReader(prfile));
			
//			newfile = new BufferedWriter(new FileWriter(new File(dir + "/id2url.txt")));
			
			line = diskInput.readLine();
//			prline = prInput.readLine();
//			&& prline != null
			while (line != null ) {
//				float pr = Float.valueOf(prline);
//				System.out.println(pr);
				linearr = line.split(" ");
				String parsent = dir.substring(dir.lastIndexOf("/") + 1);
				urlcount = linearr[linearr.length - 2];
				// System.out.println(parsent + "-" + urlcount);
				url = linearr[linearr.length - 1];
				// System.out.println(url);
				hash.put(parsent + "-" + urlcount, url);
//				pagerank.put(parsent + "-" + urlcount, pr);
				// System.out.println("加密md5\t" + MD5.getMD5ofStr(parsent + "-"
				// + urlcount));
				// System.out.println("加密md5\t" + MD5.getMD5ofStr(url));
				// newfile.write(MD5.getMD5ofStr(parsent + "-" +
				// urlcount)+"-"+MD5.getMD5ofStr(url));
//				newfile.write(parsent + "-" + urlcount);
//				newfile.write("\t" + url);
//				newfile.newLine();
//				newfile.flush();
				line = diskInput.readLine();
//				prline = prInput.readLine();
			}
			diskInput.close();
//			prInput.close();
//			newfile.close();
			linearr = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getUrl(String htmlnum) {
		String url = hash.get(htmlnum);
		hash.remove(htmlnum);
		return url;
	}
//	
	private float getPR(String htmlnum) {
		float pr = (float)pagerank.get(htmlnum);
		pagerank.remove(htmlnum);
		return pr;
	}
	
	private String getAT(String htmlnum) {
		if (docid2AT.containsKey(htmlnum))
			return docid2AT.remove(htmlnum).toString().trim();
		else 
			return " ";
	}
	private Document addDocument(File html, String htmlnum) throws Exception {
//		System.out.println(this.getName() + " 执行addDocument中......");
		Charset charset = null;
		String title = null;
		String content = null;
		String meta = null;
		String result[];

		String url = getUrl(htmlnum);
		float pr = getPR(htmlnum);
		String at = getAT(htmlnum);
		Utils tool = new Utils();
		charset = tool.autoDetectCharset(html.toURL());
//		if (charset.name().equals("Big5")) {
//			result = tool.parseHtml(html, "GB2312");
//		} else {
//			result = tool.parseHtml(html, charset.name());
//		}
		result = charset.name().toLowerCase().equals("big5") 
				? tool.parseHtml(html, "GB2312") 
						: tool.parseHtml(html, charset.name());
		content = result[0];
		title = result[1]==null ? url : result[1];		
		meta = result[2];

		Document document = new Document();
		if (htmlnum != null && !htmlnum.equals("")) {
			document.add(new Field("DOCID", htmlnum, Field.Store.COMPRESS,
					Field.Index.NO));
		}
		if (url != null && !url.equals("")) {
			Field urlField = new Field("URL", url, Field.Store.COMPRESS, 
					Field.Index.UN_TOKENIZED);
			urlField.setBoost(4.0f);
			document.add(urlField);
		}
		
		Field prField = new Field("PR", String.valueOf(pr), Field.Store.COMPRESS, Field.Index.UN_TOKENIZED);
		prField.setOmitNorms(true);
		document.add(prField);
		
		if (at != null && !at.equals("")) {
			Field atField = new Field("AnchorText", at, Field.Store.COMPRESS, 
					Field.Index.TOKENIZED);
			atField.setBoost(2.0f);
			document.add(atField);
		}
		
		if (meta != null && !meta.equals("")) {
			Field metaField = new Field("META", meta, Field.Store.COMPRESS,
					Field.Index.TOKENIZED);
			metaField.setBoost(1.5f);
			document.add(metaField);
		}
		
		if (title != null && !title.equals("")) {
			Field titleField = new Field("TITLE", title, Field.Store.COMPRESS,
					Field.Index.TOKENIZED);
			titleField.setBoost(1.5f);
			document.add(titleField);
		}
		
		if (content != null && !content.equals("")) {
			document.add(new Field("CONTENT", content, Field.Store.COMPRESS,
					Field.Index.TOKENIZED,
					Field.TermVector.WITH_POSITIONS_OFFSETS));
		}
		
		at = null;
		title = null;
		content = null;
		result = null;
		meta = null;
		return document;
	}

	public static void main(String[] args) {
		LuceneIndex luceneindex = new LuceneIndex();

		luceneindex.setAT();
		File save = new File(luceneindex.source);
		int dirnum = 0;
		String[] subdirs = null;
		if (save.isDirectory()) {
			subdirs = save.list();
			Arrays.sort(subdirs);
			dirnum = subdirs.length;
		}
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < dirnum; i++) {
			long starttime = System.currentTimeMillis();
			luceneindex.runnny(luceneindex.source + "/" + subdirs[i].toString());
			System.out.println(subdirs[i].toString() + "完成");
			long endtime = System.currentTimeMillis();
			System.out.println("index 2000 document, 耗时 "+ (endtime - starttime) +"ms"); 
		}
		long endTime = System.currentTimeMillis();
		System.out.println("总耗时 "+ (endTime - startTime) +"ms");
		try {
			System.out.println(" 执行FSDWriter的 optimize中......");
			long start = System.currentTimeMillis();
			luceneindex.FSDWriter.optimize();
			luceneindex.FSDWriter.close();
			long end = System.currentTimeMillis();
			System.out.println("Optimize耗时 "+ (end - start) +"ms");
			System.out.println("index全部完成。");
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
