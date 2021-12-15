package com.bosictsolution.waiterone;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import common.DBHelper;
import common.SystemSetting;
import data.BookingData;

public class BookingEntryActivity extends AppCompatActivity {

    TextView tvWaiterName,tvLabelDate,tvLabelTime;
    EditText etGuestName,etPhone,etTotalPeople,etPurpose,etRemark;
    Button btnDate,btnTime,btnBookNow;
    static Button btnChooseTable;

    private static final int DATE_PICKER_DIALOG=1;
    private Calendar dateCalendar,timeCalendar;
    final Context context = this;
    private DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    TimePickerDialog timepickerdialog;

    static int bookingTableID;
    int waiterid,editBookingID,people;
    String waiterName,guestName,phone,datetime,purpose,remark,format,arrivalTime;
    boolean newBooking;
    List<BookingData> lstBookingData=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_entry);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        setLayoutResource();

        db=new DBHelper(this);
        dateCalendar=Calendar.getInstance();
        timeCalendar=Calendar.getInstance();

        Intent intent=getIntent();
        newBooking=intent.getBooleanExtra("NewBooking",false);
        waiterid=intent.getIntExtra("WaiterID",0);
        waiterName=intent.getStringExtra("WaiterName");
        editBookingID=intent.getIntExtra("EditBookingID",0);
        tvWaiterName.setText(waiterName);

        if(newBooking) {
            btnBookNow.setText("Book Now");
            updateDateButtonText();
            updateTimeButtonText();
        }
        else {
           getEditBookingData(editBookingID);
        }

        btnDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showDialog(DATE_PICKER_DIALOG);
            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showTimePicker();
            }
        });
        btnChooseTable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i=new Intent(getApplicationContext(),TableActivity.class);
                i.putExtra("role", "booking");
                startActivity(i);
            }
        });
        btnBookNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(validateControls()){
                    arrivalTime=btnTime.getText().toString();
                    guestName=etGuestName.getText().toString();
                    phone=etPhone.getText().toString();
                    datetime = btnDate.getText().toString();
                    people=Integer.parseInt(etTotalPeople.getText().toString());
                    purpose=etPurpose.getText().toString();
                    remark=etRemark.getText().toString();
                    if(btnBookNow.getText().toString().equals("Book Now")){
                        insertBooking();
                    }else if(btnBookNow.getText().toString().equals("Update")){
                        updateBooking();
                    }
                }
            }
        });
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

    private DatePickerDialog showDatePicker(){
        DatePickerDialog datePicker=new DatePickerDialog(BookingEntryActivity.this,new DatePickerDialog.OnDateSetListener() {

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
        timepickerdialog = new TimePickerDialog(BookingEntryActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

    private void setLayoutResource(){
        tvWaiterName=(TextView)findViewById(R.id.tvWaiterName);
        etGuestName=(EditText) findViewById(R.id.etGuestName);
        etPhone=(EditText)findViewById(R.id.etPhone);
        etTotalPeople=(EditText)findViewById(R.id.etTotalPeople);
        etPurpose=(EditText)findViewById(R.id.etPurpose);
        etRemark=(EditText)findViewById(R.id.etRemark);
        btnDate=(Button)findViewById(R.id.btnDate);
        btnTime=(Button)findViewById(R.id.btnTime);
        btnChooseTable=(Button)findViewById(R.id.btnChooseTable);
        btnBookNow=(Button)findViewById(R.id.btnBookingNow);
        tvLabelDate=(TextView)findViewById(R.id.tvLabelDate);
        tvLabelTime=(TextView)findViewById(R.id.tvLabelTime);
    }

    private boolean validateControls(){
        if(etGuestName.getText().toString().length()==0){
            systemSetting.showMessage(SystemSetting.WARNING,"Enter Guest Name",context,getLayoutInflater());
            etGuestName.requestFocus();
            return false;
        }
        if(etPhone.getText().toString().length()==0){
            systemSetting.showMessage(SystemSetting.WARNING,"Enter Phone",context,getLayoutInflater());
            etPhone.requestFocus();
            return false;
        }
        if(etTotalPeople.getText().toString().length()==0){
            systemSetting.showMessage(SystemSetting.WARNING,"Enter Number of People",context,getLayoutInflater());
            etTotalPeople.requestFocus();
            return false;
        }
        if(bookingTableID==0){
            systemSetting.showMessage(SystemSetting.WARNING,"Choose Table",context,getLayoutInflater());
            return false;
        }
        return true;
    }

    private void getEditBookingData(int bookingid){
        lstBookingData=new ArrayList<>();
        Cursor cur=db.getBookingByBookingID(bookingid);
        if(cur.moveToFirst()){
            BookingData data = new BookingData();
            data.setBookingid(cur.getInt(0));
            data.setBookingTableid(cur.getInt(1));
            data.setBookingTableName(cur.getString(2));
            data.setGuestName(cur.getString(3));
            data.setPhone(cur.getString(4));
            data.setDate(cur.getString(5));
            data.setTime(cur.getString(6));
            data.setTotalPeople(cur.getInt(7));
            data.setPurpose(cur.getString(8));
            data.setRemark(cur.getString(9));
            lstBookingData.add(data);
        }
        if(lstBookingData.size()!=0) {
            etGuestName.setText(lstBookingData.get(0).getGuestName());
            etPhone.setText(lstBookingData.get(0).getPhone());
            etTotalPeople.setText(String.valueOf(lstBookingData.get(0).getTotalPeople()));
            etPurpose.setText(lstBookingData.get(0).getPurpose());
            etRemark.setText(lstBookingData.get(0).getRemark());
            btnChooseTable.setText(lstBookingData.get(0).getBookingTableName());
            btnTime.setText(lstBookingData.get(0).getTime());
            bookingTableID=lstBookingData.get(0).getBookingTableid();
            editBookingID=lstBookingData.get(0).getBookingid();
            /*SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date;
            try {
                String sDate = lstBookingData.get(0).getDate();
                date = dateTimeFormat.parse(sDate);
                dateCalendar.setTime(date);
            } catch (ParseException e) {
                Log.e("BookingEntry", e.getMessage(), e);
            }
            updateDateButtonText();
            timeCalendar=Calendar.getInstance();*/
            btnDate.setText(lstBookingData.get(0).getDate());
            btnBookNow.setText("Update");
        }
    }

    private void insertBooking(){
        if(db.insertBooking(waiterid,bookingTableID,guestName,phone,datetime,arrivalTime,people,purpose,remark)){
            systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
            finish();
        }
    }

    private void updateBooking(){
        if(db.updateBooking(editBookingID,waiterid,bookingTableID,guestName,phone,datetime,arrivalTime,people,purpose,remark)){
            systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
            finish();
        }
    }
}
