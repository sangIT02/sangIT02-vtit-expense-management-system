package com.january.demo.service;

import com.january.demo.dto.request.TransactionCreateRequest;
import com.january.demo.dto.request.TransactionFilter;
import com.january.demo.dto.request.TransactionUpdateRequest;
import com.january.demo.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITransactionService {

    TransactionResponse create(TransactionCreateRequest request);

    Page<TransactionResponse> getAll(TransactionFilter filter, Pageable pageable);

    TransactionResponse getById(Long id);

    TransactionResponse update(Long id, TransactionUpdateRequest request);

    void delete(Long id);

    List<TransactionResponse> export(TransactionFilter filter);
}