package com.january.demo.controller.v1;


import com.january.demo.dto.request.EncryptedTransactionHistoryRequest;
import com.january.demo.dto.request.TransactionHistoryRequest;
import com.january.demo.entity.TransactionHistory;
import com.january.demo.service.ITransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/transaction-history")
@RequiredArgsConstructor
public class TransactionHistoryController {
    private final ITransactionHistoryService transactionHistoryService;

    @PostMapping("/new-record")
    public ResponseEntity<Void> newRecord(@RequestBody TransactionHistoryRequest request) {
        transactionHistoryService.create(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/transaction-history")
    public ResponseEntity<List<EncryptedTransactionHistoryRequest>> getTransactionHistory(@RequestParam Long id) {
        return ResponseEntity.ok().body(transactionHistoryService.findByTransactionId(id));
    }
}
