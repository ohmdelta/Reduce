package com.example.reduce.Database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ProductData.class}, version = 1, exportSchema = false)
public abstract class ProductDatabase extends RoomDatabase {
	public abstract ProductDao productDao();
	/*private static volatile ProductDatabase INSTANCE;

	private static final int NUMBER_OF_THREADS = 4;
	static final ExecutorService databaseWriteExecutor =
			Executors.newFixedThreadPool(NUMBER_OF_THREADS);


	static ProductDatabase getDatabase(final Context context) {
		if (INSTANCE == null) {
			synchronized (ProductDatabase.class) {
				if (INSTANCE == null) {
					INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
							ProductDatabase.class, "word_database")
							.build();
				}
			}
		}
		return INSTANCE;
	}*/
}
