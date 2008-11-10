package search;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.function.CustomScoreQuery;
import org.apache.lucene.search.function.ValueSourceQuery;

/**
 * ���PageRank��Proximity2�ߵĵ÷ֲ�ѯ
 * 
 * @author cc512
 * 
 */
public class TotalCustomScoreQuery extends CustomScoreQuery {

	TotalCustomScoreQuery(Query query, ValueSourceQuery valueSourceQuery) {
		super(query, valueSourceQuery);
		setStrict(true);
	}

	TotalCustomScoreQuery(Query query, ValueSourceQuery[] valueSrcQueries) {
		super(query, valueSrcQueries);
		setStrict(true);
	}

	/**
	 * @Override
	 * @param doc
	 *            �ĵ����
	 * @param subQueryScore
	 *            Ԥ��ѯ�÷�
	 * @param valSrcScore
	 *            ����ֵ��ѯ�÷�
	 * @return
	 */
	public float customScore(final int doc, final float subQueryScore,
			final float valSrcScore) {
		final float totalScore;
		totalScore = calculateScore(subQueryScore, valSrcScore);
		return totalScore;
	}

	/**
	 * @Override
	 * @param doc
	 * @param subQueryScore
	 * @param valSrcScores[]
	 * @return
	 */
	public float customScore(int doc, float subQueryScore, float valSrcScores[]) {
		if (valSrcScores.length == 1) {
			return customScore(doc, subQueryScore, valSrcScores[0]);
		}
		if (valSrcScores.length == 0) {
			return customScore(doc, subQueryScore, 1);
		}
		float score = subQueryScore;
		// System.out.println("scorePre: " + score);
		for (int i = 0; i < valSrcScores.length; i++) {
			score += ((i == 0) ? 50 * valSrcScores[i] : 100 * valSrcScores[i]);
			// score += valSrcScores[i];
			// System.out.println(valSrcScores[i]);
		}
		// System.out.println("score: " + score);
		return score;
	}

	/**
	 * �������
	 * 
	 * @param subQueryScore
	 * @param valSrcScore
	 * @return
	 */
	private float calculateScore(final float subQueryScore,
			final float valSrcScore) {
		return (float) (0.5 * subQueryScore + 5.0 * valSrcScore);
	}

	public String name() {
		return "shortVal + score";
	}
}
