package com.january.demo.service;

import com.january.demo.dto.response.ReconciliationResponse;

import java.time.LocalDate;

public interface IReconciliationService {

    /**
     * Đối chiếu số dư của một ví thuộc về người dùng hiện tại trong khoảng thời gian cho trước.
     *
     * <p>Phương thức tính toán số dư đầu kỳ, tổng thu, tổng chi và số dư kỳ vọng dựa trên
     * các giao dịch trong khoảng thời gian, sau đó so sánh với số dư thực tế của ví để xác
     * định chênh lệch và trạng thái {@code RECONCILED}/{@code MISMATCH}.</p>
     *
     * @param walletId  mã định danh của ví cần đối chiếu
     * @param fromDate  ngày bắt đầu khoảng thời gian, có thể là {@code null} để lấy từ đầu
     * @param toDate    ngày kết thúc khoảng thời gian, có thể là {@code null} để lấy đến hết
     * @return kết quả đối chiếu gồm thu, chi, số dư dự kiến, số dư thực tế và chênh lệch
     * @throws com.january.demo.exception.NotFoundException nếu ví không tồn tại
     *         hoặc không thuộc về người dùng hiện tại
     */
    ReconciliationResponse reconcile(Long walletId, LocalDate fromDate, LocalDate toDate);
}