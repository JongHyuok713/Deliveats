import { uploadImage } from "./upload-util.js";

console.log("store-edit.js loaded");

document.addEventListener("DOMContentLoaded", () => {
    loadStoreData();
    initPreview();
    initSubmit();
});

// JWT role 읽기
function getToken() {
    return localStorage.getItem("accessToken");
}

// 현재 가게 데이터 로드
async function loadStoreData() {
    const storeId = document.getElementById("storeId").value;

    const res = await fetch(`/api/stores/${storeId}`);
    if (!res.ok) {
        alert("가게 정보를 불러올 수 없습니다.");
        return;
    }

    const store = await res.json();

    document.getElementById("name").value = store.name;
    document.getElementById("category").value = store.category;
    document.getElementById("address").value = store.address;
    document.getElementById("minOrderPrice").value = store.minOrderPrice;

    // 이미지 미리보기
    const preview = document.getElementById("imagePreview");
    preview.innerHTML = store.mainImage
        ? `<img src="${store.mainImage}" />`
        : `<p>이미지를 선택하세요.</p>`;
}

// 이미지 선택 시 미리보기
function initPreview() {
    const input =document.getElementById("uploadImage");
    const preview = document.getElementById("imagePreview");

    input.addEventListener("change", e => {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = () => {
            preview.innerHTML = `<img src="${reader.result}">`;
        };
        reader.readAsDataURL(file);
    });
}

// 가게 정보 저장
async function initSubmit() {
    const btn = document.getElementById("saveStoreBtn");

    btn.addEventListener("click", async () => {
        const token = getToken();
        const storeId = document.getElementById("storeId").value;

        if (!token) {
            alert("로그인이 필요합니다.");
            location.href = "/login";
            return;
        }

        let name = document.getElementById("name").value.trim();
        let category = document.getElementById("category").value.trim();
        let address = document.getElementById("address").value.trim();
        let minOrderPrice = Number(document.getElementById("minOrderPrice").value);
        let imageUrlText = document.getElementById("imageUrl").value.trim();
        let file = document.getElementById("uploadImage").files[0];

        if (!name || !category || !address || isNaN(minOrderPrice)) {
            alert("모든 입력란을 정확히 채워주세요.");
            return;
        }

        let finalImageUrl = imageUrlText || null;

        // 파일 업로드가 있다면 업로드 API 호출 (store 타입)
        if (file) {
            try {
                finalImageUrl = await uploadImage(file, "store", storeId, token);
            } catch (e) {
                alert("이미지 업로드 실패: " + e.message);
                ReadableByteStreamController;
            }
        }

        const body = {
            name,
            category,
            address,
            minOrderPrice,
            mainImage: finalImageUrl
        };

        const res = await fetch(`/api/stores/${storeId}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Aontent-Type": "application/json"
            },
            body: JSON.stringify(body)
        });

        if (!res.ok) {
            alert("수정 실패");
            return;
        }

        alert("가게 정보가 수정되었습니다.");
        location.href = "/stores/my";
    });
}