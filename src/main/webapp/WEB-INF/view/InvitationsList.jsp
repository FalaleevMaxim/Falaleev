<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Invitations</title>
    <style>
        .invitation:link{
            text-decoration: none;
            color: crimson;
            background-color: bisque;
            border: solid 1px black;
            display: block;
        }
    </style>
</head>
<body>
<jsp:include page="/main/header"/>
<c:if test="${invitationIds.size()>0}">
    <c:forEach var="i" begin="0" end="${invitationIds.size()-1}">
        <a href="/AuthGame/Game/${invitationIds.get(i)}" class="invitation" id="game_${invitationIds.get(i)}">
            Owner: <object><a href="/User/Profile/${gamesOwners.get(i).id}">${gamesOwners.get(i).userName}</a></object><br>
            Field: ${invitationGames.get(i).width}*${invitationGames.get(i).height}<br>
            Bombs: ${invitationGames.get(i).bombcount} Initial score: ${invitationGames.get(i).score}
        </a>
    </c:forEach>
</c:if>
<c:if test="${invitationIds.size()==0}">
    <h1>No invitations</h1>
</c:if>
</body>
</html>
