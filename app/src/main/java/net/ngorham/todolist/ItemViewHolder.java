package net.ngorham.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * To Do List
 * ItemViewHolder.java
 * Purpose: Provides access to RecyclerView.ViewHolder for Item option
 *
 * @author Neil Gorham
 * @version 1.1 04/20/2018
 *
 * 1.1: Added parent, optionsButton, checkImage references
 * Removed lastModifiedLabel reference
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {
    //Private variables
    private View parent;
    private TextView nameLabel;
    private ImageButton optionsButton;
    private ImageView checkImage;

    //Constructor
    public ItemViewHolder(View v){
        super(v);
        this.parent = v;
        this.nameLabel = v.findViewById(R.id.name_text);
        this.optionsButton = v.findViewById(R.id.more_vert);
        this.checkImage = v.findViewById(R.id.check_mark);

    }

    //Return reference to View parent
    public View getParent(){ return parent; }

    //Return reference of TextView nameLabel
    public TextView getNameLabel(){ return nameLabel; }

    //Return reference of ImageButton optionsButton
    public ImageButton getOptionsButton(){ return optionsButton; }

    //Return reference of ImageView checkImage
    public ImageView getCheckImage(){ return checkImage; }
}
