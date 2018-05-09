package net.ngorham.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * To Do List
 * MainActivity.java
 * Top-level
 * Purpose: Displays the name of each list
 *
 * @author Neil Gorham
 * @version 1.1 04/12/2018
 *
 * 1.1: Added viewSelectDialog for selecting view layout preference,
 * todoLayoutManagers array for storing view layout preferences,
 * View select menu icon is determined by current layout preference,
 * setUpAdapter method for setting Adapter and on click listeners,
 * setReminder boolean variable
 */

public class MainActivity extends Activity {
    //Private variables
    private Boolean listChanges = false;
    private Boolean setReminder = false;
    private ArrayList<ArrayList<Item>> listItems = new ArrayList<>();
    //Recycler View variables
    private RecyclerView todoRecycler;
    private ToDoListAdapter todoAdapter;
    private RecyclerView.LayoutManager[] todoLayoutManagers;
    //Db variables
    private ToDoListDAO dao;
    //SharedPreferences variables
    private SharedPreferences sharedPrefs;
    private boolean switchTheme;
    private int layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        switchTheme = sharedPrefs.getBoolean("switch_theme", false);
        layoutManager = sharedPrefs.getInt("layout_manager", 0);
        if(switchTheme){ setTheme(R.style.LightTheme); }
        else { setTheme(R.style.DarkTheme); }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set up recycler view
        todoRecycler = findViewById(R.id.todo_recycler);
        //Set up Layout Managers
        todoLayoutManagers = new RecyclerView.LayoutManager[] {
                new LinearLayoutManager(this), //List
                new LinearLayoutManager(this), //Details
                new GridLayoutManager(this, 3), //Grid
                new GridLayoutManager(this, 2) //Large grid
        };
        todoRecycler.setLayoutManager(todoLayoutManagers[layoutManager]);
        //Set up DAO
        dao = new ToDoListDAO(this);
        //Set up Adapter
        setUpAdapter(dao.fetchAllNotes(), this);
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
        if(listChanges || setReminder){
            listItems.clear();
            setUpAdapter(dao.fetchAllNotes(), this);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                listChanges = data.getExtras().getBoolean("changes");
                setReminder = data.getExtras().getBoolean("setReminder");
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
                intent.putExtra(ListDetailActivity.EXTRA_LIST_ID, 0);
                intent.putExtra(ListDetailActivity.EXTRA_LIST_NAME, "");
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
        MenuItem view_select = menu.findItem(R.id.view_select);
        MenuItem app_settings = menu.findItem(R.id.app_settings);
        int[] view_select_icons = new int[] {
                R.drawable.ic_view_sequential_black_18dp,
                R.drawable.ic_view_agenda_black_18dp,
                R.drawable.ic_view_grid_black_18dp,
                R.drawable.ic_view_large_grid_black_18dp
        };
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
        view_select.setIcon(view_select_icons[layoutManager]);
        return super.onPrepareOptionsMenu(menu);
    }

    //Display View select AlertDialog
    private void viewSelectDialog(){
        final ArrayList<ViewSelectOption> options = new ArrayList<>();
        options.add(new ViewSelectOption("List", R.drawable.ic_view_sequential_black_24dp));
        options.add(new ViewSelectOption("Details", R.drawable.ic_view_agenda_black_24dp));
        options.add(new ViewSelectOption("Grid", R.drawable.ic_view_grid_black_24dp));
        options.add(new ViewSelectOption("Large grid", R.drawable.ic_view_large_grid_black_24dp));
        ViewSelectAdapter adapter = new ViewSelectAdapter(
                MainActivity.this,
                options
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.view_select);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int position){
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt("layout_manager", position);
                editor.commit();
                layoutManager = position;
                todoRecycler.setLayoutManager(todoLayoutManagers[position]);
                invalidateOptionsMenu();
                setUpAdapter(todoAdapter.getNoteList(), MainActivity.this);
            }
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Set up Adapter and OnClickListener
    public void setUpAdapter(ArrayList<Note> list, Context context){
        for(int i = 0; i < list.size(); i++){
            listItems.add(dao.fetchAllItems(list.get(i).getId()));
        }
        todoAdapter = new ToDoListAdapter(0, list, listItems, context);
        todoRecycler.setAdapter(todoAdapter);
        todoAdapter.setListener(new ToDoListAdapter.Listener(){
            @Override
            public void onClick(View view, int position){
                Note note = todoAdapter.getNoteList().get(position);
                Intent intent = new Intent(getApplicationContext(), ListDetailActivity.class);
                intent.putExtra(ListDetailActivity.EXTRA_LIST_ID, note.getId());
                intent.putExtra(ListDetailActivity.EXTRA_LIST_NAME, note.getName());
                intent.putExtra(ListDetailActivity.EXTRA_LIST_LAST_MODIFIED, note.getLastModified());
                intent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER, note.getReminder());
                intent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER_TIME, note.getReminderTime());
                startActivityForResult(intent, 1);
            }
            @Override
            public void deleteItem(View v, int position){}
            @Override
            public void itemOptions(View v, int position){}
        });
    }
}
