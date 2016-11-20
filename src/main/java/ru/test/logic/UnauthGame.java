package ru.test.logic;

import ru.test.ViewModel.CellVM;
import ru.test.ViewModel.GameProperties;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UnauthGame implements Game<Integer>{
    public UnauthGame(GameProperties properties){
        board = new BoardImpl(properties.getWidth(),properties.getHeight(),properties.getBombcount());
        this.startScore = properties.getScore();
        score = startScore;
    }

    private Board board;
    private boolean started = false;
    private int score;
    private byte result = 0;
    private long startTime;
    private long endTime;
    private int startScore;

    private boolean isLoose(){
        if(result<0) return true;
        if(result>0) return false;
        if(!started) return false;
        if(System.currentTimeMillis()-startTime>score*1000) loose();
        return result<0;
    }

    private boolean isWin(){
        return result>0;
    }

    private void loose(){
        result = -1;
        if(System.currentTimeMillis()-startTime>score*1000) endTime = startTime+score*1000;
        else endTime = System.currentTimeMillis();
    }

    private void win(){
        endTime = System.currentTimeMillis();
        result = 1;
    }

    private void addScore(int addCount){
        score+=addCount;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public void addPlayer(Integer player) {
        throw new NotImplementedException();
    }

    @Override
    public void removePlayer(Integer player) {
        throw new NotImplementedException();
    }

    @Override
    public boolean hasPlayer(Integer player) {
        throw new NotImplementedException();
    }

    @Override
    public Collection<Integer> getPlayers() {
        throw new NotImplementedException();
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
        started = true;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isFinished() {
        return isLoose()||isWin();
    }

    @Override
    public Map<Integer, Integer> getScores() {
        HashMap<Integer,Integer> scores = new HashMap<>();
        scores.put(0,getScore(null));
        return scores;
    }

    @Override
    public Integer getScore(Integer player) {
        if(!started) return score;
        return  (int) (score - ((isFinished()? endTime :System.currentTimeMillis())-startTime)/1000);
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public boolean suggestBomb(int x, int y, Integer player) {
        if(isWin() || isLoose()) throw new IllegalStateException("Game already finished!");
        if(board.suggestBomb(x,y)){
            addScore(5);
            if(board.getBombsLeft()==0){
                win();
            }
            return true;
        }else{
            addScore(-15);
            return false;
        }
    }

    @Override
    public CellVM[] openCell(int x, int y, Integer player) {
        if(isWin() || isLoose()) throw new IllegalStateException("Game already finished!");
        CellVM[] opened = board.openCell(x,y);
        if(opened.length>0){
            if(opened[0].getValue()!=Board.Cell.BOMB) addScore(3);
            else loose();
        }
        if(!started) start();
        return opened;
    }
}
