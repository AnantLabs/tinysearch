package link;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import Single.HtmlFilter;

/*
 * 建立linkmap.txt存放与/larbin目录下
 */
public class LinkCreate {
	private BufferedWriter newlink;
	private BufferedReader diskInput;

	// hash是totallink.txt的hashtable
	private Hashtable<String, String> hash = new Hashtable<String, String>(
			200000);

	// hash2作用是防止重复添加链出的url
	private Hashtable<String, String> hash2 = new Hashtable<String, String>(50);

	public void init(String linkmap) throws IOException {
		newlink = new BufferedWriter(new FileWriter(new File(linkmap)));
	}

	public void close() throws IOException {
		newlink.close();
	}

	// 生成linkMap,可以考虑id2url.txt文件
	public void computer(String dir) throws IOException {
		String[] subfilesarr;
		File subdir = new File(dir);
		if (subdir.isDirectory()) {
			subfilesarr = subdir.list(new HtmlFilter() {
				public boolean accept(File dir, String fname) {
					if (fname.endsWith(".l"))
						return true;
					return false;
				}
			});
			Arrays.sort(subfilesarr);
			String parsent = dir.substring(dir.lastIndexOf("/") + 1);
			for (int i = 0, j = 0; i < subfilesarr.length; i++) {
				for (j = 0; j < subfilesarr[i].length() - 2; j++)
					if (subfilesarr[i].charAt(j) != '0'
							&& subfilesarr[i].charAt(j) != 'f')
						break;
					else
						continue;
				if (j >= subfilesarr[i].length() - 2)
					j = subfilesarr[i].length() - 3;
				String htmlnum = subfilesarr[i].substring(j, subfilesarr[i]
						.length() - 2);
				newlink.write(parsent + "-" + htmlnum + " ");
				diskInput = new BufferedReader(new FileReader(new File(dir,
						subfilesarr[i])));
				String line = diskInput.readLine();
				while (line != null) {
					if (hash.containsKey(line.toLowerCase())
							&& !hash2.containsValue(line)) {
						String num = hash.get(line.toLowerCase());
						newlink.write(num + " ");
						hash2.put(num, line);
					}
					line = diskInput.readLine();
				}
				diskInput.close();
				newlink.newLine();
				newlink.flush();
				hash2.clear();
			}
		}

	}

	/**
	 * 将totalLink.txt载入内存
	 * 
	 * @param totallink
	 * @throws IOException
	 */
	public void hashinput(String totallink) throws IOException {
		String[] linesarr;
		File linkfile = new File(totallink);
		BufferedReader linkinput = new BufferedReader(new FileReader(linkfile));
		String line;
		while ((line = linkinput.readLine()) != null) {
			linesarr = line.split(" ");
			if (hash.containsKey(linesarr[1].toLowerCase())
					&& hash.containsValue(linesarr[0]))
				continue;
			hash.put(linesarr[1].toLowerCase(), linesarr[0]);
		}
		linkinput.close();
	}

	// public static void main(String[] args) throws Exception {
	// long startTime = System.currentTimeMillis();
	// String source = "D:/cc/larbin/save";
	// String linkmap = "D:/cc/larbin/linkmap.txt";
	// File save = new File(source);
	// int dirnum = 0;
	// String[] subdirs = null;
	// if (save.isDirectory()) {
	// subdirs = save.list();
	// Arrays.sort(subdirs);
	// dirnum = subdirs.length;
	// }
	// LinkCreate linkcreate = new LinkCreate();
	// linkcreate.hashinput("D:/cc/larbin/totallink.txt");
	// linkcreate.init(linkmap);
	// for (int i = 0; i < dirnum; i++) {
	// System.out.println(subdirs[i].toString() + "开始");
	// linkcreate.computer(source + "/" + subdirs[i].toString());
	// System.out.println(subdirs[i].toString() + "完成");
	// }
	// linkcreate.close();
	// System.out.println("耗时： " + (System.currentTimeMillis() - startTime) +
	// "毫秒");
	// }
}
