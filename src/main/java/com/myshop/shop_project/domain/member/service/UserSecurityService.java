package com.myshop.shop_project.domain.member.service;

import com.myshop.shop_project.domain.member.entity.Role;
import com.myshop.shop_project.domain.member.entity.User;
import com.myshop.shop_project.domain.member.repository.UserRepository;
import com.myshop.shop_project.security.CustomUserDetails; // ★ [신규] 커스텀 객체 import
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> _user = this.userRepository.findByUsername(username);
        if (_user.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        
        User user = _user.get();
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // 권한 설정
        if (user.getRole() == Role.ADMIN) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(Role.USER.getValue()));
        }

        // ★★★ [수정] 일반 User 객체 대신 CustomUserDetails를 반환합니다! ★★★
        // 이렇게 하면 세션에 'membershipLevel' 정보가 같이 담기게 됩니다.
        return new CustomUserDetails(user, authorities);
    }
}