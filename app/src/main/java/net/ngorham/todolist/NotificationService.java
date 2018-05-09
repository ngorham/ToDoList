package net.ngorham.todolist;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;


/**
 * To Do List
 * NotificationService.java
 * Purpose: An {@link IntentService} subclass for handling asynchronous task
 * requests in a service on a separate handler thread.
 *
 * @author Neil Gorham
 * @version 1.0 04/30/2018
 */
public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            sendNotification(intent);
        }
    }

    private void sendNotification(Intent intent){
        //Intent variables
        int notificationID = intent.getIntExtra(NotificationPublisher.NOTIFICATION_ID, 0);
        String notificationTitle = intent.getStringExtra(NotificationPublisher.NOTIFICATION_TITLE);
        String notificationText = intent.getStringExtra(NotificationPublisher.NOTIFICATION_TEXT);
        String lastModified = intent.getStringExtra(ListDetailActivity.EXTRA_LIST_LAST_MODIFIED);
        int reminder = intent.getIntExtra(ListDetailActivity.EXTRA_LIST_REMINDER, 0);
        String reminderTime = intent.getStringExtra(ListDetailActivity.EXTRA_LIST_REMINDER_TIME);
        //Intent on click notification
        Intent activityIntent = new Intent(this, ListDetailActivity.class);
        activityIntent.putExtra(ListDetailActivity.EXTRA_LIST_ID, notificationID);
        activityIntent.putExtra(ListDetailActivity.EXTRA_LIST_NAME, notificationTitle);
        activityIntent.putExtra(ListDetailActivity.EXTRA_LIST_LAST_MODIFIED, lastModified);
        activityIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER, reminder);
        activityIntent.putExtra(ListDetailActivity.EXTRA_LIST_REMINDER_TIME, reminderTime);
        //Add parent activity and intent to task stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ListDetailActivity.class);
        stackBuilder.addNextIntent(activityIntent);
        PendingIntent pendingActivityIntent = stackBuilder.getPendingIntent(notificationID, PendingIntent.FLAG_UPDATE_CURRENT);
        //Build notification
        Notification builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_todolist_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingActivityIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, builder);
    }
}
