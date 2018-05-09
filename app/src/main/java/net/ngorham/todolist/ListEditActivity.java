package net.ngorham.todolist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * To Do List
 * ListEditActivity.java
 * Detail/Edit
 * Purpose: Displays the name of the selected list and item contents,
 * and provides list manipulation options
 *
 * @author Neil Gorham
 * @version 1.1 04/11/2018
 *
 * 1.1: Replaced dialog strings with strings in strings.xml, intent
 * extra strings with ListDetailActivity static constants
 * Removed public static constants, DarkTheme
 */

public class ListEditActivity extends Activity {
    //Private variables
    private Note list = new Note();
    private boolean changes = false;
    private boolean savedCalled = false;
    private boolean listNameChange = false;
    private boolean deleteListCalled = false;
    private boolean deleteListAfterChanges = false;
    private ActionBar actionBar;
    private EditText listNameField;
    private String oldListName;
    private ArrayList<Integer> deletedItems = new ArrayList<>();
    private ArrayList<Item> items;
    private TextView lastModifiedTime;
    private TextView alarmText;
    //Recycler View variables
    private RecyclerView todoRecycler;
    private ToDoListAdapter todoAdapter;
    private RecyclerView.LayoutManager todoLayoutManager;
    //Db variables
    private ToDoListDAO dao;
    //SharedPreferences variables
    //private SharedPreferences sharedPrefs;
    //private boolean switchTheme;

