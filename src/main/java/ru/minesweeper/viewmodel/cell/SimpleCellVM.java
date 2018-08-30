package ru.minesweeper.viewmodel.cell;

public class SimpleCellVM {
    public final Integer value;
    public final boolean isOpened;
    public final boolean bombSuggested;

    public SimpleCellVM(Integer value, boolean isOpened, boolean bombSuggested) {
        this.value = value;
        this.isOpened = isOpened;
        this.bombSuggested = bombSuggested;
    }
}
