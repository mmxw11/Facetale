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

window.addEventListener("load", function (e) {
    var pathname = window.location.pathname.replace(/\/$/, "");
    var locationInputs = document.getElementsByClassName("page-location-input");
    for (let i = 0; i < locationInputs.length; i++) {
        locationInputs[i].value = pathname;
    }
    updateStaticProfileViewDisplayElements(false);
});

function changeProfileViewDisplayType() {
    if (profileViewDisplayType === "POSTS") {
        profileViewDisplayType = "ALBUM";
    } else if (profileViewDisplayType === "ALBUM") {
        profileViewDisplayType = "POSTS";
    } else {
        return;
    }
    updateStaticProfileViewDisplayElements(true);
    /* alert("CHANGE VIEW! username: " + username);
       alert("name: " + name);
       alert("profileTag: " + profileTag);
       alert("profileViewDisplayType: " + profileViewDisplayType);*/
    /* alert("contextRoot: " + contextRoot);*/
}


function updateStaticProfileViewDisplayElements(updateUrl) {
    var cpvdisplayTypeButton = document.getElementById("change-profileview-displaytype");
    var pvdisplaytypeTitle = document.getElementById("profileview-displaytype-title");
    var profileContextUrl;
    if (updateUrl) {
        var path = window.location.pathname.replace(/\/$/, "").split("/");
        if (path[path.length - 1] === "me" || path[path.length - 2] === "me") {
            profileContextUrl = contextRoot + "me";
        } else {
            profileContextUrl = contextRoot + "users/" + profileTag;
        }
    }
    if (profileViewDisplayType === "POSTS") {
        document.title = profileName + " (@" + profileTag + ") -  Julkaisut";
        if (updateUrl) {
            history.pushState({}, null, profileContextUrl + "/posts");
        }
        cpvdisplayTypeButton.innerHTML = "<i class='fas fa-images'></i>Albumi";
        pvdisplaytypeTitle.innerHTML = "<i class='fas fa-th-large fa-sm'></i> Julkaisut";
    } else if (profileViewDisplayType === "ALBUM") {
        document.title = profileName + " (@" + profileTag + ") -  Albumi";
        if (updateUrl) {
            history.pushState({}, null, profileContextUrl + "/album");
        }
        cpvdisplayTypeButton.innerHTML = "<i class='fas fa-th-large'></i>Julkaisut";
        pvdisplaytypeTitle.innerHTML = "<i class='fas fa-images fa-sm'></i> Albumi";
    }
}


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