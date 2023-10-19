package com.depthspace.ticket.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TICKET_IMAGES")
public class TicketImagesVO implements Serializable {

	@Id
	@Column(name="SERIAL_ID" , updatable=false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Integer serialId; 
	
	@Column(name="TICKET_ID")
	private Integer ticketId; 
	
	@Column(name="IMAGE", columnDefinition = "mediumblob")
	private byte[] image; 
	
	@Column(name = "IS_MAIN_IMAGE")
	private byte isMainImage;


    public TicketImagesVO() {
    }


    public TicketImagesVO(Integer serialId, Integer ticketId, byte[] image, byte isMainImage) {
		super();
		this.serialId = serialId;
		this.ticketId = ticketId;
		this.image = image;
		this.isMainImage = isMainImage;
	}



	public Integer getSerialId() {
        return serialId;
    }

    public void setSerialId(Integer serialId) {
        this.serialId = serialId;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
    
    public byte isMainImage() {
        return isMainImage;
    }

    public void setMainImage(byte mainImage) {
        isMainImage = mainImage;
    }

}
