package com.kob.botrunningsystem.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Queue<Bot> bots = new LinkedList<>();


    public void addBot(Integer userId, String botCode, String game) {
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, game));
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot) {
        bot.executeBot(2000);
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (bots.isEmpty()) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    lock.unlock();
                    throw new RuntimeException(e);
                }
            } else {
                Bot bot = bots.remove();
                lock.unlock();

                consume(bot);
            }
        }
    }
}

