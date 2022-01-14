package com.ms.app;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class EventHandler {
    private static volatile EventHandler instance;

    public static synchronized EventHandler getInstance() {
        if (instance == null) {
            instance = new EventHandler();
        }
        return instance;
    }

    private Map<String, String> headers = new HashMap<>();
    private Set<String> urls = new TreeSet<>();

    private EventHandler() {
        new Thread(new SendTask()).start();
        new Thread(new DeleteTask()).start();


    }

    public int httpSend(String body) {
        for (String url : urls) {
            HttpURLConnection connection = null;
            PrintWriter out = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                // 连接最大时间
                connection.setConnectTimeout(10 * 1000);
                // 读取最大时间
                connection.setReadTimeout(10 * 1000);
                if (headers != null) {
                    for (String k : headers.keySet()) {
                        if (null != k && !"".equals(k)) {
                            connection.setRequestProperty(k, headers.get(k));
                        }
                    }
                }
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.connect();
                out = new PrintWriter(connection.getOutputStream());
                out.write(body);
                out.flush();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    return responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * 发送任务
     */
    public class SendTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                List<DBHelper.Event> notDeleteEvents = DBHelper.getInstance().getDb().getNotDeleteEvents();
                if (notDeleteEvents.size() == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (DBHelper.Event event : notDeleteEvents) {
                        int code = httpSend(event._event);
                        if (code == 204) {
                            DBHelper.getInstance().getDb().updateDelete(event._id);
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除任务
     */
    public class DeleteTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                List<DBHelper.Event> events = DBHelper.getInstance().getDb().getDeleteEvents();
                if (events.size() == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (DBHelper.Event event : events) {
                        DBHelper.getInstance().getDb().delete(event._id);
                    }
                }
            }
        }
    }
}
