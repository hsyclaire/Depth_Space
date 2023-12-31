<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.depthspace.attractions.model.AttractionsVO"%>
<%@ page import="com.depthspace.attractions.service.AttractionsService"%>

<%
AttractionsService attractionService = new AttractionsService();
List<AttractionsVO> list = attractionService.getAll();
pageContext.setAttribute("list", list);
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="stylesheet"	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">	
<link rel="stylesheet"	href="<c:url value='/static/css/backendlist.css'/>">
<style>
    .add-button {
        background-color: #63bfc9; 
        color: white; 
        padding: 8px 40px; 
        border: none; 
        border-radius: 5px;
        cursor: pointer; 
    }
    .add-button:hover {
        background-color: #5b969c; 
    }
    .table-hover tbody tr:hover {
        background-color: #f5f5f5;
    }
    .table-list .viewimg{
    max-width: 50%;
    max-height: none;
    display: block;
/*     margin: inherit; */
    padding-bottom: 20px;
    }
    .h5, h5 {
    font-size: 1.2rem;
    padding: 0px 0px 10px 0px;
	}
	.rowrow{
    padding: 0px 0px 20px 0px;
	}
	.custom-select-wrapper {
	    position: relative;
	}
	
	.custom-select-wrapper select {
	    appearance: none; 
	    -webkit-appearance: none;
	    -moz-appearance: none;
	    padding-right: 30px; 
	}
	
	.custom-select-wrapper::after {
	    content: "\f078"; 
	    font-family: "FontAwesome";
	    position: absolute;
	    top: 0;
	    right: 10px;
	    pointer-events: none; 
	    color: #555;
	    font-size: 18px;
	    height: 100%;
	    display: flex;
	    align-items: center;
	}
</style>
<title>景點列表</title>
<%--  include --%>
	<jsp:include page="/backend/backIndex/head.jsp"></jsp:include>
  
</head>

<body>

	<jsp:include page="/backend/backIndex/header.jsp"></jsp:include>
	<div class="container-fluid my-0">
	<div class="row">
	  
	<div class="col-lg-2 g-3 my-0">
	<jsp:include page="/backend/backIndex/sidebar.jsp"></jsp:include>
	</div>
	
	<div class="col-lg-10 g-2 transparent rounded my-0">
	
<%-- include end--%>


