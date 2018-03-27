package net.ngorham.todolist;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by NBG on 3/14/2018.
 *
 */

public class ToDoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Private variables
    private Listener listener;
    private ArrayList<Item> items;
    private ArrayList<Note> notes;
    private int editType = 0;
    //Private constants
    private final int NOTE_TYPE = 0;
    private final int ITEM_TYPE = 1;
    private final int EDIT_ITEM_TYPE = 2;
    private final int ADD_ITEM_TYPE = 3;

    public interface Listener {
        void onClick(View view, int position);
        void deleteItem(View view, int position);
    }

    //Constructor with List<Item>
    public ToDoListAdapter(ArrayList<Item> items, int editType){
        this.items = items;
        this.editType = editType;
    }

    //Constructor with List<Note>
    public ToDoListAdapter(int editType, ArrayList<Note> notes){
        this.notes = notes;
        this.editType = editType;
    }

    //Set onClick listener
    public void setListener(Listener listener){
        this.listener = listener;
    }

    //Configure Note type item
    private void configureNote(NoteViewHolder holder, final int position){
        Note note = notes.get(position);
        if(note != null){
            holder.getNameLabel().setText(note.getName());
        }
        holder.getNameLabel().setOnClickListener(new View.OnClickListener(){
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
        if(item != null){
            holder.getNameLabel().setText(item.getName());
            if(item.getStrike() == 1){
                holder.getNameLabel().setPaintFlags(
                        holder.getNameLabel().getPaintFlags()
                                | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.getNameLabel().setPaintFlags(0);
            }
            //SimpleDateFormat dateFormat =
            //        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            //String lastModifiedDate = dateFormat.format(item.getLastModified());
            holder.getLastModifiedLabel().setText(item.getLastModified());
        }
        holder.getNameLabel().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //If view is clicked, call onClick
                if(listener != null){
                    listener.onClick(v, position);
                }
            }
        });
    }

    //Configure AddItem type item
    private void configureAddItem(AddItemViewHolder holder, final int position){
        Item item = items.get(position);
        if(item != null){
            holder.getTextLabel().setText(item.getName());
        }
        holder.getTextLabel().setOnClickListener(new View.OnClickListener(){
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
        Item item = (Item)items.get(position);
        if(item != null){
            holder.getNameLabel().setText(item.getName());
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
        if(viewType == NOTE_TYPE){
            v = inflater.inflate(R.layout.note_view, parent, false);
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
}
