package com.bosictsolution.waiterone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapter.DgChoosePrinterListAdapter;
import common.DBHelper;
import common.FeatureList;
import common.SystemSetting;
import data.PrinterData;
import listener.PrinterChoiceListener;

public class MainActivity extends AppCompatActivity implements PrinterChoiceListener {

    private DBHelper db;
    LinearLayout layoutSale,layoutReport,layoutSystem,layoutOpenOrder;
    TextView tvSale,tvReport,tvSystem,tvOpenOrder;
    ListView lvPrinter;
    final Context context = this;
    int waiter_id,interfaceId;
    String waiter_name,printer_message,printerAddress="";
    DgChoosePrinterListAdapter dgChoosePrinterListAdapter;
    List<PrinterData> lstPrinter;
    SystemSetting systemSetting=new SystemSetting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLayoutResource();

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);

        Intent intent=getIntent();
        waiter_id=intent.getIntExtra("waiterid", 0);
        waiter_name=intent.getStringExtra("waitername");

        db=new DBHelper(this);

        authorizedModule();

        layoutSale.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(getApplicationContext(),SaleActivity.class);
                intent.putExtra("waiterid", waiter_id);
                intent.putExtra("waitername", waiter_name);
                startActivity(intent);
            }
        });
        layoutReport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(db.getFeatureResult(FeatureList.fUseMultiPrinter)!=1) {
                    Intent intent = new Intent(getApplicationContext(), ReportListActivity.class);
                    startActivity(intent);
                }else{
                    interfaceId=0;
                    printerAddress="";
                    showPrinterDialog();
                }
            }
        });
        layoutSystem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(getApplicationContext(),SystemActivity.class);
                startActivity(intent);
            }
        });
        layoutOpenOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(getApplicationContext(),OpenOrderActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPrinterChoiceListener(int position){
        interfaceId=lstPrinter.get(position).getInterfaceId();
        printerAddress=lstPrinter.get(position).getPrinterAddress();
    }

    @Override
    public void onPrinterUnChoiceListener(int position){
        interfaceId=0;
        printerAddress="";
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void setTitle(CharSequence title){
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi=inflater.inflate(R.layout.actionbar_main, null);
        TextView tvShopName=(TextView)vi.findViewById(R.id.tvShopName);
        TextView tvPrinterMessage=(TextView)vi.findViewById(R.id.tvPrinterMessage);
        Button btnConnect=(Button)vi.findViewById(R.id.btnConnect);

        tvPrinterMessage.setText(printer_message);
        if(printer_message.equals("PRINTER ONLINE!")){
            tvPrinterMessage.setTextColor(Color.GREEN);
            tvPrinterMessage.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.happy, 0, 0, 0);
            tvPrinterMessage.setCompoundDrawablePadding(10);
            btnConnect.setVisibility(View.INVISIBLE);
        }
        else {
            tvPrinterMessage.setTextColor(Color.RED);
            tvPrinterMessage.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.sad, 0, 0, 0);
            tvPrinterMessage.setCompoundDrawablePadding(10);
            btnConnect.setVisibility(View.VISIBLE);
        }
        Cursor cur_shop_name=db.getShopName();
        if(cur_shop_name.moveToFirst())tvShopName.setText(cur_shop_name.getString(0));

        tvPrinterMessage.setVisibility(View.INVISIBLE);
        btnConnect.setVisibility(View.INVISIBLE);

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT);
        getSupportActionBar().setCustomView(vi, params);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**wifiPort = PrinterWiFiPort.getInstance();
                connectPrinter();**/
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if (itemId == R.id.menuLogout) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLayoutResource(){
        tvSale=(TextView) findViewById(R.id.tvSale);
        tvReport=(TextView)findViewById(R.id.tvReport);
        tvSystem=(TextView)findViewById(R.id.tvSystem);
        tvOpenOrder=(TextView)findViewById(R.id.tvOpenOrder);
        layoutSale=(LinearLayout)findViewById(R.id.layoutSale);
        layoutReport=(LinearLayout)findViewById(R.id.layoutReport);
        layoutSystem=(LinearLayout)findViewById(R.id.layoutSystem);
        layoutOpenOrder=(LinearLayout)findViewById(R.id.layoutOpenOrder);

        layoutSale.setEnabled(false);
        layoutSale.setBackgroundColor(getResources().getColor(R.color.colorGray));
        layoutReport.setEnabled(false);
        layoutReport.setBackgroundColor(getResources().getColor(R.color.colorGray));
        layoutSystem.setEnabled(false);
        layoutSystem.setBackgroundColor(getResources().getColor(R.color.colorGray));
        layoutOpenOrder.setEnabled(false);
        layoutOpenOrder.setBackgroundColor(getResources().getColor(R.color.colorGray));
    }

    private void authorizedModule(){
        Cursor cur=db.getModuleByUserID(waiter_id);
        while(cur.moveToNext()){
            String name=cur.getString(1);
            if(name.equals("SALE")) {
                layoutSale.setEnabled(true);
                layoutSale.setBackgroundColor(getResources().getColor(R.color.colorFirstPri));
            }
            else if(name.equals("OPEN ORDER")){
                layoutOpenOrder.setEnabled(true);
                layoutOpenOrder.setBackgroundColor(getResources().getColor(R.color.colorFirstPri));

            }
            else if(name.equals("REPORT")){
                layoutReport.setEnabled(true);
                layoutReport.setBackgroundColor(getResources().getColor(R.color.colorFirstPri));

            }
            else if(name.equals("SYSTEM")){
                layoutSystem.setEnabled(true);
                layoutSystem.setBackgroundColor(getResources().getColor(R.color.colorFirstPri));

            }
        }
    }

    private void showPrinterDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View vi=reg.inflate(R.layout.dg_choose_printer, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(vi);

        final TextView tvLabelChoosePrinter=(TextView)vi.findViewById(R.id.tvLabelChoosePrinter);
        lvPrinter=(ListView)vi.findViewById(R.id.lvPrinter);
        final Button btnOk=(Button)vi.findViewById(R.id.btnOk);

        getPrinter();

        dialog.setCancelable(true);
        final AlertDialog dialog1=dialog.create();
        dialog1.show();

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(lstPrinter.size()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Set Printer!",context,getLayoutInflater());
                    dialog1.dismiss();
                    return;
                }
                if(printerAddress.length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Choose Printer!",context,getLayoutInflater());
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), ReportListActivity.class);
                intent.putExtra("InterfaceID",interfaceId);
                intent.putExtra("PrinterAddress",printerAddress);
                startActivity(intent);
                dialog1.dismiss();
            }
        });
    }

    private void getPrinter(){
        lstPrinter=new ArrayList<>();
        Cursor cur=db.getPrinter80();
        while(cur.moveToNext()){
            PrinterData data=new PrinterData();
            data.setPrinterId(cur.getInt(0));
            data.setPrinterName(cur.getString(1));
            data.setModelId(cur.getInt(2));
            data.setInterfaceId(cur.getInt(3));
            data.setPrinterAddress(cur.getString(4));
            data.setWidthId(cur.getInt(5));
            data.setModelName(cur.getString(6));
            data.setInterfaceName(cur.getString(7));
            data.setWidthName(cur.getString(8));
            lstPrinter.add(data);
        }

        if(lstPrinter.size()==0){
            List<String> list=new ArrayList<>();
            list.add("No 80mm Printer");
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,list);
            lvPrinter.setAdapter(arrayAdapter);
        }else {
            dgChoosePrinterListAdapter = new DgChoosePrinterListAdapter(this, lstPrinter);
            lvPrinter.setAdapter(dgChoosePrinterListAdapter);
            dgChoosePrinterListAdapter.setCustomChoiceListener(this);
        }
    }
}
