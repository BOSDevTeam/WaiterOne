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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.RpBestWaiterListAdapter;
import adapter.RpChooseTableListAdapter;
import common.SystemSetting;
import data.TableData;
import data.WaiterData;
import common.DBHelper;

/**
 * Created by NweYiAung on 23-12-2016.
 */
public class RpDataAnalysisActivity extends AppCompatActivity {

    DBHelper db;
    List<WaiterData> lstBestWaiterData;
    WaiterData waiterData;
    List<TableData> lstCustomerChoosedTableData,lstMenChoosedTableData,lstWomenChoosedTableData,lstChildChoosedTableData;
    TableData tableData;
    RpBestWaiterListAdapter rpBestWaiterListAdapter;
    RpChooseTableListAdapter rpChooseTableListAdapter;
    private ProgressDialog progDialog;
    TextView tvHeaderBestWaiter,tvHeaderSaledTableCount,tvHeaderSaledCustomerNumber,tvLabelTodaySaledTableCount,tvLabelTodayCustomerTotal,
             tvLabelTodayMenTotal,tvLabelTodayWomenTotal,tvLabelTodayChildTotal,tvTodaySaledTableCount,tvTodayCustomerTotal,tvTodayMenTotal,
             tvTodayWomenTotal,tvTodayChildTotal,tvLabelCustomerChoosedTable,tvLabelMenChoosedTable,tvLabelWomenChoosedTable,tvLabelChildChoosedTable,
             tvFromDate,tvToDate,tvToday;
    ListView lvReportBestWaiter,lvReportCustomerChoosedTable,lvReportMenChoosedTable,lvReportWomenChoosedTable,lvReportChildChoosedTable;
    Button btnDetail;
    String fromDate,toDate;
    int todaySaledTableCount,todayCustomerNumber,todayMenNumber,todayWomenNumber,todayChildNumber;
    private Calendar cCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_data_analysis);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        Intent i=getIntent();
        fromDate=i.getStringExtra("from_date");
        toDate=i.getStringExtra("to_date");

        db = new DBHelper(this);
        setTitle("Data Analysis Report");
        setLayoutResource();
        getData();

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),RpWaiterPerformanceActivity.class);
                i.putExtra("from_date",fromDate);
                i.putExtra("to_date",toDate);
                startActivity(i);
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
    public void setTitle(CharSequence title) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.actionbar_label, null);
        TextView tvActionBarText = (TextView) vi.findViewById(R.id.tvActionBarText);
        tvActionBarText.setText(title);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setCustomView(vi, params);
    }

    private void setLayoutResource() {
        tvHeaderBestWaiter = (TextView) findViewById(R.id.tvHeaderBestWaiter);
        tvHeaderSaledTableCount = (TextView) findViewById(R.id.tvHeaderSaledTableCount);
        tvHeaderSaledCustomerNumber = (TextView) findViewById(R.id.tvHeaderSaledCustomerNumber);
        tvLabelTodaySaledTableCount = (TextView) findViewById(R.id.tvLabelTodaySaledTableCount);
        tvLabelTodayCustomerTotal = (TextView) findViewById(R.id.tvLabelTodayCustomerTotal);
        tvLabelTodayMenTotal = (TextView) findViewById(R.id.tvLabelTodayMenTotal);
        tvLabelTodayWomenTotal = (TextView) findViewById(R.id.tvLabelTodayWomenTotal);
        tvLabelTodayChildTotal = (TextView) findViewById(R.id.tvLabelTodayChildTotal);
        tvTodaySaledTableCount = (TextView) findViewById(R.id.tvTodaySaledTableCount);
        tvTodayCustomerTotal = (TextView) findViewById(R.id.tvTodayCustomerTotal);
        tvTodayMenTotal = (TextView) findViewById(R.id.tvTodayMenTotal);
        tvTodayWomenTotal = (TextView) findViewById(R.id.tvTodayWomenTotal);
        tvTodayChildTotal = (TextView) findViewById(R.id.tvTodayChildTotal);
        tvLabelCustomerChoosedTable = (TextView) findViewById(R.id.tvLabelCustomerChoosedTable);
        tvLabelMenChoosedTable = (TextView) findViewById(R.id.tvLabelMenChoosedTable);
        tvLabelWomenChoosedTable = (TextView) findViewById(R.id.tvLabelWomenChoosedTable);
        tvLabelChildChoosedTable = (TextView) findViewById(R.id.tvLabelChildChoosedTable);
        lvReportBestWaiter = (ListView) findViewById(R.id.lvReportBestWaiter);
        lvReportCustomerChoosedTable = (ListView) findViewById(R.id.lvReportCustomerChoosedTable);
        lvReportMenChoosedTable = (ListView) findViewById(R.id.lvReportMenChoosedTable);
        lvReportWomenChoosedTable = (ListView) findViewById(R.id.lvReportWomenChoosedTable);
        lvReportChildChoosedTable = (ListView) findViewById(R.id.lvReportChildChoosedTable);
        btnDetail = (Button) findViewById(R.id.btnDetail);
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
        lstBestWaiterData =new ArrayList<>();
        Cursor cur=db.getReportBestWaiter(fromDate,toDate);
        while(cur.moveToNext()) {
            waiterData = new WaiterData();
            waiterData.setWaiterid(cur.getInt(0));
            waiterData.setWaiterName(cur.getString(1));
            waiterData.setSaledTableCount(cur.getInt(2));
            waiterData.setSaledCustomerNumber(cur.getInt(3));
            lstBestWaiterData.add(waiterData);
        }

        Cursor cur_customer=db.getReportTodayCustomerTotalAndSaleTableCount();
        if(cur_customer.moveToFirst()) {
            todaySaledTableCount=cur_customer.getInt(4);
            todayCustomerNumber=cur_customer.getInt(3);
            todayMenNumber=cur_customer.getInt(0);
            todayWomenNumber=cur_customer.getInt(1);
            todayChildNumber=cur_customer.getInt(2);
        }
        setData();
    }

    /**public class GetData extends AsyncTask<String,String,String> {
        String msg;
        boolean isSuccess;
        double subtotal,tax,charges;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverConnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                }else{
                    String query_best_waiter="exec PrcReportTheBestWaiter '"+fromDate+"','"+toDate+"'";
                    Statement st_best_waiter=con.createStatement();
                    ResultSet rs_best_waiter= st_best_waiter.executeQuery(query_best_waiter);
                    lstBestWaiterData =new ArrayList<>();
                    while(rs_best_waiter.next()) {
                        waiterData = new WaiterData();
                        waiterData.setWaiterID(rs_best_waiter.getInt("WaiterID"));
                        waiterData.setWaiterName(rs_best_waiter.getString("WaiterName"));
                        waiterData.setSaledTableCount(rs_best_waiter.getInt("TableNumber"));
                        waiterData.setSaledCustomerNumber(rs_best_waiter.getInt("CustomerNumber"));
                        lstBestWaiterData.add(waiterData);
                    }

                    String query_cus_choosed_table="exec PrcReportMostCustomerLikedTable";
                    Statement st_cus_table=con.createStatement();
                    ResultSet rs_cus_table= st_cus_table.executeQuery(query_cus_choosed_table);
                    lstCustomerChoosedTableData =new ArrayList<>();
                    while(rs_cus_table.next()) {
                        tableData = new TableData();
                        tableData.setTableID(rs_cus_table.getInt("TableID"));
                        tableData.setTableName(rs_cus_table.getString("TableName"));
                        lstCustomerChoosedTableData.add(tableData);
                    }

                    String query_men_choosed_table="exec PrcReportMostMaleLikedTable";
                    Statement st_men_table=con.createStatement();
                    ResultSet rs_men_table= st_men_table.executeQuery(query_men_choosed_table);
                    lstMenChoosedTableData =new ArrayList<>();
                    while(rs_men_table.next()) {
                        tableData = new TableData();
                        tableData.setTableID(rs_men_table.getInt("TableID"));
                        tableData.setTableName(rs_men_table.getString("TableName"));
                        lstMenChoosedTableData.add(tableData);
                    }

                    String query_women_choosed_table="exec PrcReportMostFemaleLikedTable";
                    Statement st_women_table=con.createStatement();
                    ResultSet rs_women_table= st_women_table.executeQuery(query_women_choosed_table);
                    lstWomenChoosedTableData =new ArrayList<>();
                    while(rs_women_table.next()) {
                        tableData = new TableData();
                        tableData.setTableID(rs_women_table.getInt("TableID"));
                        tableData.setTableName(rs_women_table.getString("TableName"));
                        lstWomenChoosedTableData.add(tableData);
                    }

                    String query_child_choosed_table="exec PrcReportMostChildLikedTable";
                    Statement st_child_table=con.createStatement();
                    ResultSet rs_child_table= st_child_table.executeQuery(query_child_choosed_table);
                    lstChildChoosedTableData =new ArrayList<>();
                    while(rs_child_table.next()) {
                        tableData = new TableData();
                        tableData.setTableID(rs_child_table.getInt("TableID"));
                        tableData.setTableName(rs_child_table.getString("TableName"));
                        lstChildChoosedTableData.add(tableData);
                    }

                    String query_today_cus_table_count="exec PrcReportTodayCustomerTotalAndSaledTableCount";
                    Statement st_today_count=con.createStatement();
                    ResultSet rs_today_count= st_today_count.executeQuery(query_today_cus_table_count);
                    if(rs_today_count.next()) {
                        todaySaledTableCount=rs_today_count.getInt("TodaySaledTableCount");
                        todayCustomerNumber=rs_today_count.getInt("TodayCustomerTotal");
                        todayMenNumber=rs_today_count.getInt("TodayMenTotal");
                        todayWomenNumber=rs_today_count.getInt("TodayFemaleTotal");
                        todayChildNumber=rs_today_count.getInt("TodayChildTotal");
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
        int totalHeight,adapterCount,size;
        tvTodaySaledTableCount.setText(String.valueOf(todaySaledTableCount));
        tvTodayCustomerTotal.setText(String.valueOf(todayCustomerNumber));
        tvTodayMenTotal.setText(String.valueOf(todayMenNumber));
        tvTodayWomenTotal.setText(String.valueOf(todayWomenNumber));
        tvTodayChildTotal.setText(String.valueOf(todayChildNumber));

        rpBestWaiterListAdapter =new RpBestWaiterListAdapter(this, lstBestWaiterData);
        totalHeight=0;
        adapterCount= rpBestWaiterListAdapter.getCount();
        for(size=0;size<adapterCount;size++){
            View listItem= rpBestWaiterListAdapter.getView(size, null, lvReportBestWaiter);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params=lvReportBestWaiter.getLayoutParams();
        params.height=totalHeight+(lvReportBestWaiter.getDividerHeight()*(adapterCount-1))+30;
        lvReportBestWaiter.setLayoutParams(params);
        lvReportBestWaiter.setAdapter(rpBestWaiterListAdapter);

        /**rpChooseTableListAdapter=new RpChooseTableListAdapter(this,lstCustomerChoosedTableData);
        totalHeight=0;
        adapterCount=rpChooseTableListAdapter.getCount();
        for(size=0;size<adapterCount;size++){
            View listItem=rpChooseTableListAdapter.getView(size, null, lvReportCustomerChoosedTable);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params1=lvReportCustomerChoosedTable.getLayoutParams();
        params1.height=totalHeight+(lvReportCustomerChoosedTable.getDividerHeight()*(adapterCount-1))+30;
        lvReportCustomerChoosedTable.setLayoutParams(params1);
        lvReportCustomerChoosedTable.setAdapter(rpChooseTableListAdapter);

        rpChooseTableListAdapter=new RpChooseTableListAdapter(this,lstMenChoosedTableData);
        totalHeight=0;
        adapterCount=rpChooseTableListAdapter.getCount();
        for(size=0;size<adapterCount;size++){
            View listItem=rpChooseTableListAdapter.getView(size, null, lvReportMenChoosedTable);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params2=lvReportMenChoosedTable.getLayoutParams();
        params2.height=totalHeight+(lvReportMenChoosedTable.getDividerHeight()*(adapterCount-1))+30;
        lvReportMenChoosedTable.setLayoutParams(params2);
        lvReportMenChoosedTable.setAdapter(rpChooseTableListAdapter);

        rpChooseTableListAdapter=new RpChooseTableListAdapter(this,lstWomenChoosedTableData);
        totalHeight=0;
        adapterCount=rpChooseTableListAdapter.getCount();
        for(size=0;size<adapterCount;size++){
            View listItem=rpChooseTableListAdapter.getView(size, null, lvReportWomenChoosedTable);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params3=lvReportWomenChoosedTable.getLayoutParams();
        params3.height=totalHeight+(lvReportWomenChoosedTable.getDividerHeight()*(adapterCount-1))+30;
        lvReportWomenChoosedTable.setLayoutParams(params3);
        lvReportWomenChoosedTable.setAdapter(rpChooseTableListAdapter);

        rpChooseTableListAdapter=new RpChooseTableListAdapter(this,lstChildChoosedTableData);
        totalHeight=0;
        adapterCount=rpChooseTableListAdapter.getCount();
        for(size=0;size<adapterCount;size++){
            View listItem=rpChooseTableListAdapter.getView(size, null, lvReportChildChoosedTable);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params4=lvReportChildChoosedTable.getLayoutParams();
        params4.height=totalHeight+(lvReportChildChoosedTable.getDividerHeight()*(adapterCount-1))+30;
        lvReportChildChoosedTable.setLayoutParams(params4);
        lvReportChildChoosedTable.setAdapter(rpChooseTableListAdapter);**/
    }
}
