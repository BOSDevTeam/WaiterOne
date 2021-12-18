package com.bosictsolution.waiterone;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import adapter.SpWaiterAdapter;
import common.DBHelper;
import common.FeatureList;
import common.SystemSetting;
import data.WaiterData;

public class LoginActivity extends AppCompatActivity {

    Spinner spWaiterName;
    EditText etPassword;
    Button btnLogin,btnExit;
    TextView tvTitleLogin;

    private DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    SpWaiterAdapter spWaiterAdapter;

    final Context context = this;
    List<WaiterData> lstWaiterData;
    public int login_waiter_id;
    int selectedUserPosition;
    //boolean isAdminForgetPassword;
    String adminPassword;
    boolean isAdminFirstTime;
    public static BluetoothAdapter BA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setLayoutResource();

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);

        db = new DBHelper(this);
        BA = BluetoothAdapter.getDefaultAdapter();

        if (db.getFeatureResult(FeatureList.fUseMultiPrinter) == 1) {
            checkBluetoothOn();
        }

        etPassword.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent arg1) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                login();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                moveTaskToBack(true);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        getWaiter();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if (itemId == R.id.menuAdmin) {
            showAdminPasswordDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    private boolean checkBluetoothOn(){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            return false;
        }else {
            return true;
        }
    }

    /**
     * Methods
     */
    private void getWaiter(){
        Cursor cur= db.getWaiter();
        lstWaiterData=new ArrayList<>();
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                WaiterData data=new WaiterData();
                data.setWaiterid(cur.getInt(0));
                data.setWaiterName(cur.getString(1));
                data.setPassword(cur.getString(2));
                lstWaiterData.add(data);
            }
        }
        if(lstWaiterData.size()==0){
            WaiterData data=new WaiterData();
            data.setWaiterName("No Waiter");
            lstWaiterData.add(data);
        }
        SystemSetting.isSpTxtWhite=true;
        spWaiterAdapter =new SpWaiterAdapter(this,lstWaiterData);
        spWaiterName.setAdapter(spWaiterAdapter);
    }

    private void login(){
        selectedUserPosition = spWaiterName.getSelectedItemPosition();
        String password=etPassword.getText().toString();
        if(password.length()==0){
            systemSetting.showMessage(SystemSetting.INFO,"Enter Password!",context,getLayoutInflater());
            etPassword.requestFocus();
            return;
        }
        String passwordByPosition=lstWaiterData.get(selectedUserPosition).getPassword();
        if(!password.equals(passwordByPosition)){
            systemSetting.showMessage(SystemSetting.ERROR,"Wrong Password!",context,getLayoutInflater());
            etPassword.requestFocus();
            return;
        }
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        login_waiter_id= lstWaiterData.get(selectedUserPosition).getWaiterid();
        String waiter_name=lstWaiterData.get(selectedUserPosition).getWaiterName();
        intent.putExtra("waiterid", login_waiter_id);
        intent.putExtra("waitername", waiter_name);
        startActivity(intent);
    }

    private void showAdminPasswordDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_password, null);
        AlertDialog.Builder passwordDialog=new AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final EditText etAdminPassword=(EditText)passwordView.findViewById(R.id.etPassword);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        Cursor cur=db.getAdminPassword();
        if(!cur.moveToFirst()) isAdminFirstTime=true;
        else adminPassword=cur.getString(0);

        /**Cursor cur=db.getAdminPassword();
        if(!cur.moveToFirst()) {
            isAdminForgetPassword=true;
            btnForgetPassword.setVisibility(View.GONE);
        }else{
            adminPassword=cur.getString(0);
        }**/

        passwordDialog.setCancelable(true);
        final AlertDialog passwordRequireDialog=passwordDialog.create();
        passwordRequireDialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                passwordRequireDialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String inputAdminPassword=etAdminPassword.getText().toString();
                if(inputAdminPassword.length()==0){
                    systemSetting.showMessage(SystemSetting.INFO,"Enter Password!",context,getLayoutInflater());
                    return;
                }
                if(isAdminFirstTime) {
                    if (inputAdminPassword.equals("bos")) {
                        isAdminFirstTime=false;
                        Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity(i);
                        passwordRequireDialog.dismiss();
                    } else {
                        systemSetting.showMessage(SystemSetting.ERROR,"Invalid Password!",context,getLayoutInflater());
                    }
                }else {
                    if (inputAdminPassword.equals(adminPassword)) {
                        Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity(i);
                        passwordRequireDialog.dismiss();
                    } else {
                        systemSetting.showMessage(SystemSetting.ERROR,"Invalid Password!",context,getLayoutInflater());
                    }
                }
            }
        });

        /**btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAdminForgetPassword=true;
                etAdminPassword.setHint("Enter Original Password");
            }
        });**/
    }

    private void setLayoutResource(){
        btnLogin=(Button)findViewById(R.id.btnLogin);
        btnExit=(Button)findViewById(R.id.btnExit);
        spWaiterName =(Spinner)findViewById(R.id.spUserName);
        etPassword=(EditText) findViewById(R.id.txtPassword);
        tvTitleLogin=(TextView) findViewById(R.id.tvTitleLogin);
    }
}

