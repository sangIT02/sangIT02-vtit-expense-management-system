package com.january.demo.controller.v1;

import com.january.demo.dto.BaseResponse;
import com.january.demo.dto.request.CategoryRequest;
import com.january.demo.dto.request.CategoryUpdateRequest;
import com.january.demo.dto.response.CategoryResponse;
import com.january.demo.dto.response.CategoryTreeNode;
import com.january.demo.enums.CategoryType;
import com.january.demo.service.ICategoryService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Category", description = "Cac API quan ly danh muc thu/chi")
@RestController
@RequestMapping("${app.api-prefix}/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @Operation(summary = "Tao danh muc")
    @PostMapping
    public ResponseEntity<BaseResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Tao danh muc thanh cong", categoryService.create(request)));
    }

    @Operation(summary = "Lay danh sach danh muc")
    @GetMapping
    public BaseResponse<List<CategoryResponse>> getAll(
            @RequestParam(required = false) CategoryType type,
            @RequestParam(required = false) Long parentId
    ) {
        return BaseResponse.success("Lay danh sach danh muc thanh cong", categoryService.getAll(type, parentId));
    }

    @Operation(summary = "Lay cay danh muc")
    @GetMapping("/tree")
    public BaseResponse<List<CategoryTreeNode>> getTree() {
        return BaseResponse.success("Lay cay danh muc thanh cong", categoryService.getTree());
    }

    @Operation(summary = "Lay chi tiet danh muc")
    @GetMapping("/{id}")
    public BaseResponse<CategoryResponse> getById(@PathVariable Long id) {
        return BaseResponse.success("Lay chi tiet danh muc thanh cong", categoryService.getById(id));
    }

    @Operation(summary = "Cap nhat danh muc")
    @PutMapping("/{id}")
    public BaseResponse<CategoryResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody CategoryUpdateRequest request) {
        return BaseResponse.success("Cap nhat danh muc thanh cong", categoryService.update(id, request));
    }

    @Operation(summary = "Xoa danh muc")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}