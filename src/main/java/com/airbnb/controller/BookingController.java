package com.airbnb.controller;

import com.airbnb.dto.BookingDto;
import com.airbnb.entity.Bookings;
import com.airbnb.entity.Property;
import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.BookingsRepository;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.service.BucketService;
import com.airbnb.service.TwilioService;
import com.itextpdf.text.pdf.PdfAction;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import  com.airbnb.service.PdfService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {


    public BookingController(BookingsRepository bookingsRepository, PropertyRepository propertyRepository, PdfService pdfService, BucketService bucketService, TwilioService twilioService) {
        this.bookingsRepository = bookingsRepository;
        this.propertyRepository = propertyRepository;
        this.pdfService = pdfService;
        this.bucketService = bucketService;
        this.twilioService = twilioService;
    }

    private BookingsRepository bookingsRepository;

    private PropertyRepository propertyRepository;

    private PdfService pdfService;

    private BucketService bucketService;

    private TwilioService twilioService;


    @PostMapping("/createBooking/{propertyId}")
    public ResponseEntity<?> createBooking(
            @RequestBody Bookings bookings,
            @AuthenticationPrincipal PropertyUser user,
            @PathVariable long propertyId
    ) throws IOException {
        bookings.setPropertyUser(user);
        System.out.println(propertyId);
        Property property = propertyRepository.findById(propertyId).get();
        Integer propertyPrice = property.getNightlyPrice();
        Integer totalNights = bookings.getTotalNights();
        int totalPrice = propertyPrice * totalNights;
        bookings.setProperty(property);
        bookings.setTotalPrice(totalPrice);
        Bookings savedBooking = bookingsRepository.save(bookings);

        BookingDto dto = new BookingDto();
        dto.setBookingId(savedBooking.getId());
        dto.setGuestName(savedBooking.getGuestName());
        dto.setPrice(propertyPrice);
        dto.setTotal_price(savedBooking.getTotalPrice());
        dto.setMobile(savedBooking.getMobile());
        boolean b = pdfService.generatePdf("E://JavaProjects-2023//" + "BookingConfirmationId" + savedBooking.getId() + ".pdf", dto);
        if(b){
            //upload pdf into bucket
            //convert will work only if you add the class name infront of it
            MultipartFile file = BookingController.convert("E://JavaProjects-2023//" + "BookingConfirmationId" + savedBooking.getId() + ".pdf");
            String uploadedFileUrl = bucketService.uploadFile(file, "myairbnbstore");
            System.out.println(dto);
            twilioService.sendSms(dto.getMobile(), "Test");
        }else{
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    //TO CONVERT STRING FILE INTO MULTIPART FILE
    public static MultipartFile convert(String filepath) throws IOException{
        //load the file from specified path
        File file = new File(filepath);

        //read the file content into byte array
        byte[] fileContent = Files.readAllBytes(file.toPath());

        //convert bytearray to a Resource(ByteArrayResource)
        Resource resource  = new ByteArrayResource(fileContent);

        //create MultiPartFile from Resource
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return file.getName();
            }

            @Override
            public String getOriginalFilename() {
                return file.getName();
            }

            @Override
            public String getContentType() {
                return null;//you can set appropriate content type here
            }

            @Override
            public boolean isEmpty() {
                return fileContent.length==0;
            }

            @Override
            public long getSize() {
                return fileContent.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return fileContent;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return resource.getInputStream();
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
              Files.write(dest.toPath(),fileContent);
            }
        };
        return multipartFile;


    }


}
