package com.airbnb.controller;


import com.airbnb.service.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("s3bucket")
//for accessing api in angular or else api cant be accessed from anywhere
@CrossOrigin("*")
public class BucketController {
    @Autowired
    BucketService service;
    //bucket name in the url
    //media type : what kind of content we are uploading
    //whatever file uploading take that file into multipart datatype in @requestparam
    @PostMapping(path = "/upload/file/{bucketName}", consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file,
                                             @PathVariable String bucketName){
        //returns the object url once uploaded the file in aws
        return new ResponseEntity<>(service.uploadFile(file,bucketName), HttpStatus.OK);
    }
}
//@DeleteMapping(path="/delete/file/{bucketName}/{fileName}")
//public ResponseEntity<String> deleteFile(@PathVariable String bucketName,@PathVariable
//String fileName)
        //{
        //
        //}
        //return new ResponseEntity<>(service.deleteFile(bucketName,fileName),HttpStatus.OK)
  //  }