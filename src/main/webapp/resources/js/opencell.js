var shift = false;
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
        $.post("/UnauthGame/OpenCell",
            {
                x:x,
                y:y,
                value:null
            },
            onOpenSuccess);
    }else{
        $.post("/UnauthGame/SuggestBomb",
            {
                x:x,
                y:y,
                value:null
            },
            function (data) {
                var cell = $('#cell_'+x+'_'+y);
                if(data){
                    cell.append("<img src='/resources/images/bombs/flag.png'>");
                    cell.removeAttr("onClick");
                    cell.removeAttr("title");
                }else{
                    cell.append("<img src='/resources/images/bombs/cross.png'>");
                }
            }
        );
    }
}

function onOpenSuccess(data){
    data.forEach(function (item, i, arr) {
        var cell = $('#cell_'+item.x+'_'+item.y)
        cell.empty();
        if(item.value==-1){
            cell.removeClass("closedCell");
            cell.addClass("bomb");
            cell.css("background-image","url(/resources/images/bombs/bomb"+Math.round(Math.random()*(14))+".jpg)");
        }else{
            cell.removeClass("closedCell");
            cell.addClass("emptyCell");
            if(item.value>0){
                cell.text(item.value);
                cell.addClass("n"+item.value)
            }
        }
    })
}

