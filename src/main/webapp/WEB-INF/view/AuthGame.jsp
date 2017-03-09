<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Game</title>
    <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/resources/css/field.css">
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <c:if test="${!isFinished}">
        <script src="${pageContext.servletContext.contextPath}/resources/js/SseGame.js"></script>
    </c:if>
    <script>
        bombsleft = ${bombsLeft};
        score = ${properties.score};
        ${(isStarted && !isFinished)?"timer();":""}
        thisPlayer = ${userId}
    </script>
</head>
<body>
<jsp:include page="/main/header"/>
<div align="center" id="gameNotifications"></div>
<table id="scoreTable">
    <c:forEach var="i" begin="0" end="${scores.size()-1}">
        <tr id="score_tr_${players1.get(i).id}"
            class="score ${winner.contains(players1.get(i).id)?"score-winner":
                                                                    (scores.get(i).inGame?(
                                                                        scores.get(i).score>=20?
                                                                            "score-ok":
                                                                            scores.get(i).score>=10?
                                                                                "score-normal":
                                                                                "score-danger"):
                                                                        "score-out"
                                                                    )} ">
            <td>${players1.get(i).userName}</td>
            <td>${scores.get(i).score}</td>
        </tr>
    </c:forEach>
</table>
<p id="bombsLeft">${bombsLeft}</p><br>
<table id="field">
    <c:if test="${!isStarted}">
        <c:forEach var="i" begin="0" end="${properties.height-1}">
            <tr>
                <c:forEach var="j" begin="0" end="${properties.width-1}">
                    <td class="cell closedCell" id="cell_${j}_${i}" title="Shift+click отметить бомбу" onclick="openCell(${j},${i})"></td>
                </c:forEach>
            </tr>
        </c:forEach>
    </c:if>
    <c:if test="${isStarted}">
        <c:forEach var="i" begin="0" end="${properties.height-1}">
            <tr>
                <c:forEach var="j" begin="0" end="${properties.width-1}">
                    <c:if test="${!field[i][j].opened}">
                        <c:if test="${!field[i][j].bombSuggested}">
                            <td class="cell closedCell" id="cell_${j}_${i}" title="Shift+click отметить бомбу" onclick="openCell(${j},${i})"></td>
                        </c:if>
                        <c:if test="${field[i][j].bombSuggested}">
                            <c:if test="${field[i][j].bomb}">
                                <td class="cell closedCell" id="cell_${j}_${i}">
                                    <img src="${pageContext.servletContext.contextPath}/resources/images/bombs/flag.png">
                                </td>
                            </c:if>
                            <c:if test="${!field[i][j].bomb}">
                                <td class="cell closedCell" id="cell_${j}_${i}" title="Shift+click отметить бомбу" onclick="openCell(${j},${i})">
                                    <img src="${pageContext.servletContext.contextPath}/resources/images/bombs/cross.png">
                                </td>
                            </c:if>
                        </c:if>
                    </c:if>

                    <c:if test="${field[i][j].opened}">
                        <c:if test="${field[i][j].bomb}">
                            <td class="cell bomb" style="background-image: url('${pageContext.servletContext.contextPath}/resources/images/bombs/bomb<%=(int)(Math.random()*14)%>.jpg')"></td>
                        </c:if>
                        <c:if test="${field[i][j].value==0}">
                            <td class="cell emptyCell"></td>
                        </c:if>
                        <c:if test="${field[i][j].value>0}">
                            <td class="cell emptyCell n${field[i][j].value}">${field[i][j].value}</td>
                        </c:if>
                    </c:if>
                </c:forEach>
            </tr>
        </c:forEach>
    </c:if>
</table>
</body>
</html>