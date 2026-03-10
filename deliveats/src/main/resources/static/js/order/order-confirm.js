import {
    fetchCart,
    apiFetch,
    formatWon,
    escapeHtml
} from "../cart/cart-util.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadOrderConfirm();
    bindConfirmOrder();
});

let currentCart = null;

async function loadOrderConfirm() {
    currentCart = await fetchCart();

    if (!currentCart || !currentCart.items || currentCart.items.length === 0) {
        alert("장바구니가 비어 있습니다.");
        location.href = "/cart";
        return;
    }

    // 가게명
    document.getElementById("storeName").textContent = escapeHtml(currentCart.storeName ?? "-");

    // 메뉴 목록
    const itemsWrap = document.getElementById("orderItems");
    itemsWrap.innerHTML = currentCart.items.map(i => `
        <div class="order-item">
            <span class="name">${escapeHtml(i.menuName)}</span>
            <span class="qty">${i.quantity}개</span>
            <span class="price">${formatWon(i.price * i.quantity)}</span>
        </div>
    `).join("");

    // 총 금액
    document.getElementById("totalPrice").textContent = formatWon(currentCart.totalPrice);
}

function bindConfirmOrder() {
    const btn = document.getElementById("confirmOrderBtn");
    if (!btn) {
        console.warn("confirmOrderBtn not found");
        return;
    }

    btn.addEventListener("click", submitOrder);
}

async function submitOrder() {
     try {
        const res = await apiFetch("/api/orders", {
            method: "POST"
        });

        if (res.status === 401 || res.status === 403) {
            alert("로그인이 필요합니다.");
            location.href = "/login";
            return;
        }

        if (!res.ok) {
            const text = await res.text();
            throw new Error(text);
        }

        const order = await res.json();

        // 결제API 미구현 -> 바로 주문 상세 페이지로
        location.href = `/order/${order.id}`;

    } catch (e) {
        console.error("주문 실패:", e);
        alert("주문 처리 중 오류가 발생했습니다.");
    }
}