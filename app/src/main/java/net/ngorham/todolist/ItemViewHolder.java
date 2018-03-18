package net.ngorham.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by NBG on 3/16/2018.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {
    //Private variables
    private TextView nameLabel;
    private TextView lastModifiedLabel;

    //Constructor
    public ItemViewHolder(View v){
        super(v);
        this.nameLabel = v.findViewById(R.id.name_text);
        this.lastModifiedLabel = v.findViewById(R.id.last_modified_text);
    }

    //Return reference of TextView nameLabel
    public TextView getNameLabel(){ return nameLabel; }

    //Return reference of TextView lastModifiedLabel
    public TextView getLastModifiedLabel(){ return lastModifiedLabel; }

    //Set reference of TextView nameLabel
    public void setNameLabel(TextView nameLabel){
        this.nameLabel = nameLabel;
    }
}
