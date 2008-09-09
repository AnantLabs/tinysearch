package search;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public abstract class AbstractSearcher {
	public static final int TOP_N = 20;
	public static final String START_HIGHLIGHT = "<font color=\"red\">";
	public static final String END_HIGHLIGHT = "</font>";

	public abstract List<QueryResult> search(String query) throws Exception;

	public abstract List<QueryResult> search(String queryStr, int currentPage,
			int pageSize) throws Exception;

	// public abstract Hits searchforpage(String query) throws Exception;

	public abstract void close() throws Exception;

	protected void printQueryResult(List<QueryResult> results) {
		for (QueryResult result : results) {
			System.out.print(result);
		}
	}

	protected static void debug(AbstractSearcher searcher) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		String query;
		System.out.print("Input Query: ");
		while ((query = reader.readLine()) != null) {
			if (query.equals("END")) {
				break;
			}
			System.out.println("Query " + query);
			List<QueryResult> results = searcher.search(query);
			searcher.printQueryResult(results);
			System.out.print("Input Query: ");
		}
		reader.close();
		searcher.close();
		System.out.println("Query is Over!");
	}
}
