package net.ngorham.todolist;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by NBG on 3/10/2018.
 */

public class ToDoListDatabaseHelper extends SQLiteOpenHelper{
    //Private constants
    private static final String DB_NAME = "ToDoList";
    private static final int DB_VERSION = 3;

    //Default constructor
    ToDoListDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db){
        updateDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        updateDatabase(db, oldVersion, newVersion);
    }

    //Create or update database
    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion <= newVersion){
            db.execSQL("CREATE TABLE LIST ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "NAME TEXT);");
            insertList(db, "Test List 1");
            insertList(db, "Test List 2");
        }
    }

    //Insert List info into LIST table
    private void insertList(SQLiteDatabase db, String name){
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        db.insert("LIST", null, values);
    }
}
