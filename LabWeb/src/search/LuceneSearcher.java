package search;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.function.CustomScoreQuery;
import org.apache.lucene.search.function.FieldScoreQuery;
import org.apache.lucene.search.function.ValueSourceQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.mira.lucene.analysis.IK_CAnalyzer;
import org.mira.lucene.analysis.MIK_CAnalyzer;

/**
 * search�ľ���ʵ����
 */
public class LuceneSearcher extends AbstractSearcher {

	public static String PATH = "";

	private static AbstractSearcher instance;
	private int totalCount, totalPage;
	private String searchword;
	private IndexReader reader;
	private IndexSearcher searcher;
	private Analyzer analyzer, maxLengthAnalyzer;
	// highlight
	private String[] fields;
	private BooleanClause.Occur[] flags;
	private SimpleHTMLFormatter format;

	/**
	 * ���캯��
	 * 
	 * @throws Exception
	 */
	public LuceneSearcher() throws Exception {
		reader = IndexReader.open(PATH);
		searcher = new IndexSearcher(reader);

		analyzer = new PaodingAnalyzer();
		// analyzer = new MMAnalyzer();
		// analyzer = new IK_CAnalyzer();
		maxLengthAnalyzer = new MIK_CAnalyzer(); // ������󳤶�ƥ��ִ�

		fields = new String[] { "CONTENT", "TITLE", "AnchorText", "META" };
		// fields = new String[] {"CONTENT", "TITLE", "AnchorText"};
		flags = new BooleanClause.Occur[] { BooleanClause.Occur.SHOULD,
				BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD,
				BooleanClause.Occur.SHOULD };
		// flags = new BooleanClause.Occur[] { BooleanClause.Occur.SHOULD,
		// BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};
		format = new SimpleHTMLFormatter(START_HIGHLIGHT, END_HIGHLIGHT);
	}

	/**
	 * @return instance
	 * @throws Exception
	 */
	public static AbstractSearcher getInstance() throws Exception {
		if (instance == null) {
			instance = new LuceneSearcher();
		}
		return instance;
	}

	/**
	 * @return null
	 */
	public void close() throws Exception {
		reader.close();
		searcher.close();
	}

	/**
	 * �������γ�ժҪ
	 * 
	 * @param query
	 * @param docId
	 * @param text
	 * @return String
	 * @throws IOException
	 */
	public String getSnippet(Query query, int docId, String text)
			throws IOException {
		QueryScorer scorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(format, scorer);
		highlighter.setTextFragmenter(new SimpleFragmenter(30));
		TokenStream tokenStream;
		String snippet = "";
		TermPositionVector tpv = (TermPositionVector) reader.getTermFreqVector(
				docId, "CONTENT");
		if (tpv != null) {
			tokenStream = TokenSources.getTokenStream(tpv);
			snippet = highlighter.getBestFragments(tokenStream, text, 3, "...");
		}
		return snippet;
	}

	/**
	 * ִ�в�ѯ���̣�����˷�ҳ����
	 * 
	 * @see search.AbstractSearcher#search(java.lang.String, int, int)
	 */
	public List<QueryResult> search(String queryStr, int currentPage,
			int pageSize) throws Exception {
		this.searchword = queryStr;

		BooleanQuery bq = new BooleanQuery();

		TokenStream stream = maxLengthAnalyzer.tokenStream(null,
				new StringReader(queryStr));
		ArrayList<Token> tokenList = new ArrayList<Token>();
		while (true) {
			Token token = stream.next();
			if (token == null)
				break;
			tokenList.add(token);
		}
		Token[] tokens = (Token[]) tokenList.toArray(new Token[0]); // tokensΪquery��term����
		int tokenslength = tokens.length; // tokenslengthΪquery��term���鳤��
		int j;
		for (int i = 0; i < tokenslength; i++) {
			j = ((i + 1) < (tokenslength - 1)) ? (i + 1) : i;
			Token token = tokens[i];
			Query q = MultiFieldQueryParser.parse(token.termText(), fields,
					flags, analyzer);
			bq.add(q, BooleanClause.Occur.SHOULD);
			if (j > i) {
				q = MultiFieldQueryParser.parse(tokens[i].termText()
						+ tokens[j].termText(), fields, flags, analyzer);
				bq.add(q, BooleanClause.Occur.SHOULD);
			}
		}
		Query query = MultiFieldQueryParser.parse(queryStr, fields, flags,
				analyzer);
		bq.add(query, BooleanClause.Occur.SHOULD);
		System.out.println(bq.toString());

		// ���PRֵ��ѯ
		FieldScoreQuery qf = new FieldScoreQuery("PR",
				FieldScoreQuery.Type.FLOAT);

		// ���Proximity
		// ProximityValueSourceQuery proximity = new
		// ProximityValueSourceQuery(queryStr, "CONTENT");
		// System.out.println(proximity.toString());

		// ValueSourceQuery[] vq = new ValueSourceQuery[]{qf, proximity};
		// System.out.println(vq.toString());

		// CustomScoreQuery customQ = new PageRankCustomScoreQuery(query, qf);
		// CustomScoreQuery customQ = new PageRankCustomScoreQuery(query,
		// proximity);

		// CustomScoreQuery customQ = new TotalCustomScoreQuery(query, qf);
		// CustomScoreQuery customQ = new TotalCustomScoreQuery(query,
		// proximity);
		// CustomScoreQuery customQ = new TotalCustomScoreQuery(query, vq);

		CustomScoreQuery customQ = new TotalCustomScoreQuery(bq, qf);

		// Hits hits = searcher.search(query);
		Hits hits = searcher.search(customQ);

		this.totalCount = (hits == null) ? 0 : hits.length();

		// ���ҳ����(ע������)
		totalPage = totalCount / pageSize + (totalCount % pageSize > 0 ? 1 : 0);
		// ��ǰҳ,��Ҫ��ʾ��ҳ��� (��ҳ��Ŵ���ҳ����ʱ���������һҳ��)
		currentPage = Math.min(currentPage, totalPage);
		// ��ʼ��¼��� (��ʼ��ű�����ڵ�����)
		int startPos = Math.max((currentPage - 1) * pageSize, 0);
		// ��β��¼��� (���ܴ��ڼ�¼����)
		int endPos = Math.min(currentPage * pageSize - 1, totalCount - 1);
		endPos = (endPos >= totalCount) ? totalCount - 1 : endPos;

		List<QueryResult> results = new ArrayList<QueryResult>();
		for (int i = startPos; i <= endPos; i++) {
			Document doc = hits.doc(i);
			String id = doc.get("DOCID");
			String text = doc.get("CONTENT");
			String title = doc.get("TITLE");
			String url = doc.get("URL");
			/* prepare for snippet */
			// QueryResult result = new QueryResult(id, getSnippet(query, hits
			// .id(i), text), title, url);
			QueryResult result = new QueryResult(id, getSnippet(customQ, hits
					.id(i), text), title, url);
			results.add(result);
		}
		return results;
	}

