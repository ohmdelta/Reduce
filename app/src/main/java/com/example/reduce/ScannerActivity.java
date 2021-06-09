package com.example.reduce;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Size;
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
import java.util.concurrent.Executor;
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

    imageAnalysis.setAnalyzer(
        ContextCompat.getMainExecutor(this),
        new ImageAnalysis.Analyzer() {
          @Override
          public void analyze(@NonNull ImageProxy image) {

            System.out.println("analyse");
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

            //      Barcode.FORMAT_QR_CODE,
            //      Barcode.FORMAT_CODE_128

            // [START get_detector]
            BarcodeScanner scanner = BarcodeScanning.getClient(options);
            scanner
                .process(inputImage)
                .addOnSuccessListener(
                    barcodes -> {
                      System.out.println("barcode");

                      System.out.println(barcodes);
                      Main.barcodes.addAll(barcodes);

                      for (Barcode barcode : barcodes) {
                        int valueType = barcode.getValueType();
                        switch (valueType) {
                          case Barcode.FORMAT_QR_CODE:
                            System.out.println(barcode.getDisplayValue());
                            System.out.println("barcode value:");
                            System.out.println(barcode.getRawValue());
                            Main.barcodes.add(barcode);
                            break;
                          case Barcode.FORMAT_CODE_128:
                            Main.barcodes.add(barcode);

                            System.out.println("code 128");
                            System.out.println(barcode.getRawValue());
                            break;
                          default:
                            //                Main.barcodes.add(barcode);
                            setResult(RESULT_OK, null);
                        }
                      }
                    })
                .addOnFailureListener(e -> System.out.println("failure"))
                .addOnCompleteListener(e -> image.close());
          }
        });

    imageAnalysisOnce.setAnalyzer(
        ContextCompat.getMainExecutor(this),
        new ImageAnalysis.Analyzer() {
          @Override
          public void analyze(@NonNull ImageProxy image) {
//            System.out.println("analyse");
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
                      System.out.println("barcode");

                      System.out.println(barcodes);
                      Main.barcodes.addAll(barcodes);

                      for (Barcode barcode : barcodes) {
                        int valueType = barcode.getValueType();
                        switch (valueType) {
                          case Barcode.FORMAT_QR_CODE:
                            System.out.println(barcode.getDisplayValue());
                            System.out.println("barcode value:");
                            System.out.println(barcode.getRawValue());
//                            Main.barcodes.add(barcode);
                            break;
                          case Barcode.FORMAT_CODE_128:
//                            Main.barcodes.add(barcode);
                            System.out.println("code 128");
                            System.out.println(barcode.getRawValue());
                            break;
                          default:
                            setResult(RESULT_OK, null);
                        }
                      }
                    })
                .addOnFailureListener(e -> System.out.println("failure"))
                .addOnCompleteListener(e -> {
                  image.close();
                  cameraProvider.unbind(imageAnalysisOnce);
                });
          }
        });

    View toggle = findViewById(R.id.flashToggle);
    toggleFlash(toggle);

    View toggleAnalysis = findViewById(R.id.ScanToggle);
    toggleAnalysis(toggleAnalysis);

  }

  private void toggleAnalysis(View toggle) {
    assert toggle instanceof ToggleButton;
    
    ((ToggleButton) toggle).setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          View button = findViewById(R.id.scan_button);
          assert button instanceof Button;
          ((Button) button).setEnabled(!isChecked);

          if (isChecked) {
            analysisOn();
          } else {
            analysisOff();
          }
        }
    );
  }

  private void analysisOff() {
    assert cameraProvider != null;
    cameraProvider.unbind(imageAnalysis);
  }

  private void analysisOn() {
    assert cameraProvider != null;
    cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, imageAnalysis);
    setResult(RESULT_OK, null);

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
  private ImageAnalysis imageAnalysis =
      new ImageAnalysis.Builder()
          .setTargetRotation(Surface.ROTATION_0)
          .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
          //        .setCameraSelector(cameraSelector)
          .setTargetResolution(new Size(1440, 1080))
          .build();

  private ImageAnalysis imageAnalysisOnce =
      new ImageAnalysis.Builder()
          .setTargetRotation(Surface.ROTATION_0)
          .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
          //        .setCameraSelector(cameraSelector)
          .setTargetResolution(new Size(1440, 1080))
          .build();

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

    //    camera.getCameraInfo().hasFlashUnit();

  }

    /*
        imageCapture.takePicture(cameraExecutor,
        new ImageCapture.OnImageCapturedCallback() {
          @RequiresApi(api = Build.VERSION_CODES.N)
          @Override
          public void onCaptureSuccess(@NonNull ImageProxy image) {
            //              Image img = image.getImage();

            InputImage inputImage =
                InputImage.fromMediaImage(
                    Objects.requireNonNull(image.getImage()),
                    image.getImageInfo().getRotationDegrees());

            System.out.println(inputImage.getHeight());
//            ImageView img = (ImageView) findViewById(R.id.imageView);
//            img.setImageBitmap(inputImage.getBitmapInternal());
            scanBarcodes(inputImage);
          }
        });*/

  public void scanOnce(View view) {
    /*    while (!cameraProvider.isBound(imageAnalysis)) {
      try {
        analysisOn();
      } catch (Exception e) {
      }
    }*/
    assert cameraProvider != null;
    cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, imageAnalysisOnce);
    setResult(RESULT_OK, null);
  }

}

/*
.addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {
    // Task failed with an exception
    // ...
    }
    })*/

//
//        Task<List<Barcode>> result =
//            scanner
//                .process(inputImage)
//                .addOnSuccessListener(
//                    new OnSuccessListener<List<Barcode>>() {
//                      @Override
//                      public void onSuccess(List<Barcode> barcodes) {
//                        // Task completed successfully
//
//                        if (!barcodes.isEmpty()) {
//                          for (Barcode b : barcodes) {
//                            Main.barcodes.add(b);
//                          }
//                          setResult(RESULT_OK, null);
//                        }
//                      }
//                    });
//    Main.barcodes.addAll(result.getResult());
//    super.onCaptureSuccess(image);
//  }
//
//  @Override
//  public void onError(@NonNull @NotNull ImageCaptureException exception) {
//    super.onError(exception);
//  }
//});
//
////          new ImageCapture.OutputFileResults().build();
//
////      ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY;

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
*//*

          int valueType = barcode.getValueType();
          // See API reference for complete list of supported types
          switch (valueType) {
            case Barcode.FORMAT_QR_CODE:
              System.out.println(barcode.getDisplayValue());
              break;
            case Barcode.FORMAT_CODE_128:
              barcodes.add(barcode);
              break;
          }
        }
        // [END get_barcodes]
        // [END_EXCLUDE]
      })
      .addOnFailureListener(e -> {
        // Task failed with an exception
        // ...
        System.out.println("failed");
      });
  // [END run_detector]

  System.out.println("finished");
}*/
