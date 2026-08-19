package com.january.demo.controller.v1;

import com.january.demo.dto.BaseResponse;
import com.january.demo.dto.request.TransactionCreateRequest;
import com.january.demo.dto.request.TransactionFilter;
import com.january.demo.dto.request.TransactionUpdateRequest;
import com.january.demo.dto.response.TransactionResponse;
import com.january.demo.enums.TransactionType;
import com.january.demo.service.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Transaction", description = "Cac API quan ly giao dich thu/chi")
@RestController
@RequestMapping("${app.api-prefix}/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ITransactionService transactionService;

    @Operation(summary = "Tao giao dich")
    @PostMapping
    public ResponseEntity<BaseResponse<TransactionResponse>> create(@Valid @RequestBody TransactionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Tao giao dich thanh cong", transactionService.create(request)));
    }

    @Operation(summary = "Lay danh sach giao dich")
    @GetMapping
    public BaseResponse<Page<TransactionResponse>> getAll(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long walletId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "transactionDate") Pageable pageable
    ) {
        TransactionFilter filter = new TransactionFilter(
                type, walletId, categoryId, toStartOfDay(fromDate), toEndOfDay(toDate),
                minAmount, maxAmount, keyword
        );
        return BaseResponse.success("Lay danh sach giao dich thanh cong",
                transactionService.getAll(filter, pageable));
    }

    @Operation(summary = "Xuat bao cao giao dich CSV")
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long walletId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        TransactionFilter filter = new TransactionFilter(
                type, walletId, categoryId, toStartOfDay(fromDate), toEndOfDay(toDate),
                null, null, null
        );
        List<TransactionResponse> transactions = transactionService.export(filter);
        byte[] csv = buildCsv(transactions);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @Operation(summary = "Lay chi tiet giao dich")
    @GetMapping("/{id}")
    public BaseResponse<TransactionResponse> getById(@PathVariable Long id) {
        return BaseResponse.success("Lay chi tiet giao dich thanh cong", transactionService.getById(id));
    }

    @Operation(summary = "Cap nhat giao dich")
    @PutMapping("/{id}")
    public BaseResponse<TransactionResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody TransactionUpdateRequest request) {
        return BaseResponse.success("Cap nhat giao dich thanh cong", transactionService.update(id, request));
    }

    @Operation(summary = "Xoa giao dich")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private LocalDateTime toStartOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private LocalDateTime toEndOfDay(LocalDate date) {
        return date == null ? null : date.atTime(23, 59, 59, 999999999);
    }

    private byte[] buildCsv(List<TransactionResponse> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Date,Type,Wallet,Category,Amount,Description\n");
        for (TransactionResponse t : transactions) {
            sb.append(t.id()).append(',')
                    .append(t.transactionDate()).append(',')
                    .append(t.type()).append(',')
                    .append(t.walletId()).append(',')
                    .append(t.categoryId()).append(',')
                    .append(t.amount()).append(',')
                    .append(t.description() == null ? "" : t.description())
                    .append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}