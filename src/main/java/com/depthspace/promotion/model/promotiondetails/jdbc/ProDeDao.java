package com.depthspace.promotion.model.promotiondetails.jdbc;

import com.depthspace.promotion.model.promotiondetails.PromotionDetailsVO;

import java.util.List;

public interface ProDeDao {
    public void insert(PromotionDetailsVO pdo);
    public void update(PromotionDetailsVO pdo);
    public void delete(Integer promotionId, Integer ticketId);
    public PromotionDetailsVO findByPrimaryKey(Integer promotionId, Integer ticketId);
    public List<PromotionDetailsVO> getAll();
}
