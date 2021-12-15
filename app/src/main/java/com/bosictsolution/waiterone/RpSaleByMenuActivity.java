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

import adapter.RpSaleByMenuListAdapter;
import common.SystemSetting;
import data.RpSaleByMenuData;
import common.DBHelper;

/**
 * Created by NweYiAung on 23-12-2016.
 */
public class RpSaleByMenuActivity extends AppCompatActivity {

    DBHelper db;
    RpSaleByMenuData rpSaleByMenuData;
    List<RpSaleByMenuData> lstRpSaleByMenuData;
    RpSaleByMenuListAdapter rpSaleByMenuListAdapter;
    private ProgressDialog progDialog;
    TextView tvHeaderItemName, tvHeaderQuantity, tvHeaderSaleAmount,tvFromDate,tvToDate,tvToday;
    ListView lvReportSaleByMenu;
    String fromDate,toDate;
    ArrayList<Integer> lstCheckedMenu, lstAllMenu =new ArrayList<>();
    private Calendar cCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_sale_by_menu);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        Intent i=getIntent();
        fromDate=i.getStringExtra("from_date");
        toDate=i.getStringExtra("to_date");
        lstCheckedMenu =i.getIntegerArrayListExtra("checked_menu");
        lstAllMenu =i.getIntegerArrayListExtra("all_menu");

        db = new DBHelper(this);
        setTitle("Sale By Menu Report");
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
        tvHeaderQuantity = (TextView) findViewById(R.id.tvHeaderQuantity);
        tvHeaderSaleAmount = (TextView) findViewById(R.id.tvHeaderSaleAmount);
        lvReportSaleByMenu =(ListView) findViewById(R.id.lvReportSaleByMenu);

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
        String menuList="";
        if(lstCheckedMenu.size()!=0) {
            for (int i = 0; i < lstCheckedMenu.size(); i++) {
                menuList += lstCheckedMenu.get(i) + ",";
            }
        }else{
            for (int i = 0; i < lstAllMenu.size(); i++) {
                menuList += lstAllMenu.get(i) + ",";
            }
        }
        if(menuList.length()!=0) {
            menuList = menuList.substring(0, menuList.length() - 1);
        }
        lstRpSaleByMenuData =new ArrayList<>();
        List<Integer> lstMainMenuID=new ArrayList<>();
        List<Integer> lstSubMenuID=new ArrayList<>();
        Cursor cur=db.getReportSaleByMenu(fromDate,toDate,menuList);
        while(cur.moveToNext()){
            rpSaleByMenuData =new RpSaleByMenuData();
            lstMainMenuID.add(cur.getInt(6));
            lstSubMenuID.add(cur.getInt(8));
            rpSaleByMenuData.setMainMenuID(cur.getInt(6));
            rpSaleByMenuData.setSubMenuID(cur.getInt(8));
            int type_times= Collections.frequency(lstMainMenuID,cur.getInt(6));
            if(type_times == 1) {
                rpSaleByMenuData.setTotalByMainMenu(cur.getDouble(7));
                rpSaleByMenuData.setMainMenuName(cur.getString(3));
            }
            int times= Collections.frequency(lstSubMenuID,cur.getInt(8));
            if(times == 1) {
                rpSaleByMenuData.setTotalBySubMenu(cur.getDouble(9));
                rpSaleByMenuData.setSubMenuName(cur.getString(2));
            }
            rpSaleByMenuData.setItemName(cur.getString(1));
            rpSaleByMenuData.setQuantity(cur.getInt(4));
            rpSaleByMenuData.setSaleAmount(cur.getDouble(5));
            lstRpSaleByMenuData.add(rpSaleByMenuData);
        }
        rpSaleByMenuListAdapter =new RpSaleByMenuListAdapter(this, lstRpSaleByMenuData);
        lvReportSaleByMenu.setAdapter(rpSaleByMenuListAdapter);
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
                    String menuList="";
                    if(lstCheckedMenu.size()!=0) {
                        for (int i = 0; i < lstCheckedMenu.size(); i++) {
                            menuList += lstCheckedMenu.get(i) + ",";
                        }
                    }else{
                        for (int i = 0; i < lstAllMenu.size(); i++) {
                            menuList += lstAllMenu.get(i) + ",";
                        }
                    }
                    if(menuList.length()!=0) {
                        menuList = menuList.substring(0, menuList.length() - 1);
                    }
                    String sql_query="exec PrcReportSaleByMenu '"+fromDate+"','"+toDate+"','"+menuList+"'";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);
                    lstRpSaleByMenuData =new ArrayList<>();
                    List<Integer> lstMainMenuID=new ArrayList<>();
                    List<Integer> lstSubMenuID=new ArrayList<>();
                    while(rs.next()){
                        rpSaleByMenuData =new RpSaleByMenuData();
                        lstMainMenuID.add(rs.getInt("MainMenuID"));
                        lstSubMenuID.add(rs.getInt("SubMenuID"));
                        rpSaleByMenuData.setMainMenuID(rs.getInt("MainMenuID"));
                        rpSaleByMenuData.setSubMenuID(rs.getInt("SubMenuID"));
                        int type_times= Collections.frequency(lstMainMenuID,rs.getInt("MainMenuID"));
                        if(type_times == 1) {
                            rpSaleByMenuData.setTotalByMainMenu(rs.getDouble("TotalByMainMenu"));
                            rpSaleByMenuData.setMainMenuName(rs.getString("MainMenuName"));
                        }
                        int times= Collections.frequency(lstSubMenuID,rs.getInt("SubMenuID"));
                        if(times == 1) {
                            rpSaleByMenuData.setTotalBySubMenu(rs.getDouble("TotalBySubMenu"));
                            rpSaleByMenuData.setSubMenuName(rs.getString("SubMenuName"));
                        }
                        rpSaleByMenuData.setItemName(rs.getString("ItemName"));
                        rpSaleByMenuData.setQuantity(rs.getInt("Qty"));
                        rpSaleByMenuData.setSaleAmount(rs.getDouble("SaleAmount"));
                        lstRpSaleByMenuData.add(rpSaleByMenuData);
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
        rpSaleByMenuListAdapter =new RpSaleByMenuListAdapter(this, lstRpSaleByMenuData);
        lvReportSaleByMenu.setAdapter(rpSaleByMenuListAdapter);
    }
}
