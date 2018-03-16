package net.ngorham.todolist;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListDetailFragment extends Fragment {
    //Private variables
    private long listId;
    private String listName;

    public ListDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null){
            listId = savedInstanceState.getLong("listId");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_detail, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView();
        if(view != null){
            TextView test = view.findViewById(R.id.test_view);
            String test_id = "list id: " + Long.toString(listId);
            String test_name = "\nname: " + listName;
            String text = test_id + test_name;
            test.setText(text);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putLong("listId", listId);
    }

    //Set list ID
    public void setListId(long id){
        this.listId = id;
    }
    public void setListName(String name){ this.listName = name; }
}
