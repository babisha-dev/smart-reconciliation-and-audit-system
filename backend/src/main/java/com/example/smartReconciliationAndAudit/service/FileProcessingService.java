package com.example.smartReconciliationAndAudit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class FileProcessingService {

    public String hash(MultipartFile file) throws Exception{
        byte[] hash= MessageDigest.getInstance("SHA-256").digest(file.getBytes());
StringBuilder sb=new StringBuilder();
for(Byte b:hash) {
    sb.append(String.format("%02x", b));
}
return sb.toString();

    }
public byte[] readBytes(MultipartFile file)throws  Exception{
        return file.getBytes();
}
}
