$(document).ready(function() {
    $.ajax({
        url: "/same-authors",
        type: "POST",
        data: {
            id : document.querySelector("meta[name = 'article_id']").getAttribute("content")
        },
        dataType: "html",
        success: function(response) {
            document.getElementById('collapseAuthors').innerHTML = response;
        }
    });

    $.ajax({
        url: "/close-theme",
        type: "POST",
        data: {
            id : document.querySelector("meta[name = 'article_id']").getAttribute("content")
        },
        dataType: "html",
        success: function(response) {
            document.getElementById('collapseTheme').innerHTML = response;
        }
    });
});




