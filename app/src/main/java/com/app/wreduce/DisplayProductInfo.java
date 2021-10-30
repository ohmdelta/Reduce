package com.app.wreduce;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.reduce.R;
import com.app.wreduce.database.Product;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;

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

        NumberPicker numberPicker = findViewById(R.id.quantity);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(99);
        numberPicker.setWrapSelectorWheel(false);

        cancel();

        AdView mAdView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void init() {
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
        ((NumberPicker) findViewById(R.id.quantity)).setValue(product.getQuantity());
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
        temporary.setQuantity(((NumberPicker) findViewById(R.id.quantity)).getValue());

        Main.dataBase.executeTransaction (transactionRealm -> transactionRealm.insertOrUpdate(temporary));

        setResult(RESULT_OK, null);

        cancel(view);

    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    public void enableEdit(View view) {
        findViewById(R.id.close_btn).setVisibility(View.GONE);
        findViewById(R.id.edit_button).setVisibility(View.GONE);

        findViewById(R.id.edit_product).setVisibility(View.VISIBLE);
        findViewById(R.id.cancel_button).setVisibility(View.VISIBLE);

        // enable inputs
        ((EditText) findViewById(R.id.product_name)).setInputType(InputType.TYPE_CLASS_TEXT);
        ((EditText) findViewById(R.id.location)).setInputType(InputType.TYPE_CLASS_TEXT);
        findViewById(R.id.expiration_picker).setEnabled(true);
        findViewById(R.id.quantity).setEnabled(true);

    }

    public void cancel(View view) {
        cancel();
    }

    public void cancel() {
        init();

        // disable previous buttons
        findViewById(R.id.edit_product).setVisibility(View.GONE);
        findViewById(R.id.cancel_button).setVisibility(View.GONE);

        // enable edit buttons
        findViewById(R.id.close_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.edit_button).setVisibility(View.VISIBLE);

        // disable inputs
        ((EditText) findViewById(R.id.product_name)).setInputType(InputType.TYPE_NULL);
        ((EditText) findViewById(R.id.location)).setInputType(InputType.TYPE_NULL);
        findViewById(R.id.expiration_picker).setEnabled(false);
        findViewById(R.id.quantity).setEnabled(false);
    }

}