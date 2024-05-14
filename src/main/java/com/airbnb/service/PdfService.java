package com.airbnb.service;

import com.airbnb.dto.BookingDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
public class PdfService {

    public boolean generatePdf(String fileName, BookingDto dto) {
        try {

            Document document = new Document();


            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Chunk bookingConfirmation = new Chunk("BookingConfirmation", font);
            Chunk bookingId = new Chunk("BookingId :"+ dto.getBookingId(), font);
            Chunk guestName = new Chunk("GuestName :"+dto.getGuestName(), font);
            Chunk price = new Chunk("Price: "+dto.getPrice(), font);
            Chunk totalPrice = new Chunk("TotalPrice: "+ dto.getTotal_price(), font);

            document.add(bookingConfirmation);
            document.add(new Paragraph("/n"));
            document.add(bookingId);
            document.add(new Paragraph("/n"));
            document.add(guestName);
            document.add(new Paragraph("/n"));
            document.add(price);
            document.add(new Paragraph("/n"));
            document.add(totalPrice);
            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }
}
