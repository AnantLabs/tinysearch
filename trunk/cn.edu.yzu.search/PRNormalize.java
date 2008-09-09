package pagerank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
/**
 * PR值归一化
 */
public class PRNormalize {
	
	public static void main(String args[]) throws Exception{
		long startTime = System.currentTimeMillis();
		
		String prFile = "D:/cc/larbin/PageRank.txt";
		String prline;
		String[] strline ;
		int i = 0;
		double normalizedPR = 0.0d;
		double maxtf = 0.0d;
		double sumTF = 0.0d;
		HashMap<String, Double> pagerank = new HashMap<String, Double>();
				
		BufferedReader prInput = new BufferedReader(new FileReader(new File(prFile)));
		prline = prInput.readLine();
		while (prline != null) {
			strline = prline.split(" ");
			double pr = Double.valueOf(strline[1]);
			pagerank.put(strline[0], pr);
			i++;
			prline = prInput.readLine();
		}
		prInput.close();
		
		/*
		 * 1 a = TF/ MaxTF
		 * 2 0.5 + 0.5 * a
		 * 3 TF / Math.sqrt(sum(TFi * TFi))
		 */
		Iterator it = pagerank.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry)it.next();
			
//			if (Double.compare(maxtf, (Double)entry.getValue()) < 0) {
//				maxtf = (Double)entry.getValue();
//			}
			sumTF += ((Double)entry.getValue() * (Double)entry.getValue());
		}		
//		System.out.println(String.valueOf(maxtf));
		
		//最终PR值输出至文件
		BufferedWriter newlink = new BufferedWriter(new FileWriter(
				new File("D:/cc/larbin/NormPR3.txt")));
		it = pagerank.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry)it.next();
			// Maximum Normalization
//			normalizedPR = ((Double)entry.getValue()) / maxtf;
			// Augmented Maximum Normalization
//			normalizedPR = 0.5 + 0.5 * (((Double)entry.getValue()) / maxtf);
			// Cosine Normalization
			normalizedPR = ((Double)entry.getValue()) / Math.sqrt(sumTF);
			// ====2008.8.29新增====
			newlink.write((String)entry.getKey());
			newlink.write(" ");
			// ====2008.8.29新增====
			
			newlink.write(String.valueOf(normalizedPR));
			newlink.newLine();
		}
		newlink.flush();
		newlink.close();
		pagerank = null;
		System.out.println("耗时" + (System.currentTimeMillis() - startTime) + "ms");
	}
}
