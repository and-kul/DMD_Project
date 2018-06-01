function moreResults(event) {
    var button = $('#btn-more-results');
    button.detach();

    $.ajax({
        url: "/more-results",
        type: "POST",
        data: {
            'searchID': searchID
        },
        dataType: "json",
        success: function (message) {
            var results = $('#results');

            var h4;
            var div;
            var thumbnail;
            var a;
            var small;
            var p;
            for (var i = 0; i < message.articles.length; ++i) {
                div = $(document.createElement('div')).addClass('collapse');

                thumbnail = $(document.createElement('div')).addClass('thumbnail');

                h4 = $(document.createElement('h4'));


                a = $(document.createElement('a'))
                    .attr('href', '/article?id=' + message.articles[i].id)
                    .text(message.articles[i].title);

                h4.append(a);
                h4.append(document.createElement('br'));


                small = $(document.createElement('small'));

                var text = "";
                var authors = message.articles[i].authors;
                for (var j = 0; j < authors.length - 1; ++j)
                    text += authors[j] + ", ";
                text += authors[authors.length - 1];

                small.text(text);

                h4.append(small);
                thumbnail.append(h4);

                p = $(document.createElement('p'));
                p.text(message.articles[i].summary);
                thumbnail.append(p);

                p = $(document.createElement('p'));
                small = $(document.createElement('small'));
                small.text(message.articles[i].published);
                p.append(small);
                thumbnail.append(p);

                div.append(thumbnail);
                results.append(div);
                div.collapse('show');
            }

            if (message.moreResults) {
                results.append(button);
            }

        }
    });


}


function submit(event) {
    event.preventDefault();
    var results = document.getElementById('results');
    results.innerHTML = "";
    var form = event.target;
    var category = form.elements.category.value;
    var author0 = form.elements.author0.value;
    var author1 = form.elements.author1.value;
    var yearFrom = Math.round(form.elements.yearFrom.value);
    if (yearFrom == 0) yearFrom = 1997;
    var yearTo = Math.round(form.elements.yearTo.value);
    if (yearTo == 0) yearTo = (new Date()).getFullYear();
    yearFrom = Math.min(Math.max(1997, yearFrom), (new Date()).getFullYear());
    yearTo = Math.max(Math.min(yearTo, (new Date()).getFullYear()), 1997);
    if (yearFrom > yearTo) {
        var tmp = yearFrom;
        yearFrom = yearTo;
        yearTo = tmp;
    }
    var keywords = form.elements.keywords.value;
    var affil = form.elements.affil.value;
    var sortBy = form.elements.sortBy.value;
    searchID = Date.now();

    var xhr = new XMLHttpRequest();
    yearTo++;
    var body = 'category=' + encodeURIComponent(category) +
        '&author0=' + encodeURIComponent(author0) +
        '&author1=' + encodeURIComponent(author1) +
        '&yearFrom=' + encodeURIComponent(yearFrom) +
        '&yearTo=' + encodeURIComponent(yearTo) +
        '&keywords=' + encodeURIComponent(keywords) +
        '&affil=' + encodeURIComponent(affil) +
        '&sortBy=' + encodeURIComponent(sortBy) +
        '&searchID=' + encodeURIComponent(searchID);

    yearTo--;

    xhr.open("POST", '/search', true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function () {
        var message = JSON.parse(xhr.responseText);
        var h4 = document.createElement('h4');
        h4.textContent = "Results";
        results.appendChild(h4);

        if (message.articles.length == 0) {
            var h3 = document.createElement('h3');
            h3.style.marginTop = '26px';
            h3.textContent = "No results :(";
            results.appendChild(h3);
            return;
        }

        var div;
        var thumbnail;
        var a;
        var small;
        var p;
        var button;
        for (var i = 0; i < message.articles.length; ++i) {
            div = document.createElement('div');
            div.classList.add('collapse');
            thumbnail = document.createElement('div');
            thumbnail.classList.add('thumbnail');
            h4 = document.createElement('h4');
            a = document.createElement('a');
            a.setAttribute('href', '/article?id=' + message.articles[i].id);
            a.textContent = message.articles[i].title;
            h4.appendChild(a);
            h4.appendChild(document.createElement('br'));
            small = document.createElement('small');
            small.textContent = "";
            var authors = message.articles[i].authors;
            for (var j = 0; j < authors.length - 1; ++j)
                small.textContent += authors[j] + ", ";
            small.textContent += authors[authors.length - 1];
            h4.appendChild(small);
            thumbnail.appendChild(h4);

            p = document.createElement('p');
            p.textContent = message.articles[i].summary;
            thumbnail.appendChild(p);

            p = document.createElement('p');
            small = document.createElement('small');
            small.textContent = message.articles[i].published;
            p.appendChild(small);
            thumbnail.appendChild(p);

            div.appendChild(thumbnail);
            results.appendChild(div);
            $(div).collapse('show');
        }

        if (message.moreResults) {
            button = document.createElement('button');
            button.setAttribute('type', 'button');
            button.className = 'btn btn-default btn-block';
            button.textContent = 'More results';
            button.setAttribute('id', 'btn-more-results');
            button.style.marginBottom = '32px';
            results.appendChild(button);
            btnMoreResults = document.getElementById('btn-more-results');
            btnMoreResults.addEventListener('click', moreResults);
        }

    };

    xhr.send(body);
    form.elements.yearFrom.value = yearFrom;
    form.elements.yearTo.value = yearTo;

}


function checkAuthor1(event) {
    event.preventDefault();
    if (document.querySelector('#author1 > input').value == "")
        $('#author1').collapse('toggle');
}

var btnAuthor0 = document.getElementById("btn-author0");
btnAuthor0.addEventListener('click', checkAuthor1);

var form = document.forms.form1;
form.addEventListener('submit', submit);

var btnMoreResults;
var searchID;
