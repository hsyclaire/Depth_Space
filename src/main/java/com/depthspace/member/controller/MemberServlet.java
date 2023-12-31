package com.depthspace.member.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.mindrot.jbcrypt.BCrypt;

import com.depthspace.attractions.model.AreaVO;
import com.depthspace.attractions.model.CityVO;
import com.depthspace.attractions.service.AreaService;
import com.depthspace.attractions.service.CityService;
import com.depthspace.member.model.MemVO;
import com.depthspace.member.service.HbMemService;
import com.depthspace.member.service.MemberService;
import com.depthspace.ticketshoppingcart.service.RedisCartServiceImpl;
import com.depthspace.utils.JedisUtil;
import com.depthspace.utils.MailService;
import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

@WebServlet({ "/mem/*" })
@MultipartConfig
public class MemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private RedisCartServiceImpl carSv;
	private HbMemService hbms;

	@Override
	public void init() throws ServletException {
		carSv = new RedisCartServiceImpl(JedisUtil.getJedisPool());
		hbms = new HbMemService();
	}

	public int allowUser(String memAcc, String password) {
		MemVO memvo = null;
		MemberService mems = new MemberService();

		// 檢查是否存在該帳號
		if (hbms.findByMemAcc(memAcc) == null) {
			System.out.println("沒有此帳號");
			return 1; // 返回一個特定的錯誤碼表示帳號不存在
		} else {
			memvo = mems.getMemberInfo(memAcc);
			System.out.println("memvo= " + memvo);
		}
		System.out.println("我會發瘋=" +BCrypt.checkpw(password, memvo.getMemPwd()));

		// 新增的密碼鹽值格式檢查
		if (memvo != null && memvo.getMemPwd() != null && memvo.getMemPwd().startsWith("$2a$")) {
			if (BCrypt.checkpw(password, memvo.getMemPwd())) {
				
				// 檢查帳號狀態
				if (memvo.getAccStatus() == 2) {
					return 5; // 帳號狀態問題
				} else {
					return 3; // 登入成功
				}
			} else {
				System.out.println("密碼錯誤");
				return 4; // 密碼錯誤
			}
		} else {
			System.out.println("儲存的密碼鹽值格式不正確");
			return 6; // 或其他錯誤碼
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");
		String pathInfo = req.getPathInfo();
		switch (pathInfo) {
		case "/login":// 登入
			doLogin(req, resp);
			break;
		case "/logout":// 登出
			doLogout(req, resp);
			break;
		case "/edit":// 修改會員資料
			doEdit(req, resp);
			break;
		case "/modify":// 儲存修改後的資料
			doModify(req, resp);
			break;
		case "/save":// 註冊會員
			doSave(req, resp);
			break;
		case "/memList":// 從首頁點擊我的會員資料時
			doMemList(req, resp);
			break;
		case "/forgetPassword":// 忘記密碼
			doForgetPassword(req, resp);
			break;
		case "/checkVerify":// 忘記密碼的驗證碼
			doCheckVerify(req, resp);
			break;
		case "/checkAccount":// 確認重複帳號
			doCheckAccount(req, resp);
			break;
		case "/signIn":// 註冊帳號把縣市值帶過去
			doSignIn(req, resp);
			break;
		case "/changePassword":// 變更密碼
			doChangePassword(req, resp);
			break;
		}

	}

	private void doChangePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession(false);
		Integer memId = null;
		memId = (Integer) session.getAttribute("memId");
//		System.out.println("memId=" + memId);
		MemVO memvo = hbms.getOneMem(memId);

		String changePassword = req.getParameter("changePassword");
		System.out.println("changePassword= " + changePassword);
		// 將更換過的密碼加密
		String hashedPassword = BCrypt.hashpw(changePassword, BCrypt.gensalt());

		memvo.setMemPwd(hashedPassword);
		//更新密碼
		hbms.update(memvo);

		// 再存一次session
		MemVO mem = hbms.getOneMem(memId);
		System.out.println("mem= " + mem);

		String base64Image;

		byte[] imageBytes = mem.getMemImage();
		if (imageBytes != null) {
			base64Image = Base64.getEncoder().encodeToString(imageBytes);
			session.setAttribute("base64Image", base64Image);
		} else {
			String webappPath = getServletContext().getRealPath("/");
			// 取得相對路径
			String relativeImagePath = "member/images/1.png";
			String absoluteImagePath = webappPath + relativeImagePath;

			File defaultImageFile = new File(absoluteImagePath);
			String defaultImagePath = defaultImageFile.getPath();
			if (defaultImageFile.exists()) {
				byte[] localImageBytes = Files.readAllBytes(Path.of(defaultImagePath));
				base64Image = Base64.getEncoder().encodeToString(localImageBytes);

				resp.setContentType("text/plain");
				resp.getWriter().write(base64Image);
//				req.setAttribute("base64Image", base64Image);
				session.setAttribute("base64Image", base64Image);
			} else {
				// 無照片處理錯誤
				System.out.println("圖不存在");
			}
		}

		// 設定男女顯示
		byte memSexBytes = mem.getMemSex();
		if (memSexBytes == 1) {
			session.setAttribute("sex", "男");
		} else if (memSexBytes == 2) {
			session.setAttribute("sex", "女");
		}
		// 設定狀態顯示
		byte accStatus = mem.getAccStatus();
		if (accStatus == 1) {
			session.setAttribute("status", "正常使用中");
		} else {
			session.setAttribute("status", "此帳號停權");
		}

		session.setAttribute("authenticatedMem", mem);// 會員物件
		session.setAttribute("memId", mem.getMemId());// 會員編號

		resp.getWriter().write("success");
	}

	private void doSignIn(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		CityService cityService = new CityService();
//		AreaService areaService
		List<CityVO> city = cityService.getAll();
//		List<AreaVO> area = areaService.getAll();
		req.setAttribute("city", city);
		req.getRequestDispatcher("/member/addMember.jsp").forward(req, resp);

	}

	private void doCheckAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String memAcc = req.getParameter("memAcc");
