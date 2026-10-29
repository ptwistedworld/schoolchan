<<<<<<< HEAD
document.addEventListener("click", (event) => {
  const link = event.target.closest(".quote-link");
  if (!link) return;
  event.preventDefault();
  const existing = link.parentElement.querySelector(".preview-pop");
  if (existing) {
    existing.remove();
    return;
  }
  const slug = document.body.dataset.board;
  const number = link.textContent.replace(/\D/g, "");
  if (!slug || !number) {
    window.location.hash = link.getAttribute("href");
    return;
  }
  fetch(`/boards/${slug}/preview/${number}`)
    .then((r) => r.text())
    .then((html) => {
      const pop = document.createElement("div");
      pop.className = "preview-pop";
      pop.innerHTML = html;
      link.parentElement.appendChild(pop);
    });
});

=======
// Theme toggle: light (default) <-> dark. Dark is only ever applied on click.
(function () {
  const root = document.documentElement;
  const ORDER = ["light", "dark"];
  const ICONS = { light: "☾", dark: "☀" };
  const LABEL = { light: "Switch to dark theme", dark: "Switch to light theme" };

  function current() {
    return root.getAttribute("data-theme") === "dark" ? "dark" : "light";
  }
  const saved = localStorage.getItem("sc-theme");
  if (saved === "dark") root.setAttribute("data-theme", "dark");
  else root.setAttribute("data-theme", "light");

  function bind() {
    const btn = document.getElementById("themeToggle");
    if (!btn || btn.dataset.bound) return;
    btn.dataset.bound = "1";
    btn.addEventListener("click", function () {
      const next = current() === "dark" ? "light" : "dark";
      root.setAttribute("data-theme", next);
      localStorage.setItem("sc-theme", next);
      btn.textContent = ICONS[next];
      btn.title = LABEL[next];
      btn.setAttribute("aria-label", LABEL[next]);
    });
    const c = current();
    btn.textContent = ICONS[c];
    btn.title = LABEL[c];
    btn.setAttribute("aria-label", LABEL[c]);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", bind);
  } else {
    bind();
  }
  window.addEventListener("load", bind);
})();

// Hover preview: a quoted post inside a thread, or a thread on a board.
// Both answer with the same fragment, and the fragment includes the post's
// picture when it has one, so a reply's image can be seen without opening it.
(function () {
  let timer = null;
  let current = null;
  let currentLink = null;
  const cache = new Map();
  const CACHE_MS = 30000;

  function load(url) {
    const hit = cache.get(url);
    if (hit && Date.now() - hit.at < CACHE_MS) {
      return Promise.resolve(hit.html);
    }
    return fetch(url)
      .then((r) => (r.ok ? r.text() : ""))
      .then((html) => {
        if (html) {
          cache.set(url, { html: html, at: Date.now() });
        }
        return html;
      })
      .catch(() => "");
  }

  function hide() {
    if (current) {
      const pop = current;
      current = null;
      pop.remove();
    }
    currentLink = null;
  }

  function closeSoon() {
    clearTimeout(timer);
    timer = setTimeout(hide, 350);
  }

  function cancelClose() {
    clearTimeout(timer);
  }

  function urlFor(link) {
    if (link.dataset.preview) {
      return "/threads/" + link.dataset.preview + "/preview";
    }
    const slug = document.body.dataset.board;
    const number = link.textContent.replace(/\D/g, "");
    if (!slug || !number) {
      return null;
    }
    return `/boards/${slug}/preview/${number}`;
  }

  function show(link, html) {
    hide();
    const pop = document.createElement("div");
    pop.className = "preview-pop";
    pop.innerHTML = html;
    pop.addEventListener("mouseenter", cancelClose);
    pop.addEventListener("mouseleave", closeSoon);
    link.parentElement.appendChild(pop);
    current = pop;
    currentLink = link;
  }

  document.addEventListener("mouseover", (event) => {
    const link = event.target.closest("[data-preview], .quote-link");
    if (!link) {
      return;
    }
    cancelClose();
    currentLink = link;
    timer = setTimeout(() => {
      if (currentLink !== link || !link.isConnected) {
        return;
      }
      if (link.parentElement.querySelector(".preview-pop")) {
        return;
      }
      const url = urlFor(link);
      if (!url) {
        return;
      }
      load(url).then((html) => {
        if (!html.trim() || currentLink !== link) {
          return;
        }
        show(link, html);
      });
    }, 280);
  });

  document.addEventListener("mouseout", (event) => {
    const link = event.target.closest("[data-preview], .quote-link");
    if (!link) {
      return;
    }
    if (event.relatedTarget && link.contains(event.relatedTarget)) {
      return;
    }
    closeSoon();
  });

  // Clicking a quote still toggles the preview, and is the only way on a
  // touch screen (where there is no hover).
  document.addEventListener("click", (event) => {
    const link = event.target.closest(".quote-link");
    if (!link) {
      return;
    }
    event.preventDefault();
    const existing = link.parentElement.querySelector(".preview-pop");
    if (existing) {
      existing.remove();
      if (current === existing) {
        current = null;
      }
      return;
    }
    const url = urlFor(link);
    if (!url) {
      window.location.hash = link.getAttribute("href");
      return;
    }
    clearTimeout(timer);
    load(url).then((html) => {
      if (html) {
        show(link, html);
      }
    });
  });
})();

