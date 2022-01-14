package com.ms.app;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ALL")
public class DBHelper {
    private static volatile DBHelper instance;

    public static synchronized DBHelper getInstance() {
        if (instance == null) {
            instance = new DBHelper();
        }
        return instance;
    }

    private DB db;

    public DB getDb() {
        return db;
    }

    private DBHelper() {
        db = new DB(MainApplicarion.getInstance());
    }

    public static final class Event {
        int _id;
        int _delete;
        String _event;

        @Override
        public String toString() {
            return "Event{" +
                    "_id=" + _id +
                    ", _delete=" + _delete +
                    ", _event='" + _event + '\'' +
                    '}';
        }
    }

    public static class DB extends SQLiteOpenHelper {
        private static final String TAG = "DB";
        private static final String DB_NAME = "_event.db";
        private static final String CREATE_TABLE = "create table _event(_id integer primary key autoincrement,_event text,_delete integer)";
        private static final int VERSION = 1;

        private SQLiteDatabase database;

        public DB(@Nullable Context context) {
            super(context, DB_NAME, null, VERSION);
            database = getWritableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        }

        /**
         * 增加事件
         *
         * @param event
         * @return
         */
        public long add(String event) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_event", event);
            contentValues.put("_delete", 0);
            return database.insert("_event", null, contentValues);
        }

        /**
         * 逻辑删除事件
         *
         * @param id
         * @return
         */
        public int updateDelete(int id) {
            database.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", id);
            int r = database.update("_event", contentValues, "_delete=?", new String[]{});
            database.setTransactionSuccessful();
            return r;
        }

        /**
         * 删除事件
         *
         * @param id
         * @return
         */
        public long delete(int id) {
            database.beginTransaction();
            int r = database.delete("_event", "_id=?", new String[]{id + ""});
            database.setTransactionSuccessful();
            return r;
        }

        /**
         * 获取没有删除的事件
         *
         * @return
         */
        public List<Event> getNotDeleteEvents() {
            List<Event> r = new ArrayList<>();
            database.beginTransaction();
            String sql = "select * from _event where _delete = 0";
            Cursor cursor = database.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                try {
                    Event event = new Event();
                    event._id = cursor.getInt(cursor.getColumnIndex("_id"));
                    event._event = cursor.getString(cursor.getColumnIndex("_event"));
                    event._delete = cursor.getInt(cursor.getColumnIndex("_delete"));
                    r.add(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            database.setTransactionSuccessful();
            return r;
        }


        /**
         * 获取逻辑删除的事件
         *
         * @return
         */
        public List<Event> getDeleteEvents() {
            database.beginTransaction();
            List<Event> r = new ArrayList<>();
            String sql = "select * from _event where _delete = 1";
            Cursor cursor = database.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                try {
                    Event event = new Event();
                    event._id = cursor.getInt(cursor.getColumnIndex("_id"));
                    event._event = cursor.getString(cursor.getColumnIndex("_event"));
                    event._delete = cursor.getInt(cursor.getColumnIndex("_delete"));
                    r.add(event);
                    //
                    database.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return r;
        }
    }
}