//		System.out.println("memAcc=" + memAcc);
//		HbMemService hbms = new HbMemService();
		List<MemVO> list = hbms.getAll();
		boolean account = false;

		for (MemVO memVO : list) {
			String memAllAcc = memVO.getMemAcc();

			if (memAcc != null && memAcc.equals(memAllAcc)) {
				account = true;
				break; // 找到相符的帳號後即可跳出迴圈
			}

		}

		if (account) {
			String data = "false";
			setJsonResponse(resp, data);
			System.out.println("帳號已存在，帳號不可使用");
		} else {
			String data = "true";
			setJsonResponse(resp, data);
			System.out.println("無此帳號，帳號可用");
		}

	}

	private void doCheckVerify(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String memAcc = req.getParameter("memAcc");
		String memEmail = req.getParameter("memEmail");
		String password = req.getParameter("password");

		// 在這裡應該要去Redis取得驗證碼的值，並進行比對
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.select(14); // 切換到第14個資料庫，請確保這是你存放驗證碼的資料庫

		String redisKey = jedis.get(memEmail);
//	    System.out.println("redisKey= " + redisKey + "," + "password= "+ password);
		if (redisKey != null && redisKey.equals(password)) {

			// ===================================================
			HttpSession session = req.getSession();
			MemberService ms = new MemberService();
			MemVO mem = ms.getMemberInfo(memAcc);
//			System.out.println("mem=" + mem);
			String base64Image;

			byte[] imageBytes = mem.getMemImage();
			if (imageBytes != null) {
				base64Image = Base64.getEncoder().encodeToString(imageBytes);
				session.setAttribute("base64Image", base64Image);
			} else {
				String webappPath = getServletContext().getRealPath("/");
				// 取得相對路径
				String relativeImagePath = "member/images/1.png";
				String absoluteImagePath = webappPath + relativeImagePath;

				File defaultImageFile = new File(absoluteImagePath);
				String defaultImagePath = defaultImageFile.getPath();
				if (defaultImageFile.exists()) {
					byte[] localImageBytes = Files.readAllBytes(Path.of(defaultImagePath));
					base64Image = Base64.getEncoder().encodeToString(localImageBytes);

					resp.setContentType("text/plain");
					resp.getWriter().write(base64Image);
//					req.setAttribute("base64Image", base64Image);
					session.setAttribute("base64Image", base64Image);
				} else {
					// 無照片處理錯誤
					System.out.println("圖不存在");
				}
			}

			// 設定男女顯示
			byte memSexBytes = mem.getMemSex();
			if (memSexBytes == 1) {
				session.setAttribute("sex", "男");
			} else if (memSexBytes == 2) {
				session.setAttribute("sex", "女");
			}
			// 設定狀態顯示
			byte accStatus = mem.getAccStatus();
			if (accStatus == 1) {
				session.setAttribute("status", "正常使用中");
			} else {
				session.setAttribute("status", "此帳號停權");
			}

			session.setAttribute("authenticatedMem", mem);// 會員物件
			session.setAttribute("memId", mem.getMemId());// 會員編號
			// ===================================================

			// 在這裡清除Redis中的驗證碼，因為已經使用過了
			jedis.del(memEmail);
			jedis.close();

			// 回傳成功訊息
			resp.getWriter().write("success");
		} else {
			// 驗證碼錯誤，回傳錯誤訊息
			resp.getWriter().write("error");
		}

	}

	// 用ajax傳遞請求
	private void doForgetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String memAcc = req.getParameter("memAcc");
		String memEmail = req.getParameter("memEmail");

