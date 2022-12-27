<%--
  Created by IntelliJ IDEA.
  User: linko
  Date: 25.12.2022
  Time: 21:32
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Activities</title>
</head>
<body>

<h1>Welcome to application "Time Keeping"</h1><br />

<h2>All activities</h2><br />

<h3>Sorting by</h3>
<form method="GET" action="/active">
    <select name="sort">
        <option value="category">category</option>
        <option value="duration">duration</option>
    </select>
    <input type="submit" value="Ok" name="Ok"><br>
</form>

<h3>Filtering by</h3>
<form method="GET" action="/active">
    <select name="filter">
        <c:forEach var="category" items="${requestScope.categories}">
            <option value="${category.type}"><c:out value="${category.type}" /></option>
        </c:forEach>
    </select>
    <input type="submit" value="Ok" name="Ok"><br>
</form>

<c:forEach var="activity" items="${requestScope.activities}">
    <ul>

        <li>id: <c:out value="${activity.id}"/></li>

        <li>category: <c:out value="${activity.category}"/></li>
        <li>user: <c:out value="${activity.user}"/></li>
        <li>duration: <c:out value="${activity.duration}"/></li>
        <c:if test="${activity.confirmed == false}">
            <input class="confirm-button" id="${activity.id}" onclick="confirm(this.id);" value="confirm" type="button">
        </c:if>
    </ul>
    <hr />

</c:forEach>

<script type="text/javascript">
    // const buttons = document.querySelectorAll(".confirm-button");
    // for (let i = 0; i < buttons.length; i++) {
    //     const btn = buttons[i];
    //     btn.addEventListener("click", function() {
    //         const activityId = btn.id;
    //         $.ajax({
    //             url: "/active",
    //             type: "PUT",
    //             data: activityId,
    //             success: function() {
    //                 alert("Success")
    //             }
    //         })
    //     })
    // }

    function confirm(id) {
        let xhr = new XMLHttpRequest();
        xhr.open("PUT", "/active");
        xhr.send(id);

        xhr.onload = function() {
            if (xhr.status == 200) {
                const btn = document.getElementById(id);
                $(btn).style.visibility = "hidden";
            } else {
                alert("Error " + xhr.status + ": " + xhr.statusText);
            }
        }
    }
</script>
</body>
</html>
