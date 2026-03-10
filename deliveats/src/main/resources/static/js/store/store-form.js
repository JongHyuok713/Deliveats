console.log("store-form.js loaded");

// safeJson 파서
async function safeJson(res) {
    try {
        return await res.json();
    } catch (err) {
        return null;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const btn = document.getElementById("storeSubmitBtn");
    if (!btn) {
        console.error("storeSubmitBtn not found!");
        return;
    }

    btn.addEventListener("click", async () => {
        const token = localStorage.getItem("accessToken");
        if (!token) {
            alert("로그인 후 이용해주세요.");
            return;
        }

        let payload = null;
        try {
            payload = JSON.parse(atob(token.split(".")[1]));
        } catch (e) {
            console.error("JWT parsing failed:", e);
            alert("로그인 정보가 유효하지 않습니다. 다시 로그인해주세요.");
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            window.location.href = "/login";
            return;
        }

        if (payload.role !== "OWNER") {
            alert("점주만 가게를 등록할 수 있습니다.");
            return;
        }

        const name = document.getElementById("name").value.trim();
        const category = document.getElementById("category").value.trim();
        const address = document.getElementById("address").value.trim();
        const minOrderPrice = Number(document.getElementById("minOrderPrice").value.trim());

        if (!name || !category || !address || isNaN(minOrderPrice)) {
            alert("모든 값을 정확히 입력해주세요.");
            return;
        }

        const body = { name, category, address, minOrderPrice };

        try {
            const res = await fetch("/api/stores/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(body)
            });

            if (res.ok) {
                alert("가게가 성공적으로 등록되었습니다!");
                window.location.href = "/stores/my";
                return;
            }

            const errorMsg = await res.text();

            if (res.status === 403) {
                alert("가게 등록 권한이 없습니다.");
            } else if (res.status === 400) {
                alert("잘못된 요청입니다: " + errorMsg);
            } else {
                alert("등록 실패: " + errorMsg);
            }
        } catch (err) {
            console.error("Server error:", err);
            alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    });
});