// Inline reply (4chan style): open a small box under a post, prefilled with >>No.
document.addEventListener("click", function (event) {
  const opener = event.target.closest(".inline-reply-btn");
  if (opener) {
    event.preventDefault();
    const form = document.getElementById(opener.dataset.target);
    if (!form) return;
    const wasHidden = form.hidden;
    form.hidden = !wasHidden;
    if (wasHidden) {
      const ta = form.querySelector("textarea");
      if (ta) {
        ta.focus();
        const placeholder = ta.getAttribute("placeholder") || "";
        ta.value = placeholder;
      }
    }
    return;
  }
  if (event.target.closest(".inline-reply-cancel")) {
    event.preventDefault();
    const form = event.target.closest(".inline-reply");
    if (form) form.hidden = true;
  }
});

// Keyboard navigation: j/k move, r replies, t/e jump, b back to board
(function () {
  function posts() {
    return Array.from(document.querySelectorAll(".post"));
  }
  function currentIndex() {
    const list = posts();
    const y = window.scrollY + 120;
    let idx = 0;
    list.forEach(function (p, i) {
      if (p.offsetTop <= y) idx = i;
    });
    return idx;
  }
  function focusPost(i) {
    const list = posts();
    if (!list.length) return;
    const clamped = Math.max(0, Math.min(list.length - 1, i));
    list[clamped].scrollIntoView({ behavior: "smooth", block: "center" });
    list.forEach(function (p) { p.classList.remove("focused-post"); });
    list[clamped].classList.add("focused-post");
  }
  document.addEventListener("keydown", function (event) {
    const tag = (event.target.tagName || "").toLowerCase();
    if (tag === "input" || tag === "textarea" || event.target.isContentEditable) return;
    if (event.ctrlKey || event.metaKey || event.altKey) return;
    const key = event.key.toLowerCase();
    if (key === "j") { event.preventDefault(); focusPost(currentIndex() + 1); }
    else if (key === "k") { event.preventDefault(); focusPost(currentIndex() - 1); }
    else if (key === "r") {
      event.preventDefault();
      const list = posts();
      const idx = currentIndex();
      const post = list[idx];
      if (post) {
        const btn = post.querySelector(".inline-reply-btn");
        if (btn) btn.click();
      }
    } else if (key === "t") { event.preventDefault(); window.scrollTo({ top: 0, behavior: "smooth" }); }
    else if (key === "e") { event.preventDefault(); window.scrollTo({ top: document.body.scrollHeight, behavior: "smooth" }); }
    else if (key === "b") {
      event.preventDefault();
      const back = document.querySelector(".thread-nav a");
      if (back) back.click();
    }
  });
})();

// Floating top / bottom buttons
(function () {
  function mount() {
    if (document.getElementById("scrollNav")) return;
    const nav = document.createElement("div");
    nav.id = "scrollNav";
    nav.className = "scroll-nav";
    nav.innerHTML = '<button type="button" class="linkish" data-scroll="top" title="Top (t)">▲</button>' +
                    '<button type="button" class="linkish" data-scroll="bottom" title="Bottom (e)">▼</button>';
    nav.addEventListener("click", function (e) {
      const btn = e.target.closest("[data-scroll]");
      if (!btn) return;
      if (btn.dataset.scroll === "top") window.scrollTo({ top: 0, behavior: "smooth" });
      else window.scrollTo({ top: document.body.scrollHeight, behavior: "smooth" });
    });
    document.body.appendChild(nav);
  }
  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", mount);
  else mount();
})();

// Quote toolbar: insert >>N or > at the cursor
document.addEventListener("click", function (event) {
  const btn = event.target.closest("[data-insert]");
  if (!btn) return;
  event.preventDefault();
  const ta = btn.closest("form").querySelector("textarea");
  if (!ta) return;
  const template = btn.dataset.insert || "";
  const text = template.replace("{n}", btn.dataset.number || "");
  const start = ta.selectionStart || ta.value.length;
  ta.value = ta.value.slice(0, start) + text + ta.value.slice(ta.selectionEnd || start);
  ta.focus();
  ta.selectionStart = ta.selectionEnd = start + text.length;
});

// Draft autosave for the composers
(function () {
  function keyOf(ta) { return "sc-draft-" + (ta.dataset.draft || "post"); }
  document.addEventListener("input", function (event) {
    const ta = event.target;
    if (!ta.matches || !ta.matches("textarea[data-draft]")) return;
    try { localStorage.setItem(keyOf(ta), ta.value); } catch (e) { /* ignore */ }
  });
  function restore() {
    document.querySelectorAll("textarea[data-draft]").forEach(function (ta) {
      let saved = null;
      try { saved = localStorage.getItem(keyOf(ta)); } catch (e) { /* ignore */ }
      if (saved && !ta.value) {
        ta.value = saved;
        const note = document.createElement("p");
        note.className = "hint";
        note.textContent = "Draft restored from this browser.";
        ta.parentElement.appendChild(note);
      }
    });
  }
  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", restore);
  else restore();
})();

