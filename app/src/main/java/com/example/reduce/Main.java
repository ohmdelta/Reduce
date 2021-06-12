package com.example.reduce;

import android.app.Application;
import androidx.collection.ArraySet;
import com.google.mlkit.vision.barcode.Barcode;

import java.util.*;

public class Main extends Application {

  //TODO: change <String> to <custom.class> for Priority Queue
  public static HashSet<customBarcode> barcodes = new HashSet<>();

  public static Queue<Set<customBarcode>> parsedBarcodes = new PriorityQueue<>();

//  public HashSet<String> barcodes = new HashSet<>();
  
}
