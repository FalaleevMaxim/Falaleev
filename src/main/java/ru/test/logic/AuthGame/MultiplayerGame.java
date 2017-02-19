package ru.test.logic.AuthGame;

import ru.test.ViewModel.CellVM;
import ru.test.ViewModel.GameProperties;
import ru.test.logic.AuthGame.Exceptions.PlayerHasLostException;
import ru.test.logic.AuthGame.Exceptions.PlayerNotParticipatingException;
import ru.test.logic.Board;
import ru.test.logic.BoardImpl;
import ru.test.model.GameEventsListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiplayerGame<P> implements AuthGame<P> {
    public MultiplayerGame(GameProperties properties,Collection<P> players){
        board = new BoardImpl(properties.getWidth(),properties.getHeight(),properties.getBombcount());
        this.startScore = properties.getScore();
        playersInfo = new ConcurrentHashMap<>();
        for (P player:players) {
            playersInfo.put(player,new PlayerProperties(player));
        }
    }

    private int startScore;
    private boolean started = false;
    private Map<P,PlayerProperties> playersInfo;
    private Board board;
    private long finishTime,startTime;

    public Board getBoard() {
        return board;
    }

    @Override
    public GameProperties getProperties() {
        return new GameProperties(board.getFieldWidth(),board.getFieldHeight(),board.getBombCount(),startScore);
    }

    @Override
    public Board.Cell[][] getField() {
        Board.Cell[][] field =  board.getField();
        Board.Cell[][] fieldCopy = new Board.Cell[field.length][];
        for(int i=0;i<field.length;i++){
            fieldCopy[i] = new Board.Cell[field[i].length];
            for(int j=0;j<field[i].length;j++){
                fieldCopy[i][j] = new Board.Cell(field[i][j]);
                if(!isFinished() && !fieldCopy[i][j].isOpened()){
                    fieldCopy[i][j].setValue(null);
                }
            }
        }
        return fieldCopy;
    }

    @Override
    public void setListener(P player,GameEventsListener<P> listener){
        if(isFinished()) throw new IllegalStateException("Game Already finished");
        PlayerProperties pp = playersInfo.get(player);
        if(pp==null) throw new IllegalStateException("You do not participate this game");
        pp.setListener(listener);
    }

    @Override
    public GameEventsListener<P> getListener(P player) {
        return playersInfo.get(player).getListener();
    }

    private void emitEvent(GameEvent<P> event){
        for (PlayerProperties playerProperties:playersInfo.values()) {
            event.emitEvent(playerProperties.getListener());
        }
    }

    @Override
    public void quit(P player){
        PlayerProperties pp = playersInfo.get(player);
        pp.setOutTime(System.currentTimeMillis());
        emitEvent(listener->{
            listener.onPlayerQuit(player);
            listener.onScoreChange(player,getScore(player));
        });
    }

    @Override
    public boolean hasPlayer(P player) {
        return playersInfo.containsKey(player);
    }

    @Override
    public Collection<P> getPlayers() {
        return playersInfo.keySet();
    }

    @Override
    public boolean isStarted() {
        return startTime>0;
    }

    @Override
    public boolean isWinner(P player) {
        return playersInfo.get(player).isWinner();
    }

    @Override
    public boolean isLooser(P player) {
        return playersInfo.get(player).isLooser();
    }

    @Override
    public long getOutTime(P player) {
        return playersInfo.get(player).getOutTime();
    }

    @Override
    public boolean isFinished() {
        return finishTime>0;
    }

    @Override
    public long getFinishTime(){return finishTime;}

    @Override
    public Collection<ScoreModel<P>> getScores() {
        Collection<ScoreModel<P>> scores = new ArrayList<>();
        int scoreCorrection = (int)((System.currentTimeMillis()-getStartTime())/1000);
        for (PlayerProperties pp:playersInfo.values()) {
            if(!pp.isOut() && (pp.getScore()-scoreCorrection)>0){
                playerOutOfTime(pp.getPlayer());
            }
            if(pp.isOut()){
                scores.add(new ScoreModel<>(pp.getPlayer(), pp.getScore()-(int)(pp.getOutTime()/1000), pp.isOut()));
            } else{
                scores.add(new ScoreModel<>(pp.getPlayer(), pp.getScore()-scoreCorrection, pp.isOut()));
            }
        }
        return scores;
    }

    @Override
    public int getScore(P player) {
        if(!playersInfo.containsKey(player)) throw new PlayerNotParticipatingException();
        PlayerProperties playerProperties = playersInfo.get(player);
        if(playerProperties.isOut()){
            return playerProperties.getScore() - (int)((playerProperties.getOutTime()-getStartTime())/1000);
        }else{
            int score = playerProperties.getScore() - (int)((System.currentTimeMillis()-getStartTime())/1000);
            if(score<=0){
                playerOutOfTime(player);
                return 0;
            }
            return score;
        }
    }

    private void start(){
        if(started) return;
        started=true;
        for (PlayerProperties playerProperties:playersInfo.values()) {
            playerProperties.getListener().onGameStarted();
        }
        startTime = System.currentTimeMillis();
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    private void checkPlayer(P player){
        if(isFinished()) throw new IllegalStateException("Game is over");
        if(!playersInfo.containsKey(player)) throw new PlayerNotParticipatingException();
        if(playersInfo.get(player).getScore() - (int)(System.currentTimeMillis()/1000) <=0){
            playerOutOfTime(player);
        }
        if(playersInfo.get(player).isLooser()) throw new PlayerHasLostException();
    }

    @Override
    public boolean suggestBomb(int x, int y, P player) {
        checkPlayer(player);
        boolean bomb = board.suggestBomb(x, y);
        if(bomb){
            playersInfo.get(player).addScore(5);
        }else{
            playersInfo.get(player).addScore(-15);
        }
        emitEvent(listener->{
            listener.onBombSuggested(player,x,y,bomb);
            listener.onScoreChange(player,getScore(player));
        });
        if(board.getBombsLeft()==0) noBombsLeft();
        return bomb;
    }

    private void noBombsLeft(){
        if(board.getBombsLeft()!=0) return;
        if(isFinished()) return;
        finishTime = System.currentTimeMillis();
        emitEvent(GameEventsListener::onGameOver);
        int maxScore = Integer.MIN_VALUE;
        for (PlayerProperties playerProperties:playersInfo.values()) {
            playerOutOfTime(playerProperties.getPlayer());
            if(!playerProperties.isOut()){
                playerProperties.setOutTime(finishTime);
                int score = getScore(playerProperties.getPlayer());
                if(score >maxScore){
                    maxScore = score;
                }
            }
        }
        for (PlayerProperties playerProperties:playersInfo.values()) {
            if(!playerProperties.isLooser()){
                if(getScore(playerProperties.getPlayer())==maxScore) playerProperties.setWinner();
                else playerProperties.setLooser();
            }
        }
    }

    @Override
    public CellVM[] openCell(int x, int y, P player) {
        checkPlayer(player);
        CellVM[] opened = board.openCell(x,y);
        if(opened.length==0 && opened[0].getValue().equals(Board.Cell.BOMB)){
            playerBlownUp(player);
        }else{
            emitEvent(listener->{
                listener.onCellsOpened(player,opened);
                listener.onScoreChange(player,getScore(player));
            });
            if(!started) start();
            else{
                playersInfo.get(player).addScore(3);
                scoreChanged(player);
            }
        }
        return opened;
    }

    private boolean playerOutOfTime(P player){
        PlayerProperties properties = playersInfo.get(player);
        if(properties.isOut() || properties.getScore() >(System.currentTimeMillis()-getStartTime())) return false;
        properties.setOutTime(getStartTime() + (long) properties.getScore() *1000L);
        playersRemaining();
        return true;
    }

    private void playerBlownUp(P player){
        playersInfo.get(player).setOutTime(System.currentTimeMillis());
        playersRemaining();
    }

    private boolean playersRemaining(){
        P remaining = null;
        for (PlayerProperties playerProperties:playersInfo.values()) {
            if(!playerProperties.isOut()){
                if(remaining==null) remaining = playerProperties.getPlayer();
                else return true;
            }
        }
        finishTime = System.currentTimeMillis();
        PlayerProperties playerProperties = playersInfo.get(remaining);
        playerProperties.setOutTime(finishTime);
        playerProperties.setWinner();
        return false;
    }

    private void scoreChanged(P player){
        for (PlayerProperties playerProperties:playersInfo.values()) {
            playerProperties.getListener().onScoreChange(player,getScore(player));
        }
    }

    private class PlayerProperties{
        PlayerProperties(P player) {
            this.setPlayer(player);
            this.setScore(MultiplayerGame.this.startScore);
        }
        //Идентификатор игрока
        private P player;
        //Объект, через который игроку будут отправляться оповещения о событиях игры
        private GameEventsListener<P> listener;
        //Очки
        private int score;
        //Время выбывания из игры
        private long outTime;
        //Результат игры: отрицательный - проигравший, положительный - победитель, 0 - игра не закончена
        private int result=0;

        public P getPlayer() {return player;}
        public void setPlayer(P player) {this.player = player;}
        public GameEventsListener<P> getListener() {return listener;}
        public void setListener(GameEventsListener<P> listener) {this.listener = listener;}
        public int getScore() {return score;}
        public void setScore(int score) {
            this.score = score;
            MultiplayerGame.this.emitEvent(listener1 -> listener1.onScoreChange(player,score));
            MultiplayerGame.this.playerOutOfTime(player);
        }
        public void addScore(int count){setScore(getScore()+count);}
        public long getOutTime() {return outTime;}
        public void setOutTime(long outTime) {this.outTime = outTime; if(MultiplayerGame.this.getFinishTime()!=outTime) setLooser();}
        public boolean isOut(){return outTime>0;}
        public boolean isLooser(){return result<0;}
        public void setLooser(){result = -1; MultiplayerGame.this.emitEvent(listener1 -> listener1.onPlayerLoose(player));}
        public void setWinner(){result =  1; MultiplayerGame.this.emitEvent(listener1 -> listener1.onPlayerWin(player));}
        public boolean isWinner(){return result>0;}
    }
}

interface GameEvent<P>{
    void emitEvent(GameEventsListener<P> listener);
}