// Read markers: remember the last post you read in each thread
(function () {
  const KEY = "sc-read";
  const threadId = document.body.dataset.threadId;
  if (!threadId) return;
  function read() {
    try { return JSON.parse(localStorage.getItem(KEY) || "{}"); } catch (e) { return {}; }
  }
  function save(map) {
    try { localStorage.setItem(KEY, JSON.stringify(map)); } catch (e) { /* ignore */ }
  }
  const map = read();
  const last = map[threadId] || 0;
  document.querySelectorAll(".post").forEach(function (p) {
    const num = parseInt((p.id || "").replace("p", ""), 10);
    if (!isNaN(num) && num > last) {
      p.classList.add("unread-post");
    }
  });
  function markRead() {
    const map2 = read();
    const posts = Array.from(document.querySelectorAll(".post"));
    const y = window.scrollY + window.innerHeight * 0.75;
    let highest = map2[threadId] || 0;
    posts.forEach(function (p) {
      const num = parseInt((p.id || "").replace("p", ""), 10);
      if (!isNaN(num) && p.offsetTop < y && num > highest) {
        highest = num;
      }
    });
    if (highest > (map2[threadId] || 0)) {
      map2[threadId] = highest;
      save(map2);
    }
  }
  let ticking = false;
  window.addEventListener("scroll", function () {
    if (ticking) return;
    ticking = true;
    setTimeout(function () { markRead(); ticking = false; }, 400);
  });
  markRead();
})();

// Endless scroll (toggleable) for the board thread list
(function () {
  const KEY = "sc-infinite-scroll";
  const list = document.getElementById("threadList");
  const sentinel = document.getElementById("scrollSentinel");
  const toggle = document.getElementById("infiniteToggle");
  const moreBtn = document.getElementById("loadMore");
  const status = document.getElementById("scrollStatus");
  if (!list || !sentinel) return;

  const slug = document.body.dataset.board;
  let page = parseInt(document.body.dataset.page || "0", 10);
  let loading = false;
  let done = false;
  let observer = null;

  function enabled() {
    return localStorage.getItem(KEY) !== "off";
  }

  function setStatus(text) {
    if (status) status.textContent = text || "";
  }

  /** Everything is loaded: hide the button and say so once. */
  function finish(message) {
    done = true;
    if (moreBtn) moreBtn.hidden = true;
    setStatus(message);
  }

  function setDone(message) {
    done = true;
    if (moreBtn) moreBtn.hidden = true;
    setStatus(message);
  }

  function setEnabled(on) {
    localStorage.setItem(KEY, on ? "on" : "off");
    if (toggle) {
      toggle.textContent = "Infinite scroll: " + (on ? "On" : "Off");
      toggle.setAttribute("aria-pressed", on ? "true" : "false");
    }
    // With infinite scroll on there is no page navigation to show.
    const pageNav = document.getElementById("pageNav");
    if (pageNav) pageNav.hidden = on;
    if (moreBtn) moreBtn.hidden = !on;
    if (on) {
      observe();
      loadMore();
    } else if (observer) {
      observer.disconnect();
    }
  }

  function observe() {
    if (observer || !window.IntersectionObserver) return;
    observer = new IntersectionObserver(function (entries) {
      if (entries[0].isIntersecting) loadMore();
    }, { rootMargin: "800px" });
    observer.observe(sentinel);
  }

  function loadMore() {
    if (loading || done) return;
    if (!enabled()) return;
    loading = true;
    if (moreBtn) moreBtn.disabled = true;
    setStatus("Loading more threads…");
    const url = "/boards/" + slug + "/threads-fragment?page=" + (page + 1);
    fetch(url)
      .then(function (r) { return r.text(); })
      .then(function (html) {
        if (!html || !html.trim()) {
          finish("End of board - " + list.querySelectorAll(".thread-card").length + " threads loaded.");
          return;
        }
        const holder = document.createElement("div");
        holder.innerHTML = html;
        const cards = Array.from(holder.querySelectorAll(".thread-card"));
        if (!cards.length) {
          finish("End of board - " + list.querySelectorAll(".thread-card").length + " threads loaded.");
          return;
        }
        cards.forEach(function (c) { list.appendChild(c); });
        page += 1;
        if (cards.length < 20) {
          finish("End of board - " + list.querySelectorAll(".thread-card").length + " threads loaded.");
        } else {
          setStatus("");
        }
      })
      .catch(function () {
        done = true;
        setStatus("Could not load more threads.");
      })
      .finally(function () {
        loading = false;
        if (moreBtn) moreBtn.disabled = false;
      });
  }

  if (toggle) {
    toggle.addEventListener("click", function () { setEnabled(!enabled()); });
  }
  if (moreBtn) {
    moreBtn.addEventListener("click", function () { loadMore(); });
  }
  setEnabled(enabled());
})();