//		HbMemService hbms = new HbMemService();
		MemVO mem = hbms.findByMemAcc(memAcc);
		if (mem == null) {
			String URL = req.getContextPath() + "/member/forgetPassword.jsp?error=true";
			resp.sendRedirect(URL);
		} else if (mem.getMemAcc().equals(memAcc) && mem.getMemEmail().equals(memEmail)) {
			String to = mem.getMemEmail();
			String subject = "DepthSpace會員密碼通知函";
			String ch_name = mem.getMemName();
			String random = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			// 生成驗證碼
			String passRandom = "";
			for (int i = 1; i <= 8; i++) {
				int b = (int) (Math.random() * 62);
				passRandom = passRandom + random.charAt(b);
			}

			// 將臨時密碼加密
			String hashedPassword = BCrypt.hashpw(passRandom, BCrypt.gensalt());

			mem.setMemPwd(hashedPassword);
			hbms.update(mem);

//			System.out.println("驗證碼為:"+ passRandom);
//			存到Redis裡面
			Jedis jedis = new Jedis("localhost", 6379);
			// 切換到第14個資料庫
			jedis.select(14);
			jedis.set(to, passRandom);
			jedis.expire(to, 600);

			jedis.close();

			// 寄到電子信箱
//			String messageText = "Hello! " + ch_name + " 請謹記此密碼: " + passRandom;
			// 使用MimeMultipart 將HTML放入MimeBodyPart中
			Multipart multipart = new MimeMultipart();
			MimeBodyPart bodyPart = new MimeBodyPart();
			// 將要發送的內容用HTML的格式
			StringBuffer msg = new StringBuffer();
			msg.append("Hello! " + ch_name + "您的臨時密碼為" + passRandom + "， 請謹記此密碼，並在10分鐘內驗證完畢。");
			try {
				bodyPart.setContent(msg.toString(), "text/html; charset=UTF-8");
				multipart.addBodyPart(bodyPart);
			} catch (MessagingException e) {
				e.printStackTrace();
			}

			MailService mailService = new MailService();
			try {
				mailService.sendMail(to, subject, multipart);
//				System.out.println("驗證碼已寄出");
			} catch (Exception e) {
				System.out.println("驗證碼寄出失敗");
			}

			resp.getWriter().write("success");
		} else {
			resp.getWriter().write("error");
		}
	}

	private void doMemList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer memId = null;
		HttpSession session = req.getSession(false);
		memId = (Integer) session.getAttribute("memId");
		HbMemService hbms = new HbMemService();
		MemVO mem = hbms.getOneMem(memId);

		// 處理圖片
		String base64Image;
		byte[] imageBytes = mem.getMemImage();
		if (imageBytes != null) {
			base64Image = Base64.getEncoder().encodeToString(imageBytes);
			req.setAttribute("base64Image", base64Image);
		} else {
			String webappPath = getServletContext().getRealPath("/");
			// 取得相對路径
			String relativeImagePath = "member/images/1.png";
			String absoluteImagePath = webappPath + relativeImagePath;

			File defaultImageFile = new File(absoluteImagePath);
			String defaultImagePath = defaultImageFile.getPath();
			// 使用ServletContext获取资源流
//			InputStream defaultImageStream = getServletContext().getResourceAsStream(defaultImagePath);
			if (defaultImageFile.exists()) {
				byte[] localImageBytes = Files.readAllBytes(Path.of(defaultImagePath));
				base64Image = Base64.getEncoder().encodeToString(localImageBytes);

				resp.setContentType("text/plain");
				resp.getWriter().write(base64Image);
				req.setAttribute("base64Image", base64Image);
			} else {
				// 如無照片會處理錯誤
				System.out.println("圖不存在");
			}
		}

		if (mem.getMemSex() == 1) {
			req.setAttribute("sex", "男");
		} else {
			req.setAttribute("sex", "女");
		}

		if (mem.getAccStatus() == 1) {
			req.setAttribute("status", "正常使用中");
		} else {
			req.setAttribute("status", "此帳號停權");
		}

		req.setAttribute("authenticatedMem", mem);
		req.getRequestDispatcher("/member/success.jsp").forward(req, resp);

	}

	private void doLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession();
		// 移除session
		if (session.getAttribute("authenticatedMem") != null) {
			session.removeAttribute("authenticatedMem");
		}
		if (session.getAttribute("memId") != null) {
			session.removeAttribute("memId");
		}
		if (session.getAttribute("mtoPageQty") != null) {
			session.removeAttribute("mtoPageQty");
		}
		if (session.getAttribute("toMemPageQty") != null) {
			session.removeAttribute("toMemPageQty");
		}

