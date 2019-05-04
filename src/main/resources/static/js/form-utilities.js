function formSubmitPasswordListener(event) {
    var passwordElement = document.getElementById('account-password');
    var passwordConfirmElement = document.getElementById('account-password-2');
    if (passwordElement.value !== passwordConfirmElement.value) {
        passwordConfirmElement.classList.add("fe-invalid-value");
        passwordConfirmElement.setCustomValidity("Salasanat eivät täsmää.");
        try {
            passwordConfirmElement.reportValidity();
        } catch (ex) {
            // IE Fix. IS IE still relevant?
            alert("Salasanat eivät täsmää.");
        }
        event.preventDefault()
    }
}

function registerFormGroupValidationListeners() {
    var formGroups = document.getElementsByClassName("form-group");
    for (let i = 0; i < formGroups.length; i++) {
        const felement = formGroups[i].getElementsByClassName("form-input-element")[0];
        felement.addEventListener("click", setFormElementValid, false);
        felement.addEventListener("input", setFormElementValid, false);
    }
}

function updateAllFormInputs(form, key, value) {
    var inputs = form.getElementsByTagName("input");
    var selects = form.getElementsByTagName("select");
    var textareas = form.getElementsByTagName("textarea");
    var buttons = form.getElementsByTagName("button");
    for (let i = 0; i < inputs.length; i++) {
        inputs[i][key] = value;
    }
    for (let i = 0; i < selects.length; i++) {
        selects[i][key] = value;
    }
    for (let i = 0; i < textareas.length; i++) {
        textareas[i][key] = value;
    }
    for (let i = 0; i < buttons.length; i++) {
        buttons[i][key] = value;
    }
}

function setFormElementValid(event) {
    var felement = event.target;
    felement.classList.remove("fe-invalid-value");
    felement.setCustomValidity("");
}