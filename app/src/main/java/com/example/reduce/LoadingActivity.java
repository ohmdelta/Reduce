package com.example.reduce;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        new Handler()
            .postDelayed(
                    () -> {
                      startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                      finish();
                    },1000);
    }
}
