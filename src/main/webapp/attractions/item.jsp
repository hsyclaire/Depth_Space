<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>景點詳情</title>
	<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBOKKfj_MwehUJhm-t7jPbO1ydNODLgLOM&libraries=places&callback=initMap"></script>

<jsp:include page="/indexpage/head.jsp" />


    <!-- Your custom CSS for blue theme -->
    <style>
        body {
            background-color: #f8f9fa; /* Light gray background */
            color: #007bff; /* Blue text color */
        }

        .page-container {
            margin-top: 50px;
        }

        .attractions-img {
            max-width: 100%;
            height: auto;
        }
        .attractionsName {
        	margin-top: 20px;
        }

        .map-container {
            margin-top: 20px;
            height: 300px;
        }
        .btn_back {
        	margin-bottom: 20px;
        }

        #t_name h5 {
		    color: black; /* 評分文字顏色 */
		}
		
		#openTime li {
		    color: black; /* 營業時間文字顏色 */
		}
		.yellow-star {
		    color: #ffec70; /* 黃色 */
		    font-size: 1.8em; /* 調整星星大小 */
/* 		    margin-left: 3px; /* 加入一些間距 */ 
		}
    </style>
</head>

<body>
<jsp:include page="/indexpage/header.jsp" />
<jsp:include page="/indexpage/headpic.jsp" />

	<div class="container page-container">
    <div class="row offset-md-1">
        <div class="col-md-6">
            
            <img src="<%=request.getContextPath()%>/attractionsImage?attractionsId=${attractions.attractionsId}" alt="景點圖片" class="attractions-img">
            
        </div>
        <div class="col-md-6">
        
        	<div class="attractionsName">
            	<h2>${attractions.attractionsName}</h2>
            </div>
        
            <!-- 圖片下方地址及經緯度 -->
            <p>位置資訊：${attractions.address}</p>
			<h1 id="t_name"></h1>
        </div>
    </div>
    <div class="mt-3 border-bottom">
	
	</div>
    <!-- 景點描述 -->
    <div class="row">
        <div class="col-md-10 offset-md-1">
            <p>
                ${attractions.description}
            </p>
        </div>
    </div>

    <!-- Google 地圖 -->
    <div class="row">
    <div class="col-md-10 offset-md-1 map-container">
            <!-- 在這裡加入 Google 地圖的嵌入代碼或元素 -->
            <div id="map" style="height: 300px; width: 100%;" class="mt-3"></div>
            <!-- 可能需要添加一個 iframe 或其他地圖相關的元素 -->
        </div>
    </div>

    <!-- 返回鍵 -->
    <div class="row mt-5">
	    <div class="col-md-12 d-flex justify-content-center">
	        <button type="button" class="btn btn-outline-primary btn_back" onclick="history.back()">返回</button>
	    </div>
	</div>
</div>
<script type="text/javascript">
//https://developers.google.com/maps/documentation/javascript/places?hl=zh-tw#find_place_from_query
// GoogleMap API 使用餐廳名稱當地點透過findPlaceFromQuery()抓出place_id與經緯度 再使用getDetails()抓出地點的詳細資料
function initMap() {
  	var infowindow = new google.maps.InfoWindow();
  	
  	// findPlaceFromQuery()參數 用餐廳名稱當地名取place_id與經緯度在geometry
    var request = {
			query: "${ attractions.attractionsName }",
     		fields: ['name', 'place_id', 'geometry']
    	};
    
    var map = new google.maps.Map(document.getElementById('map'));
    var service = new google.maps.places.PlacesService(map);
    
    service.findPlaceFromQuery(request, function(results, status) {
      if (status === google.maps.places.PlacesServiceStatus.OK) {
        console.log(results[0]);
        
     	// 用經緯度定位地圖位置
	    var mapOptions = {
	    	zoom: 16,
	        center: { lat: results[0].geometry.location.lat(), lng: results[0].geometry.location.lng() } 
	    };
     	// 顯示地圖位置 
	    map = new google.maps.Map(document.getElementById('map'), mapOptions);
    	
	    // 在地圖上標記地址 location包含經緯度
        var marker = new google.maps.Marker({
            map: map,
            position: results[0].geometry.location,
        });
        
	    // 用place_id 查詢map詳細資料 fields取平均評分,照片,類型,營業時間
	    placeId = results[0].place_id;
       	var req = {
    		placeId: placeId,
     		fields: ['name', 'rating', 'photo', 'type', 'opening_hours','url']
		};
        
        service = new google.maps.places.PlacesService(map);
        service.getDetails(req, callback);
      }
    });
}

function callback(place, status) {
	if (status == google.maps.places.PlacesServiceStatus.OK) {
			console.log(place.url);
// 		console.log(place.url)
		// 評分
		$("#t_name").append("<p id='rating'>評分：" + place.rating + " <span class='yellow-star'>&#9733;</span></p>")
		
		// 營業時間
		var openTime = place.opening_hours.weekday_text;
		if (openTime != null){
			var ul = $("<ul id='openTime'></ul>");
			ul.append("<li>營業時間</li>");
			openTime.forEach(function(daytime, index){
				ul.append("<li>"+daytime+"</li>");
			})
			$('#rating').append(ul);
			
		}
		
		
	  
	}
}

</script>

<!-- Bootstrap JS, Popper.js, and jQuery -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

	
<jsp:include page="/indexpage/footer.jsp" />
</body>
</html>