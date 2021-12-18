package com.bosictsolution.waiterone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.RpSaleByHourlyListAdapter;
import common.SystemSetting;
import data.RpSaleByHourlyData;
import common.DBHelper;

public class RpSaleByHourlyActivity extends AppCompatActivity {

    DBHelper db;
    RpSaleByHourlyData rpSaleByHourlyData;
    List<RpSaleByHourlyData> lstRpSaleByHourlyData;
    RpSaleByHourlyListAdapter rpSaleByHourlyListAdapter;
    private ProgressDialog progDialog;
    TextView tvHeaderTime, tvHeaderTotalTransaction, tvHeaderTotalItem,tvHeaderTotalCustomer, tvHeaderTotalAmount,tvFromDate,tvToDate,tvToday;
    ListView lvReportSaleByHourly;
    String fromDate,toDate;
    private Calendar cCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_sale_by_hourly);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        Intent i=getIntent();
        fromDate=i.getStringExtra("from_date");
        toDate=i.getStringExtra("to_date");

        db = new DBHelper(this);
        setTitle("Sale By Hourly Report");
        setLayoutResource();
        getData();
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
    public void setTitle(CharSequence title) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.actionbar_label, null);
        TextView tvActionBarText = (TextView) vi.findViewById(R.id.tvActionBarText);
        tvActionBarText.setText(title);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setCustomView(vi, params);
    }

    private void setLayoutResource() {
        tvHeaderTime = (TextView) findViewById(R.id.tvHeaderTime);
        tvHeaderTotalTransaction = (TextView) findViewById(R.id.tvHeaderTotalTransaction);
        tvHeaderTotalItem = (TextView) findViewById(R.id.tvHeaderTotalItem);
        tvHeaderTotalCustomer = (TextView) findViewById(R.id.tvHeaderTotalCustomer);
        tvHeaderTotalAmount = (TextView) findViewById(R.id.tvHeaderTotalAmount);
        lvReportSaleByHourly =(ListView) findViewById(R.id.lvReportSaleByHourly);

        tvFromDate = (TextView) findViewById(R.id.tvFromDate);
        tvToDate = (TextView) findViewById(R.id.tvToDate);
        tvFromDate.setText("From Date: "+fromDate);
        tvToDate.setText("To Date: "+toDate);
        tvToday = (TextView) findViewById(R.id.tvToday);
        cCalendar=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        String todayDate=dateFormat.format(cCalendar.getTime());
        if(todayDate.equals(fromDate) && todayDate.equals(toDate)) tvToday.setVisibility(View.VISIBLE);
        else tvToday.setVisibility(View.GONE);

        progDialog=new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(false);
    }

    private void getData(){
        lstRpSaleByHourlyData =new ArrayList<>();
        db.getReportSaleByHourly(fromDate,toDate);
        Cursor cur_12am=db.getReportSaleByHourly12AM();
        if(cur_12am.moveToFirst()){
            rpSaleByHourlyData = new RpSaleByHourlyData();
            rpSaleByHourlyData.setCTime(cur_12am.getString(0));
            rpSaleByHourlyData.setTotalTransaction(cur_12am.getInt(1));
            rpSaleByHourlyData.setTotalItem(cur_12am.getInt(2));
            rpSaleByHourlyData.setTotalAmount(cur_12am.getDouble(3));
            rpSaleByHourlyData.setTotalCustomer(cur_12am.getInt(4));
            lstRpSaleByHourlyData.add(rpSaleByHourlyData);
        }
        Cursor cur_am=db.getReportSaleByHourlyAM();
        while(cur_am.moveToNext()) {
            if(!cur_am.getString(0).contains("12")) {
                rpSaleByHourlyData = new RpSaleByHourlyData();
                rpSaleByHourlyData.setCTime(cur_am.getString(0));
                rpSaleByHourlyData.setTotalTransaction(cur_am.getInt(1));
                rpSaleByHourlyData.setTotalItem(cur_am.getInt(2));
                rpSaleByHourlyData.setTotalAmount(cur_am.getDouble(3));
                rpSaleByHourlyData.setTotalCustomer(cur_am.getInt(4));
                lstRpSaleByHourlyData.add(rpSaleByHourlyData);
            }
        }
        Cursor cur_12pm=db.getReportSaleByHourly12PM();
        if(cur_12pm.moveToFirst()){
            rpSaleByHourlyData = new RpSaleByHourlyData();
            rpSaleByHourlyData.setCTime(cur_12pm.getString(0));
            rpSaleByHourlyData.setTotalTransaction(cur_12pm.getInt(1));
            rpSaleByHourlyData.setTotalItem(cur_12pm.getInt(2));
            rpSaleByHourlyData.setTotalAmount(cur_12pm.getDouble(3));
            rpSaleByHourlyData.setTotalCustomer(cur_12pm.getInt(4));
            lstRpSaleByHourlyData.add(rpSaleByHourlyData);
        }
        Cursor cur_pm=db.getReportSaleByHourlyPM();
        while(cur_pm.moveToNext()) {
            if(!cur_pm.getString(0).contains("12")) {
                rpSaleByHourlyData = new RpSaleByHourlyData();
                rpSaleByHourlyData.setCTime(cur_pm.getString(0));
                rpSaleByHourlyData.setTotalTransaction(cur_pm.getInt(1));
                rpSaleByHourlyData.setTotalItem(cur_pm.getInt(2));
                rpSaleByHourlyData.setTotalAmount(cur_pm.getDouble(3));
                rpSaleByHourlyData.setTotalCustomer(cur_pm.getInt(4));
                lstRpSaleByHourlyData.add(rpSaleByHourlyData);
            }
        }
        rpSaleByHourlyListAdapter =new RpSaleByHourlyListAdapter(this, lstRpSaleByHourlyData);
        lvReportSaleByHourly.setAdapter(rpSaleByHourlyListAdapter);
    }

    /**public class GetData extends AsyncTask<String,String,String> {
        String msg,time;
        boolean isSuccess;
        int iTime;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverConnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                }else{
                    String sql_query="exec PrcReportSaleByHourly '"+fromDate+"','"+toDate+"'";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);
                    lstRpSaleByHourlyData =new ArrayList<>();
                    while(rs.next()){
                        rpSaleByHourlyData =new RpSaleByHourlyData();
                        iTime=rs.getInt("CTime");

                            if(iTime==1)time= "01:00AM - " + "01:59AM";
                            else if(iTime==2)time= "02:00AM - " + "02:59AM";
                            else if(iTime==3)time= "03:00AM - " + "03:59AM";
                            else if(iTime==4)time= "04:00AM - " + "04:59AM";
                            else if(iTime==5)time= "05:00AM - " + "05:59AM";
                            else if(iTime==6)time= "06:00AM - " + "06:59AM";
                            else if(iTime==7)time= "07:00AM - " + "07:59AM";
                            else if(iTime==8)time= "08:00AM - " + "08:59AM";
                            else if(iTime==9)time= "09:00AM - " + "09:59AM";
                            else if(iTime==10)time= "10:00AM - " + "10:59AM";
                            else if(iTime==11)time= "11:00AM - " + "11:59AM";
                            else if(iTime==12)time= "12:00PM - " + "12:59PM";
                            else if(iTime==13)time= "01:00PM - " + "01:59PM";
                            else if(iTime==14)time= "02:00PM - " + "02:59PM";
                            else if(iTime==15)time= "03:00PM - " + "03:59PM";
                            else if(iTime==16)time= "04:00PM - " + "04:59PM";
                            else if(iTime==17)time= "05:00PM - " + "05:59PM";
                            else if(iTime==18)time= "06:00PM - " + "06:59PM";
                            else if(iTime==19)time= "07:00PM - " + "07:59PM";
                            else if(iTime==20)time= "08:00PM - " + "08:59PM";
                            else if(iTime==21)time= "09:00PM - " + "09:59PM";
                            else if(iTime==22)time= "10:00PM - " + "10:59PM";
                            else if(iTime==23)time= "11:00PM - " + "11:59PM";

                        rpSaleByHourlyData.setCTime(time);
                        rpSaleByHourlyData.setTotalTransaction(rs.getInt("TotalTransaction"));
                        rpSaleByHourlyData.setTotalItem(rs.getInt("TotalItem"));
                        rpSaleByHourlyData.setTotalCustomer(rs.getInt("TotalCustomer"));
                        rpSaleByHourlyData.setTotalAmount(rs.getDouble("TotalAmount"));
                        lstRpSaleByHourlyData.add(rpSaleByHourlyData);
                    }
                    isSuccess=true;
                }
            }catch(SQLException e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
            return msg;
        }

        @Override
        protected void onPreExecute(){
            progDialog.setMessage("Loading......");
            progDialog.show();
        }
        @Override
        protected void onPostExecute(String r){
            progDialog.hide();
            if(!isSuccess){
                Toast.makeText(getApplicationContext(),r,Toast.LENGTH_SHORT).show();
            }
            else{
                setData();
            }
        }
    }**/

    private void setData(){
        rpSaleByHourlyListAdapter =new RpSaleByHourlyListAdapter(this, lstRpSaleByHourlyData);
        lvReportSaleByHourly.setAdapter(rpSaleByHourlyListAdapter);
    }
}
