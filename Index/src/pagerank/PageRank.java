package pagerank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * PR值计算及归一化 linkmaptest.txt内容为 d00000-0 d00000-0 d00000-1 d00000-2 d00000-1
 * d00000-2 d00000-1 d00000-2 d00000-0 d00000-3
 * 
 * @author cc512
 * 
 */
public class PageRank {
	/**
	 * 
	 * @param src
	 *            输入文件地址
	 * @param dest
	 *            输出文件地址
	 * @throws IOException
	 */
	public void computer(String src, String dest) throws IOException {
		File srcFile = new File(src);
		File destFile = new File(dest);
		computer(srcFile, destFile);
	}

	/**
	 * 
	 * @param srcfile
	 *            输入文件linkmap文件
	 * @param destfile
	 *            输出文件pagerank文件
	 * @throws IOException
	 */
	public void computer(File srcfile, File destfile) throws IOException {

		String[] linesarr;
		Hashtable<String, Integer> docIDandNum = new Hashtable<String, Integer>(
				100000);
		int total = 0;
		int father, son;
		int outdegree = 0;

		// 读取文件，得到docid，计算链接总数total，outdegree在迭代的时候计算
		BufferedReader linkinput = new BufferedReader(new FileReader(srcfile));
		String line = linkinput.readLine();
		while (line != null) {
			++total;
			linesarr = line.split(" ");
			int arrLen = linesarr.length;
			if (arrLen > 0) {
				if (linesarr[0] != null) {
					docIDandNum.put(linesarr[0], total);
				}
			}
			linesarr = null;
			line = linkinput.readLine();
		}
		linkinput.close();

		if (total > 0) {
			double[] pageRank = new double[total + 1]; // pageRank[]存放PR值
			double[] prTmp = new double[total + 1]; // 链入页面的计算总和
			double fatherRank = 1D; // 当前页面的PR值
			double alpha = 0.85D; // 阻尼系数d或称为alpha
			for (int i = 1; i <= total; ++i) {
				pageRank[i] = 1.0D; // 设置pageRank[]初始值为1.0
				prTmp[i] = 0.0D;
			}

			// 进行10次迭代
			for (int iterator = 0; iterator < 10; iterator++) {
				// long startTime = System.currentTimeMillis();
				double sum = 0.0D;
				linkinput = new BufferedReader(new FileReader(srcfile));
				line = linkinput.readLine();
				// 读出docid和outdegree和sons
				while (line != null) {
					linesarr = line.split(" ");
					int arrLen = linesarr.length;
					if (arrLen > 0) {
						father = (int) docIDandNum.get(linesarr[0]);
						outdegree = arrLen - 1;
						for (int j = 1; j <= arrLen - 1; ++j) {
							// 指向自身的链接无效，不计算在内
							if (linesarr[j].equals(linesarr[0]))
								outdegree--;
						}
						if (outdegree > 0) {
							// 对应公式中的pr(Ti)/c(Ti),Ti为指向father的页面
							fatherRank = pageRank[father] / outdegree;
							for (int k = 1; k <= arrLen - 1; ++k) {
								if (linesarr[k].equals(linesarr[0])) {
									continue;
								}
								if (docIDandNum.containsKey(linesarr[k])) {
									son = docIDandNum.get(linesarr[k]);
									if (total >= son && son >= 0) {
										prTmp[son] += fatherRank;
									}
								}
							}
						}
					}
					linesarr = null;
					line = linkinput.readLine();
				}
				/*
				 * 归一化公式1 a = TFi/ MaxTF 归一化公式2 0.5 + 0.5 * a 归一化公式3 TF /
				 * Math.sqrt(sum(TFi * TFi)) 采用公式3
				 */
				for (int i = 1; i <= total; ++i) {
					sum += prTmp[i] * prTmp[i];
				}
				// 归一化链接数并准备下次迭代的初始值
				for (int i = 1; i <= total; ++i) {
					// 归一化链接数
					prTmp[i] = prTmp[i] / Math.sqrt(sum);
					prTmp[i] = 0.15D + alpha * prTmp[i];
					// PR公式2 prTmp[i] = 0.15D / total + alpha * prTmp[i];
					pageRank[i] = prTmp[i]; // 下次迭代的初始值
					prTmp[i] = 0.0D;
				}
				linkinput.close();
				System.out.println("第" + (iterator + 1) + "次迭代完成");
				// + "，耗时" + (System.currentTimeMillis() - startTime) + "ms");
			}

			// 增加PR值与docid的对应关系
			Hashtable<Integer, String> num2Docid = new Hashtable<Integer, String>();
			Iterator it = docIDandNum.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				num2Docid.put((Integer) entry.getValue(), (String) entry
						.getKey());
			}
			docIDandNum = null;
			// 最终PR值输出至文件
			BufferedWriter newlink = new BufferedWriter(
					new FileWriter(destfile));
			for (int i = 1; i <= total; ++i) {
				newlink.write(num2Docid.get(new Integer(i)));
				newlink.write(" ");
				newlink.write(String.valueOf(pageRank[i]));
				newlink.newLine();
			}
			newlink.flush();
			newlink.close();
			pageRank = null;
			prTmp = null;
			num2Docid = null;
		}
	}

	// public static void main(String[] args) throws Exception {
	// PageRank pr = new PageRank();
	// pr.computer("G:/linkmapbak.txt", "G:/pagerank.txt");
	// }
}
