var launchTimer;
var timerId = 0;
var thisPlayer;
var openMode = true;
var scores = {};
var bombsleft;
var winner = null;

function timer() {
    timerId = setInterval(function () {
        for(var key in scores){
            if(scores[key].inGame){
                if(scores[key].score>0){
                    scores[key].score--;
                }else{
                    getScore(key);
                }
            }
        }
        setScore();
    } ,1000);
}

$("#gameNotifications").onclick = function () {
    $("#gameNotifications").empty();
};

$(document).keydown(function(event)
{
    if(event.which==16) openMode=false;
});

$(document).keyup(function(event)
{
    if(event.which==16) openMode=true;
});

var connection = new EventSource("/AuthGame/GameListener");
connection.onopen = function () {
    console.log("Connection opened");
    getScores();
};

connection.addEventListener("Started",onStart);
connection.addEventListener("CellsOpened",onCellOpened);
connection.addEventListener("SuggestedBomb",onBombSuggested);
connection.addEventListener("Score",onScoreChange);
connection.addEventListener("Win",onWin);
connection.addEventListener("Loose",onLoose);
connection.addEventListener("GameOver",onGameOver);

function onStart() {
    $("#gameNotifications").empty();
}



function onScoreChange(e) {
    var data = JSON.parse(e.data);
    scores[data.player].score = data.score;
    setScore();
}

var openCell = openCellRequest;

function openCellRequest(x, y) {
    if (openMode) {
        $.post("/AuthGame/OpenCell",
            {
                x: x,
                y: y,
                value: null
            });
    } else {
        $.post("/AuthGame/SuggestBomb",
            {
                x: x,
                y: y,
                value: null
            });
    }
}

function onCellOpened(e) {
    var data = JSON.parse(e.data);
    if(timerId==0) timer();
    data.opened.forEach(function (item) {
        var cell = $('#cell_'+item.x+'_'+item.y);
        cell.empty();
        if(item.value==-1){
            var closedCells = $(".closedCell");
            closedCells.removeAttr("onClick");
            closedCells.removeAttr("title");
            cell.removeClass("closedCell");
            cell.addClass("bomb");
            cell.css("background-image","url(/resources/images/bombs/bomb"+Math.round(Math.random()*(14))+".jpg)");
        }else{
            cell.removeClass("closedCell");
            cell.addClass("emptyCell");
            cell.removeAttr("onClick");
            cell.removeAttr("title");
            if(item.value>0){
                cell.text(item.value);
                cell.addClass("n"+item.value)
            }

        }
    });
}

function onBombSuggested(e) {
    var data = JSON.parse(e.data);
    var x = data.x;
    var y = data.y;
    var cell = $('#cell_'+x+'_'+y);
    if(data.bomb){
        cell.html("<img src='/resources/images/bombs/flag.png'>");
        cell.removeAttr("onClick");
        cell.removeAttr("title");
        bombsleft--;
        $("#bombsLeft").text(bombsleft);
    }else{
        cell.html("<img src='/resources/images/bombs/cross.png'>");
    }
}

function getScore(player) {
    $.get("/AuthGame/Score/"+player,
        function (newScore) {
            scores[player].score = newScore;
        });
}

function getScores() {
    $.get("/AuthGame/GetScores/",
        function (newScores) {
            for(var i=0;i<newScores.length;i++){
                if(!scores.hasOwnProperty(newScores[i].player)){
                    scores[newScores[i].player] = {
                        player:newScores[i].player,
                        score:newScores[i].score,
                        inGame:newScores[i].inGame
                    };
                }else{
                    scores[newScores[i].player].score = newScores[i].score;
                    scores[newScores[i].player].inGame = newScores[i].inGame;
                }
            }
            setScore();
        });
}

function setScore() {
    for(var key in scores){
        var score_tr = $('#score_tr_'+key);
        if(winner==key){
            if(score_tr.hasClass("score-winner")) continue;
            score_tr.removeClass("score-out");
            score_tr.removeClass("score-ok");
            score_tr.removeClass("score-danger");
            score_tr.removeClass("score-normal");
            score_tr.addClass("score-winner");
        }
        if(scores[key].inGame){
            var score = scores[key].score;
            score_tr.children()[1].innerHTML = score;
            score_tr.removeClass("score-out");
            score_tr.removeClass("score-ok");
            score_tr.removeClass("score-danger");
            score_tr.removeClass("score-normal");
            score_tr.addClass(score<10?"score-danger":score<20?"score-normal":"score-ok");
        }else{
            if(score_tr.hasClass("score-out")) continue;
            score_tr.removeClass("score-ok");
            score_tr.removeClass("score-danger");
            score_tr.removeClass("score-normal");
            score_tr.addClass("score-out");
        }
    }
}

function onWin(e)
{
    var data = JSON.parse(e.data);
    clearInterval(timerId);
    winner = data;
    var score_tr = $('#score_tr_'+data);
    score_tr.removeClass("score-out");
    score_tr.removeClass("score-ok");
    score_tr.removeClass("score-danger");
    score_tr.removeClass("score-normal");
    score_tr.addClass("score-winner");
    if(thisPlayer==data){
        var gameNotifications = $("#gameNotifications");
        gameNotifications.empty();
        gameNotifications.append($("<h1>You won!</h1>"));
        gameNotifications.css("color","purple");
    }
}

function onLoose(e) {
    var data = JSON.parse(e.data);
    scores[data].inGame = false;
    var score_tr = $('#score_tr_'+data);
    score_tr.removeClass("score-ok");
    score_tr.removeClass("score-danger");
    score_tr.removeClass("score-normal");
    score_tr.addClass("score-out");
    if(thisPlayer == data){
        disableFieldClick();
        var gameNotifications = $("#gameNotifications");
        gameNotifications.empty();
        gameNotifications.append($("<h1>You lost!</h1>"));
        gameNotifications.css("color","red");
    }
}

function onGameOver() {
    clearInterval(timerId);
    getScores();
    disableFieldClick();
}

function disableFieldClick() {
    var cells = $(".cell");
    cells.removeAttr("onclick");
    cells.removeAttr("title");
}