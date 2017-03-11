package ru.test.logic;

import ru.test.ViewModel.CellVM;
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
     * @return Возвращает массив всех открытых ячеек
     */
    CellVM[] getOpenedCells();
    //Возвращает размеры поля.

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

    /**
     * Класс для хранения ячеек поля
     */
    class Cell{
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return opened == cell.opened && (value != null ? value.equals(cell.value) : cell.value == null);
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (opened ? 1 : 0);
            return result;
        }

        public Cell(){}

        public static int BOMB = -1;

        private Integer value;
        private boolean opened = false;
        private boolean bombSuggested = false;

        public Integer getValue() {
            return value;
        }
        public void setValue(Integer val) {
            if(!checkValue(val)) throw new IllegalArgumentException("value should be between -1 and 8");
            value = val;
        }

        public int open() {
            opened = true;
            return value;
        }

        /**
         * Помечает, что клетка проверялась на наличие бомбы
         * @return true если в клетке бомба
         */
        public boolean suggestBomb(){
            bombSuggested = true;
            return isBomb();
        }

        /**
         * Показывает, открыта ли клетка
         */
        public boolean isOpened() {
            return opened;
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

        /**
         * Проверяет, находится ли значение в допустимых пределах
         * @param v проверяемое значение
         */
        private boolean checkValue(Integer v){
            return (v!=null &&((v >= 0 && v <= 8) || v==BOMB));
        }

        /**
         * Показывает, проверялась ли клетка на аличие бомбы
         */
        public boolean isBombSuggested() {
            return bombSuggested;
        }
    }
}
