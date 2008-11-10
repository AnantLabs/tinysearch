package link;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * ����ÿ���ļ����ڵ��ļ�����url�Ķ�Ӧ�б��ļ��Լ���Url�б��ļ�
 * id2url.txt�����/larbin/save�µĸ����ļ�����
 * totallink.txt�����/larbinĿ¼��
 */
public class Id2Url {

	public void init(String totallink) throws IOException {
		totalLinkWriter = new BufferedWriter(
				new FileWriter(new File(totallink)));
	}

	public void close() throws IOException {
		totalLinkWriter.close();
	}

	public void computer(String dir) throws IOException {

		File sourcefile = new File(dir + "/index");
		File id2UrlFile = new File(dir + "/id2url.txt");

		indexInput = new BufferedReader(new FileReader(sourcefile));
		id2UrlWriter = new BufferedWriter(new FileWriter(id2UrlFile));

		String line = indexInput.readLine();
		String parsent = dir.substring(dir.lastIndexOf("/") + 1);
		while (line != null) {
			String[] linearr = line.split(" ");
			int len = linearr.length;

			id2UrlWriter.write(parsent + "-" + linearr[len - 2].toString());
			id2UrlWriter.write(" ");
			id2UrlWriter.write(linearr[len - 1].toString());
			id2UrlWriter.newLine();
			id2UrlWriter.flush();

			totalLinkWriter.write(parsent + "-" + linearr[len - 2].toString());
			totalLinkWriter.write(" ");
			totalLinkWriter.write(linearr[len - 1].toString());
			totalLinkWriter.newLine();
			totalLinkWriter.flush();

			line = indexInput.readLine();
		}
		indexInput.close();
		id2UrlWriter.close();
	}

	private BufferedWriter id2UrlWriter;
	private BufferedWriter totalLinkWriter;
	private BufferedReader indexInput;
	// public static void main(String[] args) throws Exception {
	// // long startTime = System.currentTimeMillis();
	// String source;
	// Id2Url id2url = new Id2Url();
	// // id2url.init();
	// source = id2url.source;
	// File save = new File(source);
	// int dirnum = 0;
	// String[] subdirs = null;
	// if (save.isDirectory()) {
	// subdirs = save.list();
	// Arrays.sort(subdirs);
	// dirnum = subdirs.length;
	// }
	// for (int i = 0; i < dirnum; i++) {
	// System.out.println(subdirs[i].toString() + "��ʼ");
	// id2url.computer(source + "/" + subdirs[i].toString());
	// System.out.println(subdirs[i].toString() + "����");
	// }
	// id2url.close();
	// System.out.println("totallink.txt ���");
	// // System.out.println("��ʱ�� " + (System.currentTimeMillis() - startTime) +
	// "����");
	// }
}
