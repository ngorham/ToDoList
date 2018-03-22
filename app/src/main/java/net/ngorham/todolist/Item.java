package net.ngorham.todolist;

import java.util.Date;

/**
 * Created by NBG on 3/16/2018.
 */

public class Item {
    //Private variables
    private int id;
    private int noteId;
    private String name;
    private String createdOn;
    private String lastModified;
    private int strike = 0;
    private int position = 0;

    //Default constructor
    public Item(){
        this(0,"",null,null,0, 0, 0);
    }

    public Item(int id, String name, String createdOn, String lastModified, int noteId, int strike, int position){
        setId(id);
        setName(name);
        setCreatedOn(createdOn);
        setLastModified(lastModified);
        setNoteId(noteId);
        setStrike(strike);
        setPosition(position);
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCreatedOn(String createdOn){
        this.createdOn = createdOn;
    }

    public void setLastModified(String lastModified){
        this.lastModified = lastModified;
    }

    public void setNoteId(int noteId){
        this.noteId = noteId;
    }

    public void setStrike(int strike){
        this.strike = strike;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public int getId(){ return id; }

    public String getName(){ return name; }

    public String getCreatedOn(){ return createdOn; }

    public String  getLastModified(){ return lastModified; }

    public int getNoteId(){ return noteId; }

    public int getStrike(){ return strike; }

    public int getPosition(){ return position; }
}
