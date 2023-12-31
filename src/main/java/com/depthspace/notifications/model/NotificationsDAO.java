package com.depthspace.notifications.model;

import java.util.List;

public interface NotificationsDAO {

	void insert(NotificationsVO notificationsVO);
	
	void update(NotificationsVO notificationsVO);
	
	NotificationsVO getOneByNoteId(Integer noteId);
	
	List<NotificationsVO> getByMemId(Integer memeId);
	
	List<NotificationsVO> getAll();
	
	Integer getUnreadNotificationsCount(Integer memId);
}
