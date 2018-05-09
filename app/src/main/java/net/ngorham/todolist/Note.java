package net.ngorham.todolist;

/**
 * To Do List
 * Note.java
 * Purpose: Provides local storage and access of a Note (list)
 *
 * @author Neil Gorham
 * @version 1.1 05/04/2018
 *
 * 1.1: Added boolean reminder and string reminderTime
 */

public class Note {
    //Private variables
    private int id;
    private String name;
    private String createdOn;
    private String lastModified;
    private int reminder;
    private String reminderTime;

    //Default constructor
    public Note(){
        this(0, "", "", "", 0, "");
    }
    //Constructor
    public Note(int id, String name, String createdOn, String lastModified, int reminder, String reminderTime){
        setName(name);
        setId(id);
        setCreatedOn(createdOn);
        setLastModified(lastModified);
        setReminder(reminder);
        setReminderTime(reminderTime);
    }

    public void setName(String name){ this.name = name; }

    public void setId(int id){ this.id = id; }

    public void setCreatedOn(String createdOn){ this.createdOn = createdOn; }

    public void setLastModified(String lastModified){ this.lastModified = lastModified; }

    public void setReminder(int reminder){ this.reminder = reminder; }

    public void setReminderTime(String reminderTime){ this.reminderTime = reminderTime; }

    public String getName(){ return name; }

    public int getId(){ return id; }

    public String getCreatedOn(){ return createdOn; }

    public String getLastModified(){ return lastModified; }

    public int getReminder(){ return reminder; }

    public String getReminderTime(){ return reminderTime; }
}
