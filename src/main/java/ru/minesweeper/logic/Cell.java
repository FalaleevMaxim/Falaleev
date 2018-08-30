package ru.minesweeper.logic;

import ru.minesweeper.logic.exceptions.CellAlreadyUsedException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Класс для хранения ячеек поля
 */
public class Cell{
    private AtomicBoolean inUse = new AtomicBoolean(false);
    public static final int BOMB = -1;
    private volatile boolean used;
    private Integer value;
    private volatile boolean opened = false;
    private volatile boolean bombSuggested = false;

    /**
     * @param value Значаение ячейки. 0-8 показывает количество бомб вокруг клетки, -1 означает что в клетке бомба
     */
    public Cell(int value){
        if(!checkValue(value)) throw new IllegalArgumentException("value should be between -1 and 8");
        this.value = value;
    }

    /**
     * Конструктор копирования
     */
    public Cell(Cell other){
        this.value = other.getValue();
        this.opened = other.isOpened();
        this.bombSuggested = other.isBombSuggested();
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer val) {
        if(!checkValue(val)) throw new IllegalArgumentException("value should be between -1 and 8");
        value = val;
    }

    /**
     * Показывает, открыта ли клетка
     */
    public boolean isOpened() {
        waitUse();
        return opened;
    }

    /**
     * Показывает, проверялась ли клетка на наличие бомбы
     */
    public boolean isBombSuggested() {
        waitUse();
        return bombSuggested;
    }

    /**
     * Открывает клетку
     * @throws CellAlreadyUsedException если клетка уже использована
     * @return начение клетки (0-8 или -1 если бомба)
     */
    public int open() {
        checkUsed();
        waitUse();
        try {
            checkUsed();
            opened = true;
            used = true;
        }finally {
            inUse.set(false);
        }
        return value;
    }

    /**
     * Отмечает бомбу (если она есть)
     * Если бомба есть, клетка отмечается использованной и проверенной на бомбу.
     * @throws CellAlreadyUsedException если клетка уже использована
     * @return true если в клетке бомба
     */
    public boolean suggestBomb(){
        checkUsed();
        waitUse();
        try {
            checkUsed();
            if(isBomb()) {
                bombSuggested = true;
                used = true;
            }
        }finally {
            inUse.set(false);
        }
        return bombSuggested;
    }

    /**
     * Если клетка использована (открыта или отмечена), с ней уже нельзя ничего делать
     */
    private void checkUsed(){
        if(used) throw new CellAlreadyUsedException(this);
    }

    /**
     * Ждет, пока клетка используется
     */
    private void waitUse(){
        while (!inUse.compareAndSet(false, true)) {
            Thread.yield();
        }
    }

    /**
     * Проверяет, есть ли в клетке бомбп
     */
    public boolean isBomb() {
        return value==BOMB;
    }

    /**
     * Проверяет, пустая ли клетка
     */
    public boolean isEmpty() {
        return value==0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return opened == cell.opened && (value != null ? value.equals(cell.value) : cell.value == null);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    /**
     * Проверяет, находится ли значение в допустимых пределах
     * @param v проверяемое значение
     */
    private static boolean checkValue(Integer v){
        return (v!=null &&((v >= 0 && v <= 8) || v==BOMB));
    }
}