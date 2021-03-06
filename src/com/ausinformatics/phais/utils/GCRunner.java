package com.ausinformatics.phais.utils;

public class GCRunner implements Runnable {

    public int timeout = 100;

    @Override
    public void run() {
        while (true) {
            try {
                if (timeout < 0) {
                    break;
                }
                if (timeout == 0) {
                    Thread.sleep(10);
                    continue;
                }
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.gc();
        }
    }
    
    public void start() {
        Thread t = new Thread(this);
        t.setName("GCRunner");
        t.start();
    }
}
