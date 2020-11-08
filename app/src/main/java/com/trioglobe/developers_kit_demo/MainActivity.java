package com.trioglobe.developers_kit_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scannView;
    LinearLayout info;
    private ImageView imageView;
    private TextView output;
    String firstFourChars = "";
    Button rescan;
    private static final String TAG = "MainActivity";

    ToneGenerator t ;
    Button share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        rescan = findViewById(R.id.rescan);
        info = findViewById(R.id.info);
        scannView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this, scannView);
        output = findViewById(R.id.result);

        share = findViewById(R.id.share);
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        t = new ToneGenerator(AudioManager.STREAM_MUSIC,     100);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String input = result.getText();
                        firstFourChars = input.substring(0, 3);
                        int startin = 890;
                        int start = 689;
                        int end = 700;
                        try {
                            int num = Integer.parseInt(firstFourChars);
                            scannView.setVisibility(View.GONE);
                            info.setVisibility(View.VISIBLE);
                            codeScanner.stopPreview();
                            if (num > start && num < end) {
                                imageView.setImageResource(R.drawable.china);
                                output.setText("CHINA");
                                v.vibrate(400);
                                t.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            } else if (num == startin) {
                                imageView.setImageResource(R.drawable.india);
                                output.setText("INDIA");
                                Toast.makeText(MainActivity.this, "A Step Towards Atmanirbhar Bharat", Toast.LENGTH_SHORT).show();
                                v.vibrate(400);
                                t.startTone(400);
                                t.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            } else {
                                imageView.setImageResource(R.drawable.world);
                                output.setText("UNKNOWN ORIGIN");
                                v.vibrate(400);
                                t.startTone(400);
                                t.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            }


                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Please scan a valid QR code/Barcode", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        rescan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        codeScanner.stopPreview();
                        scannView.setVisibility(View.VISIBLE);
                        info.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Tap to Scan", Toast.LENGTH_SHORT).show();



                    }
                }
        );
        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                Uri shareBody = Uri.parse("https://play.google.com/store/apps/details?id=com.devdevelopers.myapplication");
                String text = "Hey Checkout this app, find out your home products Chinese or Indian. Download here: https://play.google.com/store/apps/details?id=com.devdevelopers.myapplication";
                String subject = "Download Barcode Scanner app";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                sharingIntent.putExtra(Intent.ACTION_VIEW, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(MainActivity.this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }



    }



