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
  private Preview preview;


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

  }

  private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
    preview = new Preview.Builder().build();

    CameraSelector cameraSelector =
        new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

    PreviewView cameraView = findViewById(R.id.cameraView);
    preview.setSurfaceProvider(cameraView.getSurfaceProvider());

    camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);

    //    camera.getCameraInfo().hasFlashUnit();

  }

  public void scanBarcode(View view) {
    // update MainActivity
//    ImageCapture imageCapture =
//        new ImageCapture.Builder().setTargetRotation(Surface.ROTATION_0).build();

    System.out.println("imageCapture");
    //    Main.barcodes.add(null);

//    Executor cameraExecutor = command -> command.run();



    ProcessCameraProvider cameraProvider = null;

    try {
      cameraProvider = cameraProviderFuture.get();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
        .setTargetRotation(Surface.ROTATION_0)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//        .setCameraSelector(cameraSelector)
        .setTargetResolution(new Size(1440, 1080))
        .build();
//    PreviewView cameraView = findViewById(R.id.cameraView);
//    preview.setSurfaceProvider(cameraView.getSurfaceProvider());

    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
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
        System.out.println("scan1");

        // [START get_detector]
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(inputImage).addOnSuccessListener(
            barcodes -> {
          System.out.println("barcode");

          System.out.println(barcodes);
          Main.barcodes.addAll(barcodes);

          for (Barcode barcode: barcodes) {

            int valueType = barcode.getValueType();
            // See API reference for complete list of supported types
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
            .addOnFailureListener(e ->
            System.out.println("failure"))
        .addOnCompleteListener(e ->
              image.close()
        );
        }
        });

    assert cameraProvider != null;
    camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis);
    Main.barcodes.add(new Barcode(new zzj() {
      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public Rect zza() {
        return null;
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public Point[] zzb() {
        return new Point[0];
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public String zzc() {
        return "hello";
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public byte[] zzd() {
        return new byte[0];
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public String zze() {
        return "hi";
      }

      @Override
      public int zzf() {
        return 0;
      }

      @Override
      public int zzg() {
        return 0;
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public Barcode.Email zzh() {
        return null;
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public Barcode.Phone zzi() {
        return null;
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public Barcode.Sms zzj() {
        return null;
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public Barcode.WiFi zzk() {
        return null;
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public Barcode.UrlBookmark zzl() {
        return null;
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public Barcode.GeoPoint zzm() {
        return null;
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public Barcode.CalendarEvent zzn() {
        return null;
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public Barcode.ContactInfo zzo() {
        return null;
      }

      @Nullable
      @org.jetbrains.annotations.Nullable
      @Override
      public Barcode.DriverLicense zzp() {
        return null;
      }
    }));

    setResult(RESULT_OK, null);

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
/*
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();
*/
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