    //Inner classes
    //Update Note to db
    private class UpdateNoteTask extends AsyncTask<Note, Void, Boolean> {
        private Note note;
        @Override
        protected void onPreExecute(){}
        @Override
        protected Boolean doInBackground(Note... notes){
            note = notes[0];
            return dao.updateNote(note);
        }
        @Override
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(ListEditActivity.this,
                        "Database unavailable, failed to update note in table",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

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
                Toast.makeText(ListEditActivity.this,
                        "Database unavailable, failed to delete note from table",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Add Item to db
    private class AddItemTask extends AsyncTask<Item, Void, Integer> {
        private Item item;
        @Override
        protected void onPreExecute(){}
        @Override
        protected Integer doInBackground(Item... items){
            item = items[0];
            if(dao.addItem(item)){
                return dao.fetchItemId(item.getCreatedOn());
            } else {
                return 0;
            }
        }
        @Override
        protected void onPostExecute(Integer success){
            if(success > 0) {
                item.setId(success);
            } else {
                Toast.makeText(ListEditActivity.this,
                        "Database unavailable, failed to insert item into table",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Update Item in db
    private class UpdateItemTask extends AsyncTask<Item, Void, Boolean> {
        private Item item;
        @Override
        protected void onPreExecute(){}
        @Override
        protected Boolean doInBackground(Item... items){
            item = items[0];
            return dao.updateItem(item);
        }
        @Override
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(ListEditActivity.this,
                        "Database unavailable, failed to update item in table",
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
                Toast.makeText(ListEditActivity.this,
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
                Toast.makeText(ListEditActivity.this,
                        "Database unavailable, failed to delete all items from table",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        //sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //switchTheme = sharedPrefs.getBoolean("switch_theme", false);
        setTheme(R.style.LightTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_edit);
        //Set up recycler view
        todoRecycler = findViewById(R.id.todo_recycler);
        //Set up Layout Manager
        todoLayoutManager = new LinearLayoutManager(this);
        todoRecycler.setLayoutManager(todoLayoutManager);
        //Set up DAO
        dao = new ToDoListDAO(this);
        if(savedInstanceState != null){ //existing instance
            list.setId(savedInstanceState.getInt("listId"));
            list.setName(savedInstanceState.getString("listName"));
            oldListName = savedInstanceState.getString("oldListName");
            items = savedInstanceState.getParcelableArrayList("items");
            deletedItems = savedInstanceState.getIntegerArrayList("deletedItems");
            changes = savedInstanceState.getBoolean("changes");
            listNameChange = savedInstanceState.getBoolean("listNameChange");
            savedCalled = savedInstanceState.getBoolean("savedCalled");
        } else { //new instance
            Bundle bundle = getIntent().getExtras();
            if(bundle.getString(ListDetailActivity.EXTRA_LIST_NAME) != null){ //New or existing list
                list.setId((int)getIntent().getExtras().get(ListDetailActivity.EXTRA_LIST_ID));
                if(list.getId() > 0) { //Edit existing list
                    list.setName(getIntent().getStringExtra(ListDetailActivity.EXTRA_LIST_NAME));
                    list.setLastModified(getIntent().getStringExtra(ListDetailActivity.EXTRA_LIST_LAST_MODIFIED));
                    list.setReminder(getIntent().getIntExtra(ListDetailActivity.EXTRA_LIST_REMINDER, 0));
                    list.setReminderTime(getIntent().getStringExtra(ListDetailActivity.EXTRA_LIST_REMINDER_TIME));
                    //DB call
                    items = dao.fetchAllItems(list.getId());
                } else { //Create new list
                    items = new ArrayList<>();
                    list.setLastModified(getDateTime());
                }
                //Add AddItem options to top and bottom of recyclerView
                Item front = new Item();
                front.setName("Add Item");
                Item back = new Item();
                back.setName("Add Item");
                items.add(0, front);
                items.add(items.size(), back);
                oldListName = list.getName();
            } else { //Existing instance, came from Settings
                list.setId(bundle.getInt("listId"));
                list.setName(bundle.getString("listName"));
                oldListName = bundle.getString("oldListName");
                items = bundle.getParcelableArrayList("items");
                deletedItems = bundle.getIntegerArrayList("deletedItems");
                changes = bundle.getBoolean("changes");
                listNameChange = bundle.getBoolean("listNameChange");
                savedCalled = bundle.getBoolean("savedCalled");
            }
        }
        //Create Adapter
        todoAdapter = new ToDoListAdapter(items, 1, this);
        //Set Adapter
        todoRecycler.setAdapter(todoAdapter);
        //Set up onClick listener
        todoAdapter.setListener(new ToDoListAdapter.Listener(){
            @Override
            public void onClick(View view, int position){
                if((todoAdapter.getItemList().get(position)).getName().equals("Add Item")){
                    addItemDialog(position);
                } else {
                    editItemDialog(view, position);
                }
            }
            @Override
            public void deleteItem(View view, int position){
                int id = todoAdapter.getItemList().get(position).getId();
                if(id > 0) {
                    deletedItems.add(id);
                    changes = true;
                    savedCalled = false;
                } else {
                    changes = false;
                }
                todoAdapter.getItemList().remove(position);
                todoAdapter.notifyDataSetChanged();
            }
            @Override
            public void itemOptions(View v, int position){}
        });
        //Add divider item decoration
        Drawable divider = ContextCompat.getDrawable(this, R.drawable.divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new ToDoListDivider(divider);
        todoRecycler.addItemDecoration(dividerItemDecoration);
        //Get ActionBar reference
        actionBar = getActionBar();
        //Set EditText view in actionBar
        actionBar.setCustomView(R.layout.text_field);
        listNameField = actionBar.getCustomView()
                .findViewById(R.id.field);
        listNameField.setText(list.getName(), TextView.BufferType.EDITABLE);
        //Set EditText listener
        listNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String newListName = listNameField.getText().toString();
                if(list.getName().equals(newListName)){
                    listNameChange = false;
                    return false;
                }
                listNameField.setText(newListName, TextView.BufferType.EDITABLE);
                listNameChange = true;
                return true;
            }
        });
        actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_CUSTOM);
        //Set up last modified/alarm time textview
        lastModifiedTime = findViewById(R.id.last_modified_time);
        alarmText = findViewById(R.id.alarm_text);
        lastModifiedTime.setText(getResources().getString(R.string.editing));
        if(list.getReminder() == 1){ //pin icon
            alarmText.setText(addIcon(R.drawable.ic_pin_black_18dp, list.getReminderTime()));
        } else if(list.getReminder() == 2){ //clock icon
            alarmText.setText(addIcon(R.drawable.ic_clock_outline_black_18dp, list.getReminderTime()));
        } else {
            alarmText.setText(getDateTimeString(list.getLastModified()));
        }
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
    public void onDestroy(){
        super.onDestroy();
        //Db close
        dao.close();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        if(!savedCalled){ intent.putExtra("changes", updateDB()); }
        else { intent.putExtra("changes", savedCalled); }
        intent.putExtra(ListDetailActivity.EXTRA_LIST_NAME, list.getName());
        intent.putExtra(ListDetailActivity.EXTRA_LIST_LAST_MODIFIED, list.getLastModified());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2){
            Bundle outState = toBundle();
            Intent intent = new Intent(this, ListEditActivity.class);
            intent.putExtras(outState);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        String newListName = listNameField.getText().toString();
        listNameChange = !oldListName.equals(newListName);
        outState.putInt("listId", list.getId());
        outState.putCharSequence("listName", newListName);
        outState.putCharSequence("oldListName", oldListName);
        outState.putParcelableArrayList("items", todoAdapter.getItemList());
        outState.putIntegerArrayList("deletedItems", deletedItems);
        outState.putBoolean("changes", changes);
        outState.putBoolean("listNameChange", listNameChange);
        outState.putBoolean("savedCalled", savedCalled);
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
        Intent intent;
        switch(item.getItemId()){ //Handle action items
            case R.id.delete_list: //delete list action
                deleteListDialog();
                return true;
            case R.id.save_list: //save list action
                if(updateDB()){
                    changes = false;
                    savedCalled = true;
                }
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
        MenuItem delete_list = menu.findItem(R.id.delete_list);
        MenuItem save_list = menu.findItem(R.id.save_list);
        MenuItem app_settings = menu.findItem(R.id.app_settings);
        if(true){ //Light Theme
            if(delete_list != null){
                delete_list.setIcon(getResources().getDrawable(R.drawable.ic_delete_black_18dp));
            }
            if(save_list != null){
                save_list.setIcon(getResources().getDrawable(R.drawable.ic_save_black_18dp));
            }
            if(app_settings != null){
                app_settings.setIcon(getResources().getDrawable(R.drawable.ic_settings_black_18dp));
            }
        } /*else { //Dark Theme
            if(delete_list != null){
                delete_list.setIcon(getResources().getDrawable(R.drawable.ic_delete_gold_18dp));
            }
            if(save_list != null){
                save_list.setIcon(getResources().getDrawable(R.drawable.ic_save_gold_18dp));
            }
            if(app_settings != null){
                app_settings.setIcon(getResources().getDrawable(R.drawable.ic_settings_gold_18dp));
            }
        }*/
        return super.onPrepareOptionsMenu(menu);
    }

    //Displays Add Item AlertDialog
    private void addItemDialog(final int position){
        //Create AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ListEditActivity.this);
        builder.setTitle(R.string.add_item);
        View addItemView = getLayoutInflater().inflate(R.layout.add_item_dialog, null);
        builder.setView(addItemView);
        final EditText addItemField = addItemView.findViewById(R.id.add_item_field);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int newPos;
                String addItemName = addItemField.getText().toString();
                if(addItemName.equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Add Item field is empty",
                            Toast.LENGTH_SHORT).show();
                    changes = false;
                    return;
                }
                Item newItem = new Item();
                newItem.setName(addItemName);
                String dateTime = getDateTime();
                newItem.setCreatedOn(dateTime);
                newItem.setLastModified(dateTime);
                newItem.setNoteId(list.getId());
                if(position == 0){
                    newPos = position + 1;
                } else {
                    newPos = position;
                }
                todoAdapter.getItemList().add(newPos, newItem);
                todoAdapter.notifyDataSetChanged();
                changes = true; //changes made to list
                savedCalled = false;
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cancel, return to activity
            }
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Displays Edit Item AlertDialog
    private void editItemDialog(View view, final int position){
        final String itemName = todoAdapter.getItemList()
                .get(position).getName();
        final TextView textView = (TextView)view;
        AlertDialog.Builder builder = new AlertDialog.Builder(ListEditActivity.this);
        builder.setTitle(R.string.edit_item);
        View editItemView = getLayoutInflater().inflate(R.layout.edit_item_dialog, null);
        builder.setView(editItemView);
        final EditText editItemField = editItemView.findViewById(R.id.edit_item_field);
        editItemField.setText(itemName, TextView.BufferType.EDITABLE);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //update item.name in db
                String editItemName = editItemField.getText().toString();
                if(editItemName.equals("")){
                    //delete item
                    int id = todoAdapter.getItemList().get(position).getId();
                    if(id > 0) {
                        deletedItems.add(id);
                        changes = true;
                        savedCalled = false;
                    }
                    todoAdapter.getItemList().remove(position);
                } else if(editItemName.equals(itemName)){
                    //do nothing
                    Toast.makeText(getApplicationContext(),
                            "Item name is the same",
                            Toast.LENGTH_SHORT).show();
                    changes = false;
                } else{
                    //update item name
                    textView.setText(editItemName);
                    todoAdapter.getItemList().get(position).setName(editItemName);
                    todoAdapter.getItemList().get(position).setLastModified(getDateTime());
                    changes = true;
                    savedCalled = false;
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cancel, return to activity
            }
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Display Delete List AlertDialog
    private void deleteListDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ListEditActivity.this);
        builder.setTitle(R.string.delete_list);
        builder.setIcon(R.drawable.ic_warning_black_18dp);
        builder.setMessage(R.string.dialog_delete_list_message);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteListCalled = true;
                if(changes){ deleteListAfterChanges = true; }
                updateDB();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cancel, return to activity
                deleteListCalled = false;
                deleteListAfterChanges = false;
            }
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Utilities
    //Utility that creates a string of the current date and time
    public String getDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //Utility that creates a string of the current date
    public String getDateString(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM-dd-yyyy", Locale.getDefault());
        Date date = new Date();
        return "(" + dateFormat.format(date) + ")";
    }

    //Utility for displaying Month and Day of list's lastModified
    private String getMonthDayString(String date){
        String month = "";
        String monthSubstr = date.substring(5, 7);
        String day = date.substring(8, 10);
        if(monthSubstr.equals("01")){
            month = "Jan";
        } else if(monthSubstr.equals("02")){
            month = "Feb";
        } else if(monthSubstr.equals("03")){
            month = "Mar";
        } else if(monthSubstr.equals("04")){
            month = "Apr";
        } else if(monthSubstr.equals("05")){
            month = "May";
        } else if(monthSubstr.equals("06")){
            month = "Jun";
        } else if(monthSubstr.equals("07")){
            month = "Jul";
        } else if(monthSubstr.equals("08")){
            month = "Aug";
        } else if(monthSubstr.equals("09")){
            month = "Sep";
        } else if(monthSubstr.equals("10")){
            month = "Oct";
        } else if(monthSubstr.equals("11")){
            month = "Nov";
        } else if(monthSubstr.equals("12")){
            month = "Dec";
        }
        return (month + " " + day);
    }

    //Utility for displaying date and time of list's lastModified
    private String getDateTimeString(String date){
        String year = date.substring(2, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String time = date.substring(11, 16);
        StringBuilder sb = new StringBuilder();
        sb.append(month);
        sb.append("/");
        sb.append(day);
        sb.append("/");
        sb.append(year);
        sb.append(" ");
        sb.append(time);
        return sb.toString();
    }

    //Updates db if changes are made
    private boolean updateDB(){
        String newListName = listNameField.getText().toString();
        listNameChange = !oldListName.equals(newListName);
        if((changes || listNameChange) && !deleteListAfterChanges){
            if(newListName.equals("")){ newListName = getDateString(); }
            if(list.getId() > 0){
                //update list in db
                list.setLastModified(getDateTime());
                list.setName(newListName);
                new UpdateNoteTask().execute(list);
                //Remove items for db
                if(!deletedItems.isEmpty()){
                    for(int i = 0; i < deletedItems.size(); i++){
                        int itemId = deletedItems.get(i);
                        new DeleteItemTask().execute(itemId);
                        deletedItems.remove(i);
                    }
                }
                //Add new items, update old items
                if(todoAdapter.getItemCount() > 2){
                    int position = 0;
                    for(int i = 1; i < todoAdapter.getItemCount() - 1; i++){
                        Item item = todoAdapter.getItemList().get(i);
                        item.setPosition(position);
                        if(item.getId() == 0){//add new item
                            new AddItemTask().execute(item);
                        } else {
                            new UpdateItemTask().execute(item);
                        }
                        position++;
                    }
                }
            } else {
                //add new list to db
                list.setCreatedOn(getDateTime());
                list.setLastModified(getDateTime());
                list.setName(newListName);
                dao.addNote(list);
                int listIdFromDb = dao.fetchNoteId(list.getCreatedOn());
                if(listIdFromDb == 0){
                    Toast.makeText(getApplicationContext(),
                            "Database unavailable, cannot access",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                //Remove items for db
                if(!deletedItems.isEmpty()){
                    for(int i = 0; i < deletedItems.size(); i++){
                        int itemId = deletedItems.get(i);
                        new DeleteItemTask().execute(itemId);
                        deletedItems.remove(i);
                    }
                }
                //Add new items
                if(todoAdapter.getItemCount() > 2){
                    int position = 0;
                    for(int i = 1; i < todoAdapter.getItemCount() - 1; i++){
                        Item item = todoAdapter.getItemList().get(i);
                        item.setPosition(position);
                        item.setNoteId(listIdFromDb);
                        new AddItemTask().execute(item);
                        position++;
                    }
                }
            }
            Toast.makeText(getApplicationContext(), "Saved",
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if(deleteListAfterChanges || deleteListCalled) {
            if(todoAdapter.getItemCount() > 2 || !deletedItems.isEmpty()){
                new DeleteAllItemsTask().execute(list.getId());
            }
            if(list.getId() > 0){
                new DeleteNoteTask().execute(list.getId());
            }
            Toast.makeText(getApplicationContext(), "Deleted",
                    Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    //Store activity variables in a bundle
    private Bundle toBundle(){
        Bundle b = new Bundle();
        String newListName = listNameField.getText().toString();
        listNameChange = !oldListName.equals(newListName);
        b.putInt("listId", list.getId());
        b.putCharSequence("listName", newListName);
        b.putCharSequence("oldListName", oldListName);
        b.putParcelableArrayList("items", todoAdapter.getItemList());
        b.putIntegerArrayList("deletedItems", deletedItems);
        b.putBoolean("changes", changes);
        b.putBoolean("listNameChange", listNameChange);
        b.putBoolean("savedCalled", savedCalled);
        return b;
    }

    //Utility for adding an icon to a string
    private SpannableStringBuilder addIcon(int iconId, String text){
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("icon");
        Drawable d = getResources().getDrawable(iconId);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d);
        ssb.setSpan(span, 0 , 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        ssb.append(text);
        return ssb;
    }
}
