package com.depthspace.restaurant.model;

import java.io.Serializable;
import java.util.Date;

public class MemBookingVO implements Serializable {
	private Integer bookingId;
	private Integer restId;
	private Integer memId;
	private Integer checkStatus;
	private Integer bookingTime;
	private Integer bookingNumber;
	private Date date;

	public MemBookingVO() {
		super();
	}

	public MemBookingVO(Integer bookingId, Integer restId, Integer memId, Integer checkStatus, Integer bookingTime,
			Integer bookingNumber, Date date) {
		super();
		this.bookingId = bookingId;
		this.restId = restId;
		this.memId = memId;
		this.checkStatus = checkStatus;
		this.bookingTime = bookingTime;
		this.bookingNumber = bookingNumber;
		this.date = date;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getRestId() {
		return restId;
	}

	public void setRestId(Integer restId) {
		this.restId = restId;
	}

	public Integer getMemId() {
		return memId;
	}

	public void setMemId(Integer memId) {
		this.memId = memId;
	}

	public Integer getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(Integer checkStatus) {
		this.checkStatus = checkStatus;
	}

	public Integer getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(Integer bookingTime) {
		this.bookingTime = bookingTime;
	}

	public Integer getBookingNumber() {
		return bookingNumber;
	}

	public void setBookingNumber(Integer bookingNumber) {
		this.bookingNumber = bookingNumber;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
