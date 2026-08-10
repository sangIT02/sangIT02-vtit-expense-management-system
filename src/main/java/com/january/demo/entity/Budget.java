package com.january.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budget")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Budget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Nguoi dung khong duoc de trong")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Ten ngan sach khong duoc de trong")
    @Size(max = 100, message = "Ten ngan sach khong duoc vuot qua 100 ky tu")
    private String name;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    @NotNull(message = "So tien ngan sach khong duoc de trong")
    @Positive(message = "So tien ngan sach phai lon hon 0")
    @Digits(integer = 17, fraction = 2, message = "So tien ngan sach khong hop le")
    private BigDecimal amount;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Ngay bat dau khong duoc de trong")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @NotNull(message = "Ngay ket thuc khong duoc de trong")
    private LocalDate endDate;

    @AssertTrue(message = "Ngay ket thuc phai lon hon hoac bang ngay bat dau")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
