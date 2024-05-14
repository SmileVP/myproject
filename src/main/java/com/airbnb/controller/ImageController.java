package com.airbnb.controller;

import com.airbnb.entity.Images;
import com.airbnb.entity.Property;
import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.ImagesRepository;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.service.BucketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/images")
@RestController
public class ImageController {

    private ImagesRepository imagesRepository;
    private PropertyRepository propertyRepository;

    private BucketService bucketService;

    public ImageController(ImagesRepository imagesRepository, PropertyRepository propertyRepository, BucketService bucketService) {
        this.imagesRepository = imagesRepository;
        this.propertyRepository = propertyRepository;
        this.bucketService = bucketService;
    }

    @PostMapping(path = "/upload/file/{bucketName}/property/{propertyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFile(
            //if request param then you should supply the value in query params of postman
            @RequestParam MultipartFile file,
            @PathVariable String bucketName,
            @PathVariable long propertyId,
            @AuthenticationPrincipal PropertyUser user
            ){

        String imageUrl = bucketService.uploadFile(file, bucketName);
        Property property = propertyRepository.findById(propertyId).get();
        Images image = new Images();
        image.setImageUrl(imageUrl);
        image.setProperty(property);
        image.setPropertyUser(user);

        Images saved = imagesRepository.save(image);

        return new ResponseEntity<>(saved,HttpStatus.CREATED);


    }




}
