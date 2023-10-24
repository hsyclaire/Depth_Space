<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="com.depthspace.faq.model.model.FaqVO"%>
<%@ page import="com.depthspace.faq.model.service.*"%>
<%@ page import="com.depthspace.faq.model.controller.*"%>
<%@ page import="com.depthspace.faq.model.model.*"%>
<%-- 此頁練習採用 EL 的寫法取值 --%>

<%
FaqService faqSvc = new FaqService();
List<FaqVO> list = faqSvc.getAll();
pageContext.setAttribute("list", list);
%>


<html>
<head>
<title>FAQ - listAllFaq.jsp</title>
<jsp:include page="../indexpage/head.jsp" />

<style>
table#table-1 {
	background-color: #CCCCFF;
	border: 2px solid black;
	text-align: center;
}

table#table-1 h4 {
	color: red;
	display: block;
	margin-bottom: 1px;
}

h4 {
	color: blue;
	display: inline;
}
</style>

<style>
table {
	width: 800px;
	background-color: white;
	margin-top: 5px;
	margin-bottom: 5px;
}

table, th, td {
	border: 1px solid #CCCCFF;
}

th, td {
	padding: 5px;
	text-align: center;
}
</style>

</head>
<body bgcolor='white'>
<jsp:include page="../indexpage/header.jsp" />

	<h4>此頁練習採用 EL 的寫法取值:</h4>
	<table id="table-1">
		<tr>
			<td>
				<h3>FAQ - listAllFaq.jsp</h3>
				<h4>
					<a href="select_page.jsp"><img src="images/back1.gif"
						width="100" height="32" border="0">回首頁</a>
				</h4>
			</td>
		</tr>
	</table>

	<table>
		<tr>
			<th>流水號</th>
			<th>FAQ編號</th>
			<th>FAQ名稱</th>
			<th>FAQ答案</th>
			<th>修改</th>
			<th>刪除</th>
		</tr>
		<%@ include file="page1.file"%>
		<c:forEach var="faqVO" items="${list}" begin="<%=pageIndex%>"
			end="<%=pageIndex+rowsPerPage-1%>">

			<tr>
				<td>${faqVO.serialId}</td>
				<td>${faqVO.faqNo}</td>
				<td>${faqVO.faqName}</td>
				<td>${faqVO.faqAns}</td>

				<td>
					<FORM METHOD="post"
						ACTION="<%=request.getContextPath()%>/faq/faq.do"
						style="margin-bottom: 0px;">
						<input type="submit" value="修改"> 
						<input type="hidden" name="serialId" value="${faqVO.serialId}"> 
						<input type="hidden" name="action" value="getOne_For_Update">
					</FORM>
				</td>
				<td>
					<FORM METHOD="post"
						ACTION="<%=request.getContextPath()%>/faq/faq.do"
						style="margin-bottom: 0px;">
						<input type="submit" value="刪除"> <input type="hidden"
							name="serialId" value="${faqVO.serialId}"> <input
							type="hidden" name="action" value="delete">
					</FORM>
				</td>
			</tr>
		</c:forEach>
	</table>
	<%@ include file="page2.file"%>

<jsp:include page="../indexpage/footer.jsp" />
</body>
</html>