package com.january.demo.repository;

import com.january.demo.entity.Wallet;
import com.january.demo.enums.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByUser_Id(Long userId);

    List<Wallet> findByUser_IdAndStatus(Long userId, WalletStatus status);

    Optional<Wallet> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndName(Long userId, String name);
}
