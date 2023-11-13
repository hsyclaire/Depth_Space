<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>會員權限管理</title>
      <%--  include head.jsp--%>
    <jsp:include page="/backend/backIndex/head.jsp"></jsp:include>
</head>
<body>
<%--include header.jsp--%>
<jsp:include page="/backend/backIndex/header.jsp"></jsp:include>
<div class="container-fluid my-0">
    <div class="row">
        <%--    側邊欄--%>
        <div class="col-lg-2 g-3 my-0">
            <jsp:include page="/backend/backIndex/sidebar.jsp"></jsp:include>
        </div>
        <div class="col-lg-10 g-2 transparent rounded my-0">

  		<%--      放入自己body裡的代碼--%>

	    <div class="container">
	        <h3 class="text-center mt-2">會員列表</h3>
	        <hr class="my-0">
	
<!-- 	       <h4 class="mt-4">查詢會員</h4> -->
				<form class="form-inline" action="your_search_action.jsp" method="post">
				    <div class="row mt-4">
				        <div class="col-md-12">
				            <span for="memId" class="form-label mt-2">搜尋會員姓名：</span>
				       
<!-- 				            <div class="input-group"> -->
				                <input type="text" id="memId" name="memId" placeholder="請輸入姓名">
<!-- 				            </div> -->
<!-- 				        </div> -->
<!-- 				        <div class="col-md-8"> -->
				            <button type="submit" class="btn btn-primary mx-1 mb-2">查詢</button>
				        </div>
				    </div>
				</form>
	
	        <!-- 會員列表 -->
<!-- 	        <h2 class="mt-4">會員列表</h2> -->
	        <table class="table table-bordered table-hover table-striped">
	            <thead>
	                <tr class="text-center">
	                    <th>會員編號</th>
	                    <th>帳號</th>
	                    <th>會員姓名</th>
	                    <th>身分證字號</th>
	                    <th>生日</th>
	                    <th>性別</th>
	                    <th>電子郵件</th>
	                    <th>手機電話</th>
	                    <th>地址</th>
	                    <th>狀態</th>
	                    <th>會員點數</th>
	                    <th>操作</th>
	                </tr>
	            </thead>
	            <tbody>
				<!-- 迴圈取出所有會員資料-->
	                <c:forEach items="${list}" var="list">
		                <tr align="center">
		                    <td>${list.memId}</td>
		                    <td>${list.memAcc}</td>
		                    <td>${list.memName}</td>
		                    <td>${list.memIdentity}</td>
		                    <td>${list.memBth}</td>
		                    <td>${list.memSex}</td>
		                    <td>${list.memEmail}</td>
		                    <td>${list.memTel}</td>
		                    <td>${list.memAdd}</td>
		                    <td>${list.accStatus}</td>
		                    <td>${list.memPoint}</td>
		                    <td>
		                    	<a href="${pageContext.request.contextPath}/backmem/edit?memId=${list.memId}" class="btn btn-secondary">編輯</a>
		                    </td>
		                </tr>
	                </c:forEach>
	                
	            </tbody>
	        </table>
	    </div>
     </div>
    </div>
</div>

    <!-- 引入Bootstrap JavaScript，如有需要 -->
<!--     <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script> -->
<!--     <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.3/dist/umd/popper.min.js"></script> -->
<!--     <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script> -->

</body>
</html>
