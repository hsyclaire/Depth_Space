package com.depthspace.ticketshoppingcart.model;

import com.depthspace.ticket.model.TicketImagesVO;
import com.depthspace.ticket.model.old.TicketImagesJDBCDAO;

import java.io.*;
import java.nio.file.Files;

public class ImageTest {
    public static void main(String[] args) {
        TicketImagesJDBCDAO dao = new TicketImagesJDBCDAO();
        String imagePath="C:\\data\\20220923002036.jpg";
        try {
            byte[] pic = getPictureByteArray(imagePath);
            TicketImagesVO entity = new TicketImagesVO(null, 324004, pic,(byte)1);
            dao.insert(entity);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("執行成功");

    }
    public static byte[] getPictureByteArray(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        byte[] buffer= fis.readAllBytes();//jdk9以後用此方法進行讀取
        fis.close();
        return buffer;
    }
}
