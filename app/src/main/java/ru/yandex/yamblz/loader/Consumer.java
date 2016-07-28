package ru.yandex.yamblz.loader;

import java.util.concurrent.CountDownLatch;

/**
 * Created by platon on 27.07.2016.
 */
public class Consumer extends Thread {

    private final CountDownLatch countDownLatch;
    private final Runnable result;

    public Consumer(Runnable result, CountDownLatch countDownLatch)
    {
        this.result = result;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        super.run();

        try
        {
            countDownLatch.await();
            result.run();

        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
