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
    private static final int DB_VERSION = 9;
    //Note Schema
    private final String NOTE_TABLE = "NOTE";
    private final String COLUMN_ID = "_id";
    private final String COLUMN_NAME = "NAME";
    private final String COLUMN_CREATED_ON = "CREATED_ON";
    private final String COLUMN_LAST_MODIFIED = "LAST_MODIFIED";
    private final String NOTE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + NOTE_TABLE + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_CREATED_ON + " TEXT, "
            + COLUMN_LAST_MODIFIED + " TEXT"
            + ");";
    private String[] COLUMNS = new String[] {COLUMN_ID, COLUMN_NAME};
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
        }
    }
}
