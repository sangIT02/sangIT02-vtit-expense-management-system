package com.january.demo.controller.v1;

import com.january.demo.dto.BaseResponse;
import com.january.demo.dto.request.ApiPermissionRequest;
import com.january.demo.dto.response.ApiPermissionResponse;
import com.january.demo.enums.PermissionName;
import com.january.demo.service.IApiPermissionService;
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

@Tag(name = "API Permission", description = "Quan ly phan quyen API theo DB")
@RestController
@RequestMapping("${app.api-prefix}/v1/admin/api-permissions")
@RequiredArgsConstructor
public class ApiPermissionController {

    private final IApiPermissionService apiPermissionService;

    @Operation(summary = "Lay danh sach mapping endpoint - permission")
    @GetMapping
    public BaseResponse<List<ApiPermissionResponse>> getAll() {
        return BaseResponse.success("Lay danh sach mapping thanh cong", apiPermissionService.findAll());
    }

    @Operation(summary = "Tao mapping endpoint - permission")
    @PostMapping
    public ResponseEntity<BaseResponse<ApiPermissionResponse>> create(
            @Valid @RequestBody ApiPermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Tao mapping thanh cong", apiPermissionService.create(request)));
    }

    @Operation(summary = "Cap nhat mapping endpoint - permission")
    @PutMapping("/{id}")
    public BaseResponse<ApiPermissionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ApiPermissionRequest request) {
        return BaseResponse.success("Cap nhat mapping thanh cong", apiPermissionService.update(id, request));
    }

    @Operation(summary = "Xoa mapping endpoint - permission")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        apiPermissionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Tai lai cache mapping tu database")
    @PostMapping("/reload")
    public BaseResponse<String> reload() {
        apiPermissionService.reloadCache();
        return BaseResponse.success("Tai lai cache thanh cong");
    }

    @Operation(summary = "Lay danh sach permission co the su dung")
    @GetMapping("/permissions")
    public BaseResponse<PermissionName[]> getPermissions() {
        return BaseResponse.success("Lay danh sach permission thanh cong", PermissionName.values());
    }
}
