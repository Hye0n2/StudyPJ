package com.myshop.shop_project.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MembershipLevel {
    
    // 등급명, 기준금액, 색상코드(Hex)
    BRONZE("브론즈", 0, "#A52A2A"),       // 갈색
    SILVER("실버", 200_000, "#708090"),   // 회색
    GOLD("골드", 500_000, "#DAA520"),     // 금색
    VIP("VIP", 1_000_000, "#4B0082");     // 보라색

    private final String label;
    private final long requiredAmount;
    private final String colorCode; // ★★★ [신규] CSS 색상 코드 추가 ★★★

    // 구매 금액에 따라 적절한 등급을 계산해주는 메서드
    public static MembershipLevel getLevelByAmount(long amount) {
        if (amount >= VIP.requiredAmount) return VIP;
        if (amount >= GOLD.requiredAmount) return GOLD;
        if (amount >= SILVER.requiredAmount) return SILVER;
        return BRONZE;
    }
}