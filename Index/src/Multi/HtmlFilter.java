package Multi;

import java.io.File;
import java.io.FilenameFilter;

public class HtmlFilter implements FilenameFilter{
	
	public boolean isIndex(String file) {
		if(file.equals("index"))
			return true;
		else
			return false;
	}
	
	public boolean isLink(String file) {
		if(file.endsWith(".l"))
			return true;
		else
			return false;
	}
	
	public boolean isTxt(String file) {
		if(file.endsWith(".txt"))
			return true;
		else
			return false;
	}
	
	public boolean accept(File dir,String fname){    
	    return (!(isIndex(fname) || isLink(fname) || isTxt(fname)));
	}
}