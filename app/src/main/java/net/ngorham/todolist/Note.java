package net.ngorham.todolist;

/**
 * Created by NBG on 3/14/2018.
 */

public class Note {
    //Private variables
    private int id;
    private String name;

    //Default constructor
    public Note(){
        this(0, "");
    }
    //Constructor
    public Note(int id, String name){
        setName(name);
        setId(id);
    }

    public void setName(String name){
        this.name = name;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public int getId(){
        return id;
    }
}
