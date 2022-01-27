package com.bosictsolution.waiterone;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import common.DBHelper;
import common.SystemSetting;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH=3000;
    TextView tvAppName,tvCompanyName;
    private static final int PERMISSION_REQUEST_CODE = 1;
    DBHelper db=new DBHelper(this);
    SystemSetting systemSetting=new SystemSetting();
    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvAppName=(TextView)findViewById(R.id.tvAppName);
        tvCompanyName=(TextView)findViewById(R.id.tvCompanyName);

        this.getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                wifiOn();
                permission();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void wifiOn(){
        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifi.isWifiEnabled()){
            systemSetting.showMessage(SystemSetting.INFO,"Please, Turn On Wifi!",getBaseContext(),getLayoutInflater());
            finish();
        }
    }

    private void permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission();
            } else {
                start();
            }
        } else {
           start();
        }
    }

    private boolean checkPermission() {
        if(SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }else{
            int write = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
       /* int result = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }*/
    }

    private void requestPermission() {
        if(SDK_INT >= Build.VERSION_CODES.R){
            try{
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",new Object[]{getApplicationContext().getPackageName()})));
                startActivityForResult(intent,2000);
            }catch (Exception e){
                Intent obj = new Intent();
                obj.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(obj,2000);
            }
        }else{
            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
        /*if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getApplicationContext(), "Require Storage Permission, Please allow this permission in App SystemSetting.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean storage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(storage && read){
                        start();
                    }else{
                        Toast.makeText(context,"Require Storage Permission, Please allow this permission in App SystemSetting.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                break;
        }
        /*switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(SplashActivity.this, StartActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    systemSetting.showMessage(SystemSetting.ERROR,"Require Storage Permission, Please allow this permission in App SystemSetting.",getApplicationContext(),getLayoutInflater());
                    finish();
                }
                break;
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2000){
            if(SDK_INT >= Build.VERSION_CODES.R){
                if(Environment.isExternalStorageManager()){
                    start();
                }
            }
        }
    }

    private void start(){
        SystemSetting backupData=new SystemSetting();
        backupData.dataBackup(db,true, SystemSetting.BackupType.SystemBackup);
        Intent i = new Intent(SplashActivity.this, StartActivity.class);
        startActivity(i);
        finish();
    }
}
