package Server;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import DAO.UserDAO;
import DTO.Cmd;
import DTO.User;
import mysql.DBConnection; //  DBConnection 클래스 import 

public class BitBoxServer extends WebSocketServer {

	// DB연결 위한 DBConnection 객체 생성
	private DBConnection dbConnection;

	public static void main(String[] args) {
		String host = "127.0.0.1"; // localhost
		final int PORT = 6454;

		WebSocketServer server = new BitBoxServer(new InetSocketAddress(host, PORT));
		server.run(); // 서버 돌아가기 시작~
	}

	// 서버가 해당 IP 주소와 포트에서 클라이언트의 연결을 기다리고 소켓 통신을 수행할 수 있도록
	// 주어진 IP 주소와 포트로 웹 소켓 서버를 초기화하는 역할
	public BitBoxServer(InetSocketAddress inetAddr) {
		super(inetAddr);

		// DB객체 초기화
		dbConnection = new DBConnection();

	}

	
	@Override
	// WebSocket 연결이 닫혔을 때 호출, 해당 연결에 대한 정보와 닫힌 이유 등을 출력하는 역할
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println(conn + "연결 종료 !!!");
//		 ex.getStackTrace(); 생략하고 위에만 보여주는 걸로...

	}

	@Override // 샌드리시브
	// WebSocket 연결 중에 에러가 발생했을 때 호출, 해당 연결에 대한 정보와 발생한 에러 메시지를 출력
	public void onError(WebSocket conn, Exception ex) {
		System.out.println(ex.getMessage());
		// ex.getStackTrace();
	}

	@Override
	// WebSocket 서버가 클라이언트와의 연결을 맺었을 때 호출, 
	// 클라이언트의 연결 요청을 수락하고, 클라이언트와의 통신을 위한 초기 설정을 수행하는 데 사용
	// 연결이 성공적으로 이루어졌음을 확인하고, 클라이언트에게 환영 메시지나 기타 초기화 정보를 전송
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		String hostIp = conn.getRemoteSocketAddress().getAddress().getHostAddress().toString();
		System.out.println("호스트: " + hostIp + "연결됨");

		JSONObject ackObj = new JSONObject();
		ackObj.put("cmd", "connect");
		ackObj.put("result", "서버 연결 추카포카");
		conn.send(ackObj.toString()); // 클라이언트한테 메시지 보내기

	}

	@Override
	// WebSocket 서버가 시작되고 클라이언트의 연결을 기다리는 상태가 됐을 때 호출
	public void onStart() {
		System.out.println("서버 Run 성공 !!!");
	}
	
	
	/*
		public void HandleLogin(WebSocket conn) {
		UserDAO.joinUser(DBConnection.getConnection(),)
		
		//ToDoListDAO.insertToDoList(DBConnection.getConnection(), toDoListObj);
	}
	*/
	
	
	public void handleJoin(WebSocket conn, Cmd cmd, String username, String password) {
	    // 클라이언트로부터 받은 사용자 정보를 이용하여 User 객체 생성
	    User user = new User(cmd, username, password);

	    try {
	        // 회원가입 시도
	        int result = UserDAO.joinUser(user);
	        
	        // 회원가입 결과에 따라 응답 처리
	        switch (result) {
	            case 1:
	                // 회원가입 성공
	                conn.send("JOIN_SUCCESS");
	                break;
	            case 2:
	                // 이미 존재하는 아이디
	                conn.send("USERNAME_EXISTS");
	                break;
	            default:
	                // 그 외의 경우 (가입 실패 등)
	                conn.send("JOIN_FAILED");
	                break;
	        }
	    } catch (Exception e) {
	        // 예외 발생 시 오류 처리
	        e.printStackTrace();
	        // 클라이언트에게 오류 메시지 전송 등의 처리를 추가할 수 있습니다.
	    }
	}
	
	public void handleLogin(WebSocket conn, Cmd cmd, String username, String password) {
	    // 클라이언트로부터 받은 사용자 정보를 이용하여 User 객체 생성
	    User user = new User(cmd, username, password);

	    try {
	        // 로그인 시도
	        int result = UserDAO.loginUser(user);
	        
	        // 로그인 결과에 따라 응답 처리
	        switch (result) {
	            case 1:
	                // 로그인 성공
	                conn.send("JOIN_SUCCESS");
	                break;
	            case 2:
	                // 이미 존재하는 아이디
	                conn.send("USERNAME_EXISTS");
	                break;
	            default:
	                // 그 외의 경우 (가입 실패 등)
	                conn.send("JOIN_FAILED");
	                break;
	        }
	    } catch (Exception e) {
	        // 예외 발생 시 오류 처리
	        e.printStackTrace();
	        // 클라이언트에게 오류 메시지 전송 등의 처리를 추가할 수 있습니다.
	    }
	}
	
	/*
	public void handleRoomChoice(WebSocket conn, Cmd cmd, String userId, String roomId) {
	    // 클라이언트의 채팅방 선택을 처리하는 메서드
	    // 클라이언트를 선택한 채팅방에 입장시킵니다.

	    // 여기에 채팅방 선택 기능을 구현합니다.
	    try {
	        // 채팅방에 클라이언트를 입장시킵니다.
	        // 예를 들어, 클라이언트를 해당 채팅방의 멤버로 추가하는 등의 작업을 수행합니다.
	        // 이 부분은 프로젝트의 요구사항에 따라 다르므로 구체적인 구현은 생략합니다.
	        
	        // 예시: 채팅방에 클라이언트를 추가하는 코드
	        ChatRoom chatRoom = getChatRoomById(roomId); // roomId에 해당하는 채팅방 객체를 가져옵니다.
	        if (chatRoom != null) {
	            chatRoom.addMember(userId); // 해당 채팅방에 클라이언트를 추가합니다.
	            conn.send("ROOM_JOIN_SUCCESS"); // 클라이언트에게 채팅방 입장 성공 메시지를 전송합니다.
	        } else {
	            conn.send("ROOM_NOT_FOUND"); // 채팅방이 존재하지 않는 경우 클라이언트에게 알립니다.
	        }
	    } catch (Exception e) {
	        // 예외가 발생한 경우 처리합니다.
	        e.printStackTrace();
	        // 클라이언트에게 오류 메시지를 전송하거나 다른 작업을 수행할 수 있습니다.
	    }
	}
	*/
	

	public void onLogin() {
		
	}
	public void onMultiChat() {
		
	}
	public void onPost() {
		
	}
	public void onTodolist() {
		
	}
	
	
	
	@Override
	// 클라이언트로부터 메시지를 받았을 때 호출,
	// 클라이언트로부터 받은 메시지를 처리하고, 해당 메시지에 따라 동작을 수행
	// 받은 메시지가 JSON 형식으로 전달, 해당 JSON 객체에서 "cmd" 필드를 확인하여 어떤 동작을 수행할 지 결정
	// 
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
			
		} else if (cmd.equals("roomchoice")) {	// 채팅방 선택 명령어 처리
		        String id = msgObj.getString("id"); // 사용자 ID 추출
		        String roomId = msgObj.getString("roomid"); // 채팅방 번호 추출
		        System.out.printf("로그인 id: %s   채팅방 : %s\n", id, roomId);
		       // handleRoomChoice(conn, Cmd.ROOM_CHOICE, id, roomId);
		        
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