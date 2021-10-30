package com.app.wreduce;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.app.wreduce.database.Product;
import com.app.wreduce.timers.CalendarFunctions;
import com.app.wreduce.timers.NotificationAlarm;
import com.example.reduce.BuildConfig;
import com.example.reduce.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import io.realm.Sort;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this);

        // init ads
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        // init product table
        updateTable();
        super.onStart();
    }

    /***
     * Hard updates table:
     * Clears table & reinserts all products back in order
     * TODO: reimplement to be more efficient using second list of products to add
     */
    public void updateTable() {
        View tableView = this.findViewById(R.id.barcodeTable);
        if (BuildConfig.DEBUG && !(tableView instanceof LinearLayout)) {
            throw new AssertionError("Assertion failed");
        }

        LinearLayout table = (LinearLayout) tableView;

        // clear table
        table.removeAllViews();

        TableLayout tableLayout = findViewById(R.id.barcodeTable);

        Calendar date = Calendar.getInstance();
        date.clear();

        for (Product barcode :
                Main.dataBase.where(Product.class).sort("expDate", Sort.ASCENDING).findAll()) {

            Calendar date1 = (Calendar) date.clone();
            date1.add(Calendar.DATE, 1);

            Date barcodeExpiryDate = barcode.getExpDate();

            if (barcodeExpiryDate.after(date1.getTime())) {

                addDateSplitter(tableLayout, barcodeExpiryDate);

                date.setTime(barcodeExpiryDate);
                CalendarFunctions.clearTimeOfDay(date);

            }

            View tableRow = getLayoutInflater().inflate(R.layout.display_data, null, false);

            productDeleteHandler(barcode, tableRow);

            if (barcode.getProductName().isEmpty()) {
                ((TextView) tableRow.findViewById(R.id.data_text)).setText(barcode.getBarcodeId());
            } else {
                ((TextView) tableRow.findViewById(R.id.data_text)).setText(barcode.getProductName());
            }

            tableRow.findViewById(R.id.data_text)
                    .setOnClickListener(v -> displayProductDetails(barcode.getBarcodeId()));

            ((TextView) tableRow
                    .findViewById(R.id.location_display))
                    .setText(barcode.getLocation());

            tableLayout.addView(tableRow);
        }
    }

    /***
     * Add a date divider to distinguish between product exp dates
     * @param tableLayout product table
     * @param barcodeExpiryDate barcode expiry date
     */
    private void addDateSplitter(TableLayout tableLayout, Date barcodeExpiryDate) {
        TextView dateHeader = new TextView(this);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        dateHeader.setText(dateFormat.format(barcodeExpiryDate));
        setColourOfBanner(barcodeExpiryDate, dateHeader);

        tableLayout.addView(dateHeader);
    }

    /***
     * Delete button in table
     * Onclick delete item from table and database
     * @param barcode barcode
     * @param tableRow row of database table
     */
    private void productDeleteHandler(Product barcode, View tableRow) {
        tableRow.findViewById(R.id.delete_key)
                .setOnClickListener(
                        v -> {
                            barcode.delete();

                            // TODO: don't use updateTable()
                            updateTable();
                        });
    }

    /**
     * Sets colour of banners/dividers according to expiry dates
     * @param barcodeDate barcode date of expiry
     * @param dateHeader divider TextView
     */
    private void setColourOfBanner(Date barcodeDate, TextView dateHeader) {
        // Get date + 3
        Calendar currentDayAddThree = CalendarFunctions.offsetTodayBy(3);
        CalendarFunctions.clearTimeOfDay(currentDayAddThree);

        // barcode date < current time
        if (barcodeDate.before(Calendar.getInstance().getTime()))
            dateHeader.setBackgroundColor(Color.RED);

        // barcode date < current time + 3 days
        else if (barcodeDate.before(currentDayAddThree.getTime()))
            dateHeader.setBackgroundColor(Color.YELLOW);

        // barcode date not about to expire
        else
            dateHeader.setBackgroundColor(Color.GREEN);

        dateHeader.setTextColor(Color.BLACK);
    }

    public void openScannerActivity(View view) {
        Intent intent = new Intent(this, ScannerActivity.class);
        //    intent.putExtra("BarcodeSet",barcodeSet);
        startActivityForResult(intent, 1);
    }

    public void openSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /***
     * Start activity to display product details
     * @param barcodeId barcodeId as string
     */
    public void displayProductDetails(String barcodeId) {
        Intent intent = new Intent(this, DisplayProductInfo.class);
        intent.putExtra("Extra_barcodeId", barcodeId);

        startActivityForResult(intent,1);
    }

    /**
    * Update table if added new products
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            updateTable();
        }
    }

    /**
     * On exit set notifications
     * */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean notificationsOn = sharedPreferences.getBoolean("reminder", false);

        if (notificationsOn) {

            Intent myIntent = new Intent(this, NotificationAlarm.class);
            int ALARM1_ID = 10000;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, getAlarmTimeAsLong(), pendingIntent);

        }
    }

    /***
     * Get alarm/reminder time as long
     * Alarm time <= current time: append 1 day
     * @return reminder time as long
     */
    private long getAlarmTimeAsLong() {
        SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(this);

        SimpleDateFormat timeFormat
                = new SimpleDateFormat("hh:mm", Locale.getDefault());

        Calendar cal = Calendar.getInstance();

        try {
            Calendar time = Calendar.getInstance();
            String userTime = sharedPreferences.getString("time", "12:00");

            time.setTime(Objects.requireNonNull(timeFormat.parse(userTime)));

            CalendarFunctions.setTimeTo(cal,
		            time.get(Calendar.HOUR_OF_DAY),
		            time.get(Calendar.MINUTE),
		            0);

        } catch (ParseException ignored) {
        }

        if (cal.compareTo(Calendar.getInstance()) <= 0) {
            cal.add(Calendar.DATE,1);
        }

        System.out.println(cal.getTime());
        return cal.getTimeInMillis();

    }
}
