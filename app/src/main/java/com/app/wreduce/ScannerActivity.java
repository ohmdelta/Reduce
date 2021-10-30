package com.app.wreduce;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.app.wreduce.database.Product;
import com.example.reduce.BuildConfig;
import com.example.reduce.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ScannerActivity extends AppCompatActivity {

    private final ImageAnalysis analysis =
            new ImageAnalysis.Builder()
                    .setTargetRotation(Surface.ROTATION_0)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(com.example.reduce.R.layout.activity_scanner);

        while (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 100);
        }

        startCamera();

        View toggle = findViewById(com.example.reduce.R.id.flashToggle);
        toggleFlash(toggle);

        View toggleAnalysisButton = findViewById(com.example.reduce.R.id.ScanToggle);
        toggleAnalysis(toggleAnalysisButton);
    }

    private void updateBarcodes(List<Barcode> barcodes) {

        CharSequence text = "Barcodes added: ";
        boolean updated = false;

        for (Barcode barcode : barcodes) {
            if (MainDatabase.dataBase
                    .where(Product.class)
                    .beginsWith("barcodeId", Objects.requireNonNull(barcode.getDisplayValue()))
                    .findFirst() == null) {
                setResult(RESULT_OK, null);
                updated = true;
                text += barcode.getDisplayValue() + " ";

                ProductUpdateDialog dialog = new ProductUpdateDialog(barcode.getDisplayValue());
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
        if (BuildConfig.DEBUG && !(toggle instanceof ToggleButton)) {
            throw new AssertionError("Assertion failed");
        }

        ((ToggleButton) toggle)
                .setOnCheckedChangeListener(
                        (buttonView, isChecked) -> {
                            View button = findViewById(R.id.captureButton);
                            if (BuildConfig.DEBUG && !(button instanceof Button)) {
                                throw new AssertionError("Assertion failed");
                            }
                            button.setEnabled(!isChecked);

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

    // Camera Functions:
    // Turn on/off Flash
    private void toggleFlash(View v) {
        if (BuildConfig.DEBUG && !(v instanceof ToggleButton)) {
            throw new AssertionError("Assertion failed");
        }

        ToggleButton toggleListener = (ToggleButton) v;
        toggleListener.setOnCheckedChangeListener(
                (buttonView, isChecked) -> camera.getCameraControl().enableTorch(isChecked));
    }

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Camera camera;
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
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector =
                new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        PreviewView cameraView = findViewById(R.id.cameraView);
        preview.setSurfaceProvider(cameraView.getSurfaceProvider());

        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);

        cameraProvider.bindToLifecycle(this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                analysis);

        //    camera.getCameraInfo().hasFlashUnit();

    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    public void scanOnce(View view) {
        assert cameraProvider != null;
        detectOnce();
    }

    private void detectOnce() {
        analysis.setAnalyzer(
                ContextCompat.getMainExecutor(this),
                image -> {
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
                                            updateBarcodes(barcodes);
                                        }
                                    })
                            .addOnFailureListener(e -> System.out.println("failure"))
                            .addOnCompleteListener(
                                    e -> {
                                        image.close();
                                        analysis.clearAnalyzer();
                                    });
                });
    }

    private void analysisOn() {
        assert cameraProvider != null;
        analysis.setAnalyzer(
                ContextCompat.getMainExecutor(this),
                image -> {
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
                                    e -> image.close());
                });
//    cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, imageAnalysis);
    }
}
