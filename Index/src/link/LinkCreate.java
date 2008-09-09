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

public class LinkCreate {
	private BufferedWriter newfile, newlink;
	private BufferedReader diskInput;
	protected Hashtable<String, String> hash = new Hashtable<String, String>();
	protected Hashtable<String, String> hash2 = new Hashtable<String, String>();
	
	public void init() throws IOException {
//		newfile = new BufferedWriter(new FileWriter(new File("E:/larbin/totallink.txt")));
		newlink = new BufferedWriter(new FileWriter(new File("E:/larbin/linkmap.txt")));
	}
	
	public void init(String dir) throws Exception{
		File sourcefile = new File(dir + "/id2url.txt");
		diskInput = new BufferedReader(new FileReader(sourcefile));
		String line = diskInput.readLine();
		while (line != null ){
			newfile.write(line);
			newfile.newLine();
			newfile.flush();
			line = diskInput.readLine();
		}
		diskInput.close();
	}

	public void computer(String dir) throws Exception{
		String[] subfilesarr;
		File subdir = new File(dir);
		if (subdir.isDirectory()) {
			subfilesarr = subdir.list(new HtmlFilter() {
				public boolean accept(File dir,String fname){
					if (fname.endsWith(".l"))
						return true;
					return false;
				}
			});	
			Arrays.sort(subfilesarr);
			String parsent = dir.substring(dir.lastIndexOf("/") + 1);
			System.out.println("parsent is:" + parsent);
			for (int i = 0, j = 0; i < subfilesarr.length; i++) {
				for (j = 0; j < subfilesarr[i].length() - 2; j++)
					if (subfilesarr[i].charAt(j) != '0'
							&& subfilesarr[i].charAt(j) != 'f')
						break;
					else
						continue;
				if (j >= subfilesarr[i].length() - 2)
					j = subfilesarr[i].length() - 3;
				String htmlnum = subfilesarr[i].substring(j, subfilesarr[i].length()-2);
				System.out.println("htmlnum is:" + htmlnum);
				newlink.write(parsent+"-"+htmlnum+" ");
				
				diskInput = new BufferedReader(new FileReader(new File(dir, subfilesarr[i])));
				String line = diskInput.readLine();
				while (line != null ){
					if (hash.containsKey(line) && !hash2.containsValue(line)){
						String num = hash.get(line);
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
	
	public void hashinput(String dir) throws Exception{
		String[] linesarr;
		File linkfile = new File(dir);
		BufferedReader linkinput = new BufferedReader(new FileReader(linkfile));
		String line = linkinput.readLine();
		while (line != null){
			linesarr = line.split("\t");
//			hash.put(linesarr[0], linesarr[1]);
			hash.put(linesarr[1], linesarr[0]);
			line = linkinput.readLine();
		}
		linkinput.close();
	}
	
	public static void main(String[] args) throws Exception {
		String source = "E:/larbin/save";
		File save = new File(source);
		int dirnum = 0;
		String[] subdirs = null;
		if (save.isDirectory()) {
			subdirs = save.list();
			Arrays.sort(subdirs);
			dirnum = subdirs.length;
		}
		LinkCreate linkcreate = new LinkCreate();
		linkcreate.hashinput("E:/larbin/totallink.txt");
		linkcreate.init();
		for (int i = 0; i < dirnum; i++) {
			System.out.println(subdirs[i].toString() + "Start£¡£¡£¡£¡£¡£¡£¡£¡£¡£¡");
//			linkcreate.init(source + "/" + subdirs[i].toString());
			linkcreate.computer(source + "/" + subdirs[i].toString());
			System.out.println(subdirs[i].toString() + "Íê³É£¡£¡£¡£¡£¡£¡£¡£¡£¡£¡");
		}

	}
}
