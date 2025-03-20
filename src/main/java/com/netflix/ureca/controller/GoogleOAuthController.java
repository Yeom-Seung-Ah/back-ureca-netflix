package com.netflix.ureca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.netflix.ureca.dto.Login;
import com.netflix.ureca.dto.Users;
import com.netflix.ureca.service.GoogleOAuthService;
import com.netflix.ureca.service.UsersService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;


@Controller
public class GoogleOAuthController {
    
    @Autowired
    GoogleOAuthService googleOAuthService;
    
    @Autowired
	UsersService usersService;
    
    @Value("${google.client.id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${google.client.secret}")
    private String GOOGLE_CLIENT_SECRET;

    @GetMapping("googleLogin")
    public String googleLogin() {
        return "redirect:https://accounts.google.com/o/oauth2/v2/auth?client_id="
                + GOOGLE_CLIENT_ID
                + "&redirect_uri=http://localhost:8080/googleLoginCallback"
                + "&response_type=code"
                + "&scope=email%20profile"
                + "&access_type=offline"; // Add this to get refresh token
    }

    @GetMapping("googleLoginCallback")
    public String googleLoginCallback(@RequestParam String code, HttpServletResponse response) throws Exception {
        System.out.println("사용자 동의 코드:" + code);

        // authorization code를 access token으로 교환
        String accessToken = googleOAuthService.getGoogleAccessToken(code);
        System.out.println("발급된 액세스 토큰:" + accessToken);

        // access token으로 사용자 이메일 정보 가져오기
        String email = googleOAuthService.getEmail(accessToken);
        System.out.println("구글 사용자 이메일:" + email);

        // 구글 사용자 정보를 이용해 로그인 처리
        Users googleUser = new Users();
        googleUser.setUserId(email);
        googleUser.setUserName(email.split("@")[0]);
        Login loginInfo = usersService.googleLogin(googleUser);

        if (loginInfo == null || loginInfo.getToken() == null) {
            System.out.println("구글 로그인 실패: 사용자 정보 없음");
            return "redirect:http://localhost:5173/login?error=user_not_found";
        }

        // 로그인 성공 시, 프론트엔드로 토큰과 이름을 쿼리 파라미터로 전달
        return "redirect:http://localhost:5173/?token=" + loginInfo.getToken() + "&name=" + loginInfo.getName() + "&userId=" + loginInfo.getUserId();
    }

}
