package com.bosictsolution.waiterone;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import common.DBHelper;
import common.FeatureList;
import common.SystemSetting;
import data.TableData;
import data.TableTypeData;

public class TableActivity extends AppCompatActivity {

    ListView lvTableType;
    GridView gvEmptyTable, gvOccupiedTable,gvBookingTable;
    TextView tvTableTypeName,tvLabelEmptyTable,tvLabelOccupiedTable,tvLabelBookingTable,tvTargetTable,tvCombineTable;
    LinearLayout tableLayout,layoutMultiTbBill,layoutTarget;
    ImageView imgBookingTable;
    Switch swtMultiTbBill;
    ImageButton btnOk,btnClear;

    private DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    ArrayAdapter adapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    final Context context = this;

    static boolean from_main, from_customer_entry;
    String role,billTableName,billWaiterName,viewOrderTableName;
    List<TableTypeData> lstTableTypeData=new ArrayList<>();
    List<TableData> lstTableDataByTableTypeID =new ArrayList<>();
    List<TableData> lstOccupiedTableDataByTableTypeID =new ArrayList<>();
    List<TableData> lstEmptyTableDataByTableTypeID =new ArrayList<>();
    List<TableData> lstBookingTableDataByTableTypeID =new ArrayList<>();
    int selectedTableTypeID,billTableID,billWaiterID,viewOrderTableID,allowBooking;
    public static List<Integer> lstMultiTbBillId=new ArrayList<>();
    List<String> lstCombineTbBillName=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        /**Configuration config=getResources().getConfiguration();
        if(config.smallestScreenWidthDp>=600){
            setContentView(R.layout.activity_table);
            ActionBar actionbar=getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
        }else{
            setContentView(R.layout.activity_table_phone);
            drawerLayout=(DrawerLayout)findViewById(R.id.sliderLayout);
            ActionBar actionbar=getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setDisplayShowCustomEnabled(true);
        }**/

        db=new DBHelper(this);

        allowBooking=db.getFeatureResult(FeatureList.fBookingTable);

        Intent i=getIntent();
        role=i.getStringExtra("role");
        if(role.equals("just_choice")){
            from_main = i.getBooleanExtra("from_waiter_main", false);
            from_customer_entry = i.getBooleanExtra("from_customer_info", false);
        }

        setLayoutResource();

        getAllTableType();
        if(lstTableTypeData.size()!=0) {
            selectedTableTypeID = lstTableTypeData.get(0).getTableTypeID();
            String first_table_type_name = lstTableTypeData.get(0).getTableTypeName();
            tvTableTypeName.setText(first_table_type_name);
        }

        getTableByTableTypeID(selectedTableTypeID);
        getOccupiedAndBookingTable();

