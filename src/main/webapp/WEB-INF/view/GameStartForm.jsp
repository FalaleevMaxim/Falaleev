<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
    <title>Start game</title>
    <link href="${pageContext.servletContext.contextPath}/resources/css/form.css" rel="stylesheet">
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script src="${pageContext.servletContext.contextPath}/resources/js/gameform.js"></script>

</head>
<body>
<div id="choose">ВЫБЕРИТЕ УРОВЕНЬ СЛОЖНОСТИ</div><br>
<div class="level" id="easy">ПРОСТОЙ</div><br>
<div class="level" id="normal">СРЕДНИЙ</div><br>
<div class="level" id="hard">СЛОЖНЫЙ</div><br>
<div class="level" id="custom">ОСОБЫЙ</div>
<input type="submit" form="frm" id="submit" value="НАЧАТЬ ИГРУ">
<img src="${pageContext.servletContext.contextPath}/resources/images/logo.PNG" style="position:absolute; height:162; border-radius:10px; left:400; top:8;">
<form id="frm" action="/UnauthGame/GameStart" method="post" style="background-color:lightblue; border-radius:20px; width:150px; padding:10px">
    Высота:<input type="text" value="10" disabled name="height" id="tr1" size="3" style="margin-left:6px;"> <br>
    Ширина:<input type="text" value="10" disabled name="width" id="td1" size="3" > <br>
    Бомб:<input type="text" value="10" disabled name="bombcount" id="bomb" size="3" style="margin-left:21px;"><br>
</form>
</body>
</html>