//		Integer memno = (Integer) session.getAttribute("memId");// 測試用(取得存在session會員編號)
//	    System.out.println("測試取得放入session的會員編號" + memno);
		resp.sendRedirect(req.getContextPath() + "/indexpage/index.jsp?state=logout");

	}

	// ============================================================================================================================================
	// 註冊
	private void doSave(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<String> errorMsgs = new LinkedList<String>();
		req.setAttribute("errorMsgs", errorMsgs);

		// 處理地址需要用到
		CityService cityService = new CityService();
		AreaService areaService = new AreaService();

		String st2 = null;
		String st3 = null;
		String st4 = null;
		String st5 = null;
		java.sql.Date st6 = null;
		Byte st7 = null;
		String st8 = null;
		Integer st9 = null;
		Integer cityId;
		Integer areaId;
		String st10 = null;
		Byte st11 = null;
		String base64Image;
		byte[] byteArray = null;
		// 處理會員點數為0(新會員無點數)
		Integer st12 = 0;

		try {
			st2 = req.getParameter("memAcc");
			if (st2 == null || st2.trim().length() == 0) {
				errorMsgs.add("帳號請勿空白");
			}

			st3 = req.getParameter("memPwd");
			if (st3 == null || st3.trim().length() == 0) {
				errorMsgs.add("密碼請勿空白");
			} else {
				// 使用 bcrypt 進行密碼加密
				String hashedPassword = BCrypt.hashpw(st3, BCrypt.gensalt());
				st3 = hashedPassword;
			}

			st4 = req.getParameter("memName");
			if (st4 == null || st4.trim().length() == 0) {
				errorMsgs.add("姓名請勿空白");
			}

			st5 = req.getParameter("memIdentity");
			if ((st5 == null || st5.trim().length() == 0) && st5.trim().length() != 10) {
				errorMsgs.add("身分證請勿空白或不等於10位數");
			}

			String memBth = req.getParameter("memBth");
			if (memBth != null && !memBth.isEmpty()) {
				try {
					st6 = java.sql.Date.valueOf(memBth);
				} catch (IllegalArgumentException e) {
					errorMsgs.add("生日請勿空白");
				}
			}

			String memSex = req.getParameter("memSex");
			st7 = Byte.parseByte(memSex);

			st8 = req.getParameter("memEmail");
			if (st8 == null || st8.trim().length() == 0) {
				errorMsgs.add("Email請勿空白");
			}

			String memTel = req.getParameter("memTel");
			System.out.println("memTel=" + memTel);
			if (memTel.startsWith("0")) {
				memTel = memTel.substring(1);
			}
			st9 = Integer.valueOf(memTel);
			if (st9 == null) {
				errorMsgs.add("電話請勿空白");
			}

			// 處理地址
			cityId = Integer.valueOf(req.getParameter("city"));
			areaId = Integer.valueOf(req.getParameter("area"));
			String city = cityService.findByPrimaryKey(cityId).getCityName();
			String area = areaService.findByPrimaryKey(areaId).getAreaName();
			st10 = city + area + req.getParameter("memAdd");
			if (st10 == null || st10.trim().length() == 0) {
				errorMsgs.add("地址請勿空白");
			}
			String accStatus = req.getParameter("accStatus");
			st11 = Byte.parseByte(accStatus);

			// 抓取上傳資料並且做base64的轉型
			Part filePart = req.getPart("memImage");
			if (filePart != null && filePart.getSize() > 0) {
				InputStream inputStream = filePart.getInputStream();
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int nRead;
				byte[] data = new byte[1024];
				while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}
				buffer.flush();
				byteArray = buffer.toByteArray();
				inputStream.close();
				buffer.close();
			} else {
				String webappPath = getServletContext().getRealPath("/");
				// 构建相對路径
				String relativeImagePath = "member/images/1.png";
				String absoluteImagePath = webappPath + relativeImagePath;

				File defaultImageFile = new File(absoluteImagePath);
				String defaultImagePath = defaultImageFile.getPath();
				// 使用ServletContext獲取資源流
//				InputStream defaultImageStream = getServletContext().getResourceAsStream(defaultImagePath);
				if (defaultImageFile.exists()) {
					byte[] localImageBytes = Files.readAllBytes(Path.of(defaultImagePath));
					base64Image = Base64.getEncoder().encodeToString(localImageBytes);

					resp.setContentType("text/plain");
					resp.getWriter().write(base64Image);
					req.setAttribute("base64Image", base64Image);
				} else {
					// 如無照片會處理錯誤
					System.out.println("圖不存在");
				}
			}

			// =======================================================================
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

		} catch (NumberFormatException e) {
			e.printStackTrace();
			return;
		}

		// 放入物件
		MemberService m = new MemberService();
