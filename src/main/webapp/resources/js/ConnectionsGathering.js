connection.onopen = function () {
    console.log("Connection opened");
    $.get("/AuthGame/ConnectedPlayers",function (data) {
        var scoreTable = $("#scoreTable");
        scoreTable.empty();
        scores = {};
        for(var i=0;i<data.length;i++){
            scores[data[i].id] = {
                player:data[i].id,
                score:score,
                inGame:data[i].connected
            };
            var tr = $("<tr id='score_tr_"+data[i].id+"' class='score "+(data[i].connected?"score-ok":"score-danger")+"'> <td>"+data[i].name+"</td><td>"+(data[i].connected?score:0)+"</td></tr>");
            tr.appendTo(scoreTable)
        }
    });
};

openCell = function () {};

//Здесь сигнал обновления счёта означает присоединение игрока.
connection.addEventListener("Score",playerConnected);

//Здесь сигнал started обозначает завершение этапа подключения и переход к игре.
connection.addEventListener("Started",started);

function playerConnected(e) {
    var data = JSON.parse(e.data);
    scores[data.player].inGame = true;
    var score_tr = $("#score_tr_"+data.player);
    score_tr.removeClass("score-danger");
    score_tr.addClass("score-ok");
    score_tr.children()[1].innerText = score;
}

function started() {
    connection.removeEventListener("Started",started);
    connection.removeEventListener("Score",playerConnected);
    connection.addEventListener("Started",onStart);
    connection.addEventListener("Score",onScoreChange);
    openCell = openCellRequest;
    getScores();
    var gameNotifications = $("#gameNotifications");
    gameNotifications.empty();
    gameNotifications.append($("<h1>Game started</h1>"));
    gameNotifications.css("color","red");
}