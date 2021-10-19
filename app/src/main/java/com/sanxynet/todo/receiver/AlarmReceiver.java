package com.sanxynet.todo.receiver;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.core.app.NotificationCompat;

import com.sanxynet.todo.helper.IntentConstants;
import com.sanxynet.todo.R;
import com.sanxynet.todo.model.Todo;
import com.sanxynet.todo.data.TodoDao;
import com.sanxynet.todo.data.TodoDatabase;
import com.sanxynet.todo.ui.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

    long todoId;
    Todo caseTodo;
    TodoDao todoDao;

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(final Context context, Intent intent) {
        todoId = intent.getLongExtra(IntentConstants.JOURNAL,-1);

        TodoDatabase database = TodoDatabase.getInstance(context);
        todoDao = database.todoDao();

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                caseTodo = todoDao.getTodoById(todoId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentTitle(caseTodo.getJournalName())
                        .setContentText(caseTodo.getJournalDesc())
                        .setDefaults(NotificationCompat.DEFAULT_LIGHTS|NotificationCompat.DEFAULT_SOUND)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(bitmap)
                        .setAutoCancel(true);

                Intent resultIntent = new Intent(context, MainActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) todoId,resultIntent,
                        PendingIntent.FLAG_ONE_SHOT);

                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) context.
                        getSystemService(context.NOTIFICATION_SERVICE);
                notificationManager.notify((int) todoId,builder.build());

            }
        }.execute();
    }

}
