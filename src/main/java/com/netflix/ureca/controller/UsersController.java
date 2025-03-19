package com.netflix.ureca.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.ureca.dto.Login;
import com.netflix.ureca.dto.Users;
import com.netflix.ureca.service.GoogleOAuthService;
import com.netflix.ureca.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin("http://localhost:5173")
public class UsersController {
	
	@Autowired
	UsersService usersService;
	
	@Autowired
	GoogleOAuthService googleOAuthService;
	
	@PostMapping("logout")
	public ResponseEntity<?> logout(@RequestHeader String authorization) {
	    try {
	        usersService.logout(authorization);
	        return ResponseEntity.ok("로그아웃 성공");
	    } catch (Exception e) {
	        // 예외 메시지가 "토큰이 만료되었습니다. 다시 로그인하세요."라면, 클라이언트가 이를 감지해서 로그인 페이지로 이동하도록 함
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	    }
	}

	
	@PostMapping("tokenLogin")
	public Map<String,String> tokenLogin(@RequestBody Users u) {
		System.out.println(u);
		
		Map<String,String> responseMap=new HashMap<>();
		
		try {
			Login loginInfo=usersService.tokenLogin(u);
			
			if(loginInfo!=null && loginInfo.getName()!=null && loginInfo.getToken()!=null) {
				responseMap.put("name", loginInfo.getName());
				responseMap.put("Authorization", loginInfo.getToken());
			}else {
				responseMap.put("msg", "다시 로그인 해주세요");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMap.put("msg", "다시 로그인 해주세요");
		}
		return responseMap;
	}
	
	@PostMapping("/googleLogin")
	public ResponseEntity<Map<String, String>> googleLogin(@RequestBody Map<String, String> requestData) {
	    Map<String, String> responseMap = new HashMap<>();
	    try {
	        String accessToken = requestData.get("access_token");
	        System.out.println("🔵 받은 액세스 토큰: " + accessToken);

	        if (accessToken == null) {
	            responseMap.put("msg", "access_token이 없습니다.");
	            return ResponseEntity.badRequest().body(responseMap);
	        }

	        String email = googleOAuthService.getEmail(accessToken);
	        System.out.println("🔵 구글 사용자 이메일: " + email);

	        Users googleUser = new Users();
	        googleUser.setUserId(email);
	        googleUser.setUserName(email.split("@")[0]);

	        Login loginInfo = usersService.googleLogin(googleUser);

	        if (loginInfo != null && loginInfo.getToken() != null) {
	            responseMap.put("name", loginInfo.getName());
	            responseMap.put("Authorization", loginInfo.getToken());
	            System.out.println("🟢 구글 로그인 성공 - 토큰: " + loginInfo.getToken());
	            return ResponseEntity.ok(responseMap);
	        } else {
	            responseMap.put("msg", "로그인 실패: 서버 오류");
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        responseMap.put("msg", "서버 오류 발생");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
	    }
	}




	
	@PostMapping("checkToken")
	public ResponseEntity<?> checkToken(@RequestHeader String authorization) {
	    try {
	        Login login = usersService.checkToken(authorization);
	        return ResponseEntity.ok(login);
	    } catch (Exception e) {  // RuntimeException → Exception으로 변경
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	    }
	}


	
	@PostMapping("/signup")
	public Map<String, Object> signup(@RequestBody Users u) {
	    Map<String, Object> responseData = new HashMap<>();
	    try {
	        usersService.signup(u);
	        responseData.put("success", true);  // 회원가입 성공 여부 추가
	        responseData.put("msg", "ok");
	    }catch (DataIntegrityViolationException e) {  // 중복 오류 발생 시
	        responseData.put("success", false);
	        responseData.put("msg", "이미 존재하는 아이디입니다."); 
	    }catch (Exception e) {
	        e.printStackTrace();
	        responseData.put("success", false);  // 실패 시 success = false
	        responseData.put("msg", e.getMessage());
	    }
	    return responseData;
	}


}