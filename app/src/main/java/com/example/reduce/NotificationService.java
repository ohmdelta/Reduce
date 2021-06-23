package com.example.reduce;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.reduce.database.Product;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
  public static final String NOTIFICATION_CHANNEL_ID = "10001";
  private static final String default_notification_channel_id = "default";

  Timer timer;
  TimerTask timerTask;
  String TAG = "Timers";
  int Your_X_SECS = 5;

  @Override
  public IBinder onBind(Intent arg0) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    startTimer();
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    stopTimerTask();
    super.onDestroy();
  }

  // we are going to use a handler to be able to run in our TimerTask
  private final Handler handler = new Handler();

  public void startTimer() {
    timer = new Timer();
    initializeTimerTask();
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 8);
    cal.add(Calendar.DAY_OF_YEAR, 1);
    //reminder at 8 am
    timer.schedule(timerTask, cal.getTime()); //
  }

  public void stopTimerTask() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }

  public void initializeTimerTask() {
    timerTask =
        new TimerTask() {
          public void run() {
            handler.post(
                new Runnable() {
                  public void run() {
                    createNotification();
                  }
                });
          }
        };
  }

  private void createNotification() {

  	Realm.init(this);
  	Main.dataBase =
			  Realm.getInstance(
					  new RealmConfiguration.Builder()
							  .name("main_database")
							  .allowQueriesOnUiThread(true)
							  .allowWritesOnUiThread(true)
							  .build());

    StringBuilder string = new StringBuilder("Items to expire: ");

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, 3);

    for (Product barcode :
        Main.dataBase.where(Product.class).sort("expDate", Sort.ASCENDING).findAll()) {
      if (barcode.getExpDate().compareTo(cal.getTime()) >= 0) {
        break;
      } else {
        string.append(" ");
        string.append(barcode.getProductName());
      }
    }

    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(getApplicationContext(), default_notification_channel_id)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Items about to Expire")
            .setContentText(string)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    Intent notificationIntent = new Intent(this, MainActivity.class);
    PendingIntent contentIntent =
        PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(contentIntent);

    // Add as notification
    NotificationManager manager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel notificationChannel =
          new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
      builder.setChannelId(NOTIFICATION_CHANNEL_ID);
      manager.createNotificationChannel(notificationChannel);
    }

    manager.notify((int) System.currentTimeMillis(), builder.build());
  }
}

