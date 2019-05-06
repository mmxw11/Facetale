// @author Matias

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
    // TODO: CHANGE CONTENT
    // TODO ADD AND REMOVE TO MAIN POST LIST
    // LOAD MORE MAIN POSTLIST
    // ADD COMMENT
    // LOAD REPLIES
}

function togglePostComments(button) {
    var postId = button.getAttribute("data-postid");
    var postRepliesElement = document.getElementById("post-replies-" + postId);
    var postTargetArray = postRepliesElement.querySelector(".posts-list ul");
    var nextPage = parseInt(postTargetArray.getAttribute("data-page"));
    if (nextPage == -1 || postTargetArray.style.display === "none") {
        showPostComments(button, postRepliesElement, postTargetArray, nextPage, 10);
        alert("Show posts!");
    } else {
        alert("Hide posts!");
        hidePostComments(postRepliesElement, postTargetArray);
    }
    alert(" nextPage: " + nextPage + " | toggle comments!");
}

function showPostComments(button, postRepliesElement, postTargetArray, nextPage, commentsPerQuery) {
    var commentCount = parseInt(button.getAttribute("data-totalcount"));
    if (commentCount == 0) {
        var postCountNotificationElement = postRepliesElement.querySelector(".post-count-notification");
        postCountNotificationElement.style.display = "block";
    }
    postTargetArray.style.display = "block";
    if (commentCount > 0) {
        var loadItemsContainer = postRepliesElement.querySelector(".load-items-container");
        if (nextPage == -1 || commentsPerQuery * (nextPage + 1) < commentCount) {
            loadItemsContainer.style.display = "flex";
        }
        if (nextPage == -1) {
            alert("TRIGGER LOAD!");
            loadItemsContainer.querySelector("button").click();
        }
    }
}

function hidePostComments(postRepliesElement, postTargetArray) {
    hideNoCommentsNotification(postRepliesElement);
    postTargetArray.style.display = "none";
    var loadItemsContainer = postRepliesElement.querySelector(".load-items-container");
    if (loadItemsContainer) {
        loadItemsContainer.style.display = "none";
    }
}

function hideNoCommentsNotification(postRepliesElement) {
    var postCountNotificationElement = postRepliesElement.querySelector(".post-count-notification");
    if (postCountNotificationElement) {
        postCountNotificationElement.style.display = "none";
    }
}

function loadComments(postListLoader) {
    var loaderElement = document.getElementById(postListLoader);
    var isPost = postListLoader === "main-post-list-loader";
    var target = loaderElement.getAttribute("data-postlisttarget");

    var postTargetArray = document.getElementById(isPost ? "main-post-list" : "post-replies-" + target);
    if (!isPost) {
        postTargetArray = postTargetArray.querySelector(".posts-list ul");
    }
    var postsPerQuery = 10;
    var nextPage = parseInt(postTargetArray.getAttribute("data-page")) + 1;
    var totalPostCountElement;
    if (isPost) {
        totalPostCountElement = document.getElementById("total-post-count");
    } else {
        totalPostCountElement = document.getElementById("post-" + target + "-total-comment-count");
    }
    var postCount = parseInt(totalPostCountElement.getAttribute("data-totalcount"));

    var loaderButton = loaderElement.querySelector("button");
    var formLoader = loaderElement.querySelector(".lds-ring");
    loaderButton.style.display = "none";
    formLoader.style.display = "block";

    var httpReq = new XMLHttpRequest();
    httpReq.onreadystatechange = function () {
        if (httpReq.readyState != 4) {
            return;
        }
        removeOldErrorMessages(loaderElement);
        // Hide loader.
        formLoader.style.display = "none";
        loaderButton.style.display = "inline";

        // Check results.
        if (httpReq.status != 200) {
            var errorElement = createErrorElement("Nyt kävi niin, että jotain hajosi :/");
            errorElement.style.display = "block";
            loaderElement.appendChild(errorElement);
            // Patenttiratkaisu.
            setTimeout(function () { alert("Jotain hajosi :( Virhekoodi: " + httpReq.status + ": " + httpReq.responseText); }, 200);
        } else {
            // Add to comment/post list.
            if (httpReq.responseText.length > 0) {
                postTargetArray.insertAdjacentHTML("beforeend", httpReq.responseText);
                postTargetArray.setAttribute("data-page", nextPage);
            }
            if (postsPerQuery * (nextPage + 1) >= postCount) {
                // No more posts.
                loaderElement.style.display = "none";
            }
        }
    }
    var params = "target=" + target + "&type=" + profileViewDisplayType + "&page=" + nextPage + "&count=" + postsPerQuery;
    var url = contextRoot + "api/posts";
    httpReq.open("GET", url + "?" + params);
    httpReq.send();
}

