package net.ngorham.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * To Do List
 * ToDoListAdapter.java
 * Purpose: Displays list of lists or list of items in a RecyclerView
 *
 * @author Neil Gorham
 * @version 1.1 04/19/2018
 *
 * 1.1: Added Note view constants for setting up NoteViewHolder instances,
 * Note view logic that determines which child views to display,
 * listItems container for list items,
 * itemOptions listener method for future dialog options
 */

public class ToDoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Private variables
    private Context context;
    private Listener listener;
    private ArrayList<Item> items;
    private ArrayList<Note> notes;
    private ArrayList<ArrayList<Item>> listItems;
    private int editType = 0;
    //SharedPreferences variables
    private SharedPreferences sharedPrefs;
    private boolean switchTheme;
    private int layoutManager;
    //Private constants
    private final int NOTE_TYPE = 0;
    private final int ITEM_TYPE = 1;
    private final int EDIT_ITEM_TYPE = 2;
    private final int ADD_ITEM_TYPE = 3;
    private final int NOTE_LIST_VIEW = 0;
    private final int NOTE_DETAILS_VIEW = 1;
    private final int NOTE_GRID_VIEW = 2;
    private final int NOTE_LARGE_GRID_VIEW = 3;

    public interface Listener {
        void onClick(View view, int position);
        void deleteItem(View view, int position);
        void itemOptions(View view, int position);
    }

    //Constructor with List<Item>
    public ToDoListAdapter(ArrayList<Item> items, int editType, Context context){
        this.context = context;
        this.items = items;
        this.editType = editType;
    }

    //Constructor with List<Note>
    public ToDoListAdapter(int editType, ArrayList<Note> notes,
                           ArrayList<ArrayList<Item>> listItems, Context context){
        this.context = context;
        this.notes = notes;
        this.listItems = listItems;
        this.editType = editType;
    }

    //Set onClick listener
    public void setListener(Listener listener){
        this.listener = listener;
    }

    //Configure Note type item
    private void configureNote(NoteViewHolder holder, final int position){
        Note note = notes.get(position);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        layoutManager = sharedPrefs.getInt("layout_manager", 0);
        if(note != null){
            holder.getNameLabel().setText(note.getName());
            if(layoutManager == NOTE_LIST_VIEW || layoutManager == NOTE_DETAILS_VIEW){
                String lastModifiedDate = getMonthDay(note.getLastModified());
                holder.getLastModifiedLabel().setText(lastModifiedDate);
            }
            if(layoutManager == NOTE_DETAILS_VIEW ||
                    (layoutManager == NOTE_GRID_VIEW || layoutManager == NOTE_LARGE_GRID_VIEW)) {
                for(int i = 0; i < listItems.get(position).size(); i++){
                    Item curItem = listItems.get(position).get(i);
                    TextView tV = new TextView(context);
                    tV.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    if(curItem.getStrike() == 1){
                        tV.setPaintFlags(tV.getPaintFlags()
                                | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        tV.setPaintFlags(0);
                    }
                    if(i == 3 && i < listItems.get(position).size() - 1){
                        String builder = curItem.getName() + "...";
                        tV.setText(builder);
                        ((LinearLayout)holder.getParent()).addView(tV);
                        break;
                    } else {
                        tV.setText(curItem.getName());
                        ((LinearLayout)holder.getParent()).addView(tV);
                    }
                }
            }
        }
        holder.getParent().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //If view is clicked, call onClick
                if(listener != null){
                    listener.onClick(v, position);
                }
            }
        });
    }

    //Configure Item type item
    private void configureItem(ItemViewHolder holder, final int position){
        Item item = items.get(position);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        switchTheme = sharedPrefs.getBoolean("switch_theme", false);
        if(item != null){
            holder.getNameLabel().setText(item.getName());
            if(item.getStrike() == 1){
                holder.getNameLabel().setPaintFlags(
                        holder.getNameLabel().getPaintFlags()
                                | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.getCheckImage().setVisibility(View.VISIBLE);
                holder.getOptionsButton().setVisibility(View.GONE);
            } else {
                holder.getNameLabel().setPaintFlags(0);
                holder.getCheckImage().setVisibility(View.GONE);
                holder.getOptionsButton().setVisibility(View.VISIBLE);
            }
            //SimpleDateFormat dateFormat =
            //        new SimpleDateFormat("MMM dd", Locale.getDefault());
            //Date date = new Date(
            //String lastModifiedDate = getMonthDay(item.getLastModified());
            //holder.getLastModifiedLabel().setText(lastModifiedDate);
            if(switchTheme){
                holder.getNameLabel().setTextColor(context.getResources().getColor(R.color.textSecondaryLight));
                //holder.getLastModifiedLabel().setTextColor(context.getResources().getColor(R.color.colorAccentLight));
            } else {
                holder.getNameLabel().setTextColor(context.getResources().getColor(R.color.textSecondaryDark));
                //holder.getLastModifiedLabel().setTextColor(context.getResources().getColor(R.color.colorAccentDark));
            }
        }
        holder.getParent().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //If view is clicked, call onClick
                if(listener != null){
                    listener.onClick(v, position);
                }
            }
        });
        holder.getOptionsButton().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(listener != null){
                    listener.itemOptions(v, position);
                }
            }
        });
    }

    //Configure AddItem type item
    private void configureAddItem(AddItemViewHolder holder, final int position){
        Item item = items.get(position);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        switchTheme = sharedPrefs.getBoolean("switch_theme", false);
        if(item != null){
            holder.getTextLabel().setText(item.getName());
            if(switchTheme){
                holder.getTextLabel().setTextColor(context.getResources().getColor(R.color.textSecondaryLight));
            } else {
                holder.getTextLabel().setTextColor(context.getResources().getColor(R.color.textSecondaryDark));
            }
        }
        holder.getParent().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(listener != null) {
                    listener.onClick(v, position);
                }
            }
        });
    }

    //Configure EditItem type item
    private void configureEditItem(EditItemViewHolder holder, final int position){
        Item item = items.get(position);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        switchTheme = sharedPrefs.getBoolean("switch_theme", false);
        if(item != null){
            holder.getNameLabel().setText(item.getName());
            if(switchTheme){
                holder.getNameLabel().setTextColor(context.getResources().getColor(R.color.textSecondaryLight));
            } else {
                holder.getNameLabel().setTextColor(context.getResources().getColor(R.color.textSecondaryDark));
            }
            if(item.getStrike() == 1){
                holder.getNameLabel().setPaintFlags(
                        holder.getNameLabel().getPaintFlags()
                                | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.getNameLabel().setPaintFlags(0);
            }
        }
        holder.getNameLabel().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(listener != null) {
                    listener.onClick(v, position);
                }
            }
        });
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(listener != null){
                    listener.deleteItem(v, position);
                }
            }
        });
    }

    //Return the item view type
    @Override
    public int getItemViewType(int position){
        if(items != null){ //items
            if(items.get(position).getName().equals("Add Item")){
                return ADD_ITEM_TYPE;
            } else {
                if (editType == 0) {
                    return ITEM_TYPE;
                } else {
                    return EDIT_ITEM_TYPE;
                }
            }
        } else if(notes != null){ //notes
            return NOTE_TYPE;
        }
        return -1;
    }

    //Create viewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        layoutManager = sharedPrefs.getInt("layout_manager", 0);
        int[] noteViews = new int[] {
                R.layout.note_list_view,
                R.layout.note_details_view,
                R.layout.note_grid_view,
                R.layout.note_grid_view
        };
        if(viewType == NOTE_TYPE){
            v = inflater.inflate(noteViews[layoutManager], parent, false);
            return new NoteViewHolder(v);
        } else if(viewType == ITEM_TYPE){
            v = inflater.inflate(R.layout.item_view, parent, false);
            return new ItemViewHolder(v);
        } else if(viewType == ADD_ITEM_TYPE){
            v = inflater.inflate(R.layout.add_item_view, parent, false);
            return new AddItemViewHolder(v);
        } else if(viewType == EDIT_ITEM_TYPE){
            v = inflater.inflate(R.layout.edit_item_view, parent, false);
            return new EditItemViewHolder(v);
        }
        return null;
    }

    //Set data inside viewHolder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position){
        switch(holder.getItemViewType()){
            case NOTE_TYPE:
                NoteViewHolder nvh = (NoteViewHolder)holder;
                configureNote(nvh, position);
                break;
            case ITEM_TYPE:
                ItemViewHolder ivh = (ItemViewHolder)holder;
                configureItem(ivh, position);
                break;
            case ADD_ITEM_TYPE:
                AddItemViewHolder aivh = (AddItemViewHolder)holder;
                configureAddItem(aivh, position);
                break;
            case EDIT_ITEM_TYPE:
                EditItemViewHolder eivh = (EditItemViewHolder)holder;
                configureEditItem(eivh, position);
                break;
        }
    }

    //Return number of items in the data set
    @Override
    public int getItemCount(){
        //Return number of items in the data set
        if(items != null) {
            return items.size();
        } else if(notes != null){
            return notes.size();
        } else {
            return 0;
        }
    }

    //Replace current item list with new or updated item list
    public void setItemList(ArrayList<Item> items){ this.items = items; }

    public void setNoteList(ArrayList<Note> notes){ this.notes = notes; }

    //Return reference to List<Item> items
    public ArrayList<Item> getItemList(){ return items; }

    public ArrayList<Note> getNoteList(){ return  notes; }

    //Utility for displaying Month and Day of item's lastModified
    private String getMonthDay(String date){
        String month = "";
        String monthSubstr = date.substring(5, 7);
        String day = date.substring(8, 10);
        if(monthSubstr.equals("01")){
            month = "Jan";
        } else if(monthSubstr.equals("02")){
            month = "Feb";
        } else if(monthSubstr.equals("03")){
            month = "Mar";
        } else if(monthSubstr.equals("04")){
            month = "Apr";
        } else if(monthSubstr.equals("05")){
            month = "May";
        } else if(monthSubstr.equals("06")){
            month = "Jun";
        } else if(monthSubstr.equals("07")){
            month = "Jul";
        } else if(monthSubstr.equals("08")){
            month = "Aug";
        } else if(monthSubstr.equals("09")){
            month = "Sep";
        } else if(monthSubstr.equals("10")){
            month = "Oct";
        } else if(monthSubstr.equals("11")){
            month = "Nov";
        } else if(monthSubstr.equals("12")){
            month = "Dec";
        }
        return (month + " " + day);
    }
}
