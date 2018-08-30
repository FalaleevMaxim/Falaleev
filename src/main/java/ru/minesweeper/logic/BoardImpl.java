package ru.minesweeper.logic;

import ru.minesweeper.util.lock.SameOperationReadWriteLock;
import ru.minesweeper.viewmodel.cell.CellVM;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BoardImpl implements Board {
    private volatile ReentrantLock initLock = new ReentrantLock();
    private SameOperationReadWriteLock lock = new SameOperationReadWriteLock();
    private Cell[][] field;
    private volatile boolean isFieldInit = false;
    private int bombCount;
    private int bombsLeft;

    public BoardImpl(int width, int height, int bombcount) {
        if (width < 2 || height < 2) throw new IllegalArgumentException("Field size should be at least 2x2");
        if (bombcount > width * height / 2)
            throw new IllegalArgumentException("Bomb count should be less than half of cells count");
        this.bombCount = bombcount;
        this.bombsLeft = bombcount;
        field = new Cell[height][width];
    }

    @Override
    public boolean hasOpenedCells() {
        return isFieldInit;
    }

    @Override
    public int getFieldWidth() {
        return field[0].length;
    }

    @Override
    public int getFieldHeight() {
        return field.length;
    }

    @Override
    public int getBombCount() {
        return bombCount;
    }

    @Override
    public int getBombsLeft() {
        return bombsLeft;
    }

    @Override
    public Cell getCell(int x, int y) {
        if (!isFieldInit) throw new IllegalStateException("Trying to get cell value but field not initialized");
        if (!validateCoords(x, y)) throw new IllegalArgumentException("Illegal coordinates {" + x + "," + y + "}");
        return new Cell(field[y][x]);
    }

    @Override
    public Cell[][] getField() {
        if (!isFieldInit) return null;
        lock.readLock();
        Cell[][] fieldcopy = new Cell[getFieldHeight()][getFieldWidth()];
        try {
            for (int i = 0; i < getFieldHeight(); i++) {
                for (int j = 0; j < getFieldWidth(); j++) {
                    fieldcopy[i][j] = new Cell(field[i][j]);
                }
            }
        } finally {
            lock.unlock();
        }
        return fieldcopy;
    }

    @Override
    public CellVM[] openCell(int x, int y) {
        if (!validateCoords(x, y)) throw new IllegalArgumentException("Illegal coordinates {" + x + "," + y + "}");
        //Если это первая открытая клетка, заполнить поле так, чтобы в этой клетке не было бомбы.
        //Чтобы несколько потоков одновременно не инициализировали поле, используется initLock
        if (!isFieldInit && initLock != null && initLock.tryLock()) {
            try {
                lock.writeLock();
                initField(x, y);
            } finally {
                initLock.unlock();
                lock.unlock();
                initLock = null;
            }
        }
        lock.writeLock();
        //Создание списка открытых клеток.
        ArrayList<CellVM> newOpened = new ArrayList<>();
        //Вызов рекурсивной функции открывания ячейки.
        openCell(x, y, newOpened);
        lock.unlock();
        return newOpened.toArray(new CellVM[newOpened.size()]);
    }

    /**
     * Рекурсивная функция открытия ячеек, заполняет переданный список открытых ячеек.
     */
    private void openCell(int x, int y, List<CellVM> newOpened) {
        //Клетка, если не является уже открытой, открывается и заносится в список
        Cell cell = field[y][x];
        if (cell.isOpened()) return;
        newOpened.add(new CellVM(x, y, cell.open()));
        if (cell.isBomb()) bombsLeft--;
        //Если клетка пустая, открыть все окружающие клетки.
        if (cell.isEmpty()) {
            for (int y1 = Math.max(y - 1, 0); y1 <= Math.min(y + 1, getFieldHeight() - 1); y1++) {
                for (int x1 = Math.max(x - 1, 0); x1 <= Math.min(x + 1, getFieldWidth() - 1); x1++) {
                    if (x1 == x && y1 == y) continue;
                    openCell(x1, y1, newOpened);
                }
            }
        }
    }

    //

    /**
     * Заполнение поля так, чтобы в первой открытой ячейке не было бомбы
     */
    private void initField(int x, int y) {
        if (!validateCoords(x, y)) throw new IllegalArgumentException("Illegal coordinates {" + x + "," + y + "}");
        class Coordinates {
            private int x;
            private int y;

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Coordinates coord = (Coordinates) o;
                return getX() == coord.getX() && getY() == coord.getY();
            }

            @Override
            public int hashCode() {
                int result = getX();
                result = 31 * result + getY();
                return result;
            }

            public int getX() {
                return x;
            }

            public void setX(int x) {
                this.x = x;
            }

            public int getY() {
                return y;
            }

            public void setY(int y) {
                this.y = y;
            }
        }
        //Список случайных координат бомб
        ArrayList<Coordinates> bombs = new ArrayList<>(bombCount);
        //Заполнение списка уникальными случайными координатами, не совпадающими с указанной точкой
        while (bombs.size() < bombCount) {
            Coordinates c = new Coordinates();
            do {
                c.setX((int) (Math.random() * getFieldWidth()));
                c.setY((int) (Math.random() * getFieldHeight()));
            } while ((c.getX() == x && c.getY() == y) || bombs.contains(c));
            bombs.add(c);
        }
        //Заполнение поля нулями.
        for (int i = 0; i < getFieldHeight(); i++) {
            for (int j = 0; j < getFieldWidth(); j++) {
                field[i][j] = new Cell(0);
            }
        }
        Cell cell;
        for (Coordinates c : bombs) {
            //Для каждой координаты из списка в соответствующую ячейку поля устанавливается бомба
            field[c.getY()][c.getX()].setValue(Cell.BOMB);
            //Для каждой окружающей бомбу клетки, не являющейся бомбой, значение увеличивается на 1.
            for (int y1 = Math.max(c.getY() - 1, 0); y1 <= Math.min(c.getY() + 1, getFieldHeight() - 1); y1++) {
                for (int x1 = Math.max(c.getX() - 1, 0); x1 <= Math.min(c.getX() + 1, getFieldWidth() - 1); x1++) {
                    if (x1 == c.getX() && y1 == c.getY()) continue;
                    cell = field[y1][x1];
                    if (!cell.isBomb()) {
                        cell.setValue(cell.getValue() + 1);
                    }
                }
            }
        }
        //Указывается, что поле создано.
        isFieldInit = true;
    }

    @Override
    public boolean suggestBomb(int x, int y) {
        if (!validateCoords(x, y)) throw new IllegalArgumentException("Illegal coordinates {" + x + "," + y + "}");
        if (!isFieldInit) throw new IllegalStateException("Trying to check bomb but field not initialized");
        lock.writeLock();
        try {
            Cell cell = field[y][x];
            if (cell.suggestBomb()) {
                bombsLeft--;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Проверка, что указанные координаты находятся в пределах поля
     */
    private boolean validateCoords(int x, int y) {
        return x >= 0 && x < getFieldWidth() && y >= 0 && y < getFieldHeight();
    }
}