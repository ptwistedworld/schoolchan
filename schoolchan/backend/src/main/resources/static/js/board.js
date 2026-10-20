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
