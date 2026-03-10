import SockJS from "https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js";
import Stomp from "https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js";

document.addEventListener("DOMContentLoaded", () => {
    const orderId = getOrderIdFromUrl();
    connectWebSocket(orderId);
});

function getOrderIdFromUrl() {
    const parts = location.pathname.split("/");
    return parts[parts.length - 1];
}

function connectWebSocket(orderId) {
    const token = localStorage.getItem("accessToken");
    if (!token) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    const socket = new SockJS("/ws");
    const stomp = Stomp.over(socket);

    // 로그 줄이기
    stomp.debug = null;

    // CONNECT 헤더로 JWT 전달
    stomp.connect({ "Authorization": `Bearer ${token}` }, () => {
        stomp.subscribe(`/topic/orders/${orderId}`, msg => {
            const data = JSON.parse(msg.body);
            document.getElementById("orderStatus").textContent = data.status;
        });
    }, err => {
        console.error("WS connect error:", err);
        alert("실시간 연결에 실패했습니다. 다시 로그인 해주세요.");
        location.href = "/login";
    });
}

document.addEventListener("DOMContentLoaded", () => {
    loadOrderDetail();
});

async function loadOrderDetail() {
    const token = localStorage.getItem("accessToken");
    if (!token) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    const pathParts = location.pathname.split("/");
    const orderId = pathParts[pathParts.length - 1];

    const res = await fetch(`/api/orders/${orderId}`, {
        headers: { "Authorization": "Bearer " + token }
    });

    if (!res.ok) {
        alert("주문 정보를 불러올 수 없습니다.");
        return;
    }

    const order = await res.json();

    const infoBox = document.getElementById("orderInfo");
    const itemsBox = document.getElementById("orderItems");
    const totalEl = document.getElementById("detailTotalPrice");

    if (!infoBox || !itemsBox || !totalEl) return;

    infoBox.innerHTML = `
        <p><strong>주문번호</strong> #${order.id}</p>
        <p><strong>가게</strong> ${order.storeName}</p>
        <p><strong>상태</strong> ${order.status}</p>
        <p><strong>주문일시</strong> ${order.createdAt || ""}</p>
    `;

    itemsBox.innerHTML = order.items.map(it => `
        <div class="item">
            <div>${it.menuName}</div>
            <div>${it.quantity}개</div>
            <div>${(it.price * it.quantity).toLocaleString("ko-KR")}원</div>
        </div>
    `).join("");

    totalEl.textContent = order.totalPrice.toLocaleString("ko-KR") + "원";
}