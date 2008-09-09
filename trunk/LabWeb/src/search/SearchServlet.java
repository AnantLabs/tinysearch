package search;

/**
 * search��Servlet��
 * */
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet {

	/**
	 * ��ʼ������
	 */
	public void init() throws ServletException {
		try {
			readParameters();
			lucene = new LuceneSearcher();
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
	}

	/**
	 * doGetִ�к���
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String startitem = null;
			startitem = request.getParameter("start");
			int currentPage;
			int totalCount;
			int totalPage;
			int pageSize = 10;// ÿҳ��¼��

			if (startitem == null || startitem.length() == 0) {
				startitem = "0";
			}
			currentPage = Integer.parseInt(startitem) / pageSize + 1; // ��ǰҳ��
			if (currentPage == 0) {
				currentPage = 1;
			}
			String query = request.getParameter("query");
			try {
				query = new String(query.getBytes("ISO-8859-1"), "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (query == null || query.length() == 0) {
				RequestDispatcher dispatcher = request
				.getRequestDispatcher("index.jsp");

				dispatcher.forward(request, response);
				return;
			}
			Date startDate = new Date();
			request.setAttribute("lucene", lucene.search(query, currentPage,
					pageSize));
			Date endDate = new Date();
			Long searchtime = endDate.getTime() - startDate.getTime();
			double second = searchtime.doubleValue() / 1000.00;

			RequestDispatcher dispatcher = request
					.getRequestDispatcher("search_results.jsp");
			if ((totalCount = lucene.getTotalCount()) == 0) {
				request.setAttribute("totalCount", totalCount); // �����¼����
				request.setAttribute("showpage", "Sorry, This is no Result!");
			} else {
				totalPage = lucene.getTotalPage(pageSize);
				String showpage = lucene.findPagedAll(currentPage, pageSize);
				request.setAttribute("totalCount", totalCount); // �����¼����
				request.setAttribute("searchTime", String.valueOf(second));
				request.setAttribute("totalPage", totalPage); // ����ҳ������
				request.setAttribute("currentPage", currentPage); // ���浱ǰҳ��
				request.setAttribute("showpage", showpage); // ��ҳ����
			}
			request.setAttribute("query", query);
			dispatcher.forward(request, response);
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
	}

	private LuceneSearcher lucene;

	private void readParameters() throws IOException {
		Properties pathConfig = new Properties();
		FileInputStream fis = new FileInputStream(getServletContext()
				.getRealPath("")
				+ "/WEB-INF/index_path.property");
		pathConfig.load(fis);
		fis.close();
		LuceneSearcher.PATH = pathConfig.getProperty("index");
	}
}
