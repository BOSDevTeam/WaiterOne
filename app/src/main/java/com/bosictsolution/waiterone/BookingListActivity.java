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

import adapter.BookingListAdapter;
import common.DBHelper;
import common.SystemSetting;
import data.BookingData;
import listener.BookingButtonClickListener;

public class BookingListActivity extends AppCompatActivity implements BookingButtonClickListener {

    TextView tvWaiterName,tvHeaderBookingTableName,tvHeaderGuestName,tvHeaderBookingDate,tvHeaderBookingTime,tvHeaderPeople,tvHeaderPhone,tvConfirmMessage;
    Button btnNewBooking;
    ListView lvBooking;

    DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    final Context context = this;
    BookingListAdapter bookingListAdapter;

    int waiterid,deleteBookingID;
    List<BookingData> lstBookingData=new ArrayList<>();
    String waiterName,deleteBookingConfirmMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);

        setLayoutResource();

        Intent intent=getIntent();
        waiterid=intent.getIntExtra("waiterid", 0);
        waiterName=intent.getStringExtra("waitername");
        tvWaiterName.setText(waiterName);

        btnNewBooking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                BookingEntryActivity.bookingTableID=0;
                Intent i=new Intent(getApplicationContext(),BookingEntryActivity.class);
                i.putExtra("NewBooking",true);
                i.putExtra("WaiterID",waiterid);
                i.putExtra("WaiterName",waiterName);
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        getBookingByWaiter();
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
    public void onEditButtonClickListener(int position){
        int editBookingID=lstBookingData.get(position).getBookingid();
        Intent i=new Intent(getApplicationContext(),BookingEntryActivity.class);
        i.putExtra("NewBooking",false);
        i.putExtra("EditBookingID",editBookingID);
        i.putExtra("WaiterID",waiterid);
        i.putExtra("WaiterName",waiterName);
        startActivity(i);
    }

    @Override
    public void onDeleteButtonClickListener(int position){
        deleteBookingID=lstBookingData.get(position).getBookingid();
        deleteBookingConfirmMessage="Are you sure you want to delete this booking table "+lstBookingData.get(position).getBookingTableName()+"?";
        showConfirmDialog();
    }

    private void setLayoutResource(){
        tvWaiterName=(TextView)findViewById(R.id.tvWaiterName);
        tvHeaderBookingTableName=(TextView)findViewById(R.id.tvHeaderBookingTableName);
        tvHeaderGuestName=(TextView)findViewById(R.id.tvHeaderGuestName);
        tvHeaderBookingDate=(TextView)findViewById(R.id.tvHeaderBookingDate);
        tvHeaderBookingTime=(TextView)findViewById(R.id.tvHeaderBookingTime);
        tvHeaderPeople=(TextView)findViewById(R.id.tvHeaderPeople);
        tvHeaderPhone=(TextView)findViewById(R.id.tvHeaderPhone);
        btnNewBooking=(Button)findViewById(R.id.btnNewBooking);
        lvBooking=(ListView)findViewById(R.id.lvBooking);
    }

    private void getBookingByWaiter(){
        lstBookingData=new ArrayList<>();
        Cursor cur=db.getBookingByWaiterID(waiterid);
        while (cur.moveToNext()) {
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
        bookingListAdapter =new BookingListAdapter(this,lstBookingData);
        lvBooking.setAdapter(bookingListAdapter);
        bookingListAdapter.setOnBookingButtonClickListener(this);
    }

    private void deleteBooking(){
        if(db.deleteBookingByBookingID(deleteBookingID)){
            systemSetting.showMessage(SystemSetting.SUCCESS,"Success!",context,getLayoutInflater());
        }
    }

    private void showConfirmDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_confirm, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        tvConfirmMessage.setText(deleteBookingConfirmMessage);

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
                deleteBooking();
                getBookingByWaiter();
                passwordRequireDialog.dismiss();
            }
        });
    }
}
