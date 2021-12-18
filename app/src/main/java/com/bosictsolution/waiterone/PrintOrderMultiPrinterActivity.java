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
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import adapter.BtDeviceListAdapter;
import adapter.PrintOrderListAdapter;
import common.DBHelper;
import common.PaperWidth;
import common.PrinterInterface;
import common.SystemSetting;
import data.PrintOrderData;
import data.STypeData;
import data.TransactionData;

public class PrintOrderMultiPrinterActivity extends AppCompatActivity {

    LinearLayout layoutPrint80, layoutPrint58;
    TextView tvPrinting;
    Button btnPrint;
    PrintOrderListAdapter printOrderListAdapter;
    DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    private Calendar cCalendar;
    String tableName,userName,printerName,printerAddress,interfaceName,widthName,sTypeName;
    final Context context = this;
    int tabletSize,tableId,printerId;
    List<STypeData> lstSTypeData;
    SimpleDateFormat dateTimeFormat;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;
    public static BluetoothAdapter BA;
    private BtDeviceListAdapter deviceAdapter;
    List<Long> lstPrintOutSType=new ArrayList<>();
    private ESCPOSPrinter posPtr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_order);

        db = new DBHelper(this);
        BA = BluetoothAdapter.getDefaultAdapter();
        deviceAdapter = new BtDeviceListAdapter(getApplicationContext(), null);
        cCalendar = Calendar.getInstance();
        dateTimeFormat = new SimpleDateFormat(SystemSetting.DATE_TIME_FORMAT);
        dateFormat = new SimpleDateFormat(SystemSetting.MM_DATE_FORMAT);
        timeFormat = new SimpleDateFormat(SystemSetting.ORDER_PRINT_TIME_FORMAT);
        SystemSetting.isTestPrint = false;

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        tableId = intent.getIntExtra("TableId", 0);
        tableName = intent.getStringExtra("TableName");
        userName = intent.getStringExtra("UserName");

        layoutPrint80 = (LinearLayout) findViewById(R.id.layoutPrint);
        layoutPrint58 = (LinearLayout) findViewById(R.id.layoutPrint2);
        tvPrinting = (TextView) findViewById(R.id.tvPrinting);
        btnPrint = (Button) findViewById(R.id.btnPrint);
        tvPrinting.setVisibility(View.GONE);
        btnPrint.setVisibility(View.VISIBLE);
        layoutPrint80.setVisibility(View.VISIBLE);
        layoutPrint58.setVisibility(View.VISIBLE);

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
        getSType();
        collectPrintData80(SaleActivity.lstOrder);
        collectPrintData58(SaleActivity.lstOrder);

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPrinting.setVisibility(View.VISIBLE);
                btnPrint.setEnabled(false);
                createOrderBySType();
                printNtOrder();
                if(isBluetoothPrint()){
                    BtPrint btPrint = new BtPrint();
                    btPrint.execute("");
                }
                else {
                    finish();
                }
            }
        });

        /**new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnPrint.performClick();
            }
        }, 1000);**/
    }

    /** create print layouts **/

    private String savePrintLayoutToWaiterOneDB(Bitmap bitmapImage,String sTypeName,String width){
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB/Order");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logoPath=new File(directory,sTypeName+width+"_order.png");

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

    private void createOrderBySType() {
        for (int i = 0; i < printOrderListAdapter.getCount(); i++) {
            long sTypeId = printOrderListAdapter.getItemId(i);
            String sTypeName = printOrderListAdapter.getItem(i);
            getPrinterInfo(sTypeId);
            if(widthName.equals("80")) {
                View v = layoutPrint80.getChildAt(i);
                if (v != null) {
                    Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    v.draw(canvas);
                    savePrintLayoutToWaiterOneDB(bitmap, sTypeName, widthName);
                }
            }else if(widthName.equals("58")){
                View v = layoutPrint58.getChildAt(i);
                if (v != null) {
                    Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    v.draw(canvas);
                    savePrintLayoutToWaiterOneDB(bitmap, sTypeName, widthName);
                }
            }
        }
    }

    //end create print layouts

    /** network code **/

    private void printNtOrder() {
        for (int i = 0; i < printOrderListAdapter.getCount(); i++) {
            long sTypeId = printOrderListAdapter.getItemId(i);
            sTypeName = printOrderListAdapter.getItem(i);
            getPrinterInfo(sTypeId); // get printerName,printerAddress,interfaceName,widthName
            if (interfaceName.length() != 0) {
                if (interfaceName.equals(PrinterInterface.iEthernet)) {
                    lstPrintOutSType.add(sTypeId);
                    try {
                        printNetworkPrinter(context,sTypeName,widthName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void printNetworkPrinter(Context context,String sTypeName,String widthName) throws IOException
    {
        posPtr = new ESCPOSPrinter();
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB/Order");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath=new File(directory,sTypeName+widthName+"_order.png");
        String receiptPath=filePath.toString();
        posPtr.printBitmap(receiptPath, ESCPOSConst.ALIGNMENT_CENTER);
        posPtr.lineFeed(4);
        posPtr.cutPaper();
    }

    //end network code

    /** bluetooth code **/

    public class BtPrint extends AsyncTask<String,String,String> {
        Boolean isSuccess = false;
        long sTypeId;
        String sTypeName;

        @Override
        protected String doInBackground(String... params) {
            try {
                for (int i = 0; i < printOrderListAdapter.getCount(); i++) {
                    sTypeId = printOrderListAdapter.getItemId(i);
                    if (!lstPrintOutSType.contains(sTypeId)) {
                        sTypeName = printOrderListAdapter.getItem(i);
                        getPrinterInfo(sTypeId);
                        if (interfaceName.length() != 0) {
                            if (interfaceName.equals(PrinterInterface.iBluetooth)) {
                                if (checkBluetoothOn()) {
                                    if (checkBluetoothDevice(printerAddress)) {
                                        isSuccess = true;
                                        break;
                                    }
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
            }
            return "";
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {
            lstPrintOutSType.add(sTypeId);
            if (isSuccess) printBitmapOrder(sTypeName, widthName);
            if (printOrderListAdapter.getCount() == lstPrintOutSType.size()) {
                db.updateOrderOutByTableID(tableId);
                finish();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BtPrint btPrint = new BtPrint();
                        btPrint.execute("");
                    }
                }, 5000);
            }
        }
    }

    private void printBitmapOrder(String sTypeName,String widthName) {
        Bitmap bitmap=null;
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB/Order");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath=new File(directory,sTypeName+widthName+"_order.png");
        if(filePath.exists()) bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
        PrintPic printPic =PrintPic.getInstance();
        printPic.length=0;
        printPic.init(bitmap);
        Log.e("BtService", "print pic is :" + printPic.length);
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
            //BtUtil.cancelDiscovery(bluetoothAdapter);
            BtUtil.cancelDiscovery(BA);
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

    private boolean isBluetoothPrint(){
        boolean result=false;
        for (int i = 0; i < printOrderListAdapter.getCount(); i++) {
            long sTypeId = printOrderListAdapter.getItemId(i);
            sTypeName = printOrderListAdapter.getItem(i);
            getPrinterInfo(sTypeId); // get printerName,printerAddress,interfaceName,widthName
            if (interfaceName.length() != 0) {
                if (interfaceName.equals(PrinterInterface.iBluetooth)) {
                    result=true;
                    break;
                }
            }
        }
        return result;
    }

    //end bluetooth code

    /** get data **/

    private void getSType() {
        lstSTypeData = new ArrayList<>();
        Cursor cur = db.getSType();
        while (cur.moveToNext()) {
            STypeData data = new STypeData();
            data.sTypeID = cur.getInt(0);
            data.sTypeName = cur.getString(1);
            lstSTypeData.add(data);
        }
    }

    private int getPrinterIdBySTypeId(long stypeid){
        Cursor cur=db.getPrinterIdBySTypeId(stypeid);
        if(cur.moveToNext()){
            printerId=cur.getInt(0);
        }else{
            printerId=0;
        }
        return printerId;
    }

    private void getPrinterInfoByPrinterId(int printerid){
        Cursor cur=db.getPrinterByPrinterId(printerid);
        if(cur.moveToNext()){
            printerName=cur.getString(1);
            printerAddress=cur.getString(4);
            interfaceName=cur.getString(7);
            widthName=cur.getString(8);
        }else{
            printerName="";
            printerAddress="";
            interfaceName="";
            widthName="";
        }
    }

    private void collectPrintData80(List<TransactionData> lstOrder){
        List<PrintOrderData> lstData=new ArrayList<>();

        for(int i=0;i<lstSTypeData.size();i++){
            List<TransactionData> lstTran=new ArrayList<>();
            for(int j=0;j<lstOrder.size();j++){
                if(lstOrder.get(j).getStype()==lstSTypeData.get(i).getsTypeID()){
                    TransactionData data=lstOrder.get(j);
                    TransactionData curData=new TransactionData();
                    curData.setItemName(data.getItemName());
                    curData.setStringQty(data.getStringQty());
                    curData.setIntegerQty(data.getIntegerQty());
                    curData.setFloatQty(data.getFloatQty());
                    curData.setTaste(data.getTaste());
                    lstTran.add(curData);
                }
            }
            if(lstTran.size()!=0) {
                PrintOrderData data = new PrintOrderData();
                data.setLstTran(lstTran);
                data.setsTypeId(lstSTypeData.get(i).sTypeID);
                data.setsTypeName(lstSTypeData.get(i).sTypeName);
                data.setTableName(tableName);
                data.setUserName(userName);
                data.setDateTime(dateTimeFormat.format(cCalendar.getTime()));
                data.setDate(dateFormat.format(cCalendar.getTime()));
                data.setTime(timeFormat.format(cCalendar.getTime()));

                lstData.add(data);
            }
        }

        printOrderListAdapter =new PrintOrderListAdapter(context,lstData);
        final int adapterCount = printOrderListAdapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View item = printOrderListAdapter.getView(i, null, null);
            layoutPrint80.addView(item);
        }
    }

    private void collectPrintData58(List<TransactionData> lstOrder){
        List<PrintOrderData> lstData=new ArrayList<>();

        for(int i=0;i<lstSTypeData.size();i++){
            List<TransactionData> lstTran=new ArrayList<>();
            for(int j=0;j<lstOrder.size();j++){
                if(lstOrder.get(j).getStype()==lstSTypeData.get(i).getsTypeID()){
                    TransactionData data=lstOrder.get(j);
                    TransactionData curData=new TransactionData();
                    curData.setItemName(data.getItemName());
                    curData.setStringQty(data.getStringQty());
                    curData.setIntegerQty(data.getIntegerQty());
                    curData.setFloatQty(data.getFloatQty());
                    curData.setTaste(data.getTaste());
                    lstTran.add(curData);
                }
            }
            if(lstTran.size()!=0) {
                PrintOrderData data = new PrintOrderData();
                data.setLstTran(lstTran);
                data.setsTypeId(lstSTypeData.get(i).sTypeID);
                data.setsTypeName(lstSTypeData.get(i).sTypeName);
                data.setTableName(tableName);
                data.setUserName(userName);
                data.setDateTime(dateTimeFormat.format(cCalendar.getTime()));
                data.setDate(dateFormat.format(cCalendar.getTime()));
                data.setTime(timeFormat.format(cCalendar.getTime()));

                lstData.add(data);
            }
        }

        printOrderListAdapter =new PrintOrderListAdapter(context,lstData);
        final int adapterCount = printOrderListAdapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View item = printOrderListAdapter.getView(i, null, null);
            layoutPrint58.addView(item);
        }
    }

    private void getPrinterInfo(long sTypeId){
        getPrinterInfoByPrinterId(getPrinterIdBySTypeId(sTypeId));  // get printerName,printerAddress,interfaceName,widthName
        if(widthName.equals(PaperWidth.w58))widthName="58";
        if(widthName.equals(PaperWidth.w80))widthName="80";
    }

    //end get data
}
