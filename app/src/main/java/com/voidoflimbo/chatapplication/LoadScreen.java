package com.voidoflimbo.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoadScreen extends AppCompatActivity {

    private static final int PERMISSION_CODE = 100;
    Button  TryApp;
    private String[] AppPermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screen);

        TryApp = findViewById(R.id.button_try_it);
        AppPermissions = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };


    }

    private boolean checkPermission(Context context, String... appPermissions){
        for(String singlePermission:appPermissions){
            if(ActivityCompat.checkSelfPermission(context,singlePermission) != PackageManager.PERMISSION_GRANTED){
                System.out.println("permission not granted");
                return false;
            }
        }
        System.out.println(" all permissions granted");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int index = 0;
        if(requestCode == PERMISSION_CODE){
            for(int grantResult : grantResults){
                String permissionName = permissions[index];
                permissionName = permissionName.substring(permissionName.lastIndexOf(".")+1);
                if(grantResult == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, permissionName + " permission has been Granted" , Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(this, permissionName + " permission has been Denied" , Toast.LENGTH_SHORT).show();
                }
                index++;
            }
        }
    }

    public void openApp(View view) {
        if(!checkPermission(this, AppPermissions)){
            ActivityCompat.requestPermissions(this,AppPermissions,PERMISSION_CODE);
        } else {
            Intent recordScreenActivity = new Intent(this, RecordScreen.class);
            startActivity(recordScreenActivity);
        }
    }
}