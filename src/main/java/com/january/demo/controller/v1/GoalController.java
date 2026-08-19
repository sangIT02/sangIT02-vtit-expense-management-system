package com.january.demo.controller.v1;

import com.january.demo.dto.BaseResponse;
import com.january.demo.dto.request.GoalContributionRequest;
import com.january.demo.dto.request.GoalRequest;
import com.january.demo.dto.response.GoalResponse;
import com.january.demo.service.IGoalService;
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

@Tag(name = "Goal", description = "Cac API quan ly muc tieu tai chinh")
@RestController
@RequestMapping("${app.api-prefix}/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final IGoalService goalService;

    @Operation(summary = "Tao muc tieu")
    @PostMapping
    public ResponseEntity<BaseResponse<GoalResponse>> create(@Valid @RequestBody GoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Tao muc tieu thanh cong", goalService.create(request)));
    }

    @Operation(summary = "Lay danh sach muc tieu")
    @GetMapping
    public BaseResponse<List<GoalResponse>> getAll() {
        return BaseResponse.success("Lay danh sach muc tieu thanh cong", goalService.getAll());
    }

    @Operation(summary = "Lay chi tiet muc tieu")
    @GetMapping("/{id}")
    public BaseResponse<GoalResponse> getById(@PathVariable Long id) {
        return BaseResponse.success("Lay chi tiet muc tieu thanh cong", goalService.getById(id));
    }

    @Operation(summary = "Cap nhat muc tieu")
    @PutMapping("/{id}")
    public BaseResponse<GoalResponse> update(@PathVariable Long id,
                                             @Valid @RequestBody GoalRequest request) {
        return BaseResponse.success("Cap nhat muc tieu thanh cong", goalService.update(id, request));
    }

    @Operation(summary = "Gop tien vao muc tieu")
    @PostMapping("/{id}/contributions")
    public BaseResponse<GoalResponse> contribute(@PathVariable Long id,
                                                 @Valid @RequestBody GoalContributionRequest request) {
        return BaseResponse.success("Gop tien thanh cong", goalService.contribute(id, request));
    }

    @Operation(summary = "Xoa muc tieu")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}