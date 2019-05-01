window.addEventListener("resize", function (e) {
    var commentboxes = document.getElementsByClassName("comment-textarea");
    for (let i = 0; i < commentboxes.length; i++) {
        const commentbox = commentboxes[i];
        resizeCommentBoxTextarea(commentbox);
    }
});

window.addEventListener("click", function (e) {
    if (!Element.prototype.matches) {
        // IE fix.
        Element.prototype.matches = Element.prototype.msMatchesSelector || Element.prototype.webkitMatchesSelector;
    }
    var element = e.target;
    if (!element.matches(".comment-textarea")) {
        return;
    }
    if (element.classList.contains("fe-invalid-value")) {
        element.classList.remove("fe-invalid-value");
    }
});

function resizeCommentBoxTextarea(e) {
    e.style.height = "auto";

    var cstyle = getComputedStyle(e);
    var crect = e.getBoundingClientRect();
    var paddingY = parseFloat(cstyle.paddingTop) + parseFloat(cstyle.paddingBottom);
    var newHeight = e.scrollHeight - paddingY;
    // crect.height is used rather than clientHeight to ensure the height is always more on the first line.
    // This is because scrollHeight is an integer and some browsers round it up and others down.
    if (e.scrollHeight > crect.height) {
        e.style.height = newHeight + "px";
    }
}