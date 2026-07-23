document.addEventListener("DOMContentLoaded", () => {
    const profileTrigger = document.querySelector("[data-profile-trigger]");
    const profileMenu = document.querySelector("[data-profile-menu]");

    const closeProfileMenu = () => {
        if (!profileTrigger || !profileMenu) return;
        profileTrigger.setAttribute("aria-expanded", "false");
        profileMenu.hidden = true;
    };

    if (profileTrigger && profileMenu) {
        profileTrigger.addEventListener("click", (event) => {
            event.stopPropagation();
            const willOpen = profileMenu.hidden;
            profileMenu.hidden = !willOpen;
            profileTrigger.setAttribute("aria-expanded", String(willOpen));
        });

        document.addEventListener("click", (event) => {
            if (!profileMenu.contains(event.target) && event.target !== profileTrigger) {
                closeProfileMenu();
            }
        });
    }

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            closeProfileMenu();
        }
    });

    document.querySelectorAll("[data-password-toggle]").forEach((button) => {
        const inputId = button.getAttribute("aria-controls");
        const input = document.getElementById(inputId);
        if (!input) return;

        button.addEventListener("click", () => {
            const willShow = input.type === "password";
            input.type = willShow ? "text" : "password";
            button.setAttribute("aria-pressed", String(willShow));
            button.setAttribute("aria-label", willShow ? "Ẩn mật khẩu" : "Hiện mật khẩu");
            button.textContent = willShow ? "Ẩn" : "Hiện";
        });
    });

    document.querySelectorAll("[data-confirm]").forEach((element) => {
        element.addEventListener("click", (event) => {
            const message = element.getAttribute("data-confirm");
            if (message && !window.confirm(message)) {
                event.preventDefault();
            }
        });
    });

    document.querySelectorAll("[data-current-year]").forEach((element) => {
        element.textContent = String(new Date().getFullYear());
    });
});
