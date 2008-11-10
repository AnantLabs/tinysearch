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
 * PRֵ���㼰��һ�� linkmaptest.txt����Ϊ d00000-0 d00000-0 d00000-1 d00000-2 d00000-1
 * d00000-2 d00000-1 d00000-2 d00000-0 d00000-3
 * 
 * @author cc512
 * 
 */
public class PageRank {
	/**
	 * 
	 * @param src
	 *            �����ļ���ַ
	 * @param dest
	 *            ����ļ���ַ
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
	 *            �����ļ�linkmap�ļ�
	 * @param destfile
	 *            ����ļ�pagerank�ļ�
	 * @throws IOException
	 */
	public void computer(File srcfile, File destfile) throws IOException {

		String[] linesarr;
		Hashtable<String, Integer> docIDandNum = new Hashtable<String, Integer>(
				100000);
		int total = 0;
		int father, son;
		int outdegree = 0;

		// ��ȡ�ļ����õ�docid��������������total��outdegree�ڵ�����ʱ�����
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
			double[] pageRank = new double[total + 1]; // pageRank[]���PRֵ
			double[] prTmp = new double[total + 1]; // ����ҳ��ļ����ܺ�
			double fatherRank = 1D; // ��ǰҳ���PRֵ
			double alpha = 0.85D; // ����ϵ��d���Ϊalpha
			for (int i = 1; i <= total; ++i) {
				pageRank[i] = 1.0D; // ����pageRank[]��ʼֵΪ1.0
				prTmp[i] = 0.0D;
			}

			// ����10�ε���
			for (int iterator = 0; iterator < 10; iterator++) {
				// long startTime = System.currentTimeMillis();
				double sum = 0.0D;
				linkinput = new BufferedReader(new FileReader(srcfile));
				line = linkinput.readLine();
				// ����docid��outdegree��sons
				while (line != null) {
					linesarr = line.split(" ");
					int arrLen = linesarr.length;
					if (arrLen > 0) {
						father = (int) docIDandNum.get(linesarr[0]);
						outdegree = arrLen - 1;
						for (int j = 1; j <= arrLen - 1; ++j) {
							// ָ�������������Ч������������
							if (linesarr[j].equals(linesarr[0]))
								outdegree--;
						}
						if (outdegree > 0) {
							// ��Ӧ��ʽ�е�pr(Ti)/c(Ti),TiΪָ��father��ҳ��
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
				 * ��һ����ʽ1 a = TFi/ MaxTF ��һ����ʽ2 0.5 + 0.5 * a ��һ����ʽ3 TF /
				 * Math.sqrt(sum(TFi * TFi)) ���ù�ʽ3
				 */
				for (int i = 1; i <= total; ++i) {
					sum += prTmp[i] * prTmp[i];
				}
				// ��һ����������׼���´ε����ĳ�ʼֵ
				for (int i = 1; i <= total; ++i) {
					// ��һ��������
					prTmp[i] = prTmp[i] / Math.sqrt(sum);
					prTmp[i] = 0.15D + alpha * prTmp[i];
					// PR��ʽ2 prTmp[i] = 0.15D / total + alpha * prTmp[i];
					pageRank[i] = prTmp[i]; // �´ε����ĳ�ʼֵ
					prTmp[i] = 0.0D;
				}
				linkinput.close();
				System.out.println("��" + (iterator + 1) + "�ε������");
				// + "����ʱ" + (System.currentTimeMillis() - startTime) + "ms");
			}

			// ����PRֵ��docid�Ķ�Ӧ��ϵ
			Hashtable<Integer, String> num2Docid = new Hashtable<Integer, String>();
			Iterator it = docIDandNum.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				num2Docid.put((Integer) entry.getValue(), (String) entry
						.getKey());
			}
			docIDandNum = null;
			// ����PRֵ������ļ�
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
