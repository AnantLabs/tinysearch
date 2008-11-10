package org.apache.lucene.search;

public class MySimilarity extends DefaultSimilarity {

	public float lengthNorm(String fieldName, int numTerms) {
		return (float) (1.0 / numTerms);
	}

	public float tf(float freq) {
		return (float) freq;
	}

}
