package com.myshop.shop_project.domain.member.controller;

import com.myshop.shop_project.domain.member.dto.MemberSignupRequest;
import com.myshop.shop_project.domain.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // === 로그인 페이지 ===
    @GetMapping("/login")
    public String loginPage(
            // ★★★ [신규] 쿠키에서 'saved_username' 값을 가져옵니다 (없을 수도 있으니 required=false)
            @CookieValue(name = "saved_username", required = false) String savedUsername,
            Model model
    ) {
        // 쿠키에 값이 있으면 모델에 담아서 HTML로 보냅니다.
        if (savedUsername != null && !savedUsername.isEmpty()) {
            model.addAttribute("savedUsername", savedUsername);
            model.addAttribute("isIdSaved", true); // 체크박스 체크용
        }
        
        return "member/login"; 
    }

    // ... (나머지 코드는 그대로 유지)
    // === 아이디/비번 찾기 ===
    @GetMapping("/find-id")
    public String findIdPage() { return "member/login"; } 
    
    @GetMapping("/find-password")
    public String findPasswordPage() { return "member/login"; } 

    // === 회원가입 (GET) ===
    @GetMapping("/signup")
    public String signupPage(HttpSession session, Model model) {
        refreshCaptcha(session, model);
        
        model.addAttribute("userCreateForm", new MemberSignupRequest(
            null, null, null, null, null, null, null, null, null, null, null
        ));
        
        return "member/signup"; 
    }

    // === 회원가입 처리 (POST) ===
    @PostMapping("/user/signup")
    public String signupProcess(
            @Valid @ModelAttribute("userCreateForm") MemberSignupRequest request,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        // 입력 데이터 로깅 (디버깅용)
        log.info("===========================================");
        log.info(">>> 회원가입 요청 데이터: {}", request);
        log.info("===========================================");
        
        String sessionCaptcha = (String) session.getAttribute("captcha_answer");
        
        if (sessionCaptcha == null || !request.captcha_input().equalsIgnoreCase(sessionCaptcha)) {
            bindingResult.rejectValue("captcha_input", "captcha.invalid", "자동가입방지 숫자가 틀렸습니다.");
        }

        if (bindingResult.hasErrors()) {
            log.info("회원가입 검증 실패: {}", bindingResult);
            refreshCaptcha(session, model);
            return "member/signup"; 
        }

        try {
            memberService.signup(request);
            session.removeAttribute("captcha_answer");
        } catch (IllegalArgumentException e) {
            bindingResult.reject("signupFailed", e.getMessage());
            refreshCaptcha(session, model);
            return "member/signup"; 
        }

        redirectAttributes.addFlashAttribute("signupSuccess", "회원가입이 완료되었습니다.");
        return "redirect:/login";
    }

    // === API: 캡챠 새로고침 ===
    @GetMapping("/user/captcha-refresh")
    @ResponseBody
    public Map<String, String> captchaRefreshApi(HttpSession session) {
        String captchaText = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        session.setAttribute("captcha_answer", captchaText);
        return Map.of("captchaText", captchaText);
    }

    // === API: 중복 확인 ===
    @GetMapping("/user/check/username")
    @ResponseBody
    public Map<String, Boolean> checkUsername(@RequestParam("value") String username) {
        return Map.of("available", memberService.isUsernameAvailable(username));
    }

    @GetMapping("/user/check/nickname")
    @ResponseBody
    public Map<String, Boolean> checkNickname(@RequestParam("value") String nickname) {
        return Map.of("available", memberService.isNicknameAvailable(nickname));
    }

    private void refreshCaptcha(HttpSession session, Model model) {
        String captchaText = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        session.setAttribute("captcha_answer", captchaText);
        model.addAttribute("captchaText", captchaText);
    }
}