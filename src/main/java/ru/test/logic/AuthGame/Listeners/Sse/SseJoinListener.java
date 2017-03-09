package ru.test.logic.AuthGame.Listeners.Sse;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.test.logic.AuthGame.Listeners.JoinListener;
import ru.test.storage.Storage;
import ru.test.model.User;

import java.io.IOException;

public class SseJoinListener implements JoinListener<Integer> {
    private SseEmitter emitter;
    private Storage<User> userStorage;

    public SseJoinListener(SseEmitter emitter, Storage<User> userStorage) {
        this.emitter = emitter;
        this.userStorage = userStorage;
    }

    @Override
    public void joinRequest(Integer player) {
        class Player{
            public Player(int id, String name, String realName) {
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
        if(user!=null)
            try {
                 emitter.send(SseEmitter.event().name("request").data(new Player(user.getId(),user.getUserName(),user.getRealName())));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void inviteAccepted(Integer player) {
        try {
            emitter.send(SseEmitter.event().name("accepted").data(player, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void inviteRejected(Integer player) {
        try {
            emitter.send(SseEmitter.event().name("rejected").data(player));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
