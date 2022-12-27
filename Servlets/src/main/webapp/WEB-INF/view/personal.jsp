<%--
  Created by IntelliJ IDEA.
  User: linko
  Date: 26.12.2022
  Time: 20:34
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><c:out value="${requestScope.user.login}" /></title>
</head>
<body>

    <h1>The page of user <c:out value="${requestScope.user.login}" /></h1>

    <c:forEach var="activity" items="${requestScope.activities}">
        <ul>

            <li>id: <c:out value="${activity.id}"/></li>

            <li>category: <c:out value="${activity.category.type}"/></li>
            <li>duration: <c:out value="${activity.duration}"/></li>
            <li>confirmed: <c:out value="${activity.confirmed}" /></li>
        </ul>
        <hr />

    </c:forEach>

    <h2>Create new activity</h2><br />

    <form method="post" action="">

        <label>
            <select name="category">
                <c:forEach var="category" items="${requestScope.categories}">
                    <option value="${category.type}"><c:out value="${category.type}" /></option>
                </c:forEach>
            </select>
        </label>Category<br>

        <label><input type="number" name="duration"></label>Duration<br>

        <input type="submit" value="Ok" name="Ok"><br>
    </form>
</body>
</html>
