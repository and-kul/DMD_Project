function deleteArticle(event) {
    event.preventDefault();
    var modal = $('#modalDelete');

    var thumbnail = this.parentElement.parentElement;

    var id = thumbnail.dataset.articleID;

    var deleteTitle = document.getElementById('deleteTitle');
    deleteTitle.setAttribute('href', '/article?id=' + id);
    deleteTitle.textContent = thumbnail.dataset.title;

    document.getElementById('deleteAccept').setAttribute('href', '/delete?id=' + id);

    modal.modal();
}

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
            var span;

            for (var i = 0; i < message.articles.length; ++i) {
                div = $(document.createElement('div')).addClass('collapse');

                thumbnail = $(document.createElement('div'))
                    .addClass('thumbnail')
                    .css('position', 'relative')
                    .data('articleID', message.articles[i].id)
                    .data('title', message.articles[i].title);


                h4 = $(document.createElement('h4')).css({'position': 'absolute', 'top': '0', 'right': '10px'});
                a = $(document.createElement('a')).attr('href', '#').click(deleteArticle);
                span = $(document.createElement('span')).addClass("glyphicon glyphicon-remove");
                a.append(span);
                h4.append(a);
                thumbnail.append(h4);


                h4 = $(document.createElement('h4'));
                a = $(document.createElement('a'))
                    .attr('href', '/article?id=' + message.articles[i].id)
                    .text(message.articles[i].title);
                h4.append(a);

                span = $(document.createElement('span')).text(" ");
                h4.append(span);


                a = $(document.createElement('a')).attr('href', '/update?id=' + message.articles[i].id);
                span = $(document.createElement('span')).addClass("glyphicon glyphicon-pencil");
                a.append(span);
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


var btnMoreResults;

var results = document.getElementById('results');
var searchID = Date.now();

var xhr = new XMLHttpRequest();
var body = 'searchID=' + encodeURIComponent(searchID);

xhr.open("POST", '/my', true);
xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
xhr.onload = function () {
    var message = JSON.parse(xhr.responseText);
    var div;
    var thumbnail;
    var a;
    var small;
    var p;
    var button;
    var span;
    for (var i = 0; i < message.articles.length; ++i) {
        div = document.createElement('div');
        div.classList.add('collapse');
        thumbnail = document.createElement('div');
        thumbnail.classList.add('thumbnail');
        thumbnail.style.position = 'relative';
        thumbnail.dataset.articleID = message.articles[i].id;
        thumbnail.dataset.title = message.articles[i].title;

        h4 = document.createElement('h4');
        h4.style.position = 'absolute';
        h4.style.top = '0';
        h4.style.right = '10px';
        a = document.createElement('a');
        a.addEventListener('click', deleteArticle);
        a.setAttribute('href', '#');
        span = document.createElement('span');
        span.classList.add("glyphicon");
        span.classList.add("glyphicon-remove");
        a.appendChild(span);
        h4.appendChild(a);
        thumbnail.appendChild(h4);

        h4 = document.createElement('h4');
        h4.style.marginRight = "32px";
        a = document.createElement('a');
        a.setAttribute('href', '/article?id=' + message.articles[i].id);
        a.textContent = message.articles[i].title;
        h4.appendChild(a);

        span = document.createElement('span');
        span.textContent = " ";
        h4.appendChild(span);

        a = document.createElement('a');
        a.setAttribute('href', '/update?id=' + message.articles[i].id);
        span = document.createElement('span');
        span.classList.add("glyphicon");
        span.classList.add("glyphicon-pencil");
        a.appendChild(span);
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
        button.textContent = 'Previous publications';
        button.setAttribute('id', 'btn-more-results');
        button.style.marginBottom = '32px';
        results.appendChild(button);
        btnMoreResults = document.getElementById('btn-more-results');
        btnMoreResults.addEventListener('click', moreResults);
    }

};

xhr.send(body);
