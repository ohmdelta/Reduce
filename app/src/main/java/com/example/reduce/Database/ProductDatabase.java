package com.example.reduce.Database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ProductData.class}, version = 2, exportSchema = false)
public abstract class ProductDatabase extends RoomDatabase {
	public abstract ProductDao productDao();
	private static volatile ProductDatabase INSTANCE;

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
	}

	private static ProductDatabase.Callback db = new RoomDatabase.Callback() {
		@Override
		public void onCreate(@NonNull SupportSQLiteDatabase db) {
			super.onCreate(db);

			// If you want to keep data through app restarts,
			// comment out the following block
			databaseWriteExecutor.execute(() -> {
				// Populate the database in the background.
				// If you want to start with more words, just add them.
				ProductDao dao = INSTANCE.productDao();
				dao.deleteAll();

				ProductData data = new ProductData();
				data.barcodeID = "hello";
				dao.insert(data);
			});
		}
	};
}
