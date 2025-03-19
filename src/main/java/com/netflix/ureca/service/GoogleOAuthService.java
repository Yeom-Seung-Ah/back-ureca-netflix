package com.netflix.ureca.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GoogleOAuthService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;
    
    public String getGoogleAccessToken(String code) throws Exception {
        URL url = new URL("https://oauth2.googleapis.com/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        
        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=authorization_code");
        sb.append("&client_id=" + clientId);
        sb.append("&client_secret=" + clientSecret); // 클라이언트 시크릿 추가
        sb.append("&redirect_uri=http://localhost:8080/googleLoginCallback");
        sb.append("&code=" + code);
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(sb.toString());
        bw.flush();
        
        int responseCode = conn.getResponseCode();
        System.out.println("responseCode:" + responseCode);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        
        String line = "";
        String result = "";
        while ((line = br.readLine()) != null) {
            result += line;
        }
        
        System.out.println("result:" + result);
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);
        
        String access_token = jsonNode.get("access_token").asText();
        // refresh_token은 항상 제공되지 않을 수 있음
        String refresh_token = "";
        if (jsonNode.has("refresh_token")) {
            refresh_token = jsonNode.get("refresh_token").asText();
            System.out.println("refresh_token:" + refresh_token);
        }
        
        System.out.println("access_token:" + access_token);
        
        br.close();
        bw.close();
        
        return access_token;
    }
    
    public String getEmail(String accessToken) throws Exception {
        // 올바른 Google API 엔드포인트 사용
        URL url = new URL("https://www.googleapis.com/oauth2/v3/userinfo");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("GET"); // GET 메소드 사용
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        
        int responseCode = conn.getResponseCode();
        System.out.println("responseCode:" + responseCode);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        String result = "";
        
        while ((line = br.readLine()) != null) {
            result += line;
        }
        
        System.out.println("result:" + result);
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);
        
        // 사용자 정보 JSON 구조에 맞게 수정
        String email = jsonNode.get("email").asText();
        System.out.println("email:" + email);
        
        br.close();
        
        return email;
    }
    
    public String refreshGoogleToken(String refreshToken) throws Exception {
        URL url = new URL("https://oauth2.googleapis.com/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=refresh_token");
        sb.append("&client_id=" + clientId);
        sb.append("&client_secret=" + clientSecret);
        sb.append("&refresh_token=" + refreshToken);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(sb.toString());
        bw.flush();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String result = br.readLine();
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);
        
        if (jsonNode.has("access_token")) {
            return jsonNode.get("access_token").asText();
        }

        return null;
    }
}
