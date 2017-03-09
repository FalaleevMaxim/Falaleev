package ru.test.logic.AuthGame;

import ru.test.ViewModel.CellVM;
import ru.test.ViewModel.GameProperties;
import ru.test.ViewModel.ScoreModel;
import ru.test.logic.AuthGame.Exceptions.PlayerHasLostException;
import ru.test.logic.AuthGame.Exceptions.PlayerNotParticipatingException;
import ru.test.logic.Board;
import ru.test.logic.BoardImpl;
import ru.test.logic.AuthGame.Listeners.GameEventsListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiplayerGame<P> implements AuthGame<P> {
    /**
     * @param properties свойства игры
     * @param players коллекция игроков
     */
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

    @Override
    public GameProperties getProperties() {
        return new GameProperties(board.getFieldWidth(),board.getFieldHeight(),board.getBombCount(),startScore);
    }

    @Override
    public Board.Cell[][] getField() {
        if(!isStarted()) return null;
        Board.Cell[][] field =  board.getField();
        Board.Cell[][] fieldCopy = new Board.Cell[field.length][];
        for(int i=0;i<field.length;i++){
            fieldCopy[i] = new Board.Cell[field[i].length];
            for(int j=0;j<field[i].length;j++){
                fieldCopy[i][j] = new Board.Cell(field[i][j]);
                if(!isFinished() && !fieldCopy[i][j].isOpened()){
                    if(fieldCopy[i][j].isBombSuggested() && fieldCopy[i][j].isBomb()){
                        fieldCopy[i][j].setValue(Board.Cell.BOMB);
                    }else{
                        fieldCopy[i][j].setValue(0);
                    }
                }
            }
        }
        return fieldCopy;
    }

    @Override
    public int getBombsLeft() {
        return board.getBombsLeft();
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

    /**
     * Рассылает всем игрокам событие, заданное функциональным игтерфейсом.
     * @param event объект функционального интерфейса, описывающий отправку события обработчику
     */
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
    public Collection<P> getWinners() {
        if(!isFinished()) return new ArrayList<>();
        Collection<P> winners = new ArrayList<>();
        for (PlayerProperties pp:playersInfo.values()) {
            if(pp.isWinner()) winners.add(pp.getPlayer());
        }
        return winners;
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
            if(!isStarted()){
                scores.add(new ScoreModel<>(pp.getPlayer(), startScore, !pp.isOut()));
                continue;
            }
            playerOutOfTime(pp.getPlayer());
            if(pp.isOut()){
                scores.add(new ScoreModel<>(pp.getPlayer(), pp.getScore()-(int)((pp.getOutTime()-getStartTime())/1000), !pp.isOut()));
            } else{
                scores.add(new ScoreModel<>(pp.getPlayer(), pp.getScore()-scoreCorrection, !pp.isOut()));
            }
        }
        return scores;
    }

    @Override
    public int getScore(P player) {
        if(!playersInfo.containsKey(player)) throw new PlayerNotParticipatingException();
        PlayerProperties playerProperties = playersInfo.get(player);
        if(!started) return startScore;
        if(playerProperties.isOut()){
            return playerProperties.getScore() - (int)((playerProperties.getOutTime()-getStartTime())/1000);
        }else{
            int score = playerProperties.getScore() - (int)((System.currentTimeMillis()-getStartTime())/1000);
            if(playerOutOfTime(player)){
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
        playerOutOfTime(player);
        if(playersInfo.get(player).isLooser()) throw new PlayerHasLostException();
    }

    @Override
    public boolean suggestBomb(int x, int y, P player) {
        checkPlayer(player);
        boolean bomb = board.suggestBomb(x, y);
        int scoreChange = bomb?5:-15;
        emitEvent(listener->{
            listener.onBombSuggested(player,x,y,bomb,scoreChange);
            listener.onScoreChange(player,getScore(player));
        });
        playersInfo.get(player).addScore(scoreChange);

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
        if(opened.length!=0 && opened[0].getValue().equals(Board.Cell.BOMB)){
            playerBlownUp(player);
        }else{
            int scoreChange = 0;
            if(!started) start();
            else{
                scoreChange=3;
            }
            final int scoreFinal = scoreChange;
            emitEvent(listener->{
                listener.onCellsOpened(player,opened,scoreFinal);
                listener.onScoreChange(player,getScore(player));
            });
            playersInfo.get(player).addScore(scoreChange);
            scoreChanged(player);
        }
        return opened;
    }

    /**
     * Проверяет, вышло ли время у данного игрока. Если время вышло, устанавливает игроку время выбывания.
     * @param player идентификатор игрока
     * @return если игрок уже выбыл раньше или у игрока не вышло время, возвращает false, иначе true.
     */
    private boolean playerOutOfTime(P player){
        PlayerProperties properties = playersInfo.get(player);
        if(properties.isOut() || !isStarted() || properties.getScore()>(int)((System.currentTimeMillis()-getStartTime())/1000)) return false;
        properties.setOutTime(getStartTime() + (long) properties.getScore() *1000L);
        playersRemaining();
        return true;
    }

    /**
     * Устанавливает игроку время выбывания, когда игрок открыл бомбу
     * @param player идентификатор игрока
     */
    private void playerBlownUp(P player){
        playersInfo.get(player).setOutTime(System.currentTimeMillis());
        playersRemaining();
    }

    /**
     * Проверяет, осталось ли в игре хотя бы два игрока.
     * Если остался один игрок, завершает игру и объявляет его победителем
     * @return true если осталось хотя бы два игрока и false если остался один игрок
     */
    private boolean playersRemaining(){
        P remaining = null;
        for (PlayerProperties playerProperties:playersInfo.values()) {
            if(!playerProperties.isOut()){
                if(remaining==null) remaining = playerProperties.getPlayer();
                else
                    return true;
            }
        }
        if(remaining==null) return false;
        finishTime = System.currentTimeMillis();
        PlayerProperties playerProperties = playersInfo.get(remaining);
        playerProperties.setOutTime(finishTime);
        playerProperties.setWinner();
        return false;
    }

    private void scoreChanged(P player){
        emitEvent(listener -> listener.onScoreChange(player,getScore(player)));
    }

    /**
     * Класс для хранения свойств игрока
     */
    private class PlayerProperties{
        PlayerProperties(P player) {
            this.player = player;
            this.score = MultiplayerGame.this.startScore;
        }
        //Идентификатор игрока
        private P player;
        //Объект, через который игроку будут отправляться оповещения о событиях игры
        private GameEventsListener<P> listener;
        //Очки
        private int score;
        //Время выбывания из игры
        private long outTime=0;
        //Результат игры: отрицательный - проигравший, положительный - победитель, 0 - игра не закончена
        private int result=0;

        /**
         * @return возвращает идентификатор игрока
         */
        P getPlayer() {return player;}

        /**
         * @return Возвращает обработчик событий игрока
         */
        GameEventsListener<P> getListener() {return listener;}

        /**
         * Устанавливает обработчик событий игрока
         * @param listener обработчик событий
         */
        void setListener(GameEventsListener<P> listener) {this.listener = listener;}

        /**
         * @return Возвращает счёт игрока (без учёта времени начала игры или выбывания игрока)
         */
        public int getScore() {return score;}

        /**
         * Устанавливает счёт игрока; через внешний класс проверяет оставшееся у игрока время и рассылает событие изменения счёта
         * @param score новый счёт
         */
        public void setScore(int score) {
            this.score = score;
            MultiplayerGame.this.playerOutOfTime(player);
            MultiplayerGame.this.emitEvent(listener1 -> listener1.onScoreChange(player,score));
        }

        /**
         * Прибавляет указанное число к счёту. Добавленная для удобства надстройка над setScore
         * @param count количество очков для добавления
         */
        void addScore(int count){setScore(getScore()+count);}

        /**
         * Возвращает время выбывания игрока
         * @return время выбывания игрока в миллисекундах от 1 января 1970
         */
        long getOutTime() {return outTime;}

        /**
         * Устанавливает время выбывания игрока. Если это время не совпадает со временем завершения игры, устанавливает игрока проигравшим.
         * @param outTime время выбывания игрока в миллисекундах от 1 января 1970
         */
        void setOutTime(long outTime) {
            this.outTime = outTime;
            if(MultiplayerGame.this.getFinishTime()!=outTime) setLooser();
        }

        /**
         * Проверяет, выбыл ли игрок (установлено ли время выбывания)
         */
        boolean isOut(){return outTime>0;}

        /**
         * Проверяет, является ли игрок проигравшим
         */
        boolean isLooser(){return result<0;}

        /**
         * Устанавливает игрока проигравшим и рассылает соответствующее событие через внешний класс
         */
        void setLooser(){
            result = -1;
            MultiplayerGame.this.emitEvent(listener1 -> listener1.onPlayerLoose(player));
        }

        /**
         * Устанавливает игрока победителем и рассылает соответствующее событие через внешний класс
         */
        void setWinner(){
            result =  1;
            MultiplayerGame.this.emitEvent(listener1 -> listener1.onPlayerWin(player));
        }

        /**
         * Проверяет, является ли игрок победителем
         */
        boolean isWinner(){return result>0;}
    }
}

/**
 * Функциональный интерфейс, описывающий отправку события обработчику
 * @param <P> Тип идентификатора игрока
 */
interface GameEvent<P>{
    void emitEvent(GameEventsListener<P> listener);
}