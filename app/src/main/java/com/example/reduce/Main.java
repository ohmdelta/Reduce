package com.example.reduce;

import android.app.Application;
import androidx.collection.ArraySet;
import com.google.mlkit.vision.barcode.Barcode;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main extends Application {

	public static Realm dataBase;

	public static DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
}
