package ru.test.logic;

import ru.test.ViewModel.CellVM;
import ru.test.ViewModel.GameProperties;

public class UnauthGame{
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

    public boolean isLoose(){
        if(result<0) return true;
        if(result>0) return false;
        if(!started) return false;
        if(System.currentTimeMillis()-startTime>score*1000) loose();
        return result<0;
    }

    public boolean isWin(){
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

    public void start() {
        startTime = System.currentTimeMillis();
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return isLoose()||isWin();
    }

    public Integer getScore() {
        if(!started) return score;
        return  (int) (score - ((isFinished()? endTime :System.currentTimeMillis())-startTime)/1000);
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean suggestBomb(int x, int y) {
        if(isWin() || isLoose()) throw new IllegalStateException("Game already finished!");
        if(getBoard().suggestBomb(x,y)){
            addScore(5);
            if(getBoard().getBombsLeft()==0){
                win();
            }
            return true;
        }else{
            addScore(-15);
            return false;
        }
    }

    public CellVM[] openCell(int x, int y) {
        if(isWin() || isLoose()) throw new IllegalStateException("Game already finished!");
        CellVM[] opened = getBoard().openCell(x,y);
        if(opened.length>0){
            if(opened[0].getValue()!=Board.Cell.BOMB) addScore(3);
            else loose();
        }
        if(!started) start();
        return opened;
    }

    public Board getBoard() {
        return board;
    }
}
