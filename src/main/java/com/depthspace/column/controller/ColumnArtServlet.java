package com.depthspace.column.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.depthspace.column.model.ColumnArticlesVO;
import com.depthspace.column.model.ColumnTypesVO;
import com.depthspace.column.service.ColumnArticlesService;
import com.depthspace.column.service.ColumnArticlesServiceImpl;
import com.depthspace.column.service.ColumnImagesService;
import com.depthspace.column.service.ColumnImagesServiceImpl;

import com.depthspace.utils.JedisUtil;


@WebServlet("/columnarticles/*")
@MultipartConfig
public class ColumnArtServlet extends HttpServlet {
	
	private ColumnArticlesService columnArticlesService;
	private ColumnImagesService columnImagesService;

	public void init() throws ServletException {
		columnArticlesService = new ColumnArticlesServiceImpl();
		columnImagesService = new ColumnImagesServiceImpl();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String pathInfo = req.getPathInfo();
		if (pathInfo == null) {
			pathInfo = "";
		}

		switch (pathInfo) {
		case "/": // 專欄進入連結
			res.sendRedirect(req.getContextPath() + "/frontend/columnarticles/info.jsp");
			break;
		case "/list": // 專欄總列表
//			String searchTerm = req.getParameter("searchTerm");
//			if (searchTerm == null || searchTerm.trim().isEmpty()) {
//				// 如果為空沒有觸發搜尋，則列出全部
//				doList(req, res);
//				return;
//			} else {
//				// 否則執行搜尋
//				doSearch(req, res);
//				return;
//			}
			doList(req, res);
			break;
		case "/item": // 專欄單一頁面
			doItem(req, res);
			break;
		case "/search": // 專欄查找
			doSearch(req, res);
			break;

		default:
			System.out.println("Path not handled: " + pathInfo);
		}

	}

	/************ 專欄列表 ************/
	protected void doList(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String page = req.getParameter("page");
		int currentPage = (page == null) ? 1 : Integer.parseInt(page);
		long total = columnArticlesService.getTotal();
		
		List<ColumnArticlesVO> columnList = columnArticlesService.getAllArti2(currentPage);

		if (req.getSession().getAttribute("pageQty") == null) {
			int pageQty = columnArticlesService.getPageTotal();
			req.getSession().setAttribute("pageQty", pageQty);
		}
			searchList(req, res);	
		req.setAttribute("total", total); //專欄文章總數
		req.setAttribute("currentPage", currentPage); //分頁數
		req.setAttribute("resultSet", columnList);  


		RequestDispatcher dispatcher = req.getRequestDispatcher("/frontend/columnarticles/list.jsp");
		dispatcher.forward(req, res);
		System.out.println("TTTTT："+ columnList);
	}

	/************ 單一專欄頁面 ************/	
	protected void doItem(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String artiIdStr = req.getParameter("artiId");
		Integer artiId;
		try {
			artiId = Integer.valueOf(artiIdStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return;
		}		
		
	    // 隨機推薦文
		List<ColumnArticlesVO> allArticles = columnArticlesService.getAllArti();
		JedisPool pool = JedisUtil.getJedisPool();
		try (Jedis jedis = pool.getResource()) { 
			jedis.select(3);
            for (ColumnArticlesVO article : allArticles) {
                jedis.sadd("columnSet", article.getArtiId().toString());  //加到columnSet集合
            }
		    List<String> randomArticleIds = jedis.srandmember("columnSet", 5); //任取五篇
			List<ColumnArticlesVO> recommendedArticles = new ArrayList<>();
			for (String id : randomArticleIds) {
		        ColumnArticlesVO article = columnArticlesService.getArtiByArtiId(Integer.parseInt(id));
		        recommendedArticles.add(article);
		}
		    req.setAttribute("recommendedArticles", recommendedArticles);
	
		} catch (Exception e) {
	        System.out.println("Redis未成功連接，取消推薦文章載入");
	   	} 		
		ColumnArticlesVO columnArticles = columnArticlesService.getArtiByArtiId(artiId);
		req.setAttribute("columnArticles", columnArticles);
		req.getRequestDispatcher("/frontend/columnarticles/item.jsp").forward(req, res);
	}
	
	/************ 左側搜尋欄 **********/
	protected void searchList(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		List<ColumnArticlesVO> columnArticlesAll = columnArticlesService.getAllArti();
		req.setAttribute("columnArticlesAll", columnArticlesAll);

		// 處理類型不重複
		Set<ColumnTypesVO> uniqueColTypes = new HashSet<>();
		for (ColumnArticlesVO column : columnArticlesAll) {
			uniqueColTypes.add(column.getColType());
		}
		req.setAttribute("uniqueColTypes", new ArrayList<>(uniqueColTypes));

	}
	
	/************ 搜尋 ************/	
	protected void doSearch(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    res.setContentType("application/json");
	    res.setCharacterEncoding("UTF-8");

	    // 創建查詢map
	    Map<String, String[]> parameterMap = req.getParameterMap();
	    // 調用萬用查詢方法
	    List<ColumnArticlesVO> resultList = columnArticlesService.getColumnArticlesByCompositeQuery(parameterMap);
	    Set<ColumnArticlesVO> resultSet = new HashSet<>(resultList);
	    
		req.setAttribute("paramValues", parameterMap);
		
	    // 查詢結果的數量
	    int searchCount = resultSet.size();
	    req.setAttribute("searchCount", searchCount);

	    // 查詢結果存入
	    req.setAttribute("resultSet", resultSet);
	    
	    System.out.println("AAAAAAAAAAAAAAA："+resultSet);
	    System.out.println("AAAAAAAAAAAAAAA："+searchCount);
//		searchList(req, res);
	    req.getRequestDispatcher("/frontend/columnarticles/search.jsp").forward(req, res);
//	    req.getRequestDispatcher("/frontend/columnarticles/list.jsp").forward(req, res);
	}
	

}