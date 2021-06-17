package com.example.reduce;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;
import com.example.reduce.Database.ProductData;
import com.example.reduce.Database.ProductDatabase;
import com.example.reduce.Database.ProductViewModel;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.mlkit.vision.barcode.Barcode;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

  private static final int requestCode = 100;

	@Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Main.db =  new ViewModelProvider(this).get(ProductViewModel.class);
		Main.db.getProductData().observe(this, productData -> {

		});

/*		ProductData productData1 = new ProductData();
		productData1.barcodeID = "hi";

		db.insert(productData1);

		LiveData<List<ProductData>> dbData = db.getProductData();
		System.out.println(dbData.getValue());
		for (ProductData productData: dbData.getValue()) {
			System.out.println(productData.barcodeID);
		}*/


    /*Main.db = Room.databaseBuilder(getApplicationContext(),
			  ProductDatabase.class, "mainDB").allowMainThreadQueries().build();
*/
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


/*    for (ProductData data : ) ) {
      Button button = new Button(this);
      button.setText(data.barcodeID);
//      byte[] b = barcode.getRawBytes();

      table.addView(button);
    }*/

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