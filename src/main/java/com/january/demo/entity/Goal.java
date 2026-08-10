package com.january.demo.entity;

import com.january.demo.enums.GoalStatus;
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
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "goal")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Goal extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Nguoi dung khong duoc de trong")
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Ten muc tieu khong duoc de trong")
    @Size(max = 100, message = "Ten muc tieu khong duoc vuot qua 100 ky tu")
    private String name;

    @Column(name = "target_amount", nullable = false, precision = 19, scale = 2)
    @NotNull(message = "So tien muc tieu khong duoc de trong")
    @Positive(message = "So tien muc tieu phai lon hon 0")
    @Digits(integer = 17, fraction = 2, message = "So tien muc tieu khong hop le")
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 19, scale = 2)
    @NotNull(message = "So tien hien tai khong duoc de trong")
    @PositiveOrZero(message = "So tien hien tai phai lon hon hoac bang 0")
    @Digits(integer = 17, fraction = 2, message = "So tien hien tai khong hop le")
    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Trang thai muc tieu khong duoc de trong")
    @Builder.Default
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    @Column(name = "description", length = 255)
    @Size(max = 255, message = "Mo ta khong duoc vuot qua 255 ky tu")
    private String description;
}
