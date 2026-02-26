import { addCartItem, updateCartIconUI, getToken } from "../cart/cart-util.js";
import { getCurrentUser } from "../auth/auth-util.js";

document.addEventListener("DOMContentLoaded", async () => {
    const user = await getCurrentUser();

    if (!user || user.role !== "USER") {
        document.querySelectorAll(".btn-cart").forEach(b => b.remove());
        return;
    }

    bindAddToCartButtons();
});

function bindAddToCartButtons() {
    document.querySelectorAll(".btn-cart").forEach((btn) => {
        btn.addEventListener("click", async (e) => {
            e.preventDefault();
            e.stopPropagation();

            const token = getToken();
            if (!token) {
                alert("로그인이 필요합니다.");
                location.href = "/login";
                return;
            }

            const menuId = Number(btn.dataset.menuId);
      const res = await addCartItem(menuId, 1);

      if (!res.ok) {
        alert("장바구니 담기에 실패했습니다.");
        return;
      }

      await updateCartIconUI();
      alert("장바구니에 추가되었습니다.");
    });
  });
}