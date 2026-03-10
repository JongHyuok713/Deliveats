document.addEventListener("DOMContentLoaded", () => {
    const container = document.getElementById("roleButtonArea");
    const token = localStorage.getItem("accessToken");

    // 로그인 안 한 경우 - 아무것도 표시되지 않음
    if (!token) {
        return;
    }

    try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        const role = payload.role;
        const username = payload.username;

        let html = "";

        // OWNER - 점주 전용 버튼
        if (role === "OWNER") {
            html += `
                <a href="/stores/my" class="role-btn owner-btn">
                    내 가게 목록
                </a>
                <a href="/stores/register" class="role-btn owner-btn">
                    + 가게 등록하기
                </a>
            `;
        }

        // RIDER - 배달원 전용 버튼
        if (role === "RIDER") {
            html += `
                <a href="/deliveries/requests" class="role-btn rider-btn">
                    배달 요청 보기
                </a>
            `;
        }

        // ADMIN - 관리자 전용 버튼
        if (role === "ADMIN") {
            html += `
                <a href="/admin" class="role-btn admin-btn">
                    관리자 대시보드
                </a>
            `;
        }

        container.innerHTML = html;
    } catch (error) {
        console.error("JWT 파싱 에러:", err);
        localStorage.removeItem("accessToken");
    }
});