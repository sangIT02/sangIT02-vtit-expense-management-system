package com.january.demo.service;

import com.january.demo.dto.request.BudgetRequest;
import com.january.demo.dto.response.BudgetResponse;

import java.util.List;

public interface IBudgetService {

    /**
     * Tạo mới một ngân sách cho người dùng hiện tại.
     *
     * <p>Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu và tên ngân sách phải là duy nhất
     * trong phạm vi người dùng.</p>
     *
     * @param request thông tin tạo ngân sách gồm tên, danh mục, hạn mức và khoảng thời gian
     * @return thông tin ngân sách đã được tạo
     * @throws com.january.demo.exception.ConflictException nếu tên ngân sách đã tồn tại
     */
    BudgetResponse create(BudgetRequest request);

    /**
     * Lấy danh sách tất cả ngân sách của người dùng hiện tại kèm số đã chi, số còn lại
     * và phần trăm sử dụng so với hạn mức.
     *
     * @return danh sách ngân sách của người dùng hiện tại
     */
    List<BudgetResponse> getAll();

    /**
     * Lấy chi tiết một ngân sách theo id nếu nó thuộc về người dùng hiện tại.
     *
     * @param id mã định danh của ngân sách
     * @return thông tin chi tiết ngân sách
     * @throws com.january.demo.exception.NotFoundException nếu ngân sách không tồn tại
     *         hoặc không thuộc về người dùng hiện tại
     */
    BudgetResponse getById(Long id);

    /**
     * Cập nhật thông tin của một ngân sách thuộc về người dùng hiện tại.
     *
     * <p>Nếu đổi tên ngân sách sang một tên đã tồn tại của cùng người dùng sẽ xảy ra xung đột.</p>
     *
     * @param id      mã định danh của ngân sách cần cập nhật
     * @param request thông tin mới của ngân sách
     * @return thông tin ngân sách sau khi cập nhật
     */
    BudgetResponse update(Long id, BudgetRequest request);

    /**
     * Xóa một ngân sách thuộc về người dùng hiện tại.
     *
     * @param id mã định danh của ngân sách cần xóa
     */
    void delete(Long id);
}