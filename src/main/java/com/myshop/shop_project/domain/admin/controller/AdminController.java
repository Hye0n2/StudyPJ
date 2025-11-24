package com.myshop.shop_project.domain.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/admin_dashboard";
    }

    // === 카테고리 관리 ===
    @GetMapping("/categories")
    public String adminCategoryList() {
        return "admin/admin_category_list";
    }

    @GetMapping("/category/create")
    public String adminCategoryForm() {
        return "admin/admin_category_form";
    }
    
    @PostMapping("/category/create")
    public String adminCategoryCreate() {
        return "redirect:/admin/categories";
    }

    // === 상품 관리 ===
    @GetMapping("/products")
    public String adminProductList() {
        return "admin/admin_product_list";
    }

    @GetMapping("/product/create")
    public String adminProductForm() {
        return "admin/admin_product_form";
    }
    
    @PostMapping("/product/create")
    public String adminProductCreate() {
        return "redirect:/admin/products";
    }

    // === 회원 관리 ===
    @GetMapping("/users")
    public String adminUserList() {
        return "admin/admin_user_list";
    }

    // === 주문 관리 ===
    @GetMapping("/orders")
    public String adminOrderList() {
        return "admin/admin_order_list";
    }

    // === Q&A 관리 ===
    @GetMapping("/qna")
    public String adminQnaList() {
        return "admin/admin_qna_list";
    }

    // === 팝업 관리 ===
    @GetMapping("/popups")
    public String adminPopupList() {
        return "admin/admin_popup_list";
    }
    
    @GetMapping("/popup/create")
    public String adminPopupForm() {
        // 팝업 등록 폼 (추후 구현)
        return "admin/admin_popup_form"; // 템플릿 필요
    }
}