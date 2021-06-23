package com.example.reduce;

import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.reduce.database.Product;

import java.util.Calendar;
import java.util.Date;

public class DisplayProductInfo extends AppCompatActivity {

	private String barcodeId;
	private Product product;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_product_info);

		barcodeId = getIntent().getStringExtra("Extra_barcodeId");

		product = Main.dataBase
				.where(Product.class)
				.beginsWith("barcodeId", barcodeId)
				.findFirst();

		((TextView) findViewById(R.id.barcodeId))
				.setText(barcodeId);
		((EditText) findViewById(R.id.product_name))
				.setText(product.getProductName());
		Calendar expDate = Calendar.getInstance();
		expDate.setTime(product.getExpDate());
		((DatePicker) findViewById(R.id.expiration_picker))
				.updateDate(expDate.get(Calendar.YEAR),
						expDate.get(Calendar.MONTH),
						expDate.get(Calendar.DAY_OF_MONTH));

		((EditText) findViewById(R.id.location)).setText(product.getLocation());

	}

	public void editProduct(View view) {
		Product temporary = new Product(barcodeId);

		temporary.setProductName(((EditText) findViewById(R.id.product_name))
				.getText()
				.toString());

		DatePicker expiryPicker = findViewById(R.id.expiration_picker);

		// calendar get date
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, expiryPicker.getYear());
		cal.set(Calendar.MONTH, expiryPicker.getMonth());
		cal.set(Calendar.DAY_OF_MONTH, expiryPicker.getDayOfMonth());

		temporary.setExpDate(cal.getTime());

		temporary.setLocation(((EditText) findViewById(R.id.location)).getText().toString());

		Main.dataBase.executeTransaction (transactionRealm -> {
			transactionRealm.insertOrUpdate(temporary);
		});

		setResult(RESULT_OK, null);

	}
}