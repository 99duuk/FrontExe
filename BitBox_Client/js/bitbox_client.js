// 서버 IP 주소 및 포트 설정
const SERVER_IP = "127.0.0.1"; // 서버 IP 주소 설정
const SERVER_PORT = 6454; // 서버 포트 설정
const server_address = `ws://${SERVER_IP}:${SERVER_PORT}`; // ws://127.0.0.1:9000

// WebSocket 객체 생성 및 서버에 연결
const socket = new WebSocket(server_address);

// 소켓 연결이 성공했을 때 실행되는 함수
socket.onopen = function (e) {
  const log_msg = "[open] 연결이 설정되었습니다."; // 연결 성공 메시지
  displayMessage("#messages", log_msg); // 메시지 출력 함수 호출
};

// 소켓 연결이 종료될 때 실행되는 함수
socket.onclose = function (e) {
  let log_msg = "";
  if (e.wasClean)
    log_msg = `[close] 연결이 정상적으로 종료되었습니다. 코드=${e.code}, 원인=${e.reason}`; // 정상 종료 메시지
  else
    log_msg = `[close] 연결이 비정상적으로 종료되었습니다.. 코드=${e.code}, 원인=${e.reason}`; // 비정상 종료 메시지
  displayMessage("#messages", log_msg); // 메시지 출력 함수 호출
};

// 소켓 에러가 발생했을 때 실행되는 함수
socket.onerror = function (error) {
  const log_msg = `[error] ${error.message}`; // 에러 메시지
  displayMessage("#messages", log_msg); // 메시지 출력 함수 호출
};

// 서버로부터 메시지를 수신했을 때 실행되는 함수
socket.onmessage = function (e) {
  const log_msg = `[message] 서버로부터 데이터 수신 : ${e.data}`; // 수신한 데이터 메시지
  displayMessage("#messages", log_msg); // 메시지 출력 함수 호출
  displayPacketMessage("#messages", e.data); // 통신 패킷 출력 함수 호출
};

// 메시지 전송 함수
const sendMessage = function (message) {
  socket.send(message); // 서버로 메시지 전송
  const log_msg = `클라이언트 => 서버 ${message}`; // 전송한 메시지 로그
  displayMessage("#messages", log_msg); // 메시지 출력 함수 호출
};

// 이벤트 로그 및 메시지 출력 함수
const displayMessage = function ($parentSelector, log_msg, kind_log = 0) {
  if (kind_log === 0 || kind_log === 2) console.log(log_msg); // 콘솔에 로그 출력
  if (kind_log === 1 || kind_log === 2) {
    const parentElem = document.querySelector($parentSelector); // 부모 요소 선택
    const childElem = document.createElement("div"); // 자식 요소 생성
    childElem.textContent = log_msg; // 텍스트 콘텐츠 설정
    parentElem.appendChild(childElem); // 부모 요소에 자식 요소 추가
  }
};

// 통신 패킷 출력 함수
const displayPacketMessage = function ($parentSelector, message) {
  const parentElem = document.querySelector($parentSelector); // 부모 요소 선택
  const msgObj = JSON.parse(message); // JSON 문자열을 JavaScript 객체로 변환

  let msg = "";
  switch (msgObj.cmd) {
    case "connect":
      msg = msgObj.result; // 연결 결과 메시지
      break;
    case "login":
      msg = msgObj.result === "ok" ? "로그인 성공" : "로그인 실패"; // 로그인 결과 메시지
      break;
    case "roomchoice":
        msg = msgObj.result === "ok"? "채팅방 선택 성공" : "채팅방 선택 실패"; // 방 선택 결과 메시지
      break;
    case "allchat":
      if ("result" in msgObj)
        msg = msgObj.result === "ok" ? "채팅 전송 성공" : "채팅 전송 실패";
      // 채팅 전송 결과 메시지
      else if ("id" in msgObj) msg = `${msgObj.id} => ${msgObj.msg}`; // 채팅 메시지 출력
      break;
  }

  const childElem = document.createElement("div"); // 자식 요소 생성
  childElem.textContent = msg; // 텍스트 콘텐츠 설정
  parentElem.appendChild(childElem); // 부모 요소에 자식 요소 추가
};

// <---------------------------------추가-------------------------------->

// 회원가입 요청 함수
const sendJoinRequest = function (username, password) {
  // JSON 형식의 데이터를 생성하여 회원가입 요청 메시지 작성
  const joinData = {
    cmd: "join",
    username: username,
    password: password,
  };

  // JSON 데이터를 문자열로 변환하여 서버로 전송
  sendMessage(JSON.stringify(joinData));
};


// <----------오류-------- -------------------------------->
// 예시: 회원가입 버튼 클릭 시 회원가입 요청 보내기
const btnJoin = document.getElementById("btnJoin");
btnJoin.addEventListener("click", function () {
  const username = document.getElementById("idInput").value; // 사용자명 입력란 값 가져오기
  const password = document.getElementById("passInput").value; // 비밀번호 입력란 값 가져오기

  // 회원가입 요청 보내기
  sendJoinRequest(username, password);
});

// <---------------------------------추가-------------------------------->

// 로그인 요청 함수
const sendLoginRequest = function (username, password) {
  // JSON 형식의 데이터를 생성하여 로그인 요청 메시지 작성
  const loginData = {
    cmd: "login",
    username: username,
    password: password,
  };

  // JSON 데이터를 문자열로 변환하여 서버로 전송
  sendMessage(JSON.stringify(loginData));
};

// 예시: 로그인 버튼 클릭 시 로그인 요청 보내기
const loginButton = document.getElementById("btnLogIn");
loginButton.addEventListener("click", function () {
  const username = document.getElementById("idInput").value; // 사용자명 입력란 값 가져오기
  const password = document.getElementById("passInput").value; // 비밀번호 입력란 값 가져오기

  // 로그인 요청 보내기
  sendLoginRequest(username, password);
});