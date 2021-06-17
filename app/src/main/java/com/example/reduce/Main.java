package com.example.reduce;

import android.app.Application;
import androidx.collection.ArraySet;
import androidx.room.Room;
import com.example.reduce.Database.ProductDatabase;
import com.example.reduce.Database.ProductViewModel;
import com.google.mlkit.vision.barcode.Barcode;

import java.util.*;

public class Main extends Application {

  //TODO: change <String> to <custom.class> for Priority Queue
  public static HashSet<customBarcode> barcodes = new HashSet<>();

//  public static Queue<Set<customBarcode>> parsedBarcodes = new PriorityQueue<>();
	public static ProductViewModel db;

}
