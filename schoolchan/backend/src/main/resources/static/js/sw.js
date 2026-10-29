// SchoolChan offline cache.
// Keeps the boards you have visited available on a weak connection. Nothing
// private is cached: only public pages, and only for a short time.
const CACHE = "schoolchan-v1";
const OFFLINE_URLS = ["/", "/about", "/rules", "/offline"];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE).then((cache) => cache.addAll(OFFLINE_URLS)).then(() => self.skipWaiting())
  );
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.filter((k) => k !== CACHE).map((k) => caches.delete(k)))
    ).then(() => self.clients.claim())
  );
});

self.addEventListener("fetch", (event) => {
  const request = event.request;
  if (request.method !== "GET") return;
  const url = new URL(request.url);
  if (url.origin !== self.location.origin) return;
  // Never cache anything that needs a session or staff rights.
  if (url.pathname.startsWith("/staff") || url.pathname.startsWith("/my-posts")
      || url.pathname.startsWith("/bookmarks") || url.pathname.startsWith("/student")) return;

  event.respondWith(
    fetch(request)
      .then((response) => {
        if (response && response.ok && response.type === "basic") {
          const copy = response.clone();
          caches.open(CACHE).then((cache) => cache.put(request, copy));
        }
        return response;
      })
      .catch(() => caches.match(request).then((cached) => cached || caches.match("/offline")))
  );
});
