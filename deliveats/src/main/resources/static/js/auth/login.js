console.log("login.js loaded");

document.addEventListener("DOMContentLoaded", () => {
    const loginBtn = document.getElementById("loginBtn");
    loginBtn.addEventListener("click", login);
});

async function login() {
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;

    if (!username || !password) {
        alert("아이디와 비밀번호를 입력하세요.");
        return;
    }

    const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        // 쿠키 전송 여부 결정
        credentials: "same-origin",
        body: JSON.stringify({ username, password })
    });

    if (!res.ok) {
        alert("로그인 실패");
        return;
    }

    const data = await res.json();
    if (!data.accessToken) {
        alert("토큰 발급 실패");
        return;
    }

    localStorage.setItem("accessToken", data.accessToken);
    location.href = "/";
}