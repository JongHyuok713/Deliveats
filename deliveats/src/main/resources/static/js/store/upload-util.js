export async function uploadImage(file, type, targetId, token) {
    const fd = new FormData();
    fd.append("file", file);

    const res = await fetch(`/api/uploads/${type}/${targetId}`, {
        method: "POST",
        credentials: "same-origin",
        headers: { "Authorization": `Bearer ${token}` },
        body: fd
    });

    if (!res.ok) {
        throw new Error("업로드 실패");
    }

    const contentType = res.headers.get("content-type");

    if (contentType && contentType.includes("application/json")) {
        const data = await res.json();
        return data.url;
    } else {
        return await res.text();
    }
}