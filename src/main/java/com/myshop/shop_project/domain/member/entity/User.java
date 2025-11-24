package com.myshop.shop_project.domain.member.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100)
    private String username; 

    private String password; 

    @Column(unique = true, length = 200)
    private String email; 

    @Column(unique = true, length = 100)
    private String nickname; 

    @Column(length = 100)
    private String name; 

    @Column(unique = true, length = 20)
    private String phone; 

    private String zipcode;
    private String address1;
    private String address2;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 멤버십 등급 (이제 'recentPurchaseAmount' 기준으로 산정됨)
    @Enumerated(EnumType.STRING)
    private MembershipLevel membershipLevel;

    // 1. [기존] 누적 구매 금액 (단순 통계용 / 장기 충성도 확인용)
    @Column(columnDefinition = "bigint default 0")
    private long totalPurchaseAmount = 0;

    // ★★★ 2. [신규] 최근 3개월 구매 금액 (등급 산정의 기준!) ★★★
    // (실무에서는 매일 밤 배치 프로그램이 3개월치 주문을 합산해서 이 필드를 업데이트합니다)
    @Column(columnDefinition = "bigint default 0")
    private long recentPurchaseAmount = 0;

    private LocalDateTime createDate;

    private String provider;    // "kakao"
    private String providerId;  // 식별자
}