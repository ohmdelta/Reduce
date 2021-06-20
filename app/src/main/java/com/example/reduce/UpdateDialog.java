package com.example.reduce;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import com.example.reduce.database.Product;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class UpdateDialog extends DialogFragment {

  private String barcodeId;

  public UpdateDialog(String displayValue) {
    this.barcodeId = displayValue;
  }

  private EditText productType;
  @NotNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

  	// Use the Builder class for convenient dialog construction
	  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = requireActivity().getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.scanner_dialog, null);

    /*Product initialProduct = new Product(barcodeId);
	  Main.dataBase.executeTransaction (transactionRealm -> {
		  transactionRealm.insertOrUpdate(initialProduct);
	  });*/

    builder
        .setView(dialogView)
        .setMessage(barcodeId)
        .setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {

	              DatePicker expiryPicker = dialogView.findViewById(R.id.expiration_picker);

	              // calendar get date
	              Calendar cal = Calendar.getInstance();
	              cal.set(Calendar.YEAR, expiryPicker.getYear());
	              cal.set(Calendar.MONTH, expiryPicker.getMonth());
	              cal.set(Calendar.DAY_OF_MONTH, expiryPicker.getDayOfMonth());

	              Product product = new Product(
			              barcodeId,
              			((EditText) dialogView.findViewById(R.id.product_type))
					              .getText()
					              .toString(),
			              ((EditText) dialogView.findViewById(R.id.location))
					              .getText()
					              .toString(),
			              1,
			              cal.getTime()
	              );

	              Main.dataBase.executeTransaction (transactionRealm -> {
									  transactionRealm.insertOrUpdate(product);
	              });


              }
            })
        .setNegativeButton(
            "cancel",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
              }
            })
        .setTitle("Set barcode value: " + barcodeId);
    // Create the AlertDialog object and return it
	  return builder.create();
  }


}
