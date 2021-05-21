package com.example.reduce;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.view.Surface;
import android.view.View;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.internal.zzj;
import com.google.mlkit.vision.common.InputImage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ScannerActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scanner);

    Bundle bundle = getIntent().getExtras();
    //    barcodeSet = (Set) bundle.get("BarcodeSet");

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
    toggleListener.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          camera.getCameraControl().enableTorch(isChecked);
        });
  }

  private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
  private Camera camera;

  // Camera stuff:
  private void startCamera() {

    cameraProviderFuture = ProcessCameraProvider.getInstance(this);

    cameraProviderFuture.addListener(
        () -> {
          try {
            ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
            bindPreview(cameraProvider);
          } catch (ExecutionException | InterruptedException e) {
            // No errors need to be handled for this Future.
            // This should never be reached.
          }
        },
        ContextCompat.getMainExecutor(this));

    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
  }

  private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
    Preview preview = new Preview.Builder().build();

    CameraSelector cameraSelector =
        new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

    PreviewView cameraView = findViewById(R.id.cameraView);
    preview.setSurfaceProvider(cameraView.getSurfaceProvider());

    camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);

    //    camera.getCameraInfo().hasFlashUnit();

  }

  int n = 0;

  private BarcodeScannerOptions options =
      new BarcodeScannerOptions.Builder()
          .setBarcodeFormats(Barcode.FORMAT_CODE_128, Barcode.FORMAT_QR_CODE)
          .build();

  ImageCapture imageCapture =
      new ImageCapture.Builder().setTargetRotation(Surface.ROTATION_0).build();

  private BarcodeScanner scanner = BarcodeScanning.getClient();

  public void scanBarcode(View view) {
    // update MainActivity


    Main.barcodes.add(null);
    imageCapture.takePicture(
        Executors.newSingleThreadExecutor(),
        new ImageCapture.OnImageCapturedCallback() {
          @RequiresApi(api = Build.VERSION_CODES.N)
          @Override
          public void onCaptureSuccess(@NonNull ImageProxy image) {
            //              Image img = image.getImage();

            InputImage inputImage =
                InputImage.fromMediaImage(
                    Objects.requireNonNull(image.getImage()),
                    image.getImageInfo().getRotationDegrees());

            Task<List<Barcode>> result =
                scanner
                    .process(inputImage)
                    .addOnSuccessListener(
                        new OnSuccessListener<List<Barcode>>() {
                          @Override
                          public void onSuccess(List<Barcode> barcodes) {
                            // Task completed successfully

                            if (!barcodes.isEmpty()) {
                              for (Barcode b : barcodes) {
                                Main.barcodes.add(b);
                              }
                              setResult(RESULT_OK, null);
                            }
                          }
                        });
            Main.barcodes.addAll(result.getResult());
            super.onCaptureSuccess(image);
          }

          @Override
          public void onError(@NonNull @NotNull ImageCaptureException exception) {
            super.onError(exception);
          }
        });

    //          new ImageCapture.OutputFileResults().build();

    //      ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY;

  }
}
