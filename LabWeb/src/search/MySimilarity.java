package search;

import org.apache.lucene.search.DefaultSimilarity;

/**
 * 
 * @author cc512 重载评分公式类可修改该公式
 */
public class MySimilarity extends DefaultSimilarity {
	/**
	 * @override 修改lengthNorm公式
	 * @see org.apache.lucene.search.DefaultSimilarity#lengthNorm(java.lang.String,
	 *      int)
	 */
	public float lengthNorm(String fieldName, int numTerms) {
		// return (float) (1.0 / numTerms);
		return (float) (numTerms);
	}

	/**
	 * @override 修改tf公式
	 * @see org.apache.lucene.search.DefaultSimilarity#tf(float)
	 */
	public float tf(float freq) {
		return (float) freq;
	}

}
