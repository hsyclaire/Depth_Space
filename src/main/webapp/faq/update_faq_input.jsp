<%@ page contentType="text/html; charset=UTF-8" pageEncoding="Big5"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.depthspace.faq.model.model.FaqVO"%>
<%@ page import="com.depthspace.faq.model.service.*"%>
<%@ page import="com.depthspace.faq.model.controller.*"%>
<%@ page import="com.depthspace.faq.model.model.*"%>

<% //��com.emp.controller.EmpServlet.java��163��s�Jreq��empVO���� (�����q��Ʈw���X��empVO, �]�i�H�O��J�榡�����~�ɪ�empVO����)
   FaqVO faqVO = (FaqVO) request.getAttribute("faqVO");
%>

<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
<title>���u��ƭק� - update_faq_input.jsp</title>

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
	width: 450px;
	background-color: white;
	margin-top: 1px;
	margin-bottom: 1px;
  }
  table, th, td {
    border: 0px solid #CCCCFF;
  }
  th, td {
    padding: 1px;
  }
</style>

</head>
<body bgcolor='white'>

<table id="table-1">
	<tr><td>
		 <h3>���u��ƭק� - update_faq_input.jsp</h3>
		 <h4><a href="select_page.jsp"><img src="images/back1.gif" width="100" height="32" border="0">�^����</a></h4>
	</td></tr>
</table>

<h3>��ƭק�:</h3>

<%-- ���~��C --%>
<c:if test="${not empty errorMsgs}">
	<font style="color:red">�Эץ��H�U���~:</font>
	<ul>
		<c:forEach var="message" items="${errorMsgs}">
			<li style="color:red">${message}</li>
		</c:forEach>
	</ul>
</c:if>

<FORM METHOD="post" ACTION="emp.do" name="form1">
<table>
	<tr>
		<td>�y����:<font color=red><b>*</b></font></td>
		<td><%=faqVO.getSerialId()%></td>
	</tr>
	<tr>
		<td>Faq�s��:</td>
		<td><input type="TEXT" name="faqNo" value="<%=faqVO.getFaqNo()%>" size="45"/></td>
	</tr>
	<tr>
		<td>¾��:</td>
		<td><input type="TEXT" name="faqName"   value="<%=faqVO.getFaqName()%>" size="45"/></td>
	</tr>
	<tr>
		<td>�~��:</td>
		<td><input type="TEXT" name="faqAns"   value="<%=faqVO.getFaqAns()%>" size="45"/></td>
	</tr>

	<jsp:useBean id="faqSvc" scope="page" class="com.depthspace.faq.model.service.FaqService" />
<!-- 	<tr> -->
<!-- 		<td>����:<font color=red><b>*</b></font></td> -->
<!-- 		<td><select size="1" name="serialId"> -->
<%-- 			<c:forEach var="FaqVO" items="${faqSvc.all}"> --%>
<%-- 				<option value="${deptVO.deptno}" ${(empVO.deptno==deptVO.deptno)?'selected':'' } >${deptVO.dname} --%>
<%-- 			</c:forEach> --%>
<!-- 		</select></td> -->
<!-- 	</tr> -->

</table>
<br>
<input type="hidden" name="action" value="update">
<input type="hidden" name="serialId" value="<%=faqVO.getSerialId()%>">
<input type="submit" value="�e�X�ק�"></FORM>
</body>


</html>