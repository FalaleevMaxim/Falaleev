var invitationEvent = new EventSource("/AuthGame/InvitationListener");

invitationEvent.onopen = function () {
    console.log("Connected to invitation listener");
};

invitationEvent.addEventListener("invited",onInvited,false);
invitationEvent.addEventListener("confirmed",onConfirmed,false);

function onInvited(e) {
    var div = document.createElement("div");
    div.class = "invitation";
    div.style.position = "fixed";
    div.style.bottom =0;
    div.style.right =0;
    div.innerHTML = "<a href='/AuthGame/Game/"+e.data+"'>You are invited to game</a><br>";
    document.getElementsByTagName("body")[0].appendChild(div);
    setTimeout(function () {
        document.getElementsByTagName("body")[0].removeChild(div);
    },10000);

    var gameId = $("#gameId").val();
    if(gameId==e.data){
        var player_tr = $("<tr class='tr_player' id='tr_player_"+$("#userId").val()+"'></tr>");
        var new_player_name = $("<td class='td_player_name'>"+$("#userName").val()+"</td>");
        var new_player_status = $("<td class='td_player_status td_status_invited'>Invited</td>");
        player_tr.append(new_player_name);
        player_tr.append(new_player_status);
        $("#players").append(player_tr);
    }
}

function onConfirmed(e) {
    var div = document.createElement("div");
    div.class = "invitation";
    div.style.position = "fixed";
    div.style.bottom =0;
    div.style.right =0;
    div.innerHTML = "<a href='/AuthGame/Game/"+e.data+"'>Your participation in game confirmed</a><br>";
    document.getElementsByTagName("body")[0].appendChild(div);
    setTimeout(function () {
        document.getElementsByTagName("body")[0].removeChild(div);
    },10000);
    var status = $("#tr_player_"+$("#userId").val()+">.td_player_status");
    if(status.length){
        status.removeClass("td_status_request");
        status.addClass("td_status_ingame");
        status.text("In game");
    }
}