	/**
	 * ԭ����ִ�в�ѯ����(non-Javadoc)
	 * 
	 * @see search.AbstractSearcher#search(java.lang.String)
	 */
	public List<QueryResult> search(String queryStr) throws Exception {
		Query query = MultiFieldQueryParser.parse(queryStr, fields, flags,
				analyzer);
		TopDocs topDocs = searcher.search(query.weight(searcher), null, TOP_N);
		ScoreDoc[] sDocs = topDocs.scoreDocs;
		List<QueryResult> results = new ArrayList<QueryResult>();
		for (int i = 0; i < sDocs.length; ++i) {
			int docId = sDocs[i].doc;
			Document doc = searcher.doc(docId);
			String id = doc.getField("DOCID").stringValue();
			String text = doc.getField("CONTENT").stringValue();
			String title = doc.getField("TITLE").stringValue();
			String url = doc.getField("URL").stringValue();
			/* prepare for snippet */
			QueryResult result = new QueryResult(id, getSnippet(query, docId,
					text), title, url);
			results.add(result);
		}
		return results;
	}

	/**
	 * �õ������������
	 * 
	 * @return �����������
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * ��ȡ��ҳ����.
	 * 
	 * @param pageSize
	 *            һҳ��ʾ������
	 * @return ҳ������
	 */
	public int getTotalPage(int pageSize) {
		return totalPage;
	}

	/**
	 * ��ʾ��ҳ����.
	 * 
	 * @param currentPage
	 *            ��ǰҳ��, �� 1 ��ʼ
	 * @param pageSize
	 *            ÿҳ��ʾ������
	 * @return ��ҳ����
	 */
	public String findPagedAll(int currentPage, int pageSize) {

		int num_left, num_right, page_left, page_right;
		String NexURL = "", PreURL = "", FirURL = "", EndURL = "", CurURL = "", MidURL = "";

		int startPos = Math.max((currentPage - 1) * pageSize, 0);
		// int endPos = Math.min(currentPage * pageSize - 1, totalCount - 1);
		String PageURL = "<a href='search?query=" + searchword + "&start=";

		// ��һҳ��ʾ�ж�
		if (currentPage > 1) {
			PreURL = PageURL + (startPos - pageSize)
					+ "'><font size=3>��һҳ</font></a>&nbsp;";
		}

		if (totalPage > pageSize) {
			num_left = currentPage - 1;
			num_right = totalPage - currentPage;
			if (num_left > (pageSize / 2)) {
				if (num_right < (pageSize / 2)) {
					page_left = currentPage - (pageSize - num_right) + 1;
					page_right = totalPage;
				} else {
					page_left = currentPage - (pageSize / 2);
					page_right = currentPage + (pageSize / 2);
				}
			} else {
				page_left = 1;
				page_right = currentPage + (pageSize - num_left) - 1;
			}

			// ǰ�����
			for (int p = page_left; p <= (currentPage - 1); p++) {
				FirURL = FirURL + PageURL + (p - 1) * pageSize + "'>[" + p
						+ "]</a>&nbsp;";
			}

			// ��ǰ�����
			CurURL = "&nbsp;<b><FONT color=red>[" + currentPage
					+ "]</font></b>&nbsp;";

			// �������
			for (int f = (currentPage + 1); f <= page_right; f++) {
				EndURL = EndURL + PageURL + (f - 1) * pageSize + "'>[" + f
						+ "]</a>&nbsp;";
			}
			MidURL = FirURL + CurURL + EndURL;
		} else {
			for (int i = 1; i <= totalPage; i++) {
				if (i == currentPage) {
					MidURL = MidURL + "&nbsp;<b><FONT color=red>[" + i
							+ "]</FONT></b>&nbsp;";
				} else {
					MidURL = MidURL + PageURL + (i - 1) * pageSize + "'>[" + i
							+ "]</a>&nbsp;";
				}
			}
		}
		// ��һҳ�ж�
		if (currentPage < totalPage) {
			NexURL = PageURL + (startPos + pageSize)
					+ "'><font size=3>��һҳ</font></a>&nbsp;";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<table><tr><td align=left>");
		sb.append(PreURL);
		sb.append(MidURL);
		sb.append(NexURL);
		sb.append("</td></tr></table>");
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		LuceneSearcher.PATH = "F:/index";
		debug(LuceneSearcher.getInstance());
	}

}
