package net.ngorham.todolist;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.List;

public class ListDetailActivity extends Activity {
    //Public constants
    public static final String EXTRA_LIST_ID = "id";

    //Private variables
    int listId;
    String listName;
    //Recycler View variables
    private RecyclerView todoRecycler;
    private ToDoListAdapter todoAdapter;
    private RecyclerView.LayoutManager todoLayoutManager;
    //Db variables
    private ToDoListDAO dao;

    //Inner classes
    //Update Item strike value
    private class UpdateItemStrikeTask extends AsyncTask<Item, Void, Boolean>{
        protected void onPreExecute(){}
        protected Boolean doInBackground(Item... items){
            Item item = items[0];
            return dao.updateStrike(item);
        }
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(ListDetailActivity.this,
                        "Database unavailable, failed to update strike in ITEM table",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
        //Set up recycler view
        todoRecycler = findViewById(R.id.todo_recycler);
        //Set up Layout Manager
        todoLayoutManager = new LinearLayoutManager(this);
        todoRecycler.setLayoutManager(todoLayoutManager);
        //Store data received from intent
        listId = (int)getIntent().getExtras().get(EXTRA_LIST_ID);
        if(listId > 0) {
            listName = getIntent().getStringExtra("NAME");
        } else {
            listName = "";
        }
        //Set ActionBar title
        getActionBar().setTitle(listName);
        //Set up DAO
        dao = new ToDoListDAO(this);
        //DB call and close
        final List<Object> items = dao.fetchAllItems(listId);
        //Set up Adapter
        todoAdapter = new ToDoListAdapter(items);
        todoRecycler.setAdapter(todoAdapter);
        //Set up onClick listener
        todoAdapter.setListener(new ToDoListAdapter.Listener(){
            @Override
            public void onClick(View view, int position){
                TextView textView = (TextView)view;
                Item item = (Item)items.get(position);
                if(item.getStrike() == 0){
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    item.setStrike(1);
                } else {
                    //Remove strikethrough
                    textView.setPaintFlags(0);
                    item.setStrike(0);
                }
                new UpdateItemStrikeTask().execute(item);
                //Update db item  column slash = 1 (true) or 0 (false)
                //AsyncTask
            }
        });
        //Add divider item decoration
        Drawable divider = ContextCompat.getDrawable(this, R.drawable.divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new ToDoListDivider(divider);
        todoRecycler.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        dao.close();
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
        getMenuInflater().inflate(R.menu.menu_list_detail, menu);
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
            case R.id.edit_list:
                //Edit list action
                Toast.makeText(this, "Edit list action", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ListEditActivity.class);
                intent.putExtra(ListEditActivity.EXTRA_LIST_ID, listId);
                intent.putExtra("NAME", listName);
                startActivity(intent);
                return true;
            case R.id.delete_list:
                //Delete list action
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
}
