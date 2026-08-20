package com.january.demo.service;

import com.january.demo.dto.request.ApiPermissionRequest;
import com.january.demo.dto.response.ApiPermissionResponse;
import com.january.demo.entity.ApiPermission;

import java.util.List;

public interface IApiPermissionService {

    /**
     * Lấy danh sách tất cả mapping giữa endpoint (HTTP method + URL pattern) và quyền truy cập.
     *
     * <p>Kết quả được sắp xếp theo HTTP method rồi đến URL pattern.</p>
     *
     * @return danh sách mapping endpoint-permission
     */
    List<ApiPermissionResponse> findAll();

    /**
     * Tạo mới một mapping giữa endpoint và quyền truy cập.
     *
     * <p>Đầu vào được chuẩn hóa (method in hoa, URL phải bắt đầu bằng {@code '/'}) và quyền
     * phải tồn tại trong hệ thống. Sau khi tạo, cache quyền được làm mới.</p>
     *
     * @param request thông tin mapping gồm HTTP method, URL pattern và tên quyền
     * @return thông tin mapping đã được tạo
     * @throws com.january.demo.exception.ConflictException nếu endpoint đã được gán quyền
     */
    ApiPermissionResponse create(ApiPermissionRequest request);

    /**
     * Cập nhật một mapping giữa endpoint và quyền truy cập.
     *
     * <p>Nếu HTTP method hoặc URL pattern thay đổi sang một giá trị đã tồn tại sẽ xảy ra xung đột.
     * Sau khi cập nhật, cache quyền được làm mới.</p>
     *
     * @param id      mã định danh của mapping cần cập nhật
     * @param request thông tin mới của mapping
     * @return thông tin mapping sau khi cập nhật
     */
    ApiPermissionResponse update(Long id, ApiPermissionRequest request);

    /**
     * Xóa một mapping giữa endpoint và quyền truy cập.
     *
     * <p>Sau khi xóa, cache quyền được làm mới.</p>
     *
     * @param id mã định danh của mapping cần xóa
     */
    void delete(Long id);

    /**
     * Lấy danh sách các mapping đang được kích hoạt phục vụ cho việc kiểm tra quyền truy cập.
     *
     * <p>Kết quả được lấy từ cache trong bộ nhớ và được nạp lazy lần đầu nếu cache chưa tồn tại.</p>
     *
     * @return danh sách mapping đang kích hoạt
     */
    List<ApiPermission> getActiveMappings();

    /**
     * Làm mới cache các mapping đang kích hoạt từ cơ sở dữ liệu.
     */
    void reloadCache();
}