//		MemVO memvo = null;
//		if (errorMsgs.isEmpty()) {
//			memvo = new MemVO(st2, st3, st4, st5, st6, st7, st8, st9, st10, st11, byteArray);
//
//			m.addMember(memvo);
		// =======================================================================================
		MemVO memvo = null;
		// 用hibernate寫法加入會員
		if (errorMsgs.isEmpty()) {

			Integer newMemId = null;
			memvo = new MemVO(newMemId, st2, st3, st4, st5, st6, st7, st8, st9, st10, st11, st12, byteArray);
			hbms.insert(memvo);

			// 抓memId值讓修改頁面可以過
			MemVO mem = new MemVO();
			Integer memId;
			String email = memvo.getMemEmail();
//		System.out.println("email=" + email);
			List<MemVO> a = m.getAll();
			int lastIndex = a.size() - 1; // 找到最後一個元素的索引
			mem = a.get(lastIndex); // 獲得最後一個元素的值
//		System.out.println("mem=" + mem);
			memId = mem.getMemId();
//		System.out.println(memId);
			req.setAttribute("memId", memId);
//		authenticatedMem.setMemId(mem.getMemId());
//		try {
//			memId = mem.getMemId();
//			System.out.println(memId);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			return;
//		}

			// =======================================================================================
			req.setAttribute("authenticatedMem", memvo);

			// 判斷是否要給預設圖
			if (byteArray != null) {
				String base64Image2 = Base64.getEncoder().encodeToString(byteArray);
				req.setAttribute("base64Image", base64Image2);
			} else {// 讓他保持預設圖
			}

			// 判斷男女
			if (st7 == 1) {
				req.setAttribute("sex", "男");
			} else {
				req.setAttribute("sex", "女");
			}

			// 判斷帳戶
			if (st11 == 1) {
				req.setAttribute("status", "正常使用中");
			} else {
				req.setAttribute("status", "此帳號停權");
			}

			req.getRequestDispatcher("/member/login.jsp").forward(req, resp);
		} else {
			String revise = "請修正以下資訊";
			req.setAttribute("errorMsgs", errorMsgs);
			req.setAttribute("revise", revise);
			RequestDispatcher failureView = req.getRequestDispatcher("/member/addmember.jsp");
			failureView.forward(req, resp);
		}
	}

	// 儲存修改後的資料
	private void doModify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 錯誤存list
		List<String> errorMsgs = new LinkedList<String>();
		req.setAttribute("errorMsgs", errorMsgs);
		// 處理地址用
		CityService cityService = new CityService();
		AreaService areaService = new AreaService();

		Integer st1 = null;
		String st2 = req.getParameter("memAcc");
