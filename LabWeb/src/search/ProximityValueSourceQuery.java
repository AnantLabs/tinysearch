package search;

import org.apache.lucene.search.function.ValueSource;
import org.apache.lucene.search.function.ValueSourceQuery;

/**
 * ��������ProximityValueSource�ϵ�Query
 * 
 * @author cc512
 * 
 */
public class ProximityValueSourceQuery extends ValueSourceQuery {

	/**
	 * @param query
	 *            ��ѯ��
	 * @param fieldStr
	 *            ��Ҫ����proximity����
	 */
	public ProximityValueSourceQuery(String query, String fieldStr) {
		super(getValueSource(query, fieldStr));
	}

	/**
	 * 
	 * @param query
	 *            ��ѯ��
	 * @param fieldStr
	 *            ��Ҫ����proximity�Ķ�Ӧ��
	 * @return
	 */
	private static ValueSource getValueSource(String query, String fieldStr) {
		return new ProximityValueSource(query, fieldStr);
	}

	// public ProximityValueSourceQuery(String query, IndexReader reader) {
	// super(getValueSource(query, reader));
	// }
	//
	// private static ValueSource getValueSource(String query, IndexReader
	// reader) {
	// return new ProximityValueSource(query);
	// }
	//	  
	// public ProximityValueSourceQuery(String query) {
	// super (getValueSource(query));
	// }
	//	  
	// private static ValueSource getValueSource(String query) {
	// return new ProximityValueSource(query);
	// }

}
