package com.example.smartReconciliationAndAudit.service;

import com.example.smartReconciliationAndAudit.enums.MatchStatus;
import com.example.smartReconciliationAndAudit.model.ReconciliationResult;
import com.example.smartReconciliationAndAudit.model.TransactionRecord;
import com.example.smartReconciliationAndAudit.repository.ReconciliationResultRepository;
import com.example.smartReconciliationAndAudit.repository.TransactionRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationEngine {
public final TransactionRecordRepository recordRepo;
public final ReconciliationResultRepository resultRepo;

@Value("${reconciliation.amount-variance}")
private Integer variancePct;
    public void reconcile(long jobId){
List<TransactionRecord> uploaded=recordRepo.findByUploadJobId(jobId);

        Map<String,Long> idCount=new HashMap<>();
        for(TransactionRecord r:uploaded){
         idCount.merge(r.getTransactionId(),1L,Long::sum);
      }
for(TransactionRecord rec:uploaded){
try{
    MatchStatus status;
    TransactionRecord sysRec=null;
    String mismatches=null;

if(idCount.get(rec.getTransactionId())>1){
status=MatchStatus.DUPLICATE;
}
else {
    var exactOpt = recordRepo.findSystemByUploadJobId(jobId);
    if (exactOpt.isPresent() && amountEquals(exactOpt.get().getAmount(), rec.getAmount())) {
        status = MatchStatus.MATCHED;
        sysRec = exactOpt.get();
    } else if (rec.getReferenceNumber() != null) {
        var byRef = recordRepo.findSystemByReferenceNumber(rec.getReferenceNumber());
        if (!byRef.isEmpty() && withinVariance(rec.getAmount(), byRef.get().getAmount())) {
            sysRec = byRef.get();
            status = MatchStatus.PARTIALLY_MATCHED;
            mismatches = mismatchField(rec, sysRec);
        } else {
            status = MatchStatus.MISMATCHED;
        }
    } else {
        status = MatchStatus.MISMATCHED;
    }
}
    BigDecimal variance=null;
    if(rec.getAmount()!=null && sysRec!=null && sysRec.getAmount()!=null){
variance=rec.getAmount().subtract(sysRec.getAmount()).abs();
    }
    resultRepo.save(ReconciliationResult.builder()
            .uploadJobId(jobId)
            .transactionId(rec.getTransactionId())
            .uploadRecordId(rec.getId())
            .systemRecordId(sysRec != null ? sysRec.getId() : null)
            .matchStatus(status)
            .uploadedAmount(rec.getAmount())
            .systemAmount(sysRec != null ? sysRec.getAmount() : null)
            .amountVariance(variance)
            .mismatchedFields(mismatches)
            .build());


}
catch (Exception e) {
    log.warn("Failed to reconcile record {}: {}", rec.getTransactionId(), e.getMessage());
}
}
    }
    public boolean   withinVariance(BigDecimal uploaded, BigDecimal system){
       if(uploaded ==null || system== null)
           return false;
   BigDecimal allowed=system.multiply(BigDecimal.valueOf(variancePct/100));
   return uploaded.subtract(system).abs().compareTo(allowed)<=0;
    }
    private boolean amountEquals(BigDecimal a, BigDecimal b){
        return a!=null && b!= null && a.compareTo(b)==0;
    }
    private String mismatchField(TransactionRecord a, TransactionRecord b){
List<String> list=new ArrayList<>();
if(!amountEquals(a.getAmount(),b.getAmount())){
    list.add("amount");
}
if(Objects.equals(a.getTransactionDate(),b.getTransactionDate())){
    list.add("transaction date");
        }
        return String.join("," , list);
    }

}

