/**
 * 
 */
package org.mira.lucene.analysis.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;

/**
 * linliangyi @ team of miracle
 * 2007-1-22 | 下午03:06:42
 * org.mira.lucene.analysis.dict.Dictionary in Project:IK2 V2.0
 * 
 * 字典管理类,单子模式
 */
public class Dictionary {

    
    //初始形态
    public static final int  BASECHARTYPE_NULL = 0x00000000;
    //英文
    public static final int  BASECHARTYPE_LETTER = 0x00010000;
    //数字
    public static final int  BASECHARTYPE_NUMBER = 0x00100000;
    //CHNNumber中文数词,如:十,万,億
    public static final int NUMBER_CHN = 0x00000001;
    //除阿拉伯数字外的数字，如：一，壹，Ⅰ，I
    public static final int NUMBER_OTHER = 0x00000002;
    //CJK_UNICODE
    public static final int  BASECHARTYPE_CJK = 0x01000000;  
    //OTHER
    public static final int  BASECHARTYPE_OTHER = 0x10000000;
    //连接符，如：－　＿　＆　＠
    public static final int OTHER_CONNECTOR = 0x00000004;
    //数字连接符,如：．／
    public static final int OTHER_NUMSIGN = 0x00000008;
    
    
    
	private static Dictionary singleton = null;
    
	private static String file_Word = "/dict/word/wordbase.dic";
	private static String file_stop_Word = "/dict/stopword/stopword.dic";
    private static String file_suffix_Word = "/dict/suffix/suffix.dic";
	private static String file_local_Word = "/dict/local/local.dic";

	private static String file_OtherDigit = "/dict/other_digit.dic";
	private static String file_CHNNumber = "/dict/c_number.dic";
	private static String file_NbSign = "/dict/number_sign.dic";
	private static String file_Connector = "/dict/connector.dic";
    private static String file_Count = "/dict/count.dic";
    private static String file_noise_char = "/dict/noisechar.dic";

	private HashSet hsNoise = null;
	private HashSet hsStop = null;
	
	private HashSet hsOtherDigit = null;
	private HashSet hsCHNNumber = null;
	private HashSet hsNbSign = null;
	private HashSet hsConnector = null;
	
    //词典Hash树对象
    private DictSegment dictSeg = null;
    
	private Dictionary(){
        dictSeg = new DictSegment();
		initDictionary();
	}
	
    public static Dictionary load(){
        if(singleton == null){
            singleton = new Dictionary();
        }
        return singleton;
    }

    private void initDictionary(){
    	loadNoiseWords();
    	loadStopWords();
    	
    	loadOtherDigit();
    	loadCHNNumber();
    	loadNbSign();
    	loadConnector();

        loadSuffixWords();
        loadCountWords();
    	loadWords();
        loadLocalWords();
    }

    public boolean isNumber(char onechar){
    	return Character.isDigit(onechar) || isOtherDigit(onechar) || isCHNNumber(onechar);
    }
    
