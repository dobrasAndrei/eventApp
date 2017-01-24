package com.example.takeofflabs.eventapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.takeofflabs.eventapp.models.Event;

import java.util.ArrayList;

/**
 * Created by takeofflabs on 24/01/17.
 */

public class EventDatabase extends SQLiteOpenHelper {

    public static final String TAG = EventDatabase.class.getSimpleName();

    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_DATE= "date";

    private static final String DATABASE_NAME = "event.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "create table "
            + TABLE_EVENTS + "( " + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_TEXT + " text not null, "
            + COLUMN_DATE + " text not null);";

    public EventDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
        }
//
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
            onCreate(db);
        }

        public void saveEvent(Event event) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ID, event.getId());
            cv.put(COLUMN_TEXT, event.getText());
            cv.put(COLUMN_DATE, event.getDate());
            SQLiteDatabase db = getWritableDatabase();
            db.insert(TABLE_EVENTS, null, cv);
            db.close();
        }
//
//        public ArrayList<Event> getEvents() {
//            ArrayList<Event> events = new ArrayList<Event>();
//            String getEventsQuery = "select * " + "from " + TABLE_EVENTS;
//            Cursor c = getReadableDatabase().rawQuery(getEventsQuery, null, null);
//            String a = "";
//            return events;
//        }
//
//        public boolean deleteUser(User currentUser) {
//            SQLiteDatabase db = getWritableDatabase();
//            int deleted = db.delete(TABLE_USERS,
//                    String.format("%s = ? and %s = ?", COLUMN_USERNAME, COLUMN_PASSWORD),
//                    new String[] { currentUser.getUsername(), currentUser.getPassword() });
//            db.close();
//            if (deleted == 0) {
//                return false;
//            } else {
//                return true;
//            }
//        }
//    }


}
