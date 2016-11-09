package ru.test.logic;

//Игровое поле, содержит основную логику игры.
public interface Board {
    //Возвращает массив всех открытых ячеек
    CellVM[] getOpenedCells();
    //Возвращает размеры поля.
    int getFieldWidth();
    int getFieldHeight();
    //Открывает ячейку и возвращает все открытые в этот ход ячейки (если ячейка пустая, будут открыты соседние ячейки)
    CellVM[] openCell(int x, int y);
    //Открывает ячейку и показывает, есть ли там бомба.
    boolean suggestBomb(int x,int y);
}
