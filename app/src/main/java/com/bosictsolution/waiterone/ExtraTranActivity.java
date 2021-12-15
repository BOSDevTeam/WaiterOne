package com.bosictsolution.waiterone;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapter.TranSaleListAdapter;
import common.DBHelper;
import common.SystemSetting;
import data.TransactionData;
import listener.DeleteTranListBtnListener;

public class ExtraTranActivity extends AppCompatActivity implements DeleteTranListBtnListener {

    ListView lvItem;
    TextView tvCount;
    Button btnAllDelete;

    DBHelper db=new DBHelper(this);
    SystemSetting systemSetting=new SystemSetting();
    private Context context=this;

    List<TransactionData> lstItem;

    TranSaleListAdapter tranSaleListAdapter;
    String itemId;
    int tranId,srNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_tran);
        setLayoutResource();

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);

        getTranData();

        btnAllDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog("Are you sure you want to delete all extra item?",true);
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
    public void onDeleteClickListener(int position){
        itemId=lstItem.get(position).getItemid();
        tranId=lstItem.get(position).getTranid();
        srNo=lstItem.get(position).getSrNo();
        showConfirmDialog("Are you sure you want to delete this sale item?",false);
    }

    private void showConfirmDialog(String msg,final boolean isAllDelete){
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
                if(isAllDelete){
                    List<Integer> lstTranID=new ArrayList<>();
                    for(int i=0;i<lstItem.size();i++){
                        lstTranID.add(lstItem.get(i).getTranid());
                    }
                    if(db.testDelExtraAllTran(lstTranID)){
                        getTranData();
                        systemSetting.showMessage(SystemSetting.SUCCESS, "Deleted", context, getLayoutInflater());
                    }
                }else {
                    if (db.testDelExtraTran(tranId, srNo, itemId)) {
                        getTranData();
                        systemSetting.showMessage(SystemSetting.SUCCESS, "Deleted", context, getLayoutInflater());
                    }
                }
            }
        });
    }

    private void getTranData(){
        lstItem=new ArrayList<>();
        Cursor cur=db.testExtraTran();
        tvCount.setText("Transaction Count-"+cur.getCount());
        while(cur.moveToNext()){
            TransactionData data=new TransactionData();
            data.setTranid(cur.getInt(0));
            data.setSrNo(cur.getInt(1));
            data.setItemid(cur.getString(2));
            data.setItemName(cur.getString(3));
            data.setStringQty(cur.getString(4));
            data.setSalePrice(cur.getDouble(5));
            data.setAmount(cur.getDouble(6));
            lstItem.add(data);
        }
        tranSaleListAdapter=new TranSaleListAdapter(this,lstItem);
        lvItem.setAdapter(tranSaleListAdapter);
        tranSaleListAdapter.setOnButtonClickListener(this);
    }

    private void setLayoutResource(){
        lvItem=(ListView)findViewById(R.id.lvItem);
        tvCount =(TextView)findViewById(R.id.tvCount);
        btnAllDelete=(Button)findViewById(R.id.btnAllDelete);
    }
}
