package ru.minesweeper.logic.AuthGame.Listeners.Sse;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.minesweeper.logic.AuthGame.Listeners.JoinListener;
import ru.minesweeper.storage.JpaUserStorage;
import ru.minesweeper.storage.Storage;
import ru.minesweeper.model.User;
import ru.minesweeper.viewmodel.UserVM;

import java.io.IOException;

public class SseJoinListener implements JoinListener<Integer> {
    private SseEmitter emitter;
    private JpaUserStorage userStorage;

    public SseJoinListener(SseEmitter emitter, JpaUserStorage userStorage) {
        this.emitter = emitter;
        this.userStorage = userStorage;
    }

    @Override
    public void joinRequest(Integer player) {
        User user = userStorage.findById(player).orElse(null);
        if (user != null)
            try {
                emitter.send(SseEmitter.event().name("request").data(new UserVM(user.getId(), user.getUserName(), user.getRealName())));
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
