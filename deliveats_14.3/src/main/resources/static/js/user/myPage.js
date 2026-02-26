document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    loadUserFromJWT(token);
    loadUserInfo();

    registerButtonEvents();
});

function loadUserFromJWT(token) {
    try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        document.getElementById("mp-username").textContent = payload.username;
        document.getElementById("mp-role").textContent = payload.role;
    } catch (e) {
        console.error("JWT 파싱 오류:", e);
    }
}

async function loadUserInfo() {
    const res = await fetch("/api/users/me", {
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("accessToken")
        }
    });

    if (res.status === 401) {
        alert("로그인 세션이 만료되었습니다.");
        logout();
        return;
    }

    const user = await res.json();
    document.getElementById("mp-address").textContent = user.address || "미등록";
    document.getElementById("mp-phone").textContent = user.phone || "미등록";
}

function registerButtonEvents() {
    document.getElementById("editAddressBtn")
        .addEventListener("click", () => toggleForm("editAddressForm"));

    document.getElementById("editPhoneBtn")
        .addEventListener("click", () => toggleForm("editPhoneForm"));

    document.getElementById("saveAddressBtn")
        .addEventListener("click", updateAddress);

    document.getElementById("savePhoneBtn")
        .addEventListener("click", updatePhone);

    document.getElementById("logoutBtn")
        .addEventListener("click", logout);
}

function toggleForm(formId) {
    const form = document.getElementById(formId);
    form.classList.toggle("hidden");
    form.classList.toggle("slide-down");
}

async function updateAddress() {
    const newAddress = document.getElementById("newAddress").value;

    if (!newAddress.trim()) {
        alert("주소를 입력하세요.");
        return;
    }

    const res = await fetch("/api/users/me/address", {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + localStorage.getItem("accessToken")
        },
        body: JSON.stringify({ address: newAddress })
    });

    if (res.ok) {
        alert("주소가 변경되었습니다.");
        loadUserInfo();
    }
}

async function updatePhone() {
    const newPhone = document.getElementById("newPhone").value.trim();

    if (!newPhone) {
        alert("전화번호를 입력하세요.");
        return;
    }

    const res = await fetch("/api/users/me/phone", {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + localStorage.getItem("accessToken")
        },
        body: JSON.stringify({ phone: newPhone })
    });

    if (res.ok) {
        alert("전화번호가 변경되었습니다.");
        loadUserInfo();
    }
}

function logout() {
    localStorage.removeItem("accessToken");
    location.href = "/login";
}