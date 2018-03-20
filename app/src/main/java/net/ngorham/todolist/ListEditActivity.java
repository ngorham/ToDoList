package net.ngorham.todolist;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ListEditActivity extends Activity {
    //Public constants
    public static final String EXTRA_LIST_ID = "id";

    //Private variables
    int listId;
    String listName;
    private ActionBar actionBar;
    //Recycler View variables
    private RecyclerView todoRecycler;
    private ToDoListAdapter todoAdapter;
    private RecyclerView.LayoutManager todoLayoutManager;
    //Db variables
    private ToDoListDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_edit);
        //Set up recycler view
        todoRecycler = findViewById(R.id.todo_recycler);
        //Set up Layout Manager
        todoLayoutManager = new LinearLayoutManager(this);
        todoRecycler.setLayoutManager(todoLayoutManager);
        final List<Object> items;
        //Store data received from intent
        listId = (int)getIntent().getExtras().get(EXTRA_LIST_ID);
        if(listId > 0) { //Edit existing list
            listName = getIntent().getStringExtra("NAME");
            //Set up DAO
            dao = new ToDoListDAO(this);
            //DB call and close
            items = dao.fetchAllItems(listId);
            dao.close();
        } else { //Create new list
            listName = "";
            items = new ArrayList<>();
        }
        //Add AddItem options to top and bottom of recyclerView
        items.add(0, new AddItem());
        items.add(items.size(), new AddItem());
        //Create Adapter
        todoAdapter = new ToDoListAdapter(items);
        //Set Adapter
        todoRecycler.setAdapter(todoAdapter);
        //Set up onClick listener
        todoAdapter.setListener(new ToDoListAdapter.Listener(){
            @Override
            public void onClick(View view, int position){

            }
        });
        //Add divider item decoration
        Drawable divider = ContextCompat.getDrawable(this, R.drawable.divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new ToDoListDivider(divider);
        todoRecycler.addItemDecoration(dividerItemDecoration);
        //Get ActionBar reference
        actionBar = getActionBar();
        //Set EditText view in actionBar
        actionBar.setCustomView(R.layout.text_field);
        EditText listNameField = (EditText)actionBar.getCustomView()
                .findViewById(R.id.field);
        listNameField.setText(listName, TextView.BufferType.EDITABLE);
        //Set EditText listener
        listNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Toast.makeText(getApplicationContext(), "EditText action listener called", Toast.LENGTH_LONG).show();
                //Check for empty string in EditText
                return false;
            }
        });
        actionBar.setDisplayOptions(getActionBar().DISPLAY_SHOW_CUSTOM);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    //Displays Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Inflate menu, add items to action bar
        getMenuInflater().inflate(R.menu.menu_list_edit, menu);
        //Display icons and text in overflow menu
        //code found on stackoverflow
        //https://stackoverflow.com/questions/18374183/how-to-show-icons-in-overflow-menu-in-actionbar
        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
            try{
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            }
            catch(NoSuchMethodException e){
                Log.e("onCreateOptionsMenu", "Did not attach icons", e);
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    //Call when user clicks an item in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Handle action items
        switch(item.getItemId()){
            case R.id.delete_list:
                //Add list action
                Toast.makeText(this, "Delete list action", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.app_settings:
                //Settings action
                Toast.makeText(this, "Settings action", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Called when invalidateOptionsMenu() is called
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        return super.onPrepareOptionsMenu(menu);
    }
}
