package com.example.reduce;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import com.example.reduce.database.Product;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScannerActivity extends AppCompatActivity {

	private ImageAnalysis analysis =
			new ImageAnalysis.Builder()
					.setTargetRotation(Surface.ROTATION_0)
					.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
					.build();

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

	    Bundle bundle = getIntent().getExtras();

        while (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 100);
        }

        startCamera();

        View toggle = findViewById(R.id.flashToggle);
        toggleFlash(toggle);

        View toggleAnalysisButton = findViewById(R.id.ScanToggle);
        toggleAnalysis(toggleAnalysisButton);
    }



    private void updateBarcodes(List<Barcode> barcodes) {

	    CharSequence text = "Barcodes added: ";
        boolean updated = false;

		for (Barcode barcode : barcodes) {
            if (Main.dataBase
                .where(Product.class)
                .beginsWith("barcodeId", barcode.getDisplayValue())
                .findFirst() == null) {
                    setResult(RESULT_OK, null);
                    updated = true;
                    text += barcode.getDisplayValue() + " ";

                    UpdateDialog dialog = new UpdateDialog(barcode.getDisplayValue());
				    dialog.show(getSupportFragmentManager(), "dialog box");
            }
		}

        if (updated) {
          Context context = getApplicationContext();
          int duration = Toast.LENGTH_SHORT;

          Toast toast = Toast.makeText(context, text, duration);
          toast.show();
        }

    }

    private void toggleAnalysis(View toggle) {
        assert toggle instanceof ToggleButton;

        ((ToggleButton) toggle)
            .setOnCheckedChangeListener(
                    (buttonView, isChecked) -> {
              View button = findViewById(R.id.captureButton);
              assert button instanceof Button;
              ((Button) button).setEnabled(!isChecked);

              if (isChecked) {
                analysisOn();
                System.out.println("analysis on");
              } else {
                analysisOff();
              }
            });
  }

  private void analysisOff() {
    assert cameraProvider != null;
    analysis.clearAnalyzer();
  }

  private void analysisOn() {
    assert cameraProvider != null;

    analysis.setAnalyzer(
        ContextCompat.getMainExecutor(this),
        new ImageAnalysis.Analyzer() {
          @Override
          public void analyze(@NonNull ImageProxy image) {

            InputImage inputImage =
                InputImage.fromMediaImage(
                    Objects.requireNonNull(image.getImage()),
                    image.getImageInfo().getRotationDegrees());

            BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build();
            // [END set_detector_options]

            // [START get_detector]
            BarcodeScanner scanner = BarcodeScanning.getClient(options);
            scanner
                .process(inputImage)
                .addOnSuccessListener(
                    barcodes -> {
                      if (!barcodes.isEmpty()) {
	                      analysis.clearAnalyzer();
	                      ((ToggleButton) findViewById(R.id.ScanToggle)).setChecked(false);
                        updateBarcodes(barcodes);

                      }
                    })
                .addOnFailureListener(e -> System.out.println("failure"))
                .addOnCompleteListener(
                    e -> {
                      image.close();
                    });
          }
        });
//    cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, imageAnalysis);
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
  private Preview preview;
  private ProcessCameraProvider cameraProvider;


  // Camera stuff:
  private void startCamera() {

    cameraProviderFuture = ProcessCameraProvider.getInstance(this);

    cameraProviderFuture.addListener(
        () -> {
          try {
            cameraProvider = cameraProviderFuture.get();
            bindPreview();
          } catch (ExecutionException | InterruptedException e) {
            // No errors need to be handled for this Future.
            // This should never be reached.
          }
        },
        ContextCompat.getMainExecutor(this));
  }

  private void bindPreview() {
    preview = new Preview.Builder().build();

    CameraSelector cameraSelector =
        new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

    PreviewView cameraView = findViewById(R.id.cameraView);
    preview.setSurfaceProvider(cameraView.getSurfaceProvider());

    camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);

    cameraProvider.bindToLifecycle(this,
		    CameraSelector.DEFAULT_BACK_CAMERA,
		    analysis);

    //    camera.getCameraInfo().hasFlashUnit();

  }

  public void scanOnce(View view) {
    assert cameraProvider != null;
    analysis.setAnalyzer(
        ContextCompat.getMainExecutor(this),
        new ImageAnalysis.Analyzer() {
          @Override
          public void analyze(@NonNull ImageProxy image) {
            InputImage inputImage =
                InputImage.fromMediaImage(
                    Objects.requireNonNull(image.getImage()),
                    image.getImageInfo().getRotationDegrees());

            System.out.println("scan");
            BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build();
            // [END set_detector_options]

            // [START get_detector]
            BarcodeScanner scanner = BarcodeScanning.getClient(options);
            scanner
                .process(inputImage)
                .addOnSuccessListener(
                    barcodes -> {
                      if (!barcodes.isEmpty()) {
                        analysis.clearAnalyzer();
                        updateBarcodes(barcodes);
                      }
                    })
                .addOnFailureListener(e -> System.out.println("failure"))
                .addOnCompleteListener(
                    e -> {
                      image.close();
                      analysis.clearAnalyzer();
                    });
          }
        });
  }
}

/*
private void scanBarcodes(InputImage image) {
  // [START set_detector_options]
  System.out.println("scan");
  BarcodeScannerOptions options =
      new BarcodeScannerOptions.Builder()
          .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
          .build();
  // [END set_detector_options]

//    Barcode.FORMAT_QR_CODE,
//        Barcode.FORMAT_CODE_128
  System.out.println("scan1");

  // [START get_detector]
  BarcodeScanner scanner = BarcodeScanning.getClient(options);
  // [END get_detector]

  // [START run_detector]
//    Task<List<Barcode>> result =
  scanner.process(image)
      .addOnSuccessListener(barcodes -> {
        System.out.println("barcode");
        // Task completed successfully
        // [START_EXCLUDE]
        // [START get_barcodes]
        for (Barcode barcode: barcodes) {
*/

/*
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();
*/
