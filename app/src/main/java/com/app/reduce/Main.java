package com.app.reduce;

import android.app.Application;
import io.realm.Realm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Main extends Application {

    public static Realm dataBase;

    public static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

}
