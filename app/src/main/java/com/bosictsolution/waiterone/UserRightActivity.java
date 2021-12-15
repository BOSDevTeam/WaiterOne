package com.bosictsolution.waiterone;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapter.DefineUserRightAdapter;
import adapter.ModuleListAdapter;
import common.DBHelper;
import common.SystemSetting;
import data.ModuleData;
import data.WaiterData;
import listener.DefineUserRightClickListener;
import listener.ModuleCheckedListener;

public class UserRightActivity extends AppCompatActivity implements DefineUserRightClickListener, ModuleCheckedListener {

    TextView tvDefineUserRight;
    ListView lvUser;

    private static DBHelper db;
    final Context context = this;
    SystemSetting systemSetting=new SystemSetting();

    List<ModuleData> lstModuleData;
    ModuleListAdapter moduleListAdapter;
    ArrayList<Integer> lstCheckedModuleID=new ArrayList<>();
    List<WaiterData> lstUserData;
    DefineUserRightAdapter userSetupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_right);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);

        setLayoutResource();
        showUserList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            Intent i=new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent i=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDefineButtonClickListener(int position){
        int userid=lstUserData.get(position).getWaiterid();
        showUserRightDialog(userid,lstUserData.get(position).getWaiterName());
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

    private void setLayoutResource(){
        tvDefineUserRight=(TextView)findViewById(R.id.tvDefineUserRight);
        lvUser=(ListView)findViewById(R.id.lvUser);
    }

    private void showUserRightDialog(final int id, String name) {
        LayoutInflater reg = LayoutInflater.from(context);
        View view = reg.inflate(R.layout.dg_define_user_right, null);
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final TextView tvLabelAllowRight = (TextView) view.findViewById(R.id.tvLabelAllowRight);
        final TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        final Button btnClose = (Button) view.findViewById(R.id.btnClose);
        final Button btnSave = (Button) view.findViewById(R.id.btnSave);
        final ListView lvModule = (ListView) view.findViewById(R.id.lvModule);

        getAllModule(lvModule);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog = dialog.create();
        setupDialog.show();

        tvUserName.setText(name);
        Cursor cur = db.getModuleByUserID(id);
        while (cur.moveToNext()) {
            lstCheckedModuleID.add(cur.getInt(0));
        }

        for (int i = 0; i < lstModuleData.size(); i++) {
            if (lstCheckedModuleID.contains(lstModuleData.get(i).getModuleID())) {
                lstModuleData.get(i).setSelected(true);
            }
        }
        moduleListAdapter = new ModuleListAdapter(this, lstModuleData);
        lvModule.setAdapter(moduleListAdapter);
        moduleListAdapter.setModuleCheckedListener(this);

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showUserList();
                lstCheckedModuleID = new ArrayList<>();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (lstCheckedModuleID.size() == 0) {
                    systemSetting.showMessage(SystemSetting.WARNING,"Choose Module!",context,getLayoutInflater());
                    return;
                }
                db.deleteUserRight(id);
                for (int i = 0; i < lstCheckedModuleID.size(); i++) {
                    db.insertUserRight(id, lstCheckedModuleID.get(i));
                }
                systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                showUserList();
                lstCheckedModuleID = new ArrayList<>();
                setupDialog.dismiss();
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
        userSetupListAdapter=new DefineUserRightAdapter(this,lstUserData);
        lvUser.setAdapter(userSetupListAdapter);
        userSetupListAdapter.setOnDefineButtonClickListener(this);
    }
}
