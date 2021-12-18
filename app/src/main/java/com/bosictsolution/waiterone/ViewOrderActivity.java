package com.bosictsolution.waiterone;

import android.content.Context;
import android.database.Cursor;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import adapter.ViewOrderListAdapter;
import common.DBHelper;
import common.FeatureList;
import common.SystemSetting;
import data.TransactionData;
import listener.ViewOrderListButtonClickListener;

public class ViewOrderActivity extends AppCompatActivity implements ViewOrderListButtonClickListener {

    private ViewOrderListAdapter viewOrderListAdapter;
    ListView lvViewOrder;
    TextView tvTable,tvTax, tvCharges,tvSubTotal, tvGrandTotal,tvLabelTax,tvLabelCharges,tvLabelSubTotal,tvLabelGrandTotal,tvHeaderItem,tvHeaderQuantity,tvHeaderAmount;
    TableLayout layoutAmount;
    private DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    final Context context = this;
    int viewOrderTableID, taxPercent, chargesPercent,deleteTranID,allowTaxIncludeCharges;
    List<TransactionData> lstViewOrder=new ArrayList<>();
    String deleteItemID,confirmMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        setLayoutResource();

        db=new DBHelper(this);

        Intent intent=getIntent();
        viewOrderTableID=intent.getIntExtra("tableid", 0);
        String tablename="TABLE - "+intent.getStringExtra("tablename")+" Current Order List";
        tvTable.setText(tablename);
        allowTaxIncludeCharges=db.getFeatureResult(FeatureList.fTaxIncCharg);
        getOrder();
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
    public void onItemDeletedClickListener(int position){
        deleteTranID=lstViewOrder.get(position).getTranid();
        deleteItemID=lstViewOrder.get(position).getItemid();
        String itemName=lstViewOrder.get(position).getItemName();
        confirmMessage=itemName+" cancel?";
        showConfirmDialog();
    }

    private void getOrder(){
        lstViewOrder=new ArrayList<>();
        String stringQty;
        int integerQty;
        float floatQty;
        Cursor cur=db.getTransactionFromTranSaleTemp(viewOrderTableID);
        while(cur.moveToNext()){
            TransactionData data=new TransactionData();
            data.setItemid(cur.getString(0));
            if(db.getFeatureResult(FeatureList.fOrderTime)==1) data.setItemName(cur.getString(1)+" - "+cur.getString(8));
            else data.setItemName(cur.getString(1));
            data.setStringQty(cur.getString(2));

            stringQty=cur.getString(2);
            floatQty = Float.parseFloat(stringQty);
            if(floatQty==Math.round(floatQty)){
                integerQty=Math.round(floatQty);
                data.setIntegerQty(integerQty);
            }else{
                data.setFloatQty(floatQty);
            }
            data.setAmount(cur.getDouble(3));
            data.setTranid(cur.getInt(4));
            lstViewOrder.add(data);
        }
        showOrder(lstViewOrder);
    }

    private void showOrder(List<TransactionData> lstViewOrder){
        double subTotal=0,grandTotal,taxAmount,chargesAmount;
        viewOrderListAdapter=new ViewOrderListAdapter(this,lstViewOrder);

        int totalHeight=0;
        int adapterCount=viewOrderListAdapter.getCount();
        for(int size=0;size<adapterCount;size++){
            View listItem=viewOrderListAdapter.getView(size, null, lvViewOrder);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params=lvViewOrder.getLayoutParams();
        params.height=totalHeight+(lvViewOrder.getDividerHeight()*(adapterCount-1))+30;
        lvViewOrder.setLayoutParams(params);

        lvViewOrder.setAdapter(viewOrderListAdapter);
        viewOrderListAdapter.setOnItemDeletedClickListener(this);

        getServiceTax();

        for(int i=0;i<lstViewOrder.size();i++){
            subTotal+=lstViewOrder.get(i).getAmount();
        }
        chargesAmount =(chargesPercent*subTotal)/100;

        if(allowTaxIncludeCharges==1){
            tvLabelTax.setText("Tax(include charges)");
            taxAmount=(taxPercent*(subTotal+chargesAmount))/100;
        }
        else {
            tvLabelTax.setText("Tax");
            taxAmount =(taxPercent*subTotal)/100;
        }

        grandTotal=subTotal+ taxAmount + chargesAmount;

        DecimalFormat df2 = new DecimalFormat("#");

        tvSubTotal.setText(String.valueOf(df2.format(subTotal)));
        tvTax.setText(String.valueOf(df2.format(taxAmount)));
        tvCharges.setText(String.valueOf(df2.format(chargesAmount)));
        tvGrandTotal.setText(String.valueOf(df2.format(grandTotal)));
    }

    private void getServiceTax(){
        Cursor cur= db.getSystemSetting();
        if(cur.getCount()==1){
            if(cur.moveToFirst()){
                taxPercent =cur.getInt(1);
                chargesPercent =cur.getInt(2);
            }
        }
    }

    private void showConfirmDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_confirm, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        TextView tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        tvConfirmMessage.setText(confirmMessage);
        btnOK.setText("Yes");
        btnCancel.setText("No");

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
                if(db.deleteTransactionItemFromTranSaleTemp(deleteTranID,deleteItemID)){
                    getOrder();
                    systemSetting.showMessage(SystemSetting.SUCCESS,"Item Deleted!",context,getLayoutInflater());
                }
                passwordRequireDialog.dismiss();
            }
        });
    }

    private void setLayoutResource(){
        lvViewOrder =(ListView)findViewById(R.id.lvViewOrder);
        tvTable=(TextView)findViewById(R.id.tvTable);
        tvTax=(TextView)findViewById(R.id.tvTax);
        tvCharges =(TextView)findViewById(R.id.tvCharges);
        tvSubTotal=(TextView)findViewById(R.id.tvSubTotal);
        tvGrandTotal =(TextView)findViewById(R.id.tvGrandTotal);
        tvLabelTax=(TextView)findViewById(R.id.tvLabelTax);
        tvLabelCharges =(TextView)findViewById(R.id.tvLabelCharges);
        tvLabelSubTotal=(TextView)findViewById(R.id.tvLabelSubTotal);
        tvLabelGrandTotal =(TextView)findViewById(R.id.tvLabelGrandTotal);
        tvHeaderAmount =(TextView)findViewById(R.id.tvHeaderAmount);
        tvHeaderItem=(TextView)findViewById(R.id.tvHeaderItem);
        tvHeaderQuantity =(TextView)findViewById(R.id.tvHeaderQuantity);
        layoutAmount =(TableLayout)findViewById(R.id.layoutAmount);
    }
}
