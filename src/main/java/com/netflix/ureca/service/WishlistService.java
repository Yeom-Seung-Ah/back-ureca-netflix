package com.netflix.ureca.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.ureca.dao.WishlistDao;
import com.netflix.ureca.dto.Wishlist;

@Service
public class WishlistService {
    @Autowired
    WishlistDao wishlistDao;

    public void addWishlist(String userId, int movieId) throws Exception {
        wishlistDao.addWish(new Wishlist(0, userId, movieId));
    }
    
    public void removeWishlist(String userId, int movieId) throws Exception {
        wishlistDao.removeWish(userId, movieId);
    }

    public List<Integer> getWishlistByUserId(String userId) throws Exception {
        return wishlistDao.getWishlistByUserId(userId);
    }
}