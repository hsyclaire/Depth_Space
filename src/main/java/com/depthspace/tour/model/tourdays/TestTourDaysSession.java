package com.depthspace.tour.model.tourdays;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.depthspace.tour.model.TourDaysVO;
import com.depthspace.utils.HibernateUtil;

public class TestTourDaysSession {

	public static void main(String[] args) {
		// 系統組態檔裡一定要有 <property
		// name="hibernate.current_session_context_class">thread</property> 的設定才可以
		Session s1 = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx1 = null;
		try {

			tx1 = s1.beginTransaction();

			TourDaysVO rest = s1.get(TourDaysVO.class, 1);
			System.out.println(rest);

			tx1.commit();

		} catch (Exception e) {
			e.printStackTrace();
			if (tx1 != null)
				tx1.rollback();
		} finally {
			HibernateUtil.shutdown();
		}
	}
}
