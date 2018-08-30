package ru.minesweeper.util.lock;

public class WaitingThread {
    private final Thread thread;
    private final LockMode mode;
    private final Object lock = new Object();

    public WaitingThread(Thread thread, LockMode mode) {
        this.thread = thread;
        this.mode = mode;
    }

    public Thread getThread() {
        return thread;
    }

    public LockMode getMode() {
        return mode;
    }

    public Object getLock() {
        return lock;
    }
}
