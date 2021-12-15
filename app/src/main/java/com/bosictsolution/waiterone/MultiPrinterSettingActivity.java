package com.bosictsolution.waiterone;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.andprn.jpos.command.ESCPOSConst;
import com.andprn.jpos.printer.ESCPOSPrinter;
import com.andprn.request.android.RequestHandler;
import com.bosictsolution.waiterone.bt.BtUtil;
import com.bosictsolution.waiterone.print.PrintQueue;
import com.bosictsolution.waiterone.print.PrintUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import adapter.BtDeviceListAdapter;
import adapter.SpPaperWidthAdapter;
import adapter.SpPrinterInterfaceAdapter;
import adapter.PrinterListAdapter;
import adapter.SpPrinterModelAdapter;
import common.AlertView;
import common.BtService;
import common.DBHelper;
import common.NTPrinterConnection;
import common.PaperWidth;
import common.PrintCount;
import common.PrinterInterface;
import common.PrinterWiFiPort;
import common.SystemSetting;
import data.PaperWidthData;
import data.PrinterData;
import data.PrinterInterfaceData;
import data.PrinterModelData;

public class MultiPrinterSettingActivity extends AppCompatActivity {

    ListView lvPrinter;
    EditText etPrinterName,etPrinterIP,etBluetoothPrinter,etPrinterUSB;
    Spinner spPrinterModel,spInterface,spPaperWidth,spPrintCount;
    LinearLayout vEthernet,vBluetooth,vUSB,vInterface,vPaperWidth,layoutDeleteRefresh,vPrintCount;
    Button btnSearch,btnSave,btnPrintTest;
    ImageButton btnDelete,btnRefresh;
    CheckBox chkIsReceipt;

    DBHelper db;
    SystemSetting systemSetting=new SystemSetting();

    SpPrinterModelAdapter spPrinterModelAdapter;
    SpPrinterInterfaceAdapter spPrinterInterfaceAdapter;
    SpPaperWidthAdapter spPaperWidthAdapter;
    PrinterListAdapter printerListAdapter;
    public static BluetoothAdapter BA;
    private BtDeviceListAdapter deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;

    private static final String TAG = "WiFiConnectMenu";
    private PrinterWiFiPort wifiPort= PrinterWiFiPort.getInstance();
    private Thread hThread;
    String ioException;
    private ESCPOSPrinter posPtr;

    final Context context = this;
    private ProgressDialog progressDialog;

    List<PrinterModelData> lstPrinterModel;
    List<PrinterInterfaceData> lstPrinterInterface;
    List<PaperWidthData> lstPaperWidth;
    List<PrinterData> lstPrinter;

