package ru.minesweeper.logic.AuthGame.Listeners.Sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.minesweeper.storage.JpaUserStorage;
import ru.minesweeper.viewmodel.GameProperties;
import ru.minesweeper.logic.AuthGame.GameCycle;
import ru.minesweeper.logic.AuthGame.Listeners.BeforeGameListener;
import ru.minesweeper.storage.Storage;
import ru.minesweeper.model.User;
import ru.minesweeper.viewmodel.UserVM;

import java.io.IOException;

/**
 * Реализация интерфейса {@link BeforeGameListener} на основе {@link SseEmitter}
 */
public class SseBeforeGameListener implements BeforeGameListener<Integer>{
    private SseEmitter emitter;

    private JpaUserStorage userStorage;
    /**
     *
     * @param emitter {@link SseEmitter}, через который будут отправляться сообщения игроку
     * @param userStorage  хранилище игроков
     */
    public SseBeforeGameListener(SseEmitter emitter, JpaUserStorage userStorage) {
        this.emitter = emitter;
        this.userStorage = userStorage;
    }

    @Override
    public void gameStarted() {
        try {
            emitter.send(SseEmitter.event().name("started").data(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playerJoined(Integer player) {
        User user = userStorage.findById(player).orElse(null);
        if(user==null) return;
        try {
            emitter.send(SseEmitter.event().name("joined").data(new UserVM(user.getId(),user.getUserName(),user.getRealName())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playerLeft(Integer player) {
        try {
            emitter.send(SseEmitter.event().name("left").data(player));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gameDismissed() {
        try {
            emitter.send(SseEmitter.event().name("dismissed"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void propertiesChange(GameProperties properties) {
        try {
            emitter.send(SseEmitter.event().name("properties").data(properties));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
