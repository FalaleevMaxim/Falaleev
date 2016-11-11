package ru.test.logic;

import ru.test.ViewModel.CellVM;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UnauthGame implements Game<Integer>{
    public UnauthGame(int width, int height, int bombcount, int startScore){
        board = new BoardImpl(width,height,bombcount);
        this.startScore = startScore;
        score = startScore;
    }

    private Board board;
    private boolean started = false;
    private int score;
    private boolean loose = false;
    private long startTime;
    private int startScore;
//    private int fieldWidth;
//    private int fieldHeight;
//    private int bombcount;
//
//    public void setBombCount(int count){
//        if(bombcount>fieldWidth*fieldHeight) throw new IllegalArgumentException("Bomb count should be less than half of cells count");
//        this.bombcount = count;
//    }
//
//    public void setStartScore(int score){
//        if(score>0 && score<=300) this.score = score;
//        else throw new IllegalArgumentException("Score should be between 1 and 300");
//    }

    private void addScore(int addCount){
        score+=addCount;
        //if((System.currentTimeMillis()/1000-startTime)>score) loose = true;
    }

    @Override
    public CellVM[] getOpenedCells() {
        if(board ==null) return null;
        return board.getOpenedCells();
    }

    @Override
    public int getFieldWidth() {
        return board.getFieldWidth();
        //return this.fieldWidth;
    }

    /*public void setFieldWidth(int boardWidth) {
        if(boardWidth<2) throw new IllegalArgumentException("Field size should be at least 2x2");
        this.fieldWidth = boardWidth;
    }*/

    public int getFieldHeight() {
        return board.getFieldHeight();
        //return fieldHeight;
    }

    /*public void setFieldHeight(int boardHeight) {
        if(boardHeight<2) throw new IllegalArgumentException("Field size should be at least 2x2");
        this.fieldHeight = boardHeight;
    }*/

    @Override
    public void addPlayer(Integer player) {

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
        startTime = System.currentTimeMillis()/1000;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public Map<Integer, Integer> getScores() {
        HashMap<Integer,Integer> scores = new HashMap<>();
        scores.put(0,score);
        return scores;
    }

    @Override
    public Integer getScore(Integer player) {
        return score;
    }

    @Override
    public boolean suggestBomb(int x, int y, Integer player) {
        if(loose) throw new IllegalStateException("Game already finished! You loosed.");
        if(board.suggestBomb(x,y)){
            addScore(5);
            return true;
        }else{
            addScore(-15);
            return false;
        }

    }

    @Override
    public CellVM[] openCell(int x, int y, Integer player) {
        if(loose) throw new IllegalStateException("Game already finished! You loosed.");
        if(!started) start();
        CellVM[] opened = board.openCell(x,y);
        if(opened.length>0){
            if(opened[0].getValue()!=Board.Cell.BOMB) addScore(3);
            else loose=true;
        }
        return opened;
    }
}
