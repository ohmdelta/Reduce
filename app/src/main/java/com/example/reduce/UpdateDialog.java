package com.example.reduce;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.fragment.app.DialogFragment;
import org.jetbrains.annotations.NotNull;

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
              public void onClick(DialogInterface dialog, int id) {}
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