// Spoiler: reveal individually, or reveal all at once
(function () {
  function revealAll() {
    document.querySelectorAll("details.spoiler-box").forEach(function (d) {
      d.open = true;
    });
  }
  const btn = document.getElementById("revealAllSpoilers");
  if (btn) {
    btn.addEventListener("click", function () {
      const allOpen = Array.from(document.querySelectorAll("details.spoiler-box")).every(function (d) { return d.open; });
      document.querySelectorAll("details.spoiler-box").forEach(function (d) { d.open = !allOpen; });
      btn.textContent = allOpen ? "Hide spoilers" : "Reveal all spoilers";
    });
  }
})();

// NSFW / media blur toggle: blur every image until you click it
(function () {
  const KEY = "sc-blur-media";
  function apply(on) {
    document.body.classList.toggle("blur-media", on);
    const btn = document.getElementById("blurToggle");
    if (btn) {
      btn.textContent = on ? "Images: blurred" : "Images: shown";
      btn.setAttribute("aria-pressed", on ? "true" : "false");
    }
  }
  const btn = document.getElementById("blurToggle");
  if (!btn) return;
  let on = localStorage.getItem(KEY) === "on";
  btn.addEventListener("click", function () {
    on = !on;
    localStorage.setItem(KEY, on ? "on" : "off");
    apply(on);
  });
  apply(on);
  document.addEventListener("click", function (e) {
    const img = e.target.closest(".post-image.blurred");
    if (img) img.classList.remove("blurred");
  });
})();

// Quote by selection: select text, press Q, it becomes >>123
(function () {
  document.addEventListener("mouseup", function () {
    const sel = window.getSelection();
    if (!sel || sel.isCollapsed) return;
    const text = sel.toString().trim();
    if (!text || text.length > 300) return;
    const node = sel.anchorNode;
    if (!node) return;
    const post = node.parentElement && node.parentElement.closest(".post");
    if (!post) return;
    const num = (post.id || "").replace("p", "");
    if (!num) return;
    const quote = ">>" + num + " " + text.replace(/\s+/g, " ") + "\n";
    const ta = document.querySelector("#reply-box textarea");
    if (ta) {
      const start = ta.selectionStart || ta.value.length;
      ta.value = ta.value.slice(0, start) + quote + ta.value.slice(ta.selectionEnd || start);
      ta.focus();
    } else {
      navigator.clipboard && navigator.clipboard.writeText(quote);
    }
    sel.removeAllRanges();
  });
})();

// Upload preview before posting
document.addEventListener("change", function (event) {
  const input = event.target;
  if (!input.matches || !input.matches('input[type="file"]')) return;
  const file = input.files && input.files[0];
  if (!file) return;
  const form = input.closest("form");
  if (!form) return;
  let preview = form.querySelector(".upload-preview");
  if (!preview) {
    preview = document.createElement("div");
    preview.className = "upload-preview";
    form.appendChild(preview);
  }
  preview.innerHTML = "";
  const img = document.createElement("img");
  img.alt = "preview";
  img.className = "post-image";
  img.src = URL.createObjectURL(file);
  const label = document.createElement("span");
  label.className = "hint";
  label.textContent = " " + file.name + " (" + Math.round(file.size / 1024) + " KB)";
  preview.appendChild(img);
  preview.appendChild(label);
});

// First-time poster guide: show once per browser
(function () {
  const KEY = "sc-seen-guide";
  if (localStorage.getItem(KEY)) return;
  document.addEventListener("DOMContentLoaded", function () {
    const composer = document.querySelector(".composer");
    if (!composer) return;
    const guide = document.createElement("details");
    guide.className = "post first-time-guide";
    guide.innerHTML = "<summary>First time here? Quick tips</summary>" +
      "<ul>" +
      "<li>Start with <code>&gt;</code> for greentext.</li>" +
      "<li>Quote someone with <code>&gt;&gt;123</code>.</li>" +
      "<li>Tick <strong>Sage</strong> to reply without bumping.</li>" +
      "<li>Tag a <strong>Spoiler</strong> for story reveals.</li>" +
      "<li>Attach an image; it is checked before anyone sees it.</li>" +
      "</ul>";
    composer.parentNode.insertBefore(guide, composer);
    guide.addEventListener("toggle", function () {
      if (guide.open) localStorage.setItem(KEY, "1");
    });
  });
})();

// Offline support (PWA). Only registered on a real site, not on file:// pages.
if ("serviceWorker" in navigator && location.protocol.startsWith("http")) {
  window.addEventListener("load", function () {
    navigator.serviceWorker.register("/js/sw.js").catch(function () { /* offline is optional */ });
  });
}

