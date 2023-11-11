<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.depthspace.faqtypes.model.model.FaqTypesVO"%>
<%@ page import="com.depthspace.faqtypes.model.service.*"%>
<%@ page import="com.depthspace.faqtypes.model.controller.*"%>
<%@ page import="com.depthspace.faqtypes.model.model.*"%>
<%-- 此頁暫練習採用 Script 的寫法取值 --%>

<%
	FaqTypesVO faqTypesVO = (FaqTypesVO) request.getAttribute("faqTypesVO"); //EmpServlet.java(Concroller), 存入req的empVO物件
%>

<html>
<head>
<title>FaqTypes資料 - listOneFaq.jsp</title>

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
	width: 600px;
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

<h4>此頁暫練習採用 Script 的寫法取值:</h4>
<table id="table-1">
	<tr><td>
		 <h3>FaqTypes資料 - listOneFaq.jsp</h3>
		 <h4><a href="select_page.jsp"><img src="images/back1.gif" width="100" height="32" border="0">回首頁</a></h4>
	</td></tr>
</table>

<table>
	<tr>
		<th>常見問題類型編號</th>
		<th>問題類型</th>
	</tr>
	<tr>
		<td>${faqTypesVO.faqNo}</td>
		<td>${faqTypesVO.qTypes}</td>
	</tr>
</table>

</body>
</html>