<%--
  Created by IntelliJ IDEA.
  User: Tibame_T14
  Date: 2023/11/2
  Time: 上午 08:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>我的票券</title>
    <jsp:include page="/indexpage/head.jsp"/>
    <style>
        .hidden {
            display: none;
        }

        .card-horizontal {
            display: flex;
            flex: 11 auto;
        }

        .btn-outline-primary:hover {
            color: #ffffff; /
            background-color: #007bff;
            border-color: #007bff;
        }

        .ticket {
            /*width: 300px;*/
            /*height: 200px;*/
            background-color: #fff;
            position: relative;
            overflow: hidden;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            margin: 20px;
        }
        .voucher-sent{
            font-weight:bold;
            color:green
        }
        .card-body{
            position:relative;
        }
         .watermark {
             position: absolute;
             top: 50%;
             left: 50%;
             transform: translate(-50%, -50%) rotate(-45deg);
             z-index: 10;
             font-size: 35px; /* 根据需要调整大小 */
             font-weight:bold;
             color: rgba(211, 211, 211, 0.5);
             white-space: nowrap; /* 防止浮水印被换行 */
             top: 50%; /* 定位 */
             left: 50%;
             transform: translate(-50%, -50%) rotate(-35deg);
         }

    </style>


    <!-- 引入Bootstrap CSS樣式表 -->
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/indexpage/header.jsp"/>
<jsp:include page="/indexpage/headpic.jsp"/>
<div class="container mt-4">
    <h1 class="text-center my-4">我的票券</h1>
    <c:if test="${mtoPageQty > 0}">
        <b><font color=red>第${currentPage}/${mtoPageQty}頁</font></b>
    </c:if>
        <!-- ... 其他代码 ... -->
        <div class="row">
            <c:forEach items="${list}" var="mto" varStatus="mtoStatus">
                <div class="col-md-12 mb-4 card-ticket ticket"> <!-- 每个卡片占据6列 -->
                    <div class="card shadow h-100 rounded">
                        <div class="row g-0">
                            <div class="col-md-5 position-relative"> <!-- 调整图片区域的宽度 -->
                                <img src="${pageContext.request.contextPath}/tsc/image?serialId=${mto.serialId}" class="card-img-top rounded-start" alt="..."> <!-- 图片覆盖整个卡片顶部宽度 -->
                                <div class="position-absolute bottom-0 start-0 m-3 mx-2">
                                    <button type="button" class="btn btn-primary btn-sm">票券詳情</button>
                                </div>
                                <div class="position-absolute bottom-0 end-0 m-3 mx-2">
                                    <span class="badge bg-success rounded-pill p-2">$${mto.discountPrice}</span>
                                </div>
                            </div>
                            <div class="col-md-7 d-flex flex-column justify-content-between"><!-- 调整文本区域的宽度 -->
                                <div class="card-body">
                                    <div class="watermark">深度漫遊-電子票券</div>
                                    <span class="voucher-sent">憑證已發送</span>
                                    <h5 class="card-title fs-3"> ${mto.ticketName}</h5>
                                    <p class="card-text">訂單編號: ${mto.orderId}</p>
                                    <p class="card-text" name="status">${mto.statusOfUse}</p>
                                    <p class="card-text offset-8">使用期限: ${mto.expiryDate}</p>
                                </div>
                                <div class="card-footer bg-transparent border-0">
                                    <button type="button" class="btn btn-primary w-100">憑證下載</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <%--                <div class="col-md-6 mb-4"> <!-- 每个卡片占据6列 -->--%>
<%--                    <div class="card shadow h-100 rounded">--%>
<%--                        <div class="row g-0">--%>
<%--                            <div class="col-md-6"> <!-- 调整图片区域的宽度 -->--%>
<%--                                <img src="${pageContext.request.contextPath}/tsc/image?serialId=${mto.serialId}" class="img-fluid rounded-start" alt="...">--%>
<%--                                <div class="d-flex justify-content-between align-items-center mt-2">--%>
<%--                                    <button type="button" class="btn btn-primary m-lg-1">票券詳情</button>--%>
<%--                                    <span class="badge badge-success badge-pill p-2">$${mto.discountPrice}</span>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                            <div class="col-md-6"> <!-- 调整文本区域的宽度 -->--%>
<%--                                <div class="card-body">--%>
<%--                                    <h5 class="card-title">票券名稱: ${mto.ticketName}</h5>--%>
<%--                                    <p class="card-text">訂單編號: ${mto.orderId}</p>--%>
<%--                                    <p class="card-text">使用期限: ${mto.expiryDate}</p>--%>
<%--                                    <p class="card-text" name="status">使用狀態: ${mto.statusOfUse}</p>--%>
<%--                                    <div class="d-flex justify-content-between align-items-center">--%>
<%--                                        <button type="button" class="btn btn-primary">憑證下載</button>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                </div>--%>
            </c:forEach>
        </div>
        <!-- ... 分頁链接 ... -->

    <c:if test="${currentPage > 1}">
        <a href="${pageContext.request.contextPath}/mto/memList?page=1&memId=${memId}">至第一頁</a>&nbsp;
    </c:if>
    <c:if test="${currentPage - 1 != 0}">
        <a href="${pageContext.request.contextPath}/mto/memList?page=${currentPage - 1}&memId=${memId}">上一頁</a>&nbsp;
    </c:if>
    <c:if test="${currentPage + 1 <= mtoPageQty}">
        <a href="${pageContext.request.contextPath}/mto/memList?page=${currentPage + 1}&memId=${memId}">下一頁</a>&nbsp;
    </c:if>
    <c:if test="${currentPage != mtoPageQty}">
        <a href="${pageContext.request.contextPath}/mto/memList?page=${mtoPageQty}&memId=${memId}">至最後一頁</a>&nbsp;
    </c:if>
</div>
<script>
    $(document).ready(function(){
        $("[name='status']").each(function(){
            switch ($(this).text()){
                case '0':
                    $(this).text("未使用");
                    break;
                case '1':
                    $(this).text("已使用");
                    break;
                case '2':
                    $(this).text("已過期");
                    break;
            }
        })
    })
</script>
<!-- 引入Bootstrap JavaScript文件（必要時） -->
<jsp:include page="/indexpage/footer.jsp"/>
</body>
</html>
