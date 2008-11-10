<%@ page language="java" import="java.util.*,search.*" pageEncoding="GB2312"%>
<html>
<head>
<%
	List luceneResults = (List) request.getAttribute("lucene");
	int minSize = luceneResults.size();	
	String searchWord = (String)request.getAttribute("query");
%>
	<title>搜索结果：<%=searchWord%></title>
    <link href="css/default.css" rel="stylesheet" type="text/css" />
</head>
<body>

<table width="100%" >
	<tbody>
	      <tr>
		  		<td style="width: 110px;" valign="top">
					<a href="http://localhost:8080"><img src="image/tiny.png" border="0" /></a>&nbsp;&nbsp;
				</td>
				<td>
					<form id="searchForm" action="search">
					<input name="query" id="query" type="text" size="30" maxlength="60" value="<%=searchWord%>" />&nbsp;&nbsp;
					<input id="search" type=submit value="搜  吧" />
					</form>
				</td>
	      </tr> 
	</tbody>
</table>
<table class="t bt" width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr class="f3">
	<td align="left" nowrap="nowrap">网页搜索</td>
	<td align="right" nowrap="nowrap">
		<%
		  Integer totalCount;
		  String showpage = null;
		  totalCount = (Integer)request.getAttribute("totalCount");
		  showpage = (String)request.getAttribute("showpage");
		  String searchTime = (String)request.getAttribute("searchTime");
		  if (totalCount > 0)
		  {
			  // Integer totalPage = (Integer)request.getAttribute("totalPage");
			  Integer currentPage = (Integer)request.getAttribute("currentPage");
			  int startPos = Math.max((currentPage - 1) * 10, 0) + 1;
			  int endPos = Math.min(currentPage * 10 - 1, totalCount - 1) + 1;
			  // 共<b> =totalPage </b>页，
		%>
		约有<b><%=totalCount %></b>项符合<b><%=searchWord %></b>的查询结果,以下是第<b><%=startPos %></b> - <b><%=endPos %></b>项,搜索用时<b><%=searchTime %></b>秒			 
		</td>
	</tr>
</table>
	  <% showpage = (String)request.getAttribute("showpage");	%>
<table>
	<% 
		for (int i = 0; i < minSize; ++i) { 
			QueryResult luceneResult = (QueryResult) luceneResults.get(i);
	%>
	<tr>
		<td>
			<table style="width: 800px; table-layout: fixed;" border="0">
			<tbody>
				<tr>
					<td class="f2">
						<a class="f1" href="<%=luceneResult.getUrl()%>" target="_blank" style="text-decoration: underline;" title="<%=luceneResult.getTitle()%>"><%=luceneResult.getTitle()%></a>
						<br><%=luceneResult.getSnippet()%>
						<br><font color="green"><%=luceneResult.getUrl()%></font>  [<a href="<%=luceneResult.getUrl()%>" target="_blank" style="color: rgb(102, 102, 102);">链接</a>]
					</td>
				</tr>
			</tbody>
			</table>
			<br>
		</td>
	</tr>
</table>
	<% } %>
	<br>
<div><%=showpage %></div><br>
<%
	  }
	  else
	  {
		  out.print("</td>");
		  out.print("</tr>");
		  out.print("</table>");
	%>
	<div><%=showpage %>
	找不到和您的查询 "<b><%=searchWord%></b>" 相符的网页。
	<br>建议：
	<ul><li>请检查输入字词有无错误。	<li>请换用另外的查询字词。<li>请改用较常见的字词。</ul>
</div>	
	<%
	  }
	%>	
	    <center>
             &copy; 2008 | <a href="http://apex.sjtu.edu.cn/">Apex Data &amp; Knowledge Management Lab</a>
	</center>
</body>
</html>