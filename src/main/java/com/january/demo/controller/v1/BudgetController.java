package com.january.demo.controller.v1;

import com.january.demo.dto.BaseResponse;
import com.january.demo.dto.request.BudgetRequest;
import com.january.demo.dto.response.BudgetResponse;
import com.january.demo.service.IBudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Budget", description = "Cac API quan ly ngan sach")
@RestController
@RequestMapping("${app.api-prefix}/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final IBudgetService budgetService;

    @Operation(summary = "Tao ngan sach")
    @PostMapping
    public ResponseEntity<BaseResponse<BudgetResponse>> create(@Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Tao ngan sach thanh cong", budgetService.create(request)));
    }

    @Operation(summary = "Lay danh sach ngan sach")
    @GetMapping
    public BaseResponse<List<BudgetResponse>> getAll() {
        return BaseResponse.success("Lay danh sach ngan sach thanh cong", budgetService.getAll());
    }

    @Operation(summary = "Lay chi tiet ngan sach")
    @GetMapping("/{id}")
    public BaseResponse<BudgetResponse> getById(@PathVariable Long id) {
        return BaseResponse.success("Lay chi tiet ngan sach thanh cong", budgetService.getById(id));
    }

    @Operation(summary = "Cap nhat ngan sach")
    @PutMapping("/{id}")
    public BaseResponse<BudgetResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody BudgetRequest request) {
        return BaseResponse.success("Cap nhat ngan sach thanh cong", budgetService.update(id, request));
    }

    @Operation(summary = "Xoa ngan sach")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}