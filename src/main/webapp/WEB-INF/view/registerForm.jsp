<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
</head>
<body>
    <jsp:include page="/main/header"/>
    <form method="post">
        Username: <input type="text" name="userName"><br>
        Real name: <input type="text" name="realName"><br>
        Password: <input type="password" name="password"><br>
        Confirmation: <input type="password" name="passConfirm">
        <input type="submit">
    </form>
</body>
</html>
