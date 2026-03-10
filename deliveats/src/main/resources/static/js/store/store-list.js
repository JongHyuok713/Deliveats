console.log("store-list.js loaded");

let currentUser = null;

document.addEventListener("DOMContentLoaded", () => {
    initOwnerButtonAndUser();
    initCategoryFilter();
    initViewButtons();
});

async function initOwnerButtonAndUser() {
    const token = localStorage.getItem("accessToken");
    if (!token) return;

    try {
        const res = await fetch("/api/users/me", {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            console.warn("failed to load /api/users/me:", res.status);
            return;
        }

        const user = await res.json();
        currentUser = user;

        if (user.role === "OWNER") {
            const btn = document.getElementById("btnRegisterStore");
            if (btn) btn.style.display = "inline-flex";
        }
    } catch (err) {
        console.error("사용자 정보 조회 실패:", err);
    }
}

function initCategoryFilter() {
    const buttons = document.querySelectorAll(".filter-btn");
    const cards = document.querySelectorAll(".store-card");
    if (!buttons.length || !cards.length) return;

    buttons.forEach(btn => {
        btn.addEventListener("click", () => {
            const filter = btn.dataset.filter;

            buttons.forEach(b => b.classList.remove("active"));
            btn.classList.add("active");

            cards.forEach(card => {
                const cat = (card.dataset.category || "").trim();

                if (filter === "ALL" || cat === filter) {
                    card.style.display = "";
                } else {
                    card.style.display = "none";
                }
            });
        });
    });
}

function initViewButtons() {
    const btns = document.querySelectorAll(".view-store-btn");
    if (!btns.length) return;

    btns.forEach(btn => {
        btn.addEventListener("click", async (e) => {
            e.preventDefault();

            const storeId = btn.dataset.storeId;
            if (!storeId) return;

            const token = localStorage.getItem("accessToken");

            // 1. 토큰 없으면 가게 상세 페이지로 이동
            if (!token) {
                window.location.href = `/stores/${storeId}`;
                return;
            }

            try {
                if (!currentUser) {
                    await initOwnerButtonAndUser();
                }

                if (!currentUser) {
                    window.location.href = `/stores/${storeId}`;
                    return;
                }

                if (currentUser.role !== "OWNER") {
                    window.location.href = `/stores/${storeId}`;
                    return;
                }

                const res = await fetch(`/api/stores/${storeId}/owner`);
                if (!res.ok) {
                    console.warn("owner check failed:", res.status);
                    window.location.href = `/stores/${storeId}`;
                    return;
                }

                const data = await res.json();
                const ownerId = data.ownerId;

                if (ownerId && currentUser.id && Number(ownerId) === Number(currentUser.id)) {
                    // 내 가게 → 내 가게 관리 페이지로 이동
                    window.location.href = "/stores/my";
                } else {
                    // 다른 가게 → 상세페이지
                    window.location.href = `/stores/${storeId}`;
                }
            } catch (err) {
                console.error("view-store-btn-error:", err);
                window.location.href = `/stores/${storeId}`;
            }
        });
    });
}
