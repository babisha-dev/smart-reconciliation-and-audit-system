package com.example.smartReconciliationAndAudit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

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

public List<Map<String,String>> preview(MultipartFile file) throws Exception{
        byte[] bytes= file.getBytes();
        return isCsv(bytes) ? previewCsv(file):previewExcel(file);
}
public boolean isCsv(byte[] bytes) {
        if(bytes.length < 2)
            return true;
        if(bytes[0]==0x50 && bytes[1]==0x4B) return  false;
        if((bytes[0]&0xFF) ==0xD0 &&((bytes[0]&0xFF) == 0xCF)) return false;
       return true;
}
public List<Map<String,String>> previewCsv(MultipartFile file){

}
public  List<Map<String,String>> previewExcel(MultipartFile file) {

}

}
