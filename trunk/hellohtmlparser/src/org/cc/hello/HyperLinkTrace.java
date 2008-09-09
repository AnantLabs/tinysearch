package org.cc.hello;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
//import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TitleTag;
//import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.NodeList;
/**
 * ���������ĵ��е����г�����
 * @author Winter Lau
 */
public class HyperLinkTrace {
	public static void main(String[] args) throws Exception {
		//��ʼ��HTMLParser
		Parser parser = new Parser();
		parser.setEncoding("8859_1");
		parser.setInputHTML(getWmlContent());
		
		//ע���µĽ�������
//		PrototypicalNodeFactory factory = new PrototypicalNodeFactory ();
//		factory.registerTag(new WmlGoTag ());
//		parser.setNodeFactory(factory);
		
		//�����������������нڵ�
//		NodeList nlist2 = parser.extractAllNodesThatMatch(titleFilter);
//		for(int i=0; i<nlist2.size(); i++){
//			CompositeTag node2 = (CompositeTag)nlist2.elementAt(i);
//			if(node2 instanceof TitleTag){
//				TitleTag title = (TitleTag)node2;
//				System.out.println("title: \t" + title.getTitle());
//			}
//		}
		
		NodeList nlist = parser.extractAllNodesThatMatch(allFilter);
		for(int i=0;i<nlist.size();i++){
			CompositeTag node = (CompositeTag)nlist.elementAt(i);
			if(node instanceof LinkTag){
				LinkTag link = (LinkTag)node;
				System.out.println("LINK: \t" + link.getLink() + "\t" + link.getLinkText());
			}
			if(node instanceof TitleTag){
				TitleTag title = (TitleTag)node;
				System.out.println("title: \t" + title.getTitle());
			}
//			else if(node instanceof WmlGoTag){
//				WmlGoTag go = (WmlGoTag)node;
//				System.out.println("GO: \t" + go.getLink() + "\t");
//			}
		}
	}
	/**
	 * ��ȡ���Ե�WML�ű�����
	 * @return
	 * @throws Exception
	 */
	static String getWmlContent() throws Exception{
		String wml1 = "F:/DataDir/1/6.html";
		//String wml2 = "F:/6.wml";
		File f = new File(wml1);
		BufferedReader in = new BufferedReader(new FileReader(f));
		StringBuffer wml = new StringBuffer();
		do{
			String line = in.readLine();
			if(line==null)
				break;
			if(wml.length()>0)
				wml.append("\r\n");
			wml.append(line);			
		}while(true);
		return wml.toString();		
	}
	
	/**
	 * ������title������link
	 */
	static NodeFilter allFilter = new NodeFilter(){
		public boolean accept(Node node){
			if (node instanceof LinkTag)
				return true;
			if(node instanceof TitleTag)
				return true;
			return false;
		}
	};
	/**
	 * ���������е����ӣ�������Ϊ<a>��<go>
	 */
	static NodeFilter lnkFilter = new NodeFilter() {
		public boolean accept(Node node) {
//			if(node instanceof WmlGoTag)
//				return true;
			if(node instanceof LinkTag)
				return true;
			return false;
		}
	};
	/**
	 * ����������
	 */
	static NodeFilter titleFilter = new NodeFilter() {
		public boolean accept(Node node) {
			if(node instanceof TitleTag)
				return true;
			return false;
		}
	};
	
	/**
	 * WML�ĵ���GO��ǩ������
	 * @author Winter Lau
	 */
//	static class WmlGoTag extends CompositeTag {
//	    private static final String[] mIds = new String[] {"GO"};
//	    private static final String[] mEndTagEnders = new String[] {"ANCHOR"};
//	    public String[] getIds (){
//	        return (mIds);
//	    }
//	    public String[] getEnders (){
//	        return (mIds);
//	    }
//	    public String[] getEndTagEnders (){
//	        return (mEndTagEnders);
//	    }
//	    
//	    public String getLink(){
//	    	return super.getAttribute("href");
//	    }
//	    
//	    public String getMethod(){
//	    	return super.getAttribute("method");
//	    }
//	}
}
