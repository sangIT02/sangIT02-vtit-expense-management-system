package com.january.demo.service;

import com.january.demo.dto.request.ApiPermissionRequest;
import com.january.demo.dto.response.ApiPermissionResponse;
import com.january.demo.entity.ApiPermission;

import java.util.List;

public interface IApiPermissionService {

    List<ApiPermissionResponse> findAll();

    ApiPermissionResponse create(ApiPermissionRequest request);

    ApiPermissionResponse update(Long id, ApiPermissionRequest request);

    void delete(Long id);

    List<ApiPermission> getActiveMappings();

    void reloadCache();
}
