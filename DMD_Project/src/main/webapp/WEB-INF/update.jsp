<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="article_id" content="<%=request.getAttribute("id")%>">
    <meta name="authorsCount" content="<%=request.getAttribute("authorsCount")%>">
    <meta name="loginsCount" content="<%=request.getAttribute("loginsCount")%>">
    <meta name="affilsCount" content="<%=request.getAttribute("affilsCount")%>">

    <title>Update</title>

    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/new.css" rel="stylesheet">
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
            <h3>Update article <%=request.getAttribute("id")%>
            </h3>

            <form action="/update" method="post" class="form-horizontal" name="form1" style="margin-top: 32px">

                <input type="hidden" name="id" value="<%=request.getAttribute("id")%>">

                <div class="form-group">
                    <label for="category" class="col-sm-2 control-label">Category *</label>

                    <div class="col-sm-10">
                        <select name="category" class="form-control" id="category">
                            <option value="0"></option>
                            <option value="1" <% if ((Integer) request.getAttribute("category_id") == 1) { %>
                                    selected <% } %> >Physics
                            </option>
                            <option value="2" <% if ((Integer) request.getAttribute("category_id") == 2) { %>
                                    selected <% } %> >Mathematics
                            </option>
                            <option value="3" <% if ((Integer) request.getAttribute("category_id") == 3) { %>
                                    selected <% } %> >Computer Science
                            </option>
                            <option value="4" <% if ((Integer) request.getAttribute("category_id") == 4) { %>
                                    selected <% } %> >Quantitative Biology
                            </option>
                            <option value="5" <% if ((Integer) request.getAttribute("category_id") == 5) { %>
                                    selected <% } %> >Quantitative Finance
                            </option>
                            <option value="6" <% if ((Integer) request.getAttribute("category_id") == 6) { %>
                                    selected <% } %> >Statistics
                            </option>
                        </select>
                    </div>
                </div>

                <div class="form-group" style="margin-bottom: 15px">
                    <label for="title" class="col-sm-2 control-label">Title *</label>

                    <div class="col-sm-10">
                        <input type="text" name="title" class="form-control" id="title"
                               value="<%=request.getAttribute("title")%>">
                    </div>
                </div>

                <div class="form-group serial" id="form-group-author0">
                    <label for="author0" class="col-sm-2 control-label">Authors *</label>

                    <div class="col-sm-10">
                        <div class="input-group" id="input-group-author0">
                            <input type="text" id="author0" class="form-control" name="author0"
                                   value="<%=((ArrayList)request.getAttribute("authors")).get(0)%>">

                            <% if (((ArrayList) request.getAttribute("authors")).size() == 1) { %>
                                <span class="input-group-btn">
                                <a id="btn-author" class="btn btn-default" href="#">
                                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                </a>
                                </span>

                            <% } else { %>

                            <span class="input-group-addon">
                                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                            </span>

                            <% } %>

                        </div>
                    </div>
                </div>

                <% for (int i = 1; i < ((ArrayList) request.getAttribute("authors")).size() - 1; ++i) { %>

                <div class="form-group serial" id="<%="form-group-author" + i%>">
                    <div class="col-sm-10 col-sm-offset-2">
                        <div class="input-group" id="<%="input-group-author" + i%>">
                            <input class="form-control" type="text" name="<%="author" + i%>" id="<%="author" + i%>"
                                   value="<%=((ArrayList)request.getAttribute("authors")).get(i)%>">
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                </span>
                        </div>
                    </div>
                </div>

                <% } %>


                <% if (((ArrayList) request.getAttribute("authors")).size() != 1) {
                    int last = (((ArrayList) request.getAttribute("authors")).size() - 1);
                %>

                <div class="form-group serial" id="<%="form-group-author" + last%>">
                    <div class="col-sm-10 col-sm-offset-2">
                        <div class="input-group" id="<%="input-group-author" + last%>">
                            <input class="form-control" type="text" name="<%="author" + last%>"
                                   id="<%="author" + last%>"
                                   value="<%=((ArrayList)request.getAttribute("authors")).get(last)%>">
                                <span class="input-group-btn">
                                <a id="btn-author" class="btn btn-default" href="#">
                                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                </a>
                                </span>
                        </div>
                    </div>
                </div>

                <% } %>

                <% if (((ArrayList) request.getAttribute("affiliations")).size() == 0) { %>

                <div class="form-group serial" id="form-group-affil0" style="margin-top: 15px">
                    <label for="affil0" class="col-sm-2 control-label">Affiliations</label>

                    <div class="col-sm-10">
                        <div class="input-group" id="input-group-affil0">
                            <input type="text" id="affil0" class="form-control" name="affil0" value="">
                                <span class="input-group-btn">
                                <a id="btn-affil" class="btn btn-default" href="#">
                                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                </a>
                                </span>
                        </div>
                    </div>
                </div>

                <% } else { %>


                <div class="form-group serial" id="form-group-affil0" style="margin-top: 15px">
                    <label for="affil0" class="col-sm-2 control-label">Affiliations</label>

                    <div class="col-sm-10">
                        <div class="input-group" id="input-group-affil0">
                            <input type="text" id="affil0" class="form-control" name="affil0"
                                   value="<%=((ArrayList)request.getAttribute("affiliations")).get(0)%>">

                            <% if (((ArrayList) request.getAttribute("affiliations")).size() == 1) { %>
                                <span class="input-group-btn">
                                <a id="btn-affil" class="btn btn-default" href="#">
                                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                </a>
                                </span>

                            <% } else { %>

                            <span class="input-group-addon">
                                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                            </span>

                            <% } %>

                        </div>
                    </div>
                </div>

                <% for (int i = 1; i < ((ArrayList) request.getAttribute("affiliations")).size() - 1; ++i) { %>

                <div class="form-group serial" id="<%="form-group-affil" + i%>">
                    <div class="col-sm-10 col-sm-offset-2">
                        <div class="input-group" id="<%="input-group-affil" + i%>">
                            <input class="form-control" type="text" name="<%="affil" + i%>" id="<%="affil" + i%>"
                                   value="<%=((ArrayList)request.getAttribute("affiliations")).get(i)%>">
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                </span>
                        </div>
                    </div>
                </div>

                <% } %>


                <% if (((ArrayList) request.getAttribute("affiliations")).size() != 1) {
                    int last = (((ArrayList) request.getAttribute("affiliations")).size() - 1);
                %>

                <div class="form-group serial" id="<%="form-group-affil" + last%>">
                    <div class="col-sm-10 col-sm-offset-2">
                        <div class="input-group" id="<%="input-group-affil" + last%>">
                            <input class="form-control" type="text" name="<%="affil" + last%>"
                                   id="<%="affil" + last%>"
                                   value="<%=((ArrayList)request.getAttribute("affiliations")).get(last)%>">
                                <span class="input-group-btn">
                                <a id="btn-affil" class="btn btn-default" href="#">
                                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                </a>
                                </span>
                        </div>
                    </div>
                </div>

                <% } %>
                <% } %>


                <div class="form-group" style="margin-top: 15px">
                    <label for="summary" class="col-sm-2 control-label">Summary *</label>

                    <div class="col-sm-10">
                        <textarea id="summary" name="summary" class="form-control"
                                  rows="8"><%=request.getAttribute("summary") != null ? request.getAttribute("summary") : ""%></textarea>
                    </div>
                </div>

                <div class="form-group">
                    <label for="comment" class="col-sm-2 control-label">Comments</label>

                    <div class="col-sm-10">
                        <input type="text" name="comment" class="form-control" id="comment"
                               value="<%=request.getAttribute("comment") != null ? request.getAttribute("comment") : ""%>">
                    </div>
                </div>

                <div class="form-group">
                    <label for="journal" class="col-sm-2 control-label">Journal reference</label>

                    <div class="col-sm-10">
                        <input type="text" name="journal" class="form-control" id="journal"
                               value="<%=request.getAttribute("journal_ref") != null ? request.getAttribute("journal_ref") : ""%>">
                    </div>
                </div>

                <div class="form-group">
                    <label for="doi" class="col-sm-2 control-label">DOI</label>

                    <div class="col-sm-10">
                        <input type="text" name="doi" class="form-control" id="doi"
                               value="<%=request.getAttribute("doi") != null ? request.getAttribute("doi") : ""%>">
                    </div>
                </div>

                <div class="form-group">
                    <label for="pdf" class="col-sm-2 control-label">PDF</label>

                    <div class="col-sm-10">
                        <input type="text" name="pdf" class="form-control" id="pdf"
                               value="<%=request.getAttribute("pdf") != null ? request.getAttribute("pdf") : ""%>">
                    </div>
                </div>

                <%-- logins --%>

                <input type="hidden" name="login0" value="root">


                <div class="form-group serial" id="form-group-login1" style="margin-top: 48px">
                    <label for="login1" class="col-sm-2 control-label">Users with access</label>

                    <div class="col-sm-10">
                        <div class="input-group" id="input-group-login1">
                            <input type="text" id="login1" class="form-control" name="login1"
                                   value="<%=request.getSession().getAttribute("login")%>" readonly>
                            <% if (((ArrayList) request.getAttribute("logins")).size() == 2) { %>
                                <span class="input-group-btn">
                                <a id="btn-login" class="btn btn-default" href="#">
                                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                </a>
                                </span>

                            <% } else { %>

                            <span class="input-group-addon">
                                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                            </span>

                            <% } %>
                        </div>
                    </div>
                </div>


                <% for (int i = 2; i < ((ArrayList) request.getAttribute("logins")).size() - 1; ++i) { %>

                <div class="form-group serial" id="<%="form-group-login" + i%>">
                    <div class="col-sm-10 col-sm-offset-2">
                        <div class="input-group" id="<%="input-group-login" + i%>">
                            <input class="form-control" type="text" name="<%="login" + i%>" id="<%="login" + i%>"
                                   value="<%=((ArrayList)request.getAttribute("logins")).get(i)%>">
                                <span class="input-group-addon">
                                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                </span>
                        </div>
                    </div>
                </div>

                <% } %>


                <% if (((ArrayList) request.getAttribute("logins")).size() != 2) {
                    int last = (((ArrayList) request.getAttribute("logins")).size() - 1);
                %>

                <div class="form-group serial" id="<%="form-group-login" + last%>">
                    <div class="col-sm-10 col-sm-offset-2">
                        <div class="input-group" id="<%="input-group-login" + last%>">
                            <input class="form-control" type="text" name="<%="login" + last%>"
                                   id="<%="login" + last%>"
                                   value="<%=((ArrayList)request.getAttribute("logins")).get(last)%>">
                                <span class="input-group-btn">
                                <a id="btn-login" class="btn btn-default" href="#">
                                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                </a>
                                </span>
                        </div>
                    </div>
                </div>

                <% } %>


                <div class="form-group" style="margin-top: 40px; margin-bottom: 40px">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="submit" class="btn btn-default btn-block">Save</button>
                        <div class="collapse" id="error">
                            <div class="well well-sm">
                                Error
                            </div>
                        </div>
                    </div>
                </div>

            </form>


        </div>

    </div>

</div>


<script src="/js/jquery-1.11.3.min.js"></script>
<script src="/js/bootstrap.min.js"></script>
<script src="/js/update.js"></script>

</body>

</html>
