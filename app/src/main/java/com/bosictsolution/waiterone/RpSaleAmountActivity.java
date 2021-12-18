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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import common.DBHelper;
import common.SystemSetting;

/**
 * Created by NweYiAung on 23-12-2016.
 */
public class RpSaleAmountActivity extends AppCompatActivity {

    DBHelper db;
    private ProgressDialog progDialog;
    TextView tvLabelDescription,tvLabelData,tvLabelSubTotal,tvLabelGrandTotal,tvLabelTax,tvLabelCharges,tvLabelDiscount,tvSubTotal,tvGrandTotal,tvTax,tvCharges,tvDiscount,tvFromDate,tvToDate,tvToday;
    double subTotal,grandTotal,charges,tax,discount;
    String fromDate,toDate;
    private Calendar cCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_sale_amount);

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
        setTitle("Sale Amount Only Report");
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
        tvLabelDescription = (TextView) findViewById(R.id.tvLabelDescription);
        tvLabelSubTotal = (TextView) findViewById(R.id.tvLabelSubTotal);
        tvLabelGrandTotal = (TextView) findViewById(R.id.tvLabelGrandTotal);
        tvLabelTax = (TextView) findViewById(R.id.tvLabelTax);
        tvLabelCharges = (TextView) findViewById(R.id.tvLabelCharges);
        tvLabelDiscount = (TextView) findViewById(R.id.tvLabelDiscount);
        tvLabelData = (TextView) findViewById(R.id.tvLabelData);
        tvSubTotal = (TextView) findViewById(R.id.tvSubTotal);
        tvGrandTotal = (TextView) findViewById(R.id.tvGrandTotal);
        tvTax = (TextView) findViewById(R.id.tvTax);
        tvCharges = (TextView) findViewById(R.id.tvCharges);
        tvDiscount = (TextView) findViewById(R.id.tvDiscount);

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
        Cursor cur=db.getReportSaleAmountOnly(fromDate,toDate);
        if(cur.moveToFirst()){
            subTotal=cur.getDouble(0);
            tax=cur.getDouble(1);
            charges=cur.getDouble(2);
            grandTotal=cur.getDouble(3);
            discount=cur.getDouble(4);
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
                    String sql_query="exec PrcReportSaleAmount '"+fromDate+"','"+toDate+"'";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);

                    if(rs.next()){
                        subTotal=rs.getDouble("Subtotal");
                        grandTotal=rs.getDouble("GrandTotal");
                        tax=rs.getDouble("Tax");
                        charges=rs.getDouble("Charges");
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
        tvSubTotal.setText(String.valueOf(subTotal));
        tvGrandTotal.setText(String.valueOf(grandTotal));
        tvTax.setText(String.valueOf(tax));
        tvCharges.setText(String.valueOf(charges));
        tvDiscount.setText(String.valueOf(discount));
    }
}
