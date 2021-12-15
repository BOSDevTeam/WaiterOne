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
import java.util.List;

import adapter.RpTopBottomSaleItemListAdapter;
import common.SystemSetting;
import data.RpSummaryItemData;
import common.DBHelper;

public class RpBottomSaleItemActivity extends AppCompatActivity {

    DBHelper db;
    RpSummaryItemData rpSummaryItemData;
    List<RpSummaryItemData> lstRpSummaryItemData;
    RpTopBottomSaleItemListAdapter reportBottomSaleItemListAdapter;
    private ProgressDialog progDialog;
    TextView tvHeaderItem,tvHeaderMenu,tvHeaderQty,tvFromDate,tvToDate,tvToday;
    ListView lvReportBottomSaleItem;
    String fromDate,toDate,title;
    int bottomnumber,rankTypeID;
    private Calendar cCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_bottom_sale_item);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        Intent i=getIntent();
        fromDate=i.getStringExtra("from_date");
        toDate=i.getStringExtra("to_date");
        bottomnumber =i.getIntExtra("bottom_number",10);
        rankTypeID=i.getIntExtra("rank_type",1);

        db = new DBHelper(this);
        progDialog=new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(false);
        title="Bottom "+ bottomnumber +" Sale Item";
        setTitle(title);
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
        tvHeaderItem = (TextView) findViewById(R.id.tvHeaderItem);
        tvHeaderMenu = (TextView) findViewById(R.id.tvHeaderMenu);
        tvHeaderQty = (TextView) findViewById(R.id.tvHeaderQty);
        lvReportBottomSaleItem = (ListView) findViewById(R.id.lvReportBottomSaleItem);

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
    }

    private void getData(){
        lstRpSummaryItemData =new ArrayList<>();
        Cursor cur=db.getReportBottomSaleItem(fromDate,toDate,bottomnumber,rankTypeID);
        while(cur.moveToNext()){
            rpSummaryItemData =new RpSummaryItemData();
            rpSummaryItemData.setName(cur.getString(0));
            rpSummaryItemData.setMenu(cur.getString(1));
            rpSummaryItemData.setQty(cur.getInt(2));
            lstRpSummaryItemData.add(rpSummaryItemData);
        }
        reportBottomSaleItemListAdapter =new RpTopBottomSaleItemListAdapter(this, lstRpSummaryItemData);
        lvReportBottomSaleItem.setAdapter(reportBottomSaleItemListAdapter);
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
                    String sql_query="exec PrcReportBottomSaleItem '"+fromDate+"','"+toDate+"',"+ bottomnumber+","+rankTypeID;
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);
                    lstRpSummaryItemData =new ArrayList<>();
                    while(rs.next()){
                        rpSummaryItemData =new RpSummaryItemData();
                        rpSummaryItemData.setName(rs.getString("Item"));
                        rpSummaryItemData.setMenu(rs.getString("Menu"));
                        rpSummaryItemData.setQty(rs.getInt("Qty"));
                        lstRpSummaryItemData.add(rpSummaryItemData);
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
        reportBottomSaleItemListAdapter =new RpTopBottomSaleItemListAdapter(this, lstRpSummaryItemData);
        lvReportBottomSaleItem.setAdapter(reportBottomSaleItemListAdapter);
    }
}
