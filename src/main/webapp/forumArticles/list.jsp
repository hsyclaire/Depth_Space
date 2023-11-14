<%@ page import="com.depthspace.forum.model.forumarticles.service.ForumArticlesServiceImpl" %>
<%@ page import="com.depthspace.forum.model.forumarticles.ForumArticlesVO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashSet" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../indexpage/head.jsp" />
<title>論壇文章清單</title>
<!-- 引入 Bootstrap CSS -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
<!-- <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css"> -->
<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<script>

$(document).ready(function() {
    let check = $("[name = 'check']").text();
    if (check=="登入/註冊"){
    	$.ajax({
            type: 'post',
            url: '<%=request.getContextPath()%>/forumArticles.do?action=list',
            cache: false,
            dataType: 'json',
            async: false,
            success: function(data) {
                data.forEach(function(item) {
                    var base64ImageData = item.base64Str;
                    var date = new Date(item.artiTime);
                    var formattedDate = formatDate(date);
                    var likesSpan = '<span class="likes float-right"><i class="fas fa-thumbs-up"></i> ' + item.artiLk + '</span>';
                    var card = $('<div class="col-md-4 mb-3">')
                    .append('<div class="card h-100">' +
                    		'<li class="list-group-item d-flex justify-content-between align-items-center">會員ID: ' + item.memId + likesSpan + '</li>' +
                        '<img src="data:image/jpeg;base64,' + base64ImageData + '" class="card-img-top fixed-height-img">' +
                        '<div class="card-body card-content">' +
                            '<h5 class="card-title">' + item.artiTitle + '</h5>' +
                            '<p class="card-text">' + item.artiText + '</p>' +
                        '</div>' +
                        '<ul class="list-group list-group-flush">' +                       
                            '<li class="list-group-item hidden-status">訊息ID: ' + item.msgId + '</li>' +
                            '<li class="list-group-item hidden-status">文章類型: ' + item.artiTypeId + '</li>' +
                            '<li class="list-group-item hidden-status">文章編號: ' + item.articleId + '</li>' +
                        '</ul>' +
                        '<div class="card-footer">' +
                            '<small class="text-muted">發布時間: ' + formattedDate + '</small>' +
                            '<button class="btn btn-primary collect-button float-end" data-articleid="' + item.articleId + '">收藏文章</button>'+                        
                            '</div>' +
                    '</div>');
                    $('#articlesRow').append(card);
                });
            }
        })
    }else{
    	  $.ajax({
    	        type: 'get',
    	        url: '<%=request.getContextPath()%>/forumArticles.do?action=list', // 替換為實際的 URL
    	        cache: false,
    	        dataType: 'json',
    	        async: false,
    	        success: function(response) {
    	            let articlesCollectList = response.articlesCollectList;
    	            let forumArticlesList = response.forumArticlesList;

    	            forumArticlesList.forEach(function(item) {
    	                var base64ImageData = item.base64Str;
    	                var date = new Date(item.artiTime);
    	                var formattedDate = formatDate(date);
    	                var likesSpan = '<span class="likes float-right"><i class="fas fa-thumbs-up"></i> ' + item.artiLk + '</span>';
    	                var isCollected = articlesCollectList.some(collectArticle => collectArticle.articleId === item.articleId);
    	                var buttonText = isCollected ? '已收藏' : '收藏文章';
    	                var buttonClass = isCollected ? 'btn btn-primary collect-button float-end collected' : 'btn btn-primary collect-button float-end';
    	                var card = $('<div class="col-md-4 mb-3">')
    	                .append('<div class="card h-100">' +
    	                        '<li class="list-group-item d-flex justify-content-between align-items-center">會員ID: ' + item.memId + likesSpan + '</li>' +
    	                        '<img src="data:image/jpeg;base64,' + base64ImageData + '" class="card-img-top fixed-height-img">' +
    	                        '<div class="card-body card-content">' +
    	                            '<h5 class="card-title">' + item.artiTitle + '</h5>' +
    	                            '<p class="card-text">' + item.artiText + '</p>' +
    	                        '</div>' +
    	                        '<ul class="list-group list-group-flush">' +                       
    	                            '<li class="list-group-item hidden-status">訊息ID: ' + item.msgId + '</li>' +
    	                            '<li class="list-group-item hidden-status">文章類型: ' + item.artiTypeId + '</li>' +
    	                            '<li class="list-group-item hidden-status">文章編號: ' + item.articleId + '</li>' +
    	                        '</ul>' +
    	                        '<div class="card-footer">' +
    	                            '<small class="text-muted">發布時間: ' + formattedDate + '</small>' +
    	                            '<button class="' + buttonClass + '" data-articleid="' + item.articleId + '">' + buttonText + '</button>'+                        
    	                        '</div>' +
    	                '</div>');
    	                $('#articlesRow').append(card);
    	            });
    	        }
    	    });
    }
    
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
                    button.text('已收藏');
                    button.addClass('collected'); // 添加已收藏的樣式
                } else if(response.message === '取消收藏') {
                    button.text('收藏文章');
                    button.removeClass('collected'); // 移除已收藏的樣式
                }
            }
        });
    });
    
