<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<head>
    <title>User</title>
</head>
<body>
<jsp:include page="/main/header"/>
Username: ${user.userName}<br>
Real name: ${user.realName}<br>
<a href="/AuthGame/Game/${user.currentGameId}">Current game</a>
</body>
</html>
