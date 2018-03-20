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
    private static final int DB_VERSION = 5;
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
    //Item Schema
    private final String ITEM_TABLE = "ITEM";
    //COLUMN_ID = "_id"
    //COLUMN_NAME = "NAME"
    private final String COLUMN_LIST_ID = "LIST_ID";
    private final String COLUMN_CREATED_ON = "CREATED_ON";
    private final String COLUMN_LAST_MODIFIED = "LAST_MODIFIED";
    private final String COLUMN_STRIKE = "STRIKE";
    private final String ITEM_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + ITEM_TABLE + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_LIST_ID + " INTEGER, "
            + COLUMN_CREATED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + COLUMN_LAST_MODIFIED + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + COLUMN_STRIKE + " NUMERIC"
            + ");";


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
        db.execSQL("DROP TABLE IF EXISTS ITEM");
        db.execSQL(NOTE_TABLE_CREATE);
        db.execSQL(ITEM_TABLE_CREATE);
    }

    //Create or update database
    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion <= newVersion){
            db.execSQL("DROP TABLE IF EXISTS NOTE");
            db.execSQL("DROP TABLE IF EXISTS ITEM");
            db.execSQL(NOTE_TABLE_CREATE);
            db.execSQL(ITEM_TABLE_CREATE);
            insertNote(db, "Test List 1");
            insertNote(db, "Test List 2");
            insertItem(db, "Test Item 1", 1);
            insertItem(db, "Test Item 2", 1);
            insertItem(db, "Test Item 3", 2);
            insertItem(db, "Test Item 4", 2);
        }
    }

    //Insert Note info into NOTE table
    private void insertNote(SQLiteDatabase db, String name){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        db.insert(NOTE_TABLE, null, values);
    }

    //Insert Item info into Item table
    private void insertItem(SQLiteDatabase db, String name, int listId){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_LIST_ID, listId);
        db.insert(ITEM_TABLE, null, values);
    }
}
