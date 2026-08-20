package com.january.demo.service;

import com.january.demo.dto.request.EncryptedTransactionHistoryRequest;
import com.january.demo.dto.request.TransactionCreateRequest;
import com.january.demo.dto.request.TransactionFilter;
import com.january.demo.dto.request.TransactionUpdateRequest;
import com.january.demo.dto.response.TransactionResponse;
import com.january.demo.entity.TransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITransactionService {

    /**
     * Tạo mới một giao dịch tài chính (thu/chi) cho người dùng hiện tại.
     *
     * <p>Phương thức xác thực ví và danh mục thuộc về người dùng, kiểm tra loại danh mục
     * khớp với loại giao dịch, kiểm tra số dư ví và phát hiện giao dịch trùng lặp trước
     * khi lưu. Sau khi lưu, số dư ví được cập nhật tương ứng với loại giao dịch.</p>
     *
     * @param request thông tin tạo giao dịch gồm loại, ví, danh mục, số tiền, ngày và mô tả
     * @return thông tin giao dịch đã được tạo
     */
    TransactionResponse create(TransactionCreateRequest request);

    /**
     * Truy vấn danh sách giao dịch của người dùng hiện tại theo bộ lọc và phân trang.
     *
     * <p>Kết quả luôn được giới hạn theo người dùng hiện tại và có thể lọc theo loại,
     * ví, danh mục, khoảng ngày, khoảng số tiền và từ khóa mô tả.</p>
     *
     * @param filter   bộ lọc điều kiện truy vấn, có thể để trống từng trường
     * @param pageable thông tin phân trang và sắp xếp
     * @return trang kết quả các giao dịch của người dùng
     */
    Page<TransactionResponse> getAll(TransactionFilter filter, Pageable pageable);

    /**
     * Lấy chi tiết một giao dịch theo id nếu nó thuộc về người dùng hiện tại.
     *
     * @param id mã định danh của giao dịch
     * @return thông tin chi tiết giao dịch
     * @throws com.january.demo.exception.TransactionNotFoundException nếu giao dịch
     *         không tồn tại hoặc không thuộc về người dùng hiện tại
     */
    TransactionResponse getById(Long id);

    /**
     * Cập nhật thông tin của một giao dịch thuộc về người dùng hiện tại.
     *
     * <p>Phương thức hoàn tác ảnh hưởng của giao dịch cũ lên số dư ví, sau đó áp dụng
     * lại ảnh hưởng mới với dữ liệu cập nhật. Quá trình này được thực hiện trong một
     * transaction đảm bảo tính nhất quán.</p>
     *
     * @param id      mã định danh của giao dịch cần cập nhật
     * @param request thông tin mới của giao dịch
     * @return thông tin giao dịch sau khi cập nhật
     */
    TransactionResponse update(Long id, TransactionUpdateRequest request);

    /**
     * Xóa một giao dịch thuộc về người dùng hiện tại.
     *
     * <p>Trước khi xóa, ảnh hưởng của giao dịch lên số dư ví được hoàn tác để giữ cho
     * số dư ví luôn nhất quán.</p>
     *
     * @param id mã định danh của giao dịch cần xóa
     */
    void delete(Long id);

    /**
     * Xuất danh sách toàn bộ giao dịch của người dùng hiện tại theo bộ lọc,
     * không phân trang, phục vụ cho việc xuất báo cáo.
     *
     * @param filter bộ lọc điều kiện truy vấn, có thể để trống từng trường
     * @return danh sách giao dịch thỏa mãn điều kiện lọc
     */
    List<TransactionResponse> export(TransactionFilter filter);

    /**
     * Lấy lịch sử giao dịch ngân hàng liên quan đến một giao dịch trong hệ thống.
     *
     * <p>Dữ liệu lịch sử được lưu dạng mã hóa nên phải được giải mã (RSA) trước khi trả về.
     * Nếu không truyền transactionId hoặc không có lịch sử nào sẽ trả về danh sách rỗng.</p>
     *
     * @param transactionId mã định danh của giao dịch cần tra cứu lịch sử
     * @return danh sách lịch sử giao dịch đã được giải mã, hoặc danh sách rỗng
     */
    List<TransactionHistory> getByTransactionId(Long transactionId);
}