package com.depthspace.column.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.hibernate.Session;
import org.json.JSONObject;

import com.depthspace.admin.model.AdminVO;
import com.depthspace.column.model.ColumnArticlesVO;
import com.depthspace.column.model.ColumnImagesVO;
import com.depthspace.column.model.ColumnTypesVO;
import com.depthspace.column.service.ColumnArticlesService;
import com.depthspace.column.service.ColumnArticlesServiceImpl;
import com.depthspace.column.service.ColumnImagesService;
import com.depthspace.column.service.ColumnImagesServiceImpl;

@WebServlet("/columnmg/*")
@MultipartConfig
public class ColumnArtMgServlet extends HttpServlet {

	private ColumnArticlesService columnArticlesService;
	private ColumnImagesService columnImagesService;
	Session session;

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
		case "/": // 專欄管理頁面
			res.sendRedirect(req.getContextPath() + "/backend/column/mg.jsp");
			break;
		case "/list": // 專欄總列表
			doList(req, res);
			break;
		case "/add": // 專欄新增
			doAdd(req, res);
			break;
		case "/edit": // 專欄修改
			doEdit(req, res);
			break;
		case "/find": 
			doSearch(req, res);
			break;	
		case "/view": 
			view(req, res);
			break;
		default:
			System.out.println("Path not handled: " + pathInfo);
		}

	}

	/************ 專欄列表 ************/
	private void doList(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		List<ColumnArticlesVO> columnListAll = columnArticlesService.getAllArti();
		req.setAttribute("columnListAll", columnListAll);

		// 取得所有內容(VO) "分頁"
		String page = req.getParameter("page");
		int currentPage = (page == null) ? 1 : Integer.parseInt(page);

		List<ColumnArticlesVO> columnList = columnArticlesService.getAllArti2(currentPage);
		
		int pageQty = columnArticlesService.getPageTotal();
		req.getSession().setAttribute("pageQty", pageQty);
		
		req.setAttribute("columnList", columnList);
		req.setAttribute("currentPage", currentPage);
		
		//處理專欄類型不重複
		Set<ColumnTypesVO> uniqueTypes = new HashSet<>();
		for (ColumnArticlesVO columnArticles : columnListAll) {
			uniqueTypes.add(columnArticles.getColType());
		}
		req.setAttribute("uniqueTypes", new ArrayList<>(uniqueTypes));
		
		RequestDispatcher dispatcher = req.getRequestDispatcher("/backend/column/list.jsp");
		dispatcher.forward(req, res);
	}

	/************ 專欄查看 ************/
    protected void view(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Integer artiId = Integer.valueOf(req.getParameter("artiId"));
		ColumnArticlesVO columnArticles = columnArticlesService.getArtiByArtiId(artiId);
		
        JSONObject jsonColumn = new JSONObject();
        if (columnArticles != null) {
        	jsonColumn.put("artiId", columnArticles.getArtiId());
        	jsonColumn.put("artiTitle", columnArticles.getArtiTitle());
        	jsonColumn.put("artiContent", columnArticles.getArtiContent());
        	jsonColumn.put("articleDate", columnArticles.getArticleDate());
        	jsonColumn.put("artiStatus", columnArticles.getArtiStatus()); 
            
            if (columnArticles.getColType() != null) {
            	jsonColumn.put("colType", columnArticles.getColType().getColTypeName());
            }
            if (columnArticles.getAdmin() != null) {
            	jsonColumn.put("admin", columnArticles.getAdmin().getAdminName());
            }
        }

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        res.getWriter().write(jsonColumn.toString());
    }
    
	/************ 專欄新增 ************/
	private void doAdd(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if (!req.getMethod().equalsIgnoreCase("POST")) {
			// 新增頁面中要放入選單項目，要取其值，set屬性到頁面中
			List<ColumnTypesVO> columnTypes = columnArticlesService.getAllColumnTypes();
			List<AdminVO> admins = columnArticlesService.getAllAdmins();
			req.setAttribute("columnTypes", columnTypes);
			req.setAttribute("admins", admins);

			RequestDispatcher dispatcher = req.getRequestDispatcher("/backend/column/add.jsp");
			dispatcher.forward(req, res);
			return;
		} else {
			// 完成表單填寫，按下送出觸發POST，就將下列的資料送出
			ColumnArticlesVO columnArticles = new ColumnArticlesVO();
			columnArticles.setArtiTitle(req.getParameter("artiTitle"));
			columnArticles.setArtiContent(req.getParameter("artiContent"));
			columnArticles.setArtiStatus(Byte.valueOf(req.getParameter("artiStatus")));

			Integer colTypeId = Integer.valueOf(req.getParameter("colTypeId"));
			ColumnTypesVO colType = new ColumnTypesVO();
			colType.setColTypeId(colTypeId);
			columnArticles.setColType(colType);

			Integer adminId = Integer.valueOf(req.getParameter("adminId"));
			AdminVO admin = new AdminVO();
			admin.setAdminId(adminId);
			columnArticles.setAdmin(admin);

			columnArticlesService.addArt(columnArticles);

			// 存入圖片
			Part filePart = req.getPart("colImg");
			if (filePart != null && filePart.getSize() > 0) {
				ColumnImagesVO colImg = new ColumnImagesVO();
				colImg.setColumnArticles(columnArticles);
				colImg.setIsMainImage((byte) 1);

				InputStream inputStream = filePart.getInputStream();
				byte[] imageBytes = readInputStream(inputStream);
				colImg.setColImg(imageBytes);
				columnImagesService.save(colImg);
			}
		}
		int pageQty = columnArticlesService.getPageTotal();
		req.getSession().setAttribute("pageQty", pageQty);
		
		res.sendRedirect(req.getContextPath() + "/columnmg/list");
	}

	/************ 專欄修改 ************/
	private void doEdit(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		Integer artiId = Integer.valueOf(req.getParameter("artiId"));
		ColumnArticlesVO columnArticles = columnArticlesService.getArtiByArtiId(artiId);

		if(!req.getMethod().equalsIgnoreCase("POST")) {
			//編輯頁面(傳既有資料)
			List<ColumnTypesVO> columnTypes = columnArticlesService.getAllColumnTypes();
//			List<AdminVO> admin = columnArticlesService.getAllAdmins();
			
			req.setAttribute("columnArticles",columnArticles);
			req.setAttribute("columnTypes",columnTypes);
//			req.setAttribute("admin",admin);
			
			RequestDispatcher dispatcher = req.getRequestDispatcher("/backend/column/edit.jsp");
			dispatcher.forward(req, res);
		} else {
			//更新後的資料送出
			columnArticles.setArtiTitle(req.getParameter("artiTitle"));
			columnArticles.setArtiContent(req.getParameter("artiContent"));
			columnArticles.setArtiStatus(Byte.valueOf(req.getParameter("artiStatus")));

			Integer colTypeId = Integer.valueOf(req.getParameter("colTypeId"));
			ColumnTypesVO colType = new ColumnTypesVO();
			colType.setColTypeId(colTypeId);
			columnArticles.setColType(colType);
			
			Part filePart = req.getPart("colImg");
			if (filePart != null && filePart.getSize() > 0) {
			    InputStream inputStream = filePart.getInputStream();
			    byte[] imageBytes = readInputStream(inputStream);
			    
			    // 檢查是否已有圖片
			    ColumnImagesVO existingImage = columnImagesService.getMainImageByArticleId(columnArticles.getArtiId());
			    if (existingImage != null) {
			        // 如果有，更新現有圖片
			        existingImage.setColImg(imageBytes);
			        columnImagesService.updateImg(existingImage);
			    } else {
			        // 如果沒有，創建新的圖片記錄
			        ColumnImagesVO newImage = new ColumnImagesVO();
			        newImage.setColumnArticles(columnArticles);
			        newImage.setIsMainImage((byte) 1);
			        newImage.setColImg(imageBytes);
			        columnImagesService.save(newImage);
			    }
			}
			columnArticlesService.updateColumnArticles(columnArticles);		
		res.sendRedirect(req.getContextPath() + "/columnmg/list");		
		}
		
	}
	
	/************ 搜尋 ************/
    protected void doSearch(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    	Integer colTypeId = null;
    	Map<String, String[]> map =req.getParameterMap();
    	
    	try {
    		colTypeId = Integer.valueOf(req.getParameter("colTypeId"));
    	} catch (NumberFormatException e) {
    		colTypeId = null;
    	}
    	List<ColumnArticlesVO> list = new ArrayList<>();
    	
		if (colTypeId != null) {
			List<ColumnArticlesVO> colTypeList = columnArticlesService.getAllColumnTypes(colTypeId);
			list.addAll(colTypeList);
		}
		
		String[] artiTitleQueries = map.get("artiTitle");
		if (artiTitleQueries != null && artiTitleQueries.length > 0 && !artiTitleQueries[0].isEmpty()) {
			List<ColumnArticlesVO> artiTitleList = columnArticlesService.getColumnArticlesByCompositeQuery(map);
			list.addAll(artiTitleList);
		}
		req.setAttribute("list", list);
		req.getRequestDispatcher("/backend/column/find.jsp").forward(req, res);
	}	
	/************ 圖片讀入DB ************/
	public byte[] readInputStream(InputStream inputStream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;

		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		return outputStream.toByteArray();
	}
}