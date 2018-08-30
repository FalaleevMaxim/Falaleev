package ru.minesweeper.logic.AuthGame;

import ru.minesweeper.logic.Cell;
import ru.minesweeper.viewmodel.cell.CellVM;
import ru.minesweeper.viewmodel.GameProperties;
import ru.minesweeper.viewmodel.ScoreModel;
import ru.minesweeper.logic.Board;
import ru.minesweeper.logic.AuthGame.Listeners.GameEventsListener;

import java.util.Collection;

/**
 * Интерфейс для игры аутентифицированного игрока
 * @param <P> Тип идентификатора игрока
 */
public interface AuthGame<P> {
//region Игроки

    /**
     * @return Возвращает всех игроков
     */
    Collection<P> getPlayers();

    /**
     * Проверяет, существует ли игрок
     * @param player идентификатор игрока
     */
    boolean hasPlayer(P player);

    /**
     * Игрок выходит из игры
     * @param player идентификатор игрока
     */
    void quit(P player);

    /**
     * Возвращает информацию по очкам и статусу всех игроков
     * @return Коллекция объектов для каждого игрока, содержащих идентификатор игрока, счёт и статус.
     */
    Collection<ScoreModel<P>> getScores();

    /**
     * Возвращает счёт заданного игрока
     * @param player идентификатор игрока
     */
    int getScore(P player);
//endregion

//region События
    /**
     * Установка обработчика, передающего игроку события
     * @param player идентификатор игрока
     * @param listener обработчик событий
     */
    void setListener(P player, GameEventsListener<P> listener);
    //Получить обработчик событий игрока

    /**
     * Возвращает обработчик событий игрока
     * @param player идентификатор игрока
     */
    GameEventsListener<P> getListener(P player);
//endregion

//region Игра
    /**
     * Показывает, начата ли игра (был ли сделан хотя бы один ход и идёт ли отсчёт времени)
     */
    boolean isStarted();

    /**
     * Возвращает время начала игры
     * @return время начала игры в миллисекундах от 1 января 1970
     */
    long getStartTime();

    /**
     * Возвращает свойства игры
     * @return свойства игры
     */
    GameProperties getProperties();

    /**
     * @return Возвращает игровое поле (может скрывать значения неоткрытых ячеек)
     */
    Cell[][] getField();

    /**
     * @return Возвращает количество оставшихся на поле бомб
     */
    int getBombsLeft();

    /**
     * Игрок открывает клетку поля
     * @param x координата x ячейки
     * @param y координата y ячейки
     * @param player идентификатор игрока
     * @return массив открытых ячеек (координаты ячеек и их значения)
     */
    CellVM[] openCell(int x, int y, P player);

    /**
     * Игрок предполагает бомбу в ячейке
     * @param x координата x ячейки
     * @param y координата y ячейки
     * @param player идентификатор игрока
     * @return содержит ли указанная клетка бомбу
     */
    boolean suggestBomb(int x, int y, P player);
//endregion

//region Итоги
    /**
     * @return Завершена ли игра
     */
    boolean isFinished();

    /**
     * Возвращает время завершения игры
     * @return время завершения игры в миллисекундах от 1 января 1970
     */
    long getFinishTime();
    //Является ли победителем игрок

    /**
     * Проверяет, является ли заданный игрок победителем
     * @param player идентификатор игрока
     */
    boolean isWinner(P player);

    /**
     * @return Возвращает всех победителей
     */
    Collection<P> getWinners();

    /**
     * Проверяет, является ли заданный игрок проигравшим
     * @param player идентификатор игрока
     */
    boolean isLooser(P player);

    /**
     * Возвращает время выбывания игрока из игры
     * @param player идентификатор игрока
     * @return время выбывания игрока в миллисекундах от 1 января 1970
     */
    long getOutTime(P player);
//endregion
}