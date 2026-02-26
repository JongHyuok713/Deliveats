console.log("order-cart.js loaded");

import {
    fetchCart,
    formatWon,
    escapeHtml,
    updateCartItemQuantity,
    removeCartItem,
    updateCartIconUI
} from "../cart/cart-util.js";

document.addEventListener("DOMContentLoaded", async () => {
  await renderOrderCart();
  bindOrderCartEvents();
});

async function renderOrderCart() {
    const cartList = document.getElementById("cartList");
    const totalPriceEl = document.getElementById("totalPrice");

    if (!cartList || !totalPriceEl) return;

    const cart = await fetchCart();

    if (!cart || !cart.items || cart.items.length === 0) {
      cartList.innerHTML = "<p>장바구니가 비어있습니다.</p>";
      totalPriceEl.textContent = formatWon(0);
      await updateCartIconUI();
      return;
    }

    let html = "";
    for (const item of cart.items) {
      const itemId = Number(item.itemId);
      const name = escapeHtml(item.menuName ?? "");
      const imageUrl = item.imageUrl || "/images/no-image.jpg";
      const price = Number(item.price) || 0;
      const qty = Number(item.quantity) || 1;
      const itemTotal = price * qty;

      html += `
      <div class="cart-item" data-item-id="${itemId}">
        <img src="${escapeHtml(imageUrl)}" alt="${name}">
        <div class="cart-info">
          <h3>${name}</h3>
          <p>${formatWon(price)} x <span class="qty-text">${qty}</span></p>
          <p>계: ${formatWon(itemTotal)}</p>

          <div class="cart-actions">
            <button class="qty-minus" type="button">-</button>
            <button class="qty-plus" type="button">+</button>
            <button class="remove" type="button">삭제</button>
          </div>
        </div>
      </div>
    `;
    }

    cartList.innerHTML = html;
    totalPriceEl.textContent = formatWon(cart.totalPrice);

    await updateCartIconUI();
}

function bindOrderCartEvents() {
  const cartList = document.getElementById("cartList");
  if (!cartList) return;

  cartList.addEventListener("click", async (e) => {
    const target = e.target;
    if (!(target instanceof HTMLElement)) return;

    const itemEl = target.closest(".cart-item");
    if (!itemEl) return;

    const itemId = Number(itemEl.dataset.itemId);

    // 삭제
    if (target.classList.contains("remove")) {
      const res = await removeCartItem(itemId);
      if (!res.ok) alert("삭제에 실패했습니다.");
      await renderOrderCart();
      return;
    }

    // 수량 -/+
    const qtyText = itemEl.querySelector(".qty-text");
    const currentQty = Math.max(1, Number(qtyText?.textContent) || 1);

    if (target.classList.contains("qty-minus")) {
      const next = Math.max(1, currentQty - 1);
      const res = await updateCartItemQuantity(itemId, next);
      if (!res.ok) alert("수량 변경에 실패했습니다.");
      await renderOrderCart();
      return;
    }

    if (target.classList.contains("qty-plus")) {
      const next = currentQty + 1;
      const res = await updateCartItemQuantity(itemId, next);
      if (!res.ok) alert("수량 변경에 실패했습니다.");
      await renderOrderCart();
      return;
    }
  });

  document.getElementById("orderBtn")?.addEventListener("click", () => {
    window.location.href = "/order/create";
  });
}