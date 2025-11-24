package com.myshop.shop_project.domain.mypage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    // 마이페이지 메인 (주문 내역으로 리다이렉트)
    @GetMapping
    public String mypage() {
        return "redirect:/mypage/orders";
    }

    // 주문 내역 조회
    @GetMapping("/orders")
    public String mypageOrders() {
        return "mypage/mypage_orders";
    }

    // 주문 상세 조회
    @GetMapping("/order/{orderId}")
    public String mypageOrderDetail(@PathVariable("orderId") Long orderId) {
        return "mypage/order_detail";
    }

    // 회원 정보 수정 페이지
    @GetMapping("/edit")
    public String mypageEdit() {
        return "mypage/mypage_edit";
    }
    
    // 회원 정보 수정 처리
    @PostMapping("/edit")
    public String mypageEditProcess() {
        // 추후: memberService.updateMember(...)
        return "redirect:/mypage/edit";
    }

    // 1:1 문의 내역
    @GetMapping("/qna")
    public String mypageQna() {
        return "mypage/mypage_qna";
    }

    // 1:1 문의 작성 폼
    @GetMapping("/qna/create")
    public String mypageQnaForm() {
        return "mypage/mypage_qna_form";
    }

    // 1:1 문의 등록 처리
    @PostMapping("/qna/create")
    public String mypageQnaCreate(
        @RequestParam("subject") String subject,
        @RequestParam("content") String content
    ) {
        // 추후: qnaService.createQna(...)
        System.out.println("문의 등록: " + subject);
        return "redirect:/mypage/qna";
    }
}