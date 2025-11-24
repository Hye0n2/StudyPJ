package com.myshop.shop_project.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
        String targetUrl = "/?logout"; // 기본: 일반 유저는 바로 메인으로 이동

        // 소셜 로그인 사용자라면?
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            String registrationId = authToken.getAuthorizedClientRegistrationId(); // "kakao", "naver" 등

            // 카카오 계정인 경우
            if ("kakao".equals(registrationId)) {
                ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("kakao");
                if (clientRegistration != null) {
                    String clientId = clientRegistration.getClientId();
                    // 카카오 로그아웃 API 주소
                    // (로그아웃 후 다시 내 쇼핑몰 메인(?logout)으로 돌아오게 설정)
                    String logoutRedirectUri = "http://localhost:8080/?logout"; 
                    
                    targetUrl = "https://kauth.kakao.com/oauth/logout?client_id=" + clientId + "&logout_redirect_uri=" + logoutRedirectUri;
                }
            }
        }

        // 최종 리다이렉트 (일반: 메인, 카카오: 카카오 로그아웃 페이지 -> 메인)
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}