package com.bosictsolution.waiterone;

import android.database.Cursor;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import adapter.SpTableTypeAdapter;
import common.DBHelper;
import common.SystemSetting;
import data.TableData;
import data.TableTypeData;

public class ChangeTableActivity extends AppCompatActivity {

    GridView gvEmptyTable,gvOccupiedTable;
    Spinner spOcpTableType,spEmpTableType;
    ImageButton btnEmpReload,btnOcpReload;
    View ocpSelectedView,empSelectedView;

    private DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    ArrayAdapter adapter;
    SpTableTypeAdapter spTableTypeAdapter;

    List<TableTypeData> lstTableType=new ArrayList<>();
    List<TableData> lstOccupiedTable =new ArrayList<>();
    List<TableData> lstEmptyTable =new ArrayList<>();
    List<TableData> lstTable =new ArrayList<>();
    List<String> lstEmptyTableName=new ArrayList<>();
    List<String> lstOccupiedTableName=new ArrayList<>();
    List<Integer> lstTableID=new ArrayList<>();
    List<Integer> lstOccupiedTableID=new ArrayList<>();

    private final String colorWhite="WHITE",colorRed="RED";
    String ocpViewColor =colorWhite,empViewColor=colorWhite, ocpTableName,empTableName;
    int ocpTableID,empTableID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_table);

        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        setLayoutResource();

        getTableType();
        getTable();
        getOccupiedTable();
        showTable();

        btnEmpReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empTableID=0;
                spEmpTableType.setSelection(0);
                getTable();
                showEmpTable();
            }
        });

        btnOcpReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocpTableID=0;
                spOcpTableType.setSelection(0);
                getOccupiedTable();
                showOcpTable();
            }
        });

        spOcpTableType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) showOcpTableByTableType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spEmpTableType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0)showEmpTableByTableType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gvOccupiedTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //new
                if(ocpSelectedView ==null){
                    ocpSelectedView =view;
                    view.setBackgroundColor(getResources().getColor(R.color.colorFirstPri));
                    ocpViewColor =colorRed;
                    ocpTableID =lstOccupiedTable.get(position).getTableid();
                    ocpTableName =lstOccupiedTable.get(position).getTableName();
                }
                //other
                else{
                    //new = other
                    if(ocpSelectedView ==view){
                        if(ocpViewColor ==colorWhite){
                            view.setBackgroundColor(getResources().getColor(R.color.colorFirstPri));
                            ocpViewColor = colorRed;
                            ocpTableID =lstOccupiedTable.get(position).getTableid();
                            ocpTableName =lstOccupiedTable.get(position).getTableName();
                        }else if(ocpViewColor ==colorRed) {
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ocpViewColor = colorWhite;
                            ocpTableID =0;
                            ocpTableName ="";
                        }
                    }else {
                        ocpSelectedView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        ocpSelectedView = view;
                        view.setBackgroundColor(getResources().getColor(R.color.colorFirstPri));
                        ocpViewColor = colorRed;
                        ocpTableID =lstOccupiedTable.get(position).getTableid();
                        ocpTableName =lstOccupiedTable.get(position).getTableName();
                    }
                }
            }
        });

        gvEmptyTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //new
                if(empSelectedView ==null){
                    empSelectedView =view;
                    view.setBackgroundColor(getResources().getColor(R.color.colorFirstPri));
                    empViewColor =colorRed;
                    empTableID =lstEmptyTable.get(position).getTableid();
                    empTableName =lstEmptyTable.get(position).getTableName();
                }
                //other
                else{
                    //new = other
                    if(empSelectedView ==view){
                        if(empViewColor ==colorWhite){
                            view.setBackgroundColor(getResources().getColor(R.color.colorFirstPri));
                            empViewColor = colorRed;
                            empTableID =lstEmptyTable.get(position).getTableid();
                            empTableName =lstEmptyTable.get(position).getTableName();
                        }else if(empViewColor ==colorRed) {
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            empViewColor = colorWhite;
                            empTableID =0;
                            empTableName ="";
                        }
                    }else {
                        empSelectedView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        empSelectedView = view;
                        view.setBackgroundColor(getResources().getColor(R.color.colorFirstPri));
                        empViewColor = colorRed;
                        empTableID =lstEmptyTable.get(position).getTableid();
                        empTableName =lstEmptyTable.get(position).getTableName();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.menu_change_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        if (itemId == R.id.menuChange) {
            if(ocpTableID==0){
                systemSetting.showMessage(SystemSetting.WARNING,"Choose Occupied Table!",getApplicationContext(),getLayoutInflater());
                return false;
            }
            if(empTableID==0){
                systemSetting.showMessage(SystemSetting.WARNING,"Choose Empty Table!",getApplicationContext(),getLayoutInflater());
                return false;
            }
            if(db.changeTable(ocpTableID,empTableID)) {
                systemSetting.showMessage(SystemSetting.INFO,"Changed From Table " + ocpTableName + " To Table " + empTableName + "!",getApplicationContext(),getLayoutInflater());
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTable(){
        lstTable =new ArrayList<>();
        Cursor cur=db.getTable();
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                TableData data=new TableData();
                data.setTableid(cur.getInt(0));
                data.setTableName(cur.getString(1));
                lstTable.add(data);
            }
        }
    }

    private void getOccupiedTable(){
        lstOccupiedTable =new ArrayList<>();
        Cursor cur=db.getTableFromTranSaleTemp();
        while(cur.moveToNext()){
            TableData data=new TableData();
            data.setTableid(cur.getInt(0));
            data.setTableName(cur.getString(1));
            lstOccupiedTable.add(data);
        }
    }

    private void showTable(){
        lstEmptyTableName=new ArrayList<>();
        lstOccupiedTableName=new ArrayList<>();
        lstTableID=new ArrayList<>();
        lstOccupiedTableID=new ArrayList<>();
        lstEmptyTable=new ArrayList<>();

        for(int i=0;i<lstTable.size();i++){
            lstTableID.add(lstTable.get(i).getTableid());
        }
        for(int i=0;i<lstOccupiedTable.size();i++){
            lstOccupiedTableID.add(lstOccupiedTable.get(i).getTableid());
            lstOccupiedTableName.add(lstOccupiedTable.get(i).getTableName());
        }
        for(int i = 0; i< lstTableID.size(); i++){
            if(!lstOccupiedTableID.contains(lstTableID.get(i))) {
                lstEmptyTableName.add(lstTable.get(i).getTableName());
                TableData data = new TableData();
                data.setTableid(lstTable.get(i).getTableid());
                data.setTableName(lstTable.get(i).getTableName());
                lstEmptyTable.add(data);
            }
        }

        adapter=new ArrayAdapter(this,R.layout.list_occupied_table,R.id.tvOccupiedTable,lstOccupiedTableName);
        gvOccupiedTable.setAdapter(adapter);

        adapter=new ArrayAdapter(this,R.layout.list_empty_table,R.id.tvEmptyTable,lstEmptyTableName);
        gvEmptyTable.setAdapter(adapter);
    }

    private void showEmpTable(){
        lstEmptyTableName=new ArrayList<>();
        lstTableID=new ArrayList<>();
        List<Integer> lstCurOcpTableID=new ArrayList<>();
        List<TableData> lstCurOcpTable=new ArrayList<>();
        lstEmptyTable=new ArrayList<>();

        Cursor cur=db.getTableFromTranSaleTemp();
        while(cur.moveToNext()){
            TableData data=new TableData();
            data.setTableid(cur.getInt(0));
            data.setTableName(cur.getString(1));
            lstCurOcpTable.add(data);
        }

        for(int i=0;i<lstTable.size();i++){
            lstTableID.add(lstTable.get(i).getTableid());
        }
        for(int i=0;i<lstCurOcpTable.size();i++){
            lstCurOcpTableID.add(lstCurOcpTable.get(i).getTableid());
        }
        for(int i = 0; i< lstTableID.size(); i++){
            if(!lstCurOcpTableID.contains(lstTableID.get(i))) {
                lstEmptyTableName.add(lstTable.get(i).getTableName());
                TableData data = new TableData();
                data.setTableid(lstTable.get(i).getTableid());
                data.setTableName(lstTable.get(i).getTableName());
                lstEmptyTable.add(data);
            }
        }

        adapter=new ArrayAdapter(this,R.layout.list_empty_table,R.id.tvEmptyTable,lstEmptyTableName);
        gvEmptyTable.setAdapter(adapter);
    }

    private void showOcpTable() {
        lstOccupiedTableName = new ArrayList<>();
        lstOccupiedTableID = new ArrayList<>();

        for (int i = 0; i < lstOccupiedTable.size(); i++) {
            lstOccupiedTableID.add(lstOccupiedTable.get(i).getTableid());
            lstOccupiedTableName.add(lstOccupiedTable.get(i).getTableName());
        }
        adapter = new ArrayAdapter(this, R.layout.list_occupied_table, R.id.tvOccupiedTable, lstOccupiedTableName);
        gvOccupiedTable.setAdapter(adapter);
    }

    private void getTableType(){
        TableTypeData empData=new TableTypeData();
        empData.setTableTypeID(0);
        empData.setTableTypeName("Choose Table Type");
        lstTableType.add(empData);
        Cursor cur=db.getTableType();
        while(cur.moveToNext()){
            TableTypeData data=new TableTypeData();
            data.setTableTypeID(cur.getInt(0));
            data.setTableTypeName(cur.getString(1));
            lstTableType.add(data);
        }
        spTableTypeAdapter =new SpTableTypeAdapter(getApplicationContext(),lstTableType);
        spOcpTableType.setAdapter(spTableTypeAdapter);
        spEmpTableType.setAdapter(spTableTypeAdapter);
    }

    private void showOcpTableByTableType(int position){
        lstOccupiedTable=new ArrayList<>();
        lstOccupiedTableID=new ArrayList<>();
        lstOccupiedTableName=new ArrayList<>();
        int tableTypeID=lstTableType.get(position).getTableTypeID();
        if(tableTypeID!=0){
            Cursor cur=db.getOcpTableByTableType(tableTypeID);
            while(cur.moveToNext()){
                TableData data=new TableData();
                data.setTableid(cur.getInt(0));
                data.setTableName(cur.getString(1));
                lstOccupiedTable.add(data);
            }
            for(int i=0;i<lstOccupiedTable.size();i++){
                lstOccupiedTableID.add(lstOccupiedTable.get(i).getTableid());
                lstOccupiedTableName.add(lstOccupiedTable.get(i).getTableName());
            }
            adapter=new ArrayAdapter(this,R.layout.list_occupied_table,R.id.tvOccupiedTable,lstOccupiedTableName);
            gvOccupiedTable.setAdapter(adapter);
        }
    }

    private void showEmpTableByTableType(int position){
        lstTableID=new ArrayList<>();
        lstTable=new ArrayList<>();
        lstEmptyTableName=new ArrayList<>();
        lstEmptyTable=new ArrayList<>();
        List<TableData> lstCurOcpTable=new ArrayList<>();
        List<Integer> lstCurOcpTableID=new ArrayList<>();
        int tableTypeID=lstTableType.get(position).getTableTypeID();
        if(tableTypeID!=0){
            Cursor cur=db.getOcpTableByTableType(tableTypeID);
            while(cur.moveToNext()){
                TableData data=new TableData();
                data.setTableid(cur.getInt(0));
                data.setTableName(cur.getString(1));
                lstCurOcpTable.add(data);
            }
            for(int i=0;i<lstCurOcpTable.size();i++){
                lstCurOcpTableID.add(lstCurOcpTable.get(i).getTableid());
            }

            Cursor cur2=db.getTableByTableType(tableTypeID);
            if(cur2.getCount()!=0){
                while(cur2.moveToNext()){
                    TableData data=new TableData();
                    data.setTableid(cur2.getInt(0));
                    data.setTableName(cur2.getString(1));
                    lstTable.add(data);
                }
            }
            for(int i=0;i<lstTable.size();i++){
                lstTableID.add(lstTable.get(i).getTableid());
            }
            for(int i = 0; i< lstTableID.size(); i++){
                if(!lstCurOcpTableID.contains(lstTableID.get(i))) {
                    lstEmptyTableName.add(lstTable.get(i).getTableName());
                    TableData data = new TableData();
                    data.setTableid(lstTable.get(i).getTableid());
                    data.setTableName(lstTable.get(i).getTableName());
                    lstEmptyTable.add(data);
                }
            }
            adapter=new ArrayAdapter(this,R.layout.list_empty_table,R.id.tvEmptyTable,lstEmptyTableName);
            gvEmptyTable.setAdapter(adapter);
        }
    }

    private void setLayoutResource(){
        gvEmptyTable =(GridView)findViewById(R.id.gvEmptyTable);
        gvOccupiedTable =(GridView)findViewById(R.id.gvOccupiedTable);
        spEmpTableType =(Spinner)findViewById(R.id.spEmpTableType);
        spOcpTableType =(Spinner)findViewById(R.id.spOcpTableType);
        btnEmpReload =(ImageButton) findViewById(R.id.btnEmpReload);
        btnOcpReload =(ImageButton)findViewById(R.id.btnOcpReload);
    }
}
