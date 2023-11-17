<%@ page import="com.depthspace.forum.model.forumarticles.service.ForumArticlesServiceImpl" %>
<%@ page import="com.depthspace.forum.model.forumarticles.ForumArticlesVO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashSet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../indexpage/head.jsp" />
<title>我的文章</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<script type="text/javascript">
$(document).ready(function() {	
	
	 $('#articlesRow').on('click', '.collect-button', function(event) {
	        var button = $(this); // 獲取當前按鈕的 jQuery 對象
	        var articleId = button.data('articleid');

	        let check = $("[name='check']").text();
	        if(check == "登入/註冊") {
	            event.preventDefault();
	            window.alert("請先登入");
	            return; // 阻止後續代碼執行
	        }

	        $.ajax({
	            type: 'post',
	            url: '<%=request.getContextPath()%>/collect?action=add',
	            data: { articleId: articleId },
	            dataType: 'json',
	            success: function(response) {
	            	if(response.message === '添加收藏') {
	            	    button.html('<i class="fas fa-heart"></i>'); // 使用實心愛心圖標
	            	    button.addClass('collected');
	            	} else if(response.message === '取消收藏') {
	            	    button.html('<i class="far fa-heart"></i>'); // 使用空心愛心圖標
	            	    button.removeClass('collected');
	            	}
	            }
	        });
	    });
	
	
	
	
	
    $('#articlesRow').on('click', '.card', function() {
    	
    	// 檢查點擊的元素是否為按鈕，如果是，則不進行後續操作
        if ($(event.target).hasClass('btn')|| $(event.target).hasClass('fa-heart')) {
            return;
        }
    	
        var articleImage = $(this).find('img.card-img-top').attr('src');
        var articleTitle = $(this).find('h5.card-title').text();
        var articleText = $(this).find('div.card-text').html();
        var articleDate = $(this).find('.text-muted').text().replace('發布時間: ', '');

        // 填充模態對話框中的內容
        $('#articleImage').attr('src', articleImage);
        $('#articleTitle').text(articleTitle);
        $('#articleText').html(articleText);
        $('#articleDate').text('發布時間: ' + articleDate);

        // 顯示模態對話框
        $('#articleDetailsModal').modal('show');
    });
});
</script>
<style>
    .fixed-height-img {
        height: 200px;
        object-fit: cover;
        width: 100%;
    }

    .card-footer {
        padding: 0.5rem 1rem; /* 保留原有的 padding */
    }

    .likes {
        display: flex;
        align-items: center;
        justify-content: flex-end; /* 將點讚數靠右顯示 */
    }
    
    .list-group-item {
        display: flex;
        justify-content: space-between; /* 新增：使內容分佈左右兩端 */
        align-items: center; /* 新增：垂直居中對齊 */
    }

    .hidden-status {
    	display: none;
	}
	
	.btn:hover, .btn:focus, .btn:active, .btn:visited {
      outline: none;
      box-shadow: none !important;
     }
     
     div.hidden {
     display:none;
     }
     
    div.font-sizee p {
    font-size: 20px;
	}
	
	div.font-sizee {
    font-size: 20px;
	}
	
	div.modal-body {
    padding: 2.5rem !important;;
	}
	
		.collected i.fas.fa-heart {
	font-size:24px;
    color: red; /* 已收藏的愛心顏色 */
	}

	i.far.fa-heart {
	font-size:24px;
    color: black; /* 未收藏的愛心顏色 */
	}
	
	.like-button{
       color: transparent;
       background-color: transparent;
       border-color: transparent;
     }

	.like-button:hover, .like-button:focus, .like-button:active, .like-button:visited {
       outline: none;
       box-shadow: none !important;
       color: transparent;
       background-color: transparent;
       border-color: transparent;
     }
     
     	#articlesRow {
  		display: flex;
  		flex-wrap: wrap;
	}

	.cards {
	  background-color: #fff; /* 设置卡片的背景颜色 */
	  border: 1px solid #ccc; /* 设置边框 */
	  border-radius: 5px; /* 圆角边框 */
	  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); /* 添加阴影效果 */
	  transition: transform 0.2s ease-in-out; /* 添加hover时的平移效果 */
	}

	.cards:hover {
	  transform: scale(1.05); /* 鼠标悬停时稍微放大卡片 */
	  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 提高阴影深度 */
	}
</style>

</head>
<body>
<jsp:include page="../indexpage/header.jsp" />
<jsp:include page="../indexpage/headpic.jsp" />
 	<div id="list" class="container mt-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
	        <div class="btn-group" role="group"> <!-- 添加這個 div 來包裹按鈕 -->
	            <button type="button" class="btn btn-primary me-2" onclick="window.location.href='<%=request.getContextPath()%>/forumArticles/list.jsp'">瀏覽所有文章</button>
	            <button type="button" class="btn btn-primary" onclick="window.location.href='<%=request.getContextPath()%>/forumArticles/add.jsp'">新增文章</button>
	        </div>
    	</div>
        <div id="articlesRow" class="row">
            <c:forEach var="article" items="${list}">
                <div class="col-md-3 mb-3">
                    <div class="cards">
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            會員ID: ${article.memId} 
                            <span class="likes float-right">
                                <i class="fas fa-thumbs-up"></i> ${article.artiLk}
                            </span>
                        </li>
                        <img src="data:image/jpeg;base64,${article.base64Str}" class="card-img-top fixed-height-img">
                        <div class="card-body card-content">
                            <h5 class="card-title">${article.artiTitle}</h5>
                            <div class="card-text hidden">${article.artiText}</div>
                        </div>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item d-none">訊息ID: ${article.msgId}</li>
                            <li class="list-group-item d-none">文章ID: ${article.articleId}</li>
                            <li class="list-group-item d-none">文章類型: ${article.artiTypeId}</li>
                        </ul>
                        	<div class="card-footer">
                        	<fmt:formatDate value="${article.artiTime}" pattern="yyyy-MM-dd HH:mm:ss" var="formattedDate"/>
                            <small class="text-muted">發布時間:${formattedDate}</small>
                            <button class="btn btn-primary collect-button float-end like-button" data-articleid="${article.articleId}"><i class="far fa-heart"></i></button>
                       		</div>
                    	</div>                   
                </div>
            </c:forEach>
        </div>
    </div>
    
    
    <!-- 模態對話框 -->
<div class="modal fade" id="articleDetailsModal" tabindex="-1" aria-labelledby="articleDetailsModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="articleDetailsModalLabel">文章詳細信息</h5>
        <button type="button" class="btn btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div class="card" style="width: 45rem;">
          <div id="pic">
            <img src="" class="card-img-top" alt="文章圖片" id="articleImage">
          </div>
          <div class="card-body">
            <h3 class="card-title" id="articleTitle">文章標題:</h3>
            <div class="card-text font-sizee" id="articleText">文章內容:</div>
            <br>
            <div class="d-flex justify-content-end">
              <h6 class="card-text" id="articleDate">發布時間:</h6>
            </div>
          </div>
          <div class="card-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">關閉</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>
<jsp:include page="../indexpage/footer.jsp" />
</body>
</html>