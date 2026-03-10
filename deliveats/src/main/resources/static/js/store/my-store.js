console.log("my-store.js loaded");

document.addEventListener("DOMContentLoaded", () => {
    loadMyStore();
});

// JWT token 가져오기
function getToken() {
    return localStorage.getItem("accessToken");
}

// 내 가게 데이터 불러오기
async function loadMyStore() {
    const token = getToken();
    if (!token) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    try {
        const res = await fetch("/api/stores/owner/stores/my", {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!res.ok) {
            const msg = await res.text();
            console.error("getMyStore error:", res.status, msg);
            alert("내 가게 정보를 불러오지 못했습니다.");
            return;
        }

        const data = await res.json();
        const { store, menus, reviews } = data;

        document.getElementById("storeName").textContent = store.name;
        document.getElementById("storeCategory").textContent = store.category;
        document.getElementById("storeAddress").textContent = store.address;
        document.getElementById("storeMinOrderPrice").textContent = store.minOrderPrice;

        renderOwnerMenuButton(store.id);
        renderMenuList(menus, store.id);
        renderReviewList(reviews, store.id);
    } catch (e) {
        console.error("loadMyStore exception:", e);
        alert("서버 오류가 발생했습니다.");
    }
}

// 메뉴 추가 버튼 표시
function renderOwnerMenuButton(storeId) {
    const area = document.getElementById("ownerMenuButtonArea");
    area.innerHTML = `
        <a href="/stores/${storeId}/menu/register" class="owner-add-menu-btn">
            메뉴 추가
        </a>
    `;
}

// 메뉴 리스트 렌더링
function renderMenuList(menus, storeId) {
    const wrap = document.getElementById("menuList");

    if (!menus || menus.length === 0) {
        wrap.innerHTML = "<p>등록된 메뉴가 없습니다.</p>";
        return;
    }

    wrap.innerHTML = menus.map(m => `
        <div class="menu-card">
            <img src="${m.imageUrl || '/images/no-image.jpg'}" alt="${m.name}">
            <h3>${m.name}</h3>
            <p>${m.price}원</p>

            <div class="menu-owner-actions">
                <button class="menu-edit-btn" data-id="${m.id}" data-store="${storeId}">
                    수정
                </button>
                <button class="menu-delete-btn" data-id="${m.id}" data-store="${storeId}">
                    삭제
                </button>
            </div>
        </div>
    `).join("");
    wrap.addEventListener("click", menuActionHandler);
}

// 메뉴 수정/삭제 핸들러
async function menuActionHandler(e) {
    const token = getToken();
    if (!token) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    const editBtn = e.target.closest(".menu-edit-btn");
    const deleteBtn = e.target.closest(".menu-delete-btn");

    // 메뉴 수정
    if (editBtn) {
        const menuId = editBtn.dataset.id;
        const storeId = editBtn.dataset.store;

        location.href = `/stores/${storeId}/menu/${menuId}/edit`;
        return;
    }

    // 메뉴 삭제
    if (deleteBtn) {
        const menuId = deleteBtn.dataset.id;
        const storeId = deleteBtn.dataset.store;

        if (!confirm("정말 삭제하시겠습니까?")) return;

        try {
            const res = await fetch(`/api/stores/${storeId}/menus/${menuId}`, {
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (!res.ok) {
                const msg = await res.text();
                alert("삭제 실패: " + msg);
                return;
            }
            alert("삭제되었습니다.");
            location.reload();
        } catch (e) {
            console.error("delete menu error:", e);
            alert("서버 오류가 발샐했습니다.");
        }
    }
}

// 리뷰 관리
function renderReviewList(reviews, storeId) {
    const wrap = document.getElementById("reviewList");

    if (!reviews || reviews.length === 0) {
        wrap.innerHTML = "<p>등록된 리뷰가 없습니다.</p>";
        return;
    }

    wrap.innerHTML = reviews.map(r => `
        <div class="review-card">
            <div class="review-user">${r.userName}</div>
            <div class="review-content">${r.content}</div>
            <div class="review-date">${(r.createdAt || '').toString().substring(0,10)}</div>

            <button class="review-delete-btn" data-id="${r.id}" data-store="${storeId}">
                리뷰 삭제
            </button>
        </div>
    `).join("");

    wrap.addEventListener("click", reviewActionHandler);
}

async function reviewActionHandler(e) {
    const token = getToken();
    const btn = e.target.closest(".review-delete-btn");
    if (!btn) return;

    if (!confirm("해당 리뷰를 삭제하시겠습니까?")) return;

    const reviewId = btn.dataset.id;

    try {
        const res = await fetch(`/api/reviews/${reviewId}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!res.ok) {
            const msg = await res.text();
            alert("삭제 실패: " + msg);
            return;
        }

        alert("리뷰 삭제 완료");
        location.reload();
    } catch (e) {
        console.error("delete review error:", e);
        alert("서버 오류가 발생했습니다.");
    }
}