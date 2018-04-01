package net.ngorham.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * To Do List
 * Note.java
 * Purpose: Provides access to RecyclerView.ViewHolder for Note option
 *
 * @author Neil Gorham
 * @version 1.0 03/16/2018
 */

public class NoteViewHolder extends RecyclerView.ViewHolder {
    //Private variables
    private TextView nameLabel;

    //Constructor
    public NoteViewHolder(View v){
        super(v);
        nameLabel = v.findViewById(R.id.info_text);
    }

    //Return reference of TextView nameLabel
    public TextView getNameLabel(){ return nameLabel; }
}