//		System.out.println("st2= " + st2);
//		String st3 = null;
		String pwd = null;
		String st4 = null;
		String st5 = null;
		java.sql.Date st6 = null;
		Byte st7 = null;
		String st8 = null;
		Integer st9 = null;
		Integer cityId;
		Integer areaId;
		String st10 = null;
		Byte st11 = null;
		Integer st12 = null;
		byte[] st13 = null;

		try {
			// 多寫的，先暫時當二次判斷使用
			String memId = req.getParameter("memId");
			if (memId == null) {
				errorMsgs.add("帳號請勿空白");
			} else {
				st1 = Integer.valueOf(memId);
			}
//			System.out.println("st1=" + st1);

//			st2 = req.getParameter("memAcc");
			System.out.println("帳號= " + st2);
			if (st2 == null || st2.trim().length() == 0) {
				errorMsgs.add("帳號請勿空白");
			}
			
			//處理密碼
			HttpSession session = req.getSession(false);
			Integer sessionmemId = (Integer) session.getAttribute("memId");
			HbMemService hbms = new HbMemService();
			System.out.println("sessionmemId=" + sessionmemId);
			//此頁面無修改密碼邏輯 所以只要取出資料就好
			pwd = hbms.getOneMem(sessionmemId).getMemPwd();
//			String pwd = memvo.getMemPwd();
//			st3 = req.getParameter("memPwd");
//			if (st3 == null || st3.trim().length() == 0) {
//				errorMsgs.add("密碼請勿空白");
//			} else {
				
//				String hashedPassword = BCrypt.hashpw(pwd, BCrypt.gensalt());
//				pwd = hashedPassword;
//			}

			st4 = req.getParameter("memName");
			if (st4 == null || st4.trim().length() == 0) {
				errorMsgs.add("姓名請勿空白");
			}

			st5 = req.getParameter("memIdentity");
			if (st5 == null || st5.trim().length() == 0) {
				errorMsgs.add("身分證請勿空白");
			}

			st6 = null;
			String memBth = req.getParameter("memBth");
			if (memBth != null && !memBth.isEmpty()) {
				try {
					st6 = java.sql.Date.valueOf(memBth);
				} catch (IllegalArgumentException e) {
					errorMsgs.add("生日請勿空白");
				}
			}

			String memSex = req.getParameter("memSex");
			if ("男".equals(memSex)) {
				st7 = 1; // 如果 memSex 是 "男"，則將 memSexByte 設置為 1
			} else {
				st7 = 2; // 否則，將 memSexByte 設置為 2
			}

			st8 = req.getParameter("memEmail");
			if (st8 == null || st8.trim().length() == 0) {
				errorMsgs.add("Email請勿空白");
			}

			String memTel = req.getParameter("memTel");
			System.out.println("memTel=" + memTel);
			if (memTel.startsWith("0")) {
				memTel = memTel.substring(1);
			}
			if (memTel == null) {
				errorMsgs.add("電話請勿空白");
			} else {
				st9 = Integer.valueOf(memTel);
			}

			// 處理地址
			cityId = Integer.valueOf(req.getParameter("city"));
			areaId = Integer.valueOf(req.getParameter("area"));
			String city = cityService.findByPrimaryKey(cityId).getCityName();
			String area = areaService.findByPrimaryKey(areaId).getAreaName();
//			String newAddress = req.getParameter("newAddress");
			st10 = city + area + req.getParameter("memAdd");
			if (st10 == null || st10.trim().length() == 0) {
				errorMsgs.add("地址請勿空白");
			}
			String accStatus = req.getParameter("accStatus");
			if ("正常使用中".equals(accStatus)) {
				st11 = 1;
			} else {
				st11 = 2;
			}

			MemberService ms = new MemberService();
			MemVO mem = ms.findByMemId(st1);
			st12 = mem.getMemPoint();

//			修改圖片
			Part filePart = req.getPart("memImage");
//			System.out.println("filePart=" + filePart);
			if (filePart != null && filePart.getSize() > 0) {
				InputStream inputStream = filePart.getInputStream();
				st13 = new byte[inputStream.available()];
				inputStream.read(st13);
				inputStream.close();
			} else {
				st13 = mem.getMemImage();
			}

//	        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//	        int nRead;
//	        byte[] data = new byte[1024];
//	        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
//	            buffer.write(data, 0, nRead);
//	        }
//	        buffer.flush();
//	        st13 = buffer.toByteArray();
//	        System.out.println("st13=" + st13);
//	        System.out.println("st13.length=" + st13.length);
//	        inputStream.close();
//	        buffer.close();

		} catch (NumberFormatException e) {
			e.printStackTrace();
			return;
		}
//		MemVO memvo = hbms.getOneMem(sessionmemId);
		//取得原本物件,hbms.getOneMem(st1)
		MemVO memvo = hbms.getOneMem(st1);
		
		memvo.setMemId(st1);
		memvo.setMemAcc(st2);
		memvo.setMemPwd(pwd);
		memvo.setMemName(st4);
		memvo.setMemIdentity(st5);
		memvo.setMemBth(st6);
		memvo.setMemSex(st7);
		memvo.setMemEmail(st8);
		memvo.setMemTel(st9);
		memvo.setMemAdd(st10);
		memvo.setAccStatus(st11);
//		memvo.setMemId(st12);
		memvo.setMemImage(st13);
		
		hbms.update(memvo);
		