// Mark composers as submitted so a restored page never shows a reply twice.
//
// The value must NOT be cleared here. The browser builds the request body from
// the form *after* this handler has run, so clearing the textarea in a submit
// listener posts an empty message. The value is cleared later, when the page
// comes back from the browser cache.
document.addEventListener("submit", function (event) {
  const form = event.target;
  if (!form.matches || !form.matches("form")) return;
  form.querySelectorAll("textarea").forEach(function (ta) {
    ta.setAttribute("data-cleared", "1");
    // The saved draft must go too, or the restore would bring it back.
    if (ta.dataset.draft) {
      try { localStorage.removeItem("sc-draft-" + ta.dataset.draft); } catch (e) { /* ignore */ }
    }
  });
});

// Clear the composer when the page comes back from the browser cache, so a
// reply that was already sent is not shown a second time.
window.addEventListener("pageshow", function (event) {
  if (!event.persisted) return;
  document.querySelectorAll("textarea[data-draft]").forEach(function (ta) {
    if (ta.value.trim() === "") return;
    // A draft that was never posted is still worth keeping.
    if (!ta.getAttribute("data-cleared")) return;
    ta.value = "";
  });
});

// Image lightbox: open attachments large, step through them with ← / →
(function () {
  function visibleImages() {
    return Array.from(document.querySelectorAll(".post-image"))
      .filter((img) => img.offsetParent !== null);
  }

  function open(list, index) {
    if (!list.length) return;
    let current = Math.max(0, Math.min(index, list.length - 1));
    const box = document.createElement("div");
    box.className = "lightbox";
    box.tabIndex = -1;
    box.setAttribute("role", "dialog");
    box.setAttribute("aria-modal", "true");
    box.setAttribute("aria-label", "Image viewer");
    const big = document.createElement("img");
    big.alt = "attachment";
    const prev = document.createElement("button");
    prev.type = "button";
    prev.className = "lightbox-nav prev";
    prev.textContent = "‹";
    prev.setAttribute("aria-label", "Previous image");
    const next = document.createElement("button");
    next.type = "button";
    next.className = "lightbox-nav next";
    next.textContent = "›";
    next.setAttribute("aria-label", "Next image");
    const count = document.createElement("span");
    count.className = "lightbox-count";
    box.append(prev, big, next, count);

    function show(i) {
      current = (i + list.length) % list.length;
      big.src = list[current].src;
      big.alt = list[current].alt || "attachment";
      count.textContent = current + 1 + " / " + list.length;
      const many = list.length > 1;
      prev.hidden = !many;
      next.hidden = !many;
      count.hidden = !many;
    }

    function close() {
      box.remove();
      document.removeEventListener("keydown", onKey);
    }

    function onKey(e) {
      if (e.key === "Escape") {
        close();
      } else if (e.key === "ArrowLeft") {
        show(current - 1);
      } else if (e.key === "ArrowRight") {
        show(current + 1);
      }
    }

    prev.addEventListener("click", (e) => { e.stopPropagation(); show(current - 1); });
    next.addEventListener("click", (e) => { e.stopPropagation(); show(current + 1); });
    // The backdrop closes; the picture itself does not.
    box.addEventListener("click", (e) => { if (e.target === box) close(); });
    document.addEventListener("keydown", onKey);
    show(index);
    document.body.appendChild(box);
    (list.length > 1 ? next : box).focus();
  }

  document.addEventListener("click", (event) => {
    const img = event.target.closest(".post-image");
    if (!img) return;
    const list = visibleImages();
    open(list, list.indexOf(img));
  });
})();

// Relative time: "12 min ago", with the exact time kept in the tooltip.
(function () {
  const nodes = Array.from(document.querySelectorAll(".time[data-time]"));
  if (!nodes.length) return;

  function label(ms) {
    const minutes = Math.floor((Date.now() - ms) / 60000);
    if (minutes < 1) return "just now";
    if (minutes < 60) return minutes + " min ago";
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return hours + " h ago";
    const days = Math.floor(hours / 24);
    if (days < 7) return days + " d ago";
    return new Date(ms).toLocaleDateString(undefined, { month: "short", day: "numeric" });
  }

  function refresh() {
    nodes.forEach((el) => {
      const ms = Number(el.dataset.time);
      if (Number.isFinite(ms)) el.textContent = label(ms);
    });
  }

  refresh();
  setInterval(refresh, 60000);
})();

// Expand / shrink every picture in the thread, remembered per browser.
(function () {
  const KEY = "sc-image-size";
  const btn = document.getElementById("sizeToggle");
  if (!btn) return;
  const root = document.documentElement;

  function apply(expanded) {
    root.classList.toggle("expand-images", expanded);
    btn.textContent = expanded ? "Shrink images" : "Expand images";
    btn.setAttribute("aria-pressed", expanded ? "true" : "false");
  }

  let expanded = false;
  try { expanded = localStorage.getItem(KEY) === "expanded"; } catch (e) { /* ignore */ }
  apply(expanded);
  btn.addEventListener("click", function () {
    expanded = !expanded;
    try { localStorage.setItem(KEY, expanded ? "expanded" : "normal"); } catch (e) { /* ignore */ }
    apply(expanded);
  });
})();

