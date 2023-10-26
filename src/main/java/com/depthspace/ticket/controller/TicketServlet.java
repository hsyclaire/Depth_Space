package com.depthspace.ticket.controller;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.alibaba.fastjson.util.IOUtils;
import com.depthspace.account.model.account.AccountVO;
import com.depthspace.attractions.model.CityVO;
import com.depthspace.restaurant.model.restaurant.RestVO;
import com.depthspace.ticket.model.TicketImagesVO;
import com.depthspace.ticket.model.TicketTypesVO;
import com.depthspace.ticket.model.TicketVO;
import com.depthspace.ticket.service.TicketService;
import com.depthspace.ticket.service.TicketImagesService;
import com.depthspace.ticket.service.TicketImagesServiceImpl;
import com.depthspace.ticket.service.TicketServiceImpl;
import com.depthspace.ticketorders.model.ticketorders.TicketOrdersVO;

@WebServlet("/backendticket/*")
@MultipartConfig
public class TicketServlet extends HttpServlet {

	// 一個 servlet 實體對應一個 service 實體
	private TicketService ticketService;
	private TicketImagesService ticketImagesService;

	@Override
	public void init() throws ServletException {
		ticketService = new TicketServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String pathInfo = req.getPathInfo();
		if (pathInfo == null) {
			pathInfo = "";
		}

		switch (pathInfo) {
		case "/": // 票券管理頁面
			res.sendRedirect(req.getContextPath() + "/ticket/mg.jsp");
			break;
		case "/mglist": // 票券總列表
			doList(req, res);
			break;
		case "/mgadd": // 票券新增
			doAdd(req, res);
			break;
		case "/mgedit": // 票券修改頁面
			doEdit(req, res);
			break;
		case "/mgeditsuccess": // 票券更新成功
			doEditSuccess(req, res);
			break;
		case "/mgsearch": // 票券查找
			doSearch(req, res);
			break;
		case "/mgdel": // 票券刪除
			doDel(req, res);
			break;

		default:
			System.out.println("Path not handled: " + pathInfo);
		}
	}

	/************ 票券列表 ************/
	private void doList(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String page = req.getParameter("page");
		int currentPage = (page == null) ? 1 : Integer.parseInt(page);

		// 取得所有票券內容(VO)
		List<TicketVO> ticketList = ticketService.getAllTickets(currentPage);
		// 取得票券區域
//		List<TicketVO> ticketsWithCity = ticketService.getTicketsWithCity();
//		//取得票券圖片
//		List<TicketVO> ticketsWithMainImages = ticketService.getAllTicketsWithMainImages();

		if (req.getSession().getAttribute("ticketPageQty") == null) {
			int ticketPageQty = ticketService.getPageTotal();
			req.getSession().setAttribute("ticketPageQty", ticketPageQty);
		}

		req.setAttribute("ticketList", ticketList);
		req.setAttribute("currentPage", currentPage);

		RequestDispatcher dispatcher = req.getRequestDispatcher("/ticket/mgList.jsp");
		dispatcher.forward(req, res);
	}
	
	//////////////////////////////////
	
    public byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        return outputStream.toByteArray();
    }

	
	/************ 票券新增 ************/
	private void doAdd(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    if (!req.getMethod().equalsIgnoreCase("POST")) {
        //判斷如果不是POST，就是到新增頁面
    	
    	//新增頁面中要放入選單項目，要取其值，set屬性到頁面中
 		List<TicketTypesVO> ticketTypes = ticketService.getAllTicketTypes();
		List<CityVO> cities = ticketService.getAllCities();
		req.setAttribute("ticketTypes", ticketTypes);
		req.setAttribute("cities", cities);

		RequestDispatcher dispatcher = req.getRequestDispatcher("/ticket/mgAdd.jsp");
        dispatcher.forward(req, res);
    } else {
        // 完成表單填寫，按下送出觸發POST，就將下列的資料送出
		TicketVO ticket = new TicketVO();
		ticket.setTicketName(req.getParameter("ticketName"));
		ticket.setPrice(Integer.valueOf(req.getParameter("price")));
		ticket.setStock(Integer.valueOf(req.getParameter("stock")));
		ticket.setValidDays(Integer.valueOf(req.getParameter("validDays")));
		ticket.setDescription(req.getParameter("description"));
		ticket.setAddress(req.getParameter("address"));
		ticket.setLongitude(Double.valueOf(req.getParameter("longitude")));
		ticket.setLatitude(Double.valueOf(req.getParameter("latitude")));
		ticket.setStatus(Byte.valueOf(req.getParameter("status")));		
		//關聯的 縣市欄位
        Integer cityId = Integer.valueOf(req.getParameter("cityId")); //parameter 對應前端name
        CityVO city = new CityVO();
        city.setCityId(cityId);
        ticket.setCity(city);
        //關聯的 類型欄位
        Integer ticketTypeId = Integer.valueOf(req.getParameter("ticketTypeId"));
        TicketTypesVO ticketType = new TicketTypesVO();
        ticketType.setTicketTypeId(ticketTypeId);
        ticket.setTicketType(ticketType);
        
        //readInput
        Part filePart = req.getPart("ticketImage");
        InputStream inputStream = filePart.getInputStream();
        byte[] imageBytes = readInputStream(inputStream);
        
        TicketImagesVO ticketImage = new TicketImagesVO();
        ticketImage.setTicket(ticket); // 设置关联的 TicketVO 对象
        ticketImage.setImage(imageBytes); // 设置图片数据
    
		ticketImagesService.save(ticketImage); 

        ticketService.addTicket(ticket);  //上述資料存入		
        // 送出後導向以下頁面
        res.sendRedirect(req.getContextPath() + "/backendticket/mglist");
    	}
	}
        
