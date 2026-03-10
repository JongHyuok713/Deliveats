const STATUS_TEXT = {
    PAID: "신규 주문",
    CONFIRMED: "조리중",
    DELIVERING: "배달중",
    COMPLETED: "완료"
};

let currentStatus = "PAID";

document.addEventListener("DOMContentLoaded", () => {
    bindFilter();
    loadOrders();
});

function bindFilter() {
    document.querySelectorAll(".order-filter button").forEach(btn => {
        btn.onclick = () => {
            document.querySelectorAll(".order-filter button")
            .forEach(b => b.classList.remove("active"));
            btn.classList.add("active");
            currentStatus = btn.dataset.status;
            loadOrders();
        };
    });
}

async function loadOrders() {
    const token = localStorage.getItem("accessToken");
    const res = await fetch(`/api/owner/orders?status=${currentStatus}`, {
        headers: { "Authorization": "Bearer " + token }
    });

    const orders = await res.json();
    renderOrders(orders);
}

function renderOrders(orders) {
    const box = document.getElementById("orderList");

    if (!orders.length) {
        box.innerHTML = `<p class="empty">주문이 없습니다.</p>`;
        return;
    }

    box.innerHTML = orders.map(o => `
        <div class="order-card">
            <div class="order-header">
                <div>#${o.id}</div>
                <div class="order-status">${STATUS_TEXT[o.status]}</div>
            </div>

            <div class="order-items">
                ${o.items.map(i => `
                    <div class="order-item">
                        <span>${i.menuName} * ${i.quantity}</span>
                        <span>${(i.price * i.quantity).toLocaleString()}원</span>
                    </div>
                `).join("")}
            </div>

            <div class="order-actions">
                ${renderActionButtons(o)}
            </div>
        </div>
    `).join("");
}

function renderActionButtons(order) {
    if (order.status === "PAID") {
        return `<button class="btn-confirm" onclick="confirmOrder(${order.id})">주문 수락</button>`;
    }
    if (order.status === "CONFIRMED") {
        return `<button class="btn-deliver" onclick="startDelivery(${order.id})">배달 요청</button>`;
    }
    return "";
}

async function confirmOrder(id) {
    await updateStatus(id, "confirm");
}

async function startDelivery(id) {
    await updateStatus(id, "deliver");
}

async function updateStatus(id, action) {
    const token = localStorage.getItem("accessToken");
    const res = await fetch(`/api/owner/orders/${id}/${action}`, {
        method: "PATCH",
        headers: { "Authorization": "Bearer " + token }
    });

    if (!res.ok) return alert("상태 변경 실패");
    loadOrders();
}