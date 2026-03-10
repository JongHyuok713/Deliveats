document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("accessToken");
    if (!token) return;

    document.querySelectorAll("a").forEach(a => {
        const url = a.getAttribute("href");
        if (!url || url.startsWith("http") || url.startsWith("#")) return;

        a.addEventListener("click", (e) => {
            e.preventDefault();

            fetch(url, {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }).then(res => {
                if (res.status === 403) {
                    alert("권한이 없습니다.");
                } else {
                    window.location.href = url;
                }
            });
        });
    });
});