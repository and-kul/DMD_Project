<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>LibEngine</title>

    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/search.css" rel="stylesheet">

</head>

<body>

<!-- Static navbar -->
<nav class="navbar navbar-default navbar-static-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="/">LibEngine</a>
        </div>
    </div>
</nav>

<div class="container">
    <h1 class="text-center">Welcome to LibEngine</h1>

    <div class="row" style="margin-top: 30px">
        <div class="col-xs-12 col-sm-8 col-sm-offset-2 col-md-6 col-md-offset-3">
            <form name="form1">
                <div class="form-group">
                    <input type="text" class="form-control" placeholder="Username" name="login" value="">
                </div>
                <div class="form-group">
                    <input type="password" class="form-control" placeholder="Password" name="password" value="">
                </div>

                <button type="submit" class="btn btn-default btn-block">Log In</button>
                <div class="collapse" id="error1">
                    <div class="well well-sm">
                        Error
                    </div>
                </div>

            </form>
        </div>
    </div>


    <div class="row" style="margin-top: 60px">
        <div class="col-xs-12 col-sm-8 col-sm-offset-2 col-md-6 col-md-offset-3">
            <form name="form2">
                <div class="form-group">
                    <input type="text" class="form-control" placeholder="New user" name="newLogin" value="">
                </div>
                <div class="form-group">
                    <input type="password" class="form-control" placeholder="New password" name="newPassword" value="">
                </div>
                <div class="form-group">
                    <input type="password" class="form-control" placeholder="Confirm password" name="confirm"
                           value="">
                </div>

                <button type="submit" class="btn btn-default btn-block">Sign Up</button>
                <div class="collapse" id="error2">
                    <div class="well well-sm">
                        Error
                    </div>
                </div>

            </form>


        </div>
    </div>


</div>


<script src="/js/jquery-1.11.3.min.js"></script>
<script src="/js/bootstrap.min.js"></script>
<script src="/js/start.js"></script>

</body>

</html>
