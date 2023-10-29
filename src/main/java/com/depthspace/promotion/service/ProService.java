package com.depthspace.promotion.service;

import com.depthspace.promotion.model.promotion.PromotionTicketView;
import com.depthspace.promotion.model.promotion.PromotionVO;
import com.depthspace.promotion.model.promotion.hibernate.HbProDaoImpl;
import com.depthspace.promotion.model.promotion.hibernate.HbProDao_Interface;
import com.depthspace.promotion.model.promotiondetails.HbProDeDaoImpl;
import com.depthspace.promotion.model.promotiondetails.HbProDeDao_Interface;
import com.depthspace.promotion.model.promotiondetails.PromotionDetailsVO;
import com.depthspace.ticket.oscardao.HbTiDao;
import com.depthspace.ticketshoppingcart.model.TicketInfoVO;
import com.depthspace.ticketshoppingcart.model.TicketShoppingCartVO;
import com.depthspace.ticketshoppingcart.model.jdbc.TicketShoppingCartDAO_Interface;
import com.depthspace.ticketshoppingcart.model.jdbc.TicketShoppingCartJDBCDAO;
import com.depthspace.utils.HibernateUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProService {
    private HbProDao_Interface dao;
    private HbProDeDao_Interface proDeDao;
    public ProService(){
        dao=new HbProDaoImpl(HibernateUtil.getSessionFactory());
        proDeDao=new HbProDeDaoImpl(HibernateUtil.getSessionFactory());
    }
    //新增促銷活動並取得最新一筆促銷編號，並遍歷生成對應的多筆促銷明細
    public void addPromotion(PromotionVO entity, String[] ticketIds, BigDecimal discount){
        PromotionVO vo = null;
        if(entity != null && ticketIds !=null && discount !=null){
//            插入一筆促銷資訊
            dao.insert(entity);
//            取得最新一筆促銷編號
            vo = dao.getLatestOrder();
            Integer proId = vo.getPromotionId();
//            創建存放促銷明細的集合
            List<PromotionDetailsVO> proDeList=new ArrayList();
            //根據有促銷的票券編號數量遍歷集合，將物件宣告放進集合中
            for(String tiketId: ticketIds) {
                Integer tid = Integer.valueOf(tiketId);
                PromotionDetailsVO proDeVo = new PromotionDetailsVO(proId, tid, discount);
                proDeList.add(proDeVo);
            }
            //新增促銷對應的多筆促銷明細
            proDeDao.insertBatch(proDeList);
        }
    }
    public PromotionVO update(PromotionVO entity){
        if(entity!=null){
            dao.update(entity);
        }
        return entity;
    }
    public PromotionVO delete(Integer promotionId){
        dao.delete(promotionId);
        return null;
    }
    public List<PromotionVO> getAll(){
        List<PromotionVO> list = dao.getAll();
        return list;
    }
    //取得促銷明細列表
    public List<PromotionTicketView> getAllByProId(Integer proId){
        List<PromotionTicketView> list = proDeDao.getByProId(proId);
        return list;
    }
    //取得一筆促銷資料
    public PromotionVO getById(Integer proId){
        return dao.getById(proId);
    }
}