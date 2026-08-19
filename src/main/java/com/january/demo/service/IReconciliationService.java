package com.january.demo.service;

import com.january.demo.dto.response.ReconciliationResponse;

import java.time.LocalDate;

public interface IReconciliationService {

    ReconciliationResponse reconcile(Long walletId, LocalDate fromDate, LocalDate toDate);
}