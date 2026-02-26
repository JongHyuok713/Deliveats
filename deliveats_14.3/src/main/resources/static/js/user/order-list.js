document.addEventListener("DOMContentLoaded", () => {
    bindEvents();
    loadOrderList(0);
});

function bindEvents() {
    const listBox = document.getElementById("orderList");
    const pageBox = document.getElementById("pagination");

    // 주문 상세 이동
    listBox?.addEventListener("click", e => {
        const card = e.target.closest(".order-card");
        if (!card) return;
        if (!e.target.classList.contains("btn-detail")) return;

        const id = card.dataset.id;
        location.href = `/orders/${id}`;
    });

    // 페이지네이션
    pageBox?.addEventListener("click", e => {
        e.preventDefault();
        const pageAttr = e.target.getAttribute("data-page");
        if (pageAttr == null) return;

        loadOrderList(Number(pageAttr));
    });
}

async function loadOrderList(page) {
    const token = localStorage.getItem("accessToken");
    if (!token) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    const res = await fetch(`/api/orders/my?page=${page}`, {
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (!res.ok) {
        alert("주문 목록을 불러오지 못했습니다.");
        return;
    }

    const data = await res.json();
    renderOrderList(data.content || []);
    renderPagination(data.number, data.totalPages);
}

function renderOrderList(orders) {
    const listBox = document.getElementById("orderList");
    if (!listBox) return;

    if (!orders.length) {
        listBox.innerHTML = `<p>주문 내역이 없습니다.</p>`;
        return;
    }

    listBox.innerHTML = orders.map(o => `
        <div class="order-card" data-id="${o.id}">
            <h3>${o.storeName}</h3>
            <p>상태: ${o.status}</p>
            <p>총 금액: ${o.totalPrice.toLocaleString("ko-KR")}원</p>
            <p>주문일: ${o.createdAt ?? ""}</p>
            <button class="btn-detail">상세 보기</button>
        </div>
    `).join("");
}

function renderPagination(currentPage, totalPages) {
    const pageBox = document.getElementById("pagination");
    if (!pageBox) return;

    if (!totalPages || totalPages <= 1) {
        pageBox.innerHTML = "";
        return;
    }

    let html = "";

    if (currentPage > 0) {
        html += `<a href="#" data-page="${currentPage - 1}">이전</a>`;
    } else {
        html += `<span class="disabled">이전</span>`;
    }

    html += `<span>${currentPage + 1} / ${totalPages}</span>`;

    if (currentPage < totalPages - 1) {
        html += `<a href="#" data-page="${currentPage + 1}">다음</a>`;
    } else {
        html += `<span class="disabled">다음</span>`;
    }

    pageBox.innerHTML = html;
}