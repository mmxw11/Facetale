function formSubmitPasswordListener(event) {
    var passwordElement = document.getElementById('form-password');
    var passwordConfirmElement = document.getElementById('form-password-2');
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
        const felement = formGroups[i].getElementsByClassName("form-element")[0];
        felement.addEventListener("click", setFormElementValid, false);
        felement.addEventListener("input", setFormElementValid, false);
    }
}

function setFormElementValid(event) {
    var felement = event.target;
    felement.classList.remove("fe-invalid-value");
    felement.setCustomValidity("");
}