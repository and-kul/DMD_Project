function loginSubmit(event) {
    event.preventDefault();
    var form1 = event.target;
    var login = form1.elements.login.value;
    var password = form1.elements.password.value;
    error = document.querySelector('#error1 > .well');
    if (login == "") {
        error.textContent = "Login is missing";
        $('#error1').collapse('show');
        return;
    }
    if (password == "") {
        error.textContent = "Password is missing";
        $('#error1').collapse('show');
        return;
    }


    var xhr = new XMLHttpRequest();
    var body = 'login=' + encodeURIComponent(login) +
        '&password=' + encodeURIComponent(password);

    xhr.open("POST", '/', true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function () {
        var message = JSON.parse(xhr.responseText);
        if (!message.ok) {
            error.textContent = message.error;
            $('#error1').collapse('show');
            return;
        }

        document.location.href = "/search";


    };

    xhr.send(body);
}


function signupSubmit(event) {
    event.preventDefault();
    var form2 = event.target;
    var newLogin = form2.elements.newLogin.value;
    var newPassword = form2.elements.newPassword.value;
    var confirm = form2.elements.confirm.value;
    error = document.querySelector('#error2 > .well');
    if (newLogin == "") {
        error.textContent = "Login is missing";
        $('#error2').collapse('show');
        return;
    }
    if (newPassword == "") {
        error.textContent = "Password is missing";
        $('#error2').collapse('show');
        return;
    }
    if (confirm == "") {
        error.textContent = "Password confirmation is missing";
        $('#error2').collapse('show');
        return;
    }
    if (confirm != newPassword) {
        error.textContent = "Password mismatch";
        $('#error2').collapse('show');
        return;
    }

    var xhr = new XMLHttpRequest();
    var body = 'newLogin=' + encodeURIComponent(newLogin) +
        '&newPassword=' + encodeURIComponent(newPassword);

    xhr.open("POST", '/signup', true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function () {
        var resp = JSON.parse(xhr.responseText);
        if (!resp.ok) {
            error.textContent = resp.error;
            $('#error2').collapse('show');
            return;
        }
        document.location.href = "/search";
    };

    xhr.send(body);
}



function focus1(event) {
    if (event.target.name == "password")
        event.target.value = "";
    $('#error1').collapse('hide');
}

function focus2(event) {
    if (event.target.name == "newPassword")
        event.target.value = "";
    if (event.target.name == "confirmPassword")
        event.target.value = "";
    $('#error2').collapse('hide');
}


var form1 = document.forms.form1;
form1.addEventListener('submit', loginSubmit);
form1.addEventListener('focus', focus1, true);

var form2 = document.forms.form2;
form2.addEventListener('submit', signupSubmit);
form2.addEventListener('focus', focus2, true);