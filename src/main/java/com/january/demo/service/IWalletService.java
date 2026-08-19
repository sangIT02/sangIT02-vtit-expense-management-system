package com.january.demo.service;

import com.january.demo.dto.request.WalletCreateRequest;
import com.january.demo.dto.request.WalletUpdateRequest;
import com.january.demo.dto.response.WalletResponse;

import java.util.List;

public interface IWalletService {

    WalletResponse create(WalletCreateRequest request);

    List<WalletResponse> getAll();

    WalletResponse getById(Long id);

    WalletResponse update(Long id, WalletUpdateRequest request);

    void delete(Long id);
}