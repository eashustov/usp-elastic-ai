package ru.sberbank.uspelasticai.elastic.services;


import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class LogBlockingQueue{

    public static ArrayBlockingQueue<Map<String, List<String>>> logsQueue = new ArrayBlockingQueue<>(500);

    public LogBlockingQueue() {
//        this.logsQueue = new LinkedBlockingDeque<>();
    }

}
