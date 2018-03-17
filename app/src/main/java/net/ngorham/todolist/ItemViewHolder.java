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

    //Constructor
    public ItemViewHolder(View v){
        super(v);
        this.nameLabel = v.findViewById(R.id.info_text);
    }

    //Return reference of TextView nameLabel
    public TextView getNameLabel(){ return nameLabel; }

    //Set reference of TextView nameLabel
    public void setNameLabel(TextView nameLabel){
        this.nameLabel = nameLabel;
    }
}
