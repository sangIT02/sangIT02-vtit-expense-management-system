package com.january.demo.service.Impl;

import com.january.demo.dto.request.WalletCreateRequest;
import com.january.demo.dto.request.WalletUpdateRequest;
import com.january.demo.dto.response.WalletResponse;
import com.january.demo.entity.Transaction;
import com.january.demo.entity.User;
import com.january.demo.entity.Wallet;
import com.january.demo.enums.TransactionType;
import com.january.demo.enums.WalletStatus;
import com.january.demo.exception.ConflictException;
import com.january.demo.exception.NotFoundException;
import com.january.demo.exception.ResourceInUseException;
import com.january.demo.repository.TransactionRepository;
import com.january.demo.repository.UserRepository;
import com.january.demo.repository.WalletRepository;
import com.january.demo.service.IWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.january.demo.utils.SecurityUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements IWalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final RsaEncryptionServiceImpl rsaEncryptionServiceImpl;

    @Override
    @Transactional
    public WalletResponse create(WalletCreateRequest request) {
        Long userId = getCurrentUserId();
        if (walletRepository.existsByUser_IdAndName(userId, request.name())) {
            throw new ConflictException("Vi da ton tai");
        }

        Wallet wallet = Wallet.builder()
                .name(request.name())
                .currency(request.currency() == null ? "VND" : request.currency())
                .description(rsaEncryptionServiceImpl.encrypt(request.description(),null))
                .balance(request.initialBalance() == null ? BigDecimal.ZERO : request.initialBalance())
                .user(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Khong tim thay nguoi dung")))
                .build();
        wallet = walletRepository.save(wallet);
        return toResponse(wallet, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    public List<WalletResponse> getAll() {
        Long userId = getCurrentUserId();
        return walletRepository.findByUser_Id(userId).stream()
                .map(this::toResponseWithTotals)
                .toList();
    }

    @Override
    public WalletResponse getById(Long id) {
        Wallet wallet = findOwned(id);
        return toResponseWithTotals(wallet);
    }

    @Override
    @Transactional
    public WalletResponse update(Long id, WalletUpdateRequest request) {
        Long userId = getCurrentUserId();
        Wallet wallet = findOwned(id);

        if (!wallet.getName().equals(request.name())
                && walletRepository.existsByUser_IdAndName(userId, request.name())) {
            throw new ConflictException("Vi da ton tai");
        }

        wallet.setName(request.name());
        wallet.setCurrency(request.currency() == null ? "VND" : request.currency());
        wallet.setDescription(request.description());
        wallet = walletRepository.save(wallet);
        return toResponseWithTotals(wallet);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Wallet wallet = findOwned(id);

        List<Transaction> transactions = transactionRepository.findByWallet_Id(id);
        if (!transactions.isEmpty()) {
            wallet.setStatus(WalletStatus.INACTIVE);
            walletRepository.save(wallet);
            return;
        }

        walletRepository.delete(wallet);
    }

    private Wallet findOwned(Long id) {
        return walletRepository.findByIdAndUser_Id(id, getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("Khong tim thay vi"));
    }

    private WalletResponse toResponseWithTotals(Wallet wallet) {
        BigDecimal totalIn = BigDecimal.ZERO;
        BigDecimal totalOut = BigDecimal.ZERO;
        for (Transaction t : transactionRepository.findByWallet_Id(wallet.getId())) {
            if (t.getType() == TransactionType.INCOME) {
                totalIn = totalIn.add(t.getAmount());
            } else {
                totalOut = totalOut.add(t.getAmount());
            }
        }
        return toResponse(wallet, totalIn, totalOut);
    }

    private WalletResponse toResponse(Wallet wallet, BigDecimal totalIn, BigDecimal totalOut) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getName(),
                wallet.getCurrency(),
                wallet.getBalance(),
                wallet.getStatus(),
                rsaEncryptionServiceImpl.decrypt(wallet.getDescription(),null),
                totalIn,
                totalOut,
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}