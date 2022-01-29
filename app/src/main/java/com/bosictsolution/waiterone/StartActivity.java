package com.bosictsolution.waiterone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adapter.ModuleListAdapter;
import adapter.StWaiterListAdapter;
import common.DBHelper;
import common.FeatureList;
import common.PaperWidth;
import common.PrinterInterface;
import common.PrinterModel;
import common.SystemSetting;
import data.FeatureData;
import data.ModuleData;
import data.ReportData;
import data.WaiterData;
import listener.ModuleCheckedListener;
import listener.SetupEditDeleteButtonClickListener;

public class StartActivity extends AppCompatActivity implements SetupEditDeleteButtonClickListener, ModuleCheckedListener {

    TextView tvTitleRegister;
    EditText etMacAddress,etRegisterKey,etShopName;
    Button btnRegister,btnOK,btnCreateUser,btnContinue;
    LinearLayout layoutRegister,layoutShopName,layoutUserSetup;
    ListView lvUser;
    CheckBox chkUseMultiPrinter;

    List<ModuleData> lstModuleData;
    ModuleListAdapter moduleListAdapter;
    ArrayList<Integer> lstCheckedModuleID=new ArrayList<>();
    List<WaiterData> lstUserData;
    StWaiterListAdapter userSetupListAdapter;

