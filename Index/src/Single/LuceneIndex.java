package Single;

/*
 * 形成索引
 * */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

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
	
	public LuceneIndex(String pr, String inAT, String save, String index){
		prFile = pr;
		atFile = inAT;
		try {
			FSDWriter = new IndexWriter(index, analyzer, true);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public LuceneIndex() {
//		try {
//			FileInputStream ios = new FileInputStream(System
//					.getProperty("user.dir")
//					+ "/path.property");
//			Properties prop = new Properties();
//			prop.load(ios);
//			ios.close();
//			prFile = prop.getProperty("pr");
//			atFile = prop.getProperty("inAT");
//			source = prop.getProperty("save");
//			FSDWriter = new IndexWriter(prop.getProperty("index"), analyzer, true);
//		} catch (CorruptIndexException e) {
//			e.printStackTrace();
//		} catch (LockObtainFailedException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	public static void main(String[] args) {
//		File save = new File(source);
//		int dirnum = 0;
//		String[] subdirs = null;
//		if (save.isDirectory()) {
//			subdirs = save.list();
//			Arrays.sort(subdirs);
//			dirnum = subdirs.length;
//		}
//		long startTime = System.currentTimeMillis();
//		LuceneIndex luceneindex = new LuceneIndex();
//		luceneindex.setAT();
//		luceneindex.setPR();
//		for (int i = 0; i < dirnum; i++) {
//			long starttime = System.currentTimeMillis();
//			luceneindex.runnny(source + "/" + subdirs[i].toString());
//			System.out.println(subdirs[i].toString() + "完成");
//			long endtime = System.currentTimeMillis();
//			System.out.println("index 2000 document, 耗时 "
//					+ (endtime - starttime) + "ms");
//		}
//		long endTime = System.currentTimeMillis();
//		System.out.println("总耗时 " + (endTime - startTime) + "ms");
//		luceneindex.optimize();
//	}

	public void optimize(){
		try {
			System.out.println(" 执行FSDWriter的 optimize中......");
			long start = System.currentTimeMillis();
			FSDWriter.optimize();
			FSDWriter.close();
			long end = System.currentTimeMillis();
			System.out.println("Optimize耗时 " + (end - start) + "ms");
			System.out.println("index全部完成。");
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void runnny(String subdir) {
		this.subdirectory = subdir;
		// System.out.println(this.getName() + "正在运行..." + subdirectory);
		createUrl(subdirectory);
		// System.out.println(this.getName() + "正在运行..." + subdirectory + "
		// createUrl完成！");
		index(subdirectory);
		System.out.println(this.getName() + "......完成于" + subdirectory);
		hash.clear();
	}

	private HashMap<String, String> docid2AT = new HashMap<String, String>(180000);
	private Hashtable<String, String> hash = new Hashtable<String, String>(180000);
	private Hashtable<String, Double> pagerank = new Hashtable<String, Double>(180000);
	private String subdirectory, prFile, atFile;
	// 获取Paoding中文分词器
	private Analyzer analyzer = new PaodingAnalyzer();
	private IndexWriter FSDWriter = null;
	private IndexWriter RAMWriter = null;

	private String getName() {
		return this.getClass().getName();
	}

	private void index(String dir) {
		System.out.println(this.getName() + " 执行index中......");
		try {
			String[] subdirsarr;
			RAMDirectory ramDirectory = new RAMDirectory();
			RAMWriter = new IndexWriter(ramDirectory, analyzer, true);
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
					Document doc = addDocument(new File(subdir, subdirsarr[i]),
							parsent + "-" + htmlnum);
					RAMWriter.addDocument(doc);
					doc = null;
				}
				RAMWriter.close();
				FSDWriter.addIndexes(new Directory[] { ramDirectory });
				subdirsarr = null;
				subdir = null;
				parsent = null;
//				ramDirectory.close();
				ramDirectory = null;
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
				if (linearr.length > 0) {
					String linetemp;
					linetemp = ((linearr.length > 1) && (linearr[1] != null))? linearr[1] : " ";
					docid2AT.put(linearr[0], linetemp);
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

	/*
	 * 设置PageRank值
	 */
	public void setPR() {
		String[] prArr;
		String prline;
		BufferedReader prInput = null;
		File prfile = new File(prFile);
		try {
			prInput = new BufferedReader(new FileReader(prfile));
			prline = prInput.readLine();
			while (prline != null) {
				prArr = prline.split(" ");
				double pr = Double.valueOf(prArr[1]);
				pagerank.put(prArr[0], pr);
				prline = prInput.readLine();
			}
			prInput.close();
			prArr = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 设置url
	 */
	private void createUrl(String dir) {
		String[] linearr;
		String urlcount, url, line;
		BufferedReader diskInput = null;
		try {
			File sourcefile = new File(dir + "/index");
			diskInput = new BufferedReader(new FileReader(sourcefile));
			line = diskInput.readLine();
			while (line != null) {
				linearr = line.split(" ");
				String parsent = dir.substring(dir.lastIndexOf("/") + 1);
				urlcount = linearr[linearr.length - 2];
				url = linearr[linearr.length - 1];
				hash.put(parsent + "-" + urlcount, url);
				line = diskInput.readLine();
			}
			diskInput.close();
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

	private double getPR(String htmlnum) {
		if (pagerank.containsKey(htmlnum)) {
			double pr = (double) pagerank.get(htmlnum);
			pagerank.remove(htmlnum);
			return pr;
		} else {
			return 0.0d;
		}
	}

	private String getAT(String htmlnum) {
		if (docid2AT.containsKey(htmlnum))
			return docid2AT.remove(htmlnum).toString().trim();
		else
			return " ";
	}

	private Document addDocument(File html, String htmlnum) throws Exception {
		Charset charset = null;
		String title = null, content = null, meta = null;
		String result[];

		String url = getUrl(htmlnum);
		double pr = getPR(htmlnum);
		String at = getAT(htmlnum);
		Utils tool = new Utils();
		charset = tool.autoDetectCharset(html.toURL());
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
			urlField.setBoost(3.0f);
			document.add(urlField);
		}

		Field prField = new Field("PR", String.valueOf(pr),
				Field.Store.COMPRESS, Field.Index.UN_TOKENIZED);
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
}
