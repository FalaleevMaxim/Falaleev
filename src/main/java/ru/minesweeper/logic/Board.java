package ru.minesweeper.logic;

import ru.minesweeper.viewmodel.cell.CellVM;
/**
 * Игровое поле, содержит основную логику игры
 */
public interface Board {
    //

    /**
     * Проверяет, есть ли открытые клетки.
     */
    boolean hasOpenedCells();

    /**
     * @return Возвращает ширину поля.
     */
    int getFieldWidth();

    /**
     * @return Возвращает высоту поля.
     */
    int getFieldHeight();

    /**
     * @return Возвращает количество бомб на поле
     */
    int getBombCount();

    /**
     * @return Возвращает количество неоткрытых бомб
     */
    int getBombsLeft();

    /**
     * @return Возвращает ячейку поля по указанным координатам
     */
    Cell getCell(int x, int y);

    /**
     * @return Возвращает поле игры
     */
    Cell[][] getField();

    /**
     * Открывает ячейку и возвращает все открытые в этот ход ячейки (если ячейка пустая, будут открыты соседние ячейки)
     * @param x координата x ячейки
     * @param y координата y ячейки
     * @return массив открытых ячеек
     */
    CellVM[] openCell(int x, int y);

    /**
     * Открывает ячейку и показывает, есть ли там бомба.
     * @param x координата x ячейки
     * @param y координата y ячейки
     * @return true если ячейка содержит бомбу
     */
    boolean suggestBomb(int x,int y);
}
