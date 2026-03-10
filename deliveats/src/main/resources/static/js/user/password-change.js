document.addEventListener("DOMContentLoaded", () => {
    const btn = document.getElementById("pwChangeBtn");
    btn.addEventListener("click", changePassword);
});

async function changePassword() {
    const cur = document.getElementById("currentPw").value;
    const pw1 = document.getElementById("newPw").value;
    const pw2 = document.getElementById("newPw2").value;

    if (pw1 !== pw2) {
        alert("새 비밀번호가 일치하지 않습니다.");
        return;
    }

    const strongPw = /^(?=.*[0-9])(?=.*[A-Za-z])(?=.*[^A-Za-z0-9]).{8,}$/;
    if (!strongPw.test(pw1)) {
        alert("비밀번호는 8자 이상, 숫자/문자/특수문자를 포함해야 합니다.");
        return;
    }

    const res = await fetch("/api/users/me/password", {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
        },
        body: JSON.stringify({ currentPassword: cur, newPassword: pw1 })
    });

    if (res.ok) {
        alert("비밀번호가 변경되었습니다. 다시 로그인해주세요.");
        localStorage.removeItem("accessToken");
        window.location.href = "/login";
    } else {
        alert("비밀번호 변경 실패");
    }
}