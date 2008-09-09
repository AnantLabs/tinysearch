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
linkmaptest.txt内容为
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

		// 读取文件，得到docid，计算链接总数total，outdegree在迭代的时候计算
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
					// System.out.println("链接" + linesarr[0] + "的出度为" + outdegree);
				}
			}
			linesarr = null;
			line = linkinput.readLine();
		}
		linkinput.close();
//		System.out.println("链接总数为：" + total);

		if (total > 0) {
			// pageRank[]存放PR值
			float[] pageRank = new float[total + 1]; 

			// 链入页面的计算总和
			float[] prTmp = new float[total + 1]; 

			 // 设置pageRank[]初始值为1.0f
			for (int i = 1; i <= total; ++i) {
				pageRank[i] = 1.0f;
				prTmp[i] = 0.0f;
			}

			 // 当前页面的PR值
			float fatherRank = 1f;

			// 阻尼系数d或称为alpha
			float alpha = 0.85f; 

			// 进行10次迭代
			for (int iterator = 0; iterator < 10; iterator++) { 
				long startTime = System.currentTimeMillis();

				linkinput = new BufferedReader(new FileReader(linkfile));
				line = linkinput.readLine();
				// 读出docid和outdegree和sons
				while (line != null) { 
					linesarr = line.split(" ");
					if (linesarr.length > 0) {
						outdegree = linesarr.length - 1;
						for (int j = 1; j <= linesarr.length - 1; ++j) {
							 // 指向自身的链接无效，不计算在内
							if (linesarr[j].equals(linesarr[0]))
								outdegree--;
						}
					}
					if (outdegree > 0) {
						father = (int) docIDandNum.get(linesarr[0]);
						 // 对应公式中的pr(Ti)/c(Ti),Ti为指向father的页面
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
				// 准备下次迭代的初始值
				for (int i = 1; i <= total; ++i) 	{
					// PR公式1
					prTmp[i] = 0.15f + alpha * prTmp[i];
					
					// PR公式2
//					prTmp[i] = 0.15f / total + alpha * prTmp[i]; 
                    sum += prTmp[i] * prTmp[i];
					// 每次迭代后的真正pr值
//					pageRank[i] = prTmp[i]; 

//					prTmp[i] = 0.0f;
				}
				// 归一化，此操作耗费时间和内存
				for (int i = 1; i <= total; ++i){
					pageRank[i] = prTmp[i] / Math.sqrt(sum);

					// 每次迭代后的真正pr值
					pageRank[i] = prTmp[i]; 
					prTmp[i] = 0.0f;
				}
				linkinput.close();

				long endTime = System.currentTimeMillis();
				System.out.println("第" + iterator + "次迭代耗时" + (endTime - startTime) + "ms");
			}
			
			// ====2008.8.29新增====
			//增加PR值与docid的对应关系
			Hashtable<Integer, String> num2Docid = new Hashtable<Integer, String>();
			Iterator it = docIDandNum.entrySet().iterator();
			while(it.hasNext()){
				Entry entry = (Entry)it.next();
				// entry.getKey() 返回与此项对应的键
				// entry.getValue() 返回与此项对应的值
				num2Docid.put((Integer)entry.getValue(), (String)entry.getKey());
			}
			docIDandNum = null;
			// ====2008.8.29新增====

			//缺归一化??在PRNormalize类中实现
			
			//最终PR值输出至文件
			BufferedWriter newlink = new BufferedWriter(new FileWriter(
					new File("D:/cc/larbin/PageRank.txt")));
			for (int i = 1; i <= total; ++i) {
				
				// ====2008.8.29新增====
				newlink.write(num2Docid.get(new Integer(i)));
				newlink.write(" ");
				// ====2008.8.29新增====
				
				newlink.write(String.valueOf(pageRank[i]));
				newlink.newLine();
			}
			newlink.flush();
			newlink.close();
			pageRank = null;
			prTmp = null;

			// ====2008.8.29新增====
			num2Docid = null;
			// ====2008.8.29新增====
		}
	}
}
