package com.january.demo.service.Impl;

import com.january.demo.dto.request.BudgetRequest;
import com.january.demo.dto.response.BudgetResponse;
import com.january.demo.entity.Budget;
import com.january.demo.entity.Category;
import com.january.demo.entity.User;
import com.january.demo.enums.TransactionType;
import com.january.demo.exception.BadRequestException;
import com.january.demo.exception.ConflictException;
import com.january.demo.exception.NotFoundException;
import com.january.demo.repository.BudgetRepository;
import com.january.demo.repository.CategoryRepository;
import com.january.demo.repository.TransactionRepository;
import com.january.demo.repository.UserRepository;
import com.january.demo.service.IBudgetService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.january.demo.utils.SecurityUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements IBudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BudgetResponse create(BudgetRequest request) {
        Long userId = getCurrentUserId();
        validateDateRange(request.startDate(), request.endDate());

        if (budgetRepository.existsByUser_IdAndName(userId, request.name())) {
            throw new ConflictException("Ngan sach da ton tai");
        }

        Budget budget = Budget.builder()
                .user(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Khong tim thay nguoi dung")))
                .name(request.name())
                .category(resolveCategory(userId, request.categoryId()))
                .amount(request.amount())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();
        budget = budgetRepository.save(budget);
        return toResponse(budget);
    }

    @Override
    public List<BudgetResponse> getAll() {
        Long userId = getCurrentUserId();
        return budgetRepository.findByUser_Id(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BudgetResponse getById(Long id) {
        return toResponse(findOwned(id));
    }

    @Override
    @Transactional
    public BudgetResponse update(Long id, BudgetRequest request) {
        Long userId = getCurrentUserId();
        Budget budget = findOwned(id);
        validateDateRange(request.startDate(), request.endDate());

        if (!budget.getName().equals(request.name())
                && budgetRepository.existsByUser_IdAndName(userId, request.name())) {
            throw new ConflictException("Ngan sach da ton tai");
        }

        budget.setName(request.name());
        budget.setCategory(resolveCategory(userId, request.categoryId()));
        budget.setAmount(request.amount());
        budget.setStartDate(request.startDate());
        budget.setEndDate(request.endDate());
        budget = budgetRepository.save(budget);
        return toResponse(budget);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        budgetRepository.delete(findOwned(id));
    }

    private Budget findOwned(Long id) {
        return budgetRepository.findByIdAndUser_Id(id, getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("Khong tim thay ngan sach"));
    }

    private Category resolveCategory(Long userId, Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdAndUser_Id(categoryId, userId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay danh muc"));
    }

    private void validateDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("Ngay ket thuc phai lon hon hoac bang ngay bat dau");
        }
    }

    private BigDecimal calculateSpent(Long userId, Budget budget) {
        Long categoryId = budget.getCategory() != null ? budget.getCategory().getId() : null;
        List<BigDecimal> amounts = transactionRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("wallet").get("user").get("id"), userId));
            predicates.add(cb.equal(root.get("type"), TransactionType.EXPENSE));
            predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"),
                    budget.getStartDate().atStartOfDay()));
            predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"),
                    budget.getEndDate().atTime(LocalDateTime.MAX.toLocalTime())));
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }).stream()
                .map(t -> t.getAmount())
                .toList();

        return amounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BudgetResponse toResponse(Budget budget) {
        Long userId = getCurrentUserId();
        BigDecimal spent = calculateSpent(userId, budget);
        BigDecimal remaining = budget.getAmount().subtract(spent);
        int percentage = budget.getAmount().compareTo(BigDecimal.ZERO) == 0
                ? 0
                : spent.multiply(BigDecimal.valueOf(100))
                        .divide(budget.getAmount(), 0, RoundingMode.HALF_UP)
                        .intValue();

        return new BudgetResponse(
                budget.getId(),
                budget.getName(),
                budget.getCategory() != null ? budget.getCategory().getId() : null,
                budget.getAmount(),
                budget.getStartDate(),
                budget.getEndDate(),
                spent,
                remaining,
                percentage
        );
    }
}