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

import adapter.RpSaleByTableListAdapter;
import common.SystemSetting;
import data.RpSaleByTableData;
import common.DBHelper;

/**
 * Created by NweYiAung on 23-12-2016.
 */
public class RpSaleByTableActivity extends AppCompatActivity {

    DBHelper db;
    RpSaleByTableData rpSaleByTableData;
    List<RpSaleByTableData> lstRpSaleByTableData;
    RpSaleByTableListAdapter rpSaleByTableListAdapter;
    private ProgressDialog progDialog;
    TextView tvHeaderDate,tvHeaderWaiter, tvHeaderGrandTotal,tvFromDate,tvToDate,tvToday,tvHeaderCustomer;
    ListView lvReportSaleByTable;
    String fromDate,toDate;
    ArrayList<Integer> lstCheckedTable,lstAllTable=new ArrayList<>();
    private Calendar cCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_sale_by_table);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        Intent i=getIntent();
        fromDate=i.getStringExtra("from_date");
        toDate=i.getStringExtra("to_date");
        lstCheckedTable=i.getIntegerArrayListExtra("checked_table");
        lstAllTable=i.getIntegerArrayListExtra("all_table");

        db = new DBHelper(this);
        setTitle("Sale By Table Report");
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
        tvHeaderWaiter = (TextView) findViewById(R.id.tvHeaderWaiter);
        tvHeaderGrandTotal = (TextView) findViewById(R.id.tvHeaderGrandTotal);
        lvReportSaleByTable =(ListView) findViewById(R.id.lvReportSaleByTable);
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
        String tableList="";
        if(lstCheckedTable.size()!=0) {
            for (int i = 0; i < lstCheckedTable.size(); i++) {
                tableList += lstCheckedTable.get(i) + ",";
            }
        }else{
            for (int i = 0; i < lstAllTable.size(); i++) {
                tableList += lstAllTable.get(i) + ",";
            }
        }
        if(tableList.length()!=0) {
            tableList = tableList.substring(0, tableList.length() - 1);
        }
        lstRpSaleByTableData =new ArrayList<>();
        List<Integer> lstTableTypeID=new ArrayList<>();
        List<Integer> lstTableID=new ArrayList<>();
        Cursor cur=db.getReportSaleByTable(fromDate,toDate,tableList);
        while(cur.moveToNext()){
            rpSaleByTableData =new RpSaleByTableData();
            lstTableTypeID.add(cur.getInt(6));
            lstTableID.add(cur.getInt(0));
            rpSaleByTableData.setTableTypeID(cur.getInt(6));
            rpSaleByTableData.setTableID(cur.getInt(0));
            int type_times= Collections.frequency(lstTableTypeID,cur.getInt(6));
            if(type_times == 1) {
                rpSaleByTableData.setTotalByTableType(cur.getDouble(8));
                rpSaleByTableData.setTableTypeName(cur.getString(7));
            }
            int times= Collections.frequency(lstTableID,cur.getInt(0));
            if(times == 1) {
                rpSaleByTableData.setTotalByTable(cur.getDouble(5));
                rpSaleByTableData.setTableName(cur.getString(1));
            }
            rpSaleByTableData.setDate(cur.getString(3));
            rpSaleByTableData.setWaiterName(cur.getString(2));
            rpSaleByTableData.setGrandTotal(cur.getDouble(4));
            rpSaleByTableData.setTotalCustomer(cur.getInt(9));
            lstRpSaleByTableData.add(rpSaleByTableData);
        }
        rpSaleByTableListAdapter =new RpSaleByTableListAdapter(this, lstRpSaleByTableData);
        lvReportSaleByTable.setAdapter(rpSaleByTableListAdapter);
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
                    String tableList="";
                    if(lstCheckedTable.size()!=0) {
                        for (int i = 0; i < lstCheckedTable.size(); i++) {
                            tableList += lstCheckedTable.get(i) + ",";
                        }
                    }else{
                        for (int i = 0; i < lstAllTable.size(); i++) {
                            tableList += lstAllTable.get(i) + ",";
                        }
                    }
                    if(tableList.length()!=0) {
                        tableList = tableList.substring(0, tableList.length() - 1);
                    }
                    String sql_query="exec PrcReportSaleByTable '"+fromDate+"','"+toDate+"','"+tableList+"'";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);
                    lstRpSaleByTableData =new ArrayList<>();
                    List<Integer> lstTableTypeID=new ArrayList<>();
                    List<Integer> lstTableID=new ArrayList<>();
                    while(rs.next()){
                        rpSaleByTableData =new RpSaleByTableData();
                        lstTableTypeID.add(rs.getInt("TableTypeID"));
                        lstTableID.add(rs.getInt("TableID"));
                        rpSaleByTableData.setTableTypeID(rs.getInt("TableTypeID"));
                        rpSaleByTableData.setTableID(rs.getInt("TableID"));
                        int type_times= Collections.frequency(lstTableTypeID,rs.getInt("TableTypeID"));
                        if(type_times == 1) {
                            rpSaleByTableData.setTotalByTableType(rs.getDouble("TotalByTableType"));
                            rpSaleByTableData.setTableTypeName(rs.getString("TableTypeName"));
                        }
                        int times= Collections.frequency(lstTableID,rs.getInt("TableID"));
                        if(times == 1) {
                            rpSaleByTableData.setTotalByTable(rs.getDouble("TotalByTable"));
                            rpSaleByTableData.setTableName(rs.getString("TableName"));
                        }
                        rpSaleByTableData.setDate(rs.getString("Date"));
                        rpSaleByTableData.setWaiterName(rs.getString("WaiterName"));
                        rpSaleByTableData.setGrandTotal(rs.getDouble("GrandTotal"));
                        rpSaleByTableData.setTotalCustomer(rs.getInt("TotalCustomer"));
                        lstRpSaleByTableData.add(rpSaleByTableData);
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
        rpSaleByTableListAdapter =new RpSaleByTableListAdapter(this, lstRpSaleByTableData);
        lvReportSaleByTable.setAdapter(rpSaleByTableListAdapter);
    }
}
