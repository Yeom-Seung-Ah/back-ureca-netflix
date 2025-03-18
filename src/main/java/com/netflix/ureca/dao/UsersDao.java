package com.netflix.ureca.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.netflix.ureca.dto.Users;



@Mapper
public interface UsersDao {
    Users login(Users u) throws Exception;
    void signup(Users u) throws Exception; 
}
