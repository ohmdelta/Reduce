package com.app.reduce;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.reduce.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class LoadingActivity extends AppCompatActivity {

	private static final int requestCode = 100;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_screen);

		new Handler()
			.postDelayed(
					() -> {
					  startActivity(new Intent(LoadingActivity.this, MainActivity.class));
						Realm.init(this);

						Main.dataBase =
								Realm.getInstance(
										new RealmConfiguration.Builder()
												.name("main_database")
												.allowQueriesOnUiThread(true)
												.allowWritesOnUiThread(true)
												.build());

						// get camera permissions
						if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
								== PackageManager.PERMISSION_DENIED) {
							ActivityCompat.requestPermissions(
									this, new String[] {Manifest.permission.CAMERA}, requestCode);
						}

						if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
								== PackageManager.PERMISSION_DENIED) {
							ActivityCompat.requestPermissions(
									this, new String[] {Manifest.permission.INTERNET}, requestCode);
						}

					  finish();
					},1000);
	}
}
