package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import DTO.User;
import mysql.DBConnection;

public class UserDAO {

	/**
	 * insertUser : 새로운 사용자를 데이터베이스에 추가하는 메서드입니다. 동일한 사용자가 이미 존재하는지 확인한 후, 새로운 사용자
	 * 정보를 데이터베이스에 추가합니다.
	 * 
	 * @param user 추가할 사용자 정보를 담은 User 객체입니다.
	 * @return int 가입 성공 여부를 나타내는 정수값입니다. 0은 가입 실패, 1은 가입 성공, 2는 이미 사용 중인 아이디임을
	 *         나타냅니다.
	 * @throws Exception SQL 관련 예외가 발생할 경우 처리합니다.
	 */
	public static int joinUser(User user) throws Exception {
		// 결과값 초기화
		int result = 0;

		// 동일한 유저가 이미 존재하는지 확인
		if (isUsernameExists(user.getUsername())) {
			result = 2; // 이미 존재하는 유저일 경우
			return result;
		}

		// SQL 쿼리문 작성
		String sql = "INSERT INTO user (username, password) VALUES (?, ?)";
		try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(sql)) {
			// PreparedStatement에 값 설정
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPassword());

			// 쿼리 실행 및 결과값 반환
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			// 예외 발생 시 오류 출력
			e.printStackTrace();
		}
		// 가입 성공 여부 반환
		return result;
	}

	/**
	 * loginUser : 사용자 로그인 기능을 수행하는 메서드입니다. 사용자가 입력한 ID와 비밀번호를 검증하여 데이터베이스에 저장된 정보와
	 * 일치하는지 확인합니다.
	 * 
	 * @param user 로그인을 시도하는 사용자 정보를 담은 User 객체입니다.
	 * @return int 로그인 성공 시 해당 사용자의 고유 ID를 반환하고, 실패 시 0을 반환합니다.
	 * @throws Exception SQL 관련 예외가 발생할 경우 처리합니다.
	 */
	public static int loginUser(User user) throws Exception {
		// 초기값 설정
		int userId = 0;

		// SQL 쿼리문 작성
		String sql = "SELECT userId FROM user WHERE username = ? AND password = ?";
		try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(sql)) {
			// PreparedStatement에 값 설정
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPassword());

			// 쿼리 실행 및 결과값 처리
			ResultSet resultSet = pstmt.executeQuery();

			// 결과가 존재하는 경우
			if (resultSet.next()) {
				// 로그인 성공 시 유저 ID 설정
				userId = resultSet.getInt("userId");
			}
		} catch (Exception e) {
			// 예외 발생 시 오류 출력
			e.printStackTrace();
		}

		// 로그인 결과 출력 후 유저 ID 반환
		System.out.printf("\n\n<<  %s  >>\n\n", userId == 0 ? "로그인에 실패했습니다. 다시 시도해 주세요." : "로그인에 성공했습니다.");
		return userId;
	}

	/**
	 * isUsernameExists : 데이터베이스에 이미 동일한 ID의 유저가 있는지 확인하는 메서드입니다. 입력받은 username을
	 * 이용하여 데이터베이스에 쿼리를 실행하여 해당 유저가 존재하는지 확인합니다.
	 * 
	 * @param username 존재 여부를 확인할 유저 ID입니다.
	 * @return boolean 해당 유저 ID가 데이터베이스에 존재하면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
	 */
	public static boolean isUsernameExists(String username) {
		// SQL 쿼리문 작성
		String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
		try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(sql)) {
			// PreparedStatement에 값 설정
			pstmt.setString(1, username);

			// 쿼리 실행 및 결과값 처리
			ResultSet resultSet = pstmt.executeQuery();
			resultSet.next();
			int count = resultSet.getInt(1);

			// 결과값 반환
			return count > 0;
		} catch (Exception e) {
			// 예외 발생 시 오류 출력
			e.printStackTrace();
		}

		// 기본적으로 false 반환
		return false;
	}
}