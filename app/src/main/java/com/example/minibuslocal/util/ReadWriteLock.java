package com.example.minibuslocal.util;

public class ReadWriteLock {
    private int readingReaders = 0;
    private int waitingWriters = 0;
    private int writingWriters = 0;
    private boolean preferWriter = false;

    public synchronized void readLock() throws InterruptedException{
        // 如果此时正在写入，或写入优先且有等待写入的线程，那么读取等待
        while (writingWriters > 0 || (preferWriter && waitingWriters > 0))
            wait();
        readingReaders++;
    }

    public synchronized void readUnlock(){
        // 将写入方式置为写入优先
        readingReaders--;
        preferWriter = true;
        // 唤醒其他线程
        notifyAll();
    }

    public synchronized void writeLock() throws InterruptedException{
        waitingWriters++;
        try{
            while (readingReaders > 0 || waitingWriters > 0)
                wait();
        }finally {
            waitingWriters--;
        }
        writingWriters++;
    }

    public synchronized void writeUnlock(){
        writingWriters--;
        preferWriter = false;
        notifyAll();
    }
}
