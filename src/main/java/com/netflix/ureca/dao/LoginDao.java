package com.netflix.ureca.dao;

import java.sql.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.netflix.ureca.dto.Login;

@Mapper
public interface LoginDao {	
	//토큰 삽입
	public void insertToken(Login login) throws Exception;
	//토큰 삭제
	public void deleteToken(String token) throws Exception;
	//토큰 확인
	public Login checkToken(String authorization) throws Exception;

    // 로그인 시간 갱신 (요청 시마다 갱신)
    public void updateLoginTime(@Param("userId") String userId, @Param("newLoginTime") Date newLoginTime) throws Exception;

}
