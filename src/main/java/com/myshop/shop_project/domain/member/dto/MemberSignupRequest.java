package com.myshop.shop_project.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Java 17 Record: 기존의 UserCreateForm을 대체하는 불변 객체
public record MemberSignupRequest(
    @NotBlank(message = "아이디는 필수 항목입니다.")
    @Size(min = 6, max = 20, message = "아이디는 6자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 영문 소문자와 숫자로만 구성되어야 합니다.")
    String username,

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 9, max = 24, message = "비밀번호는 9자 이상 24자 이하로 입력해주세요.")
    String password,

    @NotBlank(message = "비밀번호 확인은 필수 항목입니다.")
    String password_confirm,

    @NotBlank(message = "이름은 필수 항목입니다.")
    String name,

    @NotBlank(message = "닉네임은 필수 항목입니다.")
    String nickname,

    @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "휴대폰 번호는 10~11자리의 숫자만 입력해주세요.")
    String phone,

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "우편번호를 입력해주세요.")
    String zipcode,

    @NotBlank(message = "주소를 입력해주세요.")
    String address1,

    String address2, // 상세주소

    @NotBlank(message = "자동방지문자를 입력해주세요.")
    String captcha_input
) {}