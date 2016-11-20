<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Game</title>
</head>
<body>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Game</title>
    <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/resources/css/field.css">
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script src="${pageContext.servletContext.contextPath}/resources/js/opencell.js"></script>
    <script>
        id = ${id};
        bombsleft = ${properties.bombcount};
        score = ${properties.score};
        setScore();
        getScore();
        ${isFinished?"":"timer();"}
    </script>
</head>
<body>
<div id="scorediv">
    score:
</div>
<table id="field">
    <c:forEach var="i" begin="0" end="${properties.height-1}">
        <tr>
            <c:forEach var="j" begin="0" end="${properties.width-1}">
                <c:if test="${!field[i][j].opened}">
                    <c:if test="${!field[i][j].bombSuggested}">
                        <td class="cell closedCell" id="cell_${j}_${i}" title="Shift+click отметить бомбу" onclick="opencell(${j},${i})"></td>
                    </c:if>
                    <c:if test="${field[i][j].bombSuggested}">
                        <c:if test="${field[i][j].bomb}">
                            <td class="cell closedCell" id="cell_${j}_${i}">
                                <img src="${pageContext.servletContext.contextPath}/resources/images/bombs/flag.png">
                            </td>
                        </c:if>
                        <c:if test="${!field[i][j].bomb}">
                            <td class="cell closedCell" id="cell_${j}_${i}" title="Shift+click отметить бомбу" onclick="opencell(${j},${i})">
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
</table>
</body>
</html>
</body>
</html>
