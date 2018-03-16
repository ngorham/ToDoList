package net.ngorham.todolist;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by NBG on 3/14/2018.
 *
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {
    //Private variables
    private Listener listener;
    private List<Object> items;
    //Private constants
    private final int NOTE_TYPE = 0;
    private final int ITEM_TYPE = 1;

    public interface Listener { void onClick(int position); }

    //Inner classes
    //Provide a reference to the views used in recycler view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Define holder
        private CardView cardView;
        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    /*
    //Provide a reference to the item view used in recycler view
    private static class ItemViewHolder extends  RecyclerView.ViewHolder {
        //Define holder
        private View view;
        public ItemViewHolder(View v){
            super(v);
            view = v;
        }
    }*/

    //Constructor with List parameter
    public ToDoListAdapter(List<Object> items){
        this.items = items;
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

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
    public ToDoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardView cv = (CardView)inflater.inflate(R.layout.card_todo,
                parent, false);
        return new ViewHolder(cv);
    }

    //Set data inside viewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        CardView cardView = holder.cardView;
        TextView textView = (TextView)cardView.findViewById(R.id.info_text);
        Note note = (Note)items.get(position);
        textView.setText(note.getName());
        //Set onClickListener to adapter
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //If view is clicked, call onClick
                if(listener != null){
                    listener.onClick(position);
                }
            }
        });
    }

    //Return number of items in the data set
    @Override
    public int getItemCount(){
        //Return number of items in the data set
        return items.size();
    }
}
