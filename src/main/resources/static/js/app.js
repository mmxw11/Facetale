window.addEventListener("click", function (e) {
    if (!Element.prototype.matches) {
        // IE fix.
        Element.prototype.matches = Element.prototype.msMatchesSelector || Element.prototype.webkitMatchesSelector;
    }
    if (e.target.matches(".nia-dropdown-toggle")) {
        return;
    }
    var dmenu = document.getElementById("ni-account-dropdown-menu");
    if (dmenu.classList.contains("show-element")) {
        dmenu.classList.remove("show-element");
    }
});

function toggleAccountDropdownMenu() {
    var element = document.getElementById("ni-account-dropdown-menu");
    element.classList.toggle("show-element");
}

function toggleMobileSearchBar() {
    var mnavSearchGroup = document.getElementById("mobile-nav-search-group");
    mnavSearchGroup.classList.toggle("mobile-nav-search-group-visible");
    var navGroups = document.getElementsByClassName("nav-action-group");
    for (let i = 0; i < navGroups.length; i++) {
        const navGroup = navGroups[i];
        if (navGroup.id === "mobile-nav-search-group") {
            continue;
        }
        navGroup.classList.toggle("mobile-nav-hidden-element");
    }
}