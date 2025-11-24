package com.myshop.shop_project.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        
        // 1. "아이디 저장" 체크박스 값을 가져옵니다. (HTML에서 name="save-id"로 설정할 예정)
        String saveId = request.getParameter("save-id");
        String username = request.getParameter("username"); // 입력한 아이디

        // 2. 체크되어 있다면 쿠키 생성
        if (saveId != null && saveId.equals("true")) {
            Cookie cookie = new Cookie("saved_username", username);
            cookie.setPath("/");      // 모든 경로에서 접근 가능
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30일간 유지
            response.addCookie(cookie);
        } 
        // 3. 체크되어 있지 않다면 쿠키 삭제 (MaxAge = 0)
        else {
            Cookie cookie = new Cookie("saved_username", null);
            cookie.setPath("/");
            cookie.setMaxAge(0); // 즉시 만료
            response.addCookie(cookie);
        }

        // 4. 부모 클래스의 기본 로그인 성공 로직 실행 (원래 가려던 페이지로 리다이렉트 등)
        super.onAuthenticationSuccess(request, response, authentication);
    }
}