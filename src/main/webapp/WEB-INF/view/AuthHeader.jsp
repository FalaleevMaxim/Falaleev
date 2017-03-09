<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<div style="background-color: #0081b5">
    Logged as:<br/>
    ${UserName}<br/>
    ${RealName}<br/>
    <a href="<c:url value="/logout"/> ">Logout</a><br/>
    <a href="${pageContext.request.contextPath}/User/Profile">Profile</a>&nbsp;&nbsp;&nbsp;
    <a href="${pageContext.request.contextPath}/AuthGame/GameStart">Start new game</a>&nbsp;&nbsp;&nbsp;
    <c:if test="${inGame}"><a href="${pageContext.request.contextPath}/AuthGame/Game" id="current_game">Current game</a></c:if>
    <a href="${pageContext.request.contextPath}/AuthGame/Invitations">Invitations(${invitations})</a><br>
    <input type="hidden" name="userId" id="userId" value="${userId}">
    <input type="hidden" name="userName" id="userName" value="${UserName}">
</div>
<div id="notifications" style="">

</div>
<style>
    body
    {
        background-image: url('/resources/images/background.jpg');
        background-size:100%;
    }
    #notifications{
        position:fixed;
        bottom: 0;
        right: 0;
        display: none;
    }
    .notification{
        border: solid 2px darkblue;
        background-color: bisque;
    }
</style>
<script src="${pageContext.request.contextPath}/resources/js/invitationNotification.js"></script>
<c:if test="${beforeGame}">
    <script src="${pageContext.request.contextPath}/resources/js/beforeGameListener.js"></script>
</c:if>