//		byte[] byteArray = null;
//		Part filePart = req.getPart("ticketImage");
//		if(filePart != null &&  filePart.getSize() > 0) {
//			InputStream inputStream = filePart.getInputStream();
//	        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//	        int nRead;
//	        byte[] data = new byte[1024];
//	        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
//	            buffer.write(data, 0, nRead);
//	        }
//	        buffer.flush();
//	        byteArray = buffer.toByteArray();
//	        inputStream.close();
//	        buffer.close();
//		} else {
//			String webappPath = getServletContext().getRealPath("/");
//			// 构建相对路径
//			String relativeImagePath = "member/images/1.png";
//			String absoluteImagePath = webappPath + relativeImagePath;
//
//			File defaultImageFile = new File(absoluteImagePath);
//			String defaultImagePath =  defaultImageFile.getPath();
//			// 使用ServletContext获取资源流
////			InputStream defaultImageStream = getServletContext().getResourceAsStream(defaultImagePath);
//			if (defaultImageFile.exists()) {
//				byte[] localImageBytes = Files.readAllBytes(Path.of(defaultImagePath));
//				base64Image = Base64.getEncoder().encodeToString(localImageBytes);
//		        
//		        resp.setContentType("text/plain");
//		        resp.getWriter().write(base64Image);
//				req.setAttribute("base64Image", base64Image);
//			} else {
//			   // 如無照片會處理錯誤
//				System.out.println("圖不存在");
//					}
//		}


//		List<TicketVO> ticketTypes = ticketTypeService.getAllTicketTypes();
//		List<TicketVO> cities = cityService.getAllCities();
//		req.setAttribute("ticketTypes", ticketTypes);
//		req.setAttribute("cities", cities);
//		        List<TicketVO> ticketsWithCity = ticketService.getTicketsWithCity();
//		        req.setAttribute("city", ticketsWithCity);



	/************ 票券修改 ************/
	private void doEditSuccess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		Integer ticketId = Integer.valueOf(req.getParameter("ticketId"));
		TicketVO ticket = (TicketVO) ticketService.getTicketById(ticketId);

		ticket.setTicketName(req.getParameter("ticketName"));
		ticket.setPrice(Integer.valueOf(req.getParameter("price")));
		ticket.setStock(Integer.valueOf(req.getParameter("stock")));
//		Integer ticketTypeId = Integer.valueOf(req.getParameter("TICKET_TYPE_ID"));
		ticket.setValidDays(Integer.valueOf(req.getParameter("validDays")));
		ticket.setDescription(req.getParameter("description"));
		ticket.setAddress(req.getParameter("address"));
		ticket.setLongitude(Double.valueOf(req.getParameter("longitude")));
		ticket.setLatitude(Double.valueOf(req.getParameter("latitude")));
		ticket.setStatus(Byte.valueOf(req.getParameter("status")));

		req.setAttribute("ticket", ticket);
		ticketService.updateTicket(ticket);
	}

	/************ 票券搜尋 ************/
	private void doSearch(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		Map<String, String[]> map = req.getParameterMap();

		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");

		PrintWriter out = res.getWriter();

		try {
			List<TicketVO> ticketList = ticketService.getTicketsByCompositeQuery(map);

			StringBuilder json = new StringBuilder("[");
			for (TicketVO ticket : ticketList) {
				json.append(String.format(
//		                "{\"類型\": \"%s\", \"編號\": \"%s\", \"圖片\": \"%s/ticketimage?ticketId=%s\", \"名稱\": \"%s\", \"價格\": \"%s\", \"數量\": \"%s\", \"描述\": \"%s\", \"發布日\": \"%s\", \"狀況\": \"%s\", \"區域\": \"%s\"},",
						"{\"編號\": \"%s\", \"圖片\": \"%s/ticketimage?ticketId=%s\", \"名稱\": \"%s\", \"價格\": \"%s\", \"數量\": \"%s\", \"描述\": \"%s\", \"發布日\": \"%s\", \"狀況\": \"%s\"},",

						// TicketVO的屬性
//		                ticket.getTicketType(), //類型
						ticket.getTicketId(), // 編號
						req.getContextPath(), // 圖片的前半部分URL
						ticket.getTicketId(), // 圖片的ticketId參數
						ticket.getTicketName(), // 名稱
						ticket.getPrice(), // 價格
						ticket.getStock(), // 數量
						ticket.getDescription(), // 描述
						ticket.getPublishedDate(), // 發布日
						ticket.getStatus() // 狀況
//		                ticket.getCity().getCityName() //區域
				));
			}
			json = new StringBuilder(json.toString().replaceAll(",$", ""));
			json.append("]");

			out.print(json);

		} catch (Exception e) {
			e.printStackTrace();
			out.print("[]");
		} finally {
			out.flush();
		}
	}


	/************ 票券修改 ************/
	private void doEdit(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.sendRedirect(req.getContextPath() + "/ticket/mgEdit.jsp");
	}

	/************ 票券刪除 ************/
	private void doDel(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Integer ticketId = Integer.valueOf(req.getParameter("ticketId"));
		ticketService.deleteTicket(ticketId);
	}
	
}
