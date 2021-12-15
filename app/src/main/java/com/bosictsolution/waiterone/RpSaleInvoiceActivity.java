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

import adapter.RpSaleInvoiceListAdapter;
import common.SystemSetting;
import data.RpSaleInvoiceData;
import common.DBHelper;

public class RpSaleInvoiceActivity extends AppCompatActivity {

    DBHelper db;
    RpSaleInvoiceData rpSaleInvoiceData;
    List<RpSaleInvoiceData> lstRpSaleInvoiceData;
    RpSaleInvoiceListAdapter rpSaleInvoiceListAdapter;
    private ProgressDialog progDialog;
    TextView tvHeaderItemName, tvHeaderQty,tvHeaderPrice, tvHeaderAmount,tvFromDate,tvToDate,tvToday;
    ListView lvReportSaleInvoice;
    String fromDate,toDate;
    private Calendar cCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_sale_invoice);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        Intent i=getIntent();
        fromDate=i.getStringExtra("from_date");
        toDate=i.getStringExtra("to_date");

        db = new DBHelper(this);
        setTitle("Sale Invoice Report");
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
        tvHeaderItemName = (TextView) findViewById(R.id.tvHeaderItemName);
        tvHeaderQty = (TextView) findViewById(R.id.tvHeaderQuantity);
        tvHeaderPrice = (TextView) findViewById(R.id.tvHeaderSalePrice);
        tvHeaderAmount = (TextView) findViewById(R.id.tvHeaderSaleAmount);
        lvReportSaleInvoice =(ListView) findViewById(R.id.lvReportSaleInvoice);

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
        int size,curTranID,nextTranID;
        List<Integer> lstTempTranID=new ArrayList<>();
        List<Integer> lstTranID=new ArrayList<>();
        lstRpSaleInvoiceData =new ArrayList<>();
        Cursor cur=db.getReportSaleInvoice(fromDate,toDate);
        while(cur.moveToNext()){
            lstTranID.add(cur.getInt(0));
        }
        Cursor cur2=db.getReportSaleInvoice(fromDate,toDate);
        while(cur2.moveToNext()){
            rpSaleInvoiceData =new RpSaleInvoiceData();
            lstTempTranID.add(cur2.getInt(0));
            int times= Collections.frequency(lstTempTranID,cur2.getInt(0));
            if(times == 1) {
                rpSaleInvoiceData.setVoucher(cur2.getString(1));
                rpSaleInvoiceData.setDate(cur2.getString(2));
                rpSaleInvoiceData.setWaiter(cur2.getString(3));
                rpSaleInvoiceData.setTable(cur2.getString(4));
            }else{
                rpSaleInvoiceData.setVoucher("");
                rpSaleInvoiceData.setDate("");
                rpSaleInvoiceData.setWaiter("");
                rpSaleInvoiceData.setTable("");
            }

            size=lstTempTranID.size();
            curTranID=lstTempTranID.get(size-1);
            if(size<lstTranID.size()) {
                nextTranID = lstTranID.get(size);
                if (curTranID != nextTranID) {
                    rpSaleInvoiceData.setSubtotal(cur2.getDouble(5));
                    rpSaleInvoiceData.setTax(cur2.getDouble(6));
                    rpSaleInvoiceData.setCharges(cur2.getDouble(7));
                    rpSaleInvoiceData.setGrandtotal(cur2.getDouble(8));
                    rpSaleInvoiceData.setDiscount(cur2.getDouble(13));
                } else {
                    rpSaleInvoiceData.setSubtotal(1);
                    rpSaleInvoiceData.setTax(1);
                    rpSaleInvoiceData.setCharges(1);
                    rpSaleInvoiceData.setGrandtotal(1);
                    rpSaleInvoiceData.setDiscount(1);
                }
            }else{
                rpSaleInvoiceData.setSubtotal(cur2.getDouble(5));
                rpSaleInvoiceData.setTax(cur2.getDouble(6));
                rpSaleInvoiceData.setCharges(cur2.getDouble(7));
                rpSaleInvoiceData.setGrandtotal(cur2.getDouble(8));
                rpSaleInvoiceData.setDiscount(cur2.getDouble(13));
            }

            rpSaleInvoiceData.setItemName(cur2.getString(9));
            rpSaleInvoiceData.setQty(cur2.getInt(10));
            rpSaleInvoiceData.setPrice(cur2.getDouble(11));
            rpSaleInvoiceData.setAmount(cur2.getDouble(12));
            lstRpSaleInvoiceData.add(rpSaleInvoiceData);
        }
        rpSaleInvoiceListAdapter =new RpSaleInvoiceListAdapter(this, lstRpSaleInvoiceData);
        lvReportSaleInvoice.setAdapter(rpSaleInvoiceListAdapter);
    }
}
