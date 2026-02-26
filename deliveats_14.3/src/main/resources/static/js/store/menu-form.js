import { uploadImage } from "./upload-util.js";

console.log("menu-form.js loaded");

document.addEventListener("DOMContentLoaded", () => {
    initImagePreview();
    initSubmit();
});

// 이미지 파일 업로드 시 미리보기 표시
function initImagePreview() {
    const uploadInput = document.getElementById("uploadImage");
    const preview = document.getElementById("imgPreview");

    if (!uploadInput || !preview) {
        console.error("이미지 미리보기 요소를 찾을 수 없습니다.");
        return;
    }

    uploadInput.addEventListener("change", (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = () => {
            preview.innerHTML = `<img src="${reader.result}" alt="미리보기 이미지">`;
        };
        reader.readAsDataURL(file);
    });
}

// 제출 처리
function initSubmit() {
    const submitBtn = document.getElementById("menuSubmitBtn");

    submitBtn.addEventListener("click", async () => {
        const storeId = document.getElementById("storeId").value;
        const token = localStorage.getItem("accessToken");

        if (!token) {
            alert("로그인 후 이용해주세요.");
            return;
        }

        const name = document.getElementById("name").value.trim();
        const price = Number(document.getElementById("price").value);
        let imageUrl = document.getElementById("imageUrl").value.trim();
        const uploadFile = document.getElementById("uploadImage").files[0];

        if (!name || isNaN(price)) {
            alert("모든 항목을 올바르게 입력해주세요.");
            return;
        }

        // 파일 업로드가 있다면 서버에 이미지 업로드 API 호출
        if (uploadFile) {
            try {
                imageUrl = await uploadImage(uploadFile, "menu", storeId, token);
            } catch (err) {
                console.error(err);
                alert("이미지 업로드 실패!");
                return;
            }
        }

        const body = {
            name,
            price,
            imageUrl: imageUrl || null
        };

        // 메뉴 등록 API 호출
        const res = await fetch(`/api/stores/${storeId}/menus`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });

        if (!res.ok) {
            const msg = await res.text();
            alert("등록 실패: " + msg);
            return;
        }
        
        alert("메뉴가 등록되었습니다!");
        location.href = "/stores/my";
    });
}