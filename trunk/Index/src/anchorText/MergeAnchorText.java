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
	 * �ϲ�total102new��totalnew�ļ�
	 * ���ȶ���totalnew��ÿһ�У�������url��ַ��������vector��
	 * Ȼ�����total102newÿһ�У�����ǰ���url��ַ���ж��Ƿ������vector�У�����������˵�
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
		System.out.println("totalAnchorTextnew����vector���");
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
		System.out.println("totalAnchorText102new�������");
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
		System.out.println("totalAnchorTextnew������totalAnchorText.txt�������");
	}
}
