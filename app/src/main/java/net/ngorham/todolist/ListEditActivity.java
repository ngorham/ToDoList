package net.ngorham.todolist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.Date;
import java.util.List;

public class ListEditActivity extends Activity {
    //Public constants
    public static final String EXTRA_LIST_ID = "id";

    //Private variables
    int listId;
    String listName;
    private ActionBar actionBar;
    private EditText listNameField;
    private List<Object> itemObjs;
    private List<Integer> deletedItems = new ArrayList<>();
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
        itemObjs = items;
        //Create Adapter
        todoAdapter = new ToDoListAdapter(items, 1);
        //Set Adapter
        todoRecycler.setAdapter(todoAdapter);
        //Set up onClick listener
        todoAdapter.setListener(new ToDoListAdapter.Listener(){
            @Override
            public void onClick(View view, int position){
                if(itemObjs.get(position) instanceof AddItem){
                    addItemDialog(position);
                } else if(itemObjs.get(position) instanceof Item){
                    editItemDialog(view, position);
                }
            }
            @Override
            public void deleteItem(View view, int position){
                Log.v("deleteItem", "delete clicked pos: " + position);
                Log.v("deleteItem", "deletedItems count: " + deletedItems.size());
                Log.v("deleteItem", "itemObjs count: " + itemObjs.size());
                int id = ((Item)itemObjs.get(position)).getId();
                if(id > 0) {
                    Integer itemId = new Integer(id);
                    deletedItems.add(itemId);
                }
                itemObjs.remove(position);
                todoAdapter.notifyDataSetChanged();
                Log.v("deleteItem", "deletedItems count: " + deletedItems.size());
                Log.v("deleteItem", "itemObjs count: " + itemObjs.size());
                Toast.makeText(getApplicationContext(), "delete clicked pos" + position, Toast.LENGTH_SHORT).show();
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
        listNameField = (EditText)actionBar.getCustomView()
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
    public void onDestroy(){
        super.onDestroy();

    }
    @Override
    public void onResume(){
        super.onResume();
        Log.v("onResume", "items.count: " + itemObjs.size());
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

    //Displays Add Item AlertDialog
    public void addItemDialog(final int position){
        Log.v("addItemDialog", "item position: " + position);
        //Create AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ListEditActivity.this);
        builder.setTitle(R.string.add_item);
        View addItemView = getLayoutInflater().inflate(R.layout.add_item_dialog, null);
        builder.setView(addItemView);
        final EditText addItemField = addItemView.findViewById(R.id.add_item_field);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int newPos;
                String addItemName = addItemField.getText().toString();
                if(addItemName.equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Add Item field is empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.v("addItemDialog", "addItemField: " + addItemName);
                Item newItem = new Item();
                newItem.setName(addItemName);
                Log.v("addItemDialog", "newItem.name: " + newItem.getName());
                newItem.setCreatedOn(new Date());
                newItem.setLastModified(new Date());
                newItem.setNoteId(listId);
                if(position == 0){
                    newPos = position + 1;
                } else {
                    newPos = position;
                }
                newItem.setPosition(newPos);
                itemObjs.add(newPos, newItem);
                Log.v("addItemDialog", "items.count: " + itemObjs.size());
                Log.v("addItemDialog", "todoAdapter.count: " + todoAdapter.getItemCount());
                todoAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
    public void editItemDialog(View view, final int position){
        final String itemName = ((Item)itemObjs.get(position)).getName();
        final TextView textView = (TextView)view;
        AlertDialog.Builder builder = new AlertDialog.Builder(ListEditActivity.this);
        builder.setTitle(R.string.edit_item);
        View editItemView = getLayoutInflater().inflate(R.layout.edit_item_dialog, null);
        builder.setView(editItemView);
        final EditText editItemField = editItemView.findViewById(R.id.edit_item_field);
        editItemField.setText(itemName, TextView.BufferType.EDITABLE);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Check for item.name and editItemField.text equality
                //if not equal
                //update item.name
                //update item.name in db
                //update recycler view
                //if equal do nothing
                String editItemName = editItemField.getText().toString();
                if(editItemName.equals("")){
                    //delete item
                } else if(editItemName.equals(itemName)){
                    //do nothing
                    Toast.makeText(getApplicationContext(), "Item name is the same", Toast.LENGTH_SHORT).show();
                } else{
                    //update item name
                    textView.setText(editItemName);
                    ((Item) itemObjs.get(position)).setName(editItemName);
                    Log.v("editItemDialog", "item.name: " + ((Item)itemObjs.get(position)).getName());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cancel, return to activity
            }
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
