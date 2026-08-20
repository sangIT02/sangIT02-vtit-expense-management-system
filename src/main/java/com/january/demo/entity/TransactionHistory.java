package com.january.demo.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_history")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistory extends BaseEntity{
    @Column(name = "transaction_id", nullable = false, length = 100)
    private String transactionId;

    /**
     * Số tài khoản.
     * Giá trị sẽ được AES encrypt trước khi lưu xuống DB.
     */
    @Column(name = "account", nullable = false, length = 500)
    private String account;

    /**
     * Số tiền ghi Nợ.
     */
    @Column(name = "in_debt", precision = 19, scale = 4, nullable = false)
    private BigDecimal inDebt;

    /**
     * Số tiền ghi Có.
     */
    @Column(name = "have", precision = 19, scale = 4, nullable = false)
    private BigDecimal have;

    /**
     * Thời gian phát sinh giao dịch.
     */
    @Column(name = "time", nullable = false)
    private LocalDateTime time;
}
