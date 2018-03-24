package net.ngorham.todolist;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends Activity {
    //Private variables
    private Boolean listChanges = false;
    //Private constants
    private final String TAG = "MainActivity"; //debug
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
        //Set up recycler view
        todoRecycler = findViewById(R.id.todo_recycler);
        //Set up Layout Manager
        todoLayoutManager = new LinearLayoutManager(this);
        todoRecycler.setLayoutManager(todoLayoutManager);
        //Set up DAO
        dao = new ToDoListDAO(this);
        //Set up Adapter
        todoAdapter = new ToDoListAdapter(0, dao.fetchAllNotes());
        todoRecycler.setAdapter(todoAdapter);
        //Set up onClick listener
        todoAdapter.setListener(new ToDoListAdapter.Listener(){
            @Override
            public void onClick(View view, int position){
                Note note = todoAdapter.getNoteList().get(position);
                Intent intent = new Intent(getApplicationContext(), ListDetailActivity.class);
                intent.putExtra(ListDetailActivity.EXTRA_LIST_ID, note.getId());
                intent.putExtra("NAME", note.getName());
                startActivity(intent);
            }
            @Override
            public void deleteItem(View v, int position){}
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "INSIDE: onStart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "INSIDE: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "INSIDE: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "INSIDE: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "INSIDE: onDestroy");
        dao.close();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "INSIDE: onRestart");
        Log.d(TAG, "INSIDE: onRestart: changes " + listChanges);
        if(listChanges){
            todoAdapter.setNoteList(dao.fetchAllNotes());
            todoAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                listChanges = data.getExtras().getBoolean("changes");
                Log.d(TAG, "INSIDE: onActivityResult: changes " + listChanges);
            }
        }
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
            case R.id.add_list: //Add list action
                Intent intent = new Intent(this, ListEditActivity.class);
                intent.putExtra(ListEditActivity.EXTRA_LIST_ID, 0);
                startActivityForResult(intent, 1);
                return true;
            case R.id.app_settings: //Settings action
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
