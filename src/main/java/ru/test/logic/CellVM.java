package ru.test.logic;

//Содержит значение и координаты ячейки для передачи во view
public interface CellVM {
    Integer getValue();
    int xPos();
    int yPos();
}
