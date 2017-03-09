<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Game</title>
    <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/resources/css/field.css">
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script src="${pageContext.servletContext.contextPath}/resources/js/SseGame.js"></script>
    <script src="${pageContext.servletContext.contextPath}/resources/js/ConnectionsGathering.js"></script>
    <script>
        bombsleft = ${properties.bombcount};
        score = ${properties.score};
        thisPlayer = ${userId};
    </script>
</head>
<body>
<jsp:include page="/main/header"/>
<div align="center" id="gameNotifications"></div>
<div>
    <table id="scoreTable">
        <c:forEach var="p" items="${connections}">
            <tr id="score_tr_${p.id}" class="score ${p.connected?"score-ok":"score-danger"} ">
                <td>${p.name}</td>
                <td>${p.connected?properties.score:0}</td>
            </tr>
        </c:forEach>
    </table>
    <div id="bombsLeft">${properties.bombcount}</div><br>
</div>
<table id="field">
    <c:forEach var="i" begin="0" end="${properties.height-1}">
        <tr>
            <c:forEach var="j" begin="0" end="${properties.width-1}">
                <td class="cell closedCell" id="cell_${j}_${i}" title="Shift+click отметить бомбу" onclick="openCell(${j},${i})"></td>
            </c:forEach>
        </tr>
    </c:forEach>
</table>
</body>
</html>
