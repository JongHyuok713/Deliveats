document.addEventListener("DOMContentLoaded", () => {
    const cart = JSON.parse(localStorage.getItem("cart") || "[]");
    let total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

    document.getElementById("orderPaySummary").innerHTML = `
        <p>총 주문 금액: <strong>${total}원</strong></p>
    `;

    document.getElementById("finishPayBtn").addEventListener("click", () => {
        alert("결제가 완료되었습니다!");
        window.location.href = "/order/create";
    });
});