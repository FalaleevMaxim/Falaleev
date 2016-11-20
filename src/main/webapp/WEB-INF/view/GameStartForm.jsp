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
<img src="${pageContext.servletContext.contextPath}/resources/images/logo.PNG" style="height:155px; border-radius:10px; float: left;">
<div style="display: inline-block; float: left; width: 400px;">
    <div id="choose">ВЫБЕРИТЕ УРОВЕНЬ СЛОЖНОСТИ</div>
    <div style="display: inline-block; float: left; width: 100%;">
        <div class="levelVGroup" style="float: left">
            <div class="level" id="easy">ПРОСТОЙ</div><br>
            <div class="level" id="normal">СРЕДНИЙ</div>
        </div>
        <div class="levelVGroup">
            <div class="level" id="hard">СЛОЖНЫЙ</div><br>
            <div class="level" id="custom">ОСОБЫЙ</div>
        </div><br>
        <input type="submit" form="frm" class="level" id="submit" value="НАЧАТЬ ИГРУ">
    </div>
</div>
<form id="frm" action="/UnauthGame/GameStart" method="post">
    Высота:<input type="text" value="10" disabled name="height" id="tr1" size="3" style="margin-left:6px;"> <br>
    Ширина:<input type="text" value="10" disabled name="width" id="td1" size="3" > <br>
    Бомб:<input type="text" value="10" disabled name="bombcount" id="bomb" size="3" style="margin-left:21px;"><br>
    Счёт:<input type="text" value="10" disabled name="score" id="score" size="3" style="margin-left:24px;"><br>
</form>
</body>
</html>