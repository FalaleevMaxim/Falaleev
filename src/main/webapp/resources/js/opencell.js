var id;
var shift = false;
var timerId=0;
var score;
var bombsleft;
$(document).keydown(function(event)
{
    if(event.which==16) shift=true;
});
$(document).keyup(function(event)
{
    if(event.which==16) shift=false;
});

function opencell(x, y) {
    if(!shift){
        $.post("/UnauthGame/"+id+"/OpenCell",
            {
                x:x,
                y:y,
                value:null
            },
            onOpenSuccess);
    }else{
        $.post("/UnauthGame/"+id+"/SuggestBomb",
            {
                x:x,
                y:y,
                value:null
            },
            function (data) {
                var cell = $('#cell_'+x+'_'+y);
                if(data){
                    cell.html("<img src='/resources/images/bombs/flag.png'>");
                    cell.removeAttr("onClick");
                    cell.removeAttr("title");
                    if(--bombsleft==0){
                        clearInterval(timerId);
                        var closedCells = $('.closedCell');
                        closedCells.removeAttr("onClick");
                        closedCells.removeAttr("title");
                    }
                }else{
                    cell.html("<img src='/resources/images/bombs/cross.png'>");
                }
                getScore();
            }
        );
    }
}
function onOpenSuccess(data){
    if(timerId==0) timer();
    data.forEach(function (item, i, arr) {
        var cell = $('#cell_'+item.x+'_'+item.y);
        cell.empty();
        if(item.value==-1){
            cell.removeClass("closedCell");
            cell.addClass("bomb");
            cell.css("background-image","url(/resources/images/bombs/bomb"+Math.round(Math.random()*(14))+".jpg)");
            clearInterval(timerId);
            var closedCells = $('.closedCell');
            closedCells.removeAttr("onClick");
            closedCells.removeAttr("title");
        }else{
            cell.removeClass("closedCell");
            cell.addClass("emptyCell");
            if(item.value>0){
                cell.text(item.value);
                cell.addClass("n"+item.value)
            }
        }
    });
    getScore();
}


function getScore() {
    $.get("/UnauthGame/"+id+"/Score",
        {},
        onScoreRequest
    );
}
function onScoreRequest(data) {
    score = data;
    setScore();
}

function setScore() {
    var scorediv = $('#scorediv');
    scorediv.text('score: ' + score);
    scorediv.removeClass("score-ok");
    scorediv.removeClass("score-danger");
    scorediv.removeClass("score-normal");
    scorediv.addClass(score<10?"score-danger":score<20?"score-normal":"score-ok");
}

function timer() {
    timerId = setInterval(function () {
        setScore();
        if(score>0) score--;
        else clearInterval(timerId);
    } ,1000);
}






