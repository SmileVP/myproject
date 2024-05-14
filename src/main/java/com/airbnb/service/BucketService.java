package com.airbnb.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
@Service
public class BucketService {
    @Autowired
    private AmazonS3 amazonS3;

    //code to upload file in s3
    //datatype of the uploaded file should be multipart
    //bucketname to specify in which bucket we want to upload the files
    public String uploadFile(MultipartFile file, String bucketName) {
        //if file is empty then gives built in exception from java.lang package
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        try {
            //here i am getting the name of the uploaded file
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" +
                    file.getOriginalFilename());
            //its copying the file from our local system to a destination i.e aws
            // now the file is in local system but when i upload it in program then it is converted into binaries now that binaries is saved in dest
            file.transferTo(convFile);
            try {
                //i will use amazon s3 bean and give the binaries converted file name and this will upload your file in aws
                amazonS3.putObject(bucketName, convFile.getName(), convFile);
                //returns the object url once the file is uploaded in aws s3 bucket
                return amazonS3.getUrl(bucketName, file.getOriginalFilename()).toString();
            } catch (AmazonS3Exception s3Exception) {
                return "Unable to upload file :" + s3Exception.getMessage();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to upload file", e);
        }
    }

    //to delete a bucket in s3
    // public String deleteBucket(String bucketName) {
    // amazonS3.deleteBucket(bucketName);
    // return "File is deleted";
    // }

}
