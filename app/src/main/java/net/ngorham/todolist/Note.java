package net.ngorham.todolist;

/**
 * Created by NBG on 3/14/2018.
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
