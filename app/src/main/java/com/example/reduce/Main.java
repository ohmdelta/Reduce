package com.example.reduce;

import android.app.Application;
import io.realm.Realm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main extends Application {

	public static Realm dataBase;

	public static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	public static Calendar reminderTime;
}
