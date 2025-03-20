package com.netflix.ureca.dto;

public class Wishlist {
	private int id;
	private String userId;
	private int movieId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public Wishlist(int id, String userId, int movieId) {
		super();
		this.id = id;
		this.userId = userId;
		this.movieId = movieId;
	}
	public Wishlist() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Wishlist [id=" + id + ", userId=" + userId + ", movieId=" + movieId + "]";
	}
	
	
}
