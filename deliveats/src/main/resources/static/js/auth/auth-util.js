export async function getCurrentUser() {
    const token = localStorage.getItem("accessToken");
    if (!token) return null;

    const res = await fetch("/api/users/me", {
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (!res.ok) return null;
    return await res.json();
}