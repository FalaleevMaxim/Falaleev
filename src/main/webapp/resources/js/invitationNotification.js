var invitationEvent = new EventSource("/AuthGame/InvitationListener");

invitationEvent.onopen = function () {
    console.log("Connected to invitation listener");
};

invitationEvent.addEventListener("invited",onInvited,false);
invitationEvent.addEventListener("confirmed",onConfirmed,false);

function onInvited(e) {
    var div = $("<a href='/AuthGame/Game/"+e.data+"'>You are invited to game</a>");
    showNotification(div);
    var status = $("#tr_player_"+$("#userId").val()+">.td_player_status");
    if(status.length){
        status.removeClass("td_status_request");
        status.addClass("td_status_ingame");
        status.text("In game");
    }

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
    var div = $("<a href='/AuthGame/Game/"+e.data+"'>Your participation in game confirmed</a>");
    showNotification(div);
    var status = $("#tr_player_"+$("#userId").val()+">.td_player_status");
    if(status.length){
        status.removeClass("td_status_request");
        status.addClass("td_status_ingame");
        status.text("In game");
    }
}

function showNotification(notificationBody) {
    var notifications = $("#notifications");
    var notification = $("<div class='notification'></div>")
    notification.append(notificationBody);
    notifications.prepend(notification);
    notifications.css("display","block");
    setTimeout(function () {
        notification.remove();
        if(notifications.children().length==0){
            notifications.css("display","none");
        }
    },10000);
}