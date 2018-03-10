package net.ngorham.todolist;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ListDetailActivity extends Activity {
    //Public constants
    public static final String EXTRA_LIST_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
        //Populate content frame with fragment
        ListDetailFragment frag = (ListDetailFragment)
                getFragmentManager().findFragmentById(R.id.content_frame);
        int listId = (int)getIntent().getExtras().get(EXTRA_LIST_ID);
        frag.setListId(listId);
        /*Fragment frag;
        frag = new ListDetailFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, frag, "visible_frag");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();*/
    }
}
