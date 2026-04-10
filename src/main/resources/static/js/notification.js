let stompClient = null; // 웹소켓(STOMP) 연결 객체
let unreadCount = 0; // 안 읽은 알림 개수

//초기 함수 세팅
function initNotification(memberId) { // memberId가 없거나 이미 연결돼 있으면 연결 안 함
    if (!memberId) return;
    if (stompClient && stompClient.connected) return;

    connectWebSocket(memberId);
    loadUnreadCount(memberId);
}

function connectWebSocket(memberId) {
    const socket = new SockJS('/ws?memberId=' + memberId); // /ws?meberId=1로 접속, 서버에서 Principal = memberId로 인식
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        console.log("🔔 알림 WebSocket 연결 성공");

		// 메시지 받았을 때
        stompClient.subscribe("/user/queue/notifications", function (message) {
            const notification = JSON.parse(message.body); //JSON 파싱

            unreadCount++; // 안 읽은 개수 증가
			//UI 업데이트
            updateBadge(); 
            showToast(notification.message);

            document.dispatchEvent(new CustomEvent("notification-received", {
                detail: notification
            }));
        });
    }, function (error) {
        console.error("🔴 알림 WebSocket 연결 실패:", error);
    });
}

function loadUnreadCount(memberId) {
    fetch('/api/notifications/' + memberId + '/unread-count')
        .then(res => {
            if (!res.ok) throw new Error("unread count 조회 실패");
            return res.json();
        })
        .then(count => {
            unreadCount = count;
            updateBadge();
        })
        .catch(err => console.error(err));
}

function updateBadge() { 
    const badge = document.getElementById("notiBadge");
    if (!badge) return;

    if (unreadCount > 0) {
        badge.style.display = "inline-block";
        badge.textContent = unreadCount > 99 ? "99+" : unreadCount;
    } else {
        badge.style.display = "none";
    }
}

function showToast(message) {
    const toast = document.createElement("div");
    toast.innerText = message;

    toast.style.position = "fixed";
    toast.style.bottom = "30px";
    toast.style.right = "30px";
    toast.style.background = "#1abc9c";
    toast.style.color = "white";
    toast.style.padding = "12px 18px";
    toast.style.borderRadius = "8px";
    toast.style.boxShadow = "0 4px 10px rgba(0,0,0,0.2)";
    toast.style.zIndex = 9999;

    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}