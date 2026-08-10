package com.january.demo.entity;

import com.january.demo.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    @NotNull(message = "Vi khong duoc de trong")
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Danh muc khong duoc de trong")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    @NotNull(message = "Loai giao dich khong duoc de trong")
    private TransactionType type;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    @NotNull(message = "So tien khong duoc de trong")
    @Positive(message = "So tien phai lon hon 0")
    @Digits(integer = 17, fraction = 2, message = "So tien khong hop le")
    private BigDecimal amount;

    @Column(name = "description", length = 255)
    @Size(max = 255, message = "Mo ta khong duoc vuot qua 255 ky tu")
    private String description;

    @Column(name = "transaction_date", nullable = false)
    @NotNull(message = "Ngay giao dich khong duoc de trong")
    private LocalDateTime transactionDate;
}
