package com.example.reduce;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.example.reduce.database.Product;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

  private static final int requestCode = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Realm.init(this);

    Main.dataBase =
        Realm.getInstance(
            new RealmConfiguration.Builder()
                .name("main_database")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build());

    updateTable();

    // get camera permissions
    while (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(
          this, new String[] {Manifest.permission.CAMERA}, requestCode);
    }

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
        == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(
          this, new String[] {Manifest.permission.INTERNET}, requestCode);
    }

    MobileAds.initialize(this);

    AdView mAdView = findViewById(R.id.adView);
    AdRequest adRequest = new AdRequest.Builder().build();
    mAdView.loadAd(adRequest);

  }

  public void updateTable() {
    View tableView = this.findViewById(R.id.barcodeTable);
    assert tableView instanceof LinearLayout;
    LinearLayout table = (LinearLayout) tableView;
    table.removeAllViews();

    TableLayout tableLayout = findViewById(R.id.barcodeTable);

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, 3);

    Date date = Calendar.getInstance().getTime();
    for (Product barcode :
        Main.dataBase.where(Product.class).sort("expDate", Sort.ASCENDING).findAll()) {

      if (!(barcode.getExpDate()).equals(date)) {

        TextView dateHeader = new TextView(this);
        dateHeader.setText(Main.dateFormat.format(barcode.getExpDate()));

        if ((barcode.getExpDate()).compareTo(date) <= 0)
          dateHeader.setBackgroundColor(Color.RED);
        else if ((barcode.getExpDate()).compareTo(calendar.getTime()) <= 0)
        	dateHeader.setBackgroundColor(Color.YELLOW);
        else
        	dateHeader.setBackgroundColor(Color.GREEN);

        dateHeader.setTextColor(Color.BLACK);
        tableLayout.addView(dateHeader);
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
		      .setOnClickListener(v ->
		      {
		        displayProductDetails(v, barcode.getBarcodeId());
		      }
      );

      date = barcode.getExpDate();

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
	protected void onDestroy () {
		super.onDestroy();

		NotificationManager manager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancelAll();

		startService(new Intent(this, NotificationService.class));
	}
}
