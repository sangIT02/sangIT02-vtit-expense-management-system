package com.january.demo.service.Impl;


import com.january.demo.dto.request.EncryptedTransactionHistoryRequest;
import com.january.demo.dto.request.TransactionHistoryRequest;
import com.january.demo.entity.TransactionHistory;
import com.january.demo.repository.TransactionHistoryRepository;
import com.january.demo.service.IAsymmetricCryptoService;
import com.january.demo.service.ISymmetricCryptoService;
import com.january.demo.service.ITransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements ITransactionHistoryService {
    private final TransactionHistoryRepository repository;
    private final ISymmetricCryptoService aesSymmetricCryptoService;
    private final IAsymmetricCryptoService rsaEnCryptoService;
    @Override
    public void create(TransactionHistoryRequest request) {
        validateTransaction(request);
        String transactionId = request.transactionId();
        LocalDateTime time = LocalDateTime.now();

        TransactionHistory sourceAccountTransaction = TransactionHistory.builder()
                .account(aesSymmetricCryptoService.encrypt(request.sourceAccount()))
                .have(BigDecimal.ZERO)
                .inDebt(request.amount())
                .transactionId(transactionId)
                .time(time)
                .build();
        TransactionHistory destinationAccountTransaction = TransactionHistory.builder()
                .account(aesSymmetricCryptoService.encrypt(request.destinationAccount()))
                .have(request.amount())
                .inDebt(BigDecimal.ZERO)
                .transactionId(transactionId)
                .time(time)
                .build();
        repository.save(sourceAccountTransaction);
        repository.save(destinationAccountTransaction);
    }

    @Override
    public List<EncryptedTransactionHistoryRequest> findByTransactionId(Long transactionId) {
        List<TransactionHistory> histories = repository.findByTransactionId(transactionId);
        if (histories.isEmpty()) {
            return List.of();
        }
        List<EncryptedTransactionHistoryRequest> result = new ArrayList<>();
        for(TransactionHistory history : histories) {
            EncryptedTransactionHistoryRequest th = new EncryptedTransactionHistoryRequest(
                    rsaEnCryptoService.encrypt(history.getTransactionId(),null),
                    rsaEnCryptoService.encrypt(history.getAccount(),null),
                    rsaEnCryptoService.encrypt(history.getInDebt().toString(),null),
                    rsaEnCryptoService.encrypt(history.getHave().toString(),null),
                    rsaEnCryptoService.encrypt(history.getTime().toString(),null)
            );
            result.add(th);
        }
        return result;
    }


    private void validateTransaction(TransactionHistoryRequest request) {

        if (request.amount() == null ||
                request.amount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than 0"
            );
        }

        if (request.sourceAccount()
                .equals(request.destinationAccount())) {

            throw new IllegalArgumentException(
                    "Source and destination accounts cannot be the same"
            );
        }
    }
}
