package com.bosictsolution.waiterone;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import common.DBHelper;
import common.SystemSetting;

public class FeatureActivity extends AppCompatActivity {

    EditText etFeatureName;
    Button btnOK;

    private static DBHelper db;
    final Context context = this;
    SystemSetting systemSetting=new SystemSetting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        setLayoutResource();

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                insertFeature();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.menu_feature, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuDeleteFeature:
                db.truncateFeature();
                return true;
            case R.id.menuDeleteMasterSale:
                db.truncateMasterSale();
                return true;
            case R.id.menuDeleteTranSale:
                db.truncateTranSale();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setLayoutResource(){
        etFeatureName=(EditText)findViewById(R.id.etFeatureName);
        btnOK=(Button)findViewById(R.id.btnOK);

        etFeatureName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ARIALN.TTF"));
        btnOK.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ARIALN.TTF"));
    }

    private void insertFeature(){
        String feature=etFeatureName.getText().toString();
        if(feature.length()==0){
            systemSetting.showMessage(SystemSetting.INFO,"Enter Feature",context,getLayoutInflater());
            return;
        }
        else{
            if(db.insertFeature(feature,0)) {
                etFeatureName.setText("");
                systemSetting.showMessage(SystemSetting.SUCCESS,"Success",context,getLayoutInflater());
            }
        }
    }
}