// Landing on a post: flash it. If the post is in the hidden part of a long
// thread, reopen the thread with every reply instead of doing nothing.
(function () {
  const HASH = /^#p\d+$/;
  let timer = null;

  function flash() {
    const hash = window.location.hash;
    if (!HASH.test(hash)) return;
    const post = document.querySelector(hash);
    if (!post) {
      const params = new URLSearchParams(window.location.search);
      if (!params.has("all")) {
        params.set("all", "true");
        window.location.replace(window.location.pathname + "?" + params.toString() + hash);
      }
      return;
    }
    post.classList.remove("flash");
    void post.offsetWidth;
    post.classList.add("flash");
    clearTimeout(timer);
    timer = setTimeout(() => post.classList.remove("flash"), 1800);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", flash);
  } else {
    flash();
  }
  window.addEventListener("hashchange", flash);
})();

// Bookmarks (server-side, tied to the anonymous session)
(function () {
  function setState(btn, active) {
    btn.classList.toggle("active", active);
    btn.textContent = active ? "★ Bookmarked" : "☆ Bookmark";
    btn.setAttribute("aria-pressed", active ? "true" : "false");
  }

  document.addEventListener("click", function (event) {
    const btn = event.target.closest(".bookmark-btn");
    if (!btn) return;
    event.preventDefault();
    const id = btn.dataset.thread;
    if (!id) return;
    btn.disabled = true;
    const tokenEl = document.getElementById("csrfToken");
    const token = tokenEl ? tokenEl.dataset.token : "";
    fetch("/bookmarks/" + id + "/toggle", {
      method: "POST",
      headers: { "X-Requested-With": "XMLHttpRequest", "X-CSRF-TOKEN": token },
    })
      .then((r) => r.json())
      .then((data) => {
        setState(btn, !!data.bookmarked);
        const link = document.querySelector("[data-bookmark-count]");
        if (link) {
          const n = parseInt(link.dataset.count || "0", 10) + (data.bookmarked ? 1 : -1);
          link.dataset.count = String(Math.max(0, n));
          link.textContent = n > 0 ? " (" + n + ")" : "";
        }
      })
      .catch(() => {})
      .finally(() => { btn.disabled = false; });
  });
})();

>>>>>>> d5851d3 (Initial commit)
document.addEventListener("click", (event) => {
  const button = event.target.closest("[data-translate]");
  if (!button) return;
  const id = button.dataset.translate;
  const slot = document.getElementById("tr-" + id);
  if (!slot) return;
  if (slot.hidden) {
    if (!slot.dataset.loaded) {
      fetch("/posts/" + id + "/translation")
        .then((r) => r.text())
        .then((html) => {
          slot.innerHTML = html;
          slot.dataset.loaded = "1";
          slot.hidden = false;
        });
    } else {
      slot.hidden = false;
    }
    button.textContent = "Hide English";
  } else {
    slot.hidden = true;
    button.textContent = "Show English";
  }
});
<<<<<<< HEAD
=======