        gvOccupiedTable.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent,View v,int position,long id){
                if(role.equals("view")){
                    viewOrderTableID=lstOccupiedTableDataByTableTypeID.get(position).getTableid();
                    viewOrderTableName=lstOccupiedTableDataByTableTypeID.get(position).getTableName();
                    checkExistOrder();
                }
                else if(role.equals("bill")) {
                    if (swtMultiTbBill.isChecked()) {
                        String combileMultiTables="";
                        if(lstMultiTbBillId.contains(lstOccupiedTableDataByTableTypeID.get(position).getTableid()))return;
                        if(tvTargetTable.getText().toString().length()==0) {
                            tvTargetTable.setText(lstOccupiedTableDataByTableTypeID.get(position).getTableName());
                            tvTargetTable.setTag(lstOccupiedTableDataByTableTypeID.get(position).getTableid());
                            lstMultiTbBillId.add(lstOccupiedTableDataByTableTypeID.get(position).getTableid());
                            systemSetting.showMessage(SystemSetting.WARNING, "Choose Other Tables!", context, getLayoutInflater());
                        }else{
                            lstMultiTbBillId.add(lstOccupiedTableDataByTableTypeID.get(position).getTableid());
                            lstCombineTbBillName.add(lstOccupiedTableDataByTableTypeID.get(position).getTableName());
                            for(int i=0;i<lstCombineTbBillName.size();i++){
                                combileMultiTables+=lstCombineTbBillName.get(i)+",";
                            }
                            combileMultiTables=combileMultiTables.substring(0,combileMultiTables.length()-1);
                            tvCombineTable.setText(combileMultiTables);
                        }
                    } else {
                        billTableID = lstOccupiedTableDataByTableTypeID.get(position).getTableid();
                        billTableName = lstOccupiedTableDataByTableTypeID.get(position).getTableName();
                        billWaiterID = Integer.parseInt(SaleActivity.tvWaiterName.getTag().toString());
                        billWaiterName = SaleActivity.tvWaiterName.getText().toString();
                        Intent i = new Intent(getApplicationContext(), BillActivity.class);
                        i.putExtra("tableid", billTableID);
                        i.putExtra("tablename", billTableName);
                        i.putExtra("waitername", billWaiterName);
                        startActivity(i);
                        finish();
                    }
                }
                else if(role.equals("cusinfo")){
                    Intent i=new Intent(TableActivity.this,CustomerEntryActivity.class);
                    i.putExtra("tableid",lstOccupiedTableDataByTableTypeID.get(position).getTableid());
                    i.putExtra("tablename",lstOccupiedTableDataByTableTypeID.get(position).getTableName());
                    i.putExtra("waiterid", Integer.parseInt(SaleActivity.tvWaiterName.getTag().toString()));
                    i.putExtra("waitername", SaleActivity.tvWaiterName.getText().toString());
                    startActivity(i);
                    finish();
                }
                else if(role.equals("booking")){
                    systemSetting.showMessage(SystemSetting.ERROR,"Not allow booking for this table!",context,getLayoutInflater());
                }
                else if(role.equals("just_choice")){
                    if(from_main ==true){
                        SaleActivity.choosed_table_id=lstOccupiedTableDataByTableTypeID.get(position).getTableid();
                        SaleActivity.choosed_table_name=lstOccupiedTableDataByTableTypeID.get(position).getTableName();
                    }
                    if(from_customer_entry ==true){
                        CustomerEntryActivity.btnChooseTable.setTag(lstOccupiedTableDataByTableTypeID.get(position).getTableid());
                        CustomerEntryActivity.btnChooseTable.setText(lstOccupiedTableDataByTableTypeID.get(position).getTableName());
                    }
                    finish();
                }
            }
        });

        gvBookingTable.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,View v,int position,long id) {

                if(role.equals("just_choice")){
                    if(from_main ==true){
                        SaleActivity.choosed_table_id=lstBookingTableDataByTableTypeID.get(position).getTableid();
                        SaleActivity.choosed_table_name=lstBookingTableDataByTableTypeID.get(position).getTableName();
                        finish();
                    }
                    if(from_customer_entry ==true){
                        systemSetting.showMessage(SystemSetting.WARNING,lstBookingTableDataByTableTypeID.get(position).getTableName() +" is Booking Table. Not Allow to Add Customer!",context,getLayoutInflater());
                    }
                }else if(role.equals("cusinfo")){
                    systemSetting.showMessage(SystemSetting.WARNING,lstBookingTableDataByTableTypeID.get(position).getTableName() +" is Booking Table. Not Allow to Add Customer!",context,getLayoutInflater());
                }else if(role.equals("booking")){
                    systemSetting.showMessage(SystemSetting.WARNING,lstBookingTableDataByTableTypeID.get(position).getTableName() +" has already chosen for another Booking!",context,getLayoutInflater());
                }
                else{
                    systemSetting.showMessage(SystemSetting.WARNING,lstBookingTableDataByTableTypeID.get(position).getTableName() +" is Booking Table. Cannot Find Order!",context,getLayoutInflater());
                }
            }
        });

        gvEmptyTable.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,View v,int position,long id) {

                if(role.equals("just_choice")){
                    if(from_main ==true){
                        SaleActivity.choosed_table_id=lstEmptyTableDataByTableTypeID.get(position).getTableid();
                        SaleActivity.choosed_table_name=lstEmptyTableDataByTableTypeID.get(position).getTableName();
                        finish();
                    }
                    if(from_customer_entry ==true){
                        systemSetting.showMessage(SystemSetting.WARNING,lstEmptyTableDataByTableTypeID.get(position).getTableName() +" is Empty Table. Not Allow to Add Customer!",context,getLayoutInflater());
                    }
                }else if(role.equals("cusinfo")){
                    systemSetting.showMessage(SystemSetting.WARNING,lstEmptyTableDataByTableTypeID.get(position).getTableName() +" is Empty Table. Not Allow to Add Customer!",context,getLayoutInflater());
                }else if(role.equals("booking")){
                    BookingEntryActivity.bookingTableID=lstEmptyTableDataByTableTypeID.get(position).getTableid();
                    BookingEntryActivity.btnChooseTable.setText(lstEmptyTableDataByTableTypeID.get(position).getTableName());
                    finish();
                }
                else{
                    systemSetting.showMessage(SystemSetting.WARNING,lstEmptyTableDataByTableTypeID.get(position).getTableName() +" is Empty Table. Cannot Find Order!",context,getLayoutInflater());
                }
            }
        });

        lvTableType.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTableTypeID=lstTableTypeData.get(position).getTableTypeID();
                String tableTypeName=lstTableTypeData.get(position).getTableTypeName();
                tvTableTypeName.setText(tableTypeName);
                if(drawerLayout!=null)drawerLayout.closeDrawer(lvTableType);
                getTableByTableTypeID(selectedTableTypeID);
                getOccupiedAndBookingTable();
            }
        });

        swtMultiTbBill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    layoutTarget.setVisibility(View.VISIBLE);
                    systemSetting.showMessage(SystemSetting.INFO,"Choose Main Table!",context,getLayoutInflater());
                }else{
                    layoutTarget.setVisibility(View.GONE);
                }
                clearMultiTbBill();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvTargetTable.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Choose Main Table!",context,getLayoutInflater());
                    return;
                }
                if(tvCombineTable.getText().toString().length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Choose Other Tables!",context,getLayoutInflater());
                    return;
                }
                billTableID = Integer.parseInt(tvTargetTable.getTag().toString());
                billTableName =tvTargetTable.getText().toString();
                billWaiterID = Integer.parseInt(SaleActivity.tvWaiterName.getTag().toString());
                billWaiterName = SaleActivity.tvWaiterName.getText().toString();
                Intent i = new Intent(getApplicationContext(), BillActivity.class);
                i.putExtra("tableid", billTableID);
                i.putExtra("tablename", billTableName);
                i.putExtra("waitername", billWaiterName);
                i.putExtra("MultiTbBill",true);
                startActivity(i);
                finish();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMultiTbBill();
            }
        });

        if(drawerLayout!=null){
            actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.mipmap.menu,R.string.app_name,R.string.app_name){
                public void onDrawerClosed(View view){
                    invalidateOptionsMenu();
                }
            };
        }

        if(drawerLayout!=null){
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
            if(savedInstanceState==null){

            }
        }
    }

    // when using the ActionBarDrawerToggle, must call it during onPostCreate() and onConfigurationChanged()

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        if(actionBarDrawerToggle!=null){
            actionBarDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if(actionBarDrawerToggle!=null){
            actionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle!=null){
            if(actionBarDrawerToggle.onOptionsItemSelected(item)){
                return true;
            }
        }
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearMultiTbBill(){
        tvTargetTable.setText("");
        tvTargetTable.setTag("");
        tvCombineTable.setText("");
        lstMultiTbBillId=new ArrayList<>();
        lstCombineTbBillName=new ArrayList<>();
    }

    private void getAllTableType(){
        Cursor cur=db.getTableType();
        List<String> lstTableTypeName=new ArrayList<>();
        lstTableTypeData=new ArrayList<>();
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                TableTypeData data=new TableTypeData();
                data.setTableTypeID(cur.getInt(0));
                data.setTableTypeName(cur.getString(1));
                lstTableTypeName.add(cur.getString(1));
                lstTableTypeData.add(data);
            }
            if(!cur.isClosed()){
                cur.close();
            }
        }
        adapter=new ArrayAdapter(this,R.layout.list_st_table_type,R.id.tvListRowItem,lstTableTypeName);
        lvTableType.setAdapter(adapter);
    }

    private void getTableByTableTypeID(int tableTypeID){
        lstTableDataByTableTypeID =new ArrayList<>();
        Cursor cur=db.getTableByTableType(tableTypeID);
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                TableData data=new TableData();
                data.setTableid(cur.getInt(0));
                data.setTableName(cur.getString(1));
                lstTableDataByTableTypeID.add(data);
            }
        }
    }

    private void getOccupiedAndBookingTable(){
        lstOccupiedTableDataByTableTypeID =new ArrayList<>();
        Cursor cur=db.getTableByTableTypeFromTranSaleTemp(selectedTableTypeID);
        while(cur.moveToNext()){
            TableData data=new TableData();
            data.setTableid(cur.getInt(0));
            data.setTableName(cur.getString(1));
            lstOccupiedTableDataByTableTypeID.add(data);
        }
        if(allowBooking==1){
            lstBookingTableDataByTableTypeID = new ArrayList<>();
            Cursor cur1=db.getTableByTableTypeFromBooking(selectedTableTypeID);
            while (cur1.moveToNext()) {
                TableData data = new TableData();
                data.setTableid(cur1.getInt(0));
                data.setTableName(cur1.getString(1));
                lstBookingTableDataByTableTypeID.add(data);
            }
        }
        showTableByTableTypeID();
    }

    private void showTableByTableTypeID(){
        List<String> lstEmptyTableName=new ArrayList<>();
        List<String> lstOccupiedTableName=new ArrayList<>();
        List<String> lstBookingTableName=new ArrayList<>();
        List<Integer> lstTableID=new ArrayList<>();
        List<Integer> lstOccupiedTableID=new ArrayList<>();
        List<Integer> lstBookingTableID=new ArrayList<>();
        lstEmptyTableDataByTableTypeID=new ArrayList<>();
        int noOfColumns=5,totalHeight,count,adapterCount,modulo;

        for(int i=0;i<lstTableDataByTableTypeID.size();i++){
            lstTableID.add(lstTableDataByTableTypeID.get(i).getTableid());
        }
        for(int i=0;i<lstOccupiedTableDataByTableTypeID.size();i++){
            lstOccupiedTableID.add(lstOccupiedTableDataByTableTypeID.get(i).getTableid());
            lstOccupiedTableName.add(lstOccupiedTableDataByTableTypeID.get(i).getTableName());
        }
        for(int i=0;i<lstBookingTableDataByTableTypeID.size();i++){
            lstBookingTableID.add(lstBookingTableDataByTableTypeID.get(i).getTableid());
            lstBookingTableName.add(lstBookingTableDataByTableTypeID.get(i).getTableName());
        }
        for(int i = 0; i< lstTableID.size(); i++){
            if(!lstOccupiedTableID.contains(lstTableID.get(i))){
                if(!lstBookingTableID.contains(lstTableID.get(i))) {
                    lstEmptyTableName.add(lstTableDataByTableTypeID.get(i).getTableName());
                    TableData data = new TableData();
                    data.setTableid(lstTableDataByTableTypeID.get(i).getTableid());
                    data.setTableName(lstTableDataByTableTypeID.get(i).getTableName());
                    lstEmptyTableDataByTableTypeID.add(data);
                }
            }
        }

        if(lstTableDataByTableTypeID.size()!=0){
            if(lstOccupiedTableDataByTableTypeID.size()!=0){
                adapter=new ArrayAdapter(this,R.layout.list_occupied_table,R.id.tvOccupiedTable,lstOccupiedTableName);
                totalHeight=0;
                count=adapter.getCount();
                adapterCount=0;
                if(count!=0){
                    adapterCount=Math.round(count/noOfColumns);
                    modulo=count%noOfColumns;
                    if(modulo!=0){
                        adapterCount+=1;
                    }
                }
                for(int size=0;size<adapterCount;size++){
                    View listItem=adapter.getView(size, null, gvOccupiedTable);
                    listItem.measure(0, 0);
                    totalHeight+=listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams params= gvOccupiedTable.getLayoutParams();
                params.height=totalHeight;
                gvOccupiedTable.setLayoutParams(params);
                gvOccupiedTable.setAdapter(adapter);
                gvOccupiedTable.setVisibility(View.VISIBLE);
            }
            else{
                gvOccupiedTable.setVisibility(View.GONE);
            }
            if(lstBookingTableDataByTableTypeID.size()!=0){
                adapter=new ArrayAdapter(this,R.layout.list_booking_table,R.id.tvBookingTable,lstBookingTableName);
                totalHeight=0;
                count=adapter.getCount();
                adapterCount=0;
                if(count!=0){
                    adapterCount=Math.round(count/noOfColumns);
                    modulo=count%noOfColumns;
                    if(modulo!=0){
                        adapterCount+=1;
                    }
                }
                for(int size=0;size<adapterCount;size++){
                    View listItem=adapter.getView(size, null, gvBookingTable);
                    listItem.measure(0, 0);
                    totalHeight+=listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams params= gvBookingTable.getLayoutParams();
                params.height=totalHeight;
                gvBookingTable.setLayoutParams(params);
                gvBookingTable.setAdapter(adapter);
                gvBookingTable.setVisibility(View.VISIBLE);
            }
            else{
                gvBookingTable.setVisibility(View.GONE);
            }

            adapter=new ArrayAdapter(this,R.layout.list_empty_table,R.id.tvEmptyTable,lstEmptyTableName);
            totalHeight=0;
            count=adapter.getCount();
            adapterCount=0;
            if(count!=0){
                adapterCount=Math.round(count/noOfColumns);
                modulo=count%noOfColumns;
                if(modulo!=0){
                    adapterCount+=1;
                }
            }
            for(int size=0;size<adapterCount;size++){
                View listItem=adapter.getView(size, null, gvEmptyTable);
                listItem.measure(0, 0);
                totalHeight+=listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params= gvEmptyTable.getLayoutParams();
            params.height=totalHeight;
            gvEmptyTable.setLayoutParams(params);
            gvEmptyTable.setAdapter(adapter);
        }
        else {
            List<String> lstEmpty = new ArrayList<>();
            lstEmpty.add("No Tables!");
            adapter = new ArrayAdapter(this, R.layout.list_empty, R.id.tvEmpty, lstEmpty);
            gvEmptyTable.setAdapter(adapter);
            gvOccupiedTable.setVisibility(View.GONE);
            gvBookingTable.setVisibility(View.GONE);
        }
    }

    private void checkExistOrder(){
        Cursor cur=db.getTransactionFromTranSaleTemp(viewOrderTableID);
        if(cur.moveToFirst()){
            Intent i=new Intent(TableActivity.this,ViewOrderActivity.class);
            i.putExtra("tableid", viewOrderTableID);
            i.putExtra("tablename", viewOrderTableName);
            startActivity(i);
        }else{
            systemSetting.showMessage(SystemSetting.ERROR,"Not Found Order!",context,getLayoutInflater());
        }
    }

    private void setLayoutResource(){
        lvTableType =(ListView)findViewById(R.id.lvTableType);
        gvEmptyTable =(GridView)findViewById(R.id.gvEmptyTable);
        tvTableTypeName=(TextView)findViewById(R.id.tvTableTypeName);
        gvOccupiedTable =(GridView)findViewById(R.id.gvOccupiedTable);
        gvOccupiedTable.setVisibility(View.GONE);
        tableLayout=(LinearLayout)findViewById(R.id.tableLayout);
        tvLabelEmptyTable=(TextView)findViewById(R.id.tvLabelEmptyTable);
        tvLabelOccupiedTable=(TextView)findViewById(R.id.tvLabelOccupiedTable);
        gvBookingTable=(GridView)findViewById(R.id.gvBookingTable);
        tvLabelBookingTable=(TextView)findViewById(R.id.tvLabelBookingTable);
        imgBookingTable=(ImageView)findViewById(R.id.imgBookingTable);
        layoutMultiTbBill=(LinearLayout)findViewById(R.id.layoutMultiTbBill);
        layoutTarget=(LinearLayout)findViewById(R.id.layoutTarget);
        swtMultiTbBill=(Switch)findViewById(R.id.swtMultiTbBill);
        btnOk=(ImageButton)findViewById(R.id.btnOk);
        btnClear=(ImageButton)findViewById(R.id.btnClear);
        tvTargetTable=(TextView)findViewById(R.id.tvTargetTable);
        tvCombineTable=(TextView)findViewById(R.id.tvCombineTable);

        if(allowBooking!=1){
            gvBookingTable.setVisibility(View.GONE);
            tvLabelBookingTable.setVisibility(View.GONE);
            imgBookingTable.setVisibility(View.GONE);
        }else{
            gvBookingTable.setVisibility(View.VISIBLE);
            tvLabelBookingTable.setVisibility(View.VISIBLE);
            imgBookingTable.setVisibility(View.VISIBLE);
        }

        if(role.equals("bill")){
            if(db.getFeatureResult(FeatureList.fMultiTableBill)==1){
                layoutMultiTbBill.setVisibility(View.VISIBLE);
            }
        }
    }
}
