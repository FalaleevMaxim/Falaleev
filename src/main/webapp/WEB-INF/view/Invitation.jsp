<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Invitation</title>
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/invitation.css">
    <c:if test="${isOwner}">
        <script src="${pageContext.request.contextPath}/resources/js/invitation.js"></script>
    </c:if>
</head>
<body>
<jsp:include page="/main/header"/>
Field size: ${properties.width}*${properties.height}<br>
Bombs: ${properties.bombcount}<br>
Initial score: ${properties.score}<br>
<hr>
<c:if test="${isOwner}">
    <input type="text" id="name"><button onclick="invite($('#name').val())">Invite</button>
</c:if>

<table id="players" style="border-collapse: collapse; border: 2px solid;">
    <tr>
        <td class="td_player_name">name</td>
        <td class="td_player_status">status</td>
        <c:if test="${isOwner}">
            <td class="td_player_action">action</td>
        </c:if>
    </tr>
    <c:forEach var="i" begin="0" end="${players.size()-1}">
        <tr class="tr_player" id="tr_player_${players.get(i).id}">
            <td class="td_player_name">${players.get(i).name}</td>
            <c:if test="${owner.equals(players.get(i).id)}">
                <td class="td_player_status td_status_owner">Owner</td>
                <c:if test="${isOwner}">
                    <td class="td_player_action"></td>
                </c:if>
            </c:if>

            <c:if test="${!owner.equals(players.get(i).id)}">
                <c:if test="${players.get(i).ownerConfirmed}">
                    <c:if test="${players.get(i).playerConfirmed}">
                        <td class="td_player_status td_status_ingame">In game</td>
                        <c:if test="${isOwner}">
                            <td class="td_player_action td_action_uninvite">Uninvite</td>
                        </c:if>
                    </c:if>
                    <c:if test="${!players.get(i).playerConfirmed}">
                        <td class="td_player_status td_status_invited">Invited</td>
                        <c:if test="${isOwner}">
                            <td class="td_player_action td_action_uninvite">Uninvite</td>
                        </c:if>
                    </c:if>
                </c:if>
                <c:if test="${!players.get(i).ownerConfirmed && players.get(i).playerConfirmed}">
                    <td class="td_player_status td_status_request">Requests join</td>
                    <c:if test="${isOwner}">
                        <td class="td_player_action td_action_in" onclick="invite('${players.get(i).name}')">Take in</td>
                    </c:if>
                </c:if>
            </c:if>

        </tr>
    </c:forEach>

</table>
<hr>
<c:if test="${!isOwner}">
    <a href="/AuthGame/join/${gameId}">Join</a>
    <input type="hidden" value="${gameId}" name="gameId" id="gameId">
</c:if>
<c:if test="${isOwner}">
    <a href="${pageContext.request.contextPath}/AuthGame/CompleteInvitation">Complete invitation</a>
</c:if>
</body>
</html>
