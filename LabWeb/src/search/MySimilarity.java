package search;

import org.apache.lucene.search.DefaultSimilarity;

/**
 * 
 * @author cc512 �������ֹ�ʽ����޸ĸù�ʽ
 */
public class MySimilarity extends DefaultSimilarity {
	/**
	 * @override �޸�lengthNorm��ʽ
	 * @see org.apache.lucene.search.DefaultSimilarity#lengthNorm(java.lang.String,
	 *      int)
	 */
	public float lengthNorm(String fieldName, int numTerms) {
		// return (float) (1.0 / numTerms);
		return (float) (numTerms);
	}

	/**
	 * @override �޸�tf��ʽ
	 * @see org.apache.lucene.search.DefaultSimilarity#tf(float)
	 */
	public float tf(float freq) {
		return (float) freq;
	}

}
