package com.ms.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button createDb;
    private Button insert;
    private Button queryAll;
    private Button delete;
    private Button addEvent;
    private Button addEvent1;
    private Button addEvent2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDb = findViewById(R.id.createDb);
        insert = findViewById(R.id.insert);
        queryAll = findViewById(R.id.queryAll);
        delete = findViewById(R.id.delete);
        addEvent = findViewById(R.id.addEvent);
        addEvent1 = findViewById(R.id.addEvent1);
        addEvent2 = findViewById(R.id.addEvent2);


        createDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper.getInstance().getDb();
            }
        });
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper.getInstance().getDb().add("aaa");
            }
        });

        queryAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<DBHelper.Event> notDeleteEvents = DBHelper.getInstance().getDb().getNotDeleteEvents();
                for (DBHelper.Event notDeleteEvent : notDeleteEvents) {
                    System.out.println(notDeleteEvent.toString());
                    DBHelper.getInstance().getDb().delete(notDeleteEvent._id);

                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> event = new HashMap<>();
                event.put("category", "init");
                event.put("a", "a");
                event.put("b", "b");
                EventClient.getInstance().add(event);
            }
        });

        addEvent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0;i<100;i++){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, Object> event = new HashMap<>();
                            event.put("category", "init");
                            event.put("a", "a");
                            event.put("b", "b");
                            EventClient.getInstance().add(event);
                        }
                    }).start();
                }

            }
        });

        addEvent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                for (int i = 0;i<100;i++){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, Object> event = new HashMap<>();
                            event.put("category", "init");
                            event.put("a", "a");
                            event.put("b", "b");
                            EventClient.getInstance().add(event);
                        }
                    }).start();
                }



            }
        });
    }
}