// Read-side filters (G4): per-browser keyword rules and "hide this thread".
// This is deliberately client-side and invisible to the server: nothing a
// reader hides here changes the board for anyone else, and a browser without
// JavaScript simply never sees the controls.
(function () {
  const RULES_KEY = "sc-reader-filters";
  const HIDDEN_KEY = "sc-hidden-threads";
  const MAX_RULES = 20;
  const MAX_HIDDEN = 200;
  const ACTIONS = ["hide", "collapse", "highlight"];

  // Reader tools are a signed-in feature: an anonymous visitor never sees them,
  // and any rules left in storage from an earlier session stay inert.
  if (document.body && document.body.dataset.signedIn !== "true") return;

  function readArray(key) {
    try {
      const parsed = JSON.parse(localStorage.getItem(key) || "[]");
      return Array.isArray(parsed) ? parsed : [];
    } catch (e) {
      return [];
    }
  }

  function save(key, value) {
    try { localStorage.setItem(key, JSON.stringify(value)); } catch (e) { /* private mode */ }
  }

  let rules = readArray(RULES_KEY)
    .filter((rule) => rule && typeof rule.text === "string" && ACTIONS.includes(rule.action))
    .slice(0, MAX_RULES);
  let hiddenThreads = readArray(HIDDEN_KEY)
    .filter((id) => typeof id === "string")
    .slice(-MAX_HIDDEN);
  let revealed = false;

  const list = document.getElementById("threadList") || document.querySelector(".catalog");
  const panel = document.getElementById("readerTools");
  const form = document.getElementById("filterForm");
  const ruleList = document.getElementById("filterList");
  const emptyHint = document.getElementById("filterEmpty");
  const clearBtn = document.getElementById("clearFilters");
  const countBadge = document.getElementById("filterCount");
  const hiddenBar = document.getElementById("hiddenBar");
  const hiddenCount = document.getElementById("hiddenCount");
  const hiddenToggle = document.getElementById("hiddenToggle");
  const hiddenClear = document.getElementById("hiddenClear");

  function cards() {
    if (!list) return [];
    return Array.from(list.querySelectorAll(".thread-card, .catalog-card"));
  }

  function matchingRule(card) {
    const text = (card.getAttribute("data-filter-text") || "").toLowerCase();
    if (!text) return null;
    const matched = rules.filter((rule) => {
      const needle = rule.text.trim().toLowerCase();
      return needle && text.includes(needle);
    });
    // A thread that matches several rules gets the strongest one, so a broad
    // "highlight" can never accidentally undo a narrower "hide".
    return matched.find((rule) => rule.action === "hide") ||
      matched.find((rule) => rule.action === "collapse") ||
      matched[0] || null;
  }

  function updateRules() {
    if (panel) panel.hidden = false;
    if (countBadge) {
      countBadge.textContent = String(rules.length);
      countBadge.hidden = rules.length === 0;
    }
    if (ruleList) {
      ruleList.textContent = "";
      rules.forEach((rule, index) => {
        const item = document.createElement("li");
        const label = document.createElement("span");
        label.textContent = rule.text + " — " + rule.action;
        const remove = document.createElement("button");
        remove.type = "button";
        remove.className = "linkish";
        remove.textContent = "remove";
        remove.setAttribute("aria-label", "Remove filter " + rule.text);
        remove.addEventListener("click", function () {
          rules.splice(index, 1);
          save(RULES_KEY, rules);
          apply();
        });
        item.append(label, document.createTextNode(" "), remove);
        ruleList.appendChild(item);
      });
    }
    if (emptyHint) emptyHint.hidden = rules.length > 0;
    if (clearBtn) clearBtn.hidden = rules.length === 0;
  }

  function updateHiddenBar() {
    if (!hiddenBar) return;
    const onPage = cards().filter((card) => hiddenThreads.includes(card.getAttribute("data-thread") || ""));
    if (onPage.length === 0) {
      hiddenBar.hidden = true;
      return;
    }
    hiddenBar.hidden = false;
    hiddenCount.textContent = onPage.length +
      (onPage.length === 1 ? " thread hidden" : " threads hidden");
    hiddenToggle.textContent = revealed ? "Hide again" : "Show";
    hiddenToggle.setAttribute("aria-pressed", revealed ? "true" : "false");
    if (hiddenClear) hiddenClear.hidden = !revealed;
  }

  function apply() {
    if (!list) return;
    list.querySelectorAll(".filter-note").forEach((note) => note.remove());
    cards().forEach((card) => {
      card.hidden = false;
      card.classList.remove("filter-collapsed", "filter-highlight", "filter-revealed");

      const rule = matchingRule(card);
      if (rule && rule.action === "hide") {
        card.hidden = true;
      } else if (rule && rule.action === "collapse") {
        card.hidden = true;
        const note = document.createElement("p");
        note.className = "filter-note";
        note.textContent = "Hidden by your filter “" + rule.text + "”. ";
        const show = document.createElement("button");
        show.type = "button";
        show.className = "linkish";
        show.textContent = "Show";
        show.addEventListener("click", function () {
          card.hidden = false;
          note.remove();
        });
        note.appendChild(show);
        card.insertAdjacentElement("afterend", note);
      } else if (rule && rule.action === "highlight") {
        card.classList.add("filter-highlight");
      }

      const id = card.getAttribute("data-thread");
      if (id && hiddenThreads.includes(id)) {
        if (revealed) card.classList.add("filter-revealed");
        else card.hidden = true;
      }
    });
    list.querySelectorAll("[data-hide-thread][hidden]").forEach((button) => {
      button.hidden = false;
    });
    updateHiddenBar();
    updateRules();
  }

  if (form) {
    form.addEventListener("submit", function (event) {
      event.preventDefault();
      const input = form.querySelector("input[name=text]");
      const select = form.querySelector("select[name=action]");
      if (!input || !select) return;
      const text = (input.value || "").trim().slice(0, 60);
      const action = ACTIONS.includes(select.value) ? select.value : "hide";
      if (!text || rules.length >= MAX_RULES) return;
      if (!rules.some((rule) => rule.text.toLowerCase() === text.toLowerCase() && rule.action === action)) {
        rules.push({ text: text, action: action });
        save(RULES_KEY, rules);
      }
      input.value = "";
      apply();
    });
  }

  if (clearBtn) {
    clearBtn.addEventListener("click", function () {
      rules = [];
      save(RULES_KEY, rules);
      apply();
    });
  }
  if (hiddenToggle) {
    hiddenToggle.addEventListener("click", function () {
      revealed = !revealed;
      apply();
    });
  }
  if (hiddenClear) {
    hiddenClear.addEventListener("click", function () {
      hiddenThreads = [];
      save(HIDDEN_KEY, hiddenThreads);
      revealed = false;
      apply();
    });
  }

  document.addEventListener("click", function (event) {
    const button = event.target.closest("[data-hide-thread]");
    if (!button) return;
    event.preventDefault();
    const id = button.getAttribute("data-hide-thread");
    if (!id) return;
    if (!hiddenThreads.includes(id)) {
      hiddenThreads.push(id);
      hiddenThreads = hiddenThreads.slice(-MAX_HIDDEN);
      save(HIDDEN_KEY, hiddenThreads);
    }
    revealed = false;
    apply();
  });

  // Endless scroll appends cards after this script has run. Watch for added
  // thread cards (not for our own filter notes) and re-apply the rules.
  if (list && window.MutationObserver) {
    new MutationObserver(function (mutations) {
      const added = mutations.some(function (mutation) {
        return Array.from(mutation.addedNodes).some(function (node) {
          if (node.nodeType !== 1) return false;
          return node.matches(".thread-card, .catalog-card") ||
            !!node.querySelector(".thread-card, .catalog-card");
        });
      });
      if (added) apply();
    }).observe(list, { childList: true });
  }

  apply();
})();

