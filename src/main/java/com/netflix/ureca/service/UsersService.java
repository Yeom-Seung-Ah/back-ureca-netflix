package com.netflix.ureca.service;

import java.sql.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.ureca.dao.LoginDao;
import com.netflix.ureca.dao.SaltDao;
import com.netflix.ureca.dao.UsersDao;
import com.netflix.ureca.dto.Login;
import com.netflix.ureca.dto.SaltInfo;
import com.netflix.ureca.dto.Users;
import com.netflix.ureca.util.OpenCrypt;

@Service
public class UsersService {
	
	@Autowired
	UsersDao usersDao;
	
	@Autowired
	LoginDao loginDao;
	
	@Autowired
	SaltDao saltDao;
	
	@Autowired
	GoogleOAuthService googleOAuthService;

	
	// 토큰을 검증하면서 만료 여부도 체크
	public Login checkToken(String authorization) throws Exception {
		Login login = loginDao.checkToken(authorization);
		
		if (login == null) {
			throw new RuntimeException("토큰이 유효하지 않습니다.");
		}
		
		// 현재 시간과 loginTime + 30분을 비교하여 토큰 만료 여부 확인
		Long now = System.currentTimeMillis(); 
		Long tokenExpiryTime = login.getLoginTime().getTime() + (30 * 60 * 1000);

			// 토큰 만료됨 → 로그아웃 처리
		if(now > tokenExpiryTime) {	
			loginDao.deleteToken(login.getUserId());
			throw new RuntimeException("토큰이 만료되었습니다. 다시 로그인하세요.");
		}

		// 요청할 때마다 loginTime을 현재 시간으로 갱신
		loginDao.updateLoginTime(login.getUserId(), new java.sql.Timestamp(System.currentTimeMillis()));


		return login;
	}
	
	public Login refreshAccessToken(String userId) throws Exception {
	    String refreshToken = loginDao.getRefreshToken(userId);

	    if (refreshToken == null) {
	        return null; // 리프레시 토큰이 없으면 새로 로그인해야 함
	    }

	    // Google API를 통해 새 액세스 토큰 요청
	    String newAccessToken = googleOAuthService.refreshGoogleToken(refreshToken);
	    if (newAccessToken == null) {
	        return null; // 토큰 갱신 실패 시 로그아웃
	    }

	    // 새로운 액세스 토큰을 DB에 업데이트
	    loginDao.updateAccessToken(userId, newAccessToken);
	    return loginDao.checkToken(newAccessToken);
	}

	
	public Login googleLogin(Users u) throws Exception {
	    String userId = u.getUserId();
	    System.out.println("구글 로그인 요청 - 사용자 ID: " + userId);

	    // 로그인 성공 시 토큰 생성
	    String salt = UUID.randomUUID().toString();
	    byte[] originalHash = OpenCrypt.getSHA256(userId, salt);
	    String myToken = OpenCrypt.byteArrayToHex(originalHash);

	    Date loginTime = new Date(System.currentTimeMillis());
	    Login loginInfo = new Login(userId, myToken, u.getUserName(), loginTime);

	    System.out.println("구글 로그인 성공! 토큰 생성 - " + loginInfo);

	    try {
	        // 기존 토큰 삭제
	        loginDao.deleteToken(userId);  // ✅ 기존 로그인 데이터 삭제 (중복 방지)
	    } catch (Exception e) {
	        System.out.println("기존 토큰이 없어 삭제하지 않음: " + e.getMessage());
	    }

	    // 새로운 토큰 삽입
	    loginDao.insertToken(loginInfo);
	    System.out.println("새로운 토큰 저장 완료: " + loginInfo);

	    return loginInfo;
	}



	public Login tokenLogin(Users u) throws Exception {
		String userId = u.getUserId();
		
		// 1. DB에서 사용자의 salt를 가져옴
		SaltInfo saltInfo = saltDao.selectSalt(userId);
		if (saltInfo == null) {
			return null; // 해당 유저의 salt가 없으면 로그인 불가
		}

		String pwd = u.getUserPwd(); // 클라이언트에게서 받은 비밀번호(암호화되지 않은 값)

		// 2. 서버에서 salt를 추가하여 암호화
		byte[] pwdHash = OpenCrypt.getSHA256(pwd, saltInfo.getSalt());
		String pwdHashHex = OpenCrypt.byteArrayToHex(pwdHash);
		u.setUserPwd(pwdHashHex);

		// 3. 암호화된 비밀번호로 검증
		u = usersDao.login(u);

		if (u != null) {
			String name = u.getUserName();
			if (name != null && !name.trim().isEmpty()) {
				// 로그인 성공 시 토큰 생성

				// 1. salt를 생성
				String salt = UUID.randomUUID().toString();
				// 2. id를 hashing
				byte[] originalHash = OpenCrypt.getSHA256(userId, salt);
				// 3. db에 저장하기 좋은 포맷으로 인코딩
				String myToken = OpenCrypt.byteArrayToHex(originalHash);

				// 4. login 테이블에 토큰 및 로그인 시간 저장
				Date loginTime = new Date(System.currentTimeMillis()); // 현재 시간으로 생성
				Login loginInfo = new Login(userId, myToken, name, loginTime);
				loginDao.insertToken(loginInfo);

				return loginInfo;
			}
		}

		return null;
	}

	// 사용자의 마지막 요청 시간을 갱신하는 메서드
	public void updateLoginTime(String userId) throws Exception {
	    java.sql.Timestamp newLoginTime = new java.sql.Timestamp(System.currentTimeMillis());
	    loginDao.updateLoginTime(userId, newLoginTime);
	}


	// 회원가입
	public void signup(Users u) throws Exception {
		String userId = u.getUserId();
		String pwd = u.getUserPwd();

		// 1. salt를 생성
		String salt = UUID.randomUUID().toString();
		// 2. pwd를 hashing
		byte[] originalHash = OpenCrypt.getSHA256(pwd, salt);
		// 3. db에 저장하기 좋은 포맷으로 인코딩
		String pwdHash = OpenCrypt.byteArrayToHex(originalHash);

		u.setUserPwd(pwdHash);

		saltDao.insertSalt(new SaltInfo(userId, salt));
		usersDao.signup(u);
	}

	public void logout(String authorization) throws Exception {
	    Login loginInfo = checkToken(authorization);
	    
	    if (loginInfo != null) {
	        String userId = loginInfo.getUserId();
	        System.out.println("✅ 로그아웃 요청 - 삭제할 userId: " + userId);

	        try {
	            loginDao.deleteToken(userId);
	            System.out.println("✅ 로그아웃 성공: login 테이블에서 userId=" + userId + " 삭제 완료");
	        } catch (Exception e) {
	            System.out.println("❌ 로그아웃 실패 - 삭제 중 오류 발생: " + e.getMessage());
	        }
	    } else {
	        System.out.println("❌ 로그아웃 실패 - 유효하지 않은 토큰");
	    }
	}

}
