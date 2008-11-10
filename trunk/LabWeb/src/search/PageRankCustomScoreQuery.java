package search;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.function.CustomScoreQuery;
import org.apache.lucene.search.function.ValueSourceQuery;

/**
 * 
 * 2-create a customScoreQuery on top of the original query and the
 * valueSourceQuery.
 * PageRank�÷ּ�����㹫ʽ��,QueryΪ�ؼ��ʲ�ѯ,ValueSourceQueryΪPRֵ��ѯ�����ߵ÷����Ϊ���յ÷�
 * 
 * @author cc512
 */
class PageRankCustomScoreQuery extends CustomScoreQuery {

	PageRankCustomScoreQuery(final Query query,
			final ValueSourceQuery valueSourceQuery) {
		super(query, valueSourceQuery);
		setStrict(true);
	}

	/**
	 * @Override
	 * @param doc
	 *            �ĵ����
	 * @param subQueryScore
	 *            Ԥ��ѯ�÷�
	 * @param valSrcScore
	 *            PRֵ��ѯ�÷�
	 */
	public float customScore(final int doc, final float subQueryScore,
			final float valSrcScore) {
		final float totalScore;
		totalScore = calculateScore(subQueryScore, valSrcScore);
		return totalScore;
	}

	/**
	 * 
	 * @param subQueryScore
	 * @param valSrcScore
	 * @return
	 */
	private float calculateScore(final float subQueryScore,
			final float valSrcScore) {
		return (subQueryScore + 100 * valSrcScore);
	}

	/**
	 * @author cc512
	 * @return String
	 */
	public String name() {
		return "shortVal + score";
	}

}
