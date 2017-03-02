var joinEvent = new EventSource("/AuthGame/JoinListener");
joinEvent.addEventListener("request",function (e) {
    var data = JSON.parse(e.data);
    var new_player_tr = $("<tr class='tr_player' id='tr_player_"+data.id+"'></tr>");
    var new_player_name = $("<td class='td_player_name'>"+data.name+"</td>");
    var new_player_status = $("<td class='td_player_status td_status_request'>Requests join</td>");
    var new_player_action = $("<td class='td_player_action td_action_in'>Take in</td>");
    new_player_action.attr("onclick","invite('"+data.name+"');");
    new_player_tr.append(new_player_name);
    new_player_tr.append(new_player_status);
    new_player_tr.append(new_player_action);

    $("#players").append(new_player_tr);
});

joinEvent.addEventListener("accepted",function (e) {
    var status = $("#tr_player_"+e.data+">.td_player_status");
    if(status.hasClass("td_status_owner")) return;
    status.removeClass("td_status_invited");
    status.addClass("td_status_ingame");
    status.text("In game");
    var action = $("#tr_player_"+e.data+">.td_player_action");
    action.removeClass("td_action_in");
    action.addClass("td_action_uninvite");
    action.text("Uninvite");
});

function invite(player) {
    $.get("/AuthGame/invite/"+player,
        function (data) {
            var player_tr = $("#tr_player_"+data.id);
            if(!player_tr.length){
                player_tr = $("<tr class='tr_player' id='tr_player_"+data.id+"'></tr>");
                var new_player_name = $("<td class='td_player_name'>"+data.name+"</td>");
                var new_player_status = $("<td class='td_player_status'></td>");
                var new_player_action = $("<td class='td_player_action'></td>");

                player_tr.append(new_player_name);
                player_tr.append(new_player_status);
                player_tr.append(new_player_action);

                $("#players").append(player_tr);
            }
            var status = $("#tr_player_"+data.id+">.td_player_status");
            if(status.hasClass("td_status_owner")) return;
            status.removeClass("td_status_ingame");
            status.removeClass("td_status_invited");
            status.removeClass("td_status_request");
            var action = $("#tr_player_"+data.id+">.td_player_action");
            action.removeClass("td_action_uninvite");
            action.removeClass("td_action_in");
            action.removeAttr("onclick");
            if(data.ownerConfirmed){
                if(data.playerConfirmed){
                    status.addClass("td_status_ingame");
                    status.text("In game");
                }else{
                    status.addClass("td_status_invited");
                    status.text("Invited");
                }
                action.addClass("td_action_uninvite");
                action.text("Uninvite");
            }else{
                if(data.playerConfirmed){
                    status.addClass("td_status_request");
                    status.text("Join request");
                    action.addClass("td_action_in");
                    action.text("Take in");
                    action.attr("onclick","invite('"+data.name+"');");
                }else{
                    player_tr.remove();
                }
            }
        });
}