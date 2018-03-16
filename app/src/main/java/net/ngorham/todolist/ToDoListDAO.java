package net.ngorham.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NBG on 3/14/2018.
 */

public class ToDoListDAO {
    //Private variables
    private Context context;
    private SQLiteDatabase db;
    private ToDoListDatabaseHelper dbHelper;

    //Constructor
    public ToDoListDAO(Context context){
        this.context = context;
        dbHelper = new ToDoListDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    //Close db
    public void close(){
        db.close();
    }

    //Create (add) new Note to db
    public boolean addNote(Note note){
        //Set content values
        ContentValues values = new ContentValues();
        values.put("NAME", note.getName());
        //Insert into db
        try{
            long results = db.insert("NOTE", null, values);
            return (results > -1);
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to insert item into table",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Delete Note by id
    public boolean deleteNote(int noteId){
        //Delete note where id matches
        try{
            int results = db.delete("NOTE",
                    "_id = " + String.valueOf(noteId),
                    null);
            return (results > 0);
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to delete item from table",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Update Note in db
    public boolean updateNote(Note note){
        //Set content values
        ContentValues values = new ContentValues();
        values.put("NAME", note.getName());
        //update note where note.id matches
        try{
            int results = db.update("NOTE", values, "_id = ?",
                    new String[] {String.valueOf(note.getId())});
            return (results > 0);
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to update item from table",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Get Note by id
    public Note fetchNoteById(int noteId){
        Note note = new Note();
        String selection = "_id = ?";
        String[] selectionArgs = {String.valueOf(noteId)};
        String[] columns = {"_id", "NAME"};
        try {
            Cursor cursor = db.query("NOTE",
                    columns,
                    selection, selectionArgs,
                    null, null, "_id");
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    note.setId(cursor.getInt(0));
                    note.setName(cursor.getString(1));
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return note;
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to fetch item from table",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //Get all Notes from db
    public List<Note> fetchAllNotes(){
        List<Note> notes = new ArrayList<Note>();
        try{
            Cursor cursor = db.rawQuery("SELECT * FROM NOTE", null);
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    Note note = new Note(id, name);
                    notes.add(note);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return notes;
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to fetch all items from table",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
