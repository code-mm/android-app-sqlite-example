package com.ms.app;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Map;

public class EventClient {
    private static volatile EventClient instance;

    public static synchronized EventClient getInstance() {
        if (instance == null) {
            instance = new EventClient();
        }
        return instance;
    }

    /**
     * 存储事件的集合
     */
    LinkedList<String> events = new LinkedList<>();

    public synchronized void add(Map<String, Object> event) {
        events.add(new JSONObject(event).toString());
    }


    private EventClient() {
        // 读取事件 存入数据库
        new Thread(new DBTask()).start();
    }


    public class DBTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (events.size() == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    LinkedList<String> t = new LinkedList<>(events);
                    for (String event : t) {
                        long add = DBHelper.getInstance().getDb().add(event);
                        if (add != 0) {
                            System.out.println("current count : " + events.size() + "db add success  event : " + event);
                            events.remove(event);
                        }
                    }
                }
            }
        }
    }
}