import {
    fetchCart,
    updateCartIconUI,
    formatWon,
    escapeHtml,
    updateCartItemQuantity,
    removeCartItem,
    clearServerCart
} from "./cart-util.js";

document.addEventListener("DOMContentLoaded", async () => {
    await renderCartFromServer();
    bindEvents();
    bindOrderButton();
});

async function renderCartFromServer() {
  const empty = document.getElementById("cartEmpty");
  const area = document.getElementById("cartArea");

  const cart = await fetchCart();

  if (!cart || !cart.items || cart.items.length === 0) {
    empty?.classList.remove("hidden");
    area?.classList.add("hidden");
    await updateCartIconUI();
    return;
  }

  empty?.classList.add("hidden");
  area?.classList.remove("hidden");

  const storeNameEl = document.getElementById("cartStoreName");
  if (storeNameEl) storeNameEl.textContent = cart.storeName ?? "";

  const totalPriceEl = document.getElementById("totalPrice");
  if (totalPriceEl) totalPriceEl.textContent = formatWon(cart.totalPrice);

  const itemsEl = document.getElementById("cartItems");
  if (!itemsEl) return;

  itemsEl.innerHTML = cart.items
    .map((i) => {
      const itemId = Number(i.itemId);
      const menuName = escapeHtml(i.menuName ?? "");
      const imageUrl = i.imageUrl || "/images/no-image.jpg";
      const price = Number(i.price) || 0;
      const qty = Number(i.quantity) || 1;

      return `
        <div class="cart-item" data-item-id="${itemId}">
          <img src="${escapeHtml(imageUrl)}" alt="${menuName}">
          <div class="name">${menuName}</div>
          <div class="price">${formatWon(price)}</div>
          <input type="number" class="qty" min="1" value="${qty}">
          <button class="remove" type="button">삭제</button>
        </div>
      `;
    })
    .join("");

  await updateCartIconUI();
}

function bindEvents() {
  document.getElementById("clearCartBtn")?.addEventListener("click", async () => {
    const res = await clearServerCart();
    if (!res.ok) {
      alert("장바구니 비우기에 실패했습니다.");
      return;
    }
    await renderCartFromServer();
  });

  document.getElementById("cartItems")?.addEventListener("click", async (e) => {
    const btn = e.target;
    if (!(btn instanceof HTMLElement)) return;
    if (!btn.classList.contains("remove")) return;

    const itemEl = btn.closest(".cart-item");
    if (!itemEl) return;

    const itemId = Number(itemEl.dataset.itemId);
    const res = await removeCartItem(itemId);

    if (!res.ok) {
      alert("삭제에 실패했습니다.");
      return;
    }

    await renderCartFromServer();
  });

  document.getElementById("cartItems")?.addEventListener("change", async (e) => {
    const input = e.target;
    if (!(input instanceof HTMLInputElement)) return;
    if (!input.classList.contains("qty")) return;

    const itemEl = input.closest(".cart-item");
    if (!itemEl) return;

    const itemId = Number(itemEl.dataset.itemId);
    const qty = Math.max(1, Number(input.value) || 1);

    const res = await updateCartItemQuantity(itemId, qty);
    if (!res.ok) {
      alert("수량 변경에 실패했습니다.");
      await renderCartFromServer();
      return;
    }

    await renderCartFromServer();
  });
}

function bindOrderButton() {
    const btn = document.getElementById("orderBtn");
    if (!btn) {
        console.warn("orderBtn not found");
        return;
    }

    btn.addEventListener("click", () => {
        window.location.href = "/orders/confirm";
    })
}