package net.ngorham.todolist;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;

import java.util.List;

public class MainActivity extends Activity {
    //Private variables
    private int listId = 0;
    //Dummy text for testing
    private String[] todos = {"Test List 1", "Test List 2"};
    //Recycler View variables
    private RecyclerView todoRecycler;
    private ToDoListAdapter todoAdapter;
    private RecyclerView.LayoutManager todoLayoutManager;
    //Db variables
    private ToDoListDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set up DAO
        dao = new ToDoListDAO(this);
        //Set up recycler view
        todoRecycler = (RecyclerView)findViewById(R.id.todo_recycler);
        //Set up Layout Manager
        todoLayoutManager = new LinearLayoutManager(this);
        todoRecycler.setLayoutManager(todoLayoutManager);
        //Db call and close
        final List<Note> notes = dao.fetchAllNotes();
        dao.close();
        //Set up Adapter
        todoAdapter = new ToDoListAdapter(notes);
        todoRecycler.setAdapter(todoAdapter);
        //Set up on click listener
        todoAdapter.setListener(new ToDoListAdapter.Listener(){
            @Override
            public void onClick(int position){
                int id = notes.get(position).getId();
                String name = notes.get(position).getName();
                //Start activity of list clicked
                Intent intent = new Intent(getApplicationContext(), ListDetailActivity.class);
                intent.putExtra(ListDetailActivity.EXTRA_LIST_ID, id);
                intent.putExtra("NAME", name);
                startActivity(intent);
            }
        });

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Call when user clicks an item in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Handle action items
        switch(item.getItemId()){
            case R.id.add_list:
                //Add list action
                return true;
            case R.id.app_settings:
                //Settings action
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