    int editPrinterId,editPrinterPosition;
    private static final int M_EMPTY=1,M_ALL=2,M_ETHERNET=3;
    public static int WidthSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_printer_setting);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        BA = BluetoothAdapter.getDefaultAdapter();
        deviceAdapter=new BtDeviceListAdapter(getApplicationContext(), null);
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        SystemSetting.isTestPrint=true;

        setLayoutResource();

        getPrinter();
        getPrinterModel();
        getPrintCount();

        spPrinterModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spPrinterModel.getSelectedItemPosition()==0){
                    visibleLayouts(M_EMPTY);
                }else if(spPrinterModel.getSelectedItemPosition()==1) {
                    if(editPrinterId==0) visibleLayouts(M_ALL);
                }
                //else if(spPrinterModel.getSelectedItemPosition()==1 || spPrinterModel.getSelectedItemPosition()==2){
                    //visibleLayouts(M_ETHERNET);
                //}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spInterface.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    vEthernet.setVisibility(View.VISIBLE);
                    vBluetooth.setVisibility(View.GONE);
                    vUSB.setVisibility(View.GONE);
                }else if(position==1){
                    vEthernet.setVisibility(View.GONE);
                    vBluetooth.setVisibility(View.VISIBLE);
                    vUSB.setVisibility(View.GONE);
                }else if(position==2){
                    vEthernet.setVisibility(View.GONE);
                    vBluetooth.setVisibility(View.GONE);
                    vUSB.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBluetoothOn()) {
                    Intent i = new Intent(MultiPrinterSettingActivity.this, BondBtActivity.class);
                    startActivity(i);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrinter();
            }
        });

        lvPrinter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editPrinterId=lstPrinter.get(position).getPrinterId();
                if(editPrinterId==0)return;

                layoutDeleteRefresh.setVisibility(View.VISIBLE);
                etPrinterName.setText(lstPrinter.get(position).getPrinterName());
                editPrinterPosition=position;

                int modelId=lstPrinter.get(position).getModelId();
                for(int i=0;i<lstPrinterModel.size();i++){
                    if(lstPrinterModel.get(i).getModelId()==modelId){
                        spPrinterModel.setSelection(i);
                    }
                }

                int interfaceId=lstPrinter.get(position).getInterfaceId();
                for(int i=0;i<lstPrinterInterface.size();i++){
                    if(lstPrinterInterface.get(i).getInterfaceId()==interfaceId){
                        vInterface.setVisibility(View.VISIBLE);
                        spInterface.setSelection(i);
                        String selectedInterface=lstPrinterInterface.get(i).getInterfaceName();
                        if(selectedInterface.equals(PrinterInterface.iEthernet)){
                            vEthernet.setVisibility(View.VISIBLE);
                            etPrinterIP.setText(lstPrinter.get(position).getPrinterAddress());
                        }else if(selectedInterface.equals(PrinterInterface.iBluetooth)){
                            vBluetooth.setVisibility(View.VISIBLE);
                            etBluetoothPrinter.setText(lstPrinter.get(position).getPrinterAddress());
                        }else if(selectedInterface.equals(PrinterInterface.iUSB)){
                            vUSB.setVisibility(View.VISIBLE);
                            etPrinterUSB.setText(lstPrinter.get(position).getPrinterAddress());
                        }
                    }
                }

                int widthId=lstPrinter.get(position).getWidthId();
                for(int i=0;i<lstPaperWidth.size();i++){
                    if(lstPaperWidth.get(i).getWidthId()==widthId){
                        vPaperWidth.setVisibility(View.VISIBLE);
                        spPaperWidth.setSelection(i);
                    }
                }

                int isReceipt=lstPrinter.get(position).getIsReceipt();
                if(isReceipt==1){
                    chkIsReceipt.setChecked(true);
                    vPrintCount.setVisibility(View.VISIBLE);
                    int printCount=lstPrinter.get(position).getPrintCount();
                    if(printCount==1)spPrintCount.setSelection(0);
                    else if(printCount==2)spPrintCount.setSelection(1);
                    else if(printCount==3)spPrintCount.setSelection(2);
                }else{
                    chkIsReceipt.setChecked(false);
                    vPrintCount.setVisibility(View.GONE);
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearControls();
            }
        });

        btnPrintTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int interfacePosition = spInterface.getSelectedItemPosition();
                String interfaceName = lstPrinterInterface.get(interfacePosition).getInterfaceName();
                int widthPosition = spPaperWidth.getSelectedItemPosition();
                String width = lstPaperWidth.get(widthPosition).getWidthName();
                if (width.equals(PaperWidth.w58)) WidthSize = 58;
                else if (width.equals(PaperWidth.w80)) WidthSize = 80;
                if (interfaceName.equals(PrinterInterface.iEthernet)) {
                    testPrintEthernet();
                } else if (interfaceName.equals(PrinterInterface.iBluetooth)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (checkBluetoothOn()) {
                                if (checkEditBluetoothDevice()) {
                                    Intent intent = new Intent(getApplicationContext(), BtService.class);
                                    intent.setAction(PrintUtil.ACTION_PRINT_BITMAP);
                                    startService(intent);
                                }
                            }
                        }
                    }, 1000);
                }
            }
        });

        chkIsReceipt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)vPrintCount.setVisibility(View.VISIBLE);
                else vPrintCount.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(BondBtActivity.bluetoothAddress!=null) {
            if (BondBtActivity.bluetoothAddress.length() != 0) {
                etBluetoothPrinter.setText(BondBtActivity.bluetoothAddress);
            }
        }
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

    private void testPrintEthernet(){
        try {
            wifiDisConn();
        }catch (IOException e) {
            Log.e(TAG,e.getMessage(),e);
        }catch (InterruptedException e) {
            Log.e(TAG,e.getMessage(),e);
        }
        if(etPrinterIP.getText().toString().length()!=0) {
            /**NTPrinterConnection connection=new NTPrinterConnection(context);
            connection.connectPrinter(etPrinterIP.getText().toString());
            //try {
                //print(context);
            //}catch (IOException e){
                //e.printStackTrace();
            //}
            //if(connection.connectPrinter(etPrinterIP.getText().toString())){
                //try {
                    //print(context);
                //}catch (IOException e){
                    //e.printStackTrace();
                //}
            //}**/
            connectPrinter(etPrinterIP.getText().toString());
        }else{
            systemSetting.showMessage(SystemSetting.WARNING,"Enter Printer IP Address",context,getLayoutInflater());
            etPrinterIP.requestFocus();
        }
    }

    private boolean checkBluetoothOn(){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            systemSetting.showMessage(SystemSetting.INFO,"Turned on",context,getLayoutInflater());
            return false;
        }else {
            return true;
        }
    }

    private boolean checkEditBluetoothDevice(){
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();

        for(BluetoothDevice bt : pairedDevices) {
            String address=bt.getAddress();
            String editAddress=etBluetoothPrinter.getText().toString();
            if(editAddress.equals(address)){
                connectDevice(bt);
                return true;
            }
        }
        return false;
    }

    public void connectDevice(BluetoothDevice bt){
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
            systemSetting.showMessage(SystemSetting.ERROR,"Bluetooth Tethering Fail,Try Again",context,getLayoutInflater());
        }
    }

    private void savePrinter(){
        String printerName,printerIPAddress="",blueToothPrinter="",usbPrinter="";
        int selectedInterfaceId,widthId,printCount=0,isReceipt;
        if(validate()){
            printerName=etPrinterName.getText().toString();
            int modelPosition=spPrinterModel.getSelectedItemPosition();
            int modelId=lstPrinterModel.get(modelPosition).getModelId();
            int interfaceId=lstPrinterModel.get(modelPosition).getInterfaceId();
            int widthIdByModel=lstPrinterModel.get(modelPosition).getWidthId();
            int widthPosition=spPaperWidth.getSelectedItemPosition();
            widthId=lstPaperWidth.get(widthPosition).getWidthId();
            if(chkIsReceipt.isChecked()) {
                isReceipt=1;
                int countPosition = spPrintCount.getSelectedItemPosition();
                if (countPosition == 0) printCount = 1;
                else if (countPosition == 1) printCount = 2;
                else if (countPosition == 2) printCount = 3;
            }else{
                isReceipt=0;
            }
            if(interfaceId==0){ // other model
                int interfacePosition=spInterface.getSelectedItemPosition();
                selectedInterfaceId=lstPrinterInterface.get(interfacePosition).getInterfaceId();
                String interfaceName=lstPrinterInterface.get(interfacePosition).getInterfaceName();
                if(interfaceName.equals(PrinterInterface.iEthernet)){
                    if(checkPrinterIP()) {
                        printerIPAddress = etPrinterIP.getText().toString();
                        if(editPrinterId==0) {
                            if (db.insertPrinter(printerName, modelId, selectedInterfaceId, printerIPAddress, widthId,printCount,isReceipt)) {
                                systemSetting.showMessage(SystemSetting.SUCCESS,"Printer Saved!",context,getLayoutInflater());
                                clearControls();
                            }else{
                                systemSetting.showMessage(SystemSetting.INFO,"Allow only one network printer!",context,getLayoutInflater());
                            }
                        }else{
                            if(db.isExistNetPrinter(editPrinterId,selectedInterfaceId)) {
                                if (db.updatePrinterByPrinterID(editPrinterId, printerName, modelId, selectedInterfaceId, printerIPAddress, widthId, printCount, isReceipt)) {
                                    systemSetting.showMessage(SystemSetting.SUCCESS, "Printer Edited!", context, getLayoutInflater());
                                    clearControls();
                                }
                            }else{
                                systemSetting.showMessage(SystemSetting.INFO,"Allow only one network printer!",context,getLayoutInflater());
                            }
                        }
                    }
                }else if(interfaceName.equals(PrinterInterface.iBluetooth)){
                    if(checkBluetoothPrinter()) {
                        blueToothPrinter = etBluetoothPrinter.getText().toString();
                        if(editPrinterId==0) {
                            if (db.insertPrinter(printerName, modelId, selectedInterfaceId, blueToothPrinter, widthId,printCount,isReceipt)) {
                                systemSetting.showMessage(SystemSetting.SUCCESS,"Printer Saved!",context,getLayoutInflater());
                                clearControls();
                            }
                        }else{
                            if(db.isExistNetPrinter(editPrinterId,selectedInterfaceId)) {
                                if (db.updatePrinterByPrinterID(editPrinterId, printerName, modelId, selectedInterfaceId, blueToothPrinter, widthId, printCount, isReceipt)) {
                                    systemSetting.showMessage(SystemSetting.SUCCESS, "Printer Edited!", context, getLayoutInflater());
                                    clearControls();
                                }
                            }else{
                                systemSetting.showMessage(SystemSetting.INFO,"Allow only one network printer!",context,getLayoutInflater());
                            }
                        }
                    }
                }else if(interfaceName.equals(PrinterInterface.iUSB)){
                    if(checkUSBPrinter()){
                        usbPrinter=etPrinterUSB.getText().toString();
                        if(editPrinterId==0) {
                            if (db.insertPrinter(printerName, modelId, selectedInterfaceId, usbPrinter, widthId,printCount,isReceipt)) {
                                systemSetting.showMessage(SystemSetting.SUCCESS,"Printer Saved!",context,getLayoutInflater());
                                clearControls();
                            }
                        }else{
                            if(db.isExistNetPrinter(editPrinterId,selectedInterfaceId)) {
                                if (db.updatePrinterByPrinterID(editPrinterId, printerName, modelId, selectedInterfaceId, usbPrinter, widthId, printCount, isReceipt)) {
                                    systemSetting.showMessage(SystemSetting.SUCCESS, "Printer Edited!", context, getLayoutInflater());
                                    clearControls();
                                }
                            }else{
                                systemSetting.showMessage(SystemSetting.INFO,"Allow only one network printer!",context,getLayoutInflater());
                            }
                        }
                    }
                }
            }else if(interfaceId==1){ // Ethernet
                if(checkPrinterIP()) {
                    printerIPAddress = etPrinterIP.getText().toString();
                    if(editPrinterId==0) {
                        if (db.insertPrinter(printerName, modelId, interfaceId, printerIPAddress, widthIdByModel,printCount,isReceipt)) {
                            systemSetting.showMessage(SystemSetting.SUCCESS,"Printer Saved!",context,getLayoutInflater());
                            clearControls();
                        }else{
                            systemSetting.showMessage(SystemSetting.INFO,"Allow only one network printer!",context,getLayoutInflater());
                        }
                    }else{
                        if(db.isExistNetPrinter(editPrinterId,interfaceId)) {
                            if (db.updatePrinterByPrinterID(editPrinterId, printerName, modelId, interfaceId, printerIPAddress, widthIdByModel, printCount, isReceipt)) {
                                systemSetting.showMessage(SystemSetting.SUCCESS, "Printer Edited!", context, getLayoutInflater());
                                clearControls();
                            }
                        }else{
                            systemSetting.showMessage(SystemSetting.INFO,"Allow only one network printer!",context,getLayoutInflater());
                        }
                    }
                }
            }
        }
    }

    private void clearControls(){
        editPrinterId=0;
        etPrinterName.setText("");
        etPrinterIP.setText("");
        etBluetoothPrinter.setText("");
        etPrinterUSB.setText("");
        getPrinter();
        layoutDeleteRefresh.setVisibility(View.INVISIBLE);
    }

    private boolean validate(){
        if(etPrinterName.getText().toString().trim().length()==0){
            systemSetting.showMessage(SystemSetting.INFO,"Enter Printer Name!",context,getLayoutInflater());
            etPrinterName.requestFocus();
            return false;
        }
        if(spPrinterModel.getSelectedItemPosition()==0){
            systemSetting.showMessage(SystemSetting.INFO,"Choose Printer Model!",context,getLayoutInflater());
            spPrinterModel.requestFocus();
            return false;
        }

        return true;
    }

    private boolean checkPrinterIP(){
        if(etPrinterIP.getText().toString().trim().length()==0){
            systemSetting.showMessage(SystemSetting.INFO,"Enter Printer IP Address!",context,getLayoutInflater());
            etPrinterIP.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkBluetoothPrinter(){
        if(etBluetoothPrinter.getText().toString().trim().length()==0){
            systemSetting.showMessage(SystemSetting.INFO,"Cannot find Bluetooth Printer!",context,getLayoutInflater());
            etBluetoothPrinter.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkUSBPrinter(){
        if(etPrinterUSB.getText().toString().trim().length()==0){
            systemSetting.showMessage(SystemSetting.INFO,"Cannot find USB Printer!",context,getLayoutInflater());
            etPrinterUSB.requestFocus();
            return false;
        }
        return true;
    }

    private void visibleLayouts(int type){
        getPrinterInterface();
        getPaperWidth();
        if(type==M_EMPTY) {
            vInterface.setVisibility(View.GONE);
            vEthernet.setVisibility(View.GONE);
            vUSB.setVisibility(View.GONE);
            vBluetooth.setVisibility(View.GONE);
            vPaperWidth.setVisibility(View.GONE);
        }else if(type==M_ALL){
            vInterface.setVisibility(View.VISIBLE);
            vEthernet.setVisibility(View.VISIBLE);
            vUSB.setVisibility(View.GONE);
            vBluetooth.setVisibility(View.GONE);
            vPaperWidth.setVisibility(View.VISIBLE);
        }else if(type==M_ETHERNET){
            vInterface.setVisibility(View.GONE);
            vEthernet.setVisibility(View.VISIBLE);
            vUSB.setVisibility(View.GONE);
            vBluetooth.setVisibility(View.GONE);
            vPaperWidth.setVisibility(View.GONE);
        }
    }

    private void getPrinter(){
        lstPrinter=new ArrayList<>();
        Cursor cur=db.getPrinter();
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
            data.setIsReceipt(cur.getInt(10));
            lstPrinter.add(data);
        }

        if(lstPrinter.size()==0){
            PrinterData data=new PrinterData();
            data.setPrinterId(0);
            data.setPrinterName("No printer!");
            lstPrinter.add(data);
        }

        printerListAdapter = new PrinterListAdapter(this,lstPrinter);
        lvPrinter.setAdapter(printerListAdapter);
    }

    private void getPrinterModel(){
        lstPrinterModel=new ArrayList<>();

        Cursor cur=db.getPrinterModel();
        while(cur.moveToNext()){
            PrinterModelData data=new PrinterModelData();
            data.setModelId(cur.getInt(0));
            data.setModelName(cur.getString(1));
            data.setInterfaceId(cur.getInt(2));
            data.setWidthId(cur.getInt(3));
            lstPrinterModel.add(data);
        }

        spPrinterModelAdapter = new SpPrinterModelAdapter(this,lstPrinterModel);
        spPrinterModel.setAdapter(spPrinterModelAdapter);
    }

    private void getPrinterInterface(){
        lstPrinterInterface=new ArrayList<>();

        Cursor cur=db.getPrinterInterface();
        while(cur.moveToNext()){
            PrinterInterfaceData data=new PrinterInterfaceData();
            data.setInterfaceId(cur.getInt(0));
            data.setInterfaceName(cur.getString(1));
            lstPrinterInterface.add(data);
        }

        spPrinterInterfaceAdapter = new SpPrinterInterfaceAdapter(this,lstPrinterInterface);
        spInterface.setAdapter(spPrinterInterfaceAdapter);
    }

    private void getPaperWidth(){
        lstPaperWidth=new ArrayList<>();

        Cursor cur=db.getPaperWidth();
        while(cur.moveToNext()){
            PaperWidthData data=new PaperWidthData();
            data.setWidthId(cur.getInt(0));
            data.setWidthName(cur.getString(1));
            lstPaperWidth.add(data);
        }

        spPaperWidthAdapter = new SpPaperWidthAdapter(this,lstPaperWidth);
        spPaperWidth.setAdapter(spPaperWidthAdapter);
    }

    private void getPrintCount(){
        List<Integer> lstPrintCount=new ArrayList<>();
        lstPrintCount.add(PrintCount.cOne);
        lstPrintCount.add(PrintCount.cTwo);
        lstPrintCount.add(PrintCount.cThree);

        ArrayAdapter arrayAdapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,lstPrintCount);
        spPrintCount.setAdapter(arrayAdapter);
    }

    private void setLayoutResource(){
        lvPrinter=(ListView)findViewById(R.id.lvPrinter);
        etPrinterName=(EditText) findViewById(R.id.etPrinterName);
        etPrinterIP=(EditText)findViewById(R.id.etPrinterIP);
        etBluetoothPrinter=(EditText)findViewById(R.id.etBluetoothPrinter);
        etPrinterUSB=(EditText)findViewById(R.id.etPrinterUSB);
        spPrinterModel=(Spinner)findViewById(R.id.spPrinterModel);
        spInterface=(Spinner)findViewById(R.id.spInterface);
        spPaperWidth=(Spinner)findViewById(R.id.spPaperWidth);
        vEthernet=(LinearLayout) findViewById(R.id.vEthernet);
        vBluetooth=(LinearLayout)findViewById(R.id.vBlueTooth);
        vUSB=(LinearLayout)findViewById(R.id.vUSB);
        vInterface=(LinearLayout)findViewById(R.id.vInterface);
        vPaperWidth=(LinearLayout)findViewById(R.id.vPaperWidth);
        btnSearch=(Button)findViewById(R.id.btnSearch);
        btnSave=(Button)findViewById(R.id.btnSave);
        layoutDeleteRefresh=(LinearLayout)findViewById(R.id.layoutDeleteRefresh);
        btnDelete=(ImageButton)findViewById(R.id.btnDelete);
        btnRefresh=(ImageButton)findViewById(R.id.btnRefresh);
        btnPrintTest=(Button)findViewById(R.id.btnPrintTest);
        spPrintCount=(Spinner)findViewById(R.id.spPrintCount);
        vPrintCount=(LinearLayout)findViewById(R.id.vPrintCount);
        chkIsReceipt=(CheckBox)findViewById(R.id.chkIsReceipt);
    }

    private void showConfirmDialog(){
        LayoutInflater reg=LayoutInflater.from(this);
        View passwordView=reg.inflate(R.layout.dg_confirm, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(this);
        passwordDialog.setView(passwordView);

        final TextView tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        tvConfirmMessage.setText("Are you sure you want to delete?");

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
                if(editPrinterId!=0){
                    if(db.deletePrinterByPrinterID(editPrinterId)){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Deleted!",context,getLayoutInflater());
                        clearControls();
                    }
                }else{
                    systemSetting.showMessage(SystemSetting.INFO,"Not Found PrinterID!",context,getLayoutInflater());
                }
                passwordRequireDialog.dismiss();
            }
        });
    }

    //network printer connecting
    private void connectPrinter(String printerIPAddress){
        wifiPort = PrinterWiFiPort.getInstance();
        try{
            wifiConn(printerIPAddress);
        }
        catch (IOException e)
        {
            Log.e(TAG,e.getMessage(),e);
        }
    }

    private void wifiConn(String ipAddr) throws IOException
    {
        new connTask().execute(ipAddr);
    }

    private void wifiDisConn() throws IOException, InterruptedException
    {
        wifiPort.disconnect();
        if(hThread!=null)hThread.interrupt();
        //Toast toast = Toast.makeText(context, "Disconnected!", Toast.LENGTH_SHORT);
        //toast.show();
    }

    // WiFi Connection Task.
    class connTask extends AsyncTask<String, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(MultiPrinterSettingActivity.this);

        @Override
        protected void onPreExecute()
        {
            dialog.setTitle("Printer Connect");
            dialog.setMessage("Connecting");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params)
        {
            Integer retVal = null;
            try
            {
                wifiPort.connect(params[0]);
                retVal = new Integer(0);
            }
            catch (IOException e)
            {
                Log.e(TAG,e.getMessage(),e);
                retVal = new Integer(-1);
                ioException=e.getMessage();
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if(result.intValue() == 0)
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                if(dialog.isShowing()) {
                    dialog.dismiss();
                    try {
                        print(getApplicationContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                if(dialog.isShowing())
                    dialog.dismiss();
                AlertView.showAlert("Failed", "Check Devices!"+ioException, context);
            }
            super.onPostExecute(result);
        }
    }

    public void print(Context context) throws IOException
    {
        File filePath;
        posPtr = new ESCPOSPrinter();
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if(WidthSize==58) {
            filePath = new File(directory, "print58.png");
        }else{
            filePath = new File(directory, "print80.png");
        }
        String receiptPath=filePath.toString();
        posPtr.printBitmap(receiptPath, ESCPOSConst.ALIGNMENT_CENTER);
        posPtr.lineFeed(4);
        posPtr.cutPaper();
    }

    public class BluetoothTestPrintLoading extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), BtService.class);
                    intent.setAction(PrintUtil.ACTION_PRINT_BITMAP);
                    startService(intent);
                }
            }, 1000);
            return "";
        }

        @Override
        protected void onPreExecute(){
            progressDialog =new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setMessage("Loading.....");
        }

        @Override
        protected void onPostExecute(String r){
            progressDialog.hide();
        }
    }
}
