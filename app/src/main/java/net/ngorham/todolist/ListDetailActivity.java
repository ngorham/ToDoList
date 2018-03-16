package net.ngorham.todolist;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ListDetailActivity extends Activity {
    //Public constants
    public static final String EXTRA_LIST_ID = "id";

    //Private variables
    //Recycler View variables
    private RecyclerView todoRecycler;
    private ToDoListAdapter todoAdapter;
    private RecyclerView.LayoutManager todoLayoutManager;
    //Db variables
    private ToDoListDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
        //Set up DAO
        dao = new ToDoListDAO(this);
        //Set up recycler view
        todoRecycler = (RecyclerView)findViewById(R.id.todo_recycler);
        //Set up Layout Manager
        todoLayoutManager = new LinearLayoutManager(this);
        todoRecycler.setLayoutManager(todoLayoutManager);

        //Populate content frame with fragment
        ListDetailFragment frag = (ListDetailFragment)
                getFragmentManager().findFragmentById(R.id.content_frame);
        int listId = (int)getIntent().getExtras().get(EXTRA_LIST_ID);
        String listName = (String)getIntent().getStringExtra("NAME");
        frag.setListId(listId);
        frag.setListName(listName);
        /*Fragment frag;
        frag = new ListDetailFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, frag, "visible_frag");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();*/
    }
}
