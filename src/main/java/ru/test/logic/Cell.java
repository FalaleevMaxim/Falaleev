package ru.test.logic;

//Ячейка игрового поля.
public interface Cell {
    int open();
    boolean isOpened();
    boolean isBomb();
    boolean isEmpty();
    Integer getValue();
    void setValue(Integer val);
}
