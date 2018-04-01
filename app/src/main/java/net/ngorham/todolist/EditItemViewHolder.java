package net.ngorham.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * To Do List
 * EditItemViewHolder.java
 * Purpose: Provides access to RecyclerView.ViewHolder for Edit Item option
 *
 * @author Neil Gorham
 * @version 1.0 03/20/2018
 */

public class EditItemViewHolder extends RecyclerView.ViewHolder {
    //Private variables
    private TextView nameLabel;
    private ImageButton deleteButton;

    //Constructor
    public EditItemViewHolder(View v){
        super(v);
        this.nameLabel = v.findViewById(R.id.name_text);
        this.deleteButton = v.findViewById(R.id.delete_button);
    }

    //Return reference of TextView nameLabel
    public TextView getNameLabel(){ return nameLabel; }

    //Return reference to ImageButton deleteButton
    public ImageButton getDeleteButton(){ return  deleteButton; }
}
