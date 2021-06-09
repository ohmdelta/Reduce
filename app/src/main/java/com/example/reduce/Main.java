package com.example.reduce;

import android.app.Application;
import androidx.collection.ArraySet;
import com.google.mlkit.vision.barcode.Barcode;

import java.util.HashSet;
import java.util.Set;

public class Main extends Application {

  //TODO: change <String> to <custom.class> for Priority Queue
  public static HashSet<Barcode> barcodes = new HashSet<>();

//  public HashSet<String> barcodes = new HashSet<>();
  
}
