<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    <a href="<c:url value="/login"/> ">Log in</a>
    <a href="${pageContext.request.contextPath}/User/Register">Register</a><br/>
    <a href="${pageContext.request.contextPath}/UnauthGame/GameStart">Start new game</a>
</div>