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
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.RpBestWaiterListAdapter;
import adapter.RpWaiterPerformanceTableListAdapter;
import common.SystemSetting;
import data.TableData;
import data.WaiterData;
import common.DBHelper;

public class RpWaiterPerformanceActivity extends AppCompatActivity {

    DBHelper db;
    List<WaiterData> lstWaiterData;
    WaiterData waiterData;
    List<TableData> lstTableData;
    TableData tableData;
    RpBestWaiterListAdapter rpBestWaiterListAdapter;
    RpWaiterPerformanceTableListAdapter rpWaiterPerformanceTableListAdapter;
    private ProgressDialog progDialog;
    TextView tvHeaderWaiterName,tvHeaderSaledTableCount,tvHeaderSaledCustomerNumber,tvHeaderWaiterName2,tvHeaderTableName,tvHeaderTableCount,tvFromDate,tvToDate,tvToday;
    ListView lvReportWaiterPerformance,lvReportWaiterPerformance2;
    String fromDate,toDate;
    private Calendar cCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_waiter_performance);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        Intent i=getIntent();
        fromDate=i.getStringExtra("from_date");
        toDate=i.getStringExtra("to_date");

        db = new DBHelper(this);
        setTitle("Waiter Performance Report");
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
        tvHeaderWaiterName = (TextView) findViewById(R.id.tvHeaderWaiterName);
        tvHeaderSaledTableCount = (TextView) findViewById(R.id.tvHeaderSaledTableCount);
        tvHeaderSaledCustomerNumber = (TextView) findViewById(R.id.tvHeaderSaledCustomerNumber);
        tvHeaderWaiterName2 = (TextView) findViewById(R.id.tvHeaderWaiterName2);
        tvHeaderTableName = (TextView) findViewById(R.id.tvHeaderTableName);
        tvHeaderTableCount = (TextView) findViewById(R.id.tvHeaderTableCount);
        lvReportWaiterPerformance = (ListView) findViewById(R.id.lvReportWaiterPerformance);
        lvReportWaiterPerformance2 = (ListView) findViewById(R.id.lvReportWaiterPerformance2);

        tvFromDate = (TextView) findViewById(R.id.tvFromDate);
        tvToDate = (TextView) findViewById(R.id.tvToDate);
        tvFromDate.setText("From Date: "+fromDate);
        tvToDate.setText("To Date: "+toDate);
        tvToday = (TextView) findViewById(R.id.tvToday);
        cCalendar= Calendar.getInstance();
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
        lstWaiterData =new ArrayList<>();
        Cursor cur=db.getReportWaiterPerformance(fromDate,toDate);
        while(cur.moveToNext()) {
            waiterData = new WaiterData();
            waiterData.setWaiterid(cur.getInt(0));
            waiterData.setWaiterName(cur.getString(1));
            waiterData.setSaledTableCount(cur.getInt(2));
            waiterData.setSaledCustomerNumber(cur.getInt(3));
            lstWaiterData.add(waiterData);
        }
        setData();
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
                    String query_waiter_performance="exec PrcReportAllWaiterPerformance '"+fromDate+"','"+toDate+"'";
                    Statement st_waiter_performance=con.createStatement();
                    ResultSet rs_waiter_performance= st_waiter_performance.executeQuery(query_waiter_performance);
                    lstWaiterData =new ArrayList<>();
                    while(rs_waiter_performance.next()) {
                        waiterData = new WaiterData();
                        waiterData.setWaiterID(rs_waiter_performance.getInt("WaiterID"));
                        waiterData.setWaiterName(rs_waiter_performance.getString("WaiterName"));
                        waiterData.setSaledTableCount(rs_waiter_performance.getInt("SaledTableCount"));
                        waiterData.setSaledCustomerNumber(rs_waiter_performance.getInt("CustomerNumber"));
                        lstWaiterData.add(waiterData);
                    }

                    String query_waiter_saled_table="exec PrcReportAllWaiterSaledTable '"+fromDate+"','"+toDate+"'";
                    Statement st_waiter_saled_table=con.createStatement();
                    ResultSet rs_waiter_saled_table= st_waiter_saled_table.executeQuery(query_waiter_saled_table);
                    lstTableData =new ArrayList<>();
                    while(rs_waiter_saled_table.next()) {
                        tableData = new TableData();
                        tableData.setWaiterID(rs_waiter_saled_table.getInt("WaiterID"));
                        tableData.setWaiterName(rs_waiter_saled_table.getString("WaiterName"));
                        tableData.setTableID(rs_waiter_saled_table.getInt("TableID"));
                        tableData.setTableName(rs_waiter_saled_table.getString("TableName"));
                        tableData.setWaiterSaledTableCount(rs_waiter_saled_table.getInt("Count"));
                        lstTableData.add(tableData);
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

    private void setData() {
        int totalHeight, adapterCount, size;

        rpBestWaiterListAdapter = new RpBestWaiterListAdapter(this, lstWaiterData);
        totalHeight = 0;
        adapterCount = rpBestWaiterListAdapter.getCount();
        for (size = 0; size < adapterCount; size++) {
            View listItem = rpBestWaiterListAdapter.getView(size, null, lvReportWaiterPerformance);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lvReportWaiterPerformance.getLayoutParams();
        params.height = totalHeight + (lvReportWaiterPerformance.getDividerHeight() * (adapterCount - 1)) + 30;
        lvReportWaiterPerformance.setLayoutParams(params);
        lvReportWaiterPerformance.setAdapter(rpBestWaiterListAdapter);

        /**rpWaiterPerformanceTableListAdapter = new RpWaiterPerformanceTableListAdapter(this, lstTableData);
        totalHeight = 0;
        adapterCount = rpWaiterPerformanceTableListAdapter.getCount();
        for (size = 0; size < adapterCount; size++) {
            View listItem = rpWaiterPerformanceTableListAdapter.getView(size, null, lvReportWaiterPerformance2);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params1 = lvReportWaiterPerformance2.getLayoutParams();
        params1.height = totalHeight + (lvReportWaiterPerformance2.getDividerHeight() * (adapterCount - 1)) + 30;
        lvReportWaiterPerformance2.setLayoutParams(params1);
        lvReportWaiterPerformance2.setAdapter(rpWaiterPerformanceTableListAdapter);**/
    }
}