function toggleNewCommentVisibility(button) {
    var newCommentElementId = button.getAttribute("data-newcommentid");
    newCommentElement = document.getElementById(newCommentElementId);
    var style = window.getComputedStyle(newCommentElement);
    newCommentElement.style.display = style.display == "none" ? "block" : "none";
}

function ratePost(button) {
    var postId = button.getAttribute("data-postid");
    var rateAction = button.getAttribute("data-rateaction");
    var postLikeCountElement = document.getElementById("post-" + postId + "-likecount");
    var url = contextRoot + "api/posts/" + (rateAction === "LIKE" ? "likepost" : "removelike");

    sendPostAction(postId, url, new FormData(), function (httpReq) {
        if (httpReq.readyState != 4) {
            return;
        }
        // Check results.
        if (httpReq.status != 200) {
            // Patenttiratkaisu.
            alert("Jotain hajosi :( Virhekoodi: " + httpReq.status + ": " + httpReq.responseText);
        } else {
            var likeCount = parseInt(postLikeCountElement.getAttribute("data-postlikecount"));
            if (rateAction === "LIKE") {
                button.classList.add("ftb-cancel");
                button.innerHTML = "Poista tykkäys";
                button.setAttribute("data-rateaction", "REMOVE_LIKE");
                likeCount++;
            } else {
                button.classList.remove("ftb-cancel");
                button.innerHTML = "Tykkää";
                button.setAttribute("data-rateaction", "LIKE");
                likeCount--;
            }
            postLikeCountElement.setAttribute("data-postlikecount", likeCount);
            postLikeCountElement.innerHTML = likeCount + " Tykkäystä";
        }
    });
}

function deleteImagePost(button) {
    var postId = button.getAttribute("data-postid");

    var postInfoWrapperElement = document.getElementById("post-" + postId + "-info-wrapper");
    var commentActionsElement = postInfoWrapperElement.querySelector(".comment-actions");
    var formLoader = postInfoWrapperElement.querySelector(".form-loader-container .lds-ring");

    commentActionsElement.style.display = "none";
    formLoader.style.display = "block";

    var url = contextRoot + "api/posts/deleteimagepost";
    sendPostAction(postId, url, new FormData(), function (httpReq) {
        if (httpReq.readyState != 4) {
            return;
        }
        // Check results.
        if (httpReq.status != 200) {
            commentActionsElement.style.display = "block";
            formLoader.style.display = "none";
            removeOldErrorMessages(commentActionsElement);
            var errorElement = createErrorElement("Nyt kävi niin, että jotain hajosi :/");
            commentActionsElement.appendChild(errorElement);
            // Patenttiratkaisu.
            setTimeout(function () { alert("Jotain hajosi :( Virhekoodi: " + httpReq.status + ": " + httpReq.responseText); }, 200);
        } else {
            alert("Success!");
            //TODO: DELETE POST FROM LIST.
        }
        //TODO: DO SOMETHING WITH THE RESPONSE?
    });
}

