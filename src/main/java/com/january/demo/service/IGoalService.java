package com.january.demo.service;

import com.january.demo.dto.request.GoalContributionRequest;
import com.january.demo.dto.request.GoalRequest;
import com.january.demo.dto.response.GoalResponse;

import java.util.List;

public interface IGoalService {

    /**
     * Tạo mới một mục tiêu tiết kiệm cho người dùng hiện tại.
     *
     * <p>Tên mục tiêu phải là duy nhất trong phạm vi người dùng và số tiền hiện tại mặc định
     * là {@code 0} nếu không được cung cấp.</p>
     *
     * @param request thông tin tạo mục tiêu gồm tên, số tiền đích, hạn chót và mô tả
     * @return thông tin mục tiêu đã được tạo
     * @throws com.january.demo.exception.ConflictException nếu tên mục tiêu đã tồn tại
     */
    GoalResponse create(GoalRequest request);

    /**
     * Lấy danh sách tất cả mục tiêu tiết kiệm của người dùng hiện tại kèm phần trăm
     * hoàn thành so với số tiền đích.
     *
     * @return danh sách mục tiêu của người dùng hiện tại
     */
    List<GoalResponse> getAll();

    /**
     * Lấy chi tiết một mục tiêu theo id nếu nó thuộc về người dùng hiện tại.
     *
     * @param id mã định danh của mục tiêu
     * @return thông tin chi tiết mục tiêu
     * @throws com.january.demo.exception.NotFoundException nếu mục tiêu không tồn tại
     *         hoặc không thuộc về người dùng hiện tại
     */
    GoalResponse getById(Long id);

    /**
     * Cập nhật thông tin của một mục tiêu thuộc về người dùng hiện tại.
     *
     * <p>Nếu đổi tên mục tiêu sang một tên đã tồn tại của cùng người dùng sẽ xảy ra xung đột.
     * Trạng thái của mục tiêu được làm mới lại sau khi cập nhật.</p>
     *
     * @param id      mã định danh của mục tiêu cần cập nhật
     * @param request thông tin mới của mục tiêu
     * @return thông tin mục tiêu sau khi cập nhật
     */
    GoalResponse update(Long id, GoalRequest request);

    /**
     * Xóa một mục tiêu thuộc về người dùng hiện tại.
     *
     * @param id mã định danh của mục tiêu cần xóa
     */
    void delete(Long id);

    /**
     * Góp thêm tiền vào một mục tiêu thuộc về người dùng hiện tại.
     *
     * <p>Mục tiêu đã bị hủy ({@code CANCELLED}) sẽ không thể góp thêm tiền. Số tiền góp được
     * cộng dồn vào số tiền hiện tại và trạng thái mục tiêu được cập nhật lại.</p>
     *
     * @param id      mã định danh của mục tiêu cần góp tiền
     * @param request thông tin số tiền cần góp
     * @return thông tin mục tiêu sau khi góp tiền
     * @throws com.january.demo.exception.ConflictException nếu mục tiêu đã bị hủy
     */
    GoalResponse contribute(Long id, GoalContributionRequest request);
}