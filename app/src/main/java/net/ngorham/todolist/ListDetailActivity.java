package net.ngorham.todolist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * To Do List
 * ListDetailActivity.java
 * Category
 * Purpose: Displays the name of the selected list and item contents
 *
 * @author Neil Gorham
 * @version 1.1 04/09/2018
 *
 * 1.1: Added checkAllItemsDialog method, removeCheckedDialog method,
 * reminderDialog method, alarmPresetsDialog method,
 * alarmDateDialog method, alarmTimeDialog method, showDateText method,
 * showTimeText method, UpdateNoteReminderTask class
 * Replaced dialog strings with strings in strings.xml
 * Strike through on click sets listChanges to true, displays 'Saved' Toast
 * and displays check mark icon image
 * Check/Uncheck all items sets listChanges to true
 * Remove all checked items sets listChanges to true
 * Removed DarkTheme
 */

public class ListDetailActivity extends Activity {
    //Public constants
    public static final String EXTRA_LIST_ID = "id";
    public static final String EXTRA_LIST_NAME = "name";
    public static final String EXTRA_LIST_LAST_MODIFIED = "last_modified";
    public static final String EXTRA_LIST_REMINDER = "reminder";
    public static final String EXTRA_LIST_REMINDER_TIME = "reminder_time";
    public static final String EXTRA_LIST_ITEMS = "list_items";
    public static final String EXTRA_LIST_ALARM_REPETITION = "repetition";
    public static final String EXTRA_LIST_ALARM_TRIGGER_TIME = "trigger_time";

    private final String TAG = "ListDetailActivity";

    //Private variables
    private Context context;
    private Note list = new Note();
    private Boolean listChanges = false;
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
    //Reminder variables
    private AlertDialog reminderAlertDialog;
    private Calendar calendar;
    private Calendar oldCalendar;
    private AlarmManager alarmManager;
    private boolean is24HourFormat;
    private int originalReminder;
    private TextView typeText;
    private Spinner typeSpinner;
    private TextView whenText;
    private Button alarmPresetsButton;
    private Button alarmDateButton;
    private Button alarmTimeButton;
    private Button alarmDismissButton;
    private TextView repetitionText;
    private ImageView repetitionImage;
    private Spinner repetitionSpinner;

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