//		MemberService m = new MemberService();
//		if (errorMsgs.isEmpty()) {
//		memvo = new MemVO(st1, st2, pwd, st4, st5, st6, st7, st8, st9, st10, st11, st12, st13);
//		}
//		m.updateMember(memvo);
		
		HttpSession session = req.getSession(false);
//		Integer memId = null;
//		memId = (Integer) session.getAttribute("memId");
//		req.setAttribute("authenticatedMem", memvo);
		session.setAttribute("authenticatedMem", memvo);

//		st13 = mem.getMemImage();
//		String base64Image = Base64.getEncoder().encodeToString(st13);
//		System.out.println("base64Image=" + base64Image);
//		req.setAttribute("base64Image", base64Image);

//		byte[] imageBytes = memvo.getMemImage();
//		System.out.println("byte[]："+imageBytes);
//		String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//		req.setAttribute("base64Image", base64Image);
//		req.setAttribute("imageBytes", imageBytes);

		String base64Image = Base64.getEncoder().encodeToString(st13);
		req.setAttribute("base64Image", base64Image);

		if (st7 == 1) {
			req.setAttribute("sex", "男");
		} else {
			req.setAttribute("sex", "女");
		}

		if (st11 == 1) {
			req.setAttribute("status", "正常使用中");
		} else {
			req.setAttribute("status", "此帳號停權");
		}

		req.getRequestDispatcher("/member/success.jsp").forward(req, resp);
	}

	// ============================================================================================================================================

	private void doAddMember(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 加入會員資料收集
//		System.out.println("有跳");
		req.getRequestDispatcher("/member/success.jsp").forward(req, resp);
	}

	// ============================================================================================================================================

	private void doEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		MemberService mems = new MemberService();
//		Integer memId = null;
//		String st1 = req.getParameter("memId");
		HttpSession session = req.getSession(false);
		Integer memId = null;
		memId = (Integer) session.getAttribute("memId");
		System.out.println("memId=" + memId);
		MemVO memvo = hbms.getOneMem(memId);
		System.out.println("memvo=" + memvo);

//		if (memId != null && ! memId.isEmpty()) {
			try {
				memId = Integer.valueOf(memId);
				req.setAttribute("memId", memId);
			} catch (NumberFormatException e) {
				// 轉換失敗時的處理
				e.printStackTrace();
				return;
			}
//		} else {
//			MemVO mem = new MemVO();
//
//			List<MemVO> a = mems.getAll();
//			int lastIndex = a.size() - 1; // 找到最後一個元素的索引
//			mem = a.get(lastIndex); // 獲得最後一個元素的值
//			memId = mem.getMemId();
//			req.setAttribute("memId", memId);
//		}

//		MemVO memvo = mems.findByMemId(memId);
//		System.out.println("找的會員資料：" + memvo);
		if (memvo != null) {
			// 處理圖片
			byte[] imageBytes = memvo.getMemImage();
			if (imageBytes != null) {
				String base64Image = Base64.getEncoder().encodeToString(imageBytes);
				session.setAttribute("imageBytes", imageBytes);
				session.setAttribute("base64Image", base64Image);
			} else {
				req.setAttribute("base64Image", null); // 如果 memImage 為 null，設定 base64Image 為 null
			}
			// 處理性別
			byte memSex = memvo.getMemSex();
			if (memSex == 1) {
				req.setAttribute("sex", "男");
			} else {
				req.setAttribute("sex", "女");
			}

			// 處理狀態
			byte accStatus = memvo.getAccStatus();
			if (accStatus == 1) {
				req.setAttribute("status", "正常使用中");
			} else {
				req.setAttribute("status", "此帳號停權");
			}
			// 處理地址
			CityService cityService = new CityService();
			AreaService areaService = new AreaService();
			List<CityVO> city = cityService.getAll();
//			List<AreaVO> area = areaService.getAll();
			req.setAttribute("city", city);

			if (memvo.getMemAdd().length() > 6) {
				String newAddress = memvo.getMemAdd().substring(6);
//			    System.out.println(newAddress);
				req.setAttribute("newAddress", newAddress);
			} else {
				// 如果字串的長度小於或等於六，可以處理相應的邏輯，例如返回原始字串或顯示錯誤訊息
				System.out.println("字串長度不足六");
			}

			// 要把會員的地址前三個字拿出來
			String newAddress = memvo.getMemAdd();
			String cityName = newAddress.substring(0, Math.min(newAddress.length(), 3));
			// 用比對方式去把id抓到
			CityVO cvo = cityService.findByCityName(cityName);
			// 再顯示到前端
			req.setAttribute("cvo", cvo);
			// 透過cvo.getCityId找到對應的區域
			AreaVO avo = areaService.getOneCity(cvo.getCityId());
			req.setAttribute("avo", avo);
			// 顯示到前端
			// 找到對應的areaId
			List<AreaVO> area = areaService.getAllArea(cvo.getCityId());
			req.setAttribute("area", area);

			req.setAttribute("mem", memvo);
			req.getRequestDispatcher("/member/revise.jsp").forward(req, resp);
		} else {
			System.out.println("失敗QQ");
		}
	}

	// ========================================================================================

	protected void doLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String memAcc = req.getParameter("memAcc");
		String password = req.getParameter("password");
		MemberService ms = new MemberService();
		HbMemService hms = new HbMemService();
		MemVO memVo = null;

		String loginLocation = req.getParameter("loginLocation");
		System.out.println("loginLocation=" + loginLocation);
