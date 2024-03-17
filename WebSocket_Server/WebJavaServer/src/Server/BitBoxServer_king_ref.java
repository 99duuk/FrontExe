package Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import DTO.Cmd;
import mysql.DBConnection; //  DBConnection 클래스 import 


public class BitBoxServer_king_ref extends WebSocketServer {

	public static void main(String[] args) {

		// <--------------------------추가--------------> DB 연결을 초기화하는 메서드 호출
		DBConnection.openConnection();
		// <--------------------------추가-------------->

		// String host = "127.0.0.1"; // localhost
		Hashtable<String, Socket> clientHt = new Hashtable<>(); // 클라이언트 소켓을 관리하기 위한 해시테이블 생성
		final int PORT = 6454;

		try { // <--------------------------추가-------------->
			ServerSocket serverSocket = new ServerSocket(PORT); // 지정된 포트 번호로 서버 소켓 생성
			String mainThreadName = Thread.currentThread().getName(); // 현재 실행 중인 스레드의 이름을 저장
			// <--------------------------추가-------------->

			while (true) {
				System.out.printf("[BitBox 서버-%s Client 접속을 기다립니다... \n", mainThreadName);
				Socket socket = serverSocket.accept(); // 클라이언트의 연결 요청 받아들이고 클라이언트 소켓 생성
				WorkerThread wt = new WorkerThread(socket, clientHt); // 클라이언트의 통신을 담당할 WorkerThread 객체 생성
				wt.start(); // WorkerThread 시작
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// <--------------------------추가--------------> 서버 종료 시 DB 연결을 닫는 메서드 호출
		DBConnection.closeConnection();
		// <--------------------------추가-------------->

		// WebSocketServer server = new BitBoxServer(new InetSocketAddress(host, PORT));
		// server.run(); // 서버 돌아가기 시작~

	}

	class WorkerThread extends Thread {
		private Socket socket; 					//클라이언트와 통신을 위한 소켓 객체
		private Hashtable<String, Socket> ht; 	//클라이언트 소켓을 관리하는 해시 테이블 
		private Cmd cmdObj;						//클라이언트로부터 전달된 명령 객체
		private Cmd user_id;					//현재 작업 중인 사용자의 ID
		
		// WorkerThread의 생성자
		public WorkerThread(Socket socket, Hashtable<String, Socket>ht) {
			this.socket = socket;				//소켓 객체 초기화
			this.ht = ht;						//해시테이블 초기화 
		}
	
	
	}


	// 서버가 해당 IP 주소와 포트에서 클라이언트의 연결을 기다리고 소켓 통신을 수행할 수 있도록
	// 주어진 IP 주소와 포트로 웹 소켓 서버를 초기화하는 역할
	public BitBoxServer_king_ref(InetSocketAddress inetAddr) {
		super(inetAddr);
	}

	// 클라이언트와의 연결이 성립되었을 때 실행, 클라이언트에게 연결 성공 메시지를 보내는 역할
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		// 클라이언트의 IP 주소를 가져와서 문자열로 변환합니다.
		String hostIp = conn.getRemoteSocketAddress().getAddress().getHostAddress().toString();
		// 클라이언트의 IP 주소와 함께 "connected" 메시지를 출력합니다.
		System.out.println(hostIp + " connected");

		// 클라이언트에게 보낼 응답 메시지를 생성합니다.
		JSONObject ackObj = new JSONObject();
		// 응답 메시지에 'cmd' 필드를 추가하고 값으로 "connect"를 설정합니다.
		ackObj.put("cmd", "connect");
		// 응답 메시지에 'result' 필드를 추가하고 값으로 "Welcome to the Server!"를 설정합니다.
		ackObj.put("result", "Welcome to the Server!");
		// 클라이언트에게 응답 메시지를 문자열 형태로 전송합니다.
		conn.send(ackObj.toString()); // 클라이언트한테 메시지 보내기
	}

	@Override
	public void onStart() {
		System.out.println("Server started successfully!!!");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println(conn + " has diconnected");
		// ex.getStackTrace(); 생략하고 위에만 보여주는 걸로...

	}

	@Override // 샌드리시브
	public void onError(WebSocket conn, Exception ex) {
		System.out.println(ex.getMessage());
		// ex.getStackTrace();
	}

	@Override
		public void onMessage(WebSocket conn, String message) {
			System.out.println("Message from client: " + message);

			// json 문자열 to json 객체
			JSONObject msgObj = new JSONObject(message);
			String cmd = msgObj.getString("cmd"); // 패킷 종류를 구분할 수 있는 명령어를 제일 먼저 추출

			if (cmd.equals("login")) {
				String id = msgObj.getString("id");
				String pass = msgObj.getString("pass");
				System.out.printf("로그인 id: %s   pass: %s\n", id, pass);

				/*
				 * DB에서 해당 id, pass가 있는 지 확인하는 작업 추가
				 */

				JSONObject ackObj = new JSONObject();
				ackObj.put("cmd", "login");
				ackObj.put("result", "ok");
				conn.send(ackObj.toString()); // json문자열로 변환되어서 클라이언트한테 전송됨
			} else if (cmd.equals("allchat")) { // 접속된 대상에게 브로드캐스트 해야겠죠?

				String id = msgObj.getString("id"); // 누가 보냈는지 알아야하니까 id

				String msg = msgObj.getString("msg"); // 뭘 보냈는지 알아야하니까 msg

				System.out.printf("채팅 id: %s   msg: %s\n", id, msg);

				// 클라이언트한테 응답 전송
				JSONObject ackObj = new JSONObject();
				ackObj.put("cmd", "allchat");
				ackObj.put("result", "ok");
				conn.send(ackObj.toString());

				// 전체 접속자한테 브로드캐스팅
				for (WebSocket con : this.getConnections()) {
					if (conn != con) // 나를 제외한 모든 접속자한테 전송
						con.send(message);
				}
			}
	}
}