function submitCreateNewCommentForm(form) {
    // Find elements.
    var commentElement = form.closest(".new-comment");
    var buttonWrapper = commentElement.querySelector(".fsubmit-button-wrapper");
    var formLoader = commentElement.querySelector(".form-loader-container .lds-ring");
    var formData = new FormData(form);

    var isPost = commentElement.id === "new-post";
    var isImage = isPost ? formData.has("file") : false;
    var postTarget = document.getElementById(isPost ? "main-post-list" : "post-replies-" + formData.get("post"));
    var postTargetArray = isPost ? postTarget : postTarget.querySelector(".posts-list ul");

    // Disable user input and show loader.
    updateAllFormInputs(form, "disabled", true);
    buttonWrapper.style.display = "none";
    formLoader.style.display = "block";

    // HTTP Request.
    var httpReq = new XMLHttpRequest();
    httpReq.onreadystatechange = function () {
        if (httpReq.readyState != 4) {
            return;
        }
        // Hide loader.
        buttonWrapper.style.display = "block";
        formLoader.style.display = "none";
        // Check results.
        if (httpReq.status != 200) {
            // Enable user input.
            updateAllFormInputs(form, "disabled", false);
            newCommentCreationFail(form, "Nyt kävi niin, että jotain hajosi :/");
            // Patenttiratkaisu.
            setTimeout(function () { alert("Jotain hajosi :( Virhekoodi: " + httpReq.status + ": " + httpReq.responseText); }, 200);
        } else {
            form.reset();
            resetFormErrorMessages(form);
            resizeCommentBoxTextarea(form.querySelector(".comment-textarea"));
            // Make sure it's visible.
            if (!isPost) {
                postTargetArray.style.display = "block";
                hideNoCommentsNotification(postTarget);
            }
            // Add to comment/post list.
            postTargetArray.insertAdjacentHTML("afterbegin", httpReq.responseText);
            // Update comment/post count.
            var totalPostCountElement;
            if (isPost) {
                totalPostCountElement = document.getElementById("total-post-count");
            } else {
                totalPostCountElement = document.getElementById("post-" + formData.get("post") + "-total-comment-count");
            }
            var postCount = parseInt(totalPostCountElement.getAttribute("data-totalcount")) + 1;
            totalPostCountElement.setAttribute("data-totalcount", postCount);
            totalPostCountElement.innerHTML = postCount + (isPost ? (isImage ? " Kuvaa" : " Julkaisua") : " Kommenttia <i class='fas fa-chevron-down'></i>");
            if (isImage && postCount >= 10) {
                var errorElement = createErrorElement("Albumissasi voi olla maksimissaan 10 kuvaa kerrallaan!");
                buttonWrapper.insertBefore(errorElement, buttonWrapper.firstChild);
                setInputsDisplay(buttonWrapper, "none");
            } else {
                // Enable user input.
                updateAllFormInputs(form, "disabled", false);
            }
        }
    }
    httpReq.open("POST", form.action);
    httpReq.send(formData);
    return false;
}

function sendPostAction(postId, url, formData, eventListener) {
    formData.append("accountId", auserId);
    formData.append("postId", postId);
    formData.append(_csrf_param_name, _csrf_token);

    var httpReq = new XMLHttpRequest();
    httpReq.onreadystatechange = function () { eventListener(httpReq); };

    httpReq.open("POST", url);
    httpReq.send(formData);
}

function newCommentCreationFail(form, errorMsg) {
    resetFormErrorMessages(form);
    form.querySelectorAll(".comment-textarea").forEach(function (textArea) {
        if (!textArea.classList.contains("fe-invalid-value")) {
            textArea.classList.add("fe-invalid-value");
        }
    });
    var buttonWrapper = form.querySelector(".fsubmit-button-wrapper");
    buttonWrapper.insertBefore(createErrorElement(errorMsg), buttonWrapper.firstChild);
}

function createErrorElement(errorMsg) {
    var errorElement = document.createElement("p");
    errorElement.classList.add("fe-error-message");
    errorElement.appendChild(document.createTextNode(errorMsg));
    return errorElement;
}

function resetFormErrorMessages(form, textarea) {
    form.querySelectorAll(".comment-textarea").forEach(function (textArea) {
        if (textArea.classList.contains("fe-invalid-value")) {
            textArea.classList.remove("fe-invalid-value");
        }
    });
    var buttonWrapper = form.querySelector(".fsubmit-button-wrapper");
    removeOldErrorMessages(buttonWrapper);
}

function removeOldErrorMessages(parent) {
    var messageElements = parent.querySelectorAll(".fe-error-message");
    messageElements.forEach(function (errorElement) {
        parent.removeChild(errorElement);
    });
}

function setInputsDisplay(parent, display) {
    var inputs = parent.querySelectorAll("input, button");
    inputs.forEach(function (input) {
        input.style.display = display;
    });
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