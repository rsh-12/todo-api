function success() {

    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (confirmPassword !== password || confirmPassword.length < 8) {
        document.getElementById("password").style.borderColor = "red";
        document.getElementById("confirmPassword").style.borderColor = "red";
        document.getElementById('btn').hidden = true;
    } else {
        document.getElementById("password").style.borderColor = "#d6d4d4";
        document.getElementById("confirmPassword").style.borderColor = "#d6d4d4";
        document.getElementById('btn').hidden = false;
    }
}