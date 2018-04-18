package net.ngorham.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * To Do List
 * MainActivity.java
 * Top-level
 * Purpose: Displays the name of each list
 *
 * @author Neil Gorham
 * @version 1.0 03/08/2018
 */

public class MainActivity extends Activity {
    //Private variables
    private Boolean listChanges = false;
    //Recycler View variables
    private RecyclerView todoRecycler;
    private ToDoListAdapter todoAdapter;
    private RecyclerView.LayoutManager todoLayoutManager;
    //Db variables
    private ToDoListDAO dao;
    //SharedPreferences variables
    private SharedPreferences sharedPrefs;
    private boolean switchTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        switchTheme = sharedPrefs.getBoolean("switch_theme", false);
        if(switchTheme){ setTheme(R.style.LightTheme); }
        else { setTheme(R.style.DarkTheme); }
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
        todoAdapter = new ToDoListAdapter(0, dao.fetchAllNotes(), this);
        todoRecycler.setAdapter(todoAdapter);
        //Set up onClick listener
        todoAdapter.setListener(new ToDoListAdapter.Listener(){
            @Override
            public void onClick(View view, int position){
                Note note = todoAdapter.getNoteList().get(position);
                Intent intent = new Intent(getApplicationContext(), ListDetailActivity.class);
                intent.putExtra(ListDetailActivity.EXTRA_LIST_ID, note.getId());
                intent.putExtra("NAME", note.getName());
                startActivityForResult(intent, 1);
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
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        dao.close();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        if(switchTheme != sharedPrefs.getBoolean("switch_theme", false)){
            finish();
            startActivity(getIntent());
        }
        if(listChanges){
            todoAdapter.setNoteList(dao.fetchAllNotes());
            todoAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                listChanges = data.getExtras().getBoolean("changes");
            }
        } else if(requestCode == 2){
            finish();
            startActivity(getIntent());
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
        Intent intent;
        switch(item.getItemId()){ //Handle action items
            case R.id.add_list: //Add list action
                intent = new Intent(this, ListEditActivity.class);
                intent.putExtra(ListEditActivity.EXTRA_LIST_ID, 0);
                intent.putExtra("NAME", "");
                startActivityForResult(intent, 1);
                return true;
            case R.id.view_select: //View select action
                viewSelectDialog();
                return true;
            case R.id.app_settings: //Settings action
                intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Called when invalidateOptionsMenu() is called
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem add_list = menu.findItem(R.id.add_list);
        MenuItem app_settings = menu.findItem(R.id.app_settings);
        if(switchTheme){ //Light Theme
            if(add_list != null){
                add_list.setIcon(getResources().getDrawable(R.drawable.ic_add_black_18dp));
            }
            if(app_settings != null){
                app_settings.setIcon(getResources().getDrawable(R.drawable.ic_settings_black_18dp));
            }
        } else { //Dark Theme
            if(add_list != null){
                add_list.setIcon(getResources().getDrawable(R.drawable.ic_add_gold_18dp));
            }
            if(app_settings != null){
                app_settings.setIcon(getResources().getDrawable(R.drawable.ic_settings_gold_18dp));
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    //Display View select AlertDialog
    private void viewSelectDialog(){
        final ArrayList<ViewSelectOption> options = new ArrayList<>();
        options.add(new ViewSelectOption("List", R.drawable.ic_view_sequential_black_18dp));
        options.add(new ViewSelectOption("Detail", R.drawable.ic_view_agenda_black_18dp));
        options.add(new ViewSelectOption("Grid", R.drawable.ic_view_grid_black_18dp));
        options.add(new ViewSelectOption("Large grid", R.drawable.ic_view_large_grid_black_18dp));

        View viewSelectOptions = getLayoutInflater().inflate(R.layout.view_select_options, null);
        ListView lv = viewSelectOptions.findViewById(R.id.view_options);
        ViewSelectAdapter adapter = new ViewSelectAdapter(
                MainActivity.this,
                options
        );
        lv.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.view_select);
        builder.setView(viewSelectOptions);
        /*builder.setAdapter(adapter, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int position){

            }
        });*/
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Option selected: " + options.get(position).getOption(),Toast.LENGTH_SHORT).show();
            }
        });*/
        alertDialog.show();
    }
}