<div class="table-list">
   <div class="table-list">
       <div class="container mt-5">
		<h4 style="font-weight: bold;">景點列表</h4>
		<div class="row">
         <div class="col-md-4">
            <form id="filterForm" method="get" action="${pageContext.request.contextPath}/attractionsEnd/search">
                <label for="attrTypeId" style="display: none";></label>
                <select id="attrTypeId" name="attrTypeId" class="form-control mb-2">
				    <option value="">景點類型選單</option>
				    <c:forEach var="typeItem" items="${uniqueTypes}">
				        <option value="${typeItem.attractionsTypeId}">${typeItem.typeName}</option>
				    </c:forEach>
				</select>
            </form>
          </div>
          <div class="col-md-4">           
	        <form id="filterForm2" method="get" action="${pageContext.request.contextPath}/attractionsEnd/search">
	            <div class="input-group">
	                <input type="text" id="attractionsName" name="attractionsName" class="form-control" placeholder="關鍵字搜尋" value="${attr.attractionsName}">
	                <div class="input-group-append">
	                    <button class="btn btn-outline-secondary" type="submit">
	                        <i class="fas fa-search"></i>
	                    </button>
	                </div>
	            </div>
	        </form>
	       </div> 
               <div class="col-md-4 d-flex justify-content-end"> 
	      		<form method="post" action="${pageContext.request.contextPath}/attractionsEnd/add">
					<button class="add-button" type="submit">新增</button>
				</form>
		  </div>
       </div>
		
		

		<!-- 表格 -->
		<table class="table table-bordered">
		<%@ include file="page1.file"%>
			<thead>
				<tr>
					<th>景點編號</th>
					<th>景點類型</th>
					<th>圖片</th>
					<th>景點名稱</th>
					<th>描述</th>
					<th>地址</th>
					<th>狀態</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<!-- 所有資料 -->
				<tr>
					<c:forEach var="attr" items="${list}" varStatus="status" begin="<%=pageIndex%>" end="<%=pageIndex+rowsPerPage-1%>">
						<td>${attr.attractionsId}</td>
						<td>${attr.attractionsTypeId.typeName}</td>
						<td>
							<img src="${pageContext.request.contextPath}/attractionsImage?attractionsId=${attr.attractionsId}">
						</td>
						<td>${attr.attractionsName}</td>
						<td><c:choose>
								<c:when test="${fn:length(attr.description) > 30}">
								${fn:substring(attr.description,0,30)}...
								</c:when>
								<c:otherwise>
								${attr.description}
								</c:otherwise>
							</c:choose></td>
						<td>${attr.address}</td>
						<td><c:choose>
								<c:when test="${attr.attractionsStatus == 0 }">上架</c:when>
								<c:otherwise>未上架</c:otherwise>
							</c:choose></td>
						<td>
						    <a href="${pageContext.request.contextPath}/attractionsEnd/edit?attractionsId=${attr.attractionsId}" class="btn btn-primary btn-sm" style="background-color: #63bfc9; border-color: #63bfc9;">
							    <i class="fas fa-edit"></i>
							</a><p></p>
						    <button type="button" class="btn btn-primary btn-sm view" data-toggle="modal" data-target="#viewModal" data-arti-id="${attr.attractionsId}" style="background-color: #63bfc9; border-color: #63bfc9;">
						        <i class="fas fa-eye"></i>
						    </button>
						</td>				
				</tr>
				</c:forEach>
		</table>
	 </div>
	 
	 <!-- 查看 -->		
		<div class="modal fade" id="viewModal" tabindex="-1" role="dialog" aria-labelledby="viewModalLabel" aria-hidden="true">
		    <div class="modal-dialog modal-lg" role="document">
		        <div class="modal-content">
		            <div class="modal-header">
		                <h5 class="modal-title" id="viewModalLabel">景點詳情</h5>
		                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		                    <span aria-hidden="true">&times;</span>
		                </button>
		            </div>
		            <div class="modal-body">
		                <div class="row">
                          
		                    <div id="carouselExampleIndicators" class="carousel slide">
		                        <div class="carousel-inner" id="images"></div>
							</div>
		                    <div class="col-md-6 mb-3"><strong class="d-block mb-2">景點類型</strong>
		                        <p id="attractionsType"></p>
		                    </div>
		                    <div class="col-md-6 mb-3"><strong class="d-block mb-2">景點</strong>
		                        <p id="attrnName"></p>
		                    </div>
		                    <div class="col-md-12 mb-3"><strong class="d-block mb-2">景點介紹</strong>
		                        <div id="description"></div>
		                    </div>		                    
		                    <div class="col-md-6 mb-3"><strong class="d-block mb-2">地址</strong>
		                        <p id="address"></p>
		                    </div>
		                    <div class="col-md-6 mb-3" id="attractionsStatus"><strong class="d-block mb-2"></strong>
		                    </div>
		                </div>
		            </div>
		            <div class="modal-footer">
		                <button type="button" class="btn btn-secondary" data-dismiss="modal">關閉</button>
		                <a href="${pageContext.request.contextPath}/attractionsEnd/edit?attractionsId=${attr.attractionsId}" class="btn btn-primary" id="editButton">修改</a>
		            </div>
		        </div>
		    </div>
		</div>
		<div style="text-align: center;">
		<%@ include file="page2.file"%>
		</div>
		
		</div>
		</div>
	</div>		
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script	src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script src="//cdn.jsdelivr.net/npm/sweetalert2@11"></script>			
<script>
$(document).ready(function() {
    $('#filterForm, #filterForm2').on('change', 'select', function() {
        var formId = $(this).closest('form').attr('id');
    	Swal.fire({
            title: '正在處理',
            html: '請稍等，正在更新資料。',
            timer: 1500,
            timerProgressBar: true,
            didOpen: () => {
                Swal.showLoading();
            },
            willClose: () => {
                $('#' + formId).submit();
            }
        });
    });
});

    $('.view').on('click', function() {
        var artiId = $(this).data('arti-id');
        console.log(artiId);
        
        let url = "${pageContext.request.contextPath}/attractionsEnd/view?attractionsId="+artiId;
        fetch(url)
			.then(function(response){
	            return response.json();
	        })
	        .then(function(data){
				console.log(data);
				console.log(data.attractionsId);
				console.log(data.attractionsName);
				console.log( "測試1" + $('#attractionsId'));
				console.log( "測試2" + $('#attractionsName'));
			 $('#attractionsId').text(data.attractionsId);
			 $('#attractionsType').text(data.attractionsTypeId.typeName);
             $('#attrnName').text(data.attractionsName);
             $('#description').html(data.description);
             $('#address').text(data.address);
             $('#attractionsStatus').text(data.attractionsStatus == 0 ? '景點上架中' : '景點未開放');
             $('#editButton').attr('href', '${pageContext.request.contextPath}/attractionsEnd/edit?attractionsId=' + data.attractionsId);
             var carouselInner = $('#images').empty();
             var carouselItem = $('<div>').addClass('carousel-item active');
             var img = $('<img>')
	             .attr("src", "<%=request.getContextPath()%>/attractionsImage?attractionsId=" + data.attractionsId)
	             .addClass("viewimg");
	         carouselItem.append(img);
	         carouselInner.append(carouselItem);

	        })
	        
	        .catch(function(error){
	          console.log(error);
	        })
    });


</script>
		

</body>
</html>
