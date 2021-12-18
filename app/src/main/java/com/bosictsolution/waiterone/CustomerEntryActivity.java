package com.bosictsolution.waiterone;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import common.DBHelper;
import common.SystemSetting;

public class CustomerEntryActivity extends AppCompatActivity {

    TextView tvWaiterName,tvLabelTable,tvLabelDate,tvLabelTime,tvLabelMan,tvLabelWomen,tvLabelChild;
    EditText etMan,etWomen,etChild;
    Button btnDate,btnTime,btnConfirm, btnGetCustomerData;
    static Button btnChooseTable;

    private static final int DATE_PICKER_DIALOG=1;
    private Calendar dateCalendar,timeCalendar;
    final Context context = this;
    private DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    TimePickerDialog timepickerdialog;

    public static int choosed_table_id;
    public static String choosed_table_name;
    int tranid,insertTableID,insertWaiterID,insertMen,insertWomen,insertChild,insertTotal,editTableID;
    String insertDate,insertTime,insertOrUpdate,editTableName,format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_entry);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        setLayoutResource();

        db=new DBHelper(this);
        dateCalendar=Calendar.getInstance();
        timeCalendar=Calendar.getInstance();

        Intent intent=getIntent();
        tvWaiterName.setTag(intent.getIntExtra("waiterid",0));
        tvWaiterName.setText(intent.getStringExtra("waitername"));
        choosed_table_id=intent.getIntExtra("tableid", 0);
        choosed_table_name=intent.getStringExtra("tablename");
        btnChooseTable.setTag(choosed_table_id);
        btnChooseTable.setText(choosed_table_name);

        registerButtonListenersAndSetDefaultText();
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
    protected Dialog onCreateDialog(int id){
        switch(id){
            case DATE_PICKER_DIALOG:
                return showDatePicker();
        }
        return super.onCreateDialog(id);
    }

    private void registerButtonListenersAndSetDefaultText(){
        btnDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_DIALOG);

            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimePicker();

            }
        });
        updateDateButtonText();
        updateTimeButtonText();
        btnChooseTable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i=new Intent(CustomerEntryActivity.this,TableActivity.class);
                i.putExtra("role", "just_choice");
                i.putExtra("from_waiter_main", false);
                i.putExtra("from_customer_info", true);
                startActivity(i);
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(btnChooseTable.getTag()==null){
                    systemSetting.showMessage(SystemSetting.INFO,"Please Choose Table!",context,getLayoutInflater());
                    return;
                }
                else{
                    if(validateControls()) {
                        insertTableID = Integer.parseInt(btnChooseTable.getTag().toString());
                        insertWaiterID = Integer.parseInt(tvWaiterName.getTag().toString());
                        insertDate = btnDate.getText().toString();
                        insertTime =btnTime.getText().toString();
                        insertMen = Integer.parseInt((etMan.getText().length() == 0) ? "0" : etMan.getText().toString());
                        insertWomen = Integer.parseInt((etWomen.getText().length() == 0) ? "0" : etWomen.getText().toString());
                        insertChild = Integer.parseInt((etChild.getText().length() == 0) ? "0" : etChild.getText().toString());
                        insertTotal = insertMen + insertWomen + insertChild;

                        insertOrUpdate = btnConfirm.getText().toString();
                        insertUpdateCustomer();
                    }
                }
            }
        });
        btnGetCustomerData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(btnChooseTable.getTag()==null){
                    systemSetting.showMessage(SystemSetting.INFO,"Please Choose Table",context,getLayoutInflater());
                    return;
                }
                else{
                    editTableID=Integer.parseInt(btnChooseTable.getTag().toString());
                    editTableName=String.valueOf(btnChooseTable.getText().toString());
                    getCustomerInfo();
                }

            }
        });
    }

    private DatePickerDialog showDatePicker(){
        DatePickerDialog datePicker=new DatePickerDialog(CustomerEntryActivity.this,new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateCalendar.set(Calendar.YEAR,year);
                dateCalendar.set(Calendar.MONTH, monthOfYear);
                dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateButtonText();
            }
        },dateCalendar.get(Calendar.YEAR),dateCalendar.get(Calendar.MONTH),dateCalendar.get(Calendar.DAY_OF_MONTH));
        return datePicker;
    }

    private void updateDateButtonText(){
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        String dateForButton=dateFormat.format(dateCalendar.getTime());
        btnDate.setText(dateForButton);
    }

    private void showTimePicker(){
        timepickerdialog = new TimePickerDialog(CustomerEntryActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String sMinute=String.valueOf(minute);
                if (hourOfDay == 0) {
                    hourOfDay += 12;
                    format = "AM";
                }
                else if (hourOfDay == 12) {
                    format = "PM";
                }
                else if (hourOfDay > 12) {
                    hourOfDay -= 12;
                    format = "PM";
                }
                else {
                    format = "AM";
                }
                if(minute==0) btnTime.setText(hourOfDay + ":" + "00" + format);
                else if(sMinute.length()==1)btnTime.setText(hourOfDay+":"+"0"+minute+format);
                else btnTime.setText(hourOfDay + ":" + minute + format);
            }
        }, timeCalendar.get(Calendar.HOUR_OF_DAY), timeCalendar.get(Calendar.MINUTE), false);
        timepickerdialog.show();
    }

    private void updateTimeButtonText(){
        SimpleDateFormat timeFormat=new SimpleDateFormat(SystemSetting.TIME_FORMAT);
        String timeForButton=timeFormat.format(timeCalendar.getTime());
        btnTime.setText(timeForButton);
    }

    private void insertUpdateCustomer(){
        tranid= db.getTranIDFromMasterSaleTempByTableID(insertTableID);
        if(insertOrUpdate.equals("CONFIRM")){
            Cursor curCustomerID=db.getCustomerInfoIDByTranID(tranid);
            if(curCustomerID.moveToFirst()){
                if(db.updateCustomerInfoByTranID(tranid,insertTableID,insertWaiterID,insertDate,insertTime,insertMen,insertWomen,insertChild,insertTotal)){
                    systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                }
            }else{
                if(db.insertCustomerInfo(tranid,insertTableID,insertWaiterID,insertDate,insertTime,insertMen,insertWomen,insertChild,insertTotal)){
                    systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
                }
            }
        }else{
            if(db.updateCustomerInfoByTranID(tranid,insertTableID,insertWaiterID,insertDate,insertTime,insertMen,insertWomen,insertChild,insertTotal)){
                systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
            }
        }
        choosed_table_id=0;
        choosed_table_name="";
        finish();
    }

    private void getCustomerInfo() {
        int tranid = db.getTranIDFromMasterSaleTempByTableID(editTableID);
        Cursor curCustomer = db.getCustomerInfoByTranID(tranid);
        if (curCustomer.moveToFirst()) {
            btnDate.setText(curCustomer.getString(1));
            btnTime.setText(curCustomer.getString(2));
            etMan.setText(String.valueOf(curCustomer.getInt(3)));
            etWomen.setText(String.valueOf(curCustomer.getInt(4)));
            etChild.setText(String.valueOf(curCustomer.getInt(5)));
            btnConfirm.setText("UPDATE");
        }
    }

    private boolean validateControls(){
        if(etMan.getText().length()==0 && etWomen.getText().length()==0 && etChild.getText().length()==0){
            systemSetting.showMessage(SystemSetting.WARNING,"At least one person must have!",context,getLayoutInflater());
            return false;
        }
        return true;
    }

    private void setLayoutResource(){
        tvWaiterName=(TextView)findViewById(R.id.tvWaiterName);
        etMan=(EditText)findViewById(R.id.etMan);
        etWomen=(EditText)findViewById(R.id.etWomen);
        etChild=(EditText)findViewById(R.id.etChild);
        btnChooseTable=(Button)findViewById(R.id.btnChooseTable);
        btnDate=(Button)findViewById(R.id.btnDate);
        btnTime=(Button)findViewById(R.id.btnTime);
        btnConfirm=(Button)findViewById(R.id.btnConfirm);
        btnGetCustomerData =(Button)findViewById(R.id.btnGetCustomerData);
        tvLabelTable=(TextView)findViewById(R.id.tvLabelTable);
        tvLabelDate=(TextView)findViewById(R.id.tvLabelDate);
        tvLabelTime=(TextView)findViewById(R.id.tvLabelTime);
        tvLabelMan=(TextView)findViewById(R.id.tvLabelMan);
        tvLabelWomen=(TextView)findViewById(R.id.tvLabelWomen);
        tvLabelChild=(TextView)findViewById(R.id.tvLabelChild);
    }
}
