package net.ngorham.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by NBG on 3/19/2018.
 */

public class AddItemViewHolder extends RecyclerView.ViewHolder {
    //Private variables
    private View parent;
    private TextView textLabel;

    //Constructor
    public AddItemViewHolder(View v){
        super(v);
        this.parent = v;
        this.textLabel = v.findViewById(R.id.add_item_text);
    }

    //Return reference to View parent
    public View getParent(){ return parent; }

    //Return reference of TextView textLabel
    public TextView getTextLabel(){ return textLabel; }
}
