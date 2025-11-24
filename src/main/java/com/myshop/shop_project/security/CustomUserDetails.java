package com.myshop.shop_project.security;

import com.myshop.shop_project.domain.member.entity.MembershipLevel;
import com.myshop.shop_project.domain.member.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * 일반 로그인(UserDetails)과 소셜 로그인(OAuth2User)을 통합한 만능 신분증
 */
@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User implements OAuth2User {

    private final MembershipLevel membershipLevel;
    private final String nickname;
    private Map<String, Object> attributes; // 소셜 유저 정보

    // 1. [일반 로그인용] 생성자 (파라미터 2개)
    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), authorities);
        this.membershipLevel = user.getMembershipLevel();
        this.nickname = user.getNickname();
    }

    // 2. ★★★ [소셜 로그인용] 생성자 (파라미터 3개) ★★★
    // 이 생성자가 없으면 CustomOAuth2UserService에서 에러가 납니다!
    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        super(user.getUsername(), user.getPassword(), authorities);
        this.membershipLevel = user.getMembershipLevel();
        this.nickname = user.getNickname();
        this.attributes = attributes;
    }

    // --- OAuth2User 인터페이스 구현 ---
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return getUsername();
    }
}