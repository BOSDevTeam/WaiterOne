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
import android.os.AsyncTask;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import adapter.BtDeviceListAdapter;
import adapter.PrintBillParentListAdapter;
import common.DBHelper;
import common.FeatureList;
import common.PaperWidth;
import common.PrinterInterface;
import common.SystemSetting;
import data.PrintBillData;
import data.PrinterData;
import data.TransactionData;

public class PrintBillActivity extends AppCompatActivity {

    LinearLayout layoutPrint80,layoutPrint58;
    Button btnPrint;
    TextView tvPrinting;

    public static BluetoothAdapter BA;
    private BtDeviceListAdapter deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private ESCPOSPrinter posPtr;

    PrintBillParentListAdapter printBillParentListAdapter;
    private final int PRINT_TRAN_SIZE=20;
    DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    private Calendar calendar;
    final Context context = this;

    List<PrinterData> lstReceiptPrinter;
    List<TransactionData> lstOrder80=new ArrayList<>(),lstOrder58=new ArrayList<>();

    String shopName,description,phone,address,message1,message2,billTableName,billWaiterName,tax,charges,discount,
            dateTime,subTotal,grandTotal, paid, change,startTime,endTime,orderNumber;
    int billTableId,slipId;
    boolean isPrintTwo,isAutoPrint, isPrintFromSale;
    int tabletSize,takeAwayType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_bill);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        slipId=intent.getIntExtra("slipid",0);
        billTableId=intent.getIntExtra("tableid",0);
        billTableName=intent.getStringExtra("table");
        billWaiterName=intent.getStringExtra("waiter");
        subTotal=intent.getStringExtra("subtotal");
        tax=intent.getStringExtra("tax");
        charges=intent.getStringExtra("charges");
        grandTotal=intent.getStringExtra("grandtotal");
        discount=intent.getStringExtra("discount");
        paid=intent.getStringExtra("paid");
        change=intent.getStringExtra("change");
        dateTime=intent.getStringExtra("datetime");
        startTime=intent.getStringExtra("StartTime");
        endTime=intent.getStringExtra("EndTime");
        isPrintFromSale =intent.getBooleanExtra("PrintFromSale",false);
        orderNumber=intent.getStringExtra("OrderNumber");
        takeAwayType=intent.getIntExtra("TakeAwayType",0);

        layoutPrint80 =(LinearLayout) findViewById(R.id.layoutPrint);
        layoutPrint58 =(LinearLayout) findViewById(R.id.layoutPrint2);
        btnPrint=(Button) findViewById(R.id.btnPrint);
        tvPrinting=(TextView)findViewById(R.id.tvPrinting);

        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp <= 600) {
            tabletSize = 8;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutPrint80.getLayoutParams();
            params.width = 600;
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) layoutPrint58.getLayoutParams();
            params2.width = 400;
        } else {
            tabletSize = 10;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutPrint80.getLayoutParams();
            params.width = 600;
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) layoutPrint58.getLayoutParams();
            params2.width = 400;
        }

        db = new DBHelper(this);
        BA = BluetoothAdapter.getDefaultAdapter();
        deviceAdapter = new BtDeviceListAdapter(getApplicationContext(), null);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        calendar= Calendar.getInstance();

        if(db.getFeatureResult(FeatureList.fPrint2)==1)isPrintTwo=true;

        getData();
        getReceiptPrinter();

        if(!isPrintFromSale) {
                if (db.getFeatureResult(FeatureList.fAutoPrint) == 1) {
                    tvPrinting.setVisibility(View.VISIBLE);
                    btnPrint.setVisibility(View.GONE);
                    isAutoPrint = true;
                } else {
                    tvPrinting.setVisibility(View.GONE);
                    btnPrint.setVisibility(View.VISIBLE);
                    isAutoPrint = false;
                }

            lstOrder58=BillActivity.lstViewOrder;
            lstOrder80=BillActivity.lstViewOrder;
            createPrintData80(lstOrder80);
        }else {
            printAfterOrder();
        }

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.getFeatureResult(FeatureList.fUseMultiPrinter)!=1){
                    changePrintLayoutToBitmap();
                    if(takeAwayType!=1)SaleActivity.isTableClear=true;
                    else SaleActivity.isTableClear=false;
                    finish();
                }
                else {
                    btnPrint.setEnabled(false);
                    printByNetworkPrinter();
                    //if(isBluetoothPrint()) {
                        BtPrint btPrint = new BtPrint();
                        btPrint.execute("");
                    //}else{
                        //finish();
                    //}
                }
            }
        });

        if(isAutoPrint) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnPrint.performClick();
                }
            }, 200);
        }
    }

    private void getReceiptPrinter(){
        lstReceiptPrinter=new ArrayList<>();
        Cursor cur=db.getReceiptPrinter();
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
            data.setPrintCount(cur.getInt(9));
            lstReceiptPrinter.add(data);
        }
    }

    private void printAfterOrder() {
        tvPrinting.setVisibility(View.VISIBLE);
        btnPrint.setVisibility(View.GONE);
        layoutPrint80.setVisibility(View.INVISIBLE);

        createPrintData80(SaleActivity.lstOrder);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnPrint.performClick();
            }
        }, 200);
    }

   /** private boolean isBluetoothPrint(){
        boolean result=false;
        for (int i = 0; i < lstReceiptPrinter.size(); i++) {
            String interfaceName = lstReceiptPrinter.get(i).getInterfaceName();
            if (interfaceName.length() != 0) {
                if (interfaceName.equals(PrinterInterface.iBluetooth)) {
                    result=true;
                    break;
                }
            }
        }
        return result;
    }**/

    private void changePrintLayoutToBitmap() {
        for (int i = 0; i < printBillParentListAdapter.getCount() ; i++) {
            View v = layoutPrint80.getChildAt(i);
            if (v != null) {
                Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                v.draw(canvas);
                savePrintLayoutToWaiterOneDB(bitmap);
                try {
                    if(orderNumber==null)orderNumber="";
                    if(isPrintTwo && !orderNumber.equals("")) {
                        print(context, "Printed!");
                        print(context, "Printed!");
                        print(context, "Printed!");
                        print(context, "Printed!");
                    }else if(isPrintTwo){
                        print(context, "Printed!");
                        print(context, "Printed!");
                    }else if(!orderNumber.equals("")){
                        print(context, "Printed!");
                        print(context, "Printed!");
                    } else{
                        print(context,"Printed");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void printByNetworkPrinter() {
        View v;
        int removePosition;
        for (int n = 0; n < lstReceiptPrinter.size(); n++) {
            String interfaceName = lstReceiptPrinter.get(n).getInterfaceName();
            int printCount = lstReceiptPrinter.get(n).getPrintCount();
            String widthName = lstReceiptPrinter.get(n).getWidthName();
            String printerAddress=lstReceiptPrinter.get(n).getPrinterAddress();
            removePosition = n;
            if (interfaceName.equals(PrinterInterface.iEthernet)) {
                for (int i = 0; i < printBillParentListAdapter.getCount(); i++) {
                    if (widthName.equals(PaperWidth.w80)) {
                        v = layoutPrint80.getChildAt(i);
                        if (v != null) {
                            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            v.draw(canvas);
                            savePrintLayoutToWaiterOneDB(bitmap);
                            try {
                                for (int p = 0; p < printCount; p++) {
                                    print(context, "Printed!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (widthName.equals(PaperWidth.w58)) {
                        v = layoutPrint58.getChildAt(i);
                        if (v != null) {
                            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            v.draw(canvas);
                            savePrintLayoutToWaiterOneDB(bitmap);
                            try {
                                for (int p = 0; p < printCount; p++) {
                                    print(context, "Printed!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                lstReceiptPrinter.remove(removePosition);
            }
        }
    }

    private String savePrintLayoutToWaiterOneDB(Bitmap bitmapImage){
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logoPath=new File(directory,"print.png");

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

    public void print(Context context,String msg) throws IOException
    {
        posPtr = new ESCPOSPrinter();
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath=new File(directory,"print.png");
        String receiptPath=filePath.toString();
        posPtr.printBitmap(receiptPath, ESCPOSConst.ALIGNMENT_CENTER);
        posPtr.lineFeed(4);
        posPtr.cutPaper();
        systemSetting.showMessage(SystemSetting.SUCCESS,msg,context,getLayoutInflater());
    }

    private void getData(){
        Cursor cur=db.getVoucherSetting();
        if(cur.moveToFirst()){
            if(cur.getString(0).trim().length()!=0){
                shopName=cur.getString(0);
            }
            if(cur.getString(1).trim().length()!=0){
                description=cur.getString(1);
            }
            if(cur.getString(2).trim().length()!=0){
                phone=cur.getString(2);
            }
            if(cur.getString(4).trim().length()!=0){
                address=cur.getString(4);
            }
            if(cur.getString(3).trim().length()!=0){
                message1=cur.getString(3);
            }
            if(cur.getString(5).trim().length()!=0){
                message2=cur.getString(5);
            }
        }
    }

    private void createPrintData80(List<TransactionData> lstOrder){
        List<PrintBillData> lstData=new ArrayList<>();

        while(lstOrder.size()>PRINT_TRAN_SIZE) {
            List<TransactionData> lstTran=new ArrayList<>();
            for (int i = 0; i < PRINT_TRAN_SIZE; i++) {
                TransactionData data = lstOrder.get(i);
                TransactionData curData = new TransactionData();
                curData.setItemName(data.getItemName());
                curData.setIntegerQty(data.getIntegerQty());
                curData.setFloatQty(data.getFloatQty());
                curData.setStringQty( data.getStringQty());
                curData.setSalePrice(data.getSalePrice());
                curData.setAmount(data.getAmount());

                lstTran.add(curData);
            }

            for (int i = 0; i < PRINT_TRAN_SIZE; i++) {
                lstOrder.remove(0);
            }

            PrintBillData data=new PrintBillData();
            data.setShopName(shopName);
            data.setShopDesp(description);
            data.setAddress(address);
            data.setPhone(phone);
            data.setPrintDate(dateTime);
            data.setSlipNo("Slip No:"+ slipId);
            data.setTable("Table:"+billTableName);
            data.setUser("User:"+billWaiterName);
            data.setSubTotal(Double.parseDouble(subTotal));
            data.setCharges(Double.parseDouble(charges));
            data.setLstTran(lstTran);
            data.setTax(Double.parseDouble(tax));
            data.setDiscount(Double.parseDouble(discount));
            data.setNetAmount(Double.parseDouble(grandTotal));
            data.setPaidAmount(Double.parseDouble(paid));
            data.setChangeAmount(Double.parseDouble(change));
            data.setMessage1(message1);
            data.setMessage2(message2);
            data.setStartTime(startTime);
            data.setEndTime(endTime);
            data.setOrderNumber(orderNumber);
            lstData.add(data);
        }

        if(lstOrder.size()<=PRINT_TRAN_SIZE) {
            List<TransactionData> lstTran=new ArrayList<>();
            for (int i = 0; i < lstOrder.size(); i++) {
                TransactionData data = lstOrder.get(i);
                TransactionData curData = new TransactionData();
                curData.setItemName(data.getItemName());
                curData.setStringQty( data.getStringQty());
                curData.setIntegerQty(data.getIntegerQty());
                curData.setFloatQty(data.getFloatQty());
                curData.setSalePrice(data.getSalePrice());
                curData.setAmount(data.getAmount());

                lstTran.add(curData);
            }
            PrintBillData data=new PrintBillData();
            data.setShopName(shopName);
            data.setShopDesp(description);
            data.setAddress(address);
            data.setPhone(phone);
            data.setPrintDate(dateTime);
            data.setSlipNo("Slip No:"+ slipId);
            data.setTable("Table:"+billTableName);
            data.setUser("User:"+billWaiterName);
            data.setSubTotal(Double.parseDouble(subTotal));
            data.setCharges(Double.parseDouble(charges));
            data.setLstTran(lstTran);
            data.setTax(Double.parseDouble(tax));
            data.setDiscount(Double.parseDouble(discount));
            data.setNetAmount(Double.parseDouble(grandTotal));
            data.setPaidAmount(Double.parseDouble(paid));
            data.setChangeAmount(Double.parseDouble(change));
            data.setMessage1(message1);
            data.setMessage2(message2);
            data.setStartTime(startTime);
            data.setEndTime(endTime);
            data.setOrderNumber(orderNumber);
            lstData.add(data);
        }
        printBillParentListAdapter =new PrintBillParentListAdapter(this,lstData);
        final int adapterCount = printBillParentListAdapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View item = printBillParentListAdapter.getView(i, null, null);
            layoutPrint80.addView(item);
        }

        if(db.getFeatureResult(FeatureList.fUseMultiPrinter)==1){
            for (int i = 0; i < adapterCount; i++) {
                View item = printBillParentListAdapter.getView(i, null, null);
                layoutPrint58.addView(item);
            }
        }
    }

    /** bluetooth code **/

    public class BtPrint extends AsyncTask<String,String,String> {
        Boolean isSuccess;
        View v;
        int removePosition,printCount;
        String widthName;
        @Override
        protected String doInBackground(String... params) {
            try {
                if(lstReceiptPrinter.size()!=0) {
                    for (int n = 0; n < lstReceiptPrinter.size(); n++) {
                        String interfaceName = lstReceiptPrinter.get(n).getInterfaceName();
                        printCount = lstReceiptPrinter.get(n).getPrintCount();
                        String printerAddress = lstReceiptPrinter.get(n).getPrinterAddress();
                        widthName = lstReceiptPrinter.get(n).getWidthName();
                        removePosition = n;
                        if (interfaceName.equals(PrinterInterface.iBluetooth)) {
                            if (checkBluetoothOn()) {
                                if (checkBluetoothDevice(printerAddress)) {
                                    isSuccess = true;
                                    break;
                                }
                            }
                        }
                    }
                }else {
                    isSuccess = false;
                }
            } catch (Exception ex) {
                isSuccess = false;
            }
            return "";
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected void onPostExecute(String r){
            if(isSuccess){
                for (int i = 0; i < printBillParentListAdapter.getCount(); i++) {
                    if (widthName.equals(PaperWidth.w80)) {
                        v = layoutPrint80.getChildAt(i);
                        if (v != null) {
                            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            v.draw(canvas);
                            savePrintLayoutToWaiterOneDB(bitmap);  // created bill print paper
                            for(int p=0;p<printCount;p++) {
                                printBitmapOrder();                //  printed by print count
                            }
                        }
                    } else if (widthName.equals(PaperWidth.w58)) {
                        v = layoutPrint58.getChildAt(i);
                        if (v != null) {
                            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            v.draw(canvas);
                            savePrintLayoutToWaiterOneDB(bitmap);  // created bill print paper
                            for(int p=0;p<printCount;p++) {
                                printBitmapOrder();                // printed by print count
                            }
                        }
                    }
                }
                lstReceiptPrinter.remove(removePosition);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BtPrint btPrint=new BtPrint();
                        btPrint.execute("");
                    }
                }, 5000);
            }else{
                finish();
            }
        }
    }

    private void printBitmapOrder() {
        Bitmap bitmap=null;
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath=new File(directory,"print.png");
        if(filePath.exists()) bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
        PrintPic printPic = PrintPic.getInstance();
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
        Log.e("BtService", "image bytes size is :" + bytes.length);
        printBytes.add(com.bosictsolution.waiterone.print.GPrinterCommand.print);
        printBytes.add(GPrinterCommand.cut);
        PrintQueue.getQueue(getApplicationContext()).add(printBytes);
    }

    private boolean checkBluetoothOn(){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(),"Turned on",Toast.LENGTH_LONG).show();
            return false;
        }else {
            return true;
        }
    }

    private boolean checkBluetoothDevice(String btAddress){
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();

        for(BluetoothDevice bt : pairedDevices) {
            String address=bt.getAddress();
            if(btAddress.equals(address)){
                connectBluetoothDevice(bt);
                return true;
            }
        }
        return false;
    }

    public void connectBluetoothDevice(BluetoothDevice bt){
        if (null == deviceAdapter) {
            return;
        }
        final BluetoothDevice bluetoothDevice = bt;
        if (null == bluetoothDevice) {
            return;
        }
        try {
            BtUtil.cancelDiscovery(bluetoothAdapter);
            PrintUtil.setDefaultBluetoothDeviceAddress(getApplicationContext(), bluetoothDevice.getAddress());
            PrintUtil.setDefaultBluetoothDeviceName(getApplicationContext(), bluetoothDevice.getName());
            if (null != deviceAdapter) {
                deviceAdapter.setConnectedDeviceAddress(bluetoothDevice.getAddress());
            }

            //if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(bluetoothDevice);
            //}
            PrintQueue.getQueue(getApplicationContext()).disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            PrintUtil.setDefaultBluetoothDeviceAddress(getApplicationContext(), "");
            PrintUtil.setDefaultBluetoothDeviceName(getApplicationContext(), "");
            systemSetting.showMessage(SystemSetting.ERROR,"Bluetooth Tethering Fail,Try Again!",context,getLayoutInflater());
        }
    }

    //end bluetooth code
}