// Live thread (G3): poll for new replies and let the reader choose when to show
// them. Nothing moves under a half-written reply, and a hidden tab stops
// asking the server altogether.
(function () {
  const body = document.body;
  if (!body) return;
  const threadId = body.dataset.threadId;
  const board = body.dataset.board;
  if (!threadId || !board) return;
  const enabled = body.dataset.livePollEnabled !== "false";
  const seconds = parseInt(body.dataset.livePollSeconds || "0", 10);
  if (!enabled || !seconds || seconds < 5) return;

  const postList = document.getElementById("postList");
  const bar = document.getElementById("newPostsBar");
  const showBtn = document.getElementById("showNewPosts");
  if (!postList || !bar || !showBtn) return;

  let lastShown = parseInt(body.dataset.lastPostNumber || "0", 10) || 0;
  let pendingHtml = "";
  let pendingCount = 0;
  let timer = null;

  function hasComposerText() {
    return Array.from(document.querySelectorAll("textarea")).some(function (ta) {
      return ta.value.trim() !== "";
    });
  }

  function knownNumbers() {
    const out = new Set();
    postList.querySelectorAll(".post").forEach(function (post) {
      const match = (post.id || "").match(/^p(\d+)$/);
      if (match) out.add(match[1]);
    });
    return out;
  }

  async function poll() {
    if (document.hidden || hasComposerText()) return;
    try {
      const response = await fetch("/boards/" + board + "/threads/" + threadId +
        "/posts-after/" + lastShown, { credentials: "same-origin" });
      if (!response.ok) return;
      const html = await response.text();
      const holder = document.createElement("div");
      holder.innerHTML = html;
      const posts = Array.from(holder.querySelectorAll(".post"));
      const existing = knownNumbers();
      const fresh = posts.filter(function (post) {
        return !existing.has((post.id || "").replace("p", ""));
      });
      if (fresh.length === 0) return;

      pendingHtml += fresh.map(function (post) { return post.outerHTML; }).join("");
      pendingCount += fresh.length;
      fresh.forEach(function (post) {
        const match = (post.id || "").match(/^p(\d+)$/);
        if (match) lastShown = Math.max(lastShown, parseInt(match[1], 10));
      });
      showBtn.textContent = pendingCount + (pendingCount === 1
        ? " new post \u2014 show" : " new posts \u2014 show");
      bar.hidden = false;
    } catch (e) {
      /* Polling is best-effort; a failed request is retried next time. */
    }
  }

  function showPending() {
    if (!pendingHtml) {
      bar.hidden = true;
      return;
    }
    const holder = document.createElement("div");
    holder.innerHTML = pendingHtml;
    const existing = knownNumbers();
    Array.from(holder.querySelectorAll(".post")).forEach(function (post) {
      const number = (post.id || "").replace("p", "");
      if (number && existing.has(number)) return;
      postList.appendChild(post);
      if (number) existing.add(number);
    });
    pendingHtml = "";
    pendingCount = 0;
    bar.hidden = true;
  }

  showBtn.addEventListener("click", showPending);

  function start() {
    if (timer || hasComposerText()) return;
    timer = setInterval(poll, seconds * 1000);
  }
  function stop() {
    if (timer) {
      clearInterval(timer);
      timer = null;
    }
  }

  document.addEventListener("visibilitychange", function () {
    if (document.hidden) stop();
    else start();
  });
  document.addEventListener("input", function (event) {
    if (!event.target || !event.target.matches || !event.target.matches("textarea")) return;
    if (hasComposerText()) stop();
    else start();
  });

  start();
})();

// After posting, the redirect lands on #pN. Move focus there so keyboard and
// screen-reader users are not left at the top of the page.
(function () {
  function focusTarget() {
    const hash = window.location.hash || "";
    if (!hash.startsWith("#p")) return;
    const target = document.querySelector(hash);
    if (!target) return;
    if (!target.hasAttribute("tabindex")) target.setAttribute("tabindex", "-1");
    try { target.focus({ preventScroll: true }); } catch (e) { target.focus(); }
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", focusTarget);
  } else {
    focusTarget();
  }
})();
>>>>>>> d5851d3 (Initial commit)
