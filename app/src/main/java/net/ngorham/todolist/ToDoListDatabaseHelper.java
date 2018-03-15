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
    //Note Schema
    private final String NOTE_TABLE = "NOTE";
    private final String COLUMN_ID = "_id";
    private final String COLUMN_NAME = "NAME";
    private final String NOTE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + NOTE_TABLE + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL"
            + ");";
    private String[] COLUMNS = new String[] {COLUMN_ID, COLUMN_NAME};

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

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS NOTE");
        db.execSQL(NOTE_TABLE_CREATE);
    }

    //Create or update database
    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion <= newVersion){
            db.execSQL("DROP TABLE IF EXISTS NOTE");
            db.execSQL(NOTE_TABLE_CREATE);
            insertNote(db, "Test List 1");
            insertNote(db, "Test List 2");
        }
    }

    //Insert Note info into NOTE table
    private void insertNote(SQLiteDatabase db, String name){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        db.insert(NOTE_TABLE, null, values);
    }
}
