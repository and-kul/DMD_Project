<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Search</title>

    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/search.css" rel="stylesheet">

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
                <li class="active"><a href="/search">Search</a></li>
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
    <div class="row" style="margin-bottom: 32px">
        <div class="col-sm-4">
            <h4>Query parameters</h4>

            <form name="form1" style="margin-top: 16px">
                <div class="form-group">
                    <label for="selectCategory">Category</label>
                    <select name="category" class="form-control" id="selectCategory">
                        <option value="0" selected>All</option>
                        <option value="1">Physics</option>
                        <option value="2">Mathematics</option>
                        <option value="3">Computer Science</option>
                        <option value="4">Quantitative Biology</option>
                        <option value="5">Quantitative Finance</option>
                        <option value="6">Statistics</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="author0">Authors</label>

                    <div class="input-group">
                        <input type="text" id="author0" class="form-control" name="author0" placeholder="Name"
                               value="">
                            <span class="input-group-btn">
                                <a id="btn-author0" class="btn btn-default" href="#">
                                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                </a>
                            </span>
                    </div>

                    <div class="collapse" id="author1">
                        <input type="text" class="form-control" name="author1" placeholder="Name" value=""
                               style="margin-top: 8px">
                    </div>

                </div>

                <div class="form-group">
                    <label for="yearFrom">Years</label>

                    <div class="row">
                        <div class="col-md-6">
                            <input id="yearFrom" type="number" class="form-control" name="yearFrom" placeholder="From"
                                   value="">
                        </div>
                        <div class="col-md-6">
                            <input type="number" class="form-control" name="yearTo" placeholder="To"
                                   value="">
                        </div>
                    </div>
                </div>

                <div class="form-group" style="margin-top: 28px">
                    <input type="text" class="form-control" name="keywords" placeholder="Keywords"
                           value="">
                </div>

                <div class="form-group" style="margin-top: 20px">
                    <input type="text" class="form-control" name="affil" placeholder="Affiliation"
                           value="">
                </div>


                <div class="form-group" style="margin-top: 20px">
                    <label for="date">Sort by</label>
                    <br>
                    <label class="radio-inline">
                        <input type="radio" name="sortBy" id="date" value="date" checked> Date
                    </label>
                    <label class="radio-inline">
                        <input type="radio" name="sortBy" id="relevance" value="relevance"> Relevance
                    </label>
                </div>

                <button type="submit" class="btn btn-default" style="margin-top: 10px">Search</button>
            </form>

        </div>
        <div id="results" class="col-sm-8">

        </div>
    </div>
</div>


<script src="/js/jquery-1.11.3.min.js"></script>
<script src="/js/bootstrap.min.js"></script>
<script src="/js/search.js"></script>

</body>

</html>