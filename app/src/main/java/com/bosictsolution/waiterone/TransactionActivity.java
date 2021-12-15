package com.bosictsolution.waiterone;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapter.MasterSaleListAdapter;
import adapter.SpTableAdapter;
import adapter.SpTableTypeAdapter;
import adapter.SpWaiterAdapter;
import common.DBHelper;
import common.SystemSetting;
import data.TableData;
import data.TableTypeData;
import data.TransactionData;
import data.WaiterData;
import listener.DeleteVouListBtnListener;

public class TransactionActivity extends AppCompatActivity implements DeleteVouListBtnListener {

    EditText etVoucherNo;
    //Spinner spWaiter,spTableType,spTable;
    ImageButton btnRefresh;
    Button btnSearch;
    ListView lvSaleData;

    //List<WaiterData> lstWaiter=new ArrayList<>();
    //List<TableTypeData> lstTableType=new ArrayList<>();
    //List<TableData> lstTable,lstTableByTableType;
    List<TransactionData> lstTranData;

    //SpWaiterAdapter spWaiterAdapter;
    //SpTableTypeAdapter spTableTypeAdapter;
    //SpTableAdapter spTableAdapter;
    MasterSaleListAdapter masterSaleListAdapter;

    DBHelper db=new DBHelper(this);
    SystemSetting systemSetting=new SystemSetting();
    private Context context=this;

