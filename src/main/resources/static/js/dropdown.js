document.addEventListener('DOMContentLoaded', function() {
  // Existing dropdown logic for header
  const dropdownTriggers = document.querySelectorAll('.dropdown-trigger');

  dropdownTriggers.forEach(trigger => {
    trigger.addEventListener('click', function(event) {
      event.stopPropagation();
      const menu = this.nextElementSibling;

      // Close other header dropdowns
      document.querySelectorAll('.dropdown-menu.show').forEach(openMenu => {
        if (openMenu !== menu && openMenu.classList.contains('dropdown-menu')) { // Ensure it's a header menu
          openMenu.classList.remove('show');
        }
      });

      if (menu && menu.classList.contains('dropdown-menu')) {
        menu.classList.toggle('show');
      }
    });
  });

  // New logic for admin sidebar accordion
  const adminMenuTriggers = document.querySelectorAll('.admin-menu-trigger');

  adminMenuTriggers.forEach(trigger => {
    trigger.addEventListener('click', function() {
      const submenu = this.nextElementSibling; // The UL directly after the H5

      if (submenu && submenu.classList.contains('admin-submenu')) {
        // Optional: Close other open admin submenus for an accordion effect
        // document.querySelectorAll('.admin-submenu.show').forEach(openSubmenu => {
        //   if (openSubmenu !== submenu) {
        //     openSubmenu.classList.remove('show');
        //   }
        // });
        submenu.classList.toggle('show');
      }
    });
  });

  // Close header dropdowns when clicking outside
  window.addEventListener('click', function(event) {
    document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
      if (menu && menu.parentElement && !menu.parentElement.contains(event.target) && !menu.contains(event.target)) {
        menu.classList.remove('show');
      }
    });
    // Note: Admin submenus will not close on outside click with this current setup.
    // If you want that, you'd need to add similar logic for '.admin-submenu'.
  });
});