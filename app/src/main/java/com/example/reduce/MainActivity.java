package com.example.reduce;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.reduce.database.Product;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.mlkit.vision.barcode.Barcode;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;

import java.util.HashSet;
import java.util.Set;

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

/*    System.out.println("permission:" +
        (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_DENIED)
    );*/

    MobileAds.initialize(this, initializationStatus -> {
    });
  }

  public void updateTable() {
    View tableView =  this.findViewById(R.id.barcodeTable);
    assert tableView instanceof LinearLayout;
    LinearLayout table = (LinearLayout) tableView;
    table.removeAllViews();

    for (Product barcode :
        Main.dataBase
		        .where(Product.class)
		        .sort("expDate", Sort.ASCENDING)
		        .findAll()) {

      Button button = new Button(this);
      button.setText(barcode.getProductName() + ": " + Main.dateFormat.format(barcode.getExpDate()));
      //      byte[] b = barcode.getRawBytes();

      table.addView(button);
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

}