    private static DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    final Context context = this;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES="MyPrefs";
    public static final String finished_start="finished_start";
    String generateKey,macAddress,registerKey;
    String[] splitarr;
    String firstkey,secondkey,thirdkey,fourthkey,fifthkey,sixkey,formula="blueocean016",splitMac="",finalkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);

        db=new DBHelper(this);

        setLayoutResource();
        generateRegisterKey();
        showUserList();

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserDialog(0,"","",false);
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=sharedpreferences.edit();
                editor.putString(finished_start, "finished_start");
                editor.commit();
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                register();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setShopName();
                if(chkUseMultiPrinter.isChecked()){
                    db.updateAllowFeature(db.getUseMultiPrinterFeatureID(),1);
                }else{
                    db.updateAllowFeature(db.getUseMultiPrinterFeatureID(),0);
                }
                saveTestPrintImageToStorage();
            }
        });
    }

    @Override
    public void onResume(){
        sharedpreferences=getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        if(sharedpreferences.contains(finished_start)){
            Intent i=new Intent(this,LoginActivity.class);
            startActivity(i);
        }
        super.onResume();
    }

    @Override
    public void onEditButtonClickListener(int position){
        int userid=lstUserData.get(position).getWaiterid();
        showUserDialog(userid,lstUserData.get(position).getWaiterName(),lstUserData.get(position).getPassword(),true);
    }

    @Override
    public void onDeleteButtonClickListener(int position){
        showConfirmDialog(position);
    }

    @Override
    public void onModuleCheckedListener(int position){
        int checkedModuleID = lstModuleData.get(position).getModuleID();
        if(!lstCheckedModuleID.contains(checkedModuleID)) {
            lstCheckedModuleID.add(lstModuleData.get(position).getModuleID());
        }
    }

    @Override
    public void onModuleUnCheckedListener(int position){
        int removeIndex= lstCheckedModuleID.indexOf(lstModuleData.get(position).getModuleID());
        if(removeIndex!=-1) {
            lstCheckedModuleID.remove(removeIndex);
        }
    }

    private void saveTestPrintImageToStorage() {
        BufferedInputStream bis58 = null;
        BufferedInputStream bis80 = null;
        try {
            bis58 = new BufferedInputStream(getAssets().open("print58.png"));
            bis80 = new BufferedInputStream(getAssets().open("print80.png"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Bitmap bitmap58 = BitmapFactory.decodeStream(bis58);
        Bitmap bitmap80 = BitmapFactory.decodeStream(bis80);
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logoPath58 = new File(directory, "print58.png");
        File logoPath80 = new File(directory, "print80.png");

        FileOutputStream fos58 = null;
        FileOutputStream fos80 = null;
        try {
            fos58 = new FileOutputStream(logoPath58);
            fos80 = new FileOutputStream(logoPath80);
            bitmap58.compress(Bitmap.CompressFormat.PNG, 100, fos58);
            bitmap80.compress(Bitmap.CompressFormat.PNG, 100, fos80);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos58 != null)fos58.close();
                if(fos80 != null)fos80.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    private void generateRegisterKey(){
        String macAddress= getMacAddress();
        if(macAddress.length()==0)return;

        etMacAddress.setText(macAddress);

        splitarr=macAddress.split(":");
        for(int i=0;i<splitarr.length;i++){
            splitMac+=splitarr[i];
        }

        sixkey=Character.toString(splitMac.charAt(11))+Character.toString(splitMac.charAt(10))+Character.toString(splitMac.charAt(1))+Character.toString(splitMac.charAt(0));
        thirdkey=Character.toString(splitMac.charAt(9))+Character.toString(splitMac.charAt(8))+Character.toString(splitMac.charAt(7))+Character.toString(splitMac.charAt(6));
        fourthkey=Character.toString(splitMac.charAt(5))+Character.toString(splitMac.charAt(4))+Character.toString(splitMac.charAt(3))+Character.toString(splitMac.charAt(2));

        firstkey=Character.toString(formula.charAt(11))+Character.toString(formula.charAt(10))+Character.toString(formula.charAt(9))+Character.toString(formula.charAt(8));
        secondkey=Character.toString(sixkey.charAt(0))+Character.toString(formula.charAt(6))+Character.toString(formula.charAt(5))+Character.toString(sixkey.charAt(3));
        fifthkey=Character.toString(sixkey.charAt(1))+Character.toString(formula.charAt(2))+Character.toString(sixkey.charAt(2))+Character.toString(formula.charAt(0));

        finalkey=firstkey+"-"+secondkey+"-"+thirdkey+"-"+fourthkey+"-"+fifthkey+"-"+sixkey+"-";
        generateKey=finalkey.substring(0, finalkey.length()-1);
    }

    private void register(){
        macAddress=etMacAddress.getText().toString();
        registerKey=etRegisterKey.getText().toString();

        if(macAddress.length()==0){
            systemSetting.showMessage(SystemSetting.ERROR,"Not Found MAC Address!",context,getLayoutInflater());
            return;
        }
        else if(registerKey.length()==0){
            systemSetting.showMessage(SystemSetting.INFO,"Enter Register Key",context,getLayoutInflater());
            return;
        }
        else if(!generateKey.equals(registerKey)){
            systemSetting.showMessage(SystemSetting.ERROR,"Invalid Register Key!",context,getLayoutInflater());
            return;
        }
        else{
            db.deleteRegister();
            if (db.insertRegister(macAddress, registerKey)) {
                insertFeature();
                insertReport();
                insertModule();
                insertPrinterModel();
                insertPrinterInterface();
                insertPaperWidth();
                db.deleteSlipID();
                db.insertSlipID();
                db.deleteTranID();
                db.setTranID();
                db.deleteVouFormat();
                db.setVouFormat();
                layoutRegister.setVisibility(View.GONE);
                layoutShopName.setVisibility(View.VISIBLE);
                systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
            }
       }
    }

    private void setShopName(){
        if(etShopName.getText().toString().length()==0){
            systemSetting.showMessage(SystemSetting.WARNING,"Enter Shop Name!",context,getLayoutInflater());
            return;
        }
        //if(etShopName.getText().toString().length()==1){
            //systemSetting.showMessage(SystemSetting.WARNING,"At least, enter about 2 characters!",context,getLayoutInflater());
            //return;
        //}
        db.deleteSystemSetting();
        if(db.insertSystemSetting(etShopName.getText().toString(),0,0,0)){
            systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
            layoutShopName.setVisibility(View.GONE);
            layoutUserSetup.setVisibility(View.VISIBLE);
        }
    }

    private void insertModule(){
        db.deleteModule();
        db.insertModule("SALE");
        db.insertModule("REPORT");
        db.insertModule("SYSTEM");
    }

    private void insertReport(){
        db.deleteReport();
        List<ReportData> lstReportData=new ArrayList<>();

        ReportData data=new ReportData();
        data.setReportName("Day End Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Sale Amount Only Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Sale Invoice Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Detailed Sale Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Summary Item Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Top Sale Item Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Bottom Sale Item Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Top Sale Menu Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Bottom Sale Menu Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Sale By Menu Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Sale By Hourly Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Sale By Waiter Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Sale By Table Report");
        lstReportData.add(data);

        data=new ReportData();
        data.setReportName("Data Analysis Report");
        lstReportData.add(data);

        for(int i=0;i<lstReportData.size();i++){
            db.insertReport(lstReportData.get(i).getReportName());
        }
    }

    private void insertFeature(){
        db.deleteFeature();
        List<FeatureData> lstFeatureData=new ArrayList<>();
        FeatureData data=new FeatureData();
        data.setFeatureName(FeatureList.fOrderTime);
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName(FeatureList.fAutoTaste);
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName(FeatureList.fCustomerInfo);
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName(FeatureList.fBookingTable);
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName(FeatureList.fTaxIncCharg);
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName(FeatureList.fPrint2);
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName(FeatureList.fAutoPrint);
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName(FeatureList.fPaperConstrict);
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName((FeatureList.fUseMultiPrinter));
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName((FeatureList.fPrintOrder));
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName((FeatureList.fPrintButtonInSale));
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName((FeatureList.fMultiTableBill));
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName((FeatureList.fStartEndTime));
        lstFeatureData.add(data);
        data=new FeatureData();
        data.setFeatureName((FeatureList.fUseItemImage));
        lstFeatureData.add(data);
        for(int i=0;i<lstFeatureData.size();i++){
            db.insertFeature(lstFeatureData.get(i).getFeatureName(),0);
        }
    }

    private void insertPrinterInterface(){
        db.deletePrinterInterface();
        db.insertPrinterInterface(PrinterInterface.iEthernet);
        db.insertPrinterInterface(PrinterInterface.iBluetooth);
        //db.insertPrinterInterface(PrinterInterface.iUSB);
    }

    private void insertPaperWidth(){
        db.deletePaperWidth();
        db.insertPaperWidth(PaperWidth.w58);
        db.insertPaperWidth(PaperWidth.w80);
    }

    private void insertPrinterModel(){
        db.deletePrinterModel();
        db.insertPrinterModel(PrinterModel.pEmpty,0,0);
        //db.insertPrinterModel(PrinterModel.pXPrinter,1,2);
        //db.insertPrinterModel(PrinterModel.pZy306,1,2);
        db.insertPrinterModel(PrinterModel.pOtherModel,0,0);
    }

    private void setLayoutResource(){
        tvTitleRegister=(TextView)findViewById(R.id.tvTitleRegister);
        etMacAddress=(EditText)findViewById(R.id.etMacAddress);
        etRegisterKey=(EditText)findViewById(R.id.etRegisterKey);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        layoutRegister=(LinearLayout)findViewById(R.id.layoutRegister);
        layoutShopName=(LinearLayout)findViewById(R.id.layoutShopName);
        layoutUserSetup=(LinearLayout)findViewById(R.id.layoutUserSetup);
        etShopName=(EditText)findViewById(R.id.etShopName);
        btnOK=(Button)findViewById(R.id.btnOK);
        lvUser=(ListView)findViewById(R.id.lvUser);
        btnCreateUser=(Button)findViewById(R.id.btnCreateUser);
        btnContinue=(Button)findViewById(R.id.btnContinue);
        chkUseMultiPrinter=(CheckBox)findViewById(R.id.chkUseMultiPrinter);
        chkUseMultiPrinter.setText(FeatureList.fUseMultiPrinter+" (Multi Bluetooth and One Network Printers)");
    }

    private void showUserDialog(final int editid,String name,String password,final boolean isEdit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_user, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final TextView tvLabelAllowRight=(TextView)view.findViewById(R.id.tvLabelAllowRight);
        final EditText etUserName=(EditText)view.findViewById(R.id.etUserName);
        final EditText etPassword=(EditText)view.findViewById(R.id.etPassword);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);
        final ListView lvModule=(ListView) view.findViewById(R.id.lvModule);

        getAllModule(lvModule);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        if(isEdit){
            lstCheckedModuleID=new ArrayList<>();
            etUserName.setText(name);
            etPassword.setText(password);
            Cursor cur=db.getModuleByUserID(editid);
            while(cur.moveToNext()){
                lstCheckedModuleID.add(cur.getInt(0));
            }

            for(int i=0;i<lstModuleData.size();i++){
                if(lstCheckedModuleID.contains(lstModuleData.get(i).getModuleID())){
                    lstModuleData.get(i).setSelected(true);
                }
            }
            moduleListAdapter=new ModuleListAdapter(this,lstModuleData);
            lvModule.setAdapter(moduleListAdapter);
            moduleListAdapter.setModuleCheckedListener(this);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showUserList();
                lstCheckedModuleID=new ArrayList<>();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etUserName.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Name!",context,getLayoutInflater());
                    etUserName.requestFocus();
                    return;
                }
                if(etPassword.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Password!",context,getLayoutInflater());
                    etPassword.requestFocus();
                    return;
                }
                if (lstCheckedModuleID.size() == 0) {
                    systemSetting.showMessage(SystemSetting.WARNING,"Choose Module!",context,getLayoutInflater());
                    return;
                }

                if(!isEdit) {
                    if (db.insertWaiter(etUserName.getText().toString(), etPassword.getText().toString())) {
                        int waiterid=0;
                        Cursor cur=db.getMaxWaiterID();
                        if(cur.moveToFirst())waiterid=cur.getInt(0);
                        for(int i=0;i<lstCheckedModuleID.size();i++){
                            db.insertUserRight(waiterid,lstCheckedModuleID.get(i));
                        }
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etUserName.setText("");
                        etPassword.setText("");
                        lstCheckedModuleID=new ArrayList<>();
                        getAllModule(lvModule);
                    }
                }else{
                    if (db.updateWaiter(editid,etUserName.getText().toString(), etPassword.getText().toString())) {
                        db.deleteUserRight(editid);
                        for(int i=0;i<lstCheckedModuleID.size();i++){
                            db.insertUserRight(editid,lstCheckedModuleID.get(i));
                        }
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                        etUserName.setText("");
                        etPassword.setText("");
                        showUserList();
                        lstCheckedModuleID=new ArrayList<>();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }

    private void getAllModule(ListView lvModule){
        lstModuleData=new ArrayList<>();
        Cursor cur=db.getAllModule();
        while(cur.moveToNext()){
            ModuleData data=new ModuleData();
            data.setModuleID(cur.getInt(0));
            data.setModuleName(cur.getString(1));
            lstModuleData.add(data);
        }
        moduleListAdapter=new ModuleListAdapter(this,lstModuleData);
        lvModule.setAdapter(moduleListAdapter);
        moduleListAdapter.setModuleCheckedListener(this);
    }

    private void showUserList(){
        lstUserData=new ArrayList<>();
        Cursor cur=db.getWaiter();
        while(cur.moveToNext()){
            WaiterData data=new WaiterData();
            data.setWaiterid(cur.getInt(0));
            data.setWaiterName(cur.getString(1));
            data.setPassword(cur.getString(2));
            lstUserData.add(data);
        }
        userSetupListAdapter=new StWaiterListAdapter(this,lstUserData);
        lvUser.setAdapter(userSetupListAdapter);
        userSetupListAdapter.setOnSetupEditDeleteButtonClickListener(this);

        if(lstUserData.size()==0){
            btnCreateUser.setVisibility(View.VISIBLE);
            btnContinue.setVisibility(View.GONE);
            lvUser.setVisibility(View.GONE);
        }else{
            btnContinue.setVisibility(View.VISIBLE);
            lvUser.setVisibility(View.VISIBLE);
        }
    }

    private void showConfirmDialog(final int position){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_confirm, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        TextView tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        tvConfirmMessage.setText("Are you sure you want to delete user "+lstUserData.get(position).getWaiterName()+"?");

        passwordDialog.setCancelable(true);
        final android.app.AlertDialog passwordRequireDialog=passwordDialog.create();
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
                int deleteid=lstUserData.get(position).getWaiterid();
                if(db.deleteWaiter(deleteid)){
                    systemSetting.showMessage(SystemSetting.SUCCESS,"Delete Successful!",context,getLayoutInflater());
                    showUserList();
                }
                passwordRequireDialog.dismiss();
            }
        });
    }
}
