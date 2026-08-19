package com.january.demo.repository;

import com.january.demo.entity.Transaction;
import com.january.demo.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    List<Transaction> findByWallet_Id(Long walletId);

    List<Transaction> findByWallet_User_Id(Long userId);

    List<Transaction> findByWallet_User_IdAndType(Long userId, TransactionType type);

    List<Transaction> findByWallet_IdAndTransactionDateBetween(
            Long walletId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Transaction> findByWallet_User_IdAndTransactionDateBetween(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Transaction> findByCategory_Id(Long categoryId);

    Optional<Transaction> findByIdAndWallet_User_Id(Long id, Long userId);
}
