package net.ngorham.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * To Do List
 * ToDoListDatabaseHelper.java
 * Purpose: Builds SQLiteDatabase for long term storage
 *
 * @author Neil Gorham
 * @version 1.1 05/04/2018
 *
 * 1.1: Added REMINDER and REMINDER_TIME columns to NOTE table
 */

public class ToDoListDatabaseHelper extends SQLiteOpenHelper{
    //Private constants
    private static final String DB_NAME = "ToDoList";
    private static final int DB_VERSION = 11;
    //Note Schema
    private final String NOTE_TABLE = "NOTE";
    private final String COLUMN_ID = "_id";
    private final String COLUMN_NAME = "NAME";
    private final String COLUMN_CREATED_ON = "CREATED_ON";
    private final String COLUMN_LAST_MODIFIED = "LAST_MODIFIED";
    private final String COLUMN_REMINDER = "REMINDER";
    private final String COLUMN_REMINDER_TIME = "REMINDER_TIME";
    private final String NOTE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + NOTE_TABLE + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_CREATED_ON + " TEXT, "
            + COLUMN_LAST_MODIFIED + " TEXT, "
            + COLUMN_REMINDER + " INTEGER, "
            + COLUMN_REMINDER_TIME + " TEXT"
            + ");";
    //Item Schema
    private final String ITEM_TABLE = "ITEM";
    //COLUMN_ID = "_id"
    //COLUMN_NAME = "NAME"
    private final String COLUMN_LIST_ID = "LIST_ID";
    //COLUMN_CREATED_ON = "CREATED_ON"
    //COLUM_LAST_MODIFIED = "LAST_MODIFIED"
    private final String COLUMN_STRIKE = "STRIKE";
    private final String COLUMN_POSITION = "POSITION";
    private final String ITEM_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + ITEM_TABLE + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_LIST_ID + " INTEGER, "
            + COLUMN_CREATED_ON + " TEXT, "
            + COLUMN_LAST_MODIFIED + " TEXT, "
            + COLUMN_STRIKE + " NUMERIC, "
            + COLUMN_POSITION + " INTEGER"
            + ");";

    //Default constructor
    ToDoListDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS NOTE");
        db.execSQL("DROP TABLE IF EXISTS ITEM");
        db.execSQL(NOTE_TABLE_CREATE);
        db.execSQL(ITEM_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        updateDatabase(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

    //Create or update database
    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if(newVersion > 10){
            //Add REMINDER_TIME column to NOTE table
            db.execSQL("ALTER TABLE NOTE ADD COLUMN REMINDER_TIME TEXT");
        }
    }
}
