<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div style="background-color: #0081b5">
    Logged as:<br/>
    ${UserName}<br/>
    ${RealName}<br/>
    <a href="<c:url value="/logout"/> ">Logout</a><br/>
    <a href="${pageContext.request.contextPath}/User/Profile">Profile</a>&nbsp;&nbsp;&nbsp;
    <a href="${pageContext.request.contextPath}/AuthGame/GameStart">Start new game</a>&nbsp;&nbsp;&nbsp;
    <a href="${pageContext.request.contextPath}/AuthGame/Game">Resume game</a><br>
    <input type="hidden" name="userId" id="userId" value="${userId}">
    <input type="hidden" name="userName" id="userName" value="${UserName}">
</div>
<script src="${pageContext.request.contextPath}/resources/js/invitationNotification.js"></script>