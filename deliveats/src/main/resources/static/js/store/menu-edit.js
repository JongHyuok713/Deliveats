import { uploadImage } from "./upload-util.js";

console.log("menu-edit.js loaded");

document.addEventListener("DOMContentLoaded", () => {
    loadMenuInfo();
    initPreview();
    initSubmit();
});

// 공통 JWT
function getToken() {
    return localStorage.getItem("accessToken");
}

// 기존 메뉴 정보 불러오기
async function loadMenuInfo() {
    const storeId = document.getElementById("storeId").value;
    const menuId = document.getElementById("menuId").value;

    const res = await fetch(`/api/stores/${storeId}`);
    if (!res.ok) {
        alert("가게 정보를 불러오지 못했습니다.");
        return;
    }

    const store = await res.json();
    const menu = store.menus.find(m => m.id == menuId);

    if (!menu) {
        alert("메뉴 정보를 찾을 수 없습니다.");
        return;
    }

    document.getElementById("name").value = menu.name;
    document.getElementById("price").value = menu.price;
    document.getElementById("isAvailable").value = menu.available;
    document.getElementById("imageUrl").value = menu.imageUrl || "";

    const preview = document.getElementById("imagePreview");
    preview.innerHTML = menu.imageUrl
        ? `<img src="${menu.imageUrl}">`
        : `<p>이미지를 선택하세요.</p>`;
}

// 이미지 미리보기
function initPreview() {
    const input = document.getElementById("uploadImage");
    const preview = document.getElementById("imagePreview");

    input.addEventListener("change", e => {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = () =>
            preview.innerHTML = `<img src="${reader.result}" />`;
        reader.readAsDataURL(file);
    });
}

// 저장하기
function initSubmit() {
    const btn = document.getElementById("saveMenuBtn");

    btn.addEventListener("click", async () => {
        const token = getToken();
        const storeId = document.getElementById("storeId").value;
        const menuId = document.getElementById("menuId").value;

        let name = document.getElementById("name").value.trim();
        let price = Number(document.getElementById("price").value);
        let isAvailable = document.getElementById("isAvailable").value === "true";
        let imageUrl = document.getElementById("imageUrl").value.trim();

        const file = document.getElementById("uploadImage").files[0];

        if (!name || isNaN(price)) {
            alert("입력값을 확인해주세요.");
            return;
        }

        // 파일 업로드 발생 시 업로드 API 사용
        if (file) {
            try {
                imageUrl = await uploadImage(file, "menu", storeId, token);
            } catch (err) {
                alert("이미지 업로드 실패");
                return;
            }
        }

        const body = {
            name,
            price,
            imageUrl: imageUrl || null,
            isAvailable
        };

        const res = await fetch(`/api/stores/${storeId}/menus/${menuId}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });

        if (!res.ok) {
            alert("메뉴 수정 실패");
            return;
        }

        alert("메뉴 수정이 완료되었습니다!");
        location.href = "/stores/my";
    });
}
