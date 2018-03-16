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
    private String[] todos;
    private Listener listener;
    private List<Note> notes;

    public interface Listener { void onClick(int position); }

    //Inner classes
    //Provide a reference to the views used in recycler view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Define holder
        private CardView cardView;
        public ViewHolder(CardView view){
            super(view); //call super constructor
            cardView = view;
        }
    }

    //Constructor with List parameter
    public ToDoListAdapter(List<Note> notes){
        this.notes = notes;
    }

    //Constructor
    public ToDoListAdapter(String[] todos){
        this.todos = todos;
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    //Create viewHolder
    @Override
    public ToDoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //Create new view
        CardView cv = (CardView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_todo, parent, false);
        return new ViewHolder(cv);
    }

    //Set data inside viewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        CardView cardView = holder.cardView;
        TextView textView = (TextView)cardView.findViewById(R.id.info_text);
        textView.setText(notes.get(position).getName());
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
        return notes.size();
    }
}
