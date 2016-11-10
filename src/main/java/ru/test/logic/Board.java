package ru.test.logic;

//Игровое поле, содержит основную логику игры.
public interface Board {
    //Возвращает массив всех открытых ячеек
    CellVM[] getOpenedCells();
    //Возвращает размеры поля.
    int getFieldWidth();
    int getFieldHeight();
    //Возвращает количество бомб на поле
    int getBombCount();
    //Возвращает количество неоткрытых бомб
    int getBombsLeft();
    //Открывает ячейку и возвращает все открытые в этот ход ячейки (если ячейка пустая, будут открыты соседние ячейки)
    CellVM[] openCell(int x, int y);
    //Открывает ячейку и показывает, есть ли там бомба.
    boolean suggestBomb(int x,int y);

    class Cell{
        public Cell(int value){
            if(!checkValue(value)) throw new IllegalArgumentException("value should be between -1 and 8");
            this.value = value;
        }
        public Cell(){}

        public static int BOMB = -1;

        private Integer value;
        private boolean opened = false;

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
        public boolean isOpened() {
            return opened;
        }

        public boolean isBomb() {
            return value==BOMB;
        }

        public boolean isEmpty() {
            return value==0;
        }

        private boolean checkValue(Integer v){
            return (!(v == null || v < 0 || v > 8) && v!=BOMB);
        }
    }
}
