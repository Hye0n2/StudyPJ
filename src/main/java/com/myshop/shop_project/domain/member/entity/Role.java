package com.myshop.shop_project.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    
    // 스프링 시큐리티에서는 권한 코드 앞에 "ROLE_"을 붙이는 관례가 있습니다.
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String value;
}