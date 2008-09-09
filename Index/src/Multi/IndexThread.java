package Multi;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
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

public class IndexThread extends Thread {

	private static Object lockObject = new Object();

	protected Hashtable<String, String> hash = new Hashtable<String, String>(
			2000);
	protected String subdirectory;

	// 获取Paoding中文分词器
	protected Analyzer analyzer = new PaodingAnalyzer();

	// protected MD5_Encoding MD5 = new MD5_Encoding();;
	protected static IndexWriter FSDWriter = null;

	protected IndexWriter RAMWriter = null;

	public IndexThread(String subdir) {
		this.subdirectory = subdir;
		try {
			FSDWriter = new IndexWriter("E:\\larbin\\index", analyzer, true);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		System.out.println(this.getName() + "正在运行..." + subdirectory);
		createUrl(subdirectory);
		System.out.println(this.getName() + "正在运行..." + subdirectory
				+ " createUrl完成！");
		/*
		 * BufferedReader buf = new BufferedReader(new
		 * InputStreamReader(System.in)); try {
		 * System.out.println(this.getName() + "输入回车确认下，哈哈"); buf.readLine();
		 * buf.close(); } catch (IOException e1) { e1.printStackTrace(); }
		 */
		index(subdirectory);
		System.out.println(this.getName() + "......完成于" + subdirectory);
	}

	private void index(String dir) {
		System.out.println(this.getName() + " 执行index中......");
		try {
			String[] subdirsarr;
			RAMDirectory ramDirectory = new RAMDirectory();
			System.out.println(this.getName() + " ramDiretory new 完成");
			RAMWriter = new IndexWriter(ramDirectory, analyzer, true);
			System.out.println(this.getName() + " RAMWriter new 完成");
			File subdir = new File(dir);
			subdirsarr = subdir.list(new HtmlFilter());
			Arrays.sort(subdirsarr);
			String parsent = dir.substring(dir.lastIndexOf("\\") + 1);
			for (int i = 0,j=0; i < subdirsarr.length; i++) {
//				String htmlnum = subdirsarr[i].substring(subdirsarr[i].lastIndexOf("0") + 1);
				//htmlnum = subdirsarr[1].split(regex, limit)
				for(j = 0; j < subdirsarr[i].length(); j++)
					if(subdirsarr[i].charAt(j) != '0' && subdirsarr[i].charAt(j) != 'f')
						break;
					else
						continue;
				String htmlnum = subdirsarr[i].substring(j);
				Document doc = addDocument(new File(subdir, subdirsarr[i]),
						parsent + "-" + htmlnum);
				RAMWriter.addDocument(doc);
			}
			RAMWriter.close();
			synchronized (lockObject) {
				FSDWriter.addIndexes(new Directory[] { ramDirectory });
			}
			subdirsarr = null;
			subdir = null;
			parsent = null;
			ramDirectory.close();
		} catch (CorruptIndexException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createUrl(String dir) {
		System.out.println(this.getName() + " 执行createUrl中......");
		String[] linearr;
		String urlcount, url, line;
		BufferedReader diskInput = null;
		BufferedWriter newfile = null;
		try {
			File sourcefile = new File(dir + "\\index");
			diskInput = new BufferedReader(new FileReader(sourcefile));
			newfile = new BufferedWriter(new FileWriter(new File(dir
					+ "\\id2url.txt")));
			line = diskInput.readLine();
			while (line != null) {
				linearr = line.split(" ");
				String parsent = dir.substring(dir.lastIndexOf("\\") + 1);
				urlcount = linearr[linearr.length - 2];
				// System.out.println(parsent + "-" + urlcount);
				url = linearr[linearr.length - 1];
				// System.out.println(url);
				hash.put(parsent + "-" + urlcount, url);
				// System.out.println("加密md5\t" + MD5.getMD5ofStr(parsent + "-"
				// + urlcount));
				// System.out.println("加密md5\t" + MD5.getMD5ofStr(url));
				// newfile.write(MD5.getMD5ofStr(parsent + "-" +
				// urlcount)+"-"+MD5.getMD5ofStr(url));
				newfile.write(parsent + "-" + urlcount);
				newfile.write("\t" + url);
				newfile.newLine();
				newfile.flush();
				line = diskInput.readLine();
			}
			diskInput.close();
			newfile.close();
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

	private Document addDocument(File html, String htmlnum) throws Exception {
		System.out.println(this.getName() + " 执行addDocument中......");
		String charset = null;
		String title = null;
		String content = null;
		String result[];

		String url = getUrl(htmlnum);
		synchronized (lockObject) {
			charset = Utils.autoDetectCharset(html.toURL());
		}
		result = Utils.parseHtml(html, charset);
		title = result[1];
		content = result[0];

		Document document = new Document();
		if (htmlnum != null && !htmlnum.equals("")) {
			document.add(new Field("docid", htmlnum, Field.Store.YES,
					Field.Index.NO));
		}
		if (url != null && !url.equals("")) {
			document
					.add(new Field("url", url, Field.Store.YES, Field.Index.NO));
		}
		if (title != null && !title.equals("")) {
			document.add(new Field("title", title, Field.Store.YES,
					Field.Index.TOKENIZED));
		}
		if (content != null && !content.equals("")) {
			document.add(new Field("content", content, Field.Store.COMPRESS,
					Field.Index.TOKENIZED,
					Field.TermVector.WITH_POSITIONS_OFFSETS));
		}
		title = null;
		content = null;
		result = null;
		return document;
	}

	public static void main(String[] args) {
		String source = "E:\\larbin\\save";
		File save = new File(source);
		int dirnum = 0;
		String[] subdirs = null;
		if (save.isDirectory()) {
			subdirs = save.list();
			Arrays.sort(subdirs);
			dirnum = subdirs.length;
		}
		IndexThread[] threads = new IndexThread[dirnum];
		// Give each thread a sub directory to work with
		for (int i = 0; i < dirnum; i++) {
			threads[i] = new IndexThread(source + "\\" + subdirs[i].toString());
			threads[i].start();
		}
		System.out.println("indexthread[]分派完成");
		// Wait for each thread to finish
		// /*
		try {
			for (int i = 0; i < dirnum; i++) {
				// 关于join()的用法，当调用 Thread.join() 时，调用线程将阻塞，直到目标线程完成为止。
				// threads[i].join()中，threads[i]为目标线程
				System.out.println(threads[i].getName() + "的join方法");
				threads[i].join();
			}
		} catch (InterruptedException e) {
			// fall through
		}
		// */
		synchronized (lockObject) {
			try {
				// System.out.println(this.getName() + " 执行FSDWriter中......");
				FSDWriter.optimize();
				FSDWriter.close();
				System.out.println("index完成");
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
