package com.bosictsolution.waiterone;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapter.OpenOrderListAdapter;
import common.DBHelper;
import data.OpenOrderData;
import data.WaiterData;

public class OpenOrderActivity extends AppCompatActivity {

    ListView lvMainList;
    TextView tvWaiter;
    OpenOrderListAdapter openOrderListAdapter;
    DBHelper db;
    List<WaiterData> lstWaiterData;
    List<OpenOrderData> lstOpenOrderData;
    private Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_order);

        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        lvMainList=(ListView) findViewById(R.id.lvMainList);
        tvWaiter=(TextView)findViewById(R.id.tvWaiter);
        tvWaiter.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ARIALN.TTF"));
        getTempData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTempData(){
        lstWaiterData=new ArrayList<>();
        lstOpenOrderData=new ArrayList<>();
        Cursor curWaiter=db.getWaiterForOpenOrder();
        while(curWaiter.moveToNext()){
            WaiterData data=new WaiterData();
            data.setWaiterid(curWaiter.getInt(0));
            data.setWaiterName(curWaiter.getString(1));
            lstWaiterData.add(data);
        }
        Cursor curOpenOrder=db.getOpenOrder();
        while ((curOpenOrder.moveToNext())){
            OpenOrderData data=new OpenOrderData();
            data.setTranid(curOpenOrder.getInt(0));
            data.setWaiterid(curOpenOrder.getInt(1));
            data.setDate(curOpenOrder.getString(2));
            data.setTable(curOpenOrder.getString(3));
            data.setGuest(curOpenOrder.getInt(4));
            data.setTime(curOpenOrder.getString(5));
            lstOpenOrderData.add(data);
        }
        openOrderListAdapter=new OpenOrderListAdapter(this,lstWaiterData,lstOpenOrderData);
        lvMainList.setAdapter(openOrderListAdapter);
    }
}
