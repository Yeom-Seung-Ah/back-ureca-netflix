package com.netflix.ureca.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.netflix.ureca.dto.Wishlist;

@Mapper
public interface WishlistDao {
    void addWish(Wishlist wishlist);
    void removeWish(@Param("userId") String userId, @Param("movieId") int movieId);
    List<Integer> getWishlistByUserId(String userId);
}
