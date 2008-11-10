package Single;

/*
 * 形成索引
 * */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.mira.lucene.analysis.IK_CAnalyzer;

public class LuceneIndex {
	private HashMap<String, String> docid2AT = new HashMap<String, String>(
			180000);
	private Hashtable<String, String> hash = new Hashtable<String, String>(
			180000);
	private Hashtable<String, Double> pagerank = new Hashtable<String, Double>(
			180000);
	private String subdirectory, prFile, atFile;
	// 获取Paoding中文分词器
	// private Analyzer analyzer = new PaodingAnalyzer();
	private Analyzer analyzer = new IK_CAnalyzer();
	private IndexWriter FSDWriter = null;
	private Document document = null;
	private Field docidField = null;
	private Field metaField = null;
	private Field urlField = null;
	private Field prField = null;
	private Field atField = null;
	private Field titleField = null;
	private Field contentField = null;
	private Utils tool = new Utils();

	public LuceneIndex(String pr, String inAT, String index) {
		prFile = pr;
		atFile = inAT;
		try {
			FSDirectory indexDirectory = FSDirectory.getDirectory(index);
			
			// Open a single writer and re-use it for the duration of your indexing session.
			// Use autoCommit=false when you open your IndexWriter
			FSDWriter = new IndexWriter(indexDirectory, false, analyzer, true);

			// Turn off compound file format.
			// FSDWriter.setUseCompoundFile(false);

			// Flush by RAM usage instead of document count.
			FSDWriter.setRAMBufferSizeMB(512);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// public LuceneIndex() {
	// try {
	// FileInputStream ios = new FileInputStream(System
	// .getProperty("user.dir")
	// + "/path.property");
	// Properties prop = new Properties();
	// prop.load(ios);
	// ios.close();
	// prFile = prop.getProperty("pr");
	// atFile = prop.getProperty("inAT");
	// source = prop.getProperty("save");
	// FSDWriter = new IndexWriter(prop.getProperty("index"), analyzer, true);
	// } catch (CorruptIndexException e) {
	// e.printStackTrace();
	// } catch (LockObtainFailedException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// public static void main(String[] args) {
	// File save = new File(source);
	// int dirnum = 0;
	// String[] subdirs = null;
	// if (save.isDirectory()) {
	// subdirs = save.list();
	// Arrays.sort(subdirs);
	// dirnum = subdirs.length;
	// }
	// long startTime = System.currentTimeMillis();
	// LuceneIndex luceneindex = new LuceneIndex();
	// luceneindex.setAT();
	// luceneindex.setPR();
	// for (int i = 0; i < dirnum; i++) {
	// long starttime = System.currentTimeMillis();
	// luceneindex.runnny(source + "/" + subdirs[i].toString());
	// System.out.println(subdirs[i].toString() + "完成");
	// long endtime = System.currentTimeMillis();
	// System.out.println("index 2000 document, 耗时 "
	// + (endtime - starttime) + "ms");
	// }
	// long endTime = System.currentTimeMillis();
	// System.out.println("总耗时 " + (endTime - startTime) + "ms");
	// luceneindex.optimize();
	// }

	public void runnny(String subdir) {
		this.subdirectory = subdir;
		createUrl(subdirectory);
//		System.out.println(subdirectory	+ " createUrl完成！");
		index(subdirectory);
//		System.out.println(subdirectory + " index完成!");
		hash.clear();
	}
	
	public void optimize() {
		try {
			System.out.println(" 执行FSDWriter的 optimize中......");
//			long start = System.currentTimeMillis();
			FSDWriter.optimize();
			FSDWriter.close();
//			long end = System.currentTimeMillis();
			// optimize耗时需要4分钟左右
//			System.out.println("Optimize耗时 " + (end - start) + "ms");
			System.out.println("index全部完成。");
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getName() {
		return this.getClass().getName();
	}

	private void index(String dir) {
//		System.out.println(this.getName() + dir + " 执行index中......");
		try {
			String[] subdirsarr, result;
			String title = null, content = null, meta = null;
			String url = null, at = null, docid = null;
			double pr = 0.0d;

			File subdir = new File(dir);
			if (subdir.isDirectory()) {
//				subdirsarr = subdir.list(new HtmlFilter());
				subdirsarr = subdir.list(new FilenameFilter() {
					public boolean accept(File dir, String fname) {
						return (!(fname.equals("index") || fname.endsWith(".l") || fname.endsWith(".txt")));
					}
				});
				Arrays.sort(subdirsarr);
				String parsent = dir.substring(dir.lastIndexOf("/") + 1);
				for (int i = 0, j = 0; i < subdirsarr.length; i++) {
					document = new Document();
					for (j = 0; j < subdirsarr[i].length(); j++)
						if (subdirsarr[i].charAt(j) != '0'
								&& subdirsarr[i].charAt(j) != 'f')
							break;
						else
							continue;
					if (j >= subdirsarr[i].length())
						j = subdirsarr[i].length() - 1;
					docid = parsent + "-" + subdirsarr[i].substring(j);
					System.out.println("\t index " + docid);
					url = getUrl(docid);
					pr = getPR(docid);
					at = getAT(docid);
					at = (at == null) ? " " : at;

					result = tool.parseHtml(new File(subdir, subdirsarr[i]));
					content = (result[0] == null) ? " " : result[0];
					title = (result[1] == null) ? url : result[1];
					meta = (result[2] == null) ? " " : result[2];

					if (docidField == null) {
						docidField = new Field("DOCID", docid, Field.Store.YES,
								Field.Index.NO);
					} else {
						docidField.setValue(docid);
					}
					document.add(docidField);

					if (metaField == null) {
						metaField = new Field("META", meta, Field.Store.YES,
								Field.Index.TOKENIZED);
						metaField.setBoost(1.5f);
					} else {
						metaField.setValue(meta);
					}
					document.add(metaField);

					if (urlField == null) {
						urlField = new Field("URL", url, Field.Store.YES,
								Field.Index.UN_TOKENIZED);
					} else {
						urlField.setValue(url);
					}
					document.add(urlField);

					if (prField == null) {
						prField = new Field("PR", String.valueOf(pr),
								Field.Store.YES, Field.Index.UN_TOKENIZED);
						prField.setOmitNorms(true);
					} else {
						prField.setValue(String.valueOf(pr));
					}
					document.add(prField);

					if (atField == null) {
						atField = new Field("AnchorText", at, Field.Store.YES,
								Field.Index.TOKENIZED);
						atField.setBoost(3.0f);
					} else {
						atField.setValue(at);
					}
					document.add(atField);

					if (titleField == null) {
						titleField = new Field("TITLE", title, Field.Store.YES,
								Field.Index.TOKENIZED);
						titleField.setBoost(2.0f);
					} else {
						titleField.setValue(title);
					}
					document.add(titleField);

					if (contentField == null) {
						contentField = new Field("CONTENT", content,
								Field.Store.YES, Field.Index.TOKENIZED,
								Field.TermVector.WITH_POSITIONS_OFFSETS);
					} else {
						contentField.setValue(content);
					}
					document.add(contentField);

					FSDWriter.addDocument(document);
					document = null;
				}
				subdirsarr = null;
				result = null;
				subdir = null;
				parsent = null;
			}
		} catch (CorruptIndexException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置anchor text值
	 */
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
					linetemp = ((linearr.length > 1) && (linearr[1] != null)) ? linearr[1]
							: " ";
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

	/**
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

	/**
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
		return hash.remove(htmlnum).toString().trim();
	}

	private double getPR(String htmlnum) {
		return (pagerank.containsKey(htmlnum)) 
				? (double) pagerank.remove(htmlnum)
						: 0.0d;
	}

	private String getAT(String htmlnum) {
		return (docid2AT.containsKey(htmlnum))
				? docid2AT.remove(htmlnum).toString().trim()
						: " ";
	}
}
