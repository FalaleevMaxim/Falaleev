package ru.test.model;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.test.logic.AuthGame.GameCycle;

import java.io.IOException;

public class SseInviteListener<I> implements InviteListener<I>{
    public SseInviteListener(SseEmitter emitter) {
        this.emitter = emitter;
    }

    private SseEmitter emitter;

    @Override
    public void invited(I game) {
        try {
            emitter.send(SseEmitter.event().name("invited").data(game));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void confirmed(I game) {
        try {
            emitter.send(SseEmitter.event().name("confirmed").data(game));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uninvited(I game) {
        try {
            emitter.send(SseEmitter.event().name("uninvited").data(game));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
