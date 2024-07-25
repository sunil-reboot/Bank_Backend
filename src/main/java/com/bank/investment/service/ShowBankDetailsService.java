package com.bank.investment.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.bank.investment.model.ShowBankDetails;
import com.bank.investment.repository.ShowBankDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class ShowBankDetailsService {

    @Autowired
    private ShowBankDetailsRepository showBankDetailsRepository;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public ShowBankDetails saveUser(ShowBankDetails showBankDetails, MultipartFile multipartFile) throws Exception {
        if (multipartFile.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5 MB limit");
        }

        File file = convertMultiPartToFile(multipartFile);
        String fileName = UUID.randomUUID().toString();
        ObjectMetadata metadata = new ObjectMetadata();
        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error uploading file to S3");
        }

        String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
        showBankDetails.setBtcS3Url(fileUrl);
        return showBankDetailsRepository.save(showBankDetails);
    }

    public String getLatestImageUrl() {
        ShowBankDetails latestDetails = showBankDetailsRepository.findLatest();
        return latestDetails != null ? latestDetails.getBtcS3Url() : null;
    }


    public ResponseEntity<InputStreamResource> getImage(String key) throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, key);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // You can set appropriate content type based on your image
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + key + "\"")
                .body(new InputStreamResource(s3Object.getObjectContent()));
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
