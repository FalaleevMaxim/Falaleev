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
    <% for (int i = 0; i<((GameProperties)request.getAttribute("properties")).getHeight(); i++){ %>
    <tr>
        <% for (int j=0;j<((GameProperties)request.getAttribute("properties")).getWidth();j++){ %>
        <td class="cell closedCell" id="cell_<%=j%>_<%=i%>" title="Shift+click отметить бомбу" onclick="opencell(<%=j%>,<%=i%>)"></td>
        <% } %>
    </tr>
    <% } %>
</table>
</body>
</html>
