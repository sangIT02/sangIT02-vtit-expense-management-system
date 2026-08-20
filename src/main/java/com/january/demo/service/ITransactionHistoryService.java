package com.january.demo.service;

import com.january.demo.dto.request.EncryptedTransactionHistoryRequest;
import com.january.demo.dto.request.TransactionHistoryRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITransactionHistoryService {

    /**
     * Tạo lịch sử giao dịch ngân hàng cho một giao dịch chuyển tiền.
     *
     * <p>Mỗi lần chuyển tiền tạo ra hai bản ghi: bản ghi ghi nợ (inDebt) cho tài khoản nguồn
     * và bản ghi ghi có (have) cho tài khoản đích. Thông tin tài khoản và thời điểm giao dịch
     * được mã hóa bằng AES trước khi lưu.</p>
     *
     * @param request thông tin giao dịch chuyển tiền gồm tài khoản nguồn, tài khoản đích và số tiền
     */
    void create(TransactionHistoryRequest request);

    /**
     * Truy vấn lịch sử giao dịch ngân hàng theo mã giao dịch hệ thống.
     *
     * <p>Dữ liệu lịch sử được giải mã bằng RSA (khóa công khai) trước khi trả về phía client.
     * Nếu không có lịch sử nào sẽ trả về danh sách rỗng.</p>
     *
     * @param transactionId mã định danh của giao dịch cần tra cứu
     * @return danh sách lịch sử giao dịch đã được mã hóa RSA, hoặc danh sách rỗng
     */
    List<EncryptedTransactionHistoryRequest> findByTransactionId(Long transactionId);
}