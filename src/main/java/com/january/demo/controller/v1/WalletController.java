package com.january.demo.controller.v1;

import com.january.demo.dto.BaseResponse;
import com.january.demo.dto.request.WalletCreateRequest;
import com.january.demo.dto.request.WalletUpdateRequest;
import com.january.demo.dto.response.WalletResponse;
import com.january.demo.service.IWalletService;
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

@Tag(name = "Wallet", description = "Cac API quan ly vi/tai khoan tien")
@RestController
@RequestMapping("${app.api-prefix}/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final IWalletService walletService;

    @Operation(summary = "Tao vi")
    @PostMapping
    public ResponseEntity<BaseResponse<WalletResponse>> create(@Valid @RequestBody WalletCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Tao vi thanh cong", walletService.create(request)));
    }

    @Operation(summary = "Lay danh sach vi")
    @GetMapping
    public BaseResponse<List<WalletResponse>> getAll() {
        return BaseResponse.success("Lay danh sach vi thanh cong", walletService.getAll());
    }

    @Operation(summary = "Lay chi tiet vi")
    @GetMapping("/{id}")
    public BaseResponse<WalletResponse> getById(@PathVariable Long id) {
        return BaseResponse.success("Lay chi tiet vi thanh cong", walletService.getById(id));
    }

    @Operation(summary = "Cap nhat vi")
    @PutMapping("/{id}")
    public BaseResponse<WalletResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody WalletUpdateRequest request) {
        return BaseResponse.success("Cap nhat vi thanh cong", walletService.update(id, request));
    }

    @Operation(summary = "Xoa hoac vo hieu hoa vi")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        walletService.delete(id);
        return ResponseEntity.noContent().build();
    }
}