<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header>
	<script>

	    window.onload = function() {
	        // 假設 isUserLoggedIn 是一個函數，用來檢查用戶是否登錄
	        let check = $("[name='check']").text();
	        if (check === "登入/註冊") {
	        	let cartNum=$("#cartNum");
	        	cartNum.hide();
	        }
	    };

		function checkSession(e){
		    let check = $("[name='check']").text();
		    if(check === "登入/註冊"){
		        e.preventDefault(); // 防止預設事件，例如表單提交或連結跳轉
	
		        // 使用 SweetAlert2 顯示彈窗
		        Swal.fire({
		            icon: "error",
		            text: "尚未登入！",
		            footer: '<a href="${pageContext.request.contextPath}/member/login.jsp">點此登入</a>'
		        });
		    }
		}
        <c:if test="${sessionScope.memId != null}">
        $(document).ready(function(){
            let cartNum=$("#cartNum");
            fetch("${pageContext.request.contextPath}/tsc/getCartNum")
            .then(function(response){
                    return response.text();
                })
                    .then(function(data){
                        cartNum.text(data)
                        if (cartNum.text() === "0" || cartNum.text() === "") {
                            cartNum.hide();
                        } else {
                            cartNum.show();
                        }
                    })
                    .catch(function(error){
                        console.log(error);
                    })

        })

        </c:if>
        
        
        if ("WebSocket" in window) {
            var loc = window.location;
            var wsUri;
            var isS = loc.protocol === 'https:'; //檢查https與否
            if (isS) {
                wsUri = "wss://";
            } else {
                wsUri = "ws://";
            }
            wsUri += loc.host;
            wsUri += "<%=request.getContextPath()%>/notificationsws";
            
            var ws = new WebSocket(wsUri);
            
            fetchUnreadNotifications();

            ws.onopen = function() {
             console.log('ws OPEN');
            };

            ws.onmessage = function(event) {
            fetchUnreadNotifications();
            console.log('ws onmessage');
            };

            window.onbeforeunload = function(event) {
                ws.close();
                console.log('ws close');
            };

            ws.onerror = function(event) {
                console.log("WebSocketerro: ", event);
            };
        }
            function fetchUnreadNotifications() {
                var url = "<%=request.getContextPath()%>/notifications/countUnread";
                fetch(url)
                .then(response => {
                    if (response.ok && response.headers.get("content-type").includes("application/json")) {
                        return response.json();
                    } else {
                        throw new Error('不是有效的 JSON');
                    }
                })
                .then(data => {
                    if (data.error) {
//                         console.error('Error: ', data.error);
                    } else {
                        document.querySelector('.notification-bell .badge').textContent = data.unreadCount;
                    }
                })
                .catch(error => console.error('Error fetching notifications:', error));
        }
    </script>
    
   <style>
    .notification-bell {
        position: relative;
        text-decoration: none;
        color: blue; 
    }
    .notification-bell .badge {
        position: absolute;
        border-radius: 50%;
        background-color: rgb(13 110 253 / 16%);
        font-size: 12px;
    }
    
    #chat-icon {
    position: fixed;
    bottom: 50px;
    right: 50px;
    width: 50px;
    height: 50px;
    border-radius: 50%;
    text-align: center;
    line-height: 50px;
    cursor: pointer;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    transition: transform 1s ease;
	}
	
	.gm{
	border-radius: 50%;
	}
	</style>
    
    
    <div class="container" style="padding-right: 0;">
        <nav class="navbar navbar-expand-lg">
            <div class="container-fluid" style="padding: 20px 0;">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/indexpage/index.jsp">
                    <img src="${pageContext.request.contextPath}/indexpage/images/icon.png" alt="" width="200px"                       
                         class="d-inline-block align-text-top">
                </a>
                	<button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                        data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                        aria-expanded="false" aria-label="Toggle navigation">
                        <label class="plane-switch">
						    <input type="checkbox">
						    <div>
						        <div>
						            <svg viewBox="0 0 13 13">
						                <path d="M1.55989957,5.41666667 L5.51582215,5.41666667 L4.47015462,0.108333333 L4.47015462,0.108333333 C4.47015462,0.0634601974 4.49708054,0.0249592654 4.5354546,0.00851337035 L4.57707145,0 L5.36229752,0 C5.43359776,0 5.50087375,0.028779451 5.55026392,0.0782711996 L5.59317877,0.134368264 L7.13659662,2.81558333 L8.29565964,2.81666667 C8.53185377,2.81666667 8.72332694,3.01067661 8.72332694,3.25 C8.72332694,3.48932339 8.53185377,3.68333333 8.29565964,3.68333333 L7.63589819,3.68225 L8.63450135,5.41666667 L11.9308317,5.41666667 C12.5213171,5.41666667 13,5.90169152 13,6.5 C13,7.09830848 12.5213171,7.58333333 11.9308317,7.58333333 L8.63450135,7.58333333 L7.63589819,9.31666667 L8.29565964,9.31666667 C8.53185377,9.31666667 8.72332694,9.51067661 8.72332694,9.75 C8.72332694,9.98932339 8.53185377,10.1833333 8.29565964,10.1833333 L7.13659662,10.1833333 L5.59317877,12.8656317 C5.55725264,12.9280353 5.49882018,12.9724157 5.43174295,12.9907056 L5.36229752,13 L4.57707145,13 L4.55610333,12.9978962 C4.51267695,12.9890959 4.48069792,12.9547924 4.47230803,12.9134397 L4.47223088,12.8704208 L5.51582215,7.58333333 L1.55989957,7.58333333 L0.891288881,8.55114605 C0.853775374,8.60544678 0.798421006,8.64327676 0.73629202,8.65879796 L0.672314689,8.66666667 L0.106844414,8.66666667 L0.0715243949,8.66058466 L0.0715243949,8.66058466 C0.0297243066,8.6457608 0.00275502199,8.60729104 0,8.5651586 L0.00593007386,8.52254537 L0.580855011,6.85813984 C0.64492547,6.67265611 0.6577034,6.47392717 0.619193545,6.28316421 L0.580694768,6.14191703 L0.00601851064,4.48064746 C0.00203480725,4.4691314 0,4.45701613 0,4.44481314 C0,4.39994001 0.0269259152,4.36143908 0.0652999725,4.34499318 L0.106916826,4.33647981 L0.672546853,4.33647981 C0.737865848,4.33647981 0.80011301,4.36066329 0.848265401,4.40322477 L0.89131128,4.45169723 L1.55989957,5.41666667 Z" fill="currentColor"></path>
						            </svg>
						        </div>
						        <span class="street-middle"></span>
						        <span class="cloud"></span>
						        <span class="cloud two"></span>
						    </div>
						</label>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav menu ml-auto">
                        <li class="nav-item">
                            <a class="active" href="${pageContext.request.contextPath}/indexpage/index.jsp">首頁</a>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="column" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                專欄
                            </a>
                            <ul class="dropdown-menu" aria-labelledby="column">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/columnarticles/list">專欄文章</a></li>                             
                            </ul>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownTicket" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                票券
                            </a>
                            <ul class="dropdown-menu" aria-labelledby="navbarDropdownTicket">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/ticketproduct/list">票券瀏覽</a></li>        
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/pro/getCard">促銷活動</a></li>
                            </ul>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="${pageContext.request.contextPath}/forumArticles/list.jsp" id="navbarDropdownArticles" role="button" data-bs-toggle="dropdown" aria-expanded="false">論壇文章</a>
                            <ul class="dropdown-menu" aria-labelledby="trip">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/forumArticles/list.jsp">文章瀏覽</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/forumArticles.do?action=getmemlist" onclick="checkSession(event)">我的文章</a></li>                               
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/forumArticles.do?action=doArtiCollectList" onclick="checkSession(event)">我的收藏</a></li>                               
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/forumArticles.do?action=addArticle" onclick="checkSession(event)">新增文章</a></li>                               
                            </ul>
                        </li>
                        <li class="nav-item">
                            <a class="" href="${pageContext.request.contextPath}/Rest/getRests">餐廳</a>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownTicket" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                景點
                            </a>
                            <ul class="dropdown-menu" aria-labelledby="trip">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/attr/list">景點瀏覽</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/tr/tourList" onclick="checkSession(event)">我的行程</a></li>                               
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/tr/addTour" onclick="checkSession(event)">新增行程</a></li>                               
                            </ul>
                        </li>
                       	<c:if test="${sessionScope.memId == null}">
                        <li class="nav-item">
                            <a class="booking" href="${pageContext.request.contextPath}/member/login.jsp" name="check">登入/註冊</a>
                        </li>
                        </c:if>
                        <c:if test="${sessionScope.memId != null}">
                        <li class="nav-item dropdown">
                            <a class="booking" href="#" id="mem" role="button" data-bs-toggle="dropdown" aria-expanded="false">${authenticatedMem.memName}</a>
                            <ul class="dropdown-menu" aria-labelledby="mem">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/mem/memList">會員資料</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/mto/memList">我的票券</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/ticketcollection/list">票券收藏</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/Rest/membooking">我的訂位</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/Rest/memCollection">餐廳收藏</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/forumArticles.do?action=getmemlist">我的文章</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/forumArticles.do?action=doArtiCollectList">文章收藏</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/to/memOrderList" onclick="checkSession(event)">訂單管理</a></li>                                
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/mem/logout" name="check">登出</a></li>
                            </ul>
                        </li>
                        <!-- 會員訊息通知 -->
                        <div class="notification-bell">
			            <li class="nav-item">
			                <a href="${pageContext.request.contextPath}/notifications/list">
							    <i class="fas fa-bell"></i>
							    <span class="badge" id="notificationCount">${unreadCount}</span>
							</a>
			            </li>
			            </div>
                        </c:if>                      
						<li class="">
						    <a href="${pageContext.request.contextPath}/tsc/memCartList" onclick="checkSession(event)">
						        <div style="position: relative; display: inline-block;">
						            <img src="${pageContext.request.contextPath}/indexpage/images/shoppingcar.svg" style="width: 2em"/>
						            <span id="cartNum" style="display:none;position: absolute;top: -8px;right: -15px;background-color: red;border-radius: 50%;padding: 2px 6px;"></span>
						        </div>
						    </a>
						</li>                    
                    </ul>
                </div>
            </div>
        </nav>
    </div>
        <!-- 聊天室 -->
    <div id="chat-icon">
      <a href="${pageContext.request.contextPath}/keywordQa/question_submission.jsp" onclick="checkSession(event)">
      <img src="${pageContext.request.contextPath}/indexpage/images/gm.png" class="gm" /></a>
    </div>
    <!-- 聊天室 -->
</header>


