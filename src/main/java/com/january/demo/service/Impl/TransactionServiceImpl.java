package com.january.demo.service.Impl;

import com.january.demo.dto.request.TransactionCreateRequest;
import com.january.demo.dto.request.TransactionFilter;
import com.january.demo.dto.request.TransactionUpdateRequest;
import com.january.demo.dto.response.TransactionResponse;
import com.january.demo.entity.Category;
import com.january.demo.entity.Transaction;
import com.january.demo.entity.Wallet;
import com.january.demo.enums.TransactionType;
import com.january.demo.exception.BadRequestException;
import com.january.demo.exception.NotFoundException;
import com.january.demo.repository.CategoryRepository;
import com.january.demo.repository.TransactionRepository;
import com.january.demo.repository.WalletRepository;
import com.january.demo.service.ITransactionService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.january.demo.utils.SecurityUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public TransactionResponse create(TransactionCreateRequest request) {
        Long userId = getCurrentUserId();

        Wallet wallet = walletRepository.findByIdAndUser_Id(request.walletId(), userId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay vi"));
        Category category = categoryRepository.findByIdAndUser_Id(request.categoryId(), userId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay danh muc"));

        validateCategoryType(request.type(), category.getType());
        checkBalance(wallet, request.type(), request.amount());

        Transaction transaction = Transaction.builder()
                .type(request.type())
                .wallet(wallet)
                .category(category)
                .amount(request.amount())
                .transactionDate(request.transactionDate())
                .description(request.description())
                .build();
        transaction = transactionRepository.save(transaction);

        applyEffect(wallet, request.type(), request.amount(), +1);
        walletRepository.save(wallet);

        return toResponse(transaction);
    }

    @Override
    public Page<TransactionResponse> getAll(TransactionFilter filter, Pageable pageable) {
        Long userId = getCurrentUserId();
        return transactionRepository.findAll(buildSpec(userId, filter), pageable)
                .map(this::toResponse);
    }

    @Override
    public TransactionResponse getById(Long id) {
        Transaction transaction = findOwned(id);
        return toResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse update(Long id, TransactionUpdateRequest request) {
        Long userId = getCurrentUserId();
        Transaction transaction = findOwned(id);

        Wallet oldWallet = transaction.getWallet();
        // Hoan tac anh huong cu
        applyEffect(oldWallet, transaction.getType(), transaction.getAmount(), -1);

        Wallet newWallet = walletRepository.findByIdAndUser_Id(request.walletId(), userId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay vi"));
        Category newCategory = categoryRepository.findByIdAndUser_Id(request.categoryId(), userId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay danh muc"));

        validateCategoryType(request.type(), newCategory.getType());
        checkBalance(newWallet, request.type(), request.amount());

        transaction.setType(request.type());
        transaction.setWallet(newWallet);
        transaction.setCategory(newCategory);
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setDescription(request.description());
        transaction = transactionRepository.save(transaction);

        applyEffect(newWallet, request.type(), request.amount(), +1);
        walletRepository.save(oldWallet);
        if (oldWallet.getId() != newWallet.getId()) {
            walletRepository.save(newWallet);
        }

        return toResponse(transaction);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Transaction transaction = findOwned(id);
        Wallet wallet = transaction.getWallet();
        applyEffect(wallet, transaction.getType(), transaction.getAmount(), -1);
        walletRepository.save(wallet);
        transactionRepository.delete(transaction);
    }

    @Override
    public List<TransactionResponse> export(TransactionFilter filter) {
        Long userId = getCurrentUserId();
        return transactionRepository.findAll(buildSpec(userId, filter)).stream()
                .map(this::toResponse)
                .toList();
    }

    private Transaction findOwned(Long id) {
        return transactionRepository.findByIdAndWallet_User_Id(id, getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("Khong tim thay giao dich"));
    }

    private void validateCategoryType(TransactionType txType, com.january.demo.enums.CategoryType catType) {
        boolean matches = (txType == TransactionType.INCOME && catType == com.january.demo.enums.CategoryType.INCOME)
                || (txType == TransactionType.EXPENSE && catType == com.january.demo.enums.CategoryType.EXPENSE);
        if (!matches) {
            throw new BadRequestException("Danh muc khong phu hop voi loai giao dich");
        }
    }

    private void checkBalance(Wallet wallet, TransactionType type, BigDecimal amount) {
        if (type == TransactionType.EXPENSE && wallet.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("So du khong du de thuc hien giao dich");
        }
    }

    private void applyEffect(Wallet wallet, TransactionType type, BigDecimal amount, int multiplier) {
        BigDecimal delta = amount.multiply(BigDecimal.valueOf(multiplier));
        if (type == TransactionType.INCOME) {
            wallet.setBalance(wallet.getBalance().add(delta));
        } else {
            wallet.setBalance(wallet.getBalance().subtract(delta));
        }
    }

    private Specification<Transaction> buildSpec(Long userId, TransactionFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("wallet").get("user").get("id"), userId));

            if (filter.type() != null) {
                predicates.add(cb.equal(root.get("type"), filter.type()));
            }
            if (filter.walletId() != null) {
                predicates.add(cb.equal(root.get("wallet").get("id"), filter.walletId()));
            }
            if (filter.categoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.categoryId()));
            }
            if (filter.fromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.fromDate()));
            }
            if (filter.toDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), filter.toDate()));
            }
            if (filter.minAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), filter.minAmount()));
            }
            if (filter.maxAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), filter.maxAmount()));
            }
            if (filter.keyword() != null && !filter.keyword().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("description")),
                        "%" + filter.keyword().toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getType(),
                t.getWallet().getId(),
                t.getCategory().getId(),
                t.getAmount(),
                t.getTransactionDate(),
                t.getDescription(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}