package ru.minesweeper.logic.exceptions;

import ru.minesweeper.logic.Cell;
import ru.minesweeper.viewmodel.cell.SimpleCellVM;

public class CellAlreadyUsedException extends RuntimeException {
    public final SimpleCellVM cell;

    public CellAlreadyUsedException(Cell cell) {
        this.cell = new SimpleCellVM(cell.getValue(), cell.isOpened(), cell.isBombSuggested());
    }
}
