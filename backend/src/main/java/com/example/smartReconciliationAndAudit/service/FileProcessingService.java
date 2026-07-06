package com.example.smartReconciliationAndAudit.service;
import com.example.smartReconciliationAndAudit.enums.UploadStatus;
import com.example.smartReconciliationAndAudit.model.TransactionRecord;
import com.example.smartReconciliationAndAudit.model.UploadJob;
import com.example.smartReconciliationAndAudit.repository.TransactionRecordRepository;
import com.example.smartReconciliationAndAudit.repository.UploadJobRepository;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessingService {
    final UploadJobRepository jobRepo;
    final TransactionRecordRepository recordrepo;
    final ReconciliationEngine engine;
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
     return rows;
    }
    public  List<Map<String,String>> parseExcelBytes(byte[] bytes) throws Exception{
        List<Map<String,String>> rows=new ArrayList<>();
        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            Sheet sheet = wb.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return rows;
            List<String> headers = new ArrayList<>();
            headerRow.forEach(c -> headers.add(c.toString().trim()));
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    rowMap.put(headers.get(j), cell != null ? cell.toString().trim() : "");
                }
                rows.add(rowMap);
            }
        }
        return rows;
    }
    @Async
    public  void processAsync(Long jobId, byte[] bytes,Map<String,String> mapping, String userId, String username){
        UploadJob job = jobRepo.findById(jobId).orElseThrow();
        try{
            job.setStatus(UploadStatus.PROCESSING);
            job.setStartedAt(LocalDateTime.now());
            jobRepo.save(job);
            List<Map<String,String>> allRows=isCsv(bytes) ? parseCsvBytes(bytes) : parseExcelBytes(bytes);

            log.info("job {} - parsed rows {} ",jobId,allRows.size());
            job.setTotalRecords(allRows.size());
            jobRepo.save(job);

            int saved=0;
            for(Map<String,String> row: allRows) {
                try {
                    TransactionRecord rec = toRecord(row, mapping, jobId);
                    if (rec.getTransactionId() == null || rec.getTransactionId().isBlank()) continue;
                    recordrepo.save(rec);
                    saved++;
                    if (saved % 500 == 0) {
                        job.setProcessedRecords(saved);
                        jobRepo.save(job);
                    }
                }
                catch (Exception e){
                 log.warn("skipping bad rows{}",e.getMessage());
                }
            }
            job.setProcessedRecords(saved);
            jobRepo.save(job);
            log.info("job-{} saved {} record",jobId,saved);
            engine.reconcile(jobId);
            job.setStatus(UploadStatus.COMPLETED);
            job.setCompletedAt(LocalDateTime.now());
            jobRepo.save(job);
            log.info("job {}- completed", jobId);

        }
        catch (Exception e){
        log.error("job {} failed, {}", jobId,e.getMessage());
        job.setStatus(UploadStatus.FAILED);
        job.setCompletedAt(LocalDateTime.now());
        jobRepo.save(job);


        }
    }
private TransactionRecord toRecord(Map<String , String > row, Map<String,String> mapping, Long jobId) {
    String txnId = get(row, mapping, "transactionId", "Transaction Id");
    String amtStr = get(row, mapping, "amount", "Amount");
    String ref = get(row, mapping, "referenceNumber", "Reference Number");
    String date = get(row, mapping, "date", "Date");
    String desc = get(row, mapping, "description", "Description");

    BigDecimal amount = null;
    if (amtStr != null && amtStr.isBlank()) {
        try {
            amount = new BigDecimal(amtStr.replaceAll("[^0-9.]", ""));
        } catch (Exception Ignored) {}

    }
    LocalDate txnDate=null;
    if(date!=null && date.isBlank()){
        for(String fmt:new String[]{"yyyy-MM-dd","MM/dd/yyyy","dd-MM-yyyy","dd/MM/yyyy","M/d/yyyy","d/M/yyyy"}){
        try{txnDate=LocalDate.parse(date, DateTimeFormatter.ofPattern(fmt));}
        catch (Exception Ignored){}
        }
    }
    return TransactionRecord.builder().transactionId(txnId).amount(amount)
            .referenceNumber(ref)
            .description(desc)
            .uploadJobId(jobId).isSystemRecord(false).build();
}
    private String get(Map<String,String> row, Map<String,String> mapping, String key,String fallback){
        String col=mapping.get(key);
        if(col!=null && row.containsKey(col)) return row.get(col);
        if(row.containsKey(fallback)) return row.get(fallback);
        for(var e: row.entrySet()){
          if(e.getKey().equalsIgnoreCase(fallback))
                    return e.getValue();
        }
        return null;
    }

}
