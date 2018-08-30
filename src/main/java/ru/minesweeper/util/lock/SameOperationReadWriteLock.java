package ru.minesweeper.util.lock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SameOperationReadWriteLock {
    private BlockingQueue<WaitingThread> threadQueue = new LinkedBlockingQueue<>();
    private ConcurrentHashMap<Thread, Thread> runningThreads = new ConcurrentHashMap<>();
    private volatile LockMode currentMode;


    public void readLock(){
        lock(LockMode.READ);
    }

    public void writeLock(){
        lock(LockMode.WRITE);
    }

    public void unlock(){
        unlock(Thread.currentThread());
    }

    private void lock(LockMode mode){
        Thread thread = Thread.currentThread();
        //Check if thread already has lock
        if(runningThreads.containsKey(thread)){
            //If it tries to get another mode, exception
            if(!currentMode.equals(mode)) {
                throw new IllegalStateException("Thread already runs other mode. Reading and writing are not allowed simultaneously.");
            }else {
                //If thread already has same mode, do nothing
                return;
            }
        }

        //If queue is not empty or mode is not current, add thread to queue and wait
        if(!threadQueue.isEmpty() || (currentMode!=null && !currentMode.equals(mode))){
            WaitingThread wait = new WaitingThread(thread, mode);
            threadQueue.add(wait);
            synchronized (wait.getLock()) {
                try {
                    wait.getLock().wait();
                } catch (InterruptedException e) {
                    threadQueue.remove(wait);
                    thread.interrupt();
                }
            }
            return;
        }

        //If queue is empty and mode is same (or yet not defined), can run thread now
        currentMode = mode;//in case it is first thread and mode not defined yet
        runningThreads.put(thread, thread);
    }

    private void unlock(Thread thread){
        runningThreads.remove(thread);
        if(!runningThreads.isEmpty()) return;
        runThreadsFromQueue();
    }

    private synchronized void runThreadsFromQueue(){
        if(threadQueue.isEmpty()) return;
        currentMode = threadQueue.peek().getMode();
        WaitingThread tm;
        while(!threadQueue.isEmpty() && threadQueue.peek().getMode().equals(currentMode)){
            tm = threadQueue.poll();
            synchronized (tm.getLock()) {
                runningThreads.put(tm.getThread(), tm.getThread());
                tm.getLock().notify();
            }
        }
    }
}
