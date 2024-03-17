package sql;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;


import db.DBConnection;

public class server {
	public static void main(String[] args) {
		// DB 연결 초기화
		DBConnection.openConnection(); // DB 연결을 초기화하는 메서드 호출

		final int PORT = 9000; // 서버 포트 번호를 상수로 지정
		Hashtable<String, Socket> clientHt = new Hashtable<>(); // 클라이언트 소켓을 관리하기 위한 해시테이블 생성

		try {
			ServerSocket serverSocket = new ServerSocket(PORT); // 지정된 포트 번호로 서버 소켓 생성
			String mainThreadName = Thread.currentThread().getName(); // 현재 실행 중인 스레드의 이름을 저장

			// 클라이언트의 연결 요청을 계속해서 대기하는 무한 루프
			while (true) {
				System.out.printf("[서버-%s] Client접속을 기다립니다...\n", mainThreadName); // 대기 메시지 출력
				Socket socket = serverSocket.accept(); // 클라이언트의 연결 요청을 받아들이고 클라이언트 소켓 생성
				WorkerThread wt = new WorkerThread(socket, clientHt); // 클라이언트와 통신을 담당할 WorkerThread 객체 생성
				wt.start(); // WorkerThread 시작
			}
		} catch (Exception e) { // 예외 처리
			System.out.println(e.getMessage()); // 예외 메시지 출력
		}

		DBConnection.closeConnection(); // 서버 종료 시 DB 연결을 닫는 메서드 호출
	}
}

class WorkerThread extends Thread {
	private Socket socket; // 클라이언트와의 통신을 위한 소켓 객체
	private Hashtable<String, Socket> ht; // 클라이언트 소켓을 관리하는 해시테이블
	private Cmd cmdObj; // 클라이언트로부터 전달된 명령 객체
	private ToDoList toDoListObj; // 클라이언트로부터 전달된 ToDoList 객체
	private Diary diaryObj; // 클라이언트로부터 전달된 Diary 객체
	private static Integer userId; // 현재 작업 중인 사용자의 ID

	// WorkerThread 생성자
	public WorkerThread(Socket socket, Hashtable<String, Socket> ht) {
		this.socket = socket; // 소켓 객체 초기화
		this.ht = ht; // 해시테이블 초기화
	}

	@Override
	public void run() {
	    try {
	        // 클라이언트의 IP 주소를 가져옵니다.
	        InetAddress inetAddr = socket.getInetAddress();
	        // 클라이언트의 IP 주소와 현재 스레드의 이름을 출력합니다.
	        System.out.printf("<서버-%s>%s로부터 접속했습니다.\n", getName(), inetAddr.getHostAddress());

	        // 클라이언트와의 통신을 위한 입출력 스트림을 생성합니다.
	        OutputStream out = socket.getOutputStream();
	        InputStream in = socket.getInputStream();
	        // 객체를 주고받기 위한 ObjectOutputStream과 ObjectInputStream을 생성합니다.
	        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

	        // 클라이언트로부터 전송된 객체를 수신하고 처리하는 무한 루프입니다.
	        while (true) {
	            // 클라이언트로부터 전송된 객체를 수신합니다.
	            Object packetObj = ois.readObject();

	            // 수신된 객체가 User 클래스의 인스턴스인지 확인합니다.
	            if (packetObj instanceof User) {
	                // User 클래스의 인스턴스인 경우, 해당 패킷을 처리하는 메서드를 호출합니다.
	                processUserPacket(packetObj);
	            } 
	            // 수신된 객체가 ToDoList 또는 Diary 클래스의 인스턴스인지 확인합니다.
	            else if (packetObj instanceof ToDoList || packetObj instanceof Diary) {
	                // ToDoList 또는 Diary 클래스의 인스턴스인 경우, 해당 패킷을 처리하는 메서드를 호출합니다.
	                processMenuPacket(packetObj, oos);
	            }
	        }
	    } 
	    // 예외 처리
	    catch (Exception e) {
	        // 예외가 발생한 경우, 예외 메시지를 출력합니다.
	        System.out.printf("<서버-%s>%s\n", getName(), e.getMessage());
	    }
	}


