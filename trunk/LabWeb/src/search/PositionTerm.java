package search;

/**
 * 一篇doc中term的名称，位置，频率等信息类
 * 
 * @author cc512
 * 
 */
public class PositionTerm {
	private final String term;
	private final int position;
	private final int freq;

	public PositionTerm(int position, String term, int freq) {
		this.position = position;
		this.term = term;
		this.freq = freq;
	}

	public String getTerm() {
		return this.term;
	}

	public int getPosition() {
		return this.position;
	}

	public int getFreq() {
		return this.freq;
	}
}
