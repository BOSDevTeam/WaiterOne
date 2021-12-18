package com.bosictsolution.waiterone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.andprn.request.android.RequestHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.BillListAdapter;
import common.AlertView;
import common.DBHelper;
import common.FeatureList;
import common.PrinterWiFiPort;
import common.SystemSetting;
import data.TransactionData;

public class BillActivity extends AppCompatActivity {

    TextView tvBillTitle,tvHeaderItemName,tvHeaderQuantity,tvHeaderAmount, tvLabelDiscountPercent, tvLabelDiscountAmount,
                tvLabelSubTotal,tvAmountSubTotal,tvLabelCharges,tvAmountCharges,tvLabelTax,tvAmountTax,tvLabelDiscount,tvAmountDiscount,
                tvLabelGrandTotal,tvAmountGrandTotal,tvHeaderPrice, tvLabelPaid, tvLabelChange, tvAmountChange;
    ListView lvBillItem,lvPrinter;
    EditText etDiscountPercent,etDiscountAmount,etPaidAmount;
    Button btnDiscountCalculate,btnPay,btnPaidCalculate;
    Switch swtChargesOff;

    private DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    final Context context = this;
    private Calendar calendar;
    private Calendar cCalendar;
    DecimalFormat df2 = new DecimalFormat("#");

    BillListAdapter billListAdapter;

    int taxPercent,chargesPercent,billTableID,portNumber,billSlipID,allowTaxIncludeCharges;
    String billTableName,confirmMessage,billWaiterName,date,time,printerIP,currentTime,startTime,endTime;
    public static List<TransactionData> lstViewOrder;
    double subTotal,grandTotal,taxAmount,chargesAmount,discount,onChargesAmount;

    private static final String TAG = "WiFiConnectMenu";
    private PrinterWiFiPort wifiPort= PrinterWiFiPort.getInstance();
    private String printerIPAddress;
    private Thread hThread;
    String ioException,printer_message;
    boolean isMultiTbBill;
    SharedPreferences sharedpreferences;
    int takeAwayType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        billTableID=intent.getIntExtra("tableid", 0);
        billTableName=intent.getStringExtra("tablename");
        billWaiterName=intent.getStringExtra("waitername");
        isMultiTbBill=intent.getBooleanExtra("MultiTbBill",false);

        db=new DBHelper(this);
        sharedpreferences = getSharedPreferences(SystemSetting.MyPREFERENCES, Context.MODE_PRIVATE);
        takeAwayType=sharedpreferences.getInt("TakeAway",0);
        calendar=Calendar.getInstance();

        setLayoutResource();

        String tablename="TABLE - "+billTableName+" Order #";
        tvBillTitle.setText(tablename);
        allowTaxIncludeCharges=db.getFeatureResult(FeatureList.fTaxIncCharg);

        getOrder();

