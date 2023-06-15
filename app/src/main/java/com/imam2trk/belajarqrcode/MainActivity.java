package com.imam2trk.belajarqrcode;

import static android.Manifest.permission_group.CAMERA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if (checkPermission()){
            Toast.makeText(this, "Hak akses diberikan!", Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQUEST_CAMERA);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission()) {
            if (scannerView == null){
                scannerView = new ZXingScannerView(this);
                setContentView(scannerView);
            }
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        } else {
            requestPermission();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA){
            if (grantResults.length>0){
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    Toast.makeText(getApplicationContext(), "Anda diizinkan untuk menggunakan " +
                            "kamera", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Akses ditolak. Anda dilarang " +
                            "menggunakan kamera", Toast.LENGTH_LONG).show();
                    if (shouldShowRequestPermissionRationale(CAMERA)){

                    }
                }
            }
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener oklistener){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Anda harus memberi izin akses untuk aplikasi")
                .setPositiveButton("OK", oklistener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(MainActivity.this);
            }
        });
        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myResult));
                startActivity(browserIntent);
            }
        });
    }
}























