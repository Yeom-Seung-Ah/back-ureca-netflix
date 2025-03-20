package com.netflix.ureca.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.ureca.dto.Wishlist;
import com.netflix.ureca.service.WishlistService;

@RestController
@CrossOrigin("http://localhost:5173")
public class WishlistController {
    @Autowired
    WishlistService wishlistService;

    @PostMapping("/addWishList")
    public void addWishList(@RequestBody Wishlist wishlist) throws Exception {
        wishlistService.addWishlist(wishlist.getUserId(), wishlist.getMovieId());
    }

    @GetMapping("/getWishList")
    public List<Integer> getWishList(@RequestParam String userId) throws Exception {
        return wishlistService.getWishlistByUserId(userId);
    }
    
    @PostMapping("/removeWishList")
    public void removeWishList(@RequestBody Wishlist wishlist) throws Exception {
        wishlistService.removeWishlist(wishlist.getUserId(), wishlist.getMovieId());
    }

}

