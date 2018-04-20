package net.ngorham.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * To Do List
 * Note.java
 * Purpose: Provides access to RecyclerView.ViewHolder for Note option
 *
 * @author Neil Gorham
 * @version 1.1 04/19/2018
 *
 */

public class NoteViewHolder extends RecyclerView.ViewHolder {
    //Private variables
    private LinearLayout parent;
    private TextView nameLabel;
    private TextView lastModifiedLabel;

    //Constructor
    public NoteViewHolder(View v){
        super(v);
        this.parent = v.findViewById(R.id.root_layout);
        this.nameLabel = v.findViewById(R.id.name_text);
        this.lastModifiedLabel = v.findViewById(R.id.last_modified_text);
    }

    //Return reference of View parent
    public View getParent() { return parent; }

    //Return reference of TextView nameLabel
    public TextView getNameLabel(){ return nameLabel; }

    //Return reference of TextView lastModifiedLabel
    public TextView getLastModifiedLabel(){ return lastModifiedLabel; }
}
