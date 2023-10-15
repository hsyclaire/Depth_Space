package com.depthspace.restaurant.model.restbookingdate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.depthspace.restaurant.model.restaurant.RestVO;
import com.depthspace.utils.HibernateUtil;

public class RestBookingDateHibernateDAOImpl implements RestBookingDateDAO_interface {

	@Override
	public void insert(RestBookingDateVO restBookingDateVO) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			session.save(restBookingDateVO);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		
	}

	@Override
	public void update(RestBookingDateVO restBookingDateVO) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			List<RestBookingDateVO> query = 
					session.createQuery("FROM RestBookingDateVO WHERE :REST_ID AND :BOOKING_DATE", RestBookingDateVO.class)
					.list();
			session.update(query);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		
	}

	@Override
	public void delete(RestBookingDateVO restBookingDateVO) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			session.delete(restBookingDateVO);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		
	}

	@Override
	public RestBookingDateVO findByPrimaryKey(RestBookingDateVO restBookingDateVO) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			RestBookingDateVO restBookingDateVO1 = session.get(RestBookingDateVO.class, restBookingDateVO);
			session.getTransaction().commit();
			return restBookingDateVO1;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}

	@Override
	public List<RestBookingDateVO> getAll() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			List<RestBookingDateVO> list = session.createQuery("FROM RestBookingDateVO", RestBookingDateVO.class).list();
			session.getTransaction().commit();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}
	
}
