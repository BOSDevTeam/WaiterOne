package com.bosictsolution.waiterone;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andprn.request.android.RequestHandler;
import com.bosictsolution.waiterone.bt.BtUtil;
import com.bosictsolution.waiterone.print.PrintQueue;
import com.bosictsolution.waiterone.print.PrintUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import adapter.BtDeviceListAdapter;
import adapter.DgChooseItemListAdapter;
import adapter.DgChooseMenuListAdapter;
import adapter.DgChooseTableListAdapter;
import adapter.DgChooseWaiterListAdapter;
import adapter.RpListAdapter;
import common.AlertView;
import common.DBHelper;
import common.FeatureList;
import common.PrinterWiFiPort;
import common.SystemSetting;
import data.ItemData;
import data.MainMenuData;
import data.RankingTypeData;
import data.ReportData;
import data.SubMenuData;
import data.TableData;
import data.TableTypeData;
import data.WaiterData;
import listener.CheckedListener;
import listener.ReportListClickListener;

public class ReportListActivity extends AppCompatActivity implements ReportListClickListener, CheckedListener {

    TextView tvLabelFromDate,tvLabelToDate;
    Button btnFromDate,btnToDate,btnWaiterDialogOK, btnViewByTableType,btnViewByMainMenu,btnViewBySubMenu, btnViewByItem,
            btnFromDate2,btnToDate2;
    ListView lvReport;
    final Context context = this;
    private DBHelper db;
    private Calendar cCalendar;
    List<ReportData> lstReport;
    RpListAdapter rpListAdapter;
    int fromToDate;
    private static List<WaiterData> lstWaiterData;
    private static List<TableData> lstTableData,lstTableDataByTableType;
    private static List<TableTypeData> lstTableTypeData;
    private static List<MainMenuData> lstMainMenuData;
    private static List<SubMenuData> lstSubMenuData;
    private static List<ItemData> lstItemData,lstSubMenuDataByMainMenu,lstItemDataBySubMenu;
    DgChooseWaiterListAdapter dgChooseWaiterListAdapter;
    DgChooseTableListAdapter dgChooseTableListAdapter;
    DgChooseMenuListAdapter dgChooseMenuListAdapter;
    DgChooseItemListAdapter dgChooseItemListAdapter;
    ListView lvWaiter,lvTable,lvSubMenu,lvItem;
    ArrayAdapter<String> arrayAdapter;
    Spinner spTableType,spMainMenu,spSubMenu;
    private static ArrayList<Integer> lstCheckedTableIDByTableType, lstTableIDByTableType, lstAllTableID, lstCheckedWaiterID =new ArrayList<>();
    private static ArrayList<Integer> lstCheckedSubMenuIDByMainMenu, lstSubMenuIDByMainMenu, lstAllSubMenuID =new ArrayList<>();
    public  static ArrayList<String> lstCheckedItemID =new ArrayList<>();
    int topItem=1,bottomItem=2,topMenu=3,bottomMenu=4;
    int topBottomMode,interfaceId;
    int rankTypeID =1, rankTypeName =2, rankTypeMenu =3;
    List<RankingTypeData> lstRankingTypeData;
    private static final String TAG = "WiFiConnectMenu";
    private PrinterWiFiPort wifiPort;
    private String printerIPAddress,printerAddress;
    private Thread hThread;
    String ioException;
    public static BluetoothAdapter BA;
    private BtDeviceListAdapter deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;
    SystemSetting systemSetting=new SystemSetting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent i=getIntent();
        interfaceId=i.getIntExtra("InterfaceID",0);
        printerAddress=i.getStringExtra("PrinterAddress");

