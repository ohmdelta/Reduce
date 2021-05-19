package com.example.reduce;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class ScannerActivity extends AppCompatActivity {



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scanner);

    while (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 100);
    }

    startCamera();

    View toggle = findViewById(R.id.flashToggle);
    assert toggle instanceof ToggleButton;

    ToggleButton toggleListener = (ToggleButton) toggle;

    toggleListener.setOnCheckedChangeListener((buttonView, isChecked) -> {

      camera.getCameraControl().enableTorch(isChecked);

    });

  }

  private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
  private Camera camera;

  private void startCamera() {

    cameraProviderFuture = ProcessCameraProvider.getInstance(this);

    cameraProviderFuture.addListener(() -> {
      try {
        ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
        bindPreview(cameraProvider);
      } catch (ExecutionException | InterruptedException e) {
        // No errors need to be handled for this Future.
        // This should never be reached.
      }
    }, ContextCompat.getMainExecutor(this));

    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

  }

  private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
    Preview preview = new Preview.Builder()
        .build();

    CameraSelector cameraSelector = new CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build();

    PreviewView cameraView = findViewById(R.id.cameraView);
    preview.setSurfaceProvider(cameraView.getSurfaceProvider());

    camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);

//    camera.getCameraInfo().hasFlashUnit();

  }

  /*private boolean toggle;

  public void toggleFlashlight(View view) {
    toggle = !toggle;
    camera.getCameraControl().enableTorch(toggle);

//    int toggleButtonId = R.id.flashButton;

  }*/
}