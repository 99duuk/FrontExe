package server;


import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;



public class TestServer2 extends WebSocketServer{
	
	public static void main(String[] args) {
		String host = "127.0.0.1";		// localhost
		final int PORT = 6454;
		
		WebSocketServer server = new TestServer2(new InetSocketAddress(host, PORT));
		server.run();	 	//서버 돌아가기 시작~
	}
	
	public TestServer2(InetSocketAddress inetAddr) {
		super(inetAddr);
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println(conn + " has diconnected");		
		//ex.getStackTrace(); 생략하고 위에만 보여주는 걸로...

	}

	@Override //샌드리시브
	public void onError(WebSocket conn, Exception ex) {
		System.out.println(ex.getMessage());
		//ex.getStackTrace();
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		System.out.println("Message from client: " + message);
		
		// json 문자열 to json 객체
		JSONObject msgObj = new JSONObject(message);
		String cmd = msgObj.getString("cmd");		// 패킷 종류를 구분할 수 있는 명령어를 제일 먼저 추출
		
		if(cmd.equals("login")) {
			String id = msgObj.getString("id");
			String pass = msgObj.getString("pass");
			System.out.printf("로그인 id: %s   pass: %s\n", id, pass);
			
			/* DB에서 해당 id, pass가 있는 지 확인하는 작업 추가
			 * */
			
			JSONObject ackObj = new JSONObject();
			ackObj.put("cmd", "login");
			ackObj.put("result", "ok");
			conn.send(ackObj.toString());   // json문자열로 변환되어서 클라이언트한테 전송됨
		}else if(cmd.equals("allchat")) {	//접속된 대상에게 브로드캐스트 해야겠죠?

			String id = msgObj.getString("id");		//누가 보냈는지 알아야하니까 id

			String msg = msgObj.getString("msg");	//뭘 보냈는지 알아야하니까 msg

			System.out.printf("채팅 id: %s   msg: %s\n", id, msg);
			
			// 클라이언트한테 응답 전송
			JSONObject ackObj = new JSONObject();
			ackObj.put("cmd", "allchat");
			ackObj.put("result", "ok");
			conn.send(ackObj.toString());
			
			// 전체 접속자한테 브로드캐스팅
			for(WebSocket con : this.getConnections()) {
				if(conn != con)			// 나를 제외한 모든 접속자한테 전송
					con.send(message);
			}
		}
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		String hostIp = conn.getRemoteSocketAddress().getAddress().getHostAddress().toString();
		System.out.println(hostIp + " connected");
		
		JSONObject ackObj = new JSONObject();
		ackObj.put("cmd", "connect");
		ackObj.put("result", "하이루 방가방가");
		conn.send(ackObj.toString());   // 클라이언트한테 메시지 보내기
		
	}

	@Override
	public void onStart() {
		System.out.println("Server started successfully!!!");		
	}

}