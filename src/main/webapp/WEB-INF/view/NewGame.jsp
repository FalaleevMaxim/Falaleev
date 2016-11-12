<%@ page import="ru.test.ViewModel.GameProperties" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Game</title>
    <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/resources/css/field.css">
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script src="${pageContext.servletContext.contextPath}/resources/js/opencell.js"></script>
</head>
<body>
<table id="field">
    <c:forEach var="i" begin="0" end="${properties.height-1}">
        <tr>
            <c:forEach var="j" begin="0" end="${properties.width-1}">
                <td class="cell closedCell" id="cell_${j}_${i}" title="Shift+click отметить бомбу" onclick="opencell(${j},${i})"></td>
            </c:forEach>
        </tr>
    </c:forEach>
</table>
</body>
</html>
