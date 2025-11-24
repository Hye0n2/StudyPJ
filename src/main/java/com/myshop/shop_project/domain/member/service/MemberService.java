package com.myshop.shop_project.domain.member.service;

import com.myshop.shop_project.domain.member.dto.MemberSignupRequest;
import com.myshop.shop_project.domain.member.entity.MembershipLevel;
import com.myshop.shop_project.domain.member.entity.Role;
import com.myshop.shop_project.domain.member.entity.User;
import com.myshop.shop_project.domain.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 로직
    @Transactional
    public User signup(MemberSignupRequest request) {
        
        // 1. 비밀번호 일치 확인
        if (!request.password().equals(request.password_confirm())) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }

        // 2. 비밀번호 복잡도 검사
        if (!isValidPassword(request.password())) {
            throw new IllegalArgumentException("비밀번호는 9~24자 이내, 3가지 이상 조합이어야 합니다.");
        }

        // 3. 중복 검사
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (userRepository.findByNickname(request.nickname()).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        if (userRepository.findByPhone(request.phone()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 휴대폰 번호입니다.");
        }

        // 4. Entity 생성
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password())); 
        user.setName(request.name());
        user.setNickname(request.nickname());
        user.setPhone(request.phone());
        user.setEmail(request.email());
        user.setZipcode(request.zipcode());
        user.setAddress1(request.address1());
        user.setAddress2(request.address2());
        user.setCreateDate(LocalDateTime.now());
        
        // 5. 초기 권한 및 등급 설정
        user.setRole(Role.USER); 
        user.setTotalPurchaseAmount(0); // 누적 0원
        user.setRecentPurchaseAmount(0); // ★ [신규] 최근 3개월 0원
        user.setMembershipLevel(MembershipLevel.BRONZE); // 기본 등급

        return userRepository.save(user);
    }

    public boolean isUsernameAvailable(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    public boolean isNicknameAvailable(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 9 || password.length() > 24) return false;
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        int conditionCount = 0;
        if (hasLower) conditionCount++;
        if (hasUpper) conditionCount++;
        if (hasDigit) conditionCount++;
        if (hasSpecial) conditionCount++;
        return conditionCount >= 3;
    }
}