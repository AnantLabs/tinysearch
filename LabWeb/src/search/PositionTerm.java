package search;

/**
 * һƪdoc��term�����ƣ�λ�ã�Ƶ�ʵ���Ϣ��
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
