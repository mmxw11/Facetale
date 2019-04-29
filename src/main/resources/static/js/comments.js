function resizeCommentBoxTextarea(element) {
    element.style.height = "auto";
    element.style.height = element.scrollHeight + 3 + "px";
}
window.addEventListener("resize", function (e) {
    var commentboxes = document.getElementsByClassName("commentbox-textarea");
    for (let i = 0; i < commentboxes.length; i++) {
        const commentbox = commentboxes[i];
        resizeCommentBoxTextarea(commentbox);
    }
});