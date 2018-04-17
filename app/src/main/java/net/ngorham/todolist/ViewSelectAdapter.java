package net.ngorham.todolist;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewSelectAdapter extends ArrayAdapter<String> {
    private final Activity CONTEXT;
    private final String[] ITEMNAMES;
    private final Integer[] ICONNAMES;

    public ViewSelectAdapter(Activity context, String[] itemNames, Integer[] iconNames){
        super(context, R.layout.view_select_items, itemNames);
        this.CONTEXT = context;
        this.ITEMNAMES = itemNames;
        this.ICONNAMES = iconNames;
    }

    public View getView(ViewGroup parent, View view, int position){
        view = CONTEXT.getLayoutInflater().inflate(R.layout.view_select_items, null, false);
        TextView viewSelectItem = view.findViewById(R.id.view_select_item);
        viewSelectItem.setText(ITEMNAMES[position]);
        Drawable d = CONTEXT.getResources().getDrawable(ICONNAMES[position]);
        //int h = d.getIntrinsicHeight();
        //int w = d.getIntrinsicWidth();
        //d.setBounds(0,0,w, h);
        viewSelectItem.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
        //ImageView viewSelectIcon = v.findViewById(R.id.view_select_icon);
        //viewSelectIcon.setImageResource(ICONNAMES[position]);
        return view;
    }
}
