package com.bosictsolution.waiterone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andprn.jpos.command.ESCPOSConst;
import com.andprn.jpos.printer.ESCPOSPrinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import common.DBHelper;
import common.SystemSetting;
import data.TransactionData;

public class RpDayEndActivity extends AppCompatActivity {

    TextView tvReportName,tvShopName,tvShopDesc,tvPrintDate,tvHeaderSlipID,tvHeaderTotalAmt,tvHeaderTax,tvHeaderCharges,tvHeaderDis,
            tvHeaderNetAmt,tvTotalAmt,tvTax,tvCharges,tvDis,tvNetAmt,tvSubTotal,tvCommercialTax,tvServiceCharges,tvDiscount,tvGrandTotal;
    LinearLayout layoutPrint, layoutPrintList;

    DBHelper db;

    int tabletSize,interfaceId;
    private ESCPOSPrinter posPtr;
    private Calendar calendar;
    String date,time;
    final Context context = this;
    public static List<TransactionData> lstTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_day_end);

        Intent i=getIntent();
        interfaceId=i.getIntExtra("InterfaceID",0);

        Configuration config=getResources().getConfiguration();
        if(config.smallestScreenWidthDp<=600){
            tabletSize=8;
        }else{
            tabletSize=10;
        }

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        db = new DBHelper(this);
        calendar=Calendar.getInstance();
        setTitle("Day End Report");
        setLayoutResource();
        startupPrintLayout();
        setupPrintList();
    }

    private void changePrintLayoutToBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(layoutPrint.getWidth(), layoutPrint.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layoutPrint.draw(canvas);
        savePrintLayoutToWaiterOneDB(bitmap);
        try {
            print(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String savePrintLayoutToWaiterOneDB(Bitmap bitmapImage){
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logoPath=new File(directory,"day_end.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(logoPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG,100,fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public void print(Context context) throws IOException
    {
        posPtr = new ESCPOSPrinter();
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath=new File(directory,"day_end.png");
        String receiptPath=filePath.toString();
        posPtr.printBitmap(receiptPath, ESCPOSConst.ALIGNMENT_CENTER);
        posPtr.lineFeed(4);
        posPtr.cutPaper();
    }

    private void setupPrintList() {
        lstTransaction=new ArrayList<>();
        double subTotal=0,taxTotal=0,chargesTotal=0,disTotal=0,grandTotal=0;
        Cursor cur=db.getReportDayEnd();
        while(cur.moveToNext()){
            TransactionData data=new TransactionData();
            data.setSlipid(cur.getInt(0));
            data.setSubTotal(cur.getDouble(1));
            data.setTax(cur.getDouble(2));
            data.setCharges(cur.getDouble(3));
            data.setDiscount(cur.getDouble(4));
            data.setGrandTotal(cur.getDouble(5));
            lstTransaction.add(data);
        }
        for (int i = 0; i < lstTransaction.size(); i++) {
            TransactionData data = lstTransaction.get(i);
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.list_rp_day_end_child, null);
            TextView tvSlipID = (TextView) row.findViewById(R.id.tvSlipID);
            TextView tvTotalAmt = (TextView) row.findViewById(R.id.tvTotalAmt);
            TextView tvTax = (TextView) row.findViewById(R.id.tvTax);
            TextView tvCharges = (TextView) row.findViewById(R.id.tvCharges);
            TextView tvDis = (TextView) row.findViewById(R.id.tvDis);
            TextView tvNetAmt = (TextView) row.findViewById(R.id.tvNetAmt);

            if (tabletSize == 10) {
                tvSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            } else {
                tvSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            }

            tvSlipID.setText(String.valueOf(data.getSlipid()));
            String totalAmt = new DecimalFormat("#").format(data.getSubTotal());
            tvTotalAmt.setText(totalAmt);
            String tax = new DecimalFormat("#").format(data.getTax());
            tvTax.setText(tax);
            String charges = new DecimalFormat("#").format(data.getCharges());
            tvCharges.setText(charges);
            String dis = new DecimalFormat("#").format(data.getDiscount());
            tvDis.setText(dis);
            String netAmt = new DecimalFormat("#").format(data.getGrandTotal());
            tvNetAmt.setText(netAmt);
            layoutPrintList.addView(row);
        }
        for(int i=0;i<lstTransaction.size();i++){
            subTotal+=lstTransaction.get(i).getSubTotal();
            taxTotal+=lstTransaction.get(i).getTax();
            chargesTotal+=lstTransaction.get(i).getCharges();
            disTotal+=lstTransaction.get(i).getDiscount();
            grandTotal+=lstTransaction.get(i).getGrandTotal();
        }
        tvTotalAmt.setText(String.valueOf(new DecimalFormat("#,###").format(subTotal)));
        tvTax.setText(String.valueOf(new DecimalFormat("#,###").format(taxTotal)));
        tvCharges.setText(String.valueOf(new DecimalFormat("#,###").format(chargesTotal)));
        tvDis.setText(String.valueOf(new DecimalFormat("#,###").format(disTotal)));
        tvNetAmt.setText(String.valueOf(new DecimalFormat("#,###").format(grandTotal)));
    }

    private void startupPrintLayout(){
        Cursor cur=db.getVoucherSetting();
        if(cur.moveToFirst()){
            if(cur.getString(0).length()==0){
                tvShopName.setVisibility(View.GONE);
            }
            else {
                tvShopName.setText(cur.getString(0));
            }
            if(cur.getString(1).length()==0){
                tvShopDesc.setVisibility(View.GONE);
            }
            else {
                tvShopDesc.setText(cur.getString(1));
            }
        }

        calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        date=dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat=new SimpleDateFormat(SystemSetting.TIME_FORMAT);
        time=timeFormat.format(calendar.getTime());
        tvPrintDate.setText("Printed : "+date+" "+time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.menu_print, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        if (itemId == R.id.menuPrint) {
            Intent i=new Intent(context,RpDayEndPrintActivity.class);
            i.putExtra("InterfaceID",interfaceId);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLayoutResource(){
        layoutPrint =(LinearLayout)findViewById(R.id.layoutPrint);
        layoutPrintList =(LinearLayout)findViewById(R.id.layoutPrintList);
        tvReportName=(TextView)findViewById(R.id.tvReportName);
        tvShopName=(TextView)findViewById(R.id.tvShopName);
        tvShopDesc=(TextView)findViewById(R.id.tvShopDesc);
        tvPrintDate=(TextView)findViewById(R.id.tvPrintDate);
        tvHeaderSlipID=(TextView)findViewById(R.id.tvHeaderSlipID);
        tvHeaderTotalAmt=(TextView)findViewById(R.id.tvHeaderTotalAmt);
        tvHeaderTax=(TextView)findViewById(R.id.tvHeaderTax);
        tvHeaderCharges=(TextView)findViewById(R.id.tvHeaderCharges);
        tvHeaderDis=(TextView)findViewById(R.id.tvHeaderDis);
        tvHeaderNetAmt=(TextView)findViewById(R.id.tvHeaderNetAmt);
        tvTotalAmt=(TextView)findViewById(R.id.tvTotalAmt);
        tvTax=(TextView)findViewById(R.id.tvTax);
        tvCharges=(TextView)findViewById(R.id.tvCharges);
        tvDis=(TextView)findViewById(R.id.tvDis);
        tvNetAmt=(TextView)findViewById(R.id.tvNetAmt);
        tvSubTotal = (TextView) findViewById(R.id.tvSubTotal);
        tvCommercialTax = (TextView) findViewById(R.id.tvCommercialTax);
        tvServiceCharges = (TextView) findViewById(R.id.tvServiceCharges);
        tvDiscount = (TextView) findViewById(R.id.tvDiscount);
        tvGrandTotal = (TextView) findViewById(R.id.tvGrandTotal);

        if(tabletSize==10){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutPrint.getLayoutParams();
            params.width = 640;
            tvReportName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_title_10));
            tvShopName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvShopDesc.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvPrintDate.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvHeaderSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
            tvHeaderTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
            tvHeaderTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
            tvHeaderCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
            tvHeaderDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
            tvHeaderNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
            tvTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvSubTotal.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvCommercialTax.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvServiceCharges.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvDiscount.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvGrandTotal.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
        }else{
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutPrint.getLayoutParams();
            params.width = 640;
            tvReportName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_title_8));
            tvShopName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvShopDesc.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvPrintDate.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvHeaderSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
            tvHeaderTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
            tvHeaderTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
            tvHeaderCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
            tvHeaderDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
            tvHeaderNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
            tvTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvSubTotal.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvCommercialTax.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvServiceCharges.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvDiscount.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvGrandTotal.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
        }
    }
}