	/**
	 * 判断是否是连接符
	 * @return 
	 */
	public boolean isConnector(char onechar){
		return singleton.hsConnector.contains(String.valueOf(onechar));
	}
    /**
	 * 载入连接符
	 */
	private void loadConnector(){
		InputStream is = Dictionary.class.getResourceAsStream(Dictionary.file_Connector);
		this.hsConnector = new HashSet(4);
		String theWord = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "GBK"), 512);
			do {
				theWord = br.readLine();
				if (theWord != null) {
					this.hsConnector.add(theWord.trim());
					/*Test Logging*/
					//System.out.println(theWord);
				}
			} while (theWord != null);
		} catch (IOException ioe) {
			System.err.println("连接符载入异常.");
			ioe.printStackTrace();
		}finally{
			try {
				if(is != null){
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}    
    
	/**
	 * 判断是否是数字连接符
	 * @return 
	 */
	public boolean isNbSign(char onechar){
		return singleton.hsNbSign.contains(String.valueOf(onechar));
	}
	
	/**
	 * 载入数字连接符
	 */
	private void loadNbSign(){
		InputStream is = Dictionary.class.getResourceAsStream(Dictionary.file_NbSign);
		this.hsNbSign = new HashSet(4);
		String theWord = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "GBK"), 512);
			do {
				theWord = br.readLine();
				if (theWord != null) {
					this.hsNbSign.add(theWord.trim());
					/*Test Logging*/
					//System.out.println(theWord);
				}
			} while (theWord != null);
		} catch (IOException ioe) {
			System.err.println("数字连接符载入异常.");
			ioe.printStackTrace();
		}finally{
			try {
				if(is != null){
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 判断是否是中文数词
	 * @return 
	 */
	public boolean isCHNNumber(char onechar){
		return singleton.hsCHNNumber.contains(String.valueOf(onechar));
	}		
	/**
	 * 载入中文数词
	 */
	private void loadCHNNumber(){
		InputStream is = Dictionary.class.getResourceAsStream(Dictionary.file_CHNNumber);
		this.hsCHNNumber = new HashSet(32);
		String theWord = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "GBK"), 512);
			do {
				theWord = br.readLine();
				if (theWord != null) {
					this.hsCHNNumber.add(theWord.trim());
					/*Test Logging*/
					//System.out.println(theWord);
				}
			} while (theWord != null);
		} catch (IOException ioe) {
			System.err.println("中文数词载入异常.");
			ioe.printStackTrace();
		}finally{
			try {
				if(is != null){
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 判断是否是其它类型数字
	 * @return 
	 */
	public boolean isOtherDigit(char onechar){
		return singleton.hsOtherDigit.contains(String.valueOf(onechar));
	}	
	/**
	 * 载入其它数字
	 */
	private void loadOtherDigit(){
		InputStream is = Dictionary.class.getResourceAsStream(Dictionary.file_OtherDigit);
		this.hsOtherDigit = new HashSet(32);
		String theWord = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "GBK"), 512);
			do {
				theWord = br.readLine();
				if (theWord != null) {
					this.hsOtherDigit.add(theWord.trim());
					/*Test Logging*/
					//System.out.println(theWord);
				}
			} while (theWord != null);
		} catch (IOException ioe) {
			System.err.println("其它数字载入异常.");
			ioe.printStackTrace();
		}finally{
			try {
				if(is != null){
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * 载入量词词典
     */
    private void loadCountWords(){
        InputStream is = Dictionary.class.getResourceAsStream(Dictionary.file_Count);
        String theWord = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is , "GBK"), 512);
            do {
                theWord = br.readLine();
                if (theWord != null) {
                    theWord = theWord.trim();
                    this.dictSeg.addWord(theWord.toCharArray() , WordType.WT_COUNT);
                    /*Test Logging*/
                    //System.out.println(theWord);
                }
            } while (theWord != null);
        } catch (IOException ioe) {
            System.err.println("其它数字载入异常.");
            ioe.printStackTrace();
        }finally{
            try {
                if(is != null){
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
	/**
	 * 载入后缀词典
	 */
	private void loadSuffixWords(){
        InputStream is = Dictionary.class.getResourceAsStream(Dictionary.file_suffix_Word); 
		try {
			
			String theWord = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "GBK"), 512);
			do {
				theWord = br.readLine();
				if (theWord != null) {
					theWord = theWord.trim();
                    dictSeg.addWord(theWord.toCharArray() , WordType.WT_SUFFIX);
					/*Test Logging*/
					//System.out.println(theWord);
				}
			} while (theWord != null);
		} catch (IOException ioe) {
			System.err.println("后缀词库载入异常.");
			ioe.printStackTrace();
		}finally{
			try {
				if(is != null){
                    is.close();
                    is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    
    /**
     * 判断是否是无用词(包括停止词和噪声词)
     * @return
     */
    public boolean isUselessWord(String word){
        return isStopWord(word) || isNoiseWord(word);
    }
    
	
	/**
	 * 判断是否是停止词
	 * @return 
	 */
	public boolean isStopWord(String word){
		return singleton.hsStop.contains(word);
	}	
	/**
	 * 载入停止词典
	 */
	private void loadStopWords(){
        InputStream is = Dictionary.class.getResourceAsStream(Dictionary.file_stop_Word);
		this.hsStop = new HashSet(64);
		try {
			String theWord = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "GBK"), 512);
			do {
				theWord = br.readLine();
				if (theWord != null) {
					theWord = theWord.trim();
					this.hsStop.add(theWord);
					/*Test Logging*/
					//System.out.println(theWord);
				}
			} while (theWord != null);
		} catch (IOException ioe) {
			System.err.println("停止词库载入异常.");
			ioe.printStackTrace();
		}finally{
			try {
				if(is != null){
                    is.close();
                    is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 判断是否是噪声词
	 * @return 
	 */
	public boolean isNoiseWord(String word){
		return singleton.hsNoise.contains(word);
	}
	
	/**
	 * 载入噪声词典
	 */
	private void loadNoiseWords(){
        InputStream is = Dictionary.class.getResourceAsStream(Dictionary.file_noise_char);
        this.hsNoise = new HashSet(64); 
		try {
			String theWord = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "GBK"), 512);
			do {
				theWord = br.readLine();
				if (theWord != null) {
					theWord = theWord.trim();
					this.hsNoise.add(theWord);
					/*Test Logging*/
					//System.out.println(theWord);
				}
			} while (theWord != null);
		} catch (IOException ioe) {
			System.err.println("噪声词库载入异常.");
			ioe.printStackTrace();
		}finally{
			try {
				if(is != null){
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    public Hit search(char[] seg){
        return singleton.dictSeg.search(seg , 0 , seg.length - 1);
    }
    
    public Hit search(char[] seg , int beginIndex , int endIndex){
        return singleton.dictSeg.search(seg , beginIndex , endIndex);
    }
    
    /**
     * 载入外部词典
     * @param wordList
     */
    public static void loadExtendWords(List wordList){
    	String theWord = null;
    	for (int i = 0; i < wordList.size(); i++) {
    		theWord = (String)wordList.get(i);
    		load().dictSeg.addWord(theWord.toCharArray() , WordType.WT_NORMWORD);
    	}
    }
    
    /**
     * 载入外部词典
     * @param wordList
     */
    public static void loadExtendWords(String theWord){
    	load().dictSeg.addWord(theWord.toCharArray() , WordType.WT_NORMWORD);
    }
    
	/**
	 * 载入词典
	 */
	private void loadWords(){
        InputStream is = Dictionary.class.getResourceAsStream(Dictionary.file_Word);
		try {
			
			String theWord = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "GBK"), 512);
			do {
				theWord = br.readLine();
				if (theWord != null) {
					theWord = theWord.trim();
                    this.dictSeg.addWord(theWord.toCharArray() , WordType.WT_NORMWORD);
                    /*Test Logging*/
                    //System.out.println(theWord);
				}
			} while (theWord != null);
		} catch (IOException ioe) {
			System.err.println("主词典库载入异常.");
			ioe.printStackTrace();
		}finally{
			try {
				if(is != null){
                    is.close();
                    is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    /**
     * 载入词典
     */
    private void loadLocalWords(){
        InputStream is = Dictionary.class.getResourceAsStream(Dictionary.file_local_Word);
        try {
            
            String theWord = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is , "GBK"), 512);
            do {
                theWord = br.readLine();
                if (theWord != null) {
                    theWord = theWord.trim();
                    this.dictSeg.addWord(theWord.toCharArray() , WordType.WT_NORMWORD);
                    /*Test Logging*/
                    //System.out.println(theWord);
                }
            } while (theWord != null);
        } catch (IOException ioe) {
            System.err.println("本地词典库载入异常.");
            ioe.printStackTrace();
        }finally{
            try {
                if(is != null){
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 判断字符类型
     * 字符分为:英文字母\数字\CJK(汉字)\符号(拉丁字集)
     * @param theOne
     * @return
     */
    public int identify(char onechar){
        int charType = Dictionary.BASECHARTYPE_NULL;
        
        if((onechar >= 'a' && onechar <= 'z') || (onechar >= 'A' && onechar <= 'Z')){
            charType = charType | Dictionary.BASECHARTYPE_LETTER;
        }
        
        if(this.isNumber(onechar)){
            charType = charType | Dictionary.BASECHARTYPE_NUMBER;
            if(this.isCHNNumber(onechar)){
            	charType = charType | Dictionary.NUMBER_CHN;
            }
            if(this.isOtherDigit(onechar)){
            	charType = charType | Dictionary.NUMBER_OTHER;
            }
        }
       
        if(Character.UnicodeBlock.of(onechar) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS){
            charType = charType | Dictionary.BASECHARTYPE_CJK;
        }
       
        if(charType == Dictionary.BASECHARTYPE_NULL){
            charType = charType | Dictionary.BASECHARTYPE_OTHER;
			if(this.isNbSign(onechar)){
				charType = charType | Dictionary.OTHER_NUMSIGN;
			}
			if(this.isConnector(onechar)){
				charType = charType | Dictionary.OTHER_CONNECTOR;
			}
        }
        return charType;
    }
    	
    public static void main(String[] args){
        Dictionary dict = Dictionary.load();
        Hit hit = dict.search("分".toCharArray());
        System.out.println("Match : "+hit.isMatch());
        System.out.println("PrefixMatch : "+hit.isPrefixMatch());
        System.out.println("MatchAndContinue : "+hit.isMatchAndContinue());
        System.out.println("Unmatch : "+hit.isUnmatch());
        System.out.println("-----------------");
        if(hit.isMatch()){
            System.out.println("NormWord : "+hit.getWordType().isNormWord());
            System.out.println("Suffix : "+hit.getWordType().isSuffix());
            System.out.println("Count : "+hit.getWordType().isCount());
        }
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    	//HALFWIDTH_AND_FULLWIDTH_FORMS
//    	System.out.println(Character.UnicodeBlock.of('０')); 
//    	System.out.println(Character.UnicodeBlock.of('Ｏ')); 
//    	System.out.println(Character.UnicodeBlock.of('＠')); 
//    	System.out.println(Character.UnicodeBlock.of('．'));
//    	System.out.println(Character.UnicodeBlock.of('／'));
//    	
//    	//CJK_UNIFIED_IDEOGRAPHS
//    	System.out.println(Character.UnicodeBlock.of('億')); 
//    	System.out.println(Character.UnicodeBlock.of('玖'));
//
//    	//BASIC_LATIN
//    	System.out.println(Character.UnicodeBlock.of('@')); 
//    	
//    	//NUMBER_FORMS
//    	System.out.println(Character.UnicodeBlock.of('Ⅺ'));
//    	
//    	System.out.println(Character.UnicodeBlock.of('。'));
//        Dictionary dict = Dictionary.load();
//        System.out.println(dict.identify('。') == Dictionary.BASECHARTYPE_OTHER);
    }
	
}
