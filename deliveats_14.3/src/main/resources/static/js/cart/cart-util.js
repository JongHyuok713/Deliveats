export function getToken() {
    return localStorage.getItem("accessToken");
}

export function escapeHtml(str) {
    return String(str ?? "").replace(/[&<>"']/g, m => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    }[m]));
}

export function formatWon(price) {
    return (Number(price) || 0).toLocaleString("ko-KR") + "원";
}

export async function apiFetch(url, options = {}) {
    const token = getToken();

    const headers = new Headers(options.headers || {});
    if (!headers.has("Content-Type") && options.body) {
     headers.set("Content-Type", "application/json");
    }
    if (token && !headers.has("Authorization")) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const res = await fetch(url, {
    ...options,
    headers,
  });

  return res;
}

export async function fetchCart() {
  const res = await apiFetch("/api/cart/items", { method: "GET" });
  if (res.status === 401 || res.status === 403) return null;
  
  const text = await res.text();
  if (!res.ok) {
    if (text) console.warn("fetchCart failed:", text);
    return null;
  }
  if (!text) return null;

  try {
    return JSON.parse(text);
  } catch (e) {
    console.warn("fetchCart json parse failed:", e);
    return null;
  }
}

export async function addCartItem(menuId, quantity = 1) {
  const res = await apiFetch("/api/cart/items", {
    method: "POST",
    body: JSON.stringify({ menuId: Number(menuId), quantity: Number(quantity) }),
  });
  return res;
}

export async function updateCartItemQuantity(itemId, quantity) {
  const res = await apiFetch(`/api/cart/items/${Number(itemId)}`, {
    method: "POST",
    body: JSON.stringify({ quantity: Number(quantity) }),
  });
  return res;
}

export async function removeCartItem(itemId) {
  const res = await apiFetch(`/api/cart/items/${Number(itemId)}`, {
    method: "DELETE",
  });
  return res;
}

export async function clearServerCart() {
  const res = await apiFetch("/api/cart", { method: "DELETE" });
  return res;
}

export async function updateCartIconUI() {
    const badge = document.getElementById("cartCount");
    if (!badge) return;

    const token = getToken();
    if (!token) {
        badge.classList.add("hidden");
        return;
    }

    const cart = await fetchCart();
  const totalQty = cart?.items?.reduce((sum, i) => sum + (Number(i.quantity) || 0), 0) || 0;

  if (totalQty > 0) {
    badge.textContent = totalQty;
    badge.classList.remove("hidden");
  } else {
    badge.classList.add("hidden");
  }
}