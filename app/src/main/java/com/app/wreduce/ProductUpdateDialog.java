package com.app.wreduce;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import com.app.wreduce.database.Product;
import com.app.wreduce.timers.CalendarFunctions;
import com.example.reduce.R;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class ProductUpdateDialog extends DialogFragment {
	private final String barcodeId;

	public ProductUpdateDialog(String displayValue) {
		this.barcodeId = displayValue;
	}

	@NotNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = requireActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.scanner_dialog, null);

		builder
				.setView(dialogView)
				.setMessage(barcodeId)
				.setPositiveButton(
						"OK",
						(dialog, id) -> {
							DatePicker expiryPicker = dialogView.findViewById(R.id.expiration_picker);

							Calendar cal = CalendarFunctions.getDateFromPicker(expiryPicker);

							Product product =
									new Product(
											barcodeId,
											((EditText) dialogView.findViewById(R.id.product_type)).getText().toString(),
											((EditText) dialogView.findViewById(R.id.location)).getText().toString(),
											1,
											cal.getTime());

							MainDatabase.dataBase.executeTransaction(
									transactionRealm -> transactionRealm.insertOrUpdate(product));
						})
				.setNegativeButton("cancel", (dialog, id) -> {})
				.setTitle("Set barcode value: " + barcodeId);

		return builder.create();
	}
}