        btnDiscountCalculate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                calculateDiscount();
            }
        });
        btnPay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    wifiDisConn();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                connectPrinter();
                /**if(db.getFeatureResult(FeatureList.fUseMultiPrinter)!=1) {
                    try {
                        wifiDisConn();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    connectPrinter();
                    //showConfirmDialog();
                }else{
                    confirmMessage="Are you sure you want to pay bill for table "+billTableName+"?";
                    showConfirmDialog();
                }**/
            }
        });
        btnPaidCalculate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Double netAmount=Double.parseDouble(tvAmountGrandTotal.getText().toString());
                if(etPaidAmount.getText().toString().length()==0)return;
                Double paidAmount=Double.parseDouble(etPaidAmount.getText().toString());
                Double changeAmount=paidAmount-netAmount;
                tvAmountChange.setText(String.valueOf(df2.format(changeAmount)));
            }
        });
        swtChargesOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tvAmountCharges.setText(String.valueOf(df2.format(onChargesAmount)));
                    if(allowTaxIncludeCharges==1){
                        tvLabelTax.setText("Tax(include charges)");
                        taxAmount=(taxPercent*(subTotal+onChargesAmount))/100;
                    }
                    else {
                        tvLabelTax.setText("Tax");
                        taxAmount =(taxPercent*subTotal)/100;
                    }
                    tvAmountTax.setText(String.valueOf(df2.format(taxAmount)));
                    grandTotal=(subTotal+taxAmount+onChargesAmount)-discount;
                    tvAmountGrandTotal.setText(String.valueOf(df2.format(grandTotal)));
                }else{
                    chargesAmount=0;
                    tvAmountCharges.setText(String.valueOf(df2.format(chargesAmount)));
                    if(allowTaxIncludeCharges==1)taxAmount=(taxPercent*(subTotal+chargesAmount))/100;
                    else taxAmount =(taxPercent*subTotal)/100;
                    tvAmountTax.setText(String.valueOf(df2.format(taxAmount)));
                    grandTotal=(subTotal+taxAmount+chargesAmount)-discount;
                    tvAmountGrandTotal.setText(String.valueOf(df2.format(grandTotal)));
                }
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

    private void getCurrentTime(){
        cCalendar= Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat(SystemSetting.TIME_FORMAT);
        currentTime=timeFormat.format(cCalendar.getTime());
    }

    private String getCurrentEndTime(){
        if(db.getFeatureResult(FeatureList.fStartEndTime)==0)return "";
        cCalendar= Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat(SystemSetting.TIME_FORMAT);
        endTime=timeFormat.format(cCalendar.getTime());
        endTime=endTime.toUpperCase();
        return endTime;
    }

    private void payBill() {
        int tranid = 0, tableid = 0, waiterid = 0;
        String vouno = "";
        double subTotal, tax, charges, discount, grandTotal;

        Cursor curSlip = db.getSlipID();
        if (curSlip.moveToFirst()) {
            billSlipID = curSlip.getInt(0);
            db.updateSlipID();
        }

        Cursor curMaster = db.getTransactionDetailFromMasterSaleTemp(billTableID);
        if (curMaster.moveToFirst()) {
            tranid = curMaster.getInt(0);
            date = curMaster.getString(1);
            vouno = curMaster.getString(2);
            tableid = curMaster.getInt(3);
            waiterid = curMaster.getInt(4);
            startTime=curMaster.getString(5);
        }
        subTotal = Double.parseDouble(tvAmountSubTotal.getText().toString());
        tax = Double.parseDouble(tvAmountTax.getText().toString());
        charges = Double.parseDouble(tvAmountCharges.getText().toString());
        discount = Double.parseDouble(tvAmountDiscount.getText().toString());
        grandTotal = Double.parseDouble(tvAmountGrandTotal.getText().toString());

        getCurrentTime();

        List<TransactionData> lstTransactionData = new ArrayList<>();
        Cursor curTran = db.getTransactionDetailFromTranSaleTemp(billTableID);
        while (curTran.moveToNext()) {
            TransactionData data = new TransactionData();
            data.setTranid(curTran.getInt(0));
            data.setSrNo(curTran.getInt(1));
            data.setItemid(curTran.getString(2));
            data.setItemName(curTran.getString(3));
            data.setStringQty(curTran.getString(4));
            data.setSalePrice(curTran.getDouble(5));
            data.setAmount(curTran.getDouble(6));
            data.setOrderTime(curTran.getString(7));
            data.setTaste(curTran.getString(8));
            data.setCounterID(curTran.getInt(9));
            lstTransactionData.add(data);
        }

        if (db.insertMasterSale(tranid, date, vouno, tableid, waiterid, subTotal, tax, charges, discount, grandTotal, currentTime, billSlipID,startTime,getCurrentEndTime())) {
            for (int i = 0; i < lstTransactionData.size(); i++) {
                //int tran_id=lstTransactionData.get(i).getTranid();
                int srno = lstTransactionData.get(i).getSrNo();
                String itemid = lstTransactionData.get(i).getItemid();
                String itemName = lstTransactionData.get(i).getItemName();
                String quantity = lstTransactionData.get(i).getStringQty();
                double doubleQty = Double.parseDouble(quantity);
                double price = lstTransactionData.get(i).getSalePrice();
                double amount = lstTransactionData.get(i).getAmount();
                String orderTime = lstTransactionData.get(i).getOrderTime();
                String taste = lstTransactionData.get(i).getTaste();
                int counterid = lstTransactionData.get(i).getCounterID();
                db.insertTranSale(tranid, srno, itemid, itemName, doubleQty, price, amount, orderTime, taste, counterid, currentTime);
            }
            if (db.deleteTransactionFromTranSaleTemp(tableid)) {
                db.updateTranIDFromMasterSaleTempByTableID(tableid);
            }
        }

        for (int i = 0; i < TableActivity.lstMultiTbBillId.size(); i++) {
            lstTransactionData = new ArrayList<>();
            Cursor cur = db.getTransactionDetailFromTranSaleTemp(TableActivity.lstMultiTbBillId.get(i));
            if (cur.getCount()!=0) {
                while (cur.moveToNext()) {
                    TransactionData data = new TransactionData();
                    data.setTranid(cur.getInt(0));
                    data.setSrNo(cur.getInt(1));
                    data.setItemid(cur.getString(2));
                    data.setItemName(cur.getString(3));
                    data.setStringQty(cur.getString(4));
                    data.setSalePrice(cur.getDouble(5));
                    data.setAmount(cur.getDouble(6));
                    data.setOrderTime(cur.getString(7));
                    data.setTaste(cur.getString(8));
                    data.setCounterID(cur.getInt(9));
                    lstTransactionData.add(data);
                }

                for (int j = 0; j < lstTransactionData.size(); j++) {
                    //int tran_id = lstTransactionData.get(j).getTranid();
                    int srno = lstTransactionData.get(j).getSrNo();
                    String itemid = lstTransactionData.get(j).getItemid();
                    String itemName = lstTransactionData.get(j).getItemName();
                    String quantity = lstTransactionData.get(j).getStringQty();
                    double doubleQty = Double.parseDouble(quantity);
                    double price = lstTransactionData.get(j).getSalePrice();
                    double amount = lstTransactionData.get(j).getAmount();
                    String orderTime = lstTransactionData.get(j).getOrderTime();
                    String taste = lstTransactionData.get(j).getTaste();
                    int counterid = lstTransactionData.get(j).getCounterID();
                    db.insertTranSale(tranid, srno, itemid, itemName, doubleQty, price, amount, orderTime, taste, counterid, currentTime);
                }
                if (db.deleteTransactionFromTranSaleTemp(TableActivity.lstMultiTbBillId.get(i))) {
                    db.updateTranIDFromMasterSaleTempByTableID(TableActivity.lstMultiTbBillId.get(i));
                }
            }
        }
    }

    private void getOrder(){
        lstViewOrder=new ArrayList<>();
        String stringQty;
        int integerQty;
        float floatQty;
        Cursor cur;
        if(isMultiTbBill) cur=db.getTransactionFromTranSaleTemp(TableActivity.lstMultiTbBillId);
        else cur=db.getTransactionFromTranSaleTemp(billTableID);
        while(cur.moveToNext()){
            TransactionData data=new TransactionData();
            if(db.getFeatureResult(FeatureList.fOrderTime)==1)data.setItemName(cur.getString(1)+" - "+cur.getString(8));
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
            data.setSalePrice(cur.getDouble(5));
            lstViewOrder.add(data);
        }
        showOrder(lstViewOrder);
    }

    private void showOrder(List<TransactionData> lstViewOrder){
        billListAdapter =new BillListAdapter(this,lstViewOrder);
        lvBillItem.setAdapter(billListAdapter);
        getServiceTax();

        for(int i=0;i<lstViewOrder.size();i++){
            subTotal+=lstViewOrder.get(i).getAmount();
        }
        chargesAmount =(chargesPercent*subTotal)/100;
        onChargesAmount=chargesAmount;
        if(allowTaxIncludeCharges==1){
            tvLabelTax.setText("Tax(include charges)");
            taxAmount=(taxPercent*(subTotal+chargesAmount))/100;
        }
        else {
            tvLabelTax.setText("Tax");
            taxAmount =(taxPercent*subTotal)/100;
        }
        grandTotal=subTotal+ taxAmount + chargesAmount;

        tvAmountSubTotal.setText(String.valueOf(df2.format(subTotal)));
        tvAmountTax.setText(String.valueOf(df2.format(taxAmount)));
        tvAmountCharges.setText(String.valueOf(df2.format(chargesAmount)));
        tvAmountGrandTotal.setText(String.valueOf(df2.format(grandTotal)));
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

    private void calculateDiscount(){
        double discountAmount=0;
        int discountPercent=0;
        if(etDiscountAmount.getText().toString().length()!=0) discountAmount=Double.valueOf(etDiscountAmount.getText().toString());
        if(etDiscountPercent.getText().toString().length()!=0) discountPercent=Integer.parseInt(etDiscountPercent.getText().toString());
        if(discountPercent<0){
            systemSetting.showMessage(SystemSetting.WARNING,"Percent should be greater than zero!",context,getLayoutInflater());
            etDiscountPercent.requestFocus();
            return;
        }
        if(discountPercent>100){
            systemSetting.showMessage(SystemSetting.WARNING,"Percent should be less than one hundred!",context,getLayoutInflater());
            etDiscountPercent.requestFocus();
            return;
        }
        //double subTotal=Double.valueOf(tvAmountSubTotal.getText().toString());
        discount =(discountPercent*subTotal)/100;
        discount=discount+discountAmount;

        tvAmountDiscount.setText(String.valueOf(df2.format(discount)));
        //double tax=Double.valueOf(tvAmountTax.getText().toString());
        //double charges=Double.valueOf(tvAmountCharges.getText().toString());
        //double grandTotal=subTotal+tax+charges;
        grandTotal=grandTotal-discount;
        tvAmountGrandTotal.setText(String.valueOf(df2.format(grandTotal)));
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
                if(takeAwayType==0){
                    payAndPreparePrint("");
                }else{
                    showOrderNumberDialog();
                }
                passwordRequireDialog.dismiss();
            }
        });
    }

    private void showOrderNumberDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_order_number, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final EditText etOrderNumber=(EditText) passwordView.findViewById(R.id.etOrderNumber);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

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
                payAndPreparePrint(etOrderNumber.getText().toString());
                passwordRequireDialog.dismiss();
            }
        });
    }

    private void payAndPreparePrint(String orderNumber){
        payBill();
        Intent i=new Intent(getApplicationContext(),PrintBillActivity.class);
        i.putExtra("slipid",billSlipID);
        i.putExtra("tableid",billTableID);
        i.putExtra("table",billTableName);
        i.putExtra("waiter",billWaiterName);
        i.putExtra("datetime",date+" "+currentTime);
        String subtotal=new DecimalFormat("#").format(Double.parseDouble(tvAmountSubTotal.getText().toString()));
        i.putExtra("subtotal",subtotal);
        String tax=new DecimalFormat("#").format(Double.parseDouble(tvAmountTax.getText().toString()));
        i.putExtra("tax",tax);
        String charges=new DecimalFormat("#").format(Double.parseDouble(tvAmountCharges.getText().toString()));
        i.putExtra("charges",charges);
        String grandtotal=new DecimalFormat("#").format(Double.parseDouble(tvAmountGrandTotal.getText().toString()));
        i.putExtra("grandtotal",grandtotal);
        String discount=new DecimalFormat("#").format(Double.parseDouble(tvAmountDiscount.getText().toString()));
        i.putExtra("discount",discount);
        if(etPaidAmount.getText().toString().length()!=0) {
            String paid = new DecimalFormat("#").format(Double.parseDouble(etPaidAmount.getText().toString()));
            i.putExtra("paid", paid);
        }
        else i.putExtra("paid","0");

        Double netAmount=Double.parseDouble(tvAmountGrandTotal.getText().toString());
        Double paidAmount,changeAmount=0.0;
        if(etPaidAmount.getText().toString().length()!=0){
            paidAmount=Double.parseDouble(etPaidAmount.getText().toString());
            changeAmount=paidAmount-netAmount;
        }
        String change=new DecimalFormat("#").format(changeAmount);
        i.putExtra("change",change);
        i.putExtra("StartTime",startTime);
        i.putExtra("EndTime",endTime);
        i.putExtra("OrderNumber",orderNumber);
        i.putExtra("TakeAwayType",takeAwayType);
        startActivity(i);
        finish();
    }

    private void setLayoutResource(){
        tvBillTitle=(TextView)findViewById(R.id.tvBillTitle);
        tvHeaderItemName=(TextView)findViewById(R.id.tvHeaderItemName);
        tvHeaderQuantity=(TextView)findViewById(R.id.tvHeaderQuantity);
        tvHeaderAmount=(TextView)findViewById(R.id.tvHeaderAmount);
        tvHeaderPrice=(TextView)findViewById(R.id.tvHeaderPrice);
        tvLabelDiscountPercent =(TextView)findViewById(R.id.tvDiscountPercent);
        tvLabelDiscountAmount =(TextView)findViewById(R.id.tvDiscountAmount);
        tvLabelSubTotal=(TextView)findViewById(R.id.tvLabelSubTotal);
        tvAmountSubTotal=(TextView)findViewById(R.id.tvAmountSubTotal);
        tvLabelCharges=(TextView)findViewById(R.id.tvLabelCharges);
        tvAmountCharges=(TextView)findViewById(R.id.tvAmountCharges);
        tvLabelTax=(TextView)findViewById(R.id.tvLabelTax);
        tvAmountTax=(TextView)findViewById(R.id.tvAmountTax);
        tvLabelDiscount=(TextView)findViewById(R.id.tvLabelDiscount);
        tvAmountDiscount=(TextView)findViewById(R.id.tvAmountDiscount);
        tvLabelGrandTotal=(TextView)findViewById(R.id.tvLabelGrandTotal);
        tvAmountGrandTotal=(TextView)findViewById(R.id.tvAmountGrandTotal);
        lvBillItem=(ListView)findViewById(R.id.lvBillItem);
        etDiscountPercent=(EditText) findViewById(R.id.etDiscountPercent);
        etDiscountAmount=(EditText)findViewById(R.id.etDiscountAmount);
        etPaidAmount=(EditText)findViewById(R.id.etAmountPaidTotal);
        btnDiscountCalculate=(Button)findViewById(R.id.btnDiscountCalculate);
        btnPaidCalculate=(Button)findViewById(R.id.btnPaidCalculate);
        btnPay=(Button)findViewById(R.id.btnPay);
        tvLabelPaid =(TextView)findViewById(R.id.tvLabelPaidTotal);
        tvLabelChange =(TextView)findViewById(R.id.tvLabelChangeTotal);
        tvAmountChange =(TextView)findViewById(R.id.tvAmountChangeTotal);
        swtChargesOff=(Switch)findViewById(R.id.swtChargesOff);
    }

    private int getMaxSizeFromOrder(){
        int size=0,newSize;
        for(int i=0;i<lstViewOrder.size();i++){
            newSize=lstViewOrder.get(i).getItemName().length()+lstViewOrder.get(i).getStringQty().length()+String.valueOf(lstViewOrder.get(i).getAmount()).length();
            if(size<newSize)size=newSize;
        }
        return size;
    }

    // name's length=26,qty's length=6,amount's length=15,line's length=47
    private void printBySocket(){
        final String printString;
        String tranString="",name,qty,amount,name1,name2;
        int nameLength,qtyLength,amountLength,requireSpaceCount,nameSize=26,qtySize=6,amountSize=15;
        //getPrinterSetting();
        for(int i=0;i<lstViewOrder.size();i++){
            name=lstViewOrder.get(i).getItemName();
            nameLength=name.length();
            if(nameLength<nameSize){
                requireSpaceCount=nameSize-nameLength;
                for(int n=0;n<requireSpaceCount;n++){
                    name+=" ";
                }

                qty=lstViewOrder.get(i).getStringQty();
                qtyLength=qty.length();
                if(qtyLength<qtySize){
                    requireSpaceCount=qtySize-qtyLength;
                    for(int n=0;n<requireSpaceCount;n++){
                        qty=" "+qty;
                    }
                }
                amount=String.valueOf(lstViewOrder.get(i).getAmount());
                amountLength=amount.length();
                if(amountLength<amountSize){
                    requireSpaceCount=amountSize-amountLength;
                    for(int n=0;n<requireSpaceCount;n++){
                        amount=" "+amount;
                    }
                }
                tranString+=name+qty+amount+"\n";
            }
            else{ // name length 26 or >26
                //float fNameCount;
                name1=name.substring(0,25);
                name2=name.substring(25);  // start index 25 place 26

                qty=lstViewOrder.get(i).getStringQty();
                qtyLength=qty.length();
                if(qtyLength<qtySize){
                    requireSpaceCount=qtySize-qtyLength;
                    for(int n=0;n<requireSpaceCount;n++){
                        qty=" "+qty;
                    }
                }
                amount=String.valueOf(lstViewOrder.get(i).getAmount());
                amountLength=amount.length();
                if(amountLength<amountSize){
                    requireSpaceCount=amountSize-amountLength;
                    for(int n=0;n<requireSpaceCount;n++){
                        amount=" "+amount;
                    }
                }

                /**fNameCount=nameLength/25;
                 if(fNameCount==Math.round(fNameCount)) {
                 for(int k=0;k<fNameCount;k++){ // integer
                 }
                 }
                 else{
                 fNameCount=fNameCount+1;  // float +1
                 for(int k=0;k<fNameCount;k++){
                 }
                 }**/

                tranString+=name1+" "+qty+amount+"\n";
                tranString+=name2+"\n";
            }
        }
        calendar=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        date=dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat=new SimpleDateFormat(SystemSetting.TIME_FORMAT);
        time=timeFormat.format(calendar.getTime());

        int subTotalLength=tvAmountSubTotal.getText().toString().length();
        String subTotal=tvAmountSubTotal.getText().toString();
        if(subTotalLength<amountSize){
            requireSpaceCount=amountSize-subTotalLength;
            for(int n=0;n<requireSpaceCount;n++){
                subTotal=" "+subTotal;
            }
        }

        int taxTotalLength=tvAmountTax.getText().toString().length();
        String taxTotal=tvAmountTax.getText().toString();
        if(taxTotalLength<amountSize){
            requireSpaceCount=amountSize-taxTotalLength;
            for(int n=0;n<requireSpaceCount;n++){
                taxTotal=" "+taxTotal;
            }
        }

        int chargesTotalLength=tvAmountCharges.getText().toString().length();
        String chargesTotal=tvAmountCharges.getText().toString();
        if(chargesTotalLength<amountSize){
            requireSpaceCount=amountSize-chargesTotalLength;
            for(int n=0;n<requireSpaceCount;n++){
                chargesTotal=" "+chargesTotal;
            }
        }

        int discountTotalLength=tvAmountDiscount.getText().toString().length();
        String discountTotal=tvAmountDiscount.getText().toString();
        if(discountTotalLength<amountSize){
            requireSpaceCount=amountSize-discountTotalLength;
            for(int n=0;n<requireSpaceCount;n++){
                discountTotal=" "+discountTotal;
            }
        }

        int grandTotalLength=tvAmountGrandTotal.getText().toString().length();
        String grandTotal=tvAmountGrandTotal.getText().toString();
        if(grandTotalLength<amountSize){
            requireSpaceCount=amountSize-grandTotalLength;
            for(int n=0;n<requireSpaceCount;n++){
                grandTotal=" "+grandTotal;
            }
        }

        printString="\n\n"+date+"  "+time+"  "+billTableName+"  \n\n"+
                "Description               "+"   Qty"+"         Amount\n"+"_______________________________________________\n"+tranString+"_______________________________________________\n"+
                "            Sub Total           "+subTotal+"\n"+"            Commercial Tax      "+taxTotal+"\n"+
                "            Service Charges     "+chargesTotal+"\n"+"            Discount            "+discountTotal+"\n"+
                "_______________________________________________\n"+"            Total Amount        "+grandTotal+"\n\n"+"                    Thanks!                    \n\n\n";


        Thread thread=new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    Socket socket = new Socket(printerIP, portNumber);

                    PrintWriter writer=new PrintWriter(socket.getOutputStream(),true);
                    writer.write(printString);

                    /**PrintWriter out = new PrintWriter(new BufferedWriter(
                     new OutputStreamWriter(socket.getOutputStream(),
                     "UTF-8")),
                     true);
                     byte[] byteArray = printString.getBytes(Charset.forName("UTF-8"));
                     out.write(byteArray.toString() + "\n");**/

                    writer.close();
                    socket.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void connectPrinter(){
        if(db.getFeatureResult(FeatureList.fUseMultiPrinter)!=1) { // not use multi printer
            Cursor cur = db.getPrinterSetting();
            if (cur.moveToFirst()) {
                printerIPAddress = cur.getString(0);
            }
        }else{  // use multi printer
            printerIPAddress=db.getNetworkPrinter();
        }

        if(printerIPAddress.length()!=0) {
            wifiPort = PrinterWiFiPort.getInstance();
            try {
                wifiConn(printerIPAddress);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }else{
            //systemSetting.showMessage(SystemSetting.INFO,"Not found Network Printer!",context,getLayoutInflater());
            confirmMessage="Are you sure you want to pay bill for table "+billTableName+"?";
            showConfirmDialog();
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
        private final ProgressDialog dialog = new ProgressDialog(BillActivity.this);

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
                printerIPAddress = params[0];
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
                if(dialog.isShowing())
                    dialog.dismiss();
                printer_message="PRINTER ONLINE!";
                confirmMessage="Are you sure you want to pay bill for table "+billTableName+"?";
                showConfirmDialog();
            }
            else
            {
                if(dialog.isShowing())
                    dialog.dismiss();
                printer_message="PRINTER OFFLINE!";
                AlertView.showAlert("Failed", "Check Devices!"+ioException, context);
            }
            super.onPostExecute(result);
        }
    }
}
