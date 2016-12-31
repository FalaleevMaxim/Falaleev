<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    Logged as:<br/>
    ${UserName}<br/>
    ${RealName}<br/>
    <a href="<c:url value="/logout"/> ">Logout</a>
</div>
<a href="${pageContext.request.contextPath}/User/Profile">Profile</a>&nbsp;&nbsp;&nbsp;
<a href="${pageContext.request.contextPath}/SinglePlayerGame/GameStart">Start new game</a>&nbsp;&nbsp;&nbsp;
<a href="${pageContext.request.contextPath}/SinglePlayerGame/Game">Resume game</a>
