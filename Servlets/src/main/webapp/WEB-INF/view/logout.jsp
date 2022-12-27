<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: linko
  Date: 26.12.2022
  Time: 22:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Logout</title>
</head>
<body>
  <h1>Authorized <c:out value="${role}" /> <c:out value="${login}" /></h1>
  <input id="logout" onclick="logout()" value="Logout" type="button">

  <script type="text/javascript">
    function logout() {
      let xhr = new XMLHttpRequest();
      xhr.open("POST", "/");
      xhr.send();
    }
  </script>
</body>
</html>
