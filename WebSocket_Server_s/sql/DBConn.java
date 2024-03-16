package sql;

public class DBConn {

}
package db;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DBConfig
 * : 이 클래스는 MySQL JDBC 드라이버를 로드하여 데이터베이스 연결을 초기화합니다.
 * 
 * DBConnection
 * : 이 클래스는 데이터베이스 연결을 얻는 데 사용되는 메서드와 연결을 닫는 데 사용되는 메서드를 제공합니다.
 *   또한 자원 정리를 처리하기 위한 DBClose 내부 클래스를 포함합니다.
 * 
 * @author orbit
 */
public class DBConnection {
    // 데이터베이스 연결을 위한 JDBC URL
    private static final String DB_URL = "jdbc:mysql://192.168.0.5:3306/BitBox";
    private static final String userName = "bitbox";
    private static final String password = "20240315!!";

    // JDBC 드라이버 참조
    private static final String driver = "com.mysql.cj.jdbc.Driver";

    /**
     * initConnection
     * MySQL JDBC 드라이버를 로드하여 데이터베이스 연결을 초기화합니다.
     */
    public static void initConnection() {
        try {
            Class.forName(driver);
            System.out.println("드라이버 로딩 성공");
        } catch (ClassNotFoundException e) {
            System.out.println("DB 드라이버를 찾을 수 없거나 클래스 이름이 잘못되었습니다.");
            e.printStackTrace();
        }
    }

    /**
     * getConnection
     * 데이터베이스에 대한 연결을 반환합니다.
     * 
     * @return Connection 객체
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, userName, password);
            System.out.println("데이터베이스 연결 성공");
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 오류");
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * DBClose
     * : 자원 정리를 처리하기 위한 중첩 클래스입니다.
     */
    public static class DBClose {
        /**
         * close
         * 연결, 문장 및 결과 집합을 닫습니다.
         * 
         * @param conn 닫을 Connection 객체
         * @param stmt 닫을 Statement 객체
         * @param rs 닫을 ResultSet 객체
         */
        public static void close(Connection conn, Statement stmt, ResultSet rs) {
            try {
                if (conn != null) {
                    conn.close();
                }
                // 문장 및 결과 집합을 닫아야 하는 경우 아래 줄의 주석을 해제합니다.
                // if (stmt != null) {
                //     stmt.close();
                // }
                // if (rs != null) {
                //     rs.close();
                // }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