        db=new DBHelper(this);
        BA = BluetoothAdapter.getDefaultAdapter();
        deviceAdapter=new BtDeviceListAdapter(getApplicationContext(), null);
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        cCalendar=Calendar.getInstance();
        setLayoutResource();
        setDateButtonText();
        getReport();
        getData();
        if(db.getFeatureResult(FeatureList.fUseMultiPrinter)!=1) {
            connectPrinter();
            interfaceId=1;
        }else{
            if(interfaceId==1){ // Network
                connectNetworkPrinter();
            }else if(interfaceId==2){ // Bluetooth
                if (checkBluetoothOn()) {
                    checkEditBluetoothDevice();
                }
            }
        }
        btnFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromToDate=0;
                showDialog(1);
            }
        });
        btnToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromToDate=1;
                showDialog(1);
            }
        });
        btnFromDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromToDate=0;
                showDialog(1);
            }
        });
        btnToDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromToDate=1;
                showDialog(1);
            }
        });
    }

    private void connectPrinter(){
        Cursor cur=db.getPrinterSetting();
        if(cur.moveToFirst()){
            printerIPAddress =cur.getString(0);
        }
        wifiPort = PrinterWiFiPort.getInstance();
        try{
            wifiConn(printerIPAddress);
        }
        catch (IOException e)
        {
            Log.e(TAG,e.getMessage(),e);
        }
    }

    private void connectNetworkPrinter(){
        wifiPort = PrinterWiFiPort.getInstance();
        try{
            wifiConn(printerAddress);
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
        Toast toast = Toast.makeText(context, "Disconnected!", Toast.LENGTH_SHORT);
        toast.show();
    }

    // WiFi Connection Task.
    class connTask extends AsyncTask<String, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(ReportListActivity.this);

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
                //printer_message="PRINTER ONLINE!";
                //setTitle("");
            }
            else
            {
                if(dialog.isShowing())
                    dialog.dismiss();
                //printer_message="PRINTER OFFLINE!";
                //setTitle("");
                AlertView.showAlert("Failed", "Check Devices!"+ioException, context);
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed(){
        try {
            wifiDisConn();
        }catch (IOException e) {
            Log.e(TAG,e.getMessage(),e);
        }catch (InterruptedException e) {
            Log.e(TAG,e.getMessage(),e);
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            try {
                wifiDisConn();
            }catch (IOException e) {
                Log.e(TAG,e.getMessage(),e);
            }catch (InterruptedException e) {
                Log.e(TAG,e.getMessage(),e);
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        lstCheckedWaiterID =new ArrayList<>();
        lstCheckedTableIDByTableType =new ArrayList<>();
        lstCheckedSubMenuIDByMainMenu =new ArrayList<>();
        lstCheckedItemID=new ArrayList<>();
    }

    private void getData(){
        lstWaiterData=new ArrayList<>();
        Cursor curWaiter=db.getWaiter();
        while(curWaiter.moveToNext()){
            WaiterData data=new WaiterData();
            data.setWaiterid(curWaiter.getInt(0));
            data.setWaiterName(curWaiter.getString(1));
            lstWaiterData.add(data);
        }
        lstTableTypeData=new ArrayList<>();
        Cursor curTableType=db.getTableType();
        while(curTableType.moveToNext()){
            TableTypeData data=new TableTypeData();
            data.setTableTypeID(curTableType.getInt(0));
            data.setTableTypeName(curTableType.getString(1));
            lstTableTypeData.add(data);
        }
        lstTableData=new ArrayList<>();
        Cursor curTable=db.getTable();
        while(curTable.moveToNext()){
            TableData data=new TableData();
            data.setTableid(curTable.getInt(0));
            data.setTableName(curTable.getString(1));
            data.setTableTypeID(curTable.getInt(2));
            data.setTableTypeName(curTable.getString(3));
            lstTableData.add(data);
        }
        lstMainMenuData=new ArrayList<>();
        Cursor curMainMenu=db.getMainMenu();
        while(curMainMenu.moveToNext()){
            MainMenuData data=new MainMenuData();
            data.setMainMenuID(curMainMenu.getInt(0));
            data.setMainMenuName(curMainMenu.getString(1));
            lstMainMenuData.add(data);
        }
        lstSubMenuData=new ArrayList<>();
        Cursor curSubMenu=db.getSubMenu();
        while(curSubMenu.moveToNext()){
            SubMenuData data=new SubMenuData();
            data.setSubMenuID(curSubMenu.getInt(0));
            data.setSubMenuName(curSubMenu.getString(1));
            data.setMainMenuID(curSubMenu.getInt(2));
            data.setMainMenuName(curSubMenu.getString(4));
            lstSubMenuData.add(data);
        }
        lstItemData=new ArrayList<>();
        Cursor curItem=db.getItem();
        while(curItem.moveToNext()){
            ItemData data=new ItemData();
            data.setItemid(curItem.getString(1));
            data.setItemName(curItem.getString(2));
            data.setSubMenuID(curItem.getInt(3));
            data.setMainMenuID(curItem.getInt(7));
            lstItemData.add(data);
        }
    }

    @Override
    public void onReportListClickListener(int position){
        if(lstReport.get(position).getReportName().equals("Day End Report")){
            Intent i=new Intent(getApplicationContext(),RpDayEndActivity.class);
            i.putExtra("InterfaceID",interfaceId);
            startActivity(i);
        }
        else if(lstReport.get(position).getReportName().equals("Sale Amount Only Report")){
            Intent i=new Intent(getApplicationContext(),RpSaleAmountActivity.class);
            i.putExtra("from_date",btnFromDate.getText().toString());
            i.putExtra("to_date",btnToDate.getText().toString());
            startActivity(i);
        }
        else if(lstReport.get(position).getReportName().equals("Sale Invoice Report")){
            Intent i=new Intent(getApplicationContext(),RpSaleInvoiceActivity.class);
            i.putExtra("from_date",btnFromDate.getText().toString());
            i.putExtra("to_date",btnToDate.getText().toString());
            startActivity(i);
        }
        else if(lstReport.get(position).getReportName().equals("Detailed Sale Report")){
            Intent i=new Intent(getApplicationContext(),RpDetailedSaleActivity.class);
            i.putExtra("from_date",btnFromDate.getText().toString());
            i.putExtra("to_date",btnToDate.getText().toString());
            startActivity(i);
        }
        else if(lstReport.get(position).getReportName().equals("Summary Item Report")){
            showItemDialog();
        }
        else if(lstReport.get(position).getReportName().equals("Top Sale Item Report")){
            topBottomMode=topItem;
            bindItemRankingType();
            showTopBottomDialog();
        }
        else if(lstReport.get(position).getReportName().equals("Bottom Sale Item Report")){
            topBottomMode=bottomItem;
            bindItemRankingType();
            showTopBottomDialog();
        }
        else if(lstReport.get(position).getReportName().equals("Top Sale Menu Report")){
            topBottomMode=topMenu;
            bindSubMenuRankingType();
            showTopBottomDialog();
        }
        else if(lstReport.get(position).getReportName().equals("Bottom Sale Menu Report")){
            topBottomMode=bottomMenu;
            bindSubMenuRankingType();
            showTopBottomDialog();
        }
        else if(lstReport.get(position).getReportName().equals("Sale By Menu Report")){
            showMenuDialog();
        }
        else if(lstReport.get(position).getReportName().equals("Sale By Hourly Report")){
            Intent i=new Intent(getApplicationContext(),RpSaleByHourlyActivity.class);
            i.putExtra("from_date",btnFromDate.getText().toString());
            i.putExtra("to_date",btnToDate.getText().toString());
            startActivity(i);
        }
        else if(lstReport.get(position).getReportName().equals("Sale By Waiter Report")){
            showWaiterDialog();
        }
        else if(lstReport.get(position).getReportName().equals("Sale By Table Report")){
            showTableDialog();
        }
        else if(lstReport.get(position).getReportName().equals("Data Analysis Report")){
            Intent i=new Intent(getApplicationContext(),RpDataAnalysisActivity.class);
            i.putExtra("from_date",btnFromDate.getText().toString());
            i.putExtra("to_date",btnToDate.getText().toString());
            startActivity(i);
        }
    }

    @Override
    public void onWaiterCheckedListener(int position){
        int checkedWaiterID = lstWaiterData.get(position).getWaiterid();
        if(!lstCheckedWaiterID.contains(checkedWaiterID)) {
            lstCheckedWaiterID.add(lstWaiterData.get(position).getWaiterid());
            btnWaiterDialogOK.setText("OK");
        }
    }

    @Override
    public void onWaiterUnCheckedListener(int position){
        int removeIndex= lstCheckedWaiterID.indexOf(lstWaiterData.get(position).getWaiterid());
        if(removeIndex!=-1) {
            lstCheckedWaiterID.remove(removeIndex);
        }
        if(lstCheckedWaiterID.size()==0){
            btnWaiterDialogOK.setText("VIEW ALL");
        }
    }

    @Override
    public void onTableCheckedListener(int position){
        int checkedTableID=lstTableDataByTableType.get(position).getTableid();
        if(!lstCheckedTableIDByTableType.contains(checkedTableID)) {
            lstCheckedTableIDByTableType.add(checkedTableID);
            btnViewByTableType.setText("OK");
        }
    }

    @Override
    public void onTableUnCheckedListener(int position){
        int removeIndex= lstCheckedTableIDByTableType.indexOf(lstTableDataByTableType.get(position).getTableid());
        if(removeIndex!=-1) {
            lstCheckedTableIDByTableType.remove(removeIndex);
        }
        if(lstCheckedTableIDByTableType.size()==0){
            btnViewByTableType.setText("VIEW "+ spTableType.getSelectedItem().toString());
        }
    }

    @Override
    public void onSubMenuCheckedListener(int position){
        int checkedSubMenuID=lstSubMenuDataByMainMenu.get(position).getSubMenuID();
        if(!lstCheckedSubMenuIDByMainMenu.contains(checkedSubMenuID)) {
            lstCheckedSubMenuIDByMainMenu.add(checkedSubMenuID);
        }
    }

    @Override
    public void onSubMenuUnCheckedListener(int position){
        int removeIndex= lstCheckedSubMenuIDByMainMenu.indexOf(lstSubMenuDataByMainMenu.get(position).getSubMenuID());
        if(removeIndex!=-1) {
            lstCheckedSubMenuIDByMainMenu.remove(removeIndex);
        }
    }

    @Override
    public void onItemCheckedListener(int position){
        String checkedItemID=lstItemDataBySubMenu.get(position).getItemid();
        if(!lstCheckedItemID.contains(checkedItemID)) {
            lstCheckedItemID.add(checkedItemID);
        }
    }

    @Override
    public void onItemUnCheckedListener(int position){
        int removeIndex= lstCheckedItemID.indexOf(lstItemDataBySubMenu.get(position).getItemid());
        if(removeIndex!=-1) {
            lstCheckedItemID.remove(removeIndex);
        }
    }

    private void getReport(){
       lstReport=new ArrayList<>();
        Cursor cur=db.getReport();
        while(cur.moveToNext()){
            ReportData data=new ReportData();
            data.setReportID(cur.getInt(0));
            data.setReportName(cur.getString(1));
            lstReport.add(data);
        }
        rpListAdapter =new RpListAdapter(this, lstReport);
        lvReport.setAdapter(rpListAdapter);
        rpListAdapter.setOnReportListClickListener(this);
    }

    private void setLayoutResource(){
        tvLabelFromDate=(TextView)findViewById(R.id.tvLabelFromDate);
        tvLabelToDate=(TextView)findViewById(R.id.tvLabelToDate);
        btnFromDate=(Button)findViewById(R.id.btnFromDate);
        btnToDate=(Button)findViewById(R.id.btnToDate);
        lvReport=(ListView)findViewById(R.id.lvReport);
        btnFromDate2=(Button)findViewById(R.id.btnFromDate2);
        btnToDate2=(Button)findViewById(R.id.btnToDate2);
    }

    @Override
    protected AlertDialog onCreateDialog(int id){
        return showDatePicker();
    }

    private DatePickerDialog showDatePicker(){
        DatePickerDialog datePicker=new DatePickerDialog(ReportListActivity.this,new DatePickerDialog.OnDateSetListener() {

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

        SimpleDateFormat dateFormat2=new SimpleDateFormat(SystemSetting.MM_DATE_FORMAT);
        String dateForButton2=dateFormat2.format(cCalendar.getTime());
        if(fromToDate==1) btnToDate2.setText(dateForButton2);
        else btnFromDate2.setText(dateForButton2);
    }

    private void setDateButtonText(){
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        String dateForButton=dateFormat.format(cCalendar.getTime());
        btnToDate.setText(dateForButton);
        btnFromDate.setText(dateForButton);

        SimpleDateFormat dateFormat2=new SimpleDateFormat(SystemSetting.MM_DATE_FORMAT);
        String dateForButton2=dateFormat2.format(cCalendar.getTime());
        btnToDate2.setText(dateForButton2);
        btnFromDate2.setText(dateForButton2);
    }

    private void bindItemRankingType(){
        lstRankingTypeData=new ArrayList<>();
        RankingTypeData data1=new RankingTypeData();
        data1.setRankingTypeID(rankTypeID);
        data1.setRankingTypeName("Item ID");
        lstRankingTypeData.add(data1);
        RankingTypeData data2=new RankingTypeData();
        data2.setRankingTypeID(rankTypeName);
        data2.setRankingTypeName("Item Name");
        lstRankingTypeData.add(data2);
        RankingTypeData data3=new RankingTypeData();
        data3.setRankingTypeID(rankTypeMenu);
        data3.setRankingTypeName("Sub Menu");
        lstRankingTypeData.add(data3);
    }

    private void bindSubMenuRankingType(){
        lstRankingTypeData=new ArrayList<>();
        RankingTypeData data1=new RankingTypeData();
        data1.setRankingTypeID(rankTypeID);
        data1.setRankingTypeName("Sub Menu ID");
        lstRankingTypeData.add(data1);
        RankingTypeData data2=new RankingTypeData();
        data2.setRankingTypeID(rankTypeName);
        data2.setRankingTypeName("Sub Menu Name");
        lstRankingTypeData.add(data2);
        RankingTypeData data3=new RankingTypeData();
        data3.setRankingTypeID(rankTypeMenu);
        data3.setRankingTypeName("Main Menu");
        lstRankingTypeData.add(data3);
    }

    public static String[] combineString(String[] olds,String[] news){
        int length=olds.length+news.length;
        String[] result=new String[length];
        System.arraycopy(olds, 0, result, 0,olds.length);
        System.arraycopy(news, 0, result, olds.length, news.length);
        return result;
    }

    private void showTopBottomDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View vi=reg.inflate(R.layout.dg_top_bottom, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(vi);

        final TextView tvLabelEditNumber=(TextView)vi.findViewById(R.id.tvLabelEditNumber);
        final TextView tvLabelRankingType=(TextView)vi.findViewById(R.id.tvLabelRankingType);
        final EditText etNumber=(EditText)vi.findViewById(R.id.etNumber);
        final Button btnOk=(Button)vi.findViewById(R.id.btnOk);
        final Spinner spRankingType=(Spinner)vi.findViewById(R.id.spRankingType);

        String[] rank_type_name={};
        for(int i=0;i<lstRankingTypeData.size();i++){
            String[] cur=new String[] {lstRankingTypeData.get(i).rankingTypeName};
            rank_type_name=combineString(rank_type_name,cur);
        }
        arrayAdapter=new ArrayAdapter<>(this,R.layout.sp_black,R.id.tvSpinnerItem,rank_type_name);
        spRankingType.setAdapter(arrayAdapter);

        dialog.setCancelable(true);
        final AlertDialog dialog1=dialog.create();
        dialog1.show();

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(topBottomMode==topItem) {
                    int position=spRankingType.getSelectedItemPosition();
                    int rankingTypeID=lstRankingTypeData.get(position).rankingTypeID;
                    int topNum = (etNumber.getText().length() == 0) ? 10 : Integer.parseInt(etNumber.getText().toString());
                    Intent i = new Intent(getApplicationContext(), RpTopSaleItemActivity.class);
                    i.putExtra("from_date", btnFromDate.getText().toString());
                    i.putExtra("to_date", btnToDate.getText().toString());
                    i.putExtra("top_number", topNum);
                    i.putExtra("rank_type",rankingTypeID);
                    startActivity(i);
                    dialog1.dismiss();
                }
                else if(topBottomMode==bottomItem){
                    int position=spRankingType.getSelectedItemPosition();
                    int rankingTypeID=lstRankingTypeData.get(position).rankingTypeID;
                    int bottomNum = (etNumber.getText().length() == 0) ? 10 : Integer.parseInt(etNumber.getText().toString());
                    Intent i = new Intent(getApplicationContext(), RpBottomSaleItemActivity.class);
                    i.putExtra("from_date", btnFromDate.getText().toString());
                    i.putExtra("to_date", btnToDate.getText().toString());
                    i.putExtra("bottom_number", bottomNum);
                    i.putExtra("rank_type",rankingTypeID);
                    startActivity(i);
                    dialog1.dismiss();
                }
                else if(topBottomMode==topMenu) {
                    int position=spRankingType.getSelectedItemPosition();
                    int rankingTypeID=lstRankingTypeData.get(position).rankingTypeID;
                    int topNum = (etNumber.getText().length() == 0) ? 10 : Integer.parseInt(etNumber.getText().toString());
                    Intent i = new Intent(getApplicationContext(), RpTopSaleMenuActivity.class);
                    i.putExtra("from_date", btnFromDate.getText().toString());
                    i.putExtra("to_date", btnToDate.getText().toString());
                    i.putExtra("top_number", topNum);
                    i.putExtra("rank_type",rankingTypeID);
                    startActivity(i);
                    dialog1.dismiss();
                }
                else if(topBottomMode==bottomMenu){
                    int position=spRankingType.getSelectedItemPosition();
                    int rankingTypeID=lstRankingTypeData.get(position).rankingTypeID;
                    int bottomNum = (etNumber.getText().length() == 0) ? 10 : Integer.parseInt(etNumber.getText().toString());
                    Intent i = new Intent(getApplicationContext(), RpBottomSaleMenuActivity.class);
                    i.putExtra("from_date", btnFromDate.getText().toString());
                    i.putExtra("to_date", btnToDate.getText().toString());
                    i.putExtra("bottom_number", bottomNum);
                    i.putExtra("rank_type",rankingTypeID);
                    startActivity(i);
                    dialog1.dismiss();
                }
            }
        });
    }

    private void showWaiterDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View vi=reg.inflate(R.layout.dg_choose_waiter, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(vi);

        final TextView tvLabelChooseWaiter=(TextView)vi.findViewById(R.id.tvLabelChooseWaiter);
        final CheckBox chkAll=(CheckBox)vi.findViewById(R.id.chkAll);
        lvWaiter=(ListView)vi.findViewById(R.id.lvWaiter);
        btnWaiterDialogOK=(Button)vi.findViewById(R.id.btnOk);

        chkAll.setVisibility(View.INVISIBLE);

        /*DgChooseWaiterListAdapter.checkAll = false;*/
        setWaiterDataToAdapter();

        dialog.setCancelable(true);
        final AlertDialog dialog1=dialog.create();
        dialog1.show();

        chkAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(chkAll.isChecked()) {
                    /*DgChooseWaiterListAdapter.checkAll = true;*/
                    setWaiterDataToAdapter();
                }else{
                    /*DgChooseWaiterListAdapter.checkAll = false;*/
                    setWaiterDataToAdapter();
                }
            }
        });

        btnWaiterDialogOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i=new Intent(getApplicationContext(),RpSaleByWaiterActivity.class);
                i.putExtra("from_date",btnFromDate.getText().toString());
                i.putExtra("to_date",btnToDate.getText().toString());
                if(lstCheckedWaiterID.size()==0){
                    for(int k=0;k<lstWaiterData.size();k++){
                        lstCheckedWaiterID.add(lstWaiterData.get(k).getWaiterid());
                    }
                }
                i.putIntegerArrayListExtra("checked_waiter", lstCheckedWaiterID);
                startActivity(i);
                dialog1.dismiss();
            }
        });
    }

    private void setWaiterDataToAdapter(){
        dgChooseWaiterListAdapter =new DgChooseWaiterListAdapter(this, lstWaiterData);
        lvWaiter.setAdapter(dgChooseWaiterListAdapter);
        dgChooseWaiterListAdapter.setCustomCheckedListener(this);
    }

    private void showTableDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View vi=reg.inflate(R.layout.dg_choose_table, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(vi);

        final TextView tvLabelChooseTable=(TextView)vi.findViewById(R.id.tvLabelChooseTable);
        spTableType =(Spinner) vi.findViewById(R.id.spSearchTableType);
        lvTable=(ListView)vi.findViewById(R.id.lvTable);
        btnViewByTableType =(Button)vi.findViewById(R.id.btnViewByTableType);
        Button btnViewAllTable=(Button)vi.findViewById(R.id.btnViewAllTable);

        setTableDataToAdapter();

        dialog.setCancelable(true);
        final AlertDialog dialog1=dialog.create();
        dialog1.show();

        spTableType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id){
                setTableDataByTableType(lstTableTypeData.get(position).getTableTypeID());
                btnViewByTableType.setText("View "+lstTableTypeData.get(position).getTableTypeName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });


        btnViewByTableType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i=new Intent(getApplicationContext(),RpSaleByTableActivity.class);
                i.putExtra("from_date",btnFromDate.getText().toString());
                i.putExtra("to_date",btnToDate.getText().toString());
                i.putIntegerArrayListExtra("checked_table", lstCheckedTableIDByTableType);
                i.putIntegerArrayListExtra("all_table", lstTableIDByTableType);
                startActivity(i);
                dialog1.dismiss();
            }
        });

        btnViewAllTable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(getApplicationContext(),RpSaleByTableActivity.class);
                intent.putExtra("from_date",btnFromDate.getText().toString());
                intent.putExtra("to_date",btnToDate.getText().toString());
                intent.putIntegerArrayListExtra("checked_table", lstCheckedTableIDByTableType);

                lstAllTableID =new ArrayList<Integer>();
                for(int i=0;i<lstTableData.size();i++){
                    lstAllTableID.add(lstTableData.get(i).getTableid());
                }

                intent.putIntegerArrayListExtra("all_table", lstAllTableID);
                startActivity(intent);
                dialog1.dismiss();
            }
        });
    }

    private void setTableDataToAdapter(){
        String[] table_type_name={};
        for(int i=0;i<lstTableTypeData.size();i++){
            String[] cur=new String[] {lstTableTypeData.get(i).getTableTypeName()};
            table_type_name=combineString(table_type_name,cur);
        }
        arrayAdapter=new ArrayAdapter<String>(this,R.layout.sp_black,R.id.tvSpinnerItem,table_type_name);
        spTableType.setAdapter(arrayAdapter);

        lstCheckedTableIDByTableType =new ArrayList<>();
        lstTableIDByTableType =new ArrayList<>();
        lstTableDataByTableType=new ArrayList<>();
        for(int i=0;i<lstTableData.size();i++){
            if(lstTableData.get(i).getTableTypeID()==lstTableTypeData.get(0).getTableTypeID()){
                TableData data=new TableData();
                data.setTableid(lstTableData.get(i).getTableid());
                data.setTableName(lstTableData.get(i).getTableName());
                lstTableIDByTableType.add(data.getTableid());
                lstTableDataByTableType.add(data);
            }
        }
        btnViewByTableType.setText("View "+lstTableTypeData.get(0).getTableTypeName());
        dgChooseTableListAdapter =new DgChooseTableListAdapter(this, lstTableDataByTableType);
        lvTable.setAdapter(dgChooseTableListAdapter);
        dgChooseTableListAdapter.setCustomCheckedListener(this);
    }

    private void setTableDataByTableType(int tableTypeID){
        lstCheckedTableIDByTableType =new ArrayList<>();
        lstTableIDByTableType =new ArrayList<>();
        lstTableDataByTableType=new ArrayList<>();
        for(int i=0;i<lstTableData.size();i++){
            if(lstTableData.get(i).getTableTypeID()==tableTypeID){
                TableData data=new TableData();
                data.setTableid(lstTableData.get(i).getTableid());
                data.setTableName(lstTableData.get(i).getTableName());
                lstTableIDByTableType.add(data.getTableid());
                lstTableDataByTableType.add(data);
            }
        }
        dgChooseTableListAdapter =new DgChooseTableListAdapter(this, lstTableDataByTableType);
        lvTable.setAdapter(dgChooseTableListAdapter);
        dgChooseTableListAdapter.setCustomCheckedListener(this);
    }

    private void showMenuDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View vi=reg.inflate(R.layout.dg_choose_menu, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(vi);

        final TextView tvLabelChooseMenu=(TextView)vi.findViewById(R.id.tvLabelChooseMenu);
        spMainMenu =(Spinner) vi.findViewById(R.id.spSearchMainMenu);
        lvSubMenu=(ListView)vi.findViewById(R.id.lvSubMenu);
        btnViewByMainMenu =(Button)vi.findViewById(R.id.btnViewByMainMenu);
        btnViewBySubMenu =(Button)vi.findViewById(R.id.btnViewBySubMenu);
        Button btnViewAll=(Button)vi.findViewById(R.id.btnViewAll);

        setSubMenuDataToAdapter();

        dialog.setCancelable(true);
        final AlertDialog dialog1=dialog.create();
        dialog1.show();

        spMainMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id){
                setSubMenuDataByMainMenu(lstMainMenuData.get(position).getMainMenuID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });

        btnViewByMainMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i=new Intent(getApplicationContext(),RpSaleByMenuActivity.class);
                i.putExtra("from_date",btnFromDate.getText().toString());
                i.putExtra("to_date",btnToDate.getText().toString());
                i.putIntegerArrayListExtra("checked_menu", lstCheckedSubMenuIDByMainMenu);
                i.putIntegerArrayListExtra("all_menu", lstSubMenuIDByMainMenu);
                startActivity(i);
                dialog1.dismiss();
            }
        });

        btnViewBySubMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i=new Intent(getApplicationContext(),RpSaleByMenuActivity.class);
                i.putExtra("from_date",btnFromDate.getText().toString());
                i.putExtra("to_date",btnToDate.getText().toString());
                i.putIntegerArrayListExtra("checked_menu", lstCheckedSubMenuIDByMainMenu);
                i.putIntegerArrayListExtra("all_menu", lstSubMenuIDByMainMenu);
                startActivity(i);
                dialog1.dismiss();
            }
        });

        btnViewAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                lstCheckedSubMenuIDByMainMenu =new ArrayList<>();
                Intent intent=new Intent(getApplicationContext(),RpSaleByMenuActivity.class);
                intent.putExtra("from_date",btnFromDate.getText().toString());
                intent.putExtra("to_date",btnToDate.getText().toString());
                intent.putIntegerArrayListExtra("checked_menu", lstCheckedSubMenuIDByMainMenu);

                lstAllSubMenuID =new ArrayList<>();
                for(int i = 0; i< lstSubMenuData.size(); i++){
                    lstAllSubMenuID.add(lstSubMenuData.get(i).getSubMenuID());
                }

                intent.putIntegerArrayListExtra("all_menu", lstAllSubMenuID);
                startActivity(intent);
                dialog1.dismiss();
            }
        });
    }

    private void setSubMenuDataToAdapter(){
        String[] main_menu_name={};
        for(int i = 0; i< lstMainMenuData.size(); i++){
            String[] cur=new String[] {lstMainMenuData.get(i).getMainMenuName()};
            main_menu_name=combineString(main_menu_name,cur);
        }
        arrayAdapter=new ArrayAdapter<String>(this,R.layout.sp_black,R.id.tvSpinnerItem,main_menu_name);
        spMainMenu.setAdapter(arrayAdapter);

        lstCheckedSubMenuIDByMainMenu =new ArrayList<>();
        lstSubMenuIDByMainMenu =new ArrayList<>();
        lstSubMenuDataByMainMenu=new ArrayList<>();
        for(int i = 0; i< lstSubMenuData.size(); i++){
            if(lstSubMenuData.get(i).getMainMenuID()== lstMainMenuData.get(0).getMainMenuID()){
                ItemData data=new ItemData();
                data.setSubMenuID(lstSubMenuData.get(i).getSubMenuID());
                data.setSubMenuName(lstSubMenuData.get(i).getSubMenuName());
                lstSubMenuIDByMainMenu.add(data.getSubMenuID());
                lstSubMenuDataByMainMenu.add(data);
            }
        }
        dgChooseMenuListAdapter =new DgChooseMenuListAdapter(this, lstSubMenuDataByMainMenu);
        lvSubMenu.setAdapter(dgChooseMenuListAdapter);
        dgChooseMenuListAdapter.setCustomCheckedListener(this);
    }

    private void setSubMenuDataByMainMenu(int mainMenuID){
        lstCheckedSubMenuIDByMainMenu =new ArrayList<>();
        lstSubMenuIDByMainMenu =new ArrayList<>();
        lstSubMenuDataByMainMenu=new ArrayList<>();
        for(int i = 0; i< lstSubMenuData.size(); i++){
            if(lstSubMenuData.get(i).getMainMenuID()==mainMenuID){
                ItemData data=new ItemData();
                data.setSubMenuID(lstSubMenuData.get(i).getSubMenuID());
                data.setSubMenuName(lstSubMenuData.get(i).getSubMenuName());
                lstSubMenuIDByMainMenu.add(data.getSubMenuID());
                lstSubMenuDataByMainMenu.add(data);
            }
        }
        dgChooseMenuListAdapter =new DgChooseMenuListAdapter(this, lstSubMenuDataByMainMenu);
        lvSubMenu.setAdapter(dgChooseMenuListAdapter);
        dgChooseMenuListAdapter.setCustomCheckedListener(this);
    }

    private void showItemDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View vi=reg.inflate(R.layout.dg_choose_item, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(vi);

        final TextView tvLabelChooseItem=(TextView)vi.findViewById(R.id.tvLabelChooseItem);
        spMainMenu =(Spinner) vi.findViewById(R.id.spSearchMainMenu);
        spSubMenu =(Spinner) vi.findViewById(R.id.spSearchSubMenu);
        lvItem=(ListView)vi.findViewById(R.id.lvItem);
        btnViewByMainMenu=(Button)vi.findViewById(R.id.btnViewByMainMenu);
        btnViewBySubMenu=(Button)vi.findViewById(R.id.btnViewBySubMenu);
        btnViewByItem =(Button)vi.findViewById(R.id.btnViewByItem);
        Button btnViewAll =(Button)vi.findViewById(R.id.btnViewAll);

        setMenuItemDataToAdapter();

        dialog.setCancelable(true);
        final AlertDialog dialog1=dialog.create();
        dialog1.show();

        spMainMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id){
                bindSubMenuDataByMainMenu(lstMainMenuData.get(position).getMainMenuID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });

        spSubMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id){
                bindItemDataBySubMenu(lstSubMenuDataByMainMenu.get(position).getSubMenuID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });

        btnViewByItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) { //lstCheckedItems or lstItemDataBySubMenu
                if(lstCheckedItemID.size()==0){
                    for(int i=0;i<lstItemDataBySubMenu.size();i++){
                        lstCheckedItemID.add(lstItemDataBySubMenu.get(i).getItemid());
                    }
                }
                Intent i=new Intent(getApplicationContext(),RpSummaryItemActivity.class);
                i.putExtra("from_date",btnFromDate.getText().toString());
                i.putExtra("to_date",btnToDate.getText().toString());
                i.putExtra("InterfaceID",interfaceId);
                i.putStringArrayListExtra("checked_item", lstCheckedItemID);
                startActivity(i);
                dialog1.dismiss();
            }
        });

        btnViewAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) { //lstItemDataBySubMenu
                lstCheckedItemID =new ArrayList<>();
                Intent i=new Intent(getApplicationContext(),RpSummaryItemActivity.class);
                i.putExtra("from_date",btnFromDate.getText().toString());
                i.putExtra("to_date",btnToDate.getText().toString());
                i.putExtra("InterfaceID",interfaceId);
                for(int j=0;j<lstItemData.size();j++){
                    lstCheckedItemID.add(lstItemData.get(j).getItemid());
                }
                i.putStringArrayListExtra("checked_item", lstCheckedItemID);
                startActivity(i);
                dialog1.dismiss();
            }
        });

        btnViewByMainMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) { //lstItemDataBySubMenu
                lstCheckedItemID =new ArrayList<>();
                Intent i=new Intent(getApplicationContext(),RpSummaryItemActivity.class);
                i.putExtra("from_date",btnFromDate.getText().toString());
                i.putExtra("to_date",btnToDate.getText().toString());
                i.putExtra("InterfaceID",interfaceId);
                int position=spMainMenu.getSelectedItemPosition();
                int mainMenuID=lstMainMenuData.get(position).getMainMenuID();
                for(int k=0;k<lstItemData.size();k++){
                    if (lstItemData.get(k).getMainMenuID() == mainMenuID) {
                        lstCheckedItemID.add(lstItemData.get(k).getItemid());
                    }
                }
                i.putStringArrayListExtra("checked_item", lstCheckedItemID);
                startActivity(i);
                dialog1.dismiss();
            }
        });

        btnViewBySubMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) { //lstItemDataBySubMenu
                lstCheckedItemID =new ArrayList<>();
                Intent i=new Intent(getApplicationContext(),RpSummaryItemActivity.class);
                i.putExtra("from_date",btnFromDate.getText().toString());
                i.putExtra("to_date",btnToDate.getText().toString());
                int position=spSubMenu.getSelectedItemPosition();
                int subMenuID=lstSubMenuDataByMainMenu.get(position).getSubMenuID();
                for(int k=0;k<lstItemData.size();k++){
                    if (lstItemData.get(k).getSubMenuID() == subMenuID) {
                        lstCheckedItemID.add(lstItemData.get(k).getItemid());
                    }
                }
                i.putStringArrayListExtra("checked_item", lstCheckedItemID);
                startActivity(i);
                dialog1.dismiss();
            }
        });
    }

    private void setMenuItemDataToAdapter(){
        String[] main_menu_name={},sub_menu_name={};
        lstItemDataBySubMenu=new ArrayList<>();

       /* MenuItemData all_main=new MenuItemData();
        all_main.setMainMenuID(0);
        all_main.setMainMenuName("All MainMenu");
        lstAllMainMenuData.add(all_main);
        lstAllMainMenuData.addAll(lstAllMainMenuData);*/
        for(int i=0;i<lstMainMenuData.size();i++){
            String[] cur=new String[] {lstMainMenuData.get(i).getMainMenuName()};
            main_menu_name=combineString(main_menu_name,cur);
        }
        arrayAdapter=new ArrayAdapter<>(this,R.layout.sp_black,R.id.tvSpinnerItem,main_menu_name);
        spMainMenu.setAdapter(arrayAdapter);

        /*MenuItemData all_sub=new MenuItemData();
        all_sub.setSubMenuID(0);
        all_sub.setSubMenuName("All SubMenu");
        lstAllSubMenuData.add(all_sub);
        lstAllSubMenuData.addAll(lstAllSubMenuData);*/
        for(int i=0;i<lstSubMenuData.size();i++){
            String[] cur=new String[] {lstSubMenuData.get(i).getSubMenuName()};
            sub_menu_name=combineString(sub_menu_name,cur);
        }
        arrayAdapter=new ArrayAdapter<>(this,R.layout.sp_black,R.id.tvSpinnerItem,sub_menu_name);
        spSubMenu.setAdapter(arrayAdapter);

        lstItemDataBySubMenu.addAll(lstItemData);
        dgChooseItemListAdapter =new DgChooseItemListAdapter(this, lstItemDataBySubMenu);
        lvItem.setAdapter(dgChooseItemListAdapter);
        dgChooseItemListAdapter.setCustomCheckedListener(this);
    }

    private void bindSubMenuDataByMainMenu(int mainMenuID){
        String[] sub_menu_name = {};
        lstSubMenuDataByMainMenu=new ArrayList<>();

        /*if(mainMenuID!=0) {  //not all
            MenuItemData all_sub=new MenuItemData();
            all_sub.setSubMenuID(0);
            all_sub.setSubMenuName("All SubMenu by "+mainMenuName);
            lstSubMenuDataByMainMenu.add(all_sub);*/
        for (int i = 0; i < lstSubMenuData.size(); i++) {
            if (lstSubMenuData.get(i).getMainMenuID() == mainMenuID) {
                String[] cur = new String[]{lstSubMenuData.get(i).getSubMenuName()};
                sub_menu_name = combineString(sub_menu_name, cur);
                ItemData sub=new ItemData();
                sub.setSubMenuID(lstSubMenuData.get(i).getSubMenuID());
                sub.setSubMenuName(lstSubMenuData.get(i).getSubMenuName());
                lstSubMenuDataByMainMenu.add(sub);
            }
        }

        /*}*/
        arrayAdapter=new ArrayAdapter<>(this,R.layout.sp_black,R.id.tvSpinnerItem,sub_menu_name);
        spSubMenu.setAdapter(arrayAdapter);
        /*else{  //all
            lstAllSubMenuData=new ArrayList<>();
            MenuItemData all_sub=new MenuItemData();
            all_sub.setSubMenuID(0);
            all_sub.setSubMenuName("All SubMenu");
            lstSubMenuDataByMainMenu.add(all_sub);
            lstAllSubMenuData.add(all_sub);
            lstAllSubMenuData.addAll(lstAllSubMenuData);
            for(int i=0;i<lstAllSubMenuData.size();i++){
                String[] cur=new String[] {lstAllSubMenuData.get(i).subMenuName};
                sub_menu_name=combineString(sub_menu_name,cur);
                MenuItemData sub=new MenuItemData();
                sub.subMenuID=lstAllSubMenuData.get(i).subMenuID;
                sub.subMenuName=lstAllSubMenuData.get(i).subMenuName;
                lstSubMenuDataByMainMenu.add(sub);
            }
            lstSubMenuDataByMainMenu.addAll(lstAllSubMenuData);
            btnViewByItem.setText("OK");
        }*/
    }

    private void bindItemDataBySubMenu(int subMenuID){
        lstCheckedItemID =new ArrayList<>();
        lstItemDataBySubMenu=new ArrayList<>();
        for (int i = 0; i < lstItemData.size(); i++) {
            if (lstItemData.get(i).getSubMenuID() == subMenuID) {
                ItemData data = new ItemData();
                data.setItemid(lstItemData.get(i).getItemid());
                data.setItemName(lstItemData.get(i).getItemName());
                lstItemDataBySubMenu.add(data);
            }
        }

        dgChooseItemListAdapter =new DgChooseItemListAdapter(this, lstItemDataBySubMenu);
        lvItem.setAdapter(dgChooseItemListAdapter);
        dgChooseItemListAdapter.setCustomCheckedListener(this);
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
            String editAddress=printerAddress;
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
}
