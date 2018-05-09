package net.ngorham.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * To Do List
 * NotificationPublisher.java
 * Purpose: Starts an intent service for sending notifications
 *
 * @author Neil Gorham
 * @version 1.0 04/23/2018
 */

public class NotificationPublisher extends BroadcastReceiver {
    //Public constants
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION_TITLE = "notification_title";
    public static final String NOTIFICATION_TEXT = "notification_text";
    //Private variables
    private final String TAG = "NotificationPublisher";
    private Context context;
    private Calendar calendar;
    private int notificationID;
    private String notificationTitle;
    private String notificationText;
    private int repetition;
    private long triggerTime;
    private String lastModified;
    private int reminder;
    private String reminderTime;

    @Override
    public void onReceive(Context context, Intent intent){
        this.context = context;
        calendar = Calendar.getInstance();
        //Set variables for notification service
        notificationID = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationTitle = intent.getStringExtra(ListDetailActivity.EXTRA_LIST_NAME);
        notificationText = intent.getStringExtra(ListDetailActivity.EXTRA_LIST_ITEMS);
        repetition = intent.getIntExtra(ListDetailActivity.EXTRA_LIST_ALARM_REPETITION, 0);
        triggerTime = intent.getLongExtra(ListDetailActivity.EXTRA_LIST_ALARM_TRIGGER_TIME, 0);
        lastModified = intent.getStringExtra(ListDetailActivity.EXTRA_LIST_LAST_MODIFIED);
        reminder = intent.getIntExtra(ListDetailActivity.EXTRA_LIST_REMINDER, 0);
        reminderTime = intent.getStringExtra(ListDetailActivity.EXTRA_LIST_REMINDER_TIME);
        if(repetition == 2) { //Mon - Fri
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if(day == 2){ //Mon
                setAlarm(AlarmManager.INTERVAL_DAY);
            } else if(day == 6){ //Fri
                long interval = 3*24*60*60*1000;
                setAlarm(interval);
            }
        } else if(repetition == 5){ //Monthly
            setMonthlyAlarm();
        } else if(repetition == 6){ //Yearly
            setYearlyAlarm();
        }
        startNotificationService();
    }

    //Set alarm for repetition
    private void setAlarm(long interval){
        //Create intent
        Intent alarmIntent = new Intent(context, NotificationPublisher.class);
        alarmIntent.putExtra(NOTIFICATION_ID, notificationID);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_NAME, notificationTitle);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_ITEMS, notificationText);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_LAST_MODIFIED, lastModified);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER, reminder);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER_TIME, reminderTime);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_ALARM_REPETITION, repetition);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_ALARM_TRIGGER_TIME, triggerTime);
        PendingIntent pendingAlarmIntent = PendingIntent.getService(context, notificationID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Set alarm
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, interval, pendingAlarmIntent);
    }

    //Set alarm for one month later
    private void setMonthlyAlarm(){
        //Set calendar
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month + 1);
        triggerTime = calendar.getTimeInMillis();
        //Create intent
        Intent alarmIntent = new Intent(context, NotificationPublisher.class);
        alarmIntent.putExtra(NOTIFICATION_ID, notificationID);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_NAME, notificationTitle);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_ITEMS, notificationText);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_LAST_MODIFIED, lastModified);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER, reminder);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER_TIME, reminderTime);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_ALARM_REPETITION, repetition);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_ALARM_TRIGGER_TIME, triggerTime);
        PendingIntent pendingAlarmIntent = PendingIntent.getService(context, notificationID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //set alarm
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingAlarmIntent);
    }

    //Set alarm for one year later
    private void setYearlyAlarm(){
        //Set calendar
        int year = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.YEAR, year + 1);
        triggerTime = calendar.getTimeInMillis();
        //Create intent
        Intent alarmIntent = new Intent(context, NotificationPublisher.class);
        alarmIntent.putExtra(NOTIFICATION_ID, notificationID);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_NAME, notificationTitle);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_ITEMS, notificationText);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_LAST_MODIFIED, lastModified);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER, reminder);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER_TIME, reminderTime);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_ALARM_REPETITION, repetition);
        alarmIntent.putExtra(ListDetailActivity.EXTRA_LIST_ALARM_TRIGGER_TIME, triggerTime);
        PendingIntent pendingAlarmIntent = PendingIntent.getService(context, notificationID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //set alarm
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingAlarmIntent);
    }

    //Start service to display notification
    private void startNotificationService(){
        //Create intent
        Intent notificationIntent = new Intent(context, NotificationService.class);
        notificationIntent.putExtra(NOTIFICATION_ID, notificationID);
        notificationIntent.putExtra(NOTIFICATION_TITLE, notificationTitle);
        notificationIntent.putExtra(NOTIFICATION_TEXT, notificationText);
        notificationIntent.putExtra(ListDetailActivity.EXTRA_LIST_LAST_MODIFIED, lastModified);
        notificationIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER, reminder);
        notificationIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER_TIME, reminderTime);
        context.startService(notificationIntent);
    }
}
