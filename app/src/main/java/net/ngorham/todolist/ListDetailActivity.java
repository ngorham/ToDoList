package net.ngorham.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

/**
 * To Do List
 * ListDetailActivity.java
 * Category
 * Purpose: Displays the name of the selected list and item contents
 *
 * @author Neil Gorham
 * @version 1.1 04/09/2018
 *
 * 1.1: Added strikeAllItems method
 */

public class ListDetailActivity extends Activity {
    //Public constants
    public static final String EXTRA_LIST_ID = "id";

    //Private variables
    private Note list = new Note();
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

    //Inner classes
    //Delete Note from db
    private class DeleteNoteTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected void onPreExecute(){}
        @Override
        protected Boolean doInBackground(Integer... noteIds){
            int noteId = noteIds[0];
            return dao.deleteNote(noteId);
        }
        @Override
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(ListDetailActivity.this,
                        "Database unavailable, failed to delete note from table",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

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

    //Delete Item from db
    private class DeleteItemTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected void onPreExecute(){}
        @Override
        protected Boolean doInBackground(Integer... itemIds){
            int itemId = itemIds[0];
            return dao.deleteItem(itemId);
        }
        @Override
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(ListDetailActivity.this,
                        "Database unavailable, failed to delete item from table",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Delete all Items from db
    private class DeleteAllItemsTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected void onPreExecute(){}
        @Override
        protected Boolean doInBackground(Integer... listIds){
            int listId = listIds[0];
            return dao.deleteAllItems(listId);
        }
        @Override
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(ListDetailActivity.this,
                        "Database unavailable, failed to delete all items from table",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        switchTheme = sharedPrefs.getBoolean("switch_theme", false);
        if(switchTheme){ setTheme(R.style.LightTheme); }
        else { setTheme(R.style.DarkTheme); }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
        //Set up recycler view
        todoRecycler = findViewById(R.id.todo_recycler);
        //Set up Layout Manager
        todoLayoutManager = new LinearLayoutManager(this);
        todoRecycler.setLayoutManager(todoLayoutManager);
        //Set up DAO
        dao = new ToDoListDAO(this);
        //Store data received from intent
        int listId = (int)getIntent().getExtras().get(EXTRA_LIST_ID);
        if(listId > 0) {
            String listName = getIntent().getStringExtra("NAME");
            list.setName(listName);
            list.setId(listId);
        }
        //Set ActionBar title
        getActionBar().setTitle(list.getName());
        //Set up Adapter
        todoAdapter = new ToDoListAdapter(dao.fetchAllItems(list.getId()), 0, this);
        todoRecycler.setAdapter(todoAdapter);
        //Set up onClick listener
        todoAdapter.setListener(new ToDoListAdapter.Listener(){
            @Override
            public void onClick(View view, int position){
                TextView textView = (TextView)view;
                Item item = todoAdapter.getItemList().get(position);
                if(item.getStrike() == 0){
                    textView.setPaintFlags(textView.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG);
                    item.setStrike(1);
                } else { //Remove strike through
                    textView.setPaintFlags(0);
                    item.setStrike(0);
                }
                new UpdateItemStrikeTask().execute(item);
            }
            @Override
            public void deleteItem(View v, int position){}
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
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
            getActionBar().setTitle(list.getName());
            todoAdapter.setItemList(dao.fetchAllItems(list.getId()));
            todoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                listChanges = data.getExtras().getBoolean("changes");
                String newListName = data.getStringExtra("NAME");
                if(!list.getName().equals(newListName)){
                    list.setName(newListName);
                }
            }
        } else if(requestCode == 2){
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("changes", listChanges);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
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
            case R.id.edit_list: //Edit list action
                Intent intent = new Intent(this, ListEditActivity.class);
                intent.putExtra(ListEditActivity.EXTRA_LIST_ID, list.getId());
                intent.putExtra("NAME", list.getName());
                startActivityForResult(intent, 1);
                return true;
            case R.id.delete_list: //Delete list action
                deleteListDialog();
                return true;
            case R.id.check_list: //Strike all items action
                checkAllItemsDialog(0, 1);
                return true;
            case R.id.uncheck_list: //Unstrike all items action
                checkAllItemsDialog(1, 0);
                return true;
            case R.id.remove_checked: //Remove striked items action
                removeCheckedDialog();
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
        MenuItem edit_list = menu.findItem(R.id.edit_list);
        MenuItem delete_list = menu.findItem(R.id.delete_list);
        MenuItem app_settings = menu.findItem(R.id.app_settings);
        if(switchTheme){ //Light Theme
            if(edit_list != null){
                edit_list.setIcon(getResources().getDrawable(R.drawable.ic_edit_black_18dp));
            }
            if(delete_list != null){
                delete_list.setIcon(getResources().getDrawable(R.drawable.ic_delete_black_18dp));
            }
            if(app_settings != null){
                app_settings.setIcon(getResources().getDrawable(R.drawable.ic_settings_black_18dp));
            }
        } else { //Dark Theme
            if(edit_list != null){
                edit_list.setIcon(getResources().getDrawable(R.drawable.ic_edit_gold_18dp));
            }
            if(delete_list != null){
                delete_list.setIcon(getResources().getDrawable(R.drawable.ic_delete_gold_18dp));
            }
            if(app_settings != null){
                app_settings.setIcon(getResources().getDrawable(R.drawable.ic_settings_gold_18dp));
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    //Display Delete List AlertDialog
    private void deleteListDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDetailActivity.this);
        builder.setTitle(R.string.delete_list);
        if(switchTheme){ builder.setIcon(R.drawable.ic_warning_black_18dp); }
        else { builder.setIcon(R.drawable.ic_warning_gold_18dp); }
        builder.setMessage("Are you sure you want to delete this list?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listChanges = true;
                //Delete list
                if(!todoAdapter.getItemList().isEmpty()){ //db call only if list is populated
                    new DeleteAllItemsTask().execute(list.getId());
                }
                new DeleteNoteTask().execute(list.getId());
                Toast.makeText(getApplicationContext(), "Deleted",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cancel, return to activity
                listChanges = false;
            }
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Display Strike or unstrike all list items AlertDialog
    private void checkAllItemsDialog(final int check, final int value){
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDetailActivity.this);
        int title, icon;
        String msg = "";
        if(check == 0){ //strike all list items
            title = R.string.check_list;
            icon = R.drawable.ic_checkbox_marked_outline_black_18dp;
            msg = "Are you sure you want to check all items?";
        } else { //unstrike all list items
            title = R.string.uncheck_list;
            icon = R.drawable.ic_checkbox_blank_outline_black_18dp;
            msg = "Are you sure you want to uncheck all items?";
        }
        builder.setTitle(title);
        builder.setIcon(icon);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!todoAdapter.getItemList().isEmpty()){
                    for(int c = 0; c < todoAdapter.getItemCount(); c++){
                        Item item = todoAdapter.getItemList().get(c);
                        if(item != null){
                            if(item.getStrike() == check){
                                item.setStrike(value);
                                new UpdateItemStrikeTask().execute(item);
                            }
                        }
                    }
                    todoAdapter.notifyItemRangeChanged(0, todoAdapter.getItemCount());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cancel
            }
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Display Remove checked items AlertDialog
    private void removeCheckedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDetailActivity.this);
        builder.setTitle(R.string.remove_checked);
        builder.setIcon(R.drawable.ic_close_black_18dp);
        builder.setMessage("Are you sure you to remove checked items?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Remove checked items
                if (!todoAdapter.getItemList().isEmpty()) {
                    for (int c = 0; c < todoAdapter.getItemCount(); c++) {
                        Item item = todoAdapter.getItemList().get(c);
                        if (item != null) {
                            if (item.getStrike() == 1) {
                                new DeleteItemTask().execute(item.getId());
                                todoAdapter.getItemList().remove(item);
                            }
                        }
                    }
                    todoAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cancel
            }
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
