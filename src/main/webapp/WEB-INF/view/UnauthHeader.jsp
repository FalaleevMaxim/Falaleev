<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    body
    {
        background-image: url('/resources/images/background.jpg');
        background-size:100%;
    }
</style>
<div style="background-color: #0081b5">
    <a href="<c:url value="/User/Login"/> ">Log in</a>
    <a href="${pageContext.request.contextPath}/User/Register">Register</a><br/>
    <a href="${pageContext.request.contextPath}/UnauthGame/GameStart">Start new game</a>
</div>