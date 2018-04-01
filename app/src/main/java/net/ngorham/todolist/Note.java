package net.ngorham.todolist;

/**
 * To Do List
 * Note.java
 * Purpose: Provides local storage and access of a Note (list)
 *
 * @author Neil Gorham
 * @version 1.0 03/14/2018
 */

public class Note {
    //Private variables
    private int id;
    private String name;
    private String createdOn;
    private String lastModified;

    //Default constructor
    public Note(){
        this(0, "", "", "");
    }
    //Constructor
    public Note(int id, String name, String createdOn, String lastModified){
        setName(name);
        setId(id);
        setCreatedOn(createdOn);
        setLastModified(lastModified);
    }

    public void setName(String name){ this.name = name; }

    public void setId(int id){ this.id = id; }

    public void setCreatedOn(String createdOn){ this.createdOn = createdOn; }

    public void setLastModified(String lastModified){
        this.lastModified = lastModified;
    }

    public String getName(){ return name; }

    public int getId(){ return id; }

    public String getCreatedOn(){ return createdOn; }

    public String getLastModified(){ return lastModified; }
}