    //Update Note reminder value
    private class UpdateNoteReminderTask extends AsyncTask<Note, Void, Boolean>{
        protected void onPreExecute(){}
        protected Boolean doInBackground(Note... notes){
            Note note = notes[0];
            return dao.updateReminder(note);
        }
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(ListDetailActivity.this,
                        "Database unavailable, failed to update reminder in NOTE table",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Update Note last modified time value
    private class UpdateNoteLastModifiedTask extends AsyncTask<Note, Void, Boolean>{
        protected void onPreExecute(){}
        protected Boolean doInBackground(Note... notes){
            Note note = notes[0];
            return dao.updateLastModified(note);
        }
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(ListDetailActivity.this,
                        "Database unavailable, failed to update reminder in NOTE table",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        //Set SharedPreferences
        //sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //switchTheme = sharedPrefs.getBoolean("switch_theme", false);
        setTheme(R.style.LightTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
        context = this;
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //Set up recycler view
        todoRecycler = findViewById(R.id.todo_recycler);
        //Set up Layout Manager
        todoLayoutManager = new LinearLayoutManager(this);
        todoRecycler.setLayoutManager(todoLayoutManager);
        //Set up DAO
        dao = new ToDoListDAO(this);
        //Store data received from intent
        int listId = (int)getIntent().getExtras().get(EXTRA_LIST_ID);
        if(listId > 0){
            String listName = getIntent().getStringExtra(EXTRA_LIST_NAME);
            String lastModified = getIntent().getStringExtra(EXTRA_LIST_LAST_MODIFIED);
            originalReminder = getIntent().getIntExtra(EXTRA_LIST_REMINDER, 0);
            String reminderTime = getIntent().getStringExtra(EXTRA_LIST_REMINDER_TIME);
            list.setName(listName);
            list.setId(listId);
            list.setLastModified(lastModified);
            list.setReminder(originalReminder);
            list.setReminderTime(reminderTime);
            new UpdateNoteReminderTask().execute(list);
        }
        //Set ActionBar title
        getActionBar().setTitle(list.getName());
        getActionBar().setHomeButtonEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        //Set up Adapter
        todoAdapter = new ToDoListAdapter(dao.fetchAllItems(list.getId()), 0, this);
        todoRecycler.setAdapter(todoAdapter);
        //Set up onClick listener
        todoAdapter.setListener(new ToDoListAdapter.Listener(){
            @Override
            public void onClick(View view, int position){
                TextView nameText = view.findViewById(R.id.name_text);
                ImageButton optionsButton = view.findViewById(R.id.more_vert);
                ImageView checkImage = view.findViewById(R.id.check_mark);
                Item item = todoAdapter.getItemList().get(position);
                if(item.getStrike() == 0){
                    nameText.setPaintFlags(nameText.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG);
                    item.setStrike(1);
                    checkImage.setVisibility(View.VISIBLE);
                    optionsButton.setVisibility(View.GONE);
                } else { //Remove strike through
                    nameText.setPaintFlags(0);
                    item.setStrike(0);
                    checkImage.setVisibility(View.GONE);
                    optionsButton.setVisibility(View.VISIBLE);
                }
                new UpdateItemStrikeTask().execute(item);
                list.setLastModified(getDateTime());
                new UpdateNoteLastModifiedTask().execute(list);
                listChanges = true;
            }
            @Override
            public void deleteItem(View v, int position){}
            @Override
            public void itemOptions(View v, int position){
                Item item = todoAdapter.getItemList().get(position);
                itemOptionsDialog(item);
            }
        });
        //Add divider item decoration
        Drawable divider = ContextCompat.getDrawable(this, R.drawable.divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new ToDoListDivider(divider);
        todoRecycler.addItemDecoration(dividerItemDecoration);
        //Set up last modified/alarm time textview
        lastModifiedTime = findViewById(R.id.last_modified_time);
        alarmText = findViewById(R.id.alarm_text);
        lastModifiedTime.setText(getMonthDayString(list.getLastModified()));
        if(list.getReminder() == 1){ //pin icon
            alarmText.setText(addIcon(R.drawable.ic_pin_black_18dp, list.getReminderTime()));
        } else if(list.getReminder() == 2) { //clock icon
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
        /*if(switchTheme != sharedPrefs.getBoolean("switch_theme", false)){
            finish();
            startActivity(getIntent());
        }*/
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
                String newListName = data.getStringExtra(EXTRA_LIST_NAME);
                if(!list.getName().equals(newListName)){
                    list.setName(newListName);
                }
                String lastModified = data.getStringExtra(EXTRA_LIST_LAST_MODIFIED);
                if(!list.getLastModified().equals(lastModified)){
                    list.setLastModified(lastModified);
                }
                lastModifiedTime.setText(getMonthDayString(list.getLastModified()));
                if(list.getReminder() == 0) {
                    alarmText.setText(getDateTimeString(list.getLastModified()));
                }
            }
        } else if(requestCode == 2){
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onBackPressed(){
        if(listChanges){
            Toast.makeText(ListDetailActivity.this, "Saved", Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent();
        intent.putExtra("changes", listChanges);
        if(list.getReminder() != originalReminder){ intent.putExtra("setReminder", true); }
        else { intent.putExtra("setReminder", false); }
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
                intent.putExtra(EXTRA_LIST_ID, list.getId());
                intent.putExtra(EXTRA_LIST_NAME, list.getName());
                intent.putExtra(EXTRA_LIST_LAST_MODIFIED, list.getLastModified());
                intent.putExtra(EXTRA_LIST_REMINDER, list.getReminder());
                intent.putExtra(EXTRA_LIST_REMINDER_TIME, list.getReminderTime());
                startActivityForResult(intent, 1);
                return true;
            case R.id.delete_list: //Delete list action
                deleteListDialog();
                return true;
            case R.id.reminder: //Reminder action
                reminderDialog();
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
        if(true){ //Light Theme
            if(edit_list != null){
                edit_list.setIcon(getResources().getDrawable(R.drawable.ic_edit_black_18dp));
            }
            if(delete_list != null){
                delete_list.setIcon(getResources().getDrawable(R.drawable.ic_delete_black_18dp));
            }
            if(app_settings != null){
                app_settings.setIcon(getResources().getDrawable(R.drawable.ic_settings_black_18dp));
            }
        } /*else { //Dark Theme
            if(edit_list != null){
                edit_list.setIcon(getResources().getDrawable(R.drawable.ic_edit_gold_18dp));
            }
            if(delete_list != null){
                delete_list.setIcon(getResources().getDrawable(R.drawable.ic_delete_gold_18dp));
            }
            if(app_settings != null){
                app_settings.setIcon(getResources().getDrawable(R.drawable.ic_settings_gold_18dp));
            }
        }*/
        return super.onPrepareOptionsMenu(menu);
    }

    //Display Reminder AlertDialog
    private void reminderDialog(){
        is24HourFormat = DateFormat.is24HourFormat(this);
        calendar = Calendar.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDetailActivity.this);
        builder.setTitle(R.string.reminder);
        builder.setIcon(R.drawable.ic_notifications_black_18dp);
        View reminderView = getLayoutInflater().inflate(R.layout.reminder_dialog, null);
        //Views in reminder view
        typeText = reminderView.findViewById(R.id.type_text);
        typeSpinner = reminderView.findViewById(R.id.type_spinner);
        whenText = reminderView.findViewById(R.id.when_text);
        alarmPresetsButton = reminderView.findViewById(R.id.alarm_presets_button);
        alarmDateButton = reminderView.findViewById(R.id.alarm_date_button);
        alarmTimeButton = reminderView.findViewById(R.id.alarm_time_button);
        alarmDismissButton = reminderView.findViewById(R.id.alarm_dismiss_button);
        repetitionText = reminderView.findViewById(R.id.repetition_text);
        repetitionImage = reminderView.findViewById(R.id.repetition_image);
        repetitionSpinner = reminderView.findViewById(R.id.repetition_spinner);
        //Default setup - none selected in typeSpinner
        whenText.setVisibility(View.INVISIBLE);
        alarmPresetsButton.setVisibility(View.INVISIBLE);
        alarmDateButton.setVisibility(View.INVISIBLE);
        alarmTimeButton.setVisibility(View.INVISIBLE);
        repetitionText.setVisibility(View.INVISIBLE);
        repetitionImage.setVisibility(View.INVISIBLE);
        repetitionSpinner.setVisibility(View.INVISIBLE);
        if(list.getReminder() == 0){ alarmDismissButton.setVisibility(View.GONE); }
        else { alarmDismissButton.setVisibility(View.VISIBLE); }
        //Set up preset, date, and time buttons
        int in15Minutes = 900000;
        setReminderPreset(getResources().getString(R.string.preset), in15Minutes);
        //typeSpinner selection
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
                switch(position){
                    case 0: //None
                        whenText.setVisibility(View.INVISIBLE);
                        alarmPresetsButton.setVisibility(View.INVISIBLE);
                        alarmDateButton.setVisibility(View.INVISIBLE);
                        alarmTimeButton.setVisibility(View.INVISIBLE);
                        repetitionText.setVisibility(View.INVISIBLE);
                        repetitionImage.setVisibility(View.INVISIBLE);
                        repetitionSpinner.setVisibility(View.INVISIBLE);
                        break;
                    case 1: //Pin to status bar
                        whenText.setVisibility(View.INVISIBLE);
                        alarmPresetsButton.setVisibility(View.INVISIBLE);
                        alarmDateButton.setVisibility(View.INVISIBLE);
                        alarmTimeButton.setVisibility(View.INVISIBLE);
                        repetitionText.setVisibility(View.INVISIBLE);
                        repetitionImage.setVisibility(View.INVISIBLE);
                        repetitionSpinner.setVisibility(View.INVISIBLE);
                        break;
                    case 2: //Time alarm
                        whenText.setVisibility(View.VISIBLE);
                        alarmPresetsButton.setVisibility(View.VISIBLE);
                        alarmDateButton.setVisibility(View.VISIBLE);
                        alarmTimeButton.setVisibility(View.VISIBLE);
                        repetitionText.setVisibility(View.VISIBLE);
                        repetitionImage.setVisibility(View.VISIBLE);
                        repetitionSpinner.setVisibility(View.VISIBLE);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });
        builder.setView(reminderView);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int o) {
                //Set alarm for specified time and date
                //Custom String for notification content
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                for(int i = 0; i < todoAdapter.getItemCount(); i++){
                    Item item = todoAdapter.getItemList().get(i);
                    if(item != null){
                        ssb.append(getResources().getString(R.string.sm_dot));
                        ssb.append(item.getName());
                        if(item.getStrike() == 1){
                            ssb.setSpan(new StrikethroughSpan(),
                                    ssb.length() - item.getName().length(),
                                    ssb.length(),
                                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        ssb.append(" ");
                    }
                }
                String listItemsStr = ssb.toString();
                int repetition = repetitionSpinner.getSelectedItemPosition();
                long triggerTime = calendar.getTimeInMillis();
                Date alarm = new Date(triggerTime);
                SimpleDateFormat dateFormat;
                switch(typeSpinner.getSelectedItemPosition()){
                    case 0: //None, do nothing
                        break;
                    case 1: //Pin to status bar
                        dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
                        list.setReminder(1);
                        list.setReminderTime(dateFormat.format(alarm));
                        new UpdateNoteReminderTask().execute(list);
                        //Intent variables
                        int notificationID = list.getId();
                        String notificationTitle = list.getName();
                        //Intent on click notification
                        Intent activityIntent = new Intent(context, ListDetailActivity.class);
                        activityIntent.putExtra(ListDetailActivity.EXTRA_LIST_ID, notificationID);
                        activityIntent.putExtra(ListDetailActivity.EXTRA_LIST_NAME, notificationTitle);
                        activityIntent.putExtra(ListDetailActivity.EXTRA_LIST_LAST_MODIFIED, list.getLastModified());
                        activityIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER, list.getReminder());
                        activityIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER_TIME, list.getReminderTime());
                        //Add parent activity and intent to task stack
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(ListDetailActivity.class);
                        stackBuilder.addNextIntent(activityIntent);
                        PendingIntent pendingActivityIntent = stackBuilder.getPendingIntent(notificationID, PendingIntent.FLAG_UPDATE_CURRENT);
                        //Build notification
                        Notification builder = new Notification.Builder(context)
                                .setSmallIcon(R.drawable.ic_todolist_launcher)
                                .setContentTitle(notificationTitle)
                                .setContentText(listItemsStr)
                                .setPriority(Notification.PRIORITY_MAX)
                                .setDefaults(Notification.DEFAULT_VIBRATE)
                                .setContentIntent(pendingActivityIntent)
                                .build();
                        builder.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(notificationID, builder);
                        //show pin icon and date MM/DD/YY
                        alarmText.setText(addIcon(R.drawable.ic_pin_black_18dp, list.getReminderTime()));
                        Toast.makeText(ListDetailActivity.this, "Reminder is set", Toast.LENGTH_SHORT).show();
                        break;
                    case 2: //Time alarm
                        //Use specified date and time for alarm set up
                        dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.getDefault());
                        list.setReminder(2);
                        list.setReminderTime(dateFormat.format(alarm));
                        new UpdateNoteReminderTask().execute(list);
                        //Create intent for alarm
                        Intent alarmIntent = new Intent(context, NotificationPublisher.class);
                        alarmIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, list.getId());
                        alarmIntent.putExtra(EXTRA_LIST_NAME, list.getName());
                        alarmIntent.putExtra(EXTRA_LIST_ITEMS, listItemsStr);
                        alarmIntent.putExtra(EXTRA_LIST_LAST_MODIFIED, list.getLastModified());
                        alarmIntent.putExtra(EXTRA_LIST_REMINDER, list.getReminder());
                        alarmIntent.putExtra(EXTRA_LIST_REMINDER_TIME, list.getReminderTime());
                        alarmIntent.putExtra(EXTRA_LIST_ALARM_REPETITION, repetition);
                        alarmIntent.putExtra(EXTRA_LIST_ALARM_TRIGGER_TIME, triggerTime);
                        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, list.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        //Set alarm
                        if(repetition == 0){ //One-time event
                            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingAlarmIntent);
                        } else if(repetition == 1){ //Daily
                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, AlarmManager.INTERVAL_DAY, pendingAlarmIntent);
                        } else if(repetition == 2){ //Every weekday (Mon - Fri)
                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, AlarmManager.INTERVAL_DAY, pendingAlarmIntent);
                        } else if(repetition == 3){ //Weekly
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, 7*24*60*60*1000, pendingAlarmIntent);
                        } else if(repetition == 4){ //Bi-weekly
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, 14*24*60*60*1000, pendingAlarmIntent);
                        } else if(repetition == 5){ //Monthly
                            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingAlarmIntent);
                        } else if(repetition == 6){ //Yearly
                            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingAlarmIntent);
                        }
                        //show clock with alarm date and time MM/DD/YY HH:mm
                        alarmText.setText(addIcon(R.drawable.ic_clock_outline_black_18dp, list.getReminderTime()));
                        Toast.makeText(ListDetailActivity.this, "Reminder is set", Toast.LENGTH_SHORT).show();
                        break;
                    default: //do nothing
                        break;
                }
                oldCalendar = calendar; //set old calendar
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cancel
            }
        });
        builder.setCancelable(true);
        reminderAlertDialog = builder.create();
        reminderAlertDialog.show();
    }

    //Display Alarm presets AlertDialog
    public void alarmPresetsDialog(View view){
        final ArrayList<String> presets = new ArrayList<>();
        presets.add("5 minutes"); presets.add("10 minutes");
        presets.add("15 minutes"); presets.add("20 minutes");
        presets.add("25 minutes"); presets.add("30 minutes");
        presets.add("45 minutes"); presets.add("1 hour");
        presets.add("2 hours"); presets.add("3 hours");
        presets.add("6 hours"); presets.add("12 hours");
        presets.add("24 hours");
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, presets);
        final int[] delayTimes = {
                300000, 600000, 900000, 1200000, 1500000, 1800000, 2700000,
                3600000, 7200000, 10800000, 21600000, 43200000, 86400000
        };
        //Set up AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDetailActivity.this);
        builder.setTitle(R.string.time_text);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int position){
                //Set text on preset, date and time buttons
                //Set calendar instance for date and time pickers
                StringBuilder sb = new StringBuilder();
                sb.append("In ");
                sb.append(presets.get(position));
                setReminderPreset(sb.toString(), delayTimes[position]);
            }
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Display Alarm date AlertDialog
    public void alarmDateDialog(View view){
        //Get current year, month, day
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //Set up and display DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        alarmDateButton.setText(showDateText(year, monthOfYear, dayOfMonth));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    //Display Alarm time AlertDialog
    public void alarmTimeDialog(View view){
        //Get current hour, minute
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //Set up and display TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute){
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        alarmTimeButton.setText(showTimeText(hourOfDay, minute, is24HourFormat));
                    }
                }, hour, minute, is24HourFormat);
        timePickerDialog.show();
    }

    //Cancel alarm and sent notification
    public void alarmDismiss(View view){
        //Cancel alarm
        int notificationID = list.getId();
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, notificationID, new Intent(context, NotificationPublisher.class), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingAlarmIntent);
        //cancel notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);
        list.setReminder(0); //hides dismiss button
        list.setReminderTime("");
        new UpdateNoteReminderTask().execute(list);
        reminderAlertDialog.dismiss();
        alarmText.setText(getDateTimeString(list.getLastModified()));
        Toast.makeText(this, "Reminder cleared", Toast.LENGTH_SHORT).show();
    }

    //Display Delete List AlertDialog
    private void deleteListDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDetailActivity.this);
        builder.setTitle(R.string.delete_list);
        builder.setIcon(R.drawable.ic_warning_black_18dp);
        builder.setMessage(R.string.dialog_delete_list_message);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
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
        int title, icon, msg;
        if(check == 0){ //strike all list items
            title = R.string.check_list;
            icon = R.drawable.ic_checkbox_marked_outline_black_18dp;
            msg = R.string.dialog_check_list_message;
        } else { //unstrike all list items
            title = R.string.uncheck_list;
            icon = R.drawable.ic_checkbox_blank_outline_black_18dp;
            msg = R.string.dialog_uncheck_list_message;
        }
        builder.setTitle(title);
        builder.setIcon(icon);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
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
                    listChanges = true;
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
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
    private void removeCheckedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDetailActivity.this);
        builder.setTitle(R.string.remove_checked);
        builder.setIcon(R.drawable.ic_close_black_18dp);
        builder.setMessage(R.string.dialog_remove_checked_message);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Remove checked items
                if (!todoAdapter.getItemList().isEmpty()) {
                    int count = todoAdapter.getItemCount();
                    for (int c = 0; c < count; c++) {
                        Item item = todoAdapter.getItemList().get(c);
                        if (item != null) {
                            if (item.getStrike() == 1) {
                                dao.deleteItem(item.getId());
                            }
                        }
                    }
                    todoAdapter.setItemList(dao.fetchAllItems(list.getId()));
                    todoAdapter.notifyDataSetChanged();
                    listChanges = true;
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cancel
            }
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Display Item options AlertDialog
    private void itemOptionsDialog(Item item){
        final ArrayList<String> options = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, options);
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDetailActivity.this);
        builder.setTitle(item.getName());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                //Edit Item
                //Web Search
                //Copy to Clipboard
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

    //Returns a string of the selected date
    private String showDateText(int year, int month, int dayOfMonth){
        String[] months = getResources().getStringArray(R.array.months);
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, ");
        Date date = new Date(year, month, dayOfMonth - 1);
        String dayOfWeek = simpleDateFormat.format(date);
        sb.append(dayOfWeek);
        sb.append(months[month]);
        sb.append(" ");
        sb.append(dayOfMonth);
        sb.append(", ");
        sb.append(year);
        return sb.toString();
    }

    //Returns a string of the selected time
    private String showTimeText(int hour, int minute, boolean is24HourFormat){
        StringBuilder sb = new StringBuilder();
        String format;
        int adjHour;
        if(!is24HourFormat){
            if(hour == 0){
                format = "AM";
                adjHour = hour + 12;
            } else if(hour == 12){
                format = "PM";
                adjHour = hour;
            } else if(hour > 12){
                format = "PM";
                adjHour = hour - 12;
            } else {
                format = "AM";
                adjHour = hour;
            }
        } else {
            format = "";
            adjHour = hour;
        }
        if(adjHour <= 9){
            sb.append("0");
        }
        sb.append(adjHour);
        sb.append(":");
        if(minute <= 9){
            sb.append("0");
        }
        sb.append(minute);
        sb.append(" ");
        sb.append(format);
        return sb.toString();
    }

    //Set up preset for reminder
    private void setReminderPreset(String preset, int delayTime){
        long delay = System.currentTimeMillis() + delayTime;
        calendar.setTimeInMillis(delay);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        alarmPresetsButton.setText(preset);
        alarmDateButton.setText(showDateText(year, month, day));
        alarmTimeButton.setText(showTimeText(hour, minute, is24HourFormat));
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
