package net.ngorham.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by NBG on 3/16/2018.
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
