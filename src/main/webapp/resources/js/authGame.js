var gameEvents = new EventSource("/AuthGame/GameListener");
gameEvents.onopen = function () {
    console.log("connected to game");
};
gameEvents.addEventListener("");