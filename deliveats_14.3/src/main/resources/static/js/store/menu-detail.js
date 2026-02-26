import { updateCartIconUI } from "../cart/cart-util.js";

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".btn-cart")
    .forEach(btn => btn.addEventListener("click", addToCart));
});

async function addToCart(e) {
    const token = localStorage.getItem("accessToken");
    if (!token) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    const menuId = e.currentTarget.dataset.menuId;

    const res = await fetch("/api/cart/items", {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            menuId: Number(menuId),
            quantity: 1
        })
    });

    if (!res.ok) {
        const msg = await res.text();
        alert(msg || "장바구니 담기 실패");
        return;
    }

    alert("장바구니에 담겼습니다!");
    updateCartIconUI();
}