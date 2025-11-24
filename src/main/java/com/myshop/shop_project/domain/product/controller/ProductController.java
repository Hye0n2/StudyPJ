package com.myshop.shop_project.domain.product.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {

    // 상품 상세 페이지
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        // 추후: productService.getProduct(id) 호출
        model.addAttribute("productId", id);
        return "product_detail";
    }

    // 카테고리별 상품 목록
    @GetMapping("/category/{categoryId}")
    public String categoryPage(@PathVariable("categoryId") int categoryId, Model model) {
        String title = getCategoryTitle(categoryId);
        model.addAttribute("categoryTitle", title);
        // 추후: productService.getProductsByCategory(categoryId) 호출
        return "index"; // 상품 목록은 index.html 레이아웃 재사용
    }

    // 신상품
    @GetMapping("/category/new")
    public String categoryNew(Model model) {
        model.addAttribute("categoryTitle", "신상품");
        return "index"; // category_new.html이 있다면 그것을 리턴
    }

    // 베스트
    @GetMapping("/category/best")
    public String categoryBest(Model model) {
        model.addAttribute("categoryTitle", "베스트 상품");
        return "index";
    }

    // 알뜰쇼핑
    @GetMapping("/category/savings")
    public String categorySavings(Model model) {
        model.addAttribute("categoryTitle", "알뜰쇼핑");
        return "index";
    }

    // [Helper] 카테고리 ID로 제목 찾기 (임시 하드코딩 -> DB 연동 필요)
    private String getCategoryTitle(int categoryId) {
        if (categoryId == 1) return "컴퓨터";
        if (categoryId == 2) return "가전";
        if (categoryId == 3) return "도서";
        if (categoryId == 101) return "컴퓨터 > 모니터";
        if (categoryId == 102) return "컴퓨터 > 키보드";
        if (categoryId == 201) return "가전 > TV";
        if (categoryId == 202) return "가전 > 청소기";
        return "카테고리";
    }
}