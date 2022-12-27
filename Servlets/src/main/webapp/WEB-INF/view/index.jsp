<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" href="../static/main.css" type="text/css"/>
    <style>
        <%@include file="/WEB-INF/static/main.css"%>
    </style>
    <title>Authorization</title>
</head>
<body>
    <section class="container">
        <div class="main-div">
            <h1>Authorization form</h1>
            <form method="POST" action="">
                <div class="form">
                    <div class="form-group">
                        <label>Login<input type="text" name="login"></label><br>
                        <label>Password<input type="password" name="password"></label><br>
                        <label><input class="checkmark" type="checkbox" name="isAdmin"> I am administrator</label>
                    </div>
                    <div class="form-group">
                        <input class="btn-filter" type="submit" name="submit" value="Submit">
                    </div>
                </div>
            </form>
        </div>
    </section>
</body>
</html>
