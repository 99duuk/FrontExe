package mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection 클래스 : MySQL DB에 연결하는 클래스
 */
public class DBConnection {
	// 필드 정의

	// JDBC 프로토콜
	private static final String protocol = "jdbc";
	// MySQL 벤더
	private static final String vendor = ":mysql:";
	// MySQL 서버 주소와 포트 번호
	private static final String location = "//192.168.0.5:3306/";
	// 데이터베이스 이름
	private static final String databaseName = "bitbox";
	// 데이터베이스 접속을 위한 사용자 이름
	private static final String userName = "bitbox";
	// 데이터베이스 접속을 위한 비밀번호
	private static String password = "20240315!!";

	// 데이터베이스 접속 URL
	private static final String DB_URL = protocol + vendor + location + databaseName;
	// JDBC URL
	private static final String jdbcUrl = DB_URL + "?serverTimezone = UTC";

	// JDBC 드라이버 클래스 이름
	private static final String driver = "com.mysql.cj.jdbc.Driver";

	// Connection 인터페이스 객체
	public static Connection connection;

	// DB 연결을 열기 위한 메서드
	/**
	 * 데이터베이스에 연결하고 Connection 객체를 생성하는 메서드입니다.
	 */
	public static void openConnection() {
		try {
			// JDBC 드라이버를 로드합니다.
			Class.forName(driver);
			// DriverManager를 사용하여 데이터베이스에 연결합니다.
			connection = DriverManager.getConnection(jdbcUrl, userName, password);
			// 연결 성공 메시지를 출력합니다.
			System.out.println("Connection successful!\n\n");
		} catch (Exception e) {
			// 오류 발생 시 오류 메시지를 출력하고 예외 스택 트레이스를 출력합니다.
			System.out.println("Error : " + e.getMessage());
			e.printStackTrace();
		}
	}

	// DB 연결을 닫기 위한 메서드
	/**
	 * Connection 객체를 닫는 메서드입니다.
	 */
	public static void closeConnection() {
		try {
			// Connection을 닫습니다.
			connection.close();
			// 연결이 닫혔음을 알리는 메시지를 출력합니다.
			System.out.println("\n\nConnection closed!");
		} catch (Exception e) {
			// 오류 발생 시 오류 메시지를 출력합니다.
			System.out.println("Error : " + e.getMessage());
		}
	}

	// Connection 객체를 반환하는 메서드
	/**
	 * Connection 객체를 반환하는 메서드입니다.
	 * 
	 * @return Connection 객체
	 */
	public static Connection getConnection() {
		// Connection 객체가 null인 경우에만 연결을 생성합니다.
		if (connection == null) {
			try {
				// DriverManager를 사용하여 데이터베이스에 연결합니다.
				connection = DriverManager.getConnection(jdbcUrl, userName, password);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// Connection 객체를 반환합니다.
		return connection;
	}
}
