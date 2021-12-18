package com.bosictsolution.waiterone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapter.DgManageMenuListAdapter;
import adapter.FeatureListAdapter;
import adapter.SpPrinterNameAdapter;
import adapter.STypePrinterListAdapter;
import adapter.SpSTypeAdapter;
import common.DBHelper;
import common.FeatureList;
import common.SystemSetting;
import data.FeatureData;
import data.STypeData;
import data.STypePrinterData;
import data.MainMenuData;
import data.PrinterData;
import listener.DialogMenuCheckListener;
import listener.FeatureCheckListener;
import listener.SetupEditDeleteButtonClickListener;

public class AdminActivity extends AppCompatActivity implements FeatureCheckListener, DialogMenuCheckListener, SetupEditDeleteButtonClickListener {

    Button btnManageMenu,btnDeveloperMode,btnPrinterSetting,btnChangePassword,
            btnItemSTypePrinterSetting,btnMultiPrinterSetting,btnRestoreData;
    ListView lvFeature,lvMainMenu;
    TextView tvCurrentPassword,tvAllowFeature;
    Spinner spItemSType,spPrinter;
    //EditText etPrinterIP;
    Button btnCancel,btnSave;
    ListView lvSTypePrinterList;
    RadioButton rdoUseTakeAway,rdoUseBoth;
    RadioGroup rdoGroup;
    ImageButton btnRefreshTakeAway;

    private DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    FeatureListAdapter featureListAdapter;
    DgManageMenuListAdapter dgManageMenuListAdapter;
    SharedPreferences sharedpreferences;

    final Context context = this;
    List<FeatureData> lstFeatureSettingData=new ArrayList<>();
    private static List<MainMenuData> lstMainMenuData;
    private static ArrayList<Integer> lstCheckedMainMenu=new ArrayList<>();
    int updateFeatureID, editSTypePrinterID;
    List<STypeData> lstSTypeData;
    List<PrinterData> lstPrinterData;
    List<STypePrinterData> lstSTypePrinterData;
    SpSTypeAdapter spSTypeAdapter;
    SpPrinterNameAdapter spPrinterNameAdapter;
    STypePrinterListAdapter sTypePrinterListAdapter;
    private enum ConfirmType {DeletePrinter,RestoreData}
    int takeAwayType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        db=new DBHelper(this);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        sharedpreferences = getSharedPreferences(SystemSetting.MyPREFERENCES, Context.MODE_PRIVATE);
        takeAwayType=sharedpreferences.getInt("TakeAway",0);

        setLayoutResource();

        Cursor cur=db.getAdminPassword();
        if(!cur.moveToFirst()) {
            tvCurrentPassword.setVisibility(View.GONE);
        }else{
            tvCurrentPassword.setText("Current Password : "+cur.getString(0));
        }

        btnManageMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showManageMenuDialog();
            }
        });

        btnDeveloperMode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showDevPasswordDialog();
            }
        });

        btnPrinterSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showPrinterSettingDialog();
            }
        });

        btnItemSTypePrinterSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showSTypePrinterSettingDialog();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showPasswordChangeDialog();
            }
        });
        btnMultiPrinterSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AdminActivity.this,MultiPrinterSettingActivity.class);
                startActivity(i);
            }
        });
        btnRestoreData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(0,"Are you sure you want to restore data?",ConfirmType.RestoreData);
            }
        });
        rdoGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==rdoUseTakeAway.getId()){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("TakeAway", SystemSetting.USE_TAKE_AWAY);
                    editor.commit();
                }else if(checkedId==rdoUseBoth.getId()){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("TakeAway", SystemSetting.USE_BOTH_NORMAL_TAKE_AWAY);
                    editor.commit();
                }
            }
        });
        btnRefreshTakeAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdoUseTakeAway.setChecked(false);
                rdoUseBoth.setChecked(false);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("TakeAway", 0);
                editor.commit();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        getFeature();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFeatureCheckedListener(int position){
        updateFeatureID=lstFeatureSettingData.get(position).getFeatureID();
        db.updateAllowFeature(updateFeatureID,1);
    }

    @Override
    public void onFeatureUnCheckedListener(int position){
        updateFeatureID=lstFeatureSettingData.get(position).getFeatureID();
        db.updateAllowFeature(updateFeatureID,0);
    }

    @Override
    public void onMenuCheckedListener(int position){
        int checkedMainMenuID = lstMainMenuData.get(position).getMainMenuID();
        if(!lstCheckedMainMenu.contains(checkedMainMenuID)) {
            lstCheckedMainMenu.add(lstMainMenuData.get(position).getMainMenuID());
        }
    }

    @Override
    public void onMenuUnCheckedListener(int position){
        int removeIndex= lstCheckedMainMenu.indexOf(lstMainMenuData.get(position).getMainMenuID());
        if(removeIndex!=-1) {
            lstCheckedMainMenu.remove(removeIndex);
        }
    }

    @Override
    public void onEditButtonClickListener(int position){
        editSTypePrinterID = lstSTypePrinterData.get(position).getId();
        int stypeid= lstSTypePrinterData.get(position).getsTypeID();
        int printerid= lstSTypePrinterData.get(position).getPrinterID();
        btnCancel.setVisibility(View.VISIBLE);

        for(int i = 0; i< lstSTypeData.size(); i++){
            if(lstSTypeData.get(i).getsTypeID()==stypeid){
                spItemSType.setSelection(i);
            }
        }

        for(int i=0;i<lstPrinterData.size();i++){
            if(lstPrinterData.get(i).getPrinterId()==printerid){
                spPrinter.setSelection(i);
            }
        }
    }

    @Override
    public void onDeleteButtonClickListener(int position){
        showConfirmDialog(position,"Are you sure you want to delete?",ConfirmType.DeletePrinter);
    }

    private void setLayoutResource(){
        btnManageMenu=(Button)findViewById(R.id.btnManageMenu);
        btnDeveloperMode=(Button)findViewById(R.id.btnDeveloperMode);
        btnPrinterSetting=(Button)findViewById(R.id.btnPrinterSetting);
        btnChangePassword=(Button)findViewById(R.id.btnChangePassword);
        btnItemSTypePrinterSetting=(Button)findViewById(R.id.btnItemSTypePrinterSetting);
        btnMultiPrinterSetting=(Button)findViewById(R.id.btnMultiPrinterSetting);
        btnRestoreData=(Button)findViewById(R.id.btnRestoreData);
        tvCurrentPassword=(TextView)findViewById(R.id.tvCurrentPassword);
        tvAllowFeature=(TextView)findViewById(R.id.tvAllowFeature);
        lvFeature=(ListView)findViewById(R.id.lvFeature);
        rdoUseTakeAway=(RadioButton) findViewById(R.id.rdoUseTakeAway);
        rdoUseBoth=(RadioButton)findViewById(R.id.rdoUseBoth);
        rdoGroup=(RadioGroup) findViewById(R.id.rdoGroup);
        btnRefreshTakeAway=(ImageButton) findViewById(R.id.btnRefreshTakeAway);

        if(db.getFeatureResult(FeatureList.fUseMultiPrinter)==1){
            btnPrinterSetting.setVisibility(View.INVISIBLE);
        }else{
            btnItemSTypePrinterSetting.setVisibility(View.GONE);
            btnMultiPrinterSetting.setVisibility(View.GONE);
        }

        if(takeAwayType==SystemSetting.USE_TAKE_AWAY)rdoUseTakeAway.setChecked(true);
        else if(takeAwayType==SystemSetting.USE_BOTH_NORMAL_TAKE_AWAY)rdoUseBoth.setChecked(true);
    }

    private void getFeature(){
        Cursor cur= db.getFeature();
        lstFeatureSettingData=new ArrayList<>();
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                FeatureData data=new FeatureData();
                data.setFeatureID(cur.getInt(0));
                data.setFeatureName(cur.getString(1));
                data.setIsAllow(cur.getInt(2));
                int allow=cur.getInt(2);
                if(allow==1)data.setSelected(true);
                else data.setSelected(false);
                lstFeatureSettingData.add(data);
            }
            for(int i=0;i<lstFeatureSettingData.size();i++){
                if(lstFeatureSettingData.get(i).getFeatureName().equals(FeatureList.fPrint2)){
                    if(db.getFeatureResult(FeatureList.fUseMultiPrinter)==1){
                        lstFeatureSettingData.remove(i);
                    }
                }
                if(lstFeatureSettingData.get(i).getFeatureName().equals(FeatureList.fUseMultiPrinter)){
                    lstFeatureSettingData.remove(i);
                }
            }
        }
        featureListAdapter=new FeatureListAdapter(this,lstFeatureSettingData);
        lvFeature.setAdapter(featureListAdapter);
        featureListAdapter.setOnCheckedListener(this);
    }

    private void showManageMenuDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View vi=reg.inflate(R.layout.dg_manage_menu, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(vi);

        final TextView tvLabelChooseMainMenu=(TextView)vi.findViewById(R.id.tvLabelChooseMainMenu);
        lvMainMenu=(ListView)vi.findViewById(R.id.lvMainMenu);
        final Button btnAllow=(Button)vi.findViewById(R.id.btnAllow);

        setMainMenuDataToAdapter();

        dialog.setCancelable(true);
        final AlertDialog dialog1=dialog.create();
        dialog1.show();

        btnAllow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(lstCheckedMainMenu.size()==0){
                    systemSetting.showMessage(SystemSetting.INFO,"Choose Main Menu!",context,getLayoutInflater());
                    return;
                }
                db.resetMainMenu();

                for(int i=0;i<lstCheckedMainMenu.size();i++){
                    db.updateAllowedMainMenu(lstCheckedMainMenu.get(i));
                }
                lstCheckedMainMenu=new ArrayList<>();
                systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                dialog1.dismiss();
            }
        });
    }

    private void showDevPasswordDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_password, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final EditText etPassword=(EditText)passwordView.findViewById(R.id.etPassword);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

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
                String inputPassword=etPassword.getText().toString();
                if(inputPassword.length()==0){
                    systemSetting.showMessage(SystemSetting.INFO,"Enter Password!",context,getLayoutInflater());
                    return;
                }
                if(inputPassword.equals("111")){
                    Intent i=new Intent(getApplicationContext(), FeatureActivity.class);
                    startActivity(i);
                    passwordRequireDialog.dismiss();
                }else{
                    systemSetting.showMessage(SystemSetting.ERROR,"Invalid Password!",context,getLayoutInflater());
                }
            }
        });
    }

    private void showPrinterSettingDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_printer_setting, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final EditText etPrinterIP=(EditText)passwordView.findViewById(R.id.etPrinterIP);
        final EditText etPortNumber=(EditText)passwordView.findViewById(R.id.etPortNumber);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        etPortNumber.setVisibility(View.GONE);

        passwordDialog.setCancelable(true);
        final android.app.AlertDialog passwordRequireDialog=passwordDialog.create();
        passwordRequireDialog.show();

        Cursor cur=db.getPrinterSetting();
        if(cur.moveToFirst()){
            etPrinterIP.setText(cur.getString(0));
            etPortNumber.setText(cur.getString(1));
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                passwordRequireDialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               if(etPrinterIP.getText().toString().length()==0){
                   systemSetting.showMessage(SystemSetting.WARNING,"Enter Printer IP Address!",context,getLayoutInflater());
                   return;
               }
                if(db.insertPrinterSetting(etPrinterIP.getText().toString(),0)){
                    systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                    passwordRequireDialog.dismiss();
                }
            }
        });
    }

    private void setMainMenuDataToAdapter(){
        lstMainMenuData=new ArrayList<>();
        Cursor cur=db.getMainMenuForManageMenuDialog();
        while(cur.moveToNext()){
            MainMenuData data=new MainMenuData();
            data.setMainMenuID(cur.getInt(0));
            data.setMainMenuName(cur.getString(1));
            data.setIsAllow(cur.getInt(3));
            lstMainMenuData.add(data);
        }
        dgManageMenuListAdapter =new DgManageMenuListAdapter(this, lstMainMenuData);
        lvMainMenu.setAdapter(dgManageMenuListAdapter);
        dgManageMenuListAdapter.setOnCheckedListener(this);
    }

    private void showPasswordChangeDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_change_password, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final EditText etNewPassword=(EditText)passwordView.findViewById(R.id.etNewPassword);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

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
                String newPassword=etNewPassword.getText().toString();
                if(newPassword.length()==0){
                    systemSetting.showMessage(SystemSetting.INFO,"Enter Password!",context,getLayoutInflater());
                    return;
                }
                db.deleteAdminPassword();
                db.insertAdminPassword(newPassword);
                tvCurrentPassword.setVisibility(View.VISIBLE);
                tvCurrentPassword.setText("Current Password : "+newPassword);
                getFeature();
                systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                passwordRequireDialog.dismiss();
            }
        });
    }

    private void showSTypePrinterSettingDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_stype_printer_setting, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        spItemSType=(Spinner)passwordView.findViewById(R.id.spItemSType);
        spPrinter=(Spinner)passwordView.findViewById(R.id.spPrinter);
        //etPrinterIP=(EditText)passwordView.findViewById(R.id.etPrinterIP);
        final ImageButton btnClose=(ImageButton)passwordView.findViewById(R.id.btnClose);
        btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        btnSave=(Button)passwordView.findViewById(R.id.btnSave);
        lvSTypePrinterList=(ListView)passwordView.findViewById(R.id.lvSTypePrinterList);

        btnCancel.setVisibility(View.GONE);

        getItemSType(spItemSType);
        getPrinter(spPrinter);
        getItemSTypePrinterList(lvSTypePrinterList);

        passwordDialog.setCancelable(false);
        final android.app.AlertDialog passwordRequireDialog=passwordDialog.create();
        passwordRequireDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                passwordRequireDialog.dismiss();
                editSTypePrinterID=0;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                spItemSType.setSelection(0);
                spPrinter.setSelection(0);
                btnCancel.setVisibility(View.GONE);
                editSTypePrinterID=0;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int printerPosition=spPrinter.getSelectedItemPosition();
                int printerid=lstPrinterData.get(printerPosition).getPrinterId();
                int position=spItemSType.getSelectedItemPosition();
                int stypeid= lstSTypeData.get(position).getsTypeID();
                if(editSTypePrinterID==0) {
                    if(!db.insertItemSTypePrinterSetting(stypeid, printerid)){
                        systemSetting.showMessage(SystemSetting.ERROR,"Cannot save! Already Exist!",context,getLayoutInflater());
                        return;
                    }
                }else{
                    if(!db.updateItemSTypePrinterSetting(editSTypePrinterID,stypeid,printerid)){
                        systemSetting.showMessage(SystemSetting.ERROR,"Already Exist!",context,getLayoutInflater());
                        return;
                    }
                    btnCancel.setVisibility(View.GONE);
                }
                spItemSType.setSelection(0);
                spPrinter.setSelection(0);
                editSTypePrinterID=0;
                getItemSTypePrinterList(lvSTypePrinterList);
                systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
            }
        });
    }

    private void getItemSType(Spinner spSType) {
        lstSTypeData = new ArrayList<>();
        Cursor cur = db.getSType();
        while (cur.moveToNext()) {
            STypeData data = new STypeData();
            data.setsTypeID(cur.getInt(0));
            data.setsTypeName(cur.getString(1));
            lstSTypeData.add(data);
        }
        spSTypeAdapter = new SpSTypeAdapter(this, lstSTypeData);
        spSType.setAdapter(spSTypeAdapter);
    }

    private void getPrinter(Spinner spPrinter) {
        lstPrinterData = new ArrayList<>();
        Cursor cur = db.getPrinter();
        while (cur.moveToNext()) {
            PrinterData data = new PrinterData();
            data.setPrinterId(cur.getInt(0));
            data.setPrinterName(cur.getString(1));
            lstPrinterData.add(data);
        }
        spPrinterNameAdapter = new SpPrinterNameAdapter(this, lstPrinterData);
        spPrinter.setAdapter(spPrinterNameAdapter);
    }

    private void getItemSTypePrinterList(ListView lvSTypePrinter) {
        lstSTypePrinterData = new ArrayList<>();
        Cursor cur = db.getItemSTypePrinterSetting();
        while (cur.moveToNext()) {
            STypePrinterData data = new STypePrinterData();
            data.setId(cur.getInt(0));
            data.setsTypeID(cur.getInt(1));
            data.setPrinterID(cur.getInt(2));
            data.setsTypeName(cur.getString(3));
            data.setPrinterName(cur.getString(4));
            lstSTypePrinterData.add(data);
        }
        sTypePrinterListAdapter = new STypePrinterListAdapter(this, lstSTypePrinterData);
        lvSTypePrinter.setAdapter(sTypePrinterListAdapter);
        sTypePrinterListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showConfirmDialog(final int position, String msg, final ConfirmType confirmType){
        final LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_confirm, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final TextView tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        tvConfirmMessage.setText(msg);

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
                switch (confirmType) {
                    case DeletePrinter:
                        int deleteid = lstSTypePrinterData.get(position).getId();
                        db.deleteItemSTypePrinterSetting(deleteid);
                        getItemSTypePrinterList(lvSTypePrinterList);
                        passwordRequireDialog.dismiss();
                        break;
                    case RestoreData:
                        passwordRequireDialog.dismiss();
                        Restore restore=new Restore();
                        restore.execute("");
                        break;
                    default:
                        System.out.println("");
                        break;
                }
            }
        });
    }

    private class Restore extends AsyncTask<String ,String, String> {
        private final ProgressDialog dialog = new ProgressDialog(AdminActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading data...");
            this.dialog.show();
        }

        protected String doInBackground(final String... args){
            systemSetting.dataRestore(db,true);

            return "";
        }

        @Override
        protected void onPostExecute(final String error) {

            if (this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            if (error.isEmpty()){
                systemSetting.showMessage(SystemSetting.SUCCESS,"Restore Completed!",context,getLayoutInflater());
            }
            else {
                systemSetting.showMessage(SystemSetting.ERROR,"Restore Failed!",context,getLayoutInflater());
            }
        }
    }
}
