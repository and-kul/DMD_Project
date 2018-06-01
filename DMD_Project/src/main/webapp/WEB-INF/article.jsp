<%@ page import="dmd.ShortData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%! String authors = ""; %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="article_id" content="<%=request.getAttribute("id")%>">

    <title>Search</title>

    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/article.css" rel="stylesheet">

</head>

<body>
<!-- Static navbar -->
<nav class="navbar navbar-default navbar-static-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/">LibEngine</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="/search">Search</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
                       aria-expanded="false"><%=request.getSession().getAttribute("login")%> <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="/my">My articles</a></li>
                        <li><a href="/new">Add new</a></li>
                        <li role="separator" class="divider"></li>
                        <li><a href="/exit">Exit</a></li>
                    </ul>
                </li>
            </ul>
        </div>
        <!--/.nav-collapse -->
    </div>
</nav>

<div class="container">
    <div class="row">
        <div class="col-xs-12 col-sm-10 col-sm-offset-1">

            <h3 style="margin-bottom: 6px">
                <%=request.getAttribute("title")%>
            </h3>

            <h3 style="margin-top: 6px; margin-bottom: 0">
                <small><%=request.getAttribute("authors")%>
                </small>
            </h3>

            <% if (request.getAttribute("affiliations") != null) { %>

            <h3 style="margin-top: 0; margin-bottom: 6px; font-size: 19px">
                <small><%=request.getAttribute("affiliations")%>
                </small>
            </h3>

            <% } %>

            <p style="margin-top: 18px">
                <%=request.getAttribute("summary")%>
            </p>

            <p>
                <small>
                    <em><%=request.getAttribute("published")%>
                    </em>
                </small>
            </p>

            <hr>

            <p>
                Category: <em><%=request.getAttribute("category_name")%>
            </em>
            </p>

            <% if (request.getAttribute("comment") != null) { %>
            <p>
                Comments: <em><%=request.getAttribute("comment")%>
            </em>
            </p>
            <% } %>

            <% if (request.getAttribute("journal_ref") != null) { %>
            <p>
                Journal reference: <em><%=request.getAttribute("journal_ref")%>
            </em>
            </p>
            <% } %>

            <% if (request.getAttribute("doi") != null) { %>
            <p>
                DOI: <em><%=request.getAttribute("doi")%>
            </em>
            </p>
            <% } %>

            <% if (request.getAttribute("pdf") != null) { %>
            <p>
                <a href="<%=request.getAttribute("pdf")%>">PDF</a>
            </p>
            <% } %>

            <p style="margin-top: 20px">
                id = <%=request.getAttribute("id")%>
            </p>

            <hr>


            <div class="row">

                <div class="col-sm-12 col-lg-6">

                    <div class="panel panel-default">
                        <div class="panel-heading" id="headingAuthors">
                            <h4 class="panel-title">
                                <a role="button" data-toggle="collapse" href="#collapseAuthors" aria-expanded="true"
                                   aria-controls="collapseAuthors">
                                    The same authors
                                </a>
                            </h4>
                        </div>
                        <div id="collapseAuthors" class="panel-collapse collapse" aria-labelledby="headingAuthors">
                            <div class="panel-body">
                                <h5>Please wait...</h5>
                            </div>
                        </div>
                    </div>


                </div>


                <div class="col-sm-12 col-lg-6">


                    <div class="panel panel-default">
                        <div class="panel-heading" id="headingTheme">
                            <h4 class="panel-title">
                                <a role="button" data-toggle="collapse" href="#collapseTheme" aria-expanded="true"
                                   aria-controls="collapseTheme">
                                    Related topics
                                </a>
                            </h4>
                        </div>
                        <div id="collapseTheme" class="panel-collapse collapse" aria-labelledby="headingTheme">
                            <div class="panel-body">
                                <h5>Please wait...</h5>
                            </div>
                        </div>
                    </div>


                </div>


            </div>


        </div>
        <div class="col-sm-1"></div>
    </div>


</div>


<script src="/js/jquery-1.11.3.min.js"></script>
<script src="/js/bootstrap.min.js"></script>
<script src="/js/article.js"></script>

</body>

</html>