	private void processMenuPacket(Object packetObj, ObjectOutputStream oos) throws Exception {
		// 받은 패킷 객체를 Cmd 타입으로 캐스팅합니다.
		cmdObj = (Cmd) packetObj;

		// 만약 Cmd 객체가 ToDoList인 경우 ToDoList 객체로 캐스팅하고, Diary인 경우 Diary 객체로 캐스팅합니다.
		if (cmdObj instanceof ToDoList) {
			toDoListObj = (ToDoList) cmdObj;
		} else if (cmdObj instanceof Diary) {
			diaryObj = (Diary) cmdObj;
		}

		// Cmd 객체에서 명령을 가져와서 switch 문을 통해 해당하는 작업을 수행합니다.
		switch ((int) cmdObj.getCmd()) {
		// 투두리스트 작성: ToDoListDAO를 사용하여 새로운 투두리스트를 데이터베이스에 추가합니다.
		case ServiceMenu2.투두리스트_작성:
			ToDoListDAO.insertToDoList(DBConnection.getConnection(), toDoListObj);
			break;
		// 투두리스트 삭제: ToDoListDAO를 사용하여 투두리스트를 데이터베이스에서 삭제합니다.
		case ServiceMenu2.투두리스트_삭제:
			ToDoListDAO.deleteToDoList(DBConnection.getConnection(), toDoListObj);
			break;
		// 투두리스트 수정: ToDoListDAO를 사용하여 투두리스트를 데이터베이스에서 수정합니다.
		case ServiceMenu2.투두리스트_수정:
			ToDoListDAO.updateToDoList(DBConnection.getConnection(), toDoListObj);
			break;
		// 투두리스트 전체 조회: ToDoListDAO를 사용하여 모든 투두리스트를 데이터베이스에서 가져옵니다.
		case ServiceMenu2.투두리스트_전체_조회:
			ToDoListDAO.getTodoList("all", toDoListObj);
			break;
		// 완료된 투두리스트 조회: ToDoListDAO를 사용하여 완료된 투두리스트를 데이터베이스에서 가져옵니다.
		case ServiceMenu2.투두리스트_완료:
			ToDoListDAO.getTodoList("completed", toDoListObj);
			break;
		// 미완료된 투두리스트 조회: ToDoListDAO를 사용하여 미완료된 투두리스트를 데이터베이스에서 가져옵니다.
		case ServiceMenu2.투두리스트_미완료:
			ToDoListDAO.getTodoList("incomplete", toDoListObj);
			break;
		// 다이어리 작성: DiaryDAO를 사용하여 새로운 다이어리를 데이터베이스에 추가합니다.
		case ServiceMenu2.다이어리_작성:
			DiaryDAO.writeDiary(DBConnection.getConnection(), diaryObj);
			break;
		// 다이어리 삭제: DiaryDAO를 사용하여 다이어리를 데이터베이스에서 삭제합니다.
		case ServiceMenu2.다이어리_삭제:
			DiaryDAO.deleteDiary(DBConnection.getConnection(), diaryObj);
			break;
		// 다이어리 수정: DiaryDAO를 사용하여 다이어리를 데이터베이스에서 수정합니다.
		case ServiceMenu2.다이어리_수정:
			DiaryDAO.updateDiary(DBConnection.getConnection(), diaryObj);
			break;
		// 다이어리 전체 조회: DiaryDAO를 사용하여 모든 다이어리를 데이터베이스에서 가져옵니다.
		case ServiceMenu2.다이어리_전체_조회:
			DiaryDAO.getDiary("all", diaryObj);
			break;
		// 특정 날짜의 다이어리 조회: DiaryDAO를 사용하여 특정 날짜의 다이어리를 데이터베이스에서 가져옵니다.
		case ServiceMenu2.다이어리_특정날짜:
			DiaryDAO.getDiary("specdate", diaryObj);
			break;
		}
	}

	private void processUserPacket(Object packetObj) throws Exception {
		// 응답 객체를 초기화합니다.
		Object ackObj = new Object();

		// 패킷 객체를 Cmd 타입으로 캐스팅합니다.
		cmdObj = (Cmd) packetObj;

		// 명령이 "회원가입"인 경우
		if (cmdObj.getCmd().equals("회원가입")) {
			try {
				// Cmd 객체가 User 객체인지 확인합니다.
				if (cmdObj instanceof User) {
					// User 객체로 형변환하여 사용합니다.
					User userObj = (User) cmdObj;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 명령이 "로그인"인 경우
		} else if (cmdObj.getCmd().equals("로그인")) {
			try {
				// Cmd 객체를 User 객체로 형변환하여 사용합니다.
				User userObj = (User) cmdObj;
				// UserDAO를 사용하여 로그인을 시도하고, 사용자 ID를 받아옵니다.
				userId = UserDAO.loginUser(userObj);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 명령이 12인 경우 (로그아웃)
		} else if ((Integer) cmdObj.getCmd() == 12) {
			// 로그아웃을 응답하기 위해 ACK 메시지를 생성합니다.
			String ack = ackObj.toString();
			// 소켓의 출력 스트림을 사용하여 ACK 메시지를 클라이언트로 전송합니다.
			OutputStream out = this.socket.getOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
			pw.println(ack);
			pw.flush();
		}
	}
}