package anchorText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class MergeAnchorText {

	/**
	 * 合并total102new和totalnew文件
	 * 首先读入totalnew的每一行，分析出url地址，并存入vector中
	 * 然后读入total102new每一行，分析前面的url地址，判断是否存在于vector中，若存在则过滤掉
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		FileReader fr = new FileReader(new File("G:/totalAnchorTextnew.txt"));
		FileWriter fw = new FileWriter(new File("G:/totalAnchorText.txt"));
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(fw);
		Vector<String> vector = new Vector<String>();
		String line = br.readLine();
		String[] lineArr = null;
		while(line != null){
			lineArr = line.split(" ");
			vector.add(lineArr[0].toString());
			lineArr = null;
			line = br.readLine();
		}
		fr.close();
		br.close();
		line = "";
		System.out.println("totalAnchorTextnew加入vector完成");
		fr = new FileReader(new File("G:/totalAnchorText102new.txt"));
		br = new BufferedReader(fr);
		line = br.readLine();
		while(line != null){
			lineArr = line.split(" ");
			if(vector.contains(lineArr[0].toString())){
				// do nothing
			}else
			{
				bw.write(line);
				bw.newLine();
				bw.flush();
			}
			line = br.readLine();
		}
		System.out.println("totalAnchorText102new处理完成");
		fr = new FileReader(new File("G:/totalAnchorTextnew.txt"));
		br = new BufferedReader(fr);
		line = br.readLine();
		while(line != null){
			bw.write(line);
			bw.newLine();
			bw.flush();
			line = br.readLine();
		}
		bw.close();
		System.out.println("totalAnchorTextnew加入新totalAnchorText.txt处理完成");
	}
}
