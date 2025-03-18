package com.netflix.ureca.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.ureca.dto.Login;
import com.netflix.ureca.dto.Users;
import com.netflix.ureca.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin("http://localhost:5173")
public class UsersController {
	
	@Autowired
	UsersService usersService;
	
	@PostMapping("logout")
	public void logout(@RequestHeader String authorization) {
		System.out.println(authorization);
		try {
			usersService.logout(authorization);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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