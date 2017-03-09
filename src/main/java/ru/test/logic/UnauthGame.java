package ru.test.logic;

import ru.test.ViewModel.CellVM;
import ru.test.ViewModel.GameProperties;

/**
 * Класс одиночной игры для незарегистрированного игрока
 */
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

    /**
     * Проверяет, проиграл ли игрок
     */
    public boolean isLoose(){
        if(result<0) return true;
        if(result>0) return false;
        if(!started) return false;
        if(System.currentTimeMillis()-startTime>score*1000) loose();
        return result<0;
    }

    /**
     * Проверяет, выиграл ли игрок
     */
    public boolean isWin(){
        return result>0;
    }

    /**
     * Завершить игру поражением
     */
    private void loose(){
        result = -1;
        if(System.currentTimeMillis()-startTime>score*1000) endTime = startTime+score*1000;
        else endTime = System.currentTimeMillis();
    }

    /**
     * Завершить игру победой
     */
    private void win(){
        endTime = System.currentTimeMillis();
        result = 1;
    }

    /**
     * Добавить к счёту указанное количество очков
     * @param addCount количество очков для добавления
     */
    private void addScore(int addCount){
        score+=addCount;
    }

    /**
     * Начать отсчёт времени игры
     */
    public void start() {
        startTime = System.currentTimeMillis();
        started = true;
    }

    /**
     * Проверяет, начался ли отсчёт времени игры
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Проверяет, завершена ли игра
     */
    public boolean isFinished() {
        return isLoose()||isWin();
    }

    /**
     * @return Возвращает счёт
     */
    public Integer getScore() {
        if(!started) return score;
        return  (int) (score - ((isFinished()? endTime :System.currentTimeMillis())-startTime)/1000);
    }

    /**
     * @return Возвращает время начала игры
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Предположить бомбу в клетке
     * @param x координата x клетки
     * @param y координата y клетки
     * @return true если в ячейке бомба
     */
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

    /**
     * Открывает клетку и все окружающие клетки, если клетка пустая
     * @param x координата x клетки
     * @param y координата y клетки
     * @return массив всех открытых в этот ход клеток
     */
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

    /**
     * @return Возвращает поле игры.
     */
    public Board getBoard() {
        return board;
    }
}
