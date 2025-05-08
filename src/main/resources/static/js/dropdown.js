document.addEventListener("DOMContentLoaded", function () {
  const dropdownTriggers = document.querySelectorAll(".dropdown-trigger");

  dropdownTriggers.forEach((trigger) => {
    trigger.addEventListener("click", function (event) {
      event.stopPropagation();
      const menu = this.nextElementSibling;

      document.querySelectorAll(".dropdown-menu.show").forEach((openMenu) => {
        if (openMenu !== menu && openMenu.classList.contains("dropdown-menu")) {
          openMenu.classList.remove("show");
        }
      });

      if (menu && menu.classList.contains("dropdown-menu")) {
        menu.classList.toggle("show");
      }
    });
  });

  window.addEventListener("click", function (event) {
    document.querySelectorAll(".dropdown-menu.show").forEach((menu) => {
      if (
        menu &&
        menu.parentElement &&
        !menu.parentElement.contains(event.target) &&
        !menu.contains(event.target)
      ) {
        menu.classList.remove("show");
      }
    });
  });
});
