package com.bosictsolution.waiterone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andprn.jpos.command.ESCPOSConst;
import com.andprn.jpos.printer.ESCPOSPrinter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import common.DBHelper;
import common.SystemSetting;
import data.TransactionData;

public class XPrintLayoutActivity extends AppCompatActivity {

    ImageView imgLogo;
    TextView tvShopName, tvShopDesc, tvAddress, tvPhone, tvSlipID, tvDateTime,
            tvTable, tvWaiter, tvHeaderItem, tvHeaderQty, tvHeaderPrice, tvHeaderAmount, tvSubTotal, tvSubTotalAmt, tvCommercialTax,
            tvCommercialTaxAmt, tvServiceCharges, tvServiceChargesAmt, tvDiscount,
            tvDiscountAmt, tvGrandTotal, tvGrandTotalAmt, tvPaid,
            tvPaidAmt, tvChange, tvChangeAmt, tvMessage, tvOtherMessage;
    LinearLayout layoutPrint,layoutPrintList;
    Button btnPrint;
    final Context context = this;
    private DBHelper db;
    private Calendar calendar;
    String date,time,billTableName,billWaiterName;
    int billTableID,billSlipID;
    String subtotal,tax,charges,grandtotal,discount,paid,change;
    private ESCPOSPrinter posPtr;
    float floatQty;
    int tabletSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xprint_layout);

        Configuration config=getResources().getConfiguration();
        if(config.smallestScreenWidthDp<=600){
            tabletSize=8;
        }else{
            tabletSize=10;
        }

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        billSlipID=intent.getIntExtra("slipid",0);
        billTableID=intent.getIntExtra("tableid",0);
        billTableName=intent.getStringExtra("table");
        billWaiterName=intent.getStringExtra("waiter");
        subtotal=intent.getStringExtra("subtotal");
        tax=intent.getStringExtra("tax");
        charges=intent.getStringExtra("charges");
        grandtotal=intent.getStringExtra("grandtotal");
        discount=intent.getStringExtra("discount");
        paid=intent.getStringExtra("paid");
        change=intent.getStringExtra("change");

        db=new DBHelper(this);
        calendar=Calendar.getInstance();

        setLayoutResource();
        startupPrintLayout();
        setupPrintOrderList(BillActivity.lstViewOrder);
        printCalculateAmount();

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePrintLayoutToBitmap();
                finish();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnPrint.performClick();
            }
        }, 200);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLayoutResource(){
        layoutPrint =(LinearLayout)findViewById(R.id.layoutPrint);
        imgLogo =(ImageView)findViewById(R.id.imgLogo);
        layoutPrintList=(LinearLayout)findViewById(R.id.layoutPrintList);
        tvShopName =(TextView)findViewById(R.id.tvShopName);
        tvShopDesc =(TextView)findViewById(R.id.tvShopDesc);
        tvAddress =(TextView)findViewById(R.id.tvAddress);
        tvPhone =(TextView)findViewById(R.id.tvPhone);
        tvSlipID =(TextView)findViewById(R.id.tvSlipID);
        tvDateTime =(TextView)findViewById(R.id.tvDateTime);
        tvTable =(TextView)findViewById(R.id.tvTable);
        tvWaiter =(TextView)findViewById(R.id.tvUser);
        tvHeaderItem =(TextView)findViewById(R.id.tvHeaderItem);
        tvHeaderQty =(TextView)findViewById(R.id.tvHeaderQty);
        tvHeaderPrice =(TextView)findViewById(R.id.tvHeaderPrice);
        tvHeaderAmount =(TextView)findViewById(R.id.tvHeaderAmount);
        tvSubTotal =(TextView)findViewById(R.id.tvSubTotal);
        tvSubTotalAmt =(TextView)findViewById(R.id.tvSubTotalAmt);
        tvCommercialTax =(TextView)findViewById(R.id.tvCommercialTax);
        tvCommercialTaxAmt =(TextView)findViewById(R.id.tvCommercialTaxAmt);
        tvServiceCharges =(TextView)findViewById(R.id.tvServiceCharges);
        tvServiceChargesAmt =(TextView)findViewById(R.id.tvServiceChargesAmt);
        tvDiscount =(TextView)findViewById(R.id.tvDiscount);
        tvDiscountAmt =(TextView)findViewById(R.id.tvDiscountAmt);
        tvGrandTotal =(TextView)findViewById(R.id.tvGrandTotal);
        tvGrandTotalAmt =(TextView)findViewById(R.id.tvGrandTotalAmt);
        tvPaid =(TextView)findViewById(R.id.tvPaid);
        tvPaidAmt =(TextView)findViewById(R.id.tvPaidAmt);
        tvChange =(TextView)findViewById(R.id.tvChange);
        tvChangeAmt =(TextView)findViewById(R.id.tvChangeAmt);
        tvMessage =(TextView)findViewById(R.id.tvMessage);
        tvOtherMessage =(TextView)findViewById(R.id.tvOtherMessage);
        btnPrint=(Button)findViewById(R.id.btnPrint);

        if(tabletSize==10){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutPrint.getLayoutParams();
            params.width = 600;
            tvShopName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_title_10));
            tvShopDesc.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvAddress.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvPhone.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvDateTime.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvTable.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvWaiter.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvHeaderItem.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvHeaderQty.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvHeaderPrice.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvHeaderAmount.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvSubTotal.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvSubTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvCommercialTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvCommercialTaxAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvServiceCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvServiceChargesAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvDiscount.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvDiscountAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvPaid.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvPaidAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvChange.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvChangeAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvMessage.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvOtherMessage.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvGrandTotal.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_grand_amount_10));
            tvGrandTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_grand_amount_10));
        }else{
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutPrint.getLayoutParams();
            params.width = 600;
            tvShopName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_title_8));
            tvShopDesc.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvAddress.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvPhone.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvDateTime.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvTable.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvWaiter.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvHeaderItem.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvHeaderQty.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvHeaderPrice.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvHeaderAmount.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvSubTotal.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvSubTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvCommercialTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvCommercialTaxAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvServiceCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvServiceChargesAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvDiscount.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvDiscountAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvPaid.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvPaidAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvChange.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvChangeAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvMessage.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvOtherMessage.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvGrandTotal.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_grand_amount_8));
            tvGrandTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_grand_amount_8));
        }
    }

    private void changePrintLayoutToBitmap(){
        Bitmap bitmap=Bitmap.createBitmap(layoutPrint.getWidth(), layoutPrint.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        layoutPrint.draw(canvas);
        savePrintLayoutToWaiterOneDB(bitmap);
        try{
            print(context);
            print(context);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String savePrintLayoutToWaiterOneDB(Bitmap bitmapImage){
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logoPath=new File(directory,"printNetworkPrinter.png");

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

    private void startupPrintLayout(){
        try {
            File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File f=new File(directory, "shoplogo.jpg");
            if(f.exists()) {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                imgLogo.setImageBitmap(b);
                imgLogo.setVisibility(View.VISIBLE);
            }else{
                imgLogo.setVisibility(View.GONE);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
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
            if(cur.getString(2).length()==0){
                tvPhone.setVisibility(View.GONE);
            }
            else {
                tvPhone.setText("PH:"+cur.getString(2));
            }
            if(cur.getString(3).length()==0){
                tvMessage.setVisibility(View.GONE);
            }
            else {
                tvMessage.setText(cur.getString(3));
            }
            if(cur.getString(4).length()==0){
                tvAddress.setVisibility(View.GONE);
            }
            else {
                tvAddress.setText(cur.getString(4));
            }
            if(cur.getString(5).length()==0){
                tvOtherMessage.setVisibility(View.GONE);
            }
            else {
                tvOtherMessage.setText(cur.getString(5));
            }
        }

        calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        date=dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat=new SimpleDateFormat(SystemSetting.TIME_FORMAT);
        time=timeFormat.format(calendar.getTime());
        tvDateTime.setText(date+"  "+time);
        tvTable.setText("Table:"+billTableName);
        tvWaiter.setText("User:"+billWaiterName);
        tvSlipID.setText("Slip No:"+billSlipID);
    }

    private void setupPrintOrderList(List<TransactionData> lstViewOrder){
        for (int i=0; i<lstViewOrder.size(); i++) {
            TransactionData data = lstViewOrder.get(i);
            LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.list_print, null);
            TextView tvItemName =(TextView) row.findViewById(R.id.tvPrintListItem);
            TextView tvQuantity =(TextView) row.findViewById(R.id.tvPrintListQty);
            TextView tvPrice =(TextView) row.findViewById(R.id.tvPrintListPrice);
            TextView tvAmount =(TextView) row.findViewById(R.id.tvPrintListAmount);

            if(tabletSize==10) {
                tvItemName.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvQuantity.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvPrice.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            }else{
                tvItemName.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvQuantity.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvPrice.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            }

            tvItemName.setText(data.getItemName());
            floatQty = Float.parseFloat(data.getStringQty());
            if(floatQty==Math.round(floatQty)){
                tvQuantity.setText(String.valueOf(data.getIntegerQty()));
            }else{
                tvQuantity.setText(String.valueOf(data.getFloatQty()));
            }
            String price=new DecimalFormat("#,###").format(data.getSalePrice());
            tvPrice.setText(price);
            String amount=new DecimalFormat("#,###").format(data.getAmount());
            tvAmount.setText(amount);
            layoutPrintList.addView(row);
        }
    }

    private void printCalculateAmount(){
        tvSubTotalAmt.setText(String.valueOf(new DecimalFormat("#,###").format(Double.parseDouble(subtotal))));
        tvCommercialTaxAmt.setText(String.valueOf(new DecimalFormat("#,###").format(Double.parseDouble(tax))));
        tvServiceChargesAmt.setText(String.valueOf(new DecimalFormat("#,###").format(Double.parseDouble(charges))));
        tvDiscountAmt.setText(String.valueOf(new DecimalFormat("#,###").format(Double.parseDouble(discount))));
        tvGrandTotalAmt.setText(String.valueOf(new DecimalFormat("#,###").format(Double.parseDouble(grandtotal))));
        tvPaidAmt.setText(String.valueOf(new DecimalFormat("#,###").format(Double.parseDouble(paid))));
        tvChangeAmt.setText(String.valueOf(new DecimalFormat("#,###").format(Double.parseDouble(change))));
    }

    public void print(Context context) throws IOException
    {
        posPtr = new ESCPOSPrinter();
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath=new File(directory,"printNetworkPrinter.png");
        String receiptPath=filePath.toString();
        posPtr.printBitmap(receiptPath, ESCPOSConst.ALIGNMENT_CENTER);
        posPtr.lineFeed(4);
        posPtr.cutPaper();
    }
}
