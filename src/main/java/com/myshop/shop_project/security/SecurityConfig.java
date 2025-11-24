package com.myshop.shop_project.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final DataSource dataSource;
    private final UserDetailsService userDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    // ★★★ [신규] 카카오 로그아웃 핸들러 주입 ★★★
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. 접근 권한 설정
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers(new AntPathRequestMatcher("/css/**"), 
                                 new AntPathRequestMatcher("/js/**"), 
                                 new AntPathRequestMatcher("/images/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/mypage/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/order/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
            
            // 2. CSRF 비활성화
            .csrf((csrf) -> csrf.disable())
            
            .headers((headers) -> headers
                .frameOptions((frameOptions) -> frameOptions.sameOrigin()))

            // 3. 일반 로그인 설정
            .formLogin((formLogin) -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(loginSuccessHandler)
                .usernameParameter("username")
                .passwordParameter("password")
                .failureUrl("/login?error=true")
            )
            
            // 4. 소셜 로그인 설정
            .oauth2Login((oauth2) -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .userInfoEndpoint((userInfo) -> userInfo
                    .userService(customOAuth2UserService)
                )
            )
            
            // ★★★ 5. [수정] 로그아웃 설정 (핸들러 등록) ★★★
            .logout((logout) -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                // .logoutSuccessUrl("/?logout")  <-- 기존 코드 (단순 이동) 삭제 또는 주석 처리
                .logoutSuccessHandler(customLogoutSuccessHandler) // ★ 우리가 만든 핸들러 연결
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me") 
            )
            
            // 6. 자동 로그인 설정
            .rememberMe((rememberMe) -> rememberMe
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(60 * 60 * 24 * 7)
                .alwaysRemember(false)
                .userDetailsService(userDetailsService)
                .tokenRepository(persistentTokenRepository())
                .key("myshop-security-key-12345")
            )
            
            // 7. 접근 거부 처리
            .exceptionHandling((exception) -> exception
                .accessDeniedPage("/?error=denied") 
            )
        ;
        return http.build();
    }

    // ... (나머지 Bean 설정 유지) ...
    // (기존 코드 복사 붙여넣기 시 나머지 부분도 꼭 유지해주세요!)
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public CommandLineRunner debugOAuth2(ClientRegistrationRepository repository) {
        return args -> {
            var kakao = repository.findByRegistrationId("kakao");
            if (kakao != null) {
                System.out.println("✅ OAuth2 카카오 설정 로드 성공! (/oauth2/authorization/kakao 활성화됨)");
            }
        };
    }
}