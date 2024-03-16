package db;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
	public static Connection getConnection() {
	    Connection conn = null;
	    try {
	        conn = DriverManager.getConnection("jdbc:mysql://192.168.0.5:3306/BitBox", "bitbox", "20240315!!");
	        System.out.println("db연결");
	    } catch (SQLException e) {
	        System.out.println("DB 연결 오류");
	        e.printStackTrace();
	    }
	    return conn;
	}
	
	public class DBClose {
	    public static void close(Connection conn, Statement stmt, ResultSet rs) {
	        try {
	            if(conn != null) {
	                conn.close();
	            }
//	            if(stmt != null) {
//	                stmt.close();
//	            }
//	            if(rs != null) {
//	                rs.close();
//	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
}
