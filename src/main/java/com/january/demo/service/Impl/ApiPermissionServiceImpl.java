package com.january.demo.service.Impl;

import com.january.demo.dto.request.ApiPermissionRequest;
import com.january.demo.dto.response.ApiPermissionResponse;
import com.january.demo.entity.ApiPermission;
import com.january.demo.enums.PermissionName;
import com.january.demo.exception.BadRequestException;
import com.january.demo.exception.ConflictException;
import com.january.demo.exception.NotFoundException;
import com.january.demo.repository.ApiPermissionRepository;
import com.january.demo.repository.PermissionRepository;
import com.january.demo.service.IApiPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiPermissionServiceImpl implements IApiPermissionService {

    private final ApiPermissionRepository apiPermissionRepository;
    private final PermissionRepository permissionRepository;

    private volatile List<ApiPermission> cache;

    @Override
    public List<ApiPermissionResponse> findAll() {
        return apiPermissionRepository.findAll().stream()
                .sorted(Comparator.comparing(ApiPermission::getHttpMethod)
                        .thenComparing(ApiPermission::getUrlPattern))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ApiPermissionResponse create(ApiPermissionRequest request) {
        String httpMethod = normalizeMethod(request.httpMethod());
        String urlPattern = normalizeUrl(request.urlPattern());
        validatePermission(request.permissionName());

        if (apiPermissionRepository.existsByHttpMethodAndUrlPattern(httpMethod, urlPattern)) {
            throw new ConflictException("Endpoint nay da duoc gan quyen");
        }

        ApiPermission apiPermission = ApiPermission.builder()
                .httpMethod(httpMethod)
                .urlPattern(urlPattern)
                .permissionName(request.permissionName())
                .description(request.description())
                .enabled(request.enabled() == null || request.enabled())
                .build();
        apiPermission = apiPermissionRepository.save(apiPermission);
        reloadCache();
        return toResponse(apiPermission);
    }

    @Override
    @Transactional
    public ApiPermissionResponse update(Long id, ApiPermissionRequest request) {
        ApiPermission apiPermission = apiPermissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khong tim thay mapping endpoint-permission"));

        String httpMethod = normalizeMethod(request.httpMethod());
        String urlPattern = normalizeUrl(request.urlPattern());
        validatePermission(request.permissionName());

        boolean keyChanged = !apiPermission.getHttpMethod().equals(httpMethod)
                || !apiPermission.getUrlPattern().equals(urlPattern);
        if (keyChanged && apiPermissionRepository.existsByHttpMethodAndUrlPattern(httpMethod, urlPattern)) {
            throw new ConflictException("Endpoint nay da duoc gan quyen");
        }

        apiPermission.setHttpMethod(httpMethod);
        apiPermission.setUrlPattern(urlPattern);
        apiPermission.setPermissionName(request.permissionName());
        apiPermission.setDescription(request.description());
        apiPermission.setEnabled(request.enabled() == null || request.enabled());
        apiPermission = apiPermissionRepository.save(apiPermission);
        reloadCache();
        return toResponse(apiPermission);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ApiPermission apiPermission = apiPermissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khong tim thay mapping endpoint-permission"));
        apiPermissionRepository.delete(apiPermission);
        reloadCache();
    }

    @Override
    public List<ApiPermission> getActiveMappings() {
        List<ApiPermission> current = cache;
        if (current == null) {
            synchronized (this) {
                current = cache;
                if (current == null) {
                    current = apiPermissionRepository.findByEnabledTrue();
                    cache = current;
                }
            }
        }
        return current;
    }

    @Override
    public void reloadCache() {
        cache = apiPermissionRepository.findByEnabledTrue();
    }

    private void validatePermission(PermissionName permissionName) {
        if (!permissionRepository.existsByName(permissionName)) {
            throw new BadRequestException("Quyen han khong ton tai: " + permissionName);
        }
    }

    private String normalizeMethod(String method) {
        return method.trim().toUpperCase(java.util.Locale.ROOT);
    }

    private String normalizeUrl(String urlPattern) {
        String pattern = urlPattern.trim();
        if (!pattern.startsWith("/")) {
            throw new BadRequestException("URL pattern phai bat dau bang dau '/'");
        }
        return pattern;
    }

    private ApiPermissionResponse toResponse(ApiPermission apiPermission) {
        return new ApiPermissionResponse(
                apiPermission.getId(),
                apiPermission.getHttpMethod(),
                apiPermission.getUrlPattern(),
                apiPermission.getPermissionName(),
                apiPermission.getDescription(),
                apiPermission.getEnabled(),
                apiPermission.getCreatedAt(),
                apiPermission.getUpdatedAt()
        );
    }
}
