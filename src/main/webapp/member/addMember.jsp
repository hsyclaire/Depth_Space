<%@ page contentType="text/html; charset=UTF-8" pageEncoding="Big5"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.depthspace.faq.model.service.*"%>
<%@ page import="com.depthspace.faq.model.controller.*"%>
<%@ page import="com.depthspace.faq.model.model.*"%>

<html>
<head>
    <title>���U�|��</title>
</head>
<body>
    <h1>���U�|��</h1>
    <form action="�s�W�|������ݳB�zURL" method="POST">
        <label for="memAcc">�b��:</label>
        <input type="text" id="memAcc" name="memAcc" required><br><br>
        
        <label for="memPwd">�K�X:</label>
        <input type="password" id="memPwd" name="memPwd" required><br><br>
        
        <label for="memName">�|���m�W:</label>
        <input type="text" id="memName" name="memName" required><br><br>
        
        <label for="memIdentity">�����Ҧr��:</label>
        <input type="text" id="memIdentity" name="memIdentity" required><br><br>
        
        <label for="memBth">�ͤ�:</label>
        <input type="date" id="memBth" name="memBth" required><br><br>
        
        <label for="memSex">�ʧO:</label>
        <input type="radio" id="male" name="memSex" value="1"> �k
        <input type="radio" id="female" name="memSex" value="0"> �k<br><br>
        
        <label for="memEmail">�q�l�l��:</label>
        <input type="email" id="memEmail" name="memEmail" required><br><br>
        
        <label for="memTel">����q��:</label>
        <input type="tel" id="memTel" name="memTel" required><br><br>
        
        <label for="memAdd">�a�}:</label>
        <input type="text" id="memAdd" name="memAdd" required><br><br>
        
        <label for="accStatus">���A:</label>
        <select id="accStatus" name="accStatus">
            <option value="1">���`</option>
            <option value="0">����</option>
        </select><br><br>
        
        <label for="memPoint">�|���I��:</label>
        <input type="number" id="memPoint" name="memPoint" required><br><br>
        
        <label for="memImage">�|���Ϥ�:</label>
        <input type="file" id="memImage" name="memImage"><br><br>
        
        <input type="submit" value="�s�W�|��">
    </form>
</body>
</html>
