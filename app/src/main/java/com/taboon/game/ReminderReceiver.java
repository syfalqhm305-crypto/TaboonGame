package com.taboon.game;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "taboon_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        String name = context
                .getSharedPreferences("game_data", Context.MODE_PRIVATE)
                .getString("name", "أيهم");

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "تذكيرات طبون",
                            NotificationManager.IMPORTANCE_DEFAULT
                    );

            manager.createNotificationChannel(channel);
        }

        Notification.Builder notification;

        if (Build.VERSION.SDK_INT >= 26) {

            notification =
                    new Notification.Builder(
                            context,
                            CHANNEL_ID
                    );

        } else {

            notification =
                    new Notification.Builder(context);
        }

        notification
                .setSmallIcon(
                        android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                        "🍑 طبون " + name
                )
                .setContentText(
                        "لا تنسى تضرب طبون " +
                        name +
                        " 😂"
                )
                .setAutoCancel(true);

        manager.notify(
                1001,
                notification.build()
        );
    }
}
