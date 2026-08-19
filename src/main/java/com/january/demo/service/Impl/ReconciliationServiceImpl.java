package com.january.demo.service.Impl;

import com.january.demo.dto.response.ReconciliationResponse;
import com.january.demo.entity.Transaction;
import com.january.demo.entity.Wallet;
import com.january.demo.enums.TransactionType;
import com.january.demo.exception.NotFoundException;
import com.january.demo.repository.TransactionRepository;
import com.january.demo.repository.WalletRepository;
import com.january.demo.service.IReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.january.demo.utils.SecurityUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class ReconciliationServiceImpl implements IReconciliationService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public ReconciliationResponse reconcile(Long walletId, LocalDate fromDate, LocalDate toDate) {
        Long userId = getCurrentUserId();
        Wallet wallet = walletRepository.findByIdAndUser_Id(walletId, userId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay vi"));

        LocalDateTime from = fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime to = toDate == null ? null : toDate.atTime(23, 59, 59, 999999999);

        BigDecimal opening = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        List<Transaction> transactions = transactionRepository.findByWallet_Id(walletId);
        for (Transaction t : transactions) {
            LocalDateTime date = t.getTransactionDate();
            if (from != null && date.isBefore(from)) {
                opening = apply(opening, t);
                continue;
            }
            if (to != null && date.isAfter(to)) {
                continue;
            }
            if (t.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(t.getAmount());
            } else {
                totalExpense = totalExpense.add(t.getAmount());
            }
        }

        BigDecimal calculated = opening.add(totalIncome).subtract(totalExpense);
        BigDecimal actual = wallet.getBalance();
        BigDecimal difference = actual.subtract(calculated);

        return new ReconciliationResponse(
                wallet.getId(),
                wallet.getName(),
                opening,
                totalIncome,
                totalExpense,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                calculated,
                actual,
                difference,
                difference.compareTo(BigDecimal.ZERO) == 0 ? "RECONCILED" : "MISMATCH"
        );
    }

    private BigDecimal apply(BigDecimal current, Transaction t) {
        return t.getType() == TransactionType.INCOME
                ? current.add(t.getAmount())
                : current.subtract(t.getAmount());
    }
}