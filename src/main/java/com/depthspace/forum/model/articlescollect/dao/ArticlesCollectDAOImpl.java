package com.depthspace.forum.model.articlescollect.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.depthspace.forum.model.articlescollect.ArticlesCollectVO;

public class ArticlesCollectDAOImpl implements ArticlesCollectDAO{
	private SessionFactory factory;
	
	public ArticlesCollectDAOImpl(SessionFactory factory) {
		this.factory = factory;
	}
	
	private Session getSession() {
		return factory.getCurrentSession();
	}

	@Override
	public void insert(ArticlesCollectVO entity) {
		 getSession().save(entity);
	}

	@Override
	public void delete(ArticlesCollectVO.CompositeDetail id) {
		ArticlesCollectVO acvo = getSession().get(ArticlesCollectVO.class, id);
		// 0:失敗 1:成功
		int state;
		if (acvo != null) {
			getSession().delete(acvo);

			state = 1;
		} else {

			state = 0;
		}
	}

	@Override
	public List<ArticlesCollectVO> getByMemId(Integer memId) {
		return getSession()
                .createQuery("from ArticlesCollectVO where memId= :memId", ArticlesCollectVO.class)
                .setParameter("memId", memId)
                .list();
	}

	@Override
	public boolean isCollect(Integer articleId, Integer memId) {
		List<ArticlesCollectVO> results =  getSession()
                .createQuery("from ArticlesCollectVO where articleId= :articleId AND memId= :memId", ArticlesCollectVO.class)
                .setParameter("articleId", articleId)
                .setParameter("memId", memId)
                .list();
		return !results.isEmpty();
	}
	
}
