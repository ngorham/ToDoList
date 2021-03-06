package net.ngorham.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * To Do List
 * ToDoListDAO.java
 * Purpose: Database Access Object, provides access to local SQLiteDatabase
 *
 * @author Neil Gorham
 * @version 1.1 05/04/2018
 *
 * 1.1: Added REMINDER column to NOTE table, updateReminder method
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
        values.put("CREATED_ON", note.getCreatedOn());
        values.put("LAST_MODIFIED", note.getLastModified());
        values.put("REMINDER", note.getReminder());
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
        values.put("LAST_MODIFIED", note.getLastModified());
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

    //Update Note REMINDER column in db
    public boolean updateReminder(Note note){
        Log.d("ToDoListDAO", "INSIDE updateReminder: reminder = " + note.getReminder());
        //Set content values
        ContentValues values = new ContentValues();
        values.put("REMINDER", note.getReminder());
        values.put("REMINDER_TIME", note.getReminderTime());
        //Update ITEM STRIKE where id matches
        try{
            int results = db.update("NOTE", values, "_id = ?",
                    new String[] {String.valueOf(note.getId())});
            return (results > 0);
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to update reminder in NOTE table",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Update Note LAST_MODIFIED column in db
    public boolean updateLastModified(Note note){
        Log.d("ToDoListDAO", "INSIDE updateLastModified: last modified time = " + note.getLastModified());
        //Set content values
        ContentValues values = new ContentValues();
        values.put("LAST_MODIFIED", note.getLastModified());
        //Update NOTE LAST_MODIFIED where id matches
        try{
            int results = db.update("NOTE", values, "_id = ?",
                    new String[] {String.valueOf(note.getId())});
            return (results > 0);
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to update last_modified in NOTE table",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Get Note id by createdOn and lastModified times
    public int fetchNoteId(String createdOn){
        int id = 0;
        String[] columns = {"_id"};
        String selection = "CREATED_ON = ? AND LAST_MODIFIED = ?";
        String[] selectionArgs = {createdOn, createdOn};
        try{
            id = 0;
            Cursor cursor = db.query("NOTE",
                    columns,
                    selection, selectionArgs,
                    null, null, null);
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    id = cursor.getInt(0);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return id;
        } catch(SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to fetch item from table",
                    Toast.LENGTH_SHORT).show();
            return id;
        }
    }

    //Get all Notes from db
    public ArrayList<Note> fetchAllNotes(){
        ArrayList<Note> notes = new ArrayList<>();
        try{
            Cursor cursor = db.rawQuery("SELECT * FROM NOTE", null);
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String createdOn = cursor.getString(2);
                    String lastModified = cursor.getString(3);
                    int reminder = cursor.getInt(4);
                    String reminderTime = cursor.getString(5);
                    Log.d("ToDoListDAO", "inside fetchAllNotes: reminder = " + reminder);
                    Note note = new Note(id, name, createdOn, lastModified, reminder, reminderTime);
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

    //Create (add) new Item to db
    public boolean addItem(Item item){
        //Set content values
        ContentValues values = new ContentValues();
        values.put("NAME", item.getName());
        values.put("LIST_ID", item.getNoteId());
        values.put("CREATED_ON", item.getCreatedOn());
        values.put("LAST_MODIFIED", item.getLastModified());
        values.put("STRIKE", item.getStrike());
        values.put("POSITION", item.getPosition());
        //Insert into db
        try{
            long results = db.insert("ITEM", null, values);
            return (results > -1);
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to insert item into table",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Update Item in db
    public boolean updateItem(Item item){
        //Set content values
        ContentValues values = new ContentValues();
        values.put("NAME", item.getName());
        values.put("LAST_MODIFIED", item.getLastModified());
        values.put("POSITION", item.getPosition());
        //update note where note.id matches
        try{
            int results = db.update("ITEM", values, "_id = ?",
                    new String[] {String.valueOf(item.getId())});
            return (results > 0);
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to update item from table",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Delete Item by id
    public boolean deleteItem(int itemId){
        //Delete note where id matches
        try{
            int results = db.delete("ITEM",
                    "_id = " + String.valueOf(itemId),
                    null);
            return (results > 0);
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to delete item from table",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Update Item STRIKE column  in db
    public boolean updateStrike(Item item){
        //Set content values
        ContentValues values = new ContentValues();
        values.put("STRIKE", item.getStrike());
        //Update ITEM STRIKE where id matches
        try{
            int results = db.update("ITEM", values, "_id = ?",
                    new String[] {String.valueOf(item.getId())});
            return (results > 0);
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to update strike in ITEM table",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    //Get Item id by createdOn and lastModified times
    public int fetchItemId(String createdOn){
        int id = 0;
        String[] columns = {"_id"};
        String selection = "CREATED_ON = ? AND LAST_MODIFIED = ?";
        String[] selectionArgs = {createdOn, createdOn};
        try{
            id = 0;
            Cursor cursor = db.query("ITEM",
                    columns,
                    selection, selectionArgs,
                    null, null, null);
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    id = cursor.getInt(0);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return id;
        } catch(SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to fetch item from table",
                    Toast.LENGTH_SHORT).show();
            return id;
        }
    }

    //Get all Items from db where listId matches
    public ArrayList<Item> fetchAllItems(int listId){
        ArrayList<Item> items = new ArrayList<>();
        try{
            String[] selectionArgs = new String[] {String.valueOf(listId)};
            Cursor cursor = db.query("ITEM",null,
                    "LIST_ID = ?", selectionArgs,
                    null, null, "POSITION");
            if(cursor.moveToFirst()) {
                while(!cursor.isAfterLast()){
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    int noteId = cursor.getInt(2);
                    String createdOn = cursor.getString(3);
                    String lastModified = cursor.getString(4);
                    int strike = cursor.getInt(5);
                    int position = cursor.getInt(6);
                    Item item = new Item(id, name, createdOn, lastModified, noteId, strike, position);
                    items.add(item);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return items;
        } catch (SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to fetch all items from table",
                    Toast.LENGTH_SHORT).show();
            return items;
        }
    }

    //Delete all Items from db where listId matches
    public boolean deleteAllItems(int listId){
        try{
            int results = db.delete("ITEM",
                    "LIST_ID = " + String.valueOf(listId),
                    null);
            return (results > 0);
        } catch(SQLiteException e){
            Toast.makeText(context,
                    "Database unavailable, failed to delete item from table",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
