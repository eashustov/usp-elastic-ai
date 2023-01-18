package ru.sberbank.uspelasticai.elastic.services;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class LogBlockingQueue implements Runnable{

    public BlockingQueue<Map<String, List<String>>> logsQueue;
    public Map<String, List<String>> logsWithIPAndMessage;

    public LogBlockingQueue(BlockingQueue<Map<String, List<String>>> logsQueue, Map<String, List<String>> logsWithIPAndMessage) {
        this.logsQueue = logsQueue;
        this.logsWithIPAndMessage = logsWithIPAndMessage;
    }

    @Override
    public void run() {

            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    logsQueue.put(logsWithIPAndMessage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

    }
}
