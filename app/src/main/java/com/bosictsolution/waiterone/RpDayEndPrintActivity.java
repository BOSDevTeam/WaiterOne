package com.bosictsolution.waiterone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.andprn.jpos.command.ESCPOSConst;
import com.andprn.jpos.printer.ESCPOSPrinter;
import com.bosictsolution.waiterone.bt.BtUtil;
import com.bosictsolution.waiterone.print.GPrinterCommand;
import com.bosictsolution.waiterone.print.PrintPic;
import com.bosictsolution.waiterone.print.PrintQueue;
import com.bosictsolution.waiterone.print.PrintUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.BtDeviceListAdapter;
import adapter.RpDayEndParentListAdapter;
import common.DBHelper;
import common.SystemSetting;
import data.RpDayEndData;
import data.TransactionData;

public class RpDayEndPrintActivity extends AppCompatActivity {

    LinearLayout layoutPrint;
    Button btnPrint;
    RpDayEndParentListAdapter rpDayEndParentListAdapter;
    int tabletSize,interfaceId;
    private final int PRINT_TRAN_SIZE=30;
    DBHelper db;
    private Calendar calendar;
    String date,time,shopName,description,printDate;
    double subTotal=0,taxTotal=0,chargesTotal=0,disTotal=0,grandTotal=0;
    private ESCPOSPrinter posPtr;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_day_end_print);

        Intent i=getIntent();
        interfaceId=i.getIntExtra("InterfaceID",0);

        layoutPrint=(LinearLayout) findViewById(R.id.layoutPrint);
        btnPrint=(Button) findViewById(R.id.btnPrint);

        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp <= 600) {
            tabletSize = 8;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutPrint.getLayoutParams();
            params.width = 640;
        } else {
            tabletSize = 10;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutPrint.getLayoutParams();
            params.width = 640;
        }

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        db = new DBHelper(this);
        calendar= Calendar.getInstance();
        setTitle("Day End Report");
        getData();
        createPrintData();

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
        }, 5000);
    }

    private void changePrintLayoutToBitmap() {
        for (int i = 0; i < rpDayEndParentListAdapter.getCount() ; i++) {
            View v = layoutPrint.getChildAt(i);
            if (v != null) {
                Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                v.draw(canvas);
                savePrintLayoutToWaiterOneDB(bitmap);
                if(interfaceId==1) { //Network
                    try {
                        print(context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(interfaceId==2){ //Bluetooth
                    printBitmapOrder();
                }
            }
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

    private void printBitmapOrder() {
        Bitmap bitmap=null;
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath=new File(directory,"day_end.png");
        if(filePath.exists()) bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
        PrintPic printPic =PrintPic.getInstance();
        printPic.length=0;
        printPic.init(bitmap);
        if (null != bitmap) {
            if (bitmap.isRecycled()) {
                bitmap = null;
            } else {
                bitmap.recycle();
                bitmap = null;
            }
        }

        byte[] bytes = printPic.printDraw();
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.reset);
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
        printBytes.add(bytes);
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
        printBytes.add(GPrinterCommand.cut);
        PrintQueue.getQueue(getApplicationContext()).add(printBytes);
    }

    private void getData(){
        Cursor cur=db.getVoucherSetting();
        if(cur.moveToFirst()){
            if(cur.getString(0).length()!=0)
                shopName=cur.getString(0);
            if(cur.getString(1).length()!=0)
                description=cur.getString(1);

        }
        calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        date=dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat=new SimpleDateFormat(SystemSetting.TIME_FORMAT);
        time=timeFormat.format(calendar.getTime());
        printDate="Printed : "+date+" "+time;
    }

    private void createPrintData(){
        for(int i = 0; i< RpDayEndActivity.lstTransaction.size(); i++){
            subTotal+= RpDayEndActivity.lstTransaction.get(i).getSubTotal();
            taxTotal+= RpDayEndActivity.lstTransaction.get(i).getTax();
            chargesTotal+= RpDayEndActivity.lstTransaction.get(i).getCharges();
            disTotal+= RpDayEndActivity.lstTransaction.get(i).getDiscount();
            grandTotal+= RpDayEndActivity.lstTransaction.get(i).getGrandTotal();
        }

        List<RpDayEndData> lstData=new ArrayList<>();

        while(RpDayEndActivity.lstTransaction.size()>PRINT_TRAN_SIZE) {
            List<TransactionData> lstTran=new ArrayList<>();
            for (int i = 0; i < PRINT_TRAN_SIZE; i++) {
                TransactionData data = RpDayEndActivity.lstTransaction.get(i);
                TransactionData curData = new TransactionData();
                curData.setSlipid(data.getSlipid());
                curData.setSubTotal( data.getSubTotal());
                curData.setTax(data.getTax());
                curData.setCharges(data.getCharges());
                curData.setDiscount(data.getDiscount());
                curData.setGrandTotal(data.getGrandTotal());
                lstTran.add(curData);
            }

            for (int i = 0; i < PRINT_TRAN_SIZE; i++) {
                RpDayEndActivity.lstTransaction.remove(0);
            }

            RpDayEndData data=new RpDayEndData();
            data.setReportName("End of Day Report");
            data.setShopName(shopName);
            data.setShopDesp(description);
            data.setPrintDate(printDate);
            data.setSlipHeader("Slip");
            data.setTotalHeader("SubTotal");
            data.setTaxHeader("Tax");
            data.setChargesHeader("Charges");
            data.setDisHeader("Dis");
            data.setNetHeader("NetAmt");
            data.setLstTran(lstTran);
            data.setTotalAmt(subTotal);
            data.setTax(taxTotal);
            data.setCharges(chargesTotal);
            data.setDiscount(disTotal);
            data.setNetAmt(grandTotal);
            lstData.add(data);
        }

        if(RpDayEndActivity.lstTransaction.size()<=PRINT_TRAN_SIZE) {
            List<TransactionData> lstTran=new ArrayList<>();
            for (int i = 0; i < RpDayEndActivity.lstTransaction.size(); i++) {
                TransactionData data = RpDayEndActivity.lstTransaction.get(i);
                TransactionData curData = new TransactionData();
                curData.setSlipid(data.getSlipid());
                curData.setSubTotal( data.getSubTotal());
                curData.setTax(data.getTax());
                curData.setCharges(data.getCharges());
                curData.setDiscount(data.getDiscount());
                curData.setGrandTotal(data.getGrandTotal());
                lstTran.add(curData);
            }
            RpDayEndData data=new RpDayEndData();
            data.setReportName("End of Day Report");
            data.setShopName(shopName);
            data.setShopDesp(description);
            data.setPrintDate(printDate);
            data.setSlipHeader("Slip");
            data.setTotalHeader("SubTotal");
            data.setTaxHeader("Tax");
            data.setChargesHeader("Charges");
            data.setDisHeader("Dis");
            data.setNetHeader("NetAmt");
            data.setLstTran(lstTran);
            data.setTotalAmt(subTotal);
            data.setTax(taxTotal);
            data.setCharges(chargesTotal);
            data.setDiscount(disTotal);
            data.setNetAmt(grandTotal);
            lstData.add(data);
        }
        rpDayEndParentListAdapter =new RpDayEndParentListAdapter(this,lstData);
        final int adapterCount = rpDayEndParentListAdapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View item = rpDayEndParentListAdapter.getView(i, null, null);
            layoutPrint.addView(item);
        }
    }
}