//		System.out.println("存session成功"+ "memAcc= " + memAcc );
		
		int allowUser = allowUser(memAcc, password);

		if (allowUser == 1) {
			System.out.println("沒有此帳號");
			String URL = req.getContextPath() + "/member/login.jsp?error=false&requestURI=" + loginLocation;
			resp.sendRedirect(URL);
			return;
		} else if (allowUser == 3) {
			HttpSession session = req.getSession();

			MemVO mem = ms.getMemberInfo(memAcc);
//			System.out.println("mem=" + mem);
			String base64Image;
//			if (mem.getMemAcc().equals(memAcc) && mem.getMemPwd().equals(password)) {

			byte[] imageBytes = mem.getMemImage();
			if (imageBytes != null) {
				base64Image = Base64.getEncoder().encodeToString(imageBytes);
				req.setAttribute("base64Image", base64Image);
			} else {
				String webappPath = getServletContext().getRealPath("/");
				// 取得相對路径
				String relativeImagePath = "member/images/1.png";
				String absoluteImagePath = webappPath + relativeImagePath;

				File defaultImageFile = new File(absoluteImagePath);
				String defaultImagePath = defaultImageFile.getPath();
				if (defaultImageFile.exists()) {
					byte[] localImageBytes = Files.readAllBytes(Path.of(defaultImagePath));
					base64Image = Base64.getEncoder().encodeToString(localImageBytes);

					resp.setContentType("text/plain");
					resp.getWriter().write(base64Image);
					req.setAttribute("base64Image", base64Image);
				} else {
					// 無照片處理錯誤
					System.out.println("圖不存在");
				}
			}

			// 設定男女顯示
			byte memSexBytes = mem.getMemSex();
			if (memSexBytes == 1) {
				req.setAttribute("sex", "男");
			} else if (memSexBytes == 2) {
				req.setAttribute("sex", "女");
			}
			// 設定狀態顯示
			byte accStatus = mem.getAccStatus();
			if (accStatus == 1) {
				req.setAttribute("status", "正常使用中");
			} else {
				req.setAttribute("status", "此帳號停權");
			}

			session.setAttribute("authenticatedMem", mem);// 會員物件
			session.setAttribute("memId", mem.getMemId());// 會員編號

//			int cartNum = carSv.getCartNum(mem.getMemId());
//			session.setAttribute("cartNum", cartNum);
//			System.out.println("購物車品項數量="+session.getAttribute("cartNum"));

			Integer memno = (Integer) session.getAttribute("memId");// 測試用(取得存在session會員編號)
//		    System.out.println("測試取得放入session的會員編號" + memno);// 測試用

//			req.getRequestDispatcher("/member/success.jsp").forward(req, resp);
			req.getRequestDispatcher("/indexpage/index.jsp").forward(req, resp);
//			resp.sendRedirect(req.getContextPath()+"/indexpage/index.jsp");
		} else if (allowUser == 4) {
			String URL = req.getContextPath() + "/member/login.jsp?error=true&requestURI=" + loginLocation;
			resp.sendRedirect(URL);
			return;// 程式中斷
		} else if (allowUser == 5) {
			String URL = req.getContextPath() + "/member/login.jsp?error=nostatus&requestURI=" + loginLocation;
			resp.sendRedirect(URL);
		} else if (allowUser == 6) {
			String URL = req.getContextPath() + "/member/login.jsp?error=nostatus&requestURI=" + loginLocation;
			resp.sendRedirect(URL);
		}
	}

	// fetch返回json格式
	private void setJsonResponse(HttpServletResponse resp, Object obj) throws IOException {
		Gson gson = new Gson();
		String jsonData = gson.toJson(obj);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(jsonData);
	}
}
