package com.myshop.shop_project.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("categoryTitle", "추천 상품");
        // 추후 ProductService를 통해 베스트 상품 목록을 가져와야 함
        return "index";
    }
}