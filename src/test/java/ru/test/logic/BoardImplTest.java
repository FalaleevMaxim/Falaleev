package ru.test.logic;

import org.junit.Test;
import ru.test.ViewModel.CellVM;

import static org.junit.Assert.*;
import static ru.test.logic.Board.Cell;

public class BoardImplTest {
    @Test
    public void constructorTest(){
        String error = null;
        try{
            Board board = new BoardImpl(1,1,1);
        }catch (IllegalArgumentException e){
            error = e.getMessage();
        }
        assertEquals(error,"Field size should be at least 2x2");
        try{
            Board board = new BoardImpl(5,5,20);
        }catch (IllegalArgumentException e){
            error = e.getMessage();
        }
        assertEquals(error,"Bomb count should be less than half of cells count");
    }

    @Test
    public void initFieldAndOpenCell(){
        Board board = new BoardImpl(10,10,20);
        CellVM[] opened = board.openCell(5,5);
        //Проверка, не является ли первая открытая клетка бомбой
        assertEquals(board.getCell(5,5).isBomb(),false);

        //Проверка количества сгенерированных бомб, вместе с этим будет выведено поле игры.
        int bombcount = 0;
        for(int y=0;y<board.getFieldHeight();y++){
            for(int x=0;x<board.getFieldWidth();x++){
                Cell cell = board.getCell(x, y);
                System.out.print(cell.getValue()== Cell.BOMB?"*":cell.getValue()==0?"_":cell.getValue()+"");
                if(cell.isBomb()) bombcount++;
            }
            System.out.println();
        }
        assertEquals(bombcount,board.getBombCount());
        assertEquals(bombcount,20);

        //Проверка, что если открыто больше одной ячейки, первой была открыта пустая ячейка.
        assertEquals(opened.length>1,opened[0].getValue()==0);

        //Проверка, что была открыта хотя бы одна клетка.
        assertTrue(opened.length>0);
        for (CellVM c:opened ) {
            //Проверка, что на всех открытых клетках поставлен флаг что они открыты
            assertEquals(board.getCell(c.getX(),c.getY()).isOpened(),true);
            //Проверка, что значения в возвращённых клетках совпадают со значениями исходных клеток.
            assertEquals(board.getCell(c.getX(),c.getY()).getValue(),c.getValue());
            //Проверка, что повторное открытие клетки вернёт пустой массив
            assertEquals(board.openCell(c.getX(),c.getY()).length,0);
            //Проверка, что в случае рекурсивного открывания не была открыта ни одна бомба
            assertFalse(board.getCell(c.getX(),c.getY()).isBomb());
        }
        //Поскольку бомбу в первый ход открыть нельзя, общее количество бомб должно быть равно количеству неотмеченных.
        assertEquals(board.getBombCount(),board.getBombsLeft());

        //Открывание ячеек и повторение проверок для каждой.
        for(int y=0;y<board.getFieldHeight();y++){
            for(int x=0;x<board.getFieldWidth();x++){
                opened = board.openCell(x,y);
                if(opened.length>1){
                    //Проверка, что если открыто больше одной ячейки, первой была открыта пустая ячейка.
                    assertEquals(opened.length>1,opened[0].getValue()==0);
                    for (CellVM c:opened ) {
                        //Проверка, что на всех открытых клетках поставлен флаг что они открыты
                        assertEquals(board.getCell(c.getX(),c.getY()).isOpened(),true);
                        //Проверка, что значения в возвращённых клетках совпадают со значениями исходных клеток.
                        assertEquals(board.getCell(c.getX(),c.getY()).getValue(),c.getValue());
                        //Проверка, что в случае рекурсивного открывания не была открыта ни одна бомба
                        assertFalse(board.getCell(c.getX(),c.getY()).isBomb());
                    }
                }
            }
        }
        //Теперь, когда все клетки открыты, количество оставшихся бомб должно быть равно нулю.
        assertEquals(board.getBombsLeft(),0);
        //Количество открытых ячеек должно быть равно общему количеству ячеек
        assertEquals(board.getOpenedCells().length,board.getFieldHeight()*board.getFieldWidth());
    }

    @Test
    public void suggestBomb(){
        Board board = new BoardImpl(10,10,20);
        int firstopened = board.openCell(5,5).length;
        for(int y=0;y<board.getFieldHeight();y++){
            for(int x=0;x<board.getFieldWidth();x++){
                Cell cell = board.getCell(x, y);
                System.out.print(cell.getValue()== Cell.BOMB?"*":cell.getValue()==0?"_":cell.getValue()+"");
            }
            System.out.println();
        }
        int bombcount=0;
        for(int y=0;y<board.getFieldHeight();y++){
            for(int x=0;x<board.getFieldWidth();x++){
                assertEquals(board.getCell(x,y).isBomb(),board.suggestBomb(x,y));
                if(board.getCell(x,y).isBomb()) bombcount++;
                assertEquals(board.getBombsLeft(),board.getBombCount()-bombcount);
            }
        }
        assertEquals(board.getOpenedCells().length-firstopened,bombcount);
    }
}