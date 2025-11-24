package com.myshop.shop_project.global.init;

import com.myshop.shop_project.domain.member.entity.MembershipLevel;
import com.myshop.shop_project.domain.member.entity.Role;
import com.myshop.shop_project.domain.member.entity.User;
import com.myshop.shop_project.domain.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        
        // ★★★ [수정] 이미 admin이 있으면 권한만 ADMIN으로 확실하게 고치고 종료 ★★★
        // (기존 데이터가 있어도 관리자 권한을 강제로 복구합니다)
        if (userRepository.findByUsername("admin").isPresent()) {
            User admin = userRepository.findByUsername("admin").get();
            if (admin.getRole() != Role.ADMIN) {
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("========== [DataInit] 기존 admin 계정의 권한을 ADMIN으로 복구했습니다. ==========");
            }
            return;
        }

        System.out.println("========== [DataInit] 더미 데이터 생성을 시작합니다... ==========");

        List<User> userList = new ArrayList<>();
        Random random = new Random();

        // 1. 관리자 생성
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setName("관리자");
        admin.setNickname("대장");
        admin.setEmail("admin@myshop.com");
        admin.setPhone("01000000000");
        admin.setZipcode("00000");
        admin.setAddress1("서울시 강남구");
        admin.setAddress2("관리자 타워 101호");
        admin.setCreateDate(LocalDateTime.now());
        admin.setRole(Role.ADMIN); // 권한 설정
        
        admin.setTotalPurchaseAmount(10_000_000_000L); 
        admin.setRecentPurchaseAmount(1_000_000_000L); 
        admin.setMembershipLevel(MembershipLevel.VIP);
        
        userList.add(admin);

        // 2. 일반 유저 생성
        IntStream.rangeClosed(1, 50).forEach(i -> {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword(passwordEncoder.encode("1234"));
            user.setName("테스터" + i);
            user.setNickname("닉네임" + i);
            user.setEmail("user" + i + "@test.com");
            
            String phoneNum = String.format("0101000%04d", i); 
            if (userRepository.findByPhone(phoneNum).isEmpty()) {
                user.setPhone(phoneNum);
                user.setZipcode("12345");
                user.setAddress1("테스트 주소 시 테스트구");
                user.setAddress2(i + "번지");
                user.setCreateDate(LocalDateTime.now().minusDays(100 - i));
                user.setRole(Role.USER);
                
                long totalAmount = (long) (random.nextDouble() * 5_000_000);
                long recentAmount = (long) (totalAmount * random.nextDouble());
                totalAmount = (totalAmount / 1000) * 1000;
                recentAmount = (recentAmount / 1000) * 1000;

                user.setTotalPurchaseAmount(totalAmount);
                user.setRecentPurchaseAmount(recentAmount);
                user.setMembershipLevel(MembershipLevel.getLevelByAmount(recentAmount));
                
                userList.add(user);
            }
        });

        if (!userList.isEmpty()) {
            userRepository.saveAll(userList);
            System.out.println("========== [DataInit] 등급별 회원 " + userList.size() + "명 생성 완료! ==========");
        }
    }
}