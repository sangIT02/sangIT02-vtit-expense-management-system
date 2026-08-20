package com.january.demo.service;

import com.january.demo.dto.request.WalletCreateRequest;
import com.january.demo.dto.request.WalletUpdateRequest;
import com.january.demo.dto.response.WalletResponse;

import java.util.List;

public interface IWalletService {

    /**
     * Tạo mới một ví cho người dùng hiện tại.
     *
     * <p>Tên ví phải là duy nhất trong phạm vi người dùng. Mô tả của ví được mã hóa
     * trước khi lưu và số dư ban đầu mặc định là {@code 0} nếu không được cung cấp.</p>
     *
     * @param request thông tin tạo ví gồm tên, đơn vị tiền tệ, mô tả và số dư ban đầu
     * @return thông tin ví đã được tạo
     * @throws com.january.demo.exception.ConflictException nếu tên ví đã tồn tại
     */
    WalletResponse create(WalletCreateRequest request);

    /**
     * Lấy danh sách tất cả ví của người hiện tại kèm tổng thu và tổng chi của từng ví.
     *
     * @return danh sách ví của người dùng hiện tại
     */
    List<WalletResponse> getAll();

    /**
     * Lấy chi tiết một ví theo id nếu nó thuộc về người dùng hiện tại.
     *
     * @param id mã định danh của ví
     * @return thông tin chi tiết ví kèm tổng thu và tổng chi
     * @throws com.january.demo.exception.NotFoundException nếu ví không tồn tại
     *         hoặc không thuộc về người dùng hiện tại
     */
    WalletResponse getById(Long id);

    /**
     * Cập nhật thông tin của một ví thuộc về người dùng hiện tại.
     *
     * <p>Nếu đổi tên ví sang một tên đã tồn tại của cùng người dùng sẽ xảy ra xung đột.</p>
     *
     * @param id      mã định danh của ví cần cập nhật
     * @param request thông tin mới của ví
     * @return thông tin ví sau khi cập nhật
     */
    WalletResponse update(Long id, WalletUpdateRequest request);

    /**
     * Xóa một ví thuộc về người dùng hiện tại.
     *
     * <p>Nếu ví có giao dịch liên quan, ví sẽ chuyển sang trạng thái {@code INACTIVE}
     * thay vì bị xóa để bảo toàn dữ liệu lịch sử giao dịch.</p>
     *
     * @param id mã định danh của ví cần xóa
     */
    void delete(Long id);
}