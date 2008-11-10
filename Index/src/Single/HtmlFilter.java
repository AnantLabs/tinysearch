package Single;

import java.io.File;
import java.io.FilenameFilter;

public class HtmlFilter implements FilenameFilter {

	public boolean isIndex(String file) {
		return file.equals("index");
	}

	public boolean isLink(String file) {
		return file.endsWith(".l");
	}

	public boolean isTxt(String file) {
		return file.endsWith(".txt");
	}

	public boolean accept(File dir, String fname) {
		return (!(isIndex(fname) || isLink(fname) || isTxt(fname)));
	}
}