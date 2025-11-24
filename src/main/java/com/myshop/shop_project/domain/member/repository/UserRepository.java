package com.myshop.shop_project.domain.member.repository;

import com.myshop.shop_project.domain.member.entity.User; // User 위치가 바뀌었으니 import 확인
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
}