import { getCart, clearCart } from "../cart/cart-util.js";

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("orderBtn")?.addEventListener("click", submitOrder);
});

async function submitOrder() {
    const cart = getCart();
    if (!cart) return alert("장바구니가 비어있습니다.");

    const token = localStorage.getItem("accessToken");

    const res = await fetch("/api/orders", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
            storeId: cart.storeId,
            items: cart.items.map(i => ({
                menuId: i.menuId,
                quantity: i.quantity
            }))
        })
    });

    if (!res.ok) return alert("주문 실패");

    clearCart();
    location.href = "/orders/my";
}