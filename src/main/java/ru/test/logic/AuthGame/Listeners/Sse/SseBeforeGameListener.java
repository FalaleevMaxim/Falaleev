package ru.test.logic.AuthGame.Listeners.Sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.test.ViewModel.GameProperties;
import ru.test.logic.AuthGame.GameCycle;
import ru.test.logic.AuthGame.Listeners.BeforeGameListener;
import ru.test.model.Storage;
import ru.test.model.User;

import java.io.IOException;

/**
 * Реализация интерфейса {@link BeforeGameListener} на основе {@link SseEmitter}
 */
public class SseBeforeGameListener implements BeforeGameListener<Integer>{
    /**
     *
     * @param emitter {@link SseEmitter}, через который будут отправляться сообщения игроку
     * @param userStorage  хранилище игроков
     * @param gameCycle
     * @param gameId
     */
    public SseBeforeGameListener(SseEmitter emitter, Storage<User> userStorage, GameCycle<Integer> gameCycle, String gameId) {
        this.emitter = emitter;
        this.userStorage = userStorage;
        this.gameCycle = gameCycle;
        this.gameId = gameId;
    }

    private SseEmitter emitter;
    private Storage<User> userStorage;
    private GameCycle<Integer> gameCycle;
    private String gameId;

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
        class UserVM{
            public UserVM(int id, String name, String realName) {
                this.id = id;
                this.name = name;
                this.realName = realName;
            }
            private int id;
            private String name;
            private String realName;
            public int getId() {
                return id;
            }
            public void setId(int id) {
                this.id = id;
            }
            public String getName() {
                return name;
            }
            public void setName(String name) {
                this.name = name;
            }
            public String getRealName() {
                return realName;
            }
            public void setRealName(String realName) {
                this.realName = realName;
            }
        }

        User user = userStorage.findById(player);
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
