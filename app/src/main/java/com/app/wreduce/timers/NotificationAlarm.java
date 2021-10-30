package com.app.wreduce.timers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.app.wreduce.Main;
import com.app.wreduce.MainActivity;
import com.example.reduce.R;
import com.app.wreduce.database.Product;

import io.realm.Sort;

import java.util.Calendar;

public class NotificationAlarm extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		StringBuilder string = new StringBuilder("Items to expire: ");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 3);

		for (Product barcode :
				Main.dataBase.where(Product.class)
						.sort("expDate", Sort.ASCENDING).findAll()) {
			if (barcode.getExpDate().compareTo(cal.getTime()) >= 0) {
				break;
			} else {
				string.append(" ");
				string.append(barcode.getProductName());
			}
		}

		NotificationCompat.Builder builder =
				new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_launcher_background)
						.setContentTitle("Items about to Expire")
						.setContentText(string)
						.setAutoCancel(true)
						.setWhen(System.currentTimeMillis())
						.setPriority(NotificationCompat.PRIORITY_DEFAULT);

		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent contentIntent = PendingIntent.getBroadcast(context, 10000,
						notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);

		NotificationManager manager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(5, builder.build());

	}
}
