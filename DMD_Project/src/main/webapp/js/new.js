function submit() {
    event.preventDefault();
    error = document.querySelector('#error > .well');

    var categoryID = form.elements.category.value;
    if (categoryID == 0) {
        error.textContent = "Select a category";
        $('#error').collapse('show');
        return;
    }

    var title = form.elements.title.value = form.elements.title.value.trim();
    if (title == "") {
        error.textContent = "Fill in the title";
        $('#error').collapse('show');
        return;
    }

    var ok = false;
    for (var i = 0; i < authorsCount; ++i) {
        form.elements['author' + i].value = form.elements['author' + i].value.trim();
        if (form.elements['author' + i].value != "") {
            ok = true;
        }
    }
    if (!ok) {
        error.textContent = "Provide at least one author name";
        $('#error').collapse('show');
        return;
    }

    var summary = form.elements.summary.value;
    if (summary == "") {
        error.textContent = "Fill in the summary";
        $('#error').collapse('show');
        return;
    }

    for (i = 0; i < affilsCount; ++i)
        form.elements['affil' + i].value = form.elements['affil' + i].value.trim();

    form.elements.comment.value = form.elements.comment.value.trim();
    form.elements.journal.value = form.elements.journal.value.trim();
    form.elements.doi.value = form.elements.doi.value.trim();
    form.elements.pdf.value = form.elements.pdf.value.trim();

    form.submit();
}


function addAuthor(event) {
    event.preventDefault();
    var plus = $(document.createElement('span'));
    plus.addClass("input-group-addon");
    var glyphicon = $(document.createElement('span'));
    glyphicon.addClass("glyphicon glyphicon-plus")
        .attr('aria-hidden', "true");
    plus.append(glyphicon);

    var button = $('#input-group-author' + (authorsCount - 1) + ' > span');

    button = button.replaceWith(plus);

    var formGroup = $(document.createElement('div'))
        .addClass("form-group serial")
        .attr('id', "form-group-author" + authorsCount);

    var col = $(document.createElement('div')).addClass("col-sm-10 col-sm-offset-2");

    var collapse = $(document.createElement('div')).addClass(collapse);

    var inputGroup = $(document.createElement('div'))
        .addClass("input-group")
        .attr('id', "input-group-author" + authorsCount);


    var input = $(document.createElement('input'))
        .addClass("form-control")
        .attr('type', "text")
        .attr('name', "author" + authorsCount)
        .attr('id', "author" + authorsCount);

    inputGroup.append(input).append(button);
    collapse.append(inputGroup);
    col.append(collapse);
    formGroup.append(col);

    $('#form-group-author' + (authorsCount - 1)).after(formGroup);

    collapse.collapse('show');

    ++authorsCount;
}

function addAffil(event) {
    event.preventDefault();
    var plus = $(document.createElement('span'));
    plus.addClass("input-group-addon");
    var glyphicon = $(document.createElement('span'));
    glyphicon.addClass("glyphicon glyphicon-plus")
        .attr('aria-hidden', "true");
    plus.append(glyphicon);

    var button = $('#input-group-affil' + (affilsCount - 1) + ' > span');

    button = button.replaceWith(plus);

    var formGroup = $(document.createElement('div'))
        .addClass("form-group serial")
        .attr('id', "form-group-affil" + affilsCount);

    var col = $(document.createElement('div')).addClass("col-sm-10 col-sm-offset-2");

    var collapse = $(document.createElement('div')).addClass(collapse);

    var inputGroup = $(document.createElement('div'))
        .addClass("input-group")
        .attr('id', "input-group-affil" + affilsCount);


    var input = $(document.createElement('input'))
        .addClass("form-control")
        .attr('type', "text")
        .attr('name', "affil" + affilsCount)
        .attr('id', "affil" + affilsCount);

    inputGroup.append(input).append(button);
    collapse.append(inputGroup);
    col.append(collapse);
    formGroup.append(col);

    $('#form-group-affil' + (affilsCount - 1)).after(formGroup);

    collapse.collapse('show');

    ++affilsCount;
}

function addLogin(event) {
    event.preventDefault();
    var plus = $(document.createElement('span'));
    plus.addClass("input-group-addon");
    var glyphicon = $(document.createElement('span'));
    glyphicon.addClass("glyphicon glyphicon-plus")
        .attr('aria-hidden', "true");
    plus.append(glyphicon);

    var button = $('#input-group-login' + (loginsCount - 1) + ' > span');

    button = button.replaceWith(plus);

    var formGroup = $(document.createElement('div'))
        .addClass("form-group serial")
        .attr('id', "form-group-login" + loginsCount);

    var col = $(document.createElement('div')).addClass("col-sm-10 col-sm-offset-2");

    var collapse = $(document.createElement('div')).addClass(collapse);

    var inputGroup = $(document.createElement('div'))
        .addClass("input-group")
        .attr('id', "input-group-login" + loginsCount);


    var input = $(document.createElement('input'))
        .addClass("form-control")
        .attr('type', "text")
        .attr('name', "login" + loginsCount)
        .attr('id', "login" + loginsCount);

    inputGroup.append(input).append(button);
    collapse.append(inputGroup);
    col.append(collapse);
    formGroup.append(col);

    $('#form-group-login' + (loginsCount - 1)).after(formGroup);

    collapse.collapse('show');

    ++loginsCount;
}


var form = document.forms.form1;
form.addEventListener('submit', submit);
form.addEventListener('focus', focus1, true);

var btnAuthor0 = document.getElementById("btn-author");
btnAuthor0.addEventListener('click', addAuthor);

var btnAffil0 = document.getElementById("btn-affil");
btnAffil0.addEventListener('click', addAffil);

var btnLogin0 = document.getElementById("btn-login");
btnLogin0.addEventListener('click', addLogin);


function focus1(event) {
    $('#error').collapse('hide');
}


var authorsCount = 1;
var affilsCount = 1;
var loginsCount = 2;
