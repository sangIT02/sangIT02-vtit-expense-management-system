package com.january.demo.service;

import com.january.demo.dto.request.BudgetRequest;
import com.january.demo.dto.response.BudgetResponse;

import java.util.List;

public interface IBudgetService {

    BudgetResponse create(BudgetRequest request);

    List<BudgetResponse> getAll();

    BudgetResponse getById(Long id);

    BudgetResponse update(Long id, BudgetRequest request);

    void delete(Long id);
}