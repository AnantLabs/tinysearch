package search;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.function.CustomScoreQuery;
import org.apache.lucene.search.function.ValueSourceQuery;

/**
 * @author cc512 2-create a customScoreQuery on top of the original query and
 *         the valueSourceQuery.
 *         PageRank得分加入计算公式类,Query为关键词查询,ValueSourceQuery为PR值查询，两者得分相加为最终得分
 */
class PageRankCustomScoreQuery extends CustomScoreQuery {
	PageRankCustomScoreQuery(final Query query,
			final ValueSourceQuery valueSourceQuery) {
		super(query, valueSourceQuery);
		setStrict(true);
	}

	// @Override
	/***************************************************************************
	 * @param doc为文档编号,
	 *            subQueryScore为预查询得分, valSrcScore为PR值查询得分
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
		// System.out.println(subQueryScore);
		// System.out.println(valSrcScore);
		// System.out.println(subQueryScore + valSrcScore);
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
