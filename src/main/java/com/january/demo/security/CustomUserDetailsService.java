package com.january.demo.security;

import com.january.demo.entity.User;
import com.january.demo.repository.PermissionRepository;
import com.january.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Tai thong tin nguoi dung theo username de Spring Security thuc hien xac thuc
     * va tao {@link UserDetails} dua tren entity {@link User}.
     *
     * @param username ten dang nhap cua nguoi dung can xac thuc
     * @return {@link UserDetails} chua thong tin dang nhap va quyen cua nguoi dung
     * @throws UsernameNotFoundException khi khong tim thay nguoi dung theo username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException(username));
        return new CustomUserDetails(user);
    }


}
