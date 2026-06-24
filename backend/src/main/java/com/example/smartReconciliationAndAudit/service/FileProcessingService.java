package com.example.smartReconciliationAndAudit.service;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        return isCsv(bytes) ? previewCsv(bytes):previewExcel(bytes);
}
public boolean isCsv(byte[] bytes) {
        if(bytes.length < 2)
            return true;
        if(bytes[0]==0x50 && bytes[1]==0x4B) return  false;
        if((bytes[0]&0xFF) ==0xD0 &&((bytes[0]&0xFF) == 0xCF)) return false;
       return true;
}
public List<Map<String,String>> previewCsv(byte[] bytes) throws Exception {
        List<Map<String,String>> rows=new ArrayList<>();
        try(CSVReader reader=new CSVReader(new InputStreamReader(new ByteArrayInputStream(bytes)))){
             String[] header=reader.readNext();
             if(header==null) return rows;
             String[] line;
             int count=0;
             while((line=reader.readNext())!=null && count++ <20){
                 Map<String,String> row=new LinkedHashMap<>();
                 for(int i=0;i< header.length;i++)
                     row.put(header[i].trim(),i<line.length?line[i].trim():"");
                 rows.add(row);
             }
        }
return rows;

}
public  List<Map<String,String>> previewExcel(byte[] bytes)throws Exception {
     List<Map<String,String>>  rows=new ArrayList<>();
    try(Workbook wb= WorkbookFactory.create(new ByteArrayInputStream(bytes))){
      Sheet sheet=wb.getSheetAt(0);
       Row headerrow=sheet.getRow(0);
     if(headerrow ==null) return rows;
         List<String> headers=new ArrayList<>();
        headers.forEach(c->headers.add(c.toString().trim()));
    for(int i=1;i<Math.min(20,sheet.getLastRowNum());i++){
             Row row=sheet.getRow(i);
        if(row==null) continue;
          Map<String,String> rowmap=new LinkedHashMap<>();
       for(int j=0;j<headers.size();j++){
           Cell cell=row.getCell(j);
             rowmap.put(headers.get(j),cell!=null?cell.toString():"");
}
           rows.add(rowmap);
    }

}
    return rows;
}
public List<Map<String,String>> parseCsvBytes(byte[] bytes) throws Exception{
     List<Map<String,String>> rows=new ArrayList<>();
     try(CSVReader reader=new CSVReader(new InputStreamReader(new ByteArrayInputStream(bytes)))){
      String[] header=reader.readNext();
      if(header ==null) return rows;
      String[] line;
      while((line=reader.readNext())!=null){
          Map<String,String> row=new LinkedHashMap<>();
          for(int i=0;i<header.length;i++){
              row.put(header[i].trim(),i<line.length?line[i]:"");
          }
          rows.add(row);
      }
     }
    }

}
