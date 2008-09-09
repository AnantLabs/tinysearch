package pagerank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
/*
linkmaptest.txt����Ϊ
d00000-0 d00000-0 d00000-1 d00000-2
d00000-1 d00000-2 d00000-1
d00000-2 d00000-0
d00000-3 
*/
public class PageRank {

	public static void main(String[] args) throws Exception {

		String[] linesarr;
		Hashtable<String, Integer> docIDandNum = new Hashtable<String, Integer>();
		int total = 0;
		int father, son;
		int outdegree = 0;

		// ��ȡ�ļ����õ�docid��������������total��outdegree�ڵ�����ʱ�����
		File linkfile = new File("D:/cc/larbin/linkmap.txt");
		BufferedReader linkinput = new BufferedReader(new FileReader(linkfile));
		String line = linkinput.readLine();
		while (line != null) {
			++total;
			linesarr = line.split(" ");
			if (linesarr.length > 0) {
				// outdegree = linesarr.length - 1;
				// for(int j = 1; j <= linesarr.length - 1; ++j) {
				// if(linesarr[j].equals(linesarr[0]))
				// outdegree--;
				// }
				if (linesarr[0] != null) {
					docIDandNum.put(linesarr[0], total);
					// System.out.println("����" + linesarr[0] + "�ĳ���Ϊ" + outdegree);
				}
			}
			linesarr = null;
			line = linkinput.readLine();
		}
		linkinput.close();
//		System.out.println("��������Ϊ��" + total);

		if (total > 0) {
			// pageRank[]���PRֵ
			float[] pageRank = new float[total + 1]; 

			// ����ҳ��ļ����ܺ�
			float[] prTmp = new float[total + 1]; 

			 // ����pageRank[]��ʼֵΪ1.0f
			for (int i = 1; i <= total; ++i) {
				pageRank[i] = 1.0f;
				prTmp[i] = 0.0f;
			}

			 // ��ǰҳ���PRֵ
			float fatherRank = 1f;

			// ����ϵ��d���Ϊalpha
			float alpha = 0.85f; 

			// ����10�ε���
			for (int iterator = 0; iterator < 10; iterator++) { 
				long startTime = System.currentTimeMillis();

				linkinput = new BufferedReader(new FileReader(linkfile));
				line = linkinput.readLine();
				// ����docid��outdegree��sons
				while (line != null) { 
					linesarr = line.split(" ");
					if (linesarr.length > 0) {
						outdegree = linesarr.length - 1;
						for (int j = 1; j <= linesarr.length - 1; ++j) {
							 // ָ�������������Ч������������
							if (linesarr[j].equals(linesarr[0]))
								outdegree--;
						}
					}
					if (outdegree > 0) {
						father = (int) docIDandNum.get(linesarr[0]);
						 // ��Ӧ��ʽ�е�pr(Ti)/c(Ti),TiΪָ��father��ҳ��
						fatherRank = pageRank[father] / outdegree;
						for (int k = 1; k <= linesarr.length - 1; ++k) {
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
					linesarr = null;
					line = linkinput.readLine();
				}
				double sum = 0.0d;
				// ׼���´ε����ĳ�ʼֵ
				for (int i = 1; i <= total; ++i) 	{
					// PR��ʽ1
					prTmp[i] = 0.15f + alpha * prTmp[i];
					
					// PR��ʽ2
//					prTmp[i] = 0.15f / total + alpha * prTmp[i]; 
                    sum += prTmp[i] * prTmp[i];
					// ÿ�ε����������prֵ
//					pageRank[i] = prTmp[i]; 

//					prTmp[i] = 0.0f;
				}
				// ��һ�����˲����ķ�ʱ����ڴ�
				for (int i = 1; i <= total; ++i){
					pageRank[i] = prTmp[i] / Math.sqrt(sum);

					// ÿ�ε����������prֵ
					pageRank[i] = prTmp[i]; 
					prTmp[i] = 0.0f;
				}
				linkinput.close();

				long endTime = System.currentTimeMillis();
				System.out.println("��" + iterator + "�ε�����ʱ" + (endTime - startTime) + "ms");
			}
			
			// ====2008.8.29����====
			//����PRֵ��docid�Ķ�Ӧ��ϵ
			Hashtable<Integer, String> num2Docid = new Hashtable<Integer, String>();
			Iterator it = docIDandNum.entrySet().iterator();
			while(it.hasNext()){
				Entry entry = (Entry)it.next();
				// entry.getKey() ����������Ӧ�ļ�
				// entry.getValue() ����������Ӧ��ֵ
				num2Docid.put((Integer)entry.getValue(), (String)entry.getKey());
			}
			docIDandNum = null;
			// ====2008.8.29����====

			//ȱ��һ��??��PRNormalize����ʵ��
			
			//����PRֵ������ļ�
			BufferedWriter newlink = new BufferedWriter(new FileWriter(
					new File("D:/cc/larbin/PageRank.txt")));
			for (int i = 1; i <= total; ++i) {
				
				// ====2008.8.29����====
				newlink.write(num2Docid.get(new Integer(i)));
				newlink.write(" ");
				// ====2008.8.29����====
				
				newlink.write(String.valueOf(pageRank[i]));
				newlink.newLine();
			}
			newlink.flush();
			newlink.close();
			pageRank = null;
			prTmp = null;

			// ====2008.8.29����====
			num2Docid = null;
			// ====2008.8.29����====
		}
	}
}
