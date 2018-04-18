package net.ngorham.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewSelectAdapter extends ArrayAdapter<ViewSelectOption> {
    private final Context CONTEXT;
    private ArrayList<ViewSelectOption> viewSelectOptions;

    private static class ViewHolder {
        TextView optionText;
        ImageView optionIcon;
    }

    public ViewSelectAdapter(Context context, ArrayList<ViewSelectOption> viewSelectOptions){
        super(context, R.layout.view_select_option, viewSelectOptions);
        this.CONTEXT = context;
        this.viewSelectOptions = viewSelectOptions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewSelectOption currentOption = viewSelectOptions.get(position);
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(CONTEXT);
            convertView = inflater.inflate(R.layout.view_select_option, parent, false);
            viewHolder.optionText = convertView.findViewById(R.id.view_select_item);
            viewHolder.optionIcon = convertView.findViewById(R.id.view_select_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.optionText.setText(currentOption.getOption());
        viewHolder.optionIcon.setImageResource(currentOption.getIcon());
        viewHolder.optionIcon.setTag(position);
        return convertView;
    }
}
