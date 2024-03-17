package DTO;

import java.io.Serializable;

/**
 * 용자 정보를 담는 User 클래스입니다. 
 * Cmd 클래스를 상속받아 Serializable을 구현하고 있습니다. 
 * 사용자의 고유 ID, 이름, 비밀번호를 멤버 변수로 가지고 있습니다. 
 * 주석을 통해 각 라인의 역할과 메서드의 기능이 설명
 * 
 * 
 * User 클래스 : 사용자 정보를 담는 클래스로, Cmd를 상속받아 Serializable을 구현합니다.
 */
public class User extends Cmd implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer userId; // 사용자 고유 ID
	private String username; // 사용자 이름
	private String password; // 사용자 비밀번호

	/**
	 * 기본 생성자
	 */
	public User() {
	}

	/**
	 * 사용자 정보를 초기화하는 생성자
	 * 
	 * @param cmd      명령어
	 * @param username 사용자 이름
	 * @param password 사용자 비밀번호
	 */
	public User(Object cmd, String username, String password) {
		this.cmd = cmd;
		this.username = username;
		this.password = password;
	}

	/**
	 * 사용자 고유 ID getter 메서드
	 * 
	 * @return userId 사용자 고유 ID
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * 사용자 고유 ID setter 메서드
	 * 
	 * @param userId 사용자 고유 ID
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * 사용자 이름 getter 메서드
	 * 
	 * @return username 사용자 이름
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 사용자 이름 setter 메서드
	 * 
	 * @param username 사용자 이름
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 사용자 비밀번호 getter 메서드
	 * 
	 * @return password 사용자 비밀번호
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 사용자 비밀번호 setter 메서드
	 * 
	 * @param password 사용자 비밀번호
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 객체를 문자열로 표현하는 메서드
	 * 
	 * @return 사용자 정보를 문자열로 반환
	 */
	@Override
	public String toString() {
		String str = String.format("아이디:%s \n이름:%s \n비밀번호:%s ", userId, username, password);
		return str;
	}
}
