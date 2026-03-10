import { updateCartIconUI } from "../cart/cart-util.js";

console.log("navbar.js loaded");

document.addEventListener("DOMContentLoaded", ()=> {
    initNavbar();
});

async function initNavbar() {
    const token = localStorage.getItem("accessToken");

    const els = {
        login: document.getElementById("loginLink"),
        register: document.getElementById("registerLink"),

        user: document.getElementById("menuUser"),
        owner: document.getElementById("menuOwner"),
        rider: document.getElementById("menuRider"),
        admin: document.getElementById("menuAdmin"),

        cart: document.getElementById("navCartIcon"),

        profileWrapper: document.getElementById("profileMenuWrapper"),
        profileIcon: document.getElementById("profileIcon"),
        profileDropdown: document.getElementById("profileDropdown"),
        profileUsername: document.getElementById("profileUsername"),
        profileRole: document.getElementById("profileRole"),
        profileInitial: document.getElementById("profileInitial"),
        profileLogoutBtn: document.getElementById("profileLogoutBtn"),

        mobile: document.getElementById("mobileMenu"),
        overlay: document.getElementById("mobileOverlay"),
        hamburger: document.getElementById("hamburgerBtn")
    };

    // 초기 상태
    initHiddenState(els);

    // 비로그인 상태
    if (!token) {
        els.login.classList.remove("hidden");
        els.register.classList.remove("hidden");

        buildMobileMenu(els, null);
        bindMobileMenuEvents(els);
        return;
    }

    // 로그인 상태
    try {
        const res = await fetch("/api/users/me", {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!res.ok) throw new Error("Unauthorized");

        const user = await res.json();
        applyUserUI(els, user);
    } catch (e) {
        console.warn("로그인 정보 로딩 실패:", e);
        localStorage.removeItem("accessToken");
        location.href = "/login";
    }
}

// 초기 hidden 처리
function initHiddenState(els) {
    [
        els.login,
        els.register,
        els.user,
        els.owner,
        els.rider,
        els.admin,
        els.cart,
        els.profileWrapper,
        els.profileDropdown
    ].forEach(el => el && el.classList.add("hidden"));
}

// 로그인 UI 적용
function applyUserUI(els, user) {
    const role = user.role;

    // 공통 로그인 UI
    els.profileWrapper.classList.remove("hidden");

    // 역할별 메뉴
    if (role === "USER") {
        els.user.classList.remove("hidden");
        els.cart.classList.remove("hidden");
        updateCartIconUI();
    } else {
        els.cart && els.cart.classList.add("hidden");
    }
    
    if (role === "OWNER") els.owner.classList.remove("hidden");
    if (role === "RIDER") els.rider.classList.remove("hidden");
    if (role === "ADMIN") els.admin.classList.remove("hidden");

    // 프로필 정보
    els.profileUsername.textContent = user.username;
    els.profileRole.textContent = role;
    els.profileInitial.textContent = user.username.charAt(0).toUpperCase();

    bindProfileDropdown(els);
    buildMobileMenu(els, role);
    bindMobileMenuEvents(els);
}

// 프로필 드롭다운
function bindProfileDropdown(els) {
    let opened = false;

    els.profileIcon.onclick = e => {
        e.stopPropagation();
        opened = !opened;
        els.profileDropdown.classList.toggle("hidden", !opened);
    };

    document.addEventListener("click", () => {
        if (!opened) return;
        opened = false;
        els.profileDropdown.classList.add("hidden");
    });

    els.profileLogoutBtn.onclick = async () => {
        try {
            await fetch("/api/auth/logout", { method: "POST" });
        } catch (e) {
            console.warn("로그아웃 요청 실패");
        }

        localStorage.removeItem("accessToken");
        location.href = "/";
    };
}

// 모바일 메뉴
function buildMobileMenu(els, role) {
    const menu = els.mobile;
    if (!menu) return;
    menu.innerHTML = "";

    const add = (text, link) => {
        const a = document.createElement("a");
        a.href = link;
        a.textContent = text;
        menu.appendChild(a);
    };

    add("홈", "/");
    add("가게 찾기", "/stores");

    if (!role) {
        add("로그인", "/login");
        add("회원가입", "/register");
        return;
    }

    if (role === "USER") add("내 주문", "/orders/my");
    if (role === "OWNER") add("내 가게 관리", "/stores/my");
    if (role === "RIDER") add("배달 리스트", "/rider/deliveries");
    if (role === "ADMIN") add("관리자", "/admin");

    const logout = document.createElement("button");
    logout.className = "mobile-logout-btn";
    logout.textContent = "로그아웃";
    logout.onclick = () => {
        localStorage.removeItem("accessToken");
        location.href = "/";
    };
    menu.appendChild(logout);
}

// 모바일 슬라이드 UX
function bindMobileMenuEvents(els) {
    const { mobile, overlay, hamburger } = els;
    if (!mobile || !hamburger) return;

    hamburger.onclick = () => {
        mobile.classList.remove("hidden");
        overlay.classList.remove("hidden");
        requestAnimationFrame(() => mobile.classList.add("open"));
    };

    overlay.onclick = close;
    mobile.onclick = e => {
        if (e.target.tagName === "A" || e.target.tagName === "BUTTON") {
            close();
        }
    };

    function close() {
        mobile.classList.remove("open");
        overlay.classList.add("hidden");
        setTimeout(() => mobile.classList.add("hidden"), 300);
    }
}