    String fromDate,toDate;
    int deleteTranId,removePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);

        Intent i=getIntent();
        fromDate=i.getStringExtra("FromDate");
        toDate=i.getStringExtra("ToDate");

        setLayoutResource();
        startup();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshFilter();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchData();
            }
        });

        /**spTableType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getTableByTableType(lstTableType.get(position).getTableTypeID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });**/
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
    public void onDetailClickListener(int position){
        int tranId=lstTranData.get(position).getTranid();
        String vouNo=lstTranData.get(position).getVouno();
        Intent i=new Intent(this,TranDetailActivity.class);
        i.putExtra("TranId",tranId);
        i.putExtra("VouNo",vouNo);
        startActivity(i);
    }

    @Override
    public void onDeleteClickListener(int position){
        deleteTranId=lstTranData.get(position).getTranid();
        removePosition=position;
        showConfirmDialog("The whole voucher will be deleted.Do you want to delete this voucher?");
    }

    private void showConfirmDialog(String msg){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_confirm, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        TextView tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        tvConfirmMessage.setText(msg);

        passwordDialog.setCancelable(true);
        final android.app.AlertDialog passwordRequireDialog=passwordDialog.create();
        passwordRequireDialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                passwordRequireDialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                passwordRequireDialog.dismiss();
                if(db.deleteSaleVouByTranId(deleteTranId)) {
                    lstTranData.remove(removePosition);
                    setVouAdapter();
                    systemSetting.showMessage(SystemSetting.SUCCESS,"Deleted",context,getLayoutInflater());
                }
            }
        });
    }

    private void startup(){
        if(fromDate!=null && toDate!=null)setTitle("Sale Vouchers("+fromDate+" to "+toDate+")");
        //getWaiter();
        //getTableType();
        //getTable();
        getSaleVoucher();
    }

    private void searchData(){
        lstTranData=new ArrayList<>();
        //int tableId;
        if(etVoucherNo.getText().toString().length()==0)return;
        String vouNo=etVoucherNo.getText().toString();
        /**int waiterPosition=spWaiter.getSelectedItemPosition();
        int tableTypePosition=spTableType.getSelectedItemPosition();
        int tablePosition=spTable.getSelectedItemPosition();
        int waiterId=lstWaiter.get(waiterPosition).getWaiterid();
        int tableTypeId=lstTableType.get(tableTypePosition).getTableTypeID();
        if(tableTypeId==0) tableId=lstTable.get(tablePosition).getTableid();
        else tableId=lstTableByTableType.get(tablePosition).getTableid();**/

        Cursor cur=db.getSaleVouByVouNo(vouNo);
        if(cur.moveToFirst()){
            TransactionData data = new TransactionData();
            data.setTranid(cur.getInt(0));
            data.setVouno(cur.getString(1));
            data.setDate(cur.getString(2));
            data.setWaiterName(cur.getString(3));
            data.setTableName(cur.getString(4));
            lstTranData.add(data);
        }
        setVouAdapter();
    }

    private void refreshFilter(){
        systemSetting.showMessage(SystemSetting.INFO,"Refreshing",context,getLayoutInflater());
        etVoucherNo.setText("");
        //spWaiter.setSelection(0);
        //spTableType.setSelection(0);
        //spTable.setSelection(0);
        getSaleVoucher();
    }

    private void getSaleVoucher() {
        lstTranData = new ArrayList<>();
        Cursor cur;
        if (fromDate != null && toDate != null) {
            cur = db.getMasterSaleDataByDate(fromDate, toDate);
        } else {
            cur = db.getMasterSaleData();
        }
        while (cur.moveToNext()) {
            TransactionData data = new TransactionData();
            data.setTranid(cur.getInt(0));
            data.setVouno(cur.getString(1));
            data.setDate(cur.getString(2));
            data.setWaiterName(cur.getString(3));
            data.setTableName(cur.getString(4));
            lstTranData.add(data);
        }
        setVouAdapter();
    }

    private void setVouAdapter(){
        masterSaleListAdapter=new MasterSaleListAdapter(this,lstTranData);
        lvSaleData.setAdapter(masterSaleListAdapter);
        masterSaleListAdapter.setOnButtonClickListener(this);
    }

    /**private void getWaiter(){
        WaiterData emptyData=new WaiterData();
        emptyData.setWaiterid(0);
        emptyData.setWaiterName("All Waiter");
        lstWaiter.add(emptyData);
        Cursor cur=db.getWaiter();
        while(cur.moveToNext()){
            WaiterData waiterData=new WaiterData();
            waiterData.setWaiterid(cur.getInt(0));
            waiterData.setWaiterName(cur.getString(1));
            lstWaiter.add(waiterData);
        }
        SystemSetting.isSpTxtWhite=false;
        spWaiterAdapter=new SpWaiterAdapter(this,lstWaiter);
        spWaiter.setAdapter(spWaiterAdapter);
    }

    private void getTableType(){
        TableTypeData emptyData=new TableTypeData();
        emptyData.setTableTypeID(0);
        emptyData.setTableTypeName("All Table Type");
        lstTableType.add(emptyData);
        Cursor cur=db.getTableType();
        while(cur.moveToNext()){
            TableTypeData tableTypeData=new TableTypeData();
            tableTypeData.setTableTypeID(cur.getInt(0));
            tableTypeData.setTableTypeName(cur.getString(1));
            lstTableType.add(tableTypeData);
        }
        spTableTypeAdapter=new SpTableTypeAdapter(this,lstTableType);
        spTableType.setAdapter(spTableTypeAdapter);
    }

    private void getTable(){
        lstTable=new ArrayList<>();
        TableData emptyData=new TableData();
        emptyData.setTableid(0);
        emptyData.setTableName("All Table");
        lstTable.add(emptyData);
        Cursor cur=db.getTable();
        while(cur.moveToNext()){
            TableData tableData=new TableData();
            tableData.setTableid(cur.getInt(0));
            tableData.setTableName(cur.getString(1));
            tableData.setTableTypeID(cur.getInt(2));
            lstTable.add(tableData);
        }
        spTableAdapter=new SpTableAdapter(this,lstTable);
        spTable.setAdapter(spTableAdapter);
    }

    private void getTableByTableType(int tableTypeId){
        lstTableByTableType=new ArrayList<>();
        if(tableTypeId==0) {
            getTable();
        }else{
            TableData emptyData=new TableData();
            emptyData.setTableid(0);
            emptyData.setTableName("All Table");
            lstTableByTableType.add(emptyData);
            for (int i = 0; i < lstTable.size(); i++) {
                if (lstTable.get(i).getTableTypeID() == tableTypeId) {
                    TableData tableData=new TableData();
                    tableData.setTableid(lstTable.get(i).getTableid());
                    tableData.setTableName(lstTable.get(i).getTableName());
                    tableData.setTableTypeID(tableTypeId);
                    lstTableByTableType.add(tableData);
                }
            }
            spTableAdapter=new SpTableAdapter(this,lstTableByTableType);
            spTable.setAdapter(spTableAdapter);
        }
    }**/

    private void setLayoutResource(){
        etVoucherNo=(EditText)findViewById(R.id.etVoucherNo);
        //spWaiter=(Spinner)findViewById(R.id.spWaiter);
        //spTableType=(Spinner)findViewById(R.id.spTableType);
        //spTable=(Spinner)findViewById(R.id.spTable);
        btnRefresh=(ImageButton)findViewById(R.id.btnRefresh);
        btnSearch=(Button)findViewById(R.id.btnSearch);
        lvSaleData=(ListView)findViewById(R.id.lvSaleData);
    }
}
