package search;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.function.DocValues;
import org.apache.lucene.search.function.ValueSource;
import org.mira.lucene.analysis.IK_CAnalyzer;
import org.mira.lucene.analysis.MIK_CAnalyzer;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

/**
 * ProximityValueSource�γ�proximity�ϵĵ÷�
 * 
 * @author cc512
 * 
 */
public class ProximityValueSource extends ValueSource {
	private final String query;
	private final String fieldStr;

	// Constrct function
	public ProximityValueSource(String query, String fieldStr) {
		this.query = query;
		this.fieldStr = fieldStr;
	}

	private HashMap<Integer, Float> computer(IndexReader reader, String query)
			throws IOException {
		HashMap<Integer, Float> docScore = new HashMap<Integer, Float>();

		Analyzer analyzer = new PaodingAnalyzer();
		// Analyzer analyzer = new MMAnalyzer();
		// Analyzer analyzer = new MIK_CAnalyzer();
		// Analyzer analyzer = new IK_CAnalyzer();

		TokenStream stream = analyzer
				.tokenStream(null, new StringReader(query));

		ArrayList<Token> tokenList = new ArrayList<Token>();

		// documentqueryΪ������query��N��termȫ����ص�document����ŵļ���
		HashMap<Integer, ArrayList<PositionTerm>> documentquery = new HashMap<Integer, ArrayList<PositionTerm>>();

		while (true) {
			Token token = stream.next();
			if (token == null)
				break;
			tokenList.add(token);
		}
		stream = null;

		// tokensΪquery��term����
		Token[] tokens = (Token[]) tokenList.toArray(new Token[0]);

		tokenList = null;

		// tokenslengthΪquery��term���鳤��
		int tokenslength = tokens.length;

		for (int i = 0; i < tokenslength; i++) {
			Token token = tokens[i];
			Term term = new Term(fieldStr, token.termText());
			TermPositions termPositions = reader.termPositions(term);
			while (termPositions.next()) {
				// positionterm1Ϊһƪdoc��������query��ص�termλ�ü���
				ArrayList<PositionTerm> positionterm1 = new ArrayList<PositionTerm>();

				for (int k = 0; k < termPositions.freq(); k++) {
					int pos = termPositions.nextPosition();
					positionterm1.add(new PositionTerm(pos, token.termText(),
							termPositions.freq()));
				}
				if (documentquery.containsKey(termPositions.doc())) {
					ArrayList<PositionTerm> tempPosTerm;
					tempPosTerm = twoArrayAdd(documentquery
							.remove(termPositions.doc()), positionterm1);
					documentquery.put(termPositions.doc(), tempPosTerm);
					tempPosTerm = null;
				} else {
					documentquery.put(termPositions.doc(), positionterm1);
				}
				positionterm1 = null;
			}
		}

		for (Entry<Integer, ArrayList<PositionTerm>> entry : documentquery
				.entrySet()) {

			// positiontermΪһƪdoc��������query��ص�termλ�ü���
			ArrayList<PositionTerm> positionterm = (ArrayList<PositionTerm>) entry
					.getValue();

			int key = (Integer) entry.getKey();
			// positionterm = (ArrayList<PositionTerm>)entry.getValue();

			/*
			 * positionterm2Ϊһ��document��������query term��ص�positon��Ϣ���飬�������Լ����query
			 * term��doc�еľ���
			 */
			PositionTerm[] positionterm2 = (PositionTerm[]) positionterm
					.toArray(new PositionTerm[0]);

			positionterm = null;

			PositionTerm prePT = null;
			int mindist = 1;
			int pair = 0;
			int length = positionterm2.length;
			for (int jk = 0; jk < length; jk++) {
				PositionTerm pt = positionterm2[jk];
				if (prePT == null) {
					prePT = pt;
				} else {
					if (!pt.getTerm().equals(prePT.getTerm())) {
						int dist = pt.getPosition() - prePT.getPosition();
						pair++;
						if (pair == 1) {
							mindist = dist;
						}
						if (pair >= 2) {
							mindist = mindist < dist ? mindist : dist;
						}
					}
					prePT = pt;
				}
			}
			positionterm2 = null;
			prePT = null;
			// System.out.println("query��term��Document[" + key +
			// "]��term��������С����Ϊ" + (mindist - 1));
			float score;
			score = (mindist > 0 && mindist == 1) ? (float) mindist
					: ((float) 1 / mindist);
			docScore.put(key, score);
		}
		return docScore;
	}

	/**
	 * @Override
	 */
	public DocValues getValues(IndexReader reader) throws IOException {
		return getValues(reader, query);
	}

	public DocValues getValues(IndexReader reader, String query)
			throws IOException {
		final HashMap<Integer, Float> arr = computer(reader, query);
		// �������򷵻�0
		return new DocValues() {
			public float floatVal(int doc) {
				return (arr.containsKey(doc)) ? (float) arr.get(doc) : 0.0f;
			}

			public String toString(int doc) {
				return description();
			}
		};
	}

	public String description() {
		return "Proximity";
	}

	public final boolean equals(Object o) {
		return (o instanceof ProximityValueSource);
	}

	public int hashCode() {
		return Float.class.hashCode();
	}

	/**
	 * ����ArrayList�İ�positon��С����ϲ�����
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	private ArrayList<PositionTerm> twoArrayAdd(ArrayList<PositionTerm> list1,
			ArrayList<PositionTerm> list2) {
		if ((list1 == null) || (list2 == null))
			return null;
		int size = list1.size() + list2.size();
		ArrayList<PositionTerm> list = new ArrayList<PositionTerm>(size);
		int i = 0, j = 0;
		while (true) {
			if (i >= list1.size() || j >= list2.size())
				break;
			int p1 = list1.get(i).getPosition();
			int p2 = list2.get(j).getPosition();
			if (p1 < p2) {
				list.add(list1.get(i));
				i++;
			} else {
				list.add(list2.get(j));
				j++;
			}
		}
		if (i < list1.size()) {
			for (int k = i; k < list1.size(); k++) {
				list.add(list1.get(k));
			}
		}
		if (j < list2.size()) {
			for (int k = j; k < list2.size(); k++) {
				list.add(list2.get(k));
			}
		}
		return list;
	}

}
