console.log("register.js loaded");

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("registerForm");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();
        const address = document.getElementById("address").value.trim();
        const phone = document.getElementById("phone").value.trim();
        const role = document.getElementById("role").value;

        if (!username || !password) {
            alert("아이디와 비밀번호는 필수입니다.");
            return;
        }

        // 역할에 따라 API 분기
        let url;
        switch (role) {
            case "USER":
                url = "/api/auth/register/user";
                break;
            case "OWNER":
                url = "/api/auth/register/owner";
                break;
            case "RIDER":
                url = "/api/auth/register/rider";
                break;
            default:
                alert("잘못된 역할입니다.");
                return;
        }

        try {
            const res = await fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({username, password, address, phone})
            });

            if (!res.ok) {
                const msg = await res.text();
                alert("회원가입 실패: " + msg);
                return;
            }

            alert("회원가입이 완료되었습니다.");
            location.href = "/login";
        } catch (e) {
            console.error("회원가입 오류:", e);
            alert("서버 오류가 발생했습니다.");
        }
    });
});