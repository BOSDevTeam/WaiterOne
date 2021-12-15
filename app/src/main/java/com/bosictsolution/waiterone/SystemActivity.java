package com.bosictsolution.waiterone;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import common.DBHelper;
import common.ServerConnection;
import common.SystemSetting;
import data.TransactionData;

public class SystemActivity extends AppCompatActivity {

    Button btnDataSetup,btnCSVImportExport,btnServerImport,btnDeleteTran,btnBackupData,btnResetSlipId, btnCustomBackupData,
            btnToDate,btnFromDate,btnDeleteVoucher,btnDelExtraTran;
    final Context context = this;
    private ProgressDialog progressDialog;
    private DBHelper db;
    String serverIP,serverDB,serverUser,serverPassword;
    ServerConnection serverConnection;
    static final int SERVER_IMPORT=1,RESET_SLIPID=2,BACKUP_DATA=3;
    int confirmType,fromToDate;
    private Calendar cCalendar;
    SystemSetting systemSetting=new SystemSetting();
    private int PICKFILE_REQUEST_CODE=7;
    public static String customBackupPath;
    boolean isCustomBackup,isDeleteVoucher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        cCalendar=Calendar.getInstance();
        setLayoutResource();

        btnDataSetup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(getApplicationContext(),SetupActivity.class);
                startActivity(intent);
            }
        });
        btnCSVImportExport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(getApplicationContext(),ExportImportActivity.class);
                startActivity(intent);
            }
        });
        btnServerImport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                confirmType=SERVER_IMPORT;
                showBackupDialog();
            }
        });
        btnDeleteTran.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showFilterDialog();
            }
        });
        btnDeleteVoucher.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isDeleteVoucher=true;
                showOwnerPassDialog();
            }
        });
        btnBackupData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isCustomBackup=false;
                confirmType=BACKUP_DATA;
                showConfirmDialog("Data Backup Continue?");
            }
        });
        btnResetSlipId.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                confirmType=RESET_SLIPID;
                showConfirmDialog("Are you sure you want to reset SlipID?");
            }
        });
        btnCustomBackupData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isCustomBackup=true;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });
        btnDelExtraTran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDeleteVoucher=false;
                showOwnerPassDialog();
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case 7:
                if(resultCode==RESULT_OK){
                    customBackupPath = data.getData().getPath();
                    Backup backup=new Backup();
                    backup.execute("");
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void showBackupDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_confirm, null);
        AlertDialog.Builder passwordDialog=new AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final TextView tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        btnCancel.setText("No");
        btnOK.setText("Yes");
        tvConfirmMessage.setText("Current data will be deleted in Server Import! do you want to backup data into CSV file?");

        passwordDialog.setCancelable(true);
        final AlertDialog passwordRequireDialog=passwordDialog.create();
        passwordRequireDialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                passwordRequireDialog.dismiss();
                showConfirmDialog("Backup Canceled! Server Import Continue?");
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                passwordRequireDialog.dismiss();
                ExportData exportData =new ExportData();
                exportData.execute("");
            }
        });
    }

    private void showConfirmDialog(final String confirmMsg){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_confirm, null);
        AlertDialog.Builder passwordDialog=new AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final TextView tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        tvConfirmMessage.setText(confirmMsg);

        passwordDialog.setCancelable(true);
        final AlertDialog passwordRequireDialog=passwordDialog.create();
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
                if(confirmType==SERVER_IMPORT) {
                    showServerPropertyDialog();
                }else if(confirmType==RESET_SLIPID){
                    if(db.resetSlipId()){
                        systemSetting.showMessage(SystemSetting.SUCCESS,"Success! Reset SlipID is (1)!",context,getLayoutInflater());
                    }
                }else if(confirmType==BACKUP_DATA){
                    Backup backup=new Backup();
                    backup.execute("");
                }
                passwordRequireDialog.dismiss();
            }
        });
    }

    private void showServerPropertyDialog(){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dg_server_property, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);
        final EditText etIAddress=(EditText)view.findViewById(R.id.etIPAddress);
        final EditText etUser=(EditText)view.findViewById(R.id.etServerUser);
        final EditText etPassword=(EditText)view.findViewById(R.id.etServerPassword);
        final EditText etDatabaseName=(EditText)view.findViewById(R.id.etDatabaseName);
        final Button btnOK=(Button)view.findViewById(R.id.btnOK);
        final Button btnCancel=(Button)view.findViewById(R.id.btnCancel);

        dialog.setCancelable(true);
        final android.app.AlertDialog serverPropertyDialog=dialog.create();
        serverPropertyDialog.show();

        Cursor cur=db.getIPSetting();
        if(cur.moveToNext()){
            etIAddress.setText(cur.getString(0));
            etUser.setText(cur.getString(1));
            etPassword.setText(cur.getString(2));
            etDatabaseName.setText(cur.getString(3));
        }
        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                serverIP=etIAddress.getText().toString();
                if(serverIP.length()==0){
                    systemSetting.showMessage(SystemSetting.INFO,"Enter IP Address!",context,getLayoutInflater());
                    return;
                }
                serverDB=etDatabaseName.getText().toString();
                if(serverDB.length()==0){
                    systemSetting.showMessage(SystemSetting.INFO,"Enter Database Name!",context,getLayoutInflater());
                    return;
                }
                serverUser=etUser.getText().toString();
                if(serverUser.length()==0){
                    systemSetting.showMessage(SystemSetting.INFO,"Enter User!",context,getLayoutInflater());
                    return;
                }
                serverPassword=etPassword.getText().toString();
                if(serverPassword.length()==0){
                    systemSetting.showMessage(SystemSetting.INFO,"Enter Password!",context,getLayoutInflater());
                    return;
                }
                db.deleteIPSetting();
                db.insertIPSetting(serverIP, serverUser, serverPassword,serverDB);
                ImportServerData importServerData =new ImportServerData();
                importServerData.execute();
                serverPropertyDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                serverPropertyDialog.dismiss();
            }
        });
    }

    private void showOwnerPassDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_password, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final EditText etPassword=(EditText)passwordView.findViewById(R.id.etPassword);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        etPassword.setHint("Owner Password");
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
                String ownerPassword="owner";
                String password=etPassword.getText().toString();
                if(password.length()==0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Enter Password!",context,getLayoutInflater());
                    return;
                }else if(!password.equals(ownerPassword)){
                    systemSetting.showMessage(SystemSetting.ERROR,"Invalid Password!",context,getLayoutInflater());
                    return;
                }else{
                    if(isDeleteVoucher) {
                        showVouDateFilter();
                    }else{
                        Intent intent = new Intent(SystemActivity.this,ExtraTranActivity.class);
                        startActivity(intent);
                    }
                    passwordRequireDialog.dismiss();
                }
            }
        });
    }

    private void showVouDateFilter() {
        LayoutInflater reg = LayoutInflater.from(context);
        View passwordView = reg.inflate(R.layout.dg_date_filter, null);
        AlertDialog.Builder passwordDialog = new AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        btnFromDate = (Button) passwordView.findViewById(R.id.btnFromDate);
        btnToDate = (Button) passwordView.findViewById(R.id.btnToDate);
        final Button btnViewAll = (Button) passwordView.findViewById(R.id.btnExportAll);
        btnViewAll.setText("View All");
        final Button btnFilter = (Button) passwordView.findViewById(R.id.btnExport);
        btnFilter.setText("Filter");

        setDateButtonText();

        passwordDialog.setCancelable(true);
        final AlertDialog filterDialog = passwordDialog.create();
        filterDialog.show();

        btnFromDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                fromToDate = 0;
                showDialog(1);
            }
        });
        btnToDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                fromToDate = 1;
                showDialog(1);
            }
        });
        btnViewAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                filterDialog.dismiss();
                Cursor cur = db.getMasterSaleData();
                if (cur.moveToFirst()) {
                    Intent i = new Intent(SystemActivity.this, TransactionActivity.class);
                    startActivity(i);
                } else {
                    systemSetting.showMessage(SystemSetting.INFO, "No Sale Vouchers!", context, getLayoutInflater());
                }
            }
        });
        btnFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String fromDate = btnFromDate.getText().toString();
                String toDate = btnToDate.getText().toString();
                Cursor cur = db.getMasterSaleDataByDate(fromDate, toDate);
                if (cur.moveToFirst()) {
                    filterDialog.dismiss();
                    Intent i = new Intent(SystemActivity.this, TransactionActivity.class);
                    i.putExtra("FromDate", fromDate);
                    i.putExtra("ToDate", toDate);
                    startActivity(i);
                } else {
                    systemSetting.showMessage(SystemSetting.INFO, "No Sale Vouchers by Filtered Date Range!", context, getLayoutInflater());
                }
            }
        });
    }

    private void showFilterDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dg_date_filter, null);
        AlertDialog.Builder passwordDialog=new AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        btnFromDate=(Button)passwordView.findViewById(R.id.btnFromDate);
        btnToDate=(Button)passwordView.findViewById(R.id.btnToDate);
        final Button btnDeleteAll=(Button)passwordView.findViewById(R.id.btnExportAll);
        btnDeleteAll.setText("DELETE(ALL)");
        final Button btnDelete=(Button)passwordView.findViewById(R.id.btnExport);
        btnDelete.setText("DELETE");

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
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                filterDialog.dismiss();
                if(db.deleteTran()) {
                    systemSetting.showMessage(SystemSetting.SUCCESS,"All Transactions Deleted!",context,getLayoutInflater());
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String fromDate=btnFromDate.getText().toString();
                String toDate=btnToDate.getText().toString();
                filterDialog.dismiss();
                if(db.deleteTranByDate(fromDate,toDate)){
                    systemSetting.showMessage(SystemSetting.SUCCESS,"Transactions Deleted!",context,getLayoutInflater());
                }
            }
        });
    }

    @Override
    protected AlertDialog onCreateDialog(int id){
        return showDatePicker();
    }

    private DatePickerDialog showDatePicker(){
        DatePickerDialog datePicker=new DatePickerDialog(SystemActivity.this,new DatePickerDialog.OnDateSetListener() {

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

    public class ImportServerData extends AsyncTask<String,String,String> {
        int waiterid,tabletypeid,tableid,tasteid,mainmenuid,submenuid,outoforder,counterid,msg_type,stypeid;
        String waitername,tabletypename,tablename,mainmenuname,submenuname,itemname,tastename,password,itemid,sortcode,msg="",stypename;
        double price;
        Boolean isSuccess=false;
        @Override
        protected String doInBackground(String... params){
            try{
                serverConnection=new ServerConnection();
                Connection con=serverConnection.CONN(serverIP,serverDB,serverUser,serverPassword);
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=SystemSetting.ERROR;
                }else{
                    db.truncateSetupTables();
                    String select_waiter="select userid,name,password from Users";
                    String select_tabletype="select TableTypeId,TableTypeName from TableType";
                    String select_table="select Table_Name_ID,Table_Name,TableType from table_name";
                    String select_mainmenu="select class,name,counterid from InvMainMenu order by sortcode";
                    String select_submenu="select category,class,name,sortcode from InvSubMenu order by sortcode";
                    String select_item="select ItemID,Name,SubMenuID,Saleprice,Stype,OutofOrder from InvItem";
                    String select_stype="select SID,SName from Stype";
                    String select_taste="select TID,TName from TasteCode";

                    Statement st_waiter=con.createStatement();
                    Statement st_tabletype=con.createStatement();
                    Statement st_table=con.createStatement();
                    Statement st_mainmenu=con.createStatement();
                    Statement st_submenu=con.createStatement();
                    Statement st_item=con.createStatement();
                    Statement st_taste=con.createStatement();
                    Statement st_stype=con.createStatement();

                    ResultSet rs_waiter=st_waiter.executeQuery(select_waiter);
                    ResultSet rs_tabletype=st_tabletype.executeQuery(select_tabletype);
                    ResultSet rs_table=st_table.executeQuery(select_table);
                    ResultSet rs_mainmenu=st_mainmenu.executeQuery(select_mainmenu);
                    ResultSet rs_submenu=st_submenu.executeQuery(select_submenu);
                    ResultSet rs_item=st_item.executeQuery(select_item);
                    ResultSet rs_taste=st_taste.executeQuery(select_taste);
                    ResultSet rs_stype=st_stype.executeQuery(select_stype);

					/* for waiter */
                    while(rs_waiter.next()){
                        waiterid= rs_waiter.getInt(1);
                        waitername=rs_waiter.getString(2);
                        password=rs_waiter.getString(3);
                        db.insertWaiter(waiterid,waitername, password);
                    }
					/* for table type */
                    while(rs_tabletype.next()){
                        tabletypeid=rs_tabletype.getInt(1);
                        tabletypename=rs_tabletype.getString(2);
                        db.insertTableType(tabletypeid, tabletypename);
                    }
					/* for table */
                    while(rs_table.next()){
                        tableid=rs_table.getInt(1);
                        tablename=rs_table.getString(2);
                        tabletypeid=rs_table.getInt(3);
                        db.insertTable(tableid, tablename, tabletypeid);
                    }
					/* for main menu */
                    while(rs_mainmenu.next()){
                        mainmenuid=rs_mainmenu.getInt(1);
                        mainmenuname=rs_mainmenu.getString(2);
                        counterid=rs_mainmenu.getInt(3);
                        db.insertMainMenu(mainmenuid, mainmenuname,counterid,1);
                    }
					/* for sub menu */
                    while(rs_submenu.next()){
                        submenuid=rs_submenu.getInt(1);
                        mainmenuid=rs_submenu.getInt(2);
                        submenuname=rs_submenu.getString(3);
                        sortcode=rs_submenu.getString(4);
                        db.insertSubMenu(submenuid, submenuname, mainmenuid,sortcode);
                    }
					/* for item */
                    while(rs_item.next()){
                        itemid=rs_item.getString(1);
                        itemname=rs_item.getString(2);
                        submenuid=rs_item.getInt(3);
                        price=rs_item.getDouble(4);
                        stypeid=rs_item.getInt(5);
                        outoforder=rs_item.getInt(6);
                        db.insertItem(itemid, itemname, submenuid, price,outoforder,stypeid,null);
                    }
					/* for taste */
                    while(rs_taste.next()){
                        tasteid=rs_taste.getInt(1);
                        tastename=rs_taste.getString(2);
                        db.insertTaste(tasteid, tastename);
                    }
                    /* for stype */
                    while(rs_stype.next()){
                        stypeid=rs_stype.getInt(1);
                        stypename=rs_stype.getString(2);
                        db.insertSType(stypeid, stypename);
                    }

                    msg="Import Successful!";
                    msg_type=SystemSetting.SUCCESS;
                    isSuccess=true;
                }

            }catch(Exception ex){
                isSuccess=false;
                msg_type=SystemSetting.ERROR;
                msg=ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPreExecute(){
            progressDialog.show();
            progressDialog.setMessage("Importing Server Data.....");
        }
        @Override
        protected void onPostExecute(String r){
            progressDialog.hide();
            systemSetting.showMessage(msg_type,r,context,getLayoutInflater());
            if(isSuccess){
                Intent i=new Intent(getApplicationContext(),UserRightActivity.class);
                startActivity(i);
            }
        }
    }

    private void setLayoutResource(){
        btnDataSetup=(Button)findViewById(R.id.btnDataSetup);
        btnCSVImportExport=(Button)findViewById(R.id.btnCSVImportExport);
        btnServerImport=(Button)findViewById(R.id.btnServerImport);
        btnDeleteTran=(Button)findViewById(R.id.btnDeleteTran);
        btnBackupData=(Button)findViewById(R.id.btnBackupData);
        btnResetSlipId=(Button)findViewById(R.id.btnResetSlipId);
        btnCustomBackupData =(Button)findViewById(R.id.btnCustomBackupData);
        btnDeleteVoucher =(Button)findViewById(R.id.btnDeleteVoucher);
        btnDelExtraTran=(Button)findViewById(R.id.btnDelExtraTran);

        progressDialog =new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private class ExportData extends AsyncTask<String ,String, String> {
        private final ProgressDialog dialog = new ProgressDialog(SystemActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting data...");
            this.dialog.show();
        }

        protected String doInBackground(final String... args){
            SystemSetting backupData=new SystemSetting();
            backupData.dataBackup(db,false,null);

            return "";
        }

        @Override
        protected void onPostExecute(final String success) {

            if (this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            if (success.isEmpty()){
                showConfirmDialog("Backup Completed! Server Import Continue?");
            }
            else {
                systemSetting.showMessage(SystemSetting.ERROR,"Export Failed!",context,getLayoutInflater());
            }
        }
    }

    private class Backup extends AsyncTask<String ,String, String> {
        private final ProgressDialog dialog = new ProgressDialog(SystemActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Getting data...");
            this.dialog.show();
        }

        protected String doInBackground(final String... args){
            if(!isCustomBackup) systemSetting.dataBackup(db,true, SystemSetting.BackupType.UserBackup);
            else systemSetting.dataBackup(db,true, SystemSetting.BackupType.UserCustomBackup);

            return "";
        }

        @Override
        protected void onPostExecute(final String error) {

            if (this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            if (error.isEmpty()){
                systemSetting.showMessage(SystemSetting.SUCCESS,"Backup Completed!",context,getLayoutInflater());
            }
            else {
                systemSetting.showMessage(SystemSetting.ERROR,"Backup Failed!",context,getLayoutInflater());
            }
        }
    }
}