//     $.ajax({
//         type: "post",
<%--         url: '<%=request.getContextPath()%>/forumArticles.do?action=getmemlist', --%>
//         dataType: "json",
//         success: function(data) {
//             var selectBox = $("#memId");
//             selectBox.empty(); 
//             $.each(data, function(index, value) {
//                 selectBox.append($("<option></option>").attr("value", value).text(value));
//             });
//         }
//     });
    
	    $.ajax({
	        type: "post",
	        url: '<%=request.getContextPath()%>/forumArticles.do?action=getArtiTypeList',
	        dataType: "json",
	        success: function(data) {
	            var selectBox = $("#artiTypeId");
	            selectBox.empty(); 
	            $.each(data, function(index, atvo) {
	                selectBox.append($("<option></option>").attr("value", atvo.artiTypeId).text(atvo.artiTypeText));
	            });
	        }
	    });
});

    function formatDate(date) {
        var year = date.getFullYear();
        var month = padZero(date.getMonth() + 1); // 月份從0開始
        var day = padZero(date.getDate());
        var hours = padZero(date.getHours());
        var minutes = padZero(date.getMinutes());
        var seconds = padZero(date.getSeconds());
        return year + '/' + month + '/' + day + ' ' + hours + ':' + minutes + ':' + seconds;
    }

    function padZero(value) {
        return value < 10 ? '0' + value : value;
    }
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

	.collected{
    background-color: red !important;
    color: white !important;
	}
	
	.btn:hover, .btn:focus, .btn:active, .btn:visited {
      outline: none;
      box-shadow: none !important;
     }
</style>

</head>
<body>
<jsp:include page="../indexpage/header.jsp" />
<jsp:include page="../indexpage/headpic.jsp" />
<div id="list" class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>文章清單</h1>
            <!-- 查詢表單 -->
<%--     <form method="post" action="<%=request.getContextPath()%>/forumArticles.do?action=domemlist"> --%>
<!--         <label for="memId">選擇會員編號：</label> -->
<!--         <select id="memId" name="memId"> -->
<!--         </select> -->
<!--         <input type="submit" value="查詢"> -->
<!--     </form> -->
    <form method="post" action="<%=request.getContextPath()%>/forumArticles.do?action=doArtiTypeList">
        <label for="artiTypeId">選擇文章類型：</label>
        <select id="artiTypeId" name="artiTypeId">
        </select>
        <input type="submit" value="查詢">
    </form>
		<button type="button" class="btn btn-primary" onclick="window.location.href='<%=request.getContextPath()%>/forumArticles.do?action=getmemlist'">我的文章</button>
		<button type="button" class="btn btn-primary" onclick="window.location.href='<%=request.getContextPath()%>/forumArticles.do?action=doArtiCollectList'">我的收藏</button>
        <button type="button" class="btn btn-primary" onclick="window.location.href='<%=request.getContextPath()%>/forumArticles.do?action=addArticle'">新增文章</button>
    </div>
    <div id="articlesRow" class="row">
        <!-- 卡片內容將會通過 jQuery 動態加載到這裡 -->
    </div>
</div>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>
<jsp:include page="../indexpage/footer.jsp" />
</body>
</html>
