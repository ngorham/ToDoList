package net.ngorham.todolist;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A simple {@link ListFragment} subclass.
 */
public class TopFragment extends ListFragment {

    interface TopListener {
        void itemClicked(long id);
    }

    //Private Variables
    private TopListener listener;

    public TopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Dummy list for testing
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.top_frag));
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    //Attach listFragment listener to current activity
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.listener = (TopListener)activity;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int pos, long id){
        if(listener != null){
            listener.itemClicked(id);
        }
    }

}
