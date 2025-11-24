package com.myshop.shop_project.domain.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderController {

    // 장바구니 페이지
    @GetMapping("/cart")
    public String cartPage() {
        // [수정] templates/order/cart.html
        return "order/cart";
    }

    // 주문서 작성 페이지
    @GetMapping("/order")
    public String orderPage() {
        // [수정] templates/order/order_page.html
        return "order/order_page";
    }
    
    @PostMapping("/order/place")
    public String placeOrder() {
        // 리다이렉트는 URL 기준이므로 파일 경로와 상관없음 (수정 불필요)
        return "redirect:/mypage/orders"; 
    }
}