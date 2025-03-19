package com.netflix.ureca.dao;

import java.sql.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.netflix.ureca.dto.Login;

@Mapper
public interface LoginDao {	
	//토큰 삽입
	public void insertToken(Login login) throws Exception;
	// ✅ 기존 토큰 삭제 (중복 방지)
    void deleteToken(@Param("userId") String userId) throws Exception;
	//토큰 확인
	public Login checkToken(String authorization) throws Exception;

    // 로그인 시간 갱신 (요청 시마다 갱신)
    public void updateLoginTime(@Param("userId") String userId, @Param("newLoginTime") Date newLoginTime) throws Exception;

 // 리프레시 토큰 저장
    public void insertRefreshToken(@Param("userId") String userId, @Param("refreshToken") String refreshToken) throws Exception;

    // 리프레시 토큰 조회
    public String getRefreshToken(@Param("userId") String userId) throws Exception;
    
    // 액세스 토큰 업데이트
    public void updateAccessToken(@Param("userId") String userId, @Param("newAccessToken") String newAccessToken) throws Exception;

}
