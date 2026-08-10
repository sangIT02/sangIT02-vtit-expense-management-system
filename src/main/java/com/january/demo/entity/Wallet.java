package com.january.demo.entity;

import com.january.demo.enums.WalletStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Wallet extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Nguoi dung khong duoc de trong")
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Ten vi khong duoc de trong")
    @Size(max = 100, message = "Ten vi khong duoc vuot qua 100 ky tu")
    private String name;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    @NotNull(message = "So du khong duoc de trong")
    @Digits(integer = 17, fraction = 2, message = "So du khong hop le")
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 10)
    @NotBlank(message = "Don vi tien te khong duoc de trong")
    @Size(max = 10, message = "Don vi tien te khong duoc vuot qua 10 ky tu")
    @Builder.Default
    private String currency = "VND";

    @Column(name = "description", length = 255)
    @Size(max = 255, message = "Mo ta khong duoc vuot qua 255 ky tu")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Trang thai vi khong duoc de trong")
    @Builder.Default
    private WalletStatus status = WalletStatus.ACTIVE;
}
