package com.myshop.shop_project.security;

import com.myshop.shop_project.domain.member.entity.MembershipLevel;
import com.myshop.shop_project.domain.member.entity.Role;
import com.myshop.shop_project.domain.member.entity.User;
import com.myshop.shop_project.domain.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 카카오에서 유저 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info(">>> 카카오 로그인 정보: {}", oAuth2User.getAttributes());

        // 2. provider 확인 (kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        // 3. 카카오 응답 데이터 파싱
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String providerId = String.valueOf(attributes.get("id"));
        String nickname = (String) profile.get("nickname");
        String email = (String) kakaoAccount.get("email");

        // 4. 우리 DB에 저장할 아이디 만들기
        String username = registrationId + "_" + providerId; 
        
        // 5. 회원가입 또는 로그인 처리
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user;

        if (userOptional.isEmpty()) {
            // [신규 가입]
            user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // 비밀번호 랜덤
            user.setNickname(nickname);
            user.setEmail(email != null ? email : username + "@kakao.com");
            user.setRole(Role.USER);
            user.setProvider(registrationId); // ★ User 엔티티에 이 필드가 있어야 함
            user.setProviderId(providerId);   // ★ User 엔티티에 이 필드가 있어야 함
            user.setMembershipLevel(MembershipLevel.BRONZE);
            user.setTotalPurchaseAmount(0);
            user.setCreateDate(LocalDateTime.now());
            user.setName(nickname);
            user.setPhone(""); // 전화번호는 카카오에서 바로 안 넘어오므로 빈 값
            
            userRepository.save(user);
            log.info(">>> 소셜 회원가입 완료: {}", username);
        } else {
            // [기존 회원] 정보 업데이트
            user = userOptional.get();
            user.setNickname(nickname);
            log.info(">>> 소셜 로그인 성공: {}", username);
        }

        // 6. 통합 신분증(CustomUserDetails) 반환
        return new CustomUserDetails(
                user, 
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getValue())), 
                attributes
        );
    }
}