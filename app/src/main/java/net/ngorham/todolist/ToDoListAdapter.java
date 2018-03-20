package net.ngorham.todolist;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
    private final int ADD_ITEM_TYPE = 2;

    public interface Listener {
        void onClick(View view, int position);
    }

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
                    listener.onClick(v, position);
                }
            }
        });
    }

    //Configure Item type item
    private void configureItem(ItemViewHolder holder, final int position){
        Item item = (Item)items.get(position);
        if(item != null){
            holder.getNameLabel().setText(item.getName());
            if(item.getStrike() == 1){
                holder.getNameLabel().setPaintFlags(
                        holder.getNameLabel().getPaintFlags()
                                | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.getNameLabel().setPaintFlags(0);
            }
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String lastModifiedDate = dateFormat.format(item.getLastModified());
            holder.getLastModifiedLabel().setText(lastModifiedDate);
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
        AddItem item = (AddItem)items.get(position);
        if(item != null){
            holder.getTextLabel().setText(item.getText());
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

    //Return the item view type
    @Override
    public int getItemViewType(int position){
        if(items.get(position) instanceof  Note){
            return NOTE_TYPE;
        } else if(items.get(position) instanceof Item){
            return ITEM_TYPE;
        } else if(items.get(position) instanceof AddItem){
            return ADD_ITEM_TYPE;
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
        }
    }

    //Return number of items in the data set
    @Override
    public int getItemCount(){
        //Return number of items in the data set
        return items.size();
    }
}
