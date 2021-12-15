package com.bosictsolution.waiterone;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import common.DBHelper;
import common.SystemSetting;
import data.TransactionData;

public class ExportImportActivity extends AppCompatActivity {

    Button btnImportFromCSV, btnExportToCSV,btnExportTranToCSV,btnImportTranFromCSV,btnToDate,btnFromDate;

    DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    final Context context = this;
    private Calendar cCalendar;

    int fromToDate;
    boolean tranFilter;
    String fromDate,toDate;
    private final int IMPORT_DATA=1,EXPORT_DATA=2,IMPORT_TRAN=3,EXPORT_TRAN=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_import);

        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        cCalendar=Calendar.getInstance();
        setLayoutResource();

        btnExportToCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(EXPORT_DATA,"Are you sure you want to export data to csv files?");
            }
        });
        btnImportFromCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(IMPORT_DATA,"Current data will be removed! Are you sure you want to import data from csv files?");
            }
        });
        btnExportTranToCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(EXPORT_TRAN,"Are you sure you want to export transactions to csv files?");
            }
        });
        btnImportTranFromCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(IMPORT_TRAN,"Current transactions will be removed! Are you sure you want to import transactions from csv files?");
            }
        });
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

    private void setLayoutResource(){
        btnExportToCSV =(Button)findViewById(R.id.btnExportToCSV);
        btnImportFromCSV =(Button)findViewById(R.id.btnImportFromCSV);
        btnExportTranToCSV =(Button)findViewById(R.id.btnExportTranToCSV);
        btnImportTranFromCSV =(Button)findViewById(R.id.btnImportTranFromCSV);
    }

    public class ExportData extends AsyncTask<String ,String, String> {
        private final ProgressDialog dialog = new ProgressDialog(ExportImportActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting data...");
            this.dialog.show();
        }

        protected String doInBackground(final String... args){
            SystemSetting export=new SystemSetting();
            export.dataBackup(db,false,null);

            return "";
        }

        @Override
        protected void onPostExecute(final String success) {

            if (this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            if (success.isEmpty()){
                systemSetting.showMessage(SystemSetting.SUCCESS,"Export successful!",context,getLayoutInflater());
            }
            else {
                systemSetting.showMessage(SystemSetting.ERROR,"Export failed!",context,getLayoutInflater());
            }
        }
    }

    private class ExportTransaction extends AsyncTask<String ,String, String> {
        private final ProgressDialog dialog = new ProgressDialog(ExportImportActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting Transaction...");
            this.dialog.show();
        }

        protected String doInBackground(final String... args){
            File exportDir = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File fileMaster = new File(exportDir, "MasterSale.csv");
            File fileTran = new File(exportDir, "TranSale.csv");

            try {
                /** for master **/
                fileMaster.createNewFile();
                CSVWriter csvWriteMaster = new CSVWriter(new FileWriter(fileMaster));

                Cursor curMaster;
                if(tranFilter)curMaster=db.getMasterSaleByDate(fromDate,toDate);
                else curMaster=db.getMasterSale();

                List<TransactionData> lstMasterData=new ArrayList<>();
                while(curMaster.moveToNext()){
                    TransactionData data=new TransactionData();
                    data.setTranid(curMaster.getInt(0));
                    data.setDate(curMaster.getString(1));
                    data.setVouno(curMaster.getString(2));
                    data.setTableid(curMaster.getInt(3));
                    data.setWaiterid(curMaster.getInt(4));
                    data.setSubTotal(curMaster.getDouble(5));
                    data.setTax(curMaster.getDouble(6));
                    data.setCharges(curMaster.getDouble(7));
                    data.setDiscount(curMaster.getDouble(8));
                    data.setGrandTotal(curMaster.getDouble(9));
                    data.setTime(curMaster.getString(10));
                    data.setSlipid(curMaster.getInt(11));
                    data.setStartTime(curMaster.getString(12));
                    data.setEndTime(curMaster.getString(13));
                    lstMasterData.add(data);
                }

                String headerMaster[] ={"TranID", "Date", "VouNo","TableID","WaiterID","SubTotal","Tax","Charges","Discount","GrandTotal","Time","SlipID","StartTime","EndTime"};
                csvWriteMaster.writeNext(headerMaster);

                for(int i=0;i<lstMasterData.size();i++){
                    String data[] ={String.valueOf(lstMasterData.get(i).getTranid()), lstMasterData.get(i).getDate(), lstMasterData.get(i).getVouno(),String.valueOf(lstMasterData.get(i).getTableid()),String.valueOf(lstMasterData.get(i).getWaiterid()),String.valueOf(lstMasterData.get(i).getSubTotal()),String.valueOf(lstMasterData.get(i).getTax()),String.valueOf(lstMasterData.get(i).getCharges()),String.valueOf(lstMasterData.get(i).getDiscount()),String.valueOf(lstMasterData.get(i).getGrandTotal()),lstMasterData.get(i).getTime(),String.valueOf(lstMasterData.get(i).getSlipid()),lstMasterData.get(i).getStartTime(),lstMasterData.get(i).getEndTime()};
                    csvWriteMaster.writeNext(data);
                }
                csvWriteMaster.close();

                /** for tran **/
                fileTran.createNewFile();
                CSVWriter csvWriteTran = new CSVWriter(new FileWriter(fileTran));

                Cursor curTran;
                if(tranFilter)curTran=db.getTranSaleByDate(fromDate,toDate);
                else curTran=db.getTranSale();

                List<TransactionData> lstTranData=new ArrayList<>();
                while(curTran.moveToNext()){
                    TransactionData data=new TransactionData();
                    data.setTranid(curTran.getInt(0));
                    data.setSrNo(curTran.getInt(1));
                    data.setItemid(curTran.getString(2));
                    data.setItemName(curTran.getString(3));
                    data.setIntegerQty(curTran.getInt(4));
                    data.setSalePrice(curTran.getDouble(5));
                    data.setAmount(curTran.getDouble(6));
                    data.setOrderTime(curTran.getString(7));
                    data.setTaste(curTran.getString(8));
                    data.setCounterID(curTran.getInt(9));
                    data.setTime(curTran.getString(10));
                    data.setDate(curTran.getString(11));
                    lstTranData.add(data);
                }

                String headerTran[] ={"TranID", "SrNo","ItemID","ItemName","Quantity","SalePrice","Amount","OrderTime","Taste","CounterID","Time","Date"};
                csvWriteTran.writeNext(headerTran);

                for(int i=0;i<lstTranData.size();i++){
                    String data[] ={String.valueOf(lstTranData.get(i).getTranid()),String.valueOf(lstTranData.get(i).getSrNo()),lstTranData.get(i).getItemid(),lstTranData.get(i).getItemName(),String.valueOf(lstTranData.get(i).getIntegerQty()),String.valueOf(lstTranData.get(i).getSalePrice()),String.valueOf(lstTranData.get(i).getAmount()),lstTranData.get(i).getOrderTime(),lstTranData.get(i).getTaste(),String.valueOf(lstTranData.get(i).getCounterID()),lstTranData.get(i).getTime(),lstTranData.get(i).getDate()};
                    csvWriteTran.writeNext(data);
                }
                csvWriteTran.close();

                return "";
            }
            catch (IOException e){
                Log.e("ExportImportActivity", e.getMessage(), e);
                return "";
            }
        }

        @Override
        protected void onPostExecute(final String success) {

            if (this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            if (success.isEmpty()){
                systemSetting.showMessage(SystemSetting.SUCCESS,"Export successful!",context,getLayoutInflater());
            }
            else {
                systemSetting.showMessage(SystemSetting.ERROR,"Export failed!",context,getLayoutInflater());
            }
        }
    }

    private class ImportData extends AsyncTask<String ,String, String> {
        private final ProgressDialog dialog = new ProgressDialog(ExportImportActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Importing data...");
            this.dialog.show();
        }

        protected String doInBackground(final String... args){
            SystemSetting importData=new SystemSetting();
            importData.dataRestore(db,false);

            return "";
        }

        @Override
        protected void onPostExecute(final String success) {

            if (this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            if (success.isEmpty()){
                systemSetting.showMessage(SystemSetting.SUCCESS,"Import successful!",context,getLayoutInflater());
                Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
                finish();
            }
            else {
                systemSetting.showMessage(SystemSetting.ERROR,"Import failed!",context,getLayoutInflater());
            }
        }
    }

    private class ImportTransaction extends AsyncTask<String ,String, String> {
        String startTime,endTime;
        private final ProgressDialog dialog = new ProgressDialog(ExportImportActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Importing Transaction...");
            this.dialog.show();
        }

        protected String doInBackground(final String... args){
            File importDir = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
            if (!importDir.exists()) {
                importDir.mkdirs();
            }

            File fileMaster = new File(importDir, "MasterSale.csv");
            File fileTran = new File(importDir, "TranSale.csv");

            try {

                CSVReader csvReadMaster = new CSVReader(new FileReader(fileMaster));
                String [] nextLineMaster;
                db.truncateMasterSale();
                csvReadMaster.readNext();
                while ((nextLineMaster = csvReadMaster.readNext()) != null) {
                    int tranid=Integer.parseInt(nextLineMaster[0]);
                    String date=nextLineMaster[1];
                    String vouno=nextLineMaster[2];
                    int tableid=Integer.parseInt(nextLineMaster[3]);
                    int waiterid=Integer.parseInt(nextLineMaster[4]);
                    double subtotal=Double.parseDouble(nextLineMaster[5]);
                    double tax=Double.parseDouble(nextLineMaster[6]);
                    double charges=Double.parseDouble(nextLineMaster[7]);
                    double discount=Double.parseDouble(nextLineMaster[8]);
                    double grandtotal=Double.parseDouble(nextLineMaster[9]);
                    String time=nextLineMaster[10];
                    int slipid=Integer.parseInt(nextLineMaster[11]);
                    if(nextLineMaster.length>12) {
                        startTime = nextLineMaster[12];
                        endTime = nextLineMaster[13];
                    }
                    db.insertMasterSale(tranid,date,vouno,tableid,waiterid,subtotal,tax,charges,discount,grandtotal,time,slipid,startTime,endTime);
                }

                CSVReader csvReadTran = new CSVReader(new FileReader(fileTran));
                String [] nextLineTran;
                db.truncateTranSale();
                csvReadTran.readNext();
                while ((nextLineTran = csvReadTran.readNext()) != null) {
                    int tranid=Integer.parseInt(nextLineTran[0]);
                    int srno=Integer.parseInt(nextLineTran[1]);
                    String itemid=nextLineTran[2];
                    String itemname=nextLineTran[3];
                    double qty=Double.parseDouble(nextLineTran[4]);
                    double saleprice=Double.parseDouble(nextLineTran[5]);
                    double amount=Double.parseDouble(nextLineTran[6]);
                    String orderTime=nextLineTran[7];
                    String taste=nextLineTran[8];
                    int counterid=Integer.parseInt(nextLineTran[9]);
                    String time=nextLineTran[10];
                    db.insertTranSale(tranid,srno,itemid,itemname,qty,saleprice,amount,orderTime,taste,counterid,time);
                }

                db.updateSysTranIDByImport();
                db.updateVouFormatByImport();

                return "";
            }
            catch (IOException e){
                Log.e("ExportImportActivity", e.getMessage(), e);
                return "";
            }
        }

        @Override
        protected void onPostExecute(final String success) {

            if (this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            if (success.isEmpty()){
                systemSetting.showMessage(SystemSetting.SUCCESS,"Import successful!",context,getLayoutInflater());
            }
            else {
                systemSetting.showMessage(SystemSetting.ERROR,"Import failed!",context,getLayoutInflater());
            }
        }
    }

    private void showFilterDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_date_filter, null);
        AlertDialog.Builder passwordDialog=new AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        btnFromDate=(Button)passwordView.findViewById(R.id.btnFromDate);
        btnToDate=(Button)passwordView.findViewById(R.id.btnToDate);
        final Button btnExportAll=(Button)passwordView.findViewById(R.id.btnExportAll);
        final Button btnExport=(Button)passwordView.findViewById(R.id.btnExport);

        setDateButtonText();
        passwordDialog.setCancelable(true);
        final AlertDialog filterDialog=passwordDialog.create();
        filterDialog.show();

        btnFromDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                fromToDate=0;
                showDialog(1);
            }
        });
        btnToDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                fromToDate=1;
                showDialog(1);
            }
        });
        btnExportAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                tranFilter=false;
                filterDialog.dismiss();
                ExportTransaction task=new ExportTransaction();
                task.execute("");
            }
        });
        btnExport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                tranFilter=true;
                fromDate=btnFromDate.getText().toString();
                toDate=btnToDate.getText().toString();
                filterDialog.dismiss();
                ExportTransaction task=new ExportTransaction();
                task.execute("");
            }
        });
    }

    private void showConfirmDialog(final int type,String confirmMessage){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_confirm, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final TextView tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
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
                if(type==IMPORT_DATA){
                    ImportData task=new ImportData();
                    task.execute("");
                }else if(type==EXPORT_DATA){
                    ExportData task=new ExportData();
                    task.execute("");
                }else if(type==IMPORT_TRAN){
                    ImportTransaction task=new ImportTransaction();
                    task.execute("");
                }else if(type==EXPORT_TRAN){
                    showFilterDialog();
                }
                passwordRequireDialog.dismiss();
            }
        });
    }

    @Override
    protected AlertDialog onCreateDialog(int id){
        return showDatePicker();
    }

    private DatePickerDialog showDatePicker(){
        DatePickerDialog datePicker=new DatePickerDialog(ExportImportActivity.this,new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cCalendar.set(Calendar.YEAR,year);
                cCalendar.set(Calendar.MONTH, monthOfYear);
                cCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateButtonText();
            }
        },cCalendar.get(Calendar.YEAR),cCalendar.get(Calendar.MONTH),cCalendar.get(Calendar.DAY_OF_MONTH));
        return datePicker;
    }

    private void updateDateButtonText(){
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        String dateForButton=dateFormat.format(cCalendar.getTime());
        if(fromToDate==1) btnToDate.setText(dateForButton);
        else btnFromDate.setText(dateForButton);
    }

    private void setDateButtonText(){
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        String dateForButton=dateFormat.format(cCalendar.getTime());
        btnToDate.setText(dateForButton);
        btnFromDate.setText(dateForButton);
    }
}
