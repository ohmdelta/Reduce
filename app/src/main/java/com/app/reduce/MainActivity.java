package com.app.reduce;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.example.reduce.R;
import com.app.reduce.database.Product;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import io.realm.Sort;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        updateTable();
        super.onStart();
    }

    public void updateTable() {
        View tableView = this.findViewById(R.id.barcodeTable);
        assert tableView instanceof LinearLayout;
        LinearLayout table = (LinearLayout) tableView;
        table.removeAllViews();

        TableLayout tableLayout = findViewById(R.id.barcodeTable);

        Calendar calendar = Calendar.getInstance();
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.add(Calendar.DAY_OF_YEAR, 3);

        Calendar date = Calendar.getInstance();
        date.clear();

        for (Product barcode :
                Main.dataBase.where(Product.class).sort("expDate", Sort.ASCENDING).findAll()) {

            Calendar date1 = (Calendar) date.clone();
            date1.add(Calendar.DAY_OF_YEAR,1);

            if ((barcode.getExpDate()).after(date1.getTime())) {

                TextView dateHeader = new TextView(this);
                dateHeader.setText(Main.dateFormat.format(barcode.getExpDate()));

                if ((barcode.getExpDate()).before(Calendar.getInstance().getTime()))
                    dateHeader.setBackgroundColor(Color.RED);
                else if ((barcode.getExpDate()).before(calendar.getTime()))
                    dateHeader.setBackgroundColor(Color.YELLOW);
                else
                    dateHeader.setBackgroundColor(Color.GREEN);

                dateHeader.setTextColor(Color.BLACK);
                tableLayout.addView(dateHeader);

                date.setTime(barcode.getExpDate());
                date.clear(Calendar.HOUR);
                date.clear(Calendar.MINUTE);
                date.clear(Calendar.SECOND);

            }

            View tableRow = getLayoutInflater().inflate(R.layout.display_data, null, false);

            ((ImageButton) tableRow.findViewById(R.id.delete_key))
                    .setOnClickListener(
                            v -> {
                                Main.dataBase.executeTransaction(
                                        transactionRealm -> {
                                            Product product =
                                                    Main.dataBase
                                                            .where(Product.class)
                                                            .equalTo("barcodeId", barcode.getBarcodeId())
                                                            .findFirst();
                                            assert product != null;
                                            product.deleteFromRealm();
                                        });
                                updateTable();
                            });

            if (barcode.getProductName().isEmpty()) {
                ((TextView) tableRow.findViewById(R.id.data_text)).setText(barcode.getBarcodeId());
            } else {
                ((TextView) tableRow.findViewById(R.id.data_text)).setText(barcode.getProductName());
            }

            tableRow.findViewById(R.id.data_text)
                    .setOnClickListener(v -> {
                        displayProductDetails(v, barcode.getBarcodeId());
                    });


            ((TextView) tableRow
                    .findViewById(R.id.location_display))
                    .setText(barcode.getLocation());

            tableLayout.addView(tableRow);
        }
    }

    // open scanner activity
    public void scan(View view) {
        Intent intent = new Intent(this, ScannerActivity.class);
        //    intent.putExtra("BarcodeSet",barcodeSet);
        startActivityForResult(intent, 1);
    }

    public void settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void displayProductDetails(View view, String barcodeId) {
        Intent intent = new Intent(this, DisplayProductInfo.class);
        intent.putExtra("Extra_barcodeId", barcodeId);

        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            updateTable();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean notificationsOn = sharedPreferences.getBoolean("reminder", false);

        if (notificationsOn) {

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 1);

            Intent myIntent = new Intent(this, NotificationAlarm.class);
            int ALARM1_ID = 10000;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, getTime(), pendingIntent);

        }
    }

    public long getTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());

        Calendar cal = Calendar.getInstance();

        try {
            Calendar time = Calendar.getInstance();
            String userTime = sharedPreferences.getString("time", "12:00");

            time.setTime(dateFormat.parse(userTime));
            cal.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
        } catch (ParseException e) {
        }

        if (cal.compareTo(Calendar.getInstance()) <= 0) {
            cal.add(Calendar.DATE,1);
        }

        System.out.println(cal.getTime());
        return cal.getTimeInMillis();

    }
}
