package com.depthspace.tour.service;

import java.util.List;

import com.depthspace.tour.model.tour.TourVO;

public interface TourService_Interface {
	int insert(TourVO entity);
	int update(TourVO entity);
	int delete(Integer tourId);
	
	List<TourVO> getAll();
	//取得該會員行程資料
	List<TourVO> getByMemId(Integer MemId);
}