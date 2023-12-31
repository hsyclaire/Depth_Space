package com.depthspace.ticketshoppingcart.model.jdbc;

import com.depthspace.ticketshoppingcart.model.TicketInfoVO;
import com.depthspace.ticketshoppingcart.model.TicketShoppingCartVO;

import java.util.List;

public interface TscDao {
    public void insert(TicketShoppingCartVO tsc);
    public void update(TicketShoppingCartVO tsc);
    public void delete(Integer memId, Integer ticketId);
    public void delete(Integer memId);
    public TicketShoppingCartVO findByPrimaryKey(Integer memId, Integer ticketId);
    public List<TicketShoppingCartVO> findByMemId(Integer memId);
    public List<TicketShoppingCartVO> getAll();
    public List<TicketInfoVO> getbyMemId(Integer memId);
//    public CartVO getByTicketId(Integer ticketId);
}
