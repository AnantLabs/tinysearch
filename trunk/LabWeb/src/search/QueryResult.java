package search;

/*
 *  查询结果封装类
 *  */
public class QueryResult {
	private String id;
	private String url;
	private String title;
	private String snippet;

	public QueryResult(String id, String snippet, String title, String url) {
		this.id = id;
		this.snippet = snippet;
		this.title = title;
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public String getSnippet() {
		return snippet;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(id).append("\t");
		buffer.append(title).append("\t");
		buffer.append(url).append("\t");
		buffer.append(snippet).append("\n");
		return buffer.toString();
	}

}
