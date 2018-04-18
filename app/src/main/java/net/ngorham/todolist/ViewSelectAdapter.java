package net.ngorham.todolist;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViewSelectAdapter extends ArrayAdapter<ViewSelectOption> {
    private final Context CONTEXT;
    private List<ViewSelectOption> viewSelectOptions;

    public ViewSelectAdapter(Context context, ArrayList<ViewSelectOption> viewSelectOptions){
        super(context, R.layout.view_select_option, viewSelectOptions);
        this.CONTEXT = context;
        this.viewSelectOptions = viewSelectOptions;


    }

    public View getView(ViewGroup parent, View view, int position){
        View option = view;
        if(option == null){
            option = LayoutInflater.from(CONTEXT).inflate(R.layout.view_select_option, parent, false);
        }
        ViewSelectOption currentOption = viewSelectOptions.get(position);
        TextView optionText = option.findViewById(R.id.view_select_item);
        ImageView optionIcon = option.findViewById(R.id.view_select_icon);
        optionText.setText(currentOption.getOption());
        optionIcon.setImageResource(currentOption.getIcon());
        return view;
    }
}
