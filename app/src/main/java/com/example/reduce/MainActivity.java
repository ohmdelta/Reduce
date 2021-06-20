package com.example.reduce;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.example.reduce.database.Product;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;


public class MainActivity extends AppCompatActivity {

  private static final int requestCode = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Realm.init(this);

    Main.dataBase = Realm.getInstance(
			  new RealmConfiguration
					  .Builder()
					  .name("main_database")
					  .allowQueriesOnUiThread(true)
					  .allowWritesOnUiThread(true)
					  .build());

    updateTable();

    // get camera permissions
    while (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, requestCode);
    }

	  MobileAds.initialize(this);

	  AdView mAdView = findViewById(R.id.adView);
	  AdRequest adRequest = new AdRequest.Builder().build();
	  mAdView.loadAd(adRequest);

  }

  public void updateTable() {
    View tableView =  this.findViewById(R.id.barcodeTable);
    assert tableView instanceof LinearLayout;
    LinearLayout table = (LinearLayout) tableView;
    table.removeAllViews();

	  TableLayout tableLayout = (TableLayout) findViewById(R.id.barcodeTable);

	  String date = null;
    for (Product barcode :
        Main.dataBase.where(Product.class).sort("expDate", Sort.ASCENDING).findAll()) {

    	if (!Main.dateFormat.format(barcode.getExpDate()).equals(date)) {
    		TextView dateHeader = new TextView(this);
    		dateHeader
				    .setText(Main.dateFormat.format(barcode.getExpDate()));
				dateHeader
						.setBackgroundColor(Color.GREEN);
				dateHeader
						.setTextColor(Color.WHITE);


    		tableLayout.addView(dateHeader);
	    }

      View tableRow = getLayoutInflater().inflate(R.layout.display_data, null, false);

      ((ImageButton) tableRow.findViewById(R.id.delete_key)).setOnClickListener(new View.OnClickListener() {
	      public void onClick(View v) {

	      	System.out.println(barcode.getBarcodeId());

		      Main.dataBase.executeTransaction( transactionRealm -> {
			      System.out.println(barcode.getBarcodeId());

			      Product product = Main.dataBase
					      .where(Product.class)
					      .equalTo("barcodeId", barcode.getBarcodeId())
					      .findFirst();
			      assert product != null;
			      product.deleteFromRealm();
		      });
		      updateTable();
	      }
      });

      if (barcode.getProductName().isEmpty()) {
        ((TextView) tableRow.findViewById(R.id.data_text)).setText(barcode.getBarcodeId());
      } else {
        ((TextView) tableRow.findViewById(R.id.data_text)).setText(barcode.getProductName());
      }

	    date = Main.dateFormat.format(barcode.getExpDate());

      ((TextView) tableRow.findViewById(R.id.location_display))
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK) {
      updateTable();
    }
  }

  private void addNotification() {
	  NotificationCompat.Builder builder =
			  new NotificationCompat.Builder(this)
					  .setContentTitle("Items about to expire")
					  .setContentText("");

  }
}