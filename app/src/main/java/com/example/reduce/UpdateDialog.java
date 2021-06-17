package com.example.reduce;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import androidx.fragment.app.DialogFragment;
import com.example.reduce.Database.ProductDao;
import com.example.reduce.Database.ProductDao_Impl;
import com.example.reduce.Database.ProductData;
import org.jetbrains.annotations.NotNull;
import android.view.View;

import java.util.List;

public class UpdateDialog extends DialogFragment {

  private String value;

  public UpdateDialog(String displayValue) {
    this.value = displayValue;
  }

  @NotNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = requireActivity().getLayoutInflater();
    builder
        .setView(inflater.inflate(R.layout.scanner_dialog, null))
        .setMessage(value)
        .setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
	              /*ProductDao productDao = Main.db.productDao();
	              ProductData product = new ProductData();

	              EditText product_type = getActivity().findViewById(R.id.product_type);
	              SeekBar seekBar = getActivity().findViewById(R.id.Quantity);

								product.productName = product_type.getText().toString();
								product.barcodeID = value;
	              product.quantity = seekBar.getProgress();
	              productDao.insert(product);*/
              }
            })
        .setNegativeButton(
            "cancel",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
              }
            })
        .setTitle("Set barcode value: " + value);
    // Create the AlertDialog object and return it
    return builder.create();
  }
}
