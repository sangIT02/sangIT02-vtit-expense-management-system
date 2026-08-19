package com.january.demo.controller.v1;

import com.january.demo.dto.BaseResponse;
import com.january.demo.dto.response.ReconciliationResponse;
import com.january.demo.service.IReconciliationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Reconciliation", description = "Bao cao doi soat vi")
@RestController
@RequestMapping("${app.api-prefix}/v1/reconciliation")
@RequiredArgsConstructor
public class ReconciliationController {

    private final IReconciliationService reconciliationService;

    @Operation(summary = "Doi soat mot vi")
    @GetMapping("/wallets/{walletId}")
    public BaseResponse<ReconciliationResponse> reconcile(
            @PathVariable Long walletId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return BaseResponse.success("Doi soat thanh cong",
                reconciliationService.reconcile(walletId, fromDate, toDate));
    }
}