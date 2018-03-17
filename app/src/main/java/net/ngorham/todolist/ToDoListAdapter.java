package net.ngorham.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by NBG on 3/14/2018.
 *
 */

public class ToDoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Private variables
    private Listener listener;
    private List<Object> items;
    //Private constants
    private final int NOTE_TYPE = 0;
    private final int ITEM_TYPE = 1;

    public interface Listener { void onClick(int position); }

    //Constructor with List parameter
    public ToDoListAdapter(List<Object> items){
        this.items = items;
    }

    //Set onClick listener
    public void setListener(Listener listener){
        this.listener = listener;
    }

    //Configure Note type item
    private void configureNote(NoteViewHolder holder, final int position){
        Note note = (Note)items.get(position);
        if(note != null){
            holder.getNameLabel().setText(note.getName());
        }
        holder.getNameLabel().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //If view is clicked, call onClick
                if(listener != null){
                    listener.onClick(position);
                }
            }
        });
    }

    //Configure Item type item
    private void configureItem(ItemViewHolder holder, final int position){
        Item item = (Item)items.get(position);
        if(item != null){
            holder.getNameLabel().setText(item.getName());
        }
        holder.getNameLabel().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //If view is clicked, call onClick
                if(listener != null){
                    listener.onClick(position);
                }
            }
        });
    }

    //Return the item view type
    @Override
    public int getItemViewType(int position){
        if(items.get(position) instanceof  Note){
            return NOTE_TYPE;
        } else if(items.get(position) instanceof Item){
            return ITEM_TYPE;
        }
        return -1;
    }

    //Create viewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == NOTE_TYPE){
            View cv = inflater.inflate(R.layout.card_todo,
                    parent, false);
            return new NoteViewHolder(cv);
        } else if(viewType == ITEM_TYPE){
            View v = inflater.inflate(R.layout.card_todo,
                    parent, false);
            return new ItemViewHolder(v);
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
        }
    }

    //Return number of items in the data set
    @Override
    public int getItemCount(){
        //Return number of items in the data set
        return items.size();
    }
}
