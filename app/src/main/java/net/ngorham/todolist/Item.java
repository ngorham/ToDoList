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
    private Date createdOn;
    private Date lastModified;

    //Default constructor
    public Item(){
        this(0,"",null,null,0);
    }

    public Item(int id, String name, Date createdOn, Date lastModified, int noteId){
        setId(id);
        setName(name);
        setCreatedOn(createdOn);
        setLastModified(lastModified);
        setNoteId(noteId);
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCreatedOn(Date createdOn){
        this.createdOn = createdOn;
    }

    public void setLastModified(Date lastModified){
        this.lastModified = lastModified;
    }

    public void setNoteId(int noteId){
        this.noteId = noteId;
    }

    public int getId(){ return id; }

    public String getName(){ return name; }

    public Date getCreatedOn(){ return createdOn; }

    public Date getLastModified(){ return lastModified; }

    public int getNoteId(){ return noteId; }
}