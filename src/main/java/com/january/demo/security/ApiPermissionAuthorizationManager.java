package com.january.demo.security;

import com.january.demo.entity.ApiPermission;
import com.january.demo.service.IApiPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Supplier;

/**
 * AuthorizationManager phan quyen API dua tren du lieu trong bang {@code api_permission}
 * cua database. Moi endpoint (HTTP method + URL pattern) duoc gan voi mot permission,
 * nguoi dung phai co it nhat mot permission cua cac rule khop voi request.
 *
 * <p>Neu khong co rule nao khop thi chi can dang nhap la duoc phep truy cap
 * (quyen "co mat" mac dinh duoc kiem soat boi cau hinh {@code anyRequest}).
 */
@Component
@RequiredArgsConstructor
public class ApiPermissionAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final IApiPermissionService apiPermissionService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public AuthorizationDecision authorize(Supplier<? extends Authentication> authentication,
                                           RequestAuthorizationContext object) {
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return new AuthorizationDecision(false);
        }

        HttpServletRequest request = object.getRequest();
        String method = request.getMethod();
        String path = request.getServletPath();
        if (path == null || path.isEmpty()) {
            path = request.getRequestURI();
        }

        List<ApiPermission> mappings = apiPermissionService.getActiveMappings();
        boolean matched = false;
        for (ApiPermission mapping : mappings) {
            if (matches(mapping, method, path)) {
                matched = true;
                if (hasAuthority(auth, mapping.getPermissionName().name())) {
                    return new AuthorizationDecision(true);
                }
            }
        }

        return new AuthorizationDecision(!matched);
    }

    private boolean matches(ApiPermission mapping, String method, String path) {
        boolean methodMatches = "*".equals(mapping.getHttpMethod())
                || mapping.getHttpMethod().equalsIgnoreCase(method);
        return methodMatches && pathMatcher.match(mapping.getUrlPattern(), path);
    }

    private boolean hasAuthority(Authentication authentication, String permissionName) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(permissionName));
    }
}
