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

import adapter.RpDetailedSaleListAdapter;
import common.SystemSetting;
import data.RpDetailedSaleData;
import common.DBHelper;

/**
 * Created by NweYiAung on 23-12-2016.
 */
public class RpDetailedSaleActivity extends AppCompatActivity {

    DBHelper db;
    RpDetailedSaleData rpDetailedSaleData;
    List<RpDetailedSaleData> lstRpDetailedSaleData;
    RpDetailedSaleListAdapter rpDetailedSaleListAdapter;
    private ProgressDialog progDialog;
    TextView tvHeaderDate,tvHeaderWaiter,tvHeaderTable,tvHeaderCustomerNumber,tvHeaderSubTotal,tvHeaderTax,tvHeaderCharges,tvHeaderGrandTotal,
            tvTotalSubTotal,tvTotalTax,tvTotalCharges,tvTotalGrandTotal,tvTotalLabelSubTotal,tvTotalLabelTax,tvTotalLabelCharges,tvTotalLabelGrandTotal,tvFromDate,tvToDate,tvToday,
            tvNumberOFSaledTable,tvTotalCustomer,tvTotalLabelDiscount,tvTotalDiscount,tvHeaderDiscount;
    ListView lvReportDetailedSale;
    double totalSubtotal,totalTax,totalCharges,totalGrandtotal,totalDiscount;
    String fromDate,toDate;
    private Calendar cCalendar;
    int totalCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_detailed_sale);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        Intent i=getIntent();
        fromDate=i.getStringExtra("from_date");
        toDate=i.getStringExtra("to_date");

        db = new DBHelper(this);
        progDialog=new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(false);
        setTitle("Detailed Sale Report");
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
        tvHeaderTable = (TextView) findViewById(R.id.tvHeaderTable);
        tvHeaderCustomerNumber = (TextView) findViewById(R.id.tvHeaderCustomerNumber);
        tvHeaderSubTotal = (TextView) findViewById(R.id.tvHeaderSubTotal);
        tvHeaderTax = (TextView) findViewById(R.id.tvHeaderTax);
        tvHeaderCharges = (TextView) findViewById(R.id.tvHeaderCharges);
        tvHeaderGrandTotal = (TextView) findViewById(R.id.tvHeaderGrandTotal);
        tvHeaderDiscount = (TextView) findViewById(R.id.tvHeaderDiscount);
        lvReportDetailedSale = (ListView) findViewById(R.id.lvReportDetailedSale);
        tvTotalSubTotal = (TextView) findViewById(R.id.tvTotalSubTotal);
        tvTotalTax = (TextView) findViewById(R.id.tvTotalTax);
        tvTotalDiscount = (TextView) findViewById(R.id.tvTotalDiscount);
        tvTotalCharges = (TextView) findViewById(R.id.tvTotalCharges);
        tvTotalGrandTotal = (TextView) findViewById(R.id.tvTotalGrandTotal);
        tvTotalLabelSubTotal = (TextView) findViewById(R.id.tvTotalLabelSubTotal);
        tvTotalLabelDiscount = (TextView) findViewById(R.id.tvTotalLabelDiscount);
        tvTotalLabelTax = (TextView) findViewById(R.id.tvTotalLabelTax);
        tvTotalLabelCharges = (TextView) findViewById(R.id.tvTotalLabelCharges);
        tvTotalLabelGrandTotal = (TextView) findViewById(R.id.tvTotalLabelGrandTotal);
        tvNumberOFSaledTable = (TextView) findViewById(R.id.tvNumberOFSaledTable);
        tvTotalCustomer = (TextView) findViewById(R.id.tvTotalCustomer);

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
        lstRpDetailedSaleData =new ArrayList<>();
        Cursor cur=db.getReportDetailedSale(fromDate,toDate);
        while(cur.moveToNext()){
            rpDetailedSaleData =new RpDetailedSaleData();
            rpDetailedSaleData.setDate(cur.getString(0));
            rpDetailedSaleData.setWaiter(cur.getString(1));
            rpDetailedSaleData.setTable(cur.getString(2));
            rpDetailedSaleData.setCustomerNumber(cur.getInt(3));
            rpDetailedSaleData.setSubtotal(cur.getDouble(4));
            totalSubtotal+=cur.getDouble(4);
            rpDetailedSaleData.setTax(cur.getDouble(5));
            totalTax+=cur.getDouble(5);
            rpDetailedSaleData.setCharges(cur.getDouble(6));
            totalCharges+=cur.getDouble(6);
            rpDetailedSaleData.setGrandtotal(cur.getDouble(7));
            totalGrandtotal+=cur.getDouble(7);
            totalCustomer+=cur.getInt(3);
            totalDiscount+=cur.getDouble(8);
            rpDetailedSaleData.setDiscount(cur.getDouble(8));
            lstRpDetailedSaleData.add(rpDetailedSaleData);
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
                    String sql_query="exec PrcReportDetailedSale '"+fromDate+"','"+toDate+"'";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);
                    lstRpDetailedSaleData =new ArrayList<>();
                    while(rs.next()){
                        rpDetailedSaleData =new RpDetailedSaleData();
                        rpDetailedSaleData.setDate(rs.getString("Date"));
                        rpDetailedSaleData.setWaiter(rs.getString("Waiter"));
                        rpDetailedSaleData.setTable(rs.getString("Table"));
                        rpDetailedSaleData.setCustomerNumber(rs.getInt("CustomerNumber"));
                        rpDetailedSaleData.setSubtotal(rs.getDouble("SubTotal"));
                        subtotal=rs.getDouble("SubTotal");
                        rpDetailedSaleData.setTax(rs.getDouble("Tax"));
                        tax=rs.getDouble("Tax");
                        rpDetailedSaleData.setCharges(rs.getDouble("Charges"));
                        charges=rs.getDouble("Charges");
                        rpDetailedSaleData.setGrandtotal(subtotal+tax+charges);
                        totalSubtotal += rs.getDouble("SubTotal");
                        totalTax += rs.getDouble("Tax");
                        totalCharges += rs.getDouble("Charges");
                        totalGrandtotal += subtotal+tax+charges;
                        subtotal=tax=charges=0;
                        totalCustomer+=rs.getInt("CustomerNumber");
                        lstRpDetailedSaleData.add(rpDetailedSaleData);
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
        tvTotalSubTotal.setText(String.valueOf(totalSubtotal));
        tvTotalTax.setText(String.valueOf(totalTax));
        tvTotalCharges.setText(String.valueOf(totalCharges));
        tvTotalGrandTotal.setText(String.valueOf(totalGrandtotal));
        tvTotalDiscount.setText(String.valueOf(totalDiscount));
        tvNumberOFSaledTable.setText("Total Sale Table: "+String.valueOf(lstRpDetailedSaleData.size()));
        tvTotalCustomer.setText("Total Customer: "+String.valueOf(totalCustomer));
        rpDetailedSaleListAdapter =new RpDetailedSaleListAdapter(this, lstRpDetailedSaleData);
        lvReportDetailedSale.setAdapter(rpDetailedSaleListAdapter);
    }
}
