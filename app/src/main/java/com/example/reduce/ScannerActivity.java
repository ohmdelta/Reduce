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
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ScannerActivity extends AppCompatActivity {

  private BarcodeScannerOptions options  =
      new BarcodeScannerOptions.Builder()
          .setBarcodeFormats(
              Barcode.FORMAT_CODE_128,
              Barcode.FORMAT_QR_CODE)
          .build();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scanner);

    Bundle bundle = getIntent().getExtras();
//    barcodeSet = (Set) bundle.get("BarcodeSet");

    Main.barcodes.add("hi");

    while (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 100);
    }

    startCamera();

    View toggle = findViewById(R.id.flashToggle);
    toggleFlash(toggle);
  }


  // Camera Functions:

  // Turn on/off Flash
  private void toggleFlash(View v) {
    assert v instanceof ToggleButton;

    ToggleButton toggleListener = (ToggleButton) v;
    toggleListener.setOnCheckedChangeListener((buttonView, isChecked) -> {
      camera.getCameraControl().enableTorch(isChecked);
    });

  }

  private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
  private Camera camera;


  // Camera stuff:
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

  int n = 0;
  public void scanBarcode(View view) {
    Main.barcodes.add("" + n++);
//    MainActivity.updateTable();

    // update MainActivity
    setResult(RESULT_OK, null);
  }

}