package com.bosictsolution.waiterone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import adapter.RpSaleByWaiterListAdapter;
import common.SystemSetting;
import data.RpSaleByWaiterData;
import common.DBHelper;

/**
 * Created by NweYiAung on 23-12-2016.
 */
public class RpSaleByWaiterActivity extends AppCompatActivity {

    DBHelper db;
    RpSaleByWaiterData rpSaleByWaiterData;
    List<RpSaleByWaiterData> lstRpSaleByWaiterData;
    RpSaleByWaiterListAdapter rpSaleByWaiterListAdapter;
    private ProgressDialog progDialog;
    TextView tvHeaderDate,tvHeaderTable, tvHeaderGrandTotal,tvFromDate,tvToDate,tvToday,tvHeaderCustomer;
    ListView lvReportSaleByWaiter;
    String fromDate,toDate;
    ArrayList<Integer> lstCheckedWaiter=new ArrayList<>();
    private Calendar cCalendar;
    /*String[] checked_waiter={};*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_sale_by_waiter);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        Intent i=getIntent();
        fromDate=i.getStringExtra("from_date");
        toDate=i.getStringExtra("to_date");
        lstCheckedWaiter=i.getIntegerArrayListExtra("checked_waiter");

        db = new DBHelper(this);
        setTitle("Sale By Waiter Report");
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
        tvHeaderDate = (TextView) findViewById(R.id.tvHeaderDate);
        tvHeaderTable = (TextView) findViewById(R.id.tvHeaderTable);
        tvHeaderGrandTotal = (TextView) findViewById(R.id.tvHeaderGrandTotal);
        lvReportSaleByWaiter =(ListView) findViewById(R.id.lvReportSaleByWaiter);
        tvHeaderCustomer = (TextView) findViewById(R.id.tvHeaderCustomer);

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
        String waiteridList="";
        for(int i=0;i<lstCheckedWaiter.size();i++){
            waiteridList +=lstCheckedWaiter.get(i)+",";
        }
        if(waiteridList.length()!=0) {
            waiteridList = waiteridList.substring(0, waiteridList.length() - 1);
        }
        lstRpSaleByWaiterData =new ArrayList<>();
        List<Integer> lstWaiterID=new ArrayList<>();
        Cursor cur=db.getReportSaleByWaiter(fromDate,toDate,waiteridList);
        while(cur.moveToNext()){
            rpSaleByWaiterData =new RpSaleByWaiterData();
            lstWaiterID.add(cur.getInt(0));
            rpSaleByWaiterData.setWaiterID(cur.getInt(0));
            int times= Collections.frequency(lstWaiterID,cur.getInt(0));
            if(times == 1) {
                rpSaleByWaiterData.setTotal(cur.getDouble(5));
                rpSaleByWaiterData.setWaiterName(cur.getString(1));
                rpSaleByWaiterData.setTotalSaleTable(cur.getInt(7));
            }
            rpSaleByWaiterData.setDate(cur.getString(2));
            rpSaleByWaiterData.setTableName(cur.getString(3));
            rpSaleByWaiterData.setGrandTotal(cur.getDouble(4));
            rpSaleByWaiterData.setTotalCustomer(cur.getInt(6));
            lstRpSaleByWaiterData.add(rpSaleByWaiterData);
        }
        rpSaleByWaiterListAdapter =new RpSaleByWaiterListAdapter(this, lstRpSaleByWaiterData);
        lvReportSaleByWaiter.setAdapter(rpSaleByWaiterListAdapter);
    }

    /**public class GetData extends AsyncTask<String,String,String> {
        String msg;
        boolean isSuccess;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverConnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                }else{
                    String waiteridList="";
                    for(int i=0;i<lstCheckedWaiter.size();i++){
                        waiteridList +=lstCheckedWaiter.get(i)+",";
                    }
                    if(waiteridList.length()!=0) {
                        waiteridList = waiteridList.substring(0, waiteridList.length() - 1);
                    }
                    String sql_query="exec PrcReportSaleByWaiter '"+fromDate+"','"+toDate+"','"+waiteridList+"'";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);
                    lstRpSaleByWaiterData =new ArrayList<>();
                    List<Integer> lstWaiterID=new ArrayList<>();
                    while(rs.next()){
                        rpSaleByWaiterData =new RpSaleByWaiterData();
                        lstWaiterID.add(rs.getInt("WaiterID"));
                        rpSaleByWaiterData.setWaiterID(rs.getInt("WaiterID"));
                        int times= Collections.frequency(lstWaiterID,rs.getInt("WaiterID"));
                        if(times == 1) {
                            rpSaleByWaiterData.setTotal(rs.getDouble("Total"));
                            rpSaleByWaiterData.setWaiterName(rs.getString("WaiterName"));
                            rpSaleByWaiterData.setTotalSaleTable(rs.getInt("TotalSaleTable"));
                        }
                        rpSaleByWaiterData.setDate(rs.getString("Date"));
                        rpSaleByWaiterData.setTableName(rs.getString("Table"));
                        rpSaleByWaiterData.setGrandTotal(rs.getDouble("GrandTotal"));
                        rpSaleByWaiterData.setTotalCustomer(rs.getInt("TotalCustomer"));
                        lstRpSaleByWaiterData.add(rpSaleByWaiterData);
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
        rpSaleByWaiterListAdapter =new RpSaleByWaiterListAdapter(this, lstRpSaleByWaiterData);
        lvReportSaleByWaiter.setAdapter(rpSaleByWaiterListAdapter);
    }
}
