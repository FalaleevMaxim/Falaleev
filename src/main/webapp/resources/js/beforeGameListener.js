var beforeGameListener = new EventSource("/AuthGame/BeforeGameListener");
beforeGameListener.onopen = function () {
    console.log("Listening for game start");
};
beforeGameListener.addEventListener("started",function () {
    var notification = $("<a href='/AuthGame/Game'>Your game has started!</a>");
    showNotification(notification);
});