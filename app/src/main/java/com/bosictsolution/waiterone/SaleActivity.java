package com.bosictsolution.waiterone;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.legacy.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.View;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.andprn.request.android.RequestHandler;

import adapter.DgTasteGridAdapter;
import adapter.ItemImageListAdapter;
import adapter.ItemListAdapter;
import adapter.MenuExpandableListAdapter;
import adapter.OrderListAdapter;
import adapter.SaleItemSubRvAdapter;
import common.AlertView;
import common.DBHelper;
import common.FeatureList;
import common.PrinterWiFiPort;
import common.SystemSetting;
import data.ItemData;
import data.ItemSubGroupData;
import data.MainMenuData;
import data.SubMenuData;
import data.TasteData;
import data.TransactionData;
import listener.DialogTasteClickListener;
import listener.ItemImageButtonClickListener;
import listener.OrderButtonClickListener;

public class SaleActivity extends AppCompatActivity implements DialogTasteClickListener, OrderButtonClickListener, ItemImageButtonClickListener {

    DBHelper db;
    SystemSetting systemSetting=new SystemSetting();
    List<String> listDataHeader;
    HashMap<String,List<String>> listDataChild;
    MenuExpandableListAdapter expListAdapter;
    OrderListAdapter orderListAdapter;
    DgTasteGridAdapter dgTasteGridAdapter;
    ItemListAdapter itemListAdapter;
    ItemImageListAdapter itemImageListAdapter;
    LinearLayout layoutOrderPlace;
    static TextView tvTaste;
    GridView gvItemImage;

    ExpandableListView expList;
    ListView lvItem, lvOrder;
    TextView tvSubMenuName, tvTableName,tvShowTaste,tvStartTime;
    static TextView tvWaiterName;
    Button btnChooseTable,btnSendOrder,btnPrint;
    LinearLayout layoutItem;
    View selectedItemView;

    final Context context = this;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Calendar cCalendar;

    static int taste_position;
    public static int choosed_table_id;
    public static String choosed_table_name;
    int waiter_id;
    String waiter_name,insert_order;
    static boolean isTasteEdit;

    List<SubMenuData> lstSubMenuData=new ArrayList<>();
    List<MainMenuData> lstMainMenuData=new ArrayList<>();
    List<ItemData> lstItemData=new ArrayList<>();
    List<TasteData> lstTaste=new ArrayList();
    List<TransactionData> lstOrderItem=new ArrayList<>();
    public static List<TransactionData> lstOrder;
    int orderWaiterID,orderTableID,allowOrderTime,allowAutoTaste,allowCustomerInfo,allowBookingTable,allowItemImage;
    String orderWaiterName,orderTableName,printer_message="",endTime;
    boolean isFirstTime=true;
    public static boolean isTableClear;

    private static final String TAG = "WiFiConnectMenu";
    private PrinterWiFiPort wifiPort= PrinterWiFiPort.getInstance();
    private String printerIPAddress="";
    private Thread hThread;
    String ioException;
    SaleItemSubRvAdapter saleItemSubRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        db=new DBHelper(this);
        if(db.getFeatureResult(FeatureList.fUseItemImage) == 1) allowItemImage = 1;
        else allowItemImage = 0;
        setLayoutResource();

        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);

        Intent intent=getIntent();
        waiter_id=intent.getIntExtra("waiterid", 0);
        waiter_name=intent.getStringExtra("waitername");
        tvWaiterName.setText(waiter_name);
        tvWaiterName.setTag(waiter_id);

        getAllowedMainMenu();
        getAllSubMenu();
        setMenuToExpList();
        setEmptyItem();
        getAllTaste();
        if(db.getFeatureResult(FeatureList.fUseMultiPrinter)==1)setTitle("");
        allowOrderTime=db.getFeatureResult(FeatureList.fOrderTime);

        //connectPrinter();

        expList.setOnChildClickListener(new OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent,View view,int groupPosition,int childPosition,long id){
                int subMenuID,mainMenuID;
                String subMenuName,mainMenuName;
                List<Integer> lstSubMenuID = new ArrayList<>();

                mainMenuID = lstMainMenuData.get(groupPosition).getMainMenuID();
                mainMenuName = lstMainMenuData.get(groupPosition).getMainMenuName();
                subMenuName = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                for (int i = 0; i < lstSubMenuData.size(); i++) {
                    if (lstSubMenuData.get(i).mainMenuID == mainMenuID) {
                        lstSubMenuID.add(lstSubMenuData.get(i).subMenuID);
                    }
                }
                subMenuID = lstSubMenuID.get(childPosition);
                getItemBySubMenuID(subMenuID);

                if(allowItemImage != 1) {
                    tvSubMenuName.setText(subMenuName);
                    if (drawerLayout != null) drawerLayout.closeDrawer(expList);
                }else showItemImageDialog(mainMenuName+" > "+subMenuName);
                return false;
            }
        });

        if(drawerLayout!=null){
            actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.mipmap.menu,R.string.app_name,R.string.app_name){
                @SuppressLint("RestrictedApi")
                public void onDrawerClosed(View view){
                    invalidateOptionsMenu();
                }
            };
        }

        if(drawerLayout!=null){
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
            if(savedInstanceState==null){
            }
        }

        lvItem.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View v,int position,long id){
                if(saleItemSubRvAdapter != null)saleItemSubRvAdapter.lstItemSubGroup=new ArrayList<>();
                if(lstItemData.size()!=0) {
                    int outOfOrder = lstItemData.get(position).getOutOfOrder();
                    if (outOfOrder == 1) {
                        systemSetting.showMessage(SystemSetting.ERROR,"Out of Order Item!",context,getLayoutInflater());
                        return;
                    } else {
                        String itemId=lstItemData.get(position).getItemid();
                        List<ItemSubGroupData> lstItemSubGroupData=db.getItemSubByItemID(itemId);
                        if(lstItemSubGroupData.size()!=0){
                            showItemSubDialog(lstItemSubGroupData,position);
                        }else {
                            isShowTasteAndPlaceOrder(position);
                        }
                    }
                }
            }
        });

        btnChooseTable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                chooseTable();
            }
        });

        btnSendOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(choosed_table_id!=0) {
                    //allowOrderTime=db.getFeatureResult(FeatureList.fOrderTime);
                    //if (allowOrderTime == 1) getCurrentTime();
                    if (lstOrderItem.size() != 0) {
                        orderWaiterID = Integer.parseInt(tvWaiterName.getTag().toString());
                        orderWaiterName = tvWaiterName.getText().toString();
                        orderTableID = Integer.parseInt(tvTableName.getTag().toString());
                        orderTableName = tvTableName.getText().toString();
                        sendOrder();
                    }
                }else{
                    systemSetting.showMessage(SystemSetting.INFO,"Choose Table!",context,getLayoutInflater());
                }
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choosed_table_id==0 || lstOrderItem.size()!=0){
                    systemSetting.showMessage(SystemSetting.WARNING,"Please, Send Order First!",context,getLayoutInflater());
                    return;
                } else{
                    printVoucher();
                }
            }
        });

        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choosed_table_id==0){
                    systemSetting.showMessage(SystemSetting.INFO,"Choose Table!",context,getLayoutInflater());
                    return;
                }
                showTimeDialog();
            }
        });
    }

    private void printOrder(){
        lstOrder = new ArrayList<>();
        String stringQty;
        int integerQty;
        float floatQty;

        Cursor cur = db.getTranForOrderPrint(choosed_table_id);
        if (cur.getCount() == 0) return;
        while (cur.moveToNext()) {
            TransactionData data = new TransactionData();
            data.setItemName(cur.getString(1));
            data.setStringQty(cur.getString(2));

            stringQty = cur.getString(2);
            floatQty = Float.parseFloat(stringQty);
            if (floatQty == Math.round(floatQty)) {
                integerQty = Math.round(floatQty);
                data.setIntegerQty(integerQty);
            } else {
                data.setFloatQty(floatQty);
            }
            data.setTaste(cur.getString(3));
            data.setStype(cur.getInt(4));
            lstOrder.add(data);
        }

        Intent i;
        if(db.getFeatureResult(FeatureList.fUseMultiPrinter) == 1) {
            i = new Intent(getApplicationContext(), PrintOrderMultiPrinterActivity.class);
        }else{
            i = new Intent(getApplicationContext(), PrintOrderActivity.class);
        }
        i.putExtra("TableId", choosed_table_id);
        i.putExtra("TableName", choosed_table_name);
        i.putExtra("UserName", waiter_name);
        startActivity(i);
    }

    private void printVoucher() {
        lstOrder = new ArrayList<>();
        String stringQty, date = "", vouNo = "",startTime="";
        int integerQty, taxPercent = 0, chargesPercent = 0, slipId = 0, tranId = 0, tableId = 0, waiterId = 0;
        float floatQty;
        double subTotal = 0, chargesAmount, taxAmount = 0, grandTotal;

        // Get Transaction by Table
        Cursor cur = db.getTransactionFromTranSaleTemp(choosed_table_id);
        if (cur.getCount() == 0) return;
        while (cur.moveToNext()) {
            TransactionData data = new TransactionData();
            if(cur.getColumnCount()==9) data.setItemName(cur.getString(1)+" - "+cur.getString(8));
            else data.setItemName(cur.getString(1));
            data.setStringQty(cur.getString(2));

            stringQty = cur.getString(2);
            floatQty = Float.parseFloat(stringQty);
            if (floatQty == Math.round(floatQty)) {
                integerQty = Math.round(floatQty);
                data.setIntegerQty(integerQty);
            } else {
                data.setFloatQty(floatQty);
            }
            data.setAmount(cur.getDouble(3));
            data.setSalePrice(cur.getDouble(5));
            lstOrder.add(data);

            subTotal += cur.getDouble(3);
        }

        // Calculate Amount
        Cursor cGetTaxCharges = db.getSystemSetting();
        if (cGetTaxCharges.getCount() == 1) {
            if (cGetTaxCharges.moveToFirst()) {
                taxPercent = cGetTaxCharges.getInt(1);
                chargesPercent = cGetTaxCharges.getInt(2);
            }
        }

        chargesAmount = (chargesPercent * subTotal) / 100;

            if (db.getFeatureResult(FeatureList.fTaxIncCharg) == 1) {
                taxAmount = (taxPercent * (subTotal + chargesAmount)) / 100;
            } else {
                taxAmount = (taxPercent * subTotal) / 100;
            }

        grandTotal = subTotal + taxAmount + chargesAmount;

        // Pay Bill
        Cursor curSlip = db.getSlipID();
        if (curSlip.moveToFirst()) {
            slipId = curSlip.getInt(0);
            db.updateSlipID();
        }

        Cursor curMaster = db.getTransactionDetailFromMasterSaleTemp(choosed_table_id);
        if (curMaster.moveToFirst()) {
            tranId = curMaster.getInt(0);
            date = curMaster.getString(1);
            vouNo = curMaster.getString(2);
            tableId = curMaster.getInt(3);
            waiterId = curMaster.getInt(4);
            startTime=curMaster.getString(5);
        }

        List<TransactionData> lstTransactionData = new ArrayList<>();
        Cursor curTran = db.getTransactionDetailFromTranSaleTemp(choosed_table_id);
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

        if (db.insertMasterSale(tranId, date, vouNo, tableId, waiterId, subTotal, taxAmount, chargesAmount, 0, grandTotal, getCurrentTime(), slipId,startTime,getCurrentEndTime())) {
            for (int i = 0; i < lstTransactionData.size(); i++) {
                int tran_id = lstTransactionData.get(i).getTranid();
                int srNo = lstTransactionData.get(i).getSrNo();
                String itemId = lstTransactionData.get(i).getItemid();
                String itemName = lstTransactionData.get(i).getItemName();
                String quantity = lstTransactionData.get(i).getStringQty();
                double doubleQty = Double.parseDouble(quantity);
                double price = lstTransactionData.get(i).getSalePrice();
                double amount = lstTransactionData.get(i).getAmount();
                String orderTime = lstTransactionData.get(i).getOrderTime();
                String taste = lstTransactionData.get(i).getTaste();
                int counterId = lstTransactionData.get(i).getCounterID();
                db.insertTranSale(tran_id, srNo, itemId, itemName, doubleQty, price, amount, orderTime, taste, counterId, getCurrentTime());
            }
            if (db.deleteTransactionFromTranSaleTemp(tableId)) {
                db.updateTranIDFromMasterSaleTempByTableID(tableId);
            }
        }

        // Redirect printVoucher
        Intent i = new Intent(getApplicationContext(), PrintBillActivity.class);
        i.putExtra("slipid", slipId);
        i.putExtra("tableid", tableId);
        i.putExtra("table", choosed_table_name);
        i.putExtra("waiter", waiter_name);
        i.putExtra("datetime", date + " " + getCurrentTime());
        String subtotal = new DecimalFormat("#").format(Double.parseDouble(String.valueOf(subTotal)));
        i.putExtra("subtotal", subtotal);
        String tax = new DecimalFormat("#").format(Double.parseDouble(String.valueOf(taxAmount)));
        i.putExtra("tax", tax);
        String charges = new DecimalFormat("#").format(Double.parseDouble(String.valueOf(chargesAmount)));
        i.putExtra("charges", charges);
        String grandtotal = new DecimalFormat("#").format(Double.parseDouble(String.valueOf(grandTotal)));
        i.putExtra("grandtotal", grandtotal);
        i.putExtra("discount", "0");
        i.putExtra("paid", "0");
        i.putExtra("change", "0");
        i.putExtra("PrintFromSale",true);
        i.putExtra("StartTime",startTime);
        i.putExtra("EndTime",endTime);
        startActivity(i);
    }

    //when using the ActionBarDrawerToggle, must call it during onPostCreate() and onConfigurationChanged()
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        if(actionBarDrawerToggle!=null){
            actionBarDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if(actionBarDrawerToggle!=null){
            actionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(db.isOrderStateOrNotByTableID(choosed_table_id)==1){
            tvStartTime.setText("Set Time");
            tvStartTime.setEnabled(false);
            Toast.makeText(context,"Reorder",Toast.LENGTH_SHORT).toString();
        }
        else{
            tvStartTime.setText("Set Time");
            tvStartTime.setEnabled(true);
        }
        if(isTableClear){
            tvTableName.setText("");
            choosed_table_id=0;
            choosed_table_name="";
            isTableClear=false;
        }
        try {
            wifiDisConn();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        connectPrinter();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        if(choosed_table_id!=0){
            tvTableName.setTag(choosed_table_id);
        }
        if(choosed_table_name!=""){
            tvTableName.setText(choosed_table_name);
        }
    }

    @Override
    public void onBackPressed(){
        systemSetting.showMessage(SystemSetting.INFO,"Press Exit!",context,getLayoutInflater());
    }

    @Override
    public void setTitle(CharSequence title){
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi=inflater.inflate(R.layout.actionbar_sale, null);
        ImageButton btnViewOrder=(ImageButton)vi.findViewById(R.id.btnViewOrder);
        ImageButton btnGetBill=(ImageButton)vi.findViewById(R.id.btnGetBill);
        ImageButton btnCustomerEntry=(ImageButton)vi.findViewById(R.id.btnCustomerEntry);
        ImageButton btnBooking=(ImageButton)vi.findViewById(R.id.btnBooking);
        ImageButton btnChangeTable=(ImageButton)vi.findViewById(R.id.btnChangeTable);
        TextView tvShopName=(TextView)vi.findViewById(R.id.tvShopName);
        TextView tvPrinterMessage=(TextView)vi.findViewById(R.id.tvPrinterMessage);
        Button btnConnect=(Button)vi.findViewById(R.id.btnConnect);
        Button btnExit=(Button)vi.findViewById(R.id.btnExit);

        tvPrinterMessage.setText(printer_message);
        if(printer_message.equals("Printer Online!")){
            tvPrinterMessage.setTextColor(getResources().getColor(R.color.colorSuccess));
            btnConnect.setVisibility(View.INVISIBLE);
        }
        else if(printer_message.equals("Printer Offline!")) {
            tvPrinterMessage.setTextColor(getResources().getColor(R.color.colorGray2));
            btnConnect.setVisibility(View.VISIBLE);
        }
        Cursor cur_shop_name=db.getShopName();
        if(cur_shop_name.moveToFirst())tvShopName.setText(cur_shop_name.getString(0));

        allowCustomerInfo=db.getFeatureResult(FeatureList.fCustomerInfo);
        if(allowCustomerInfo==1)btnCustomerEntry.setVisibility(View.VISIBLE);
        else btnCustomerEntry.setVisibility(View.GONE);

        allowBookingTable=db.getFeatureResult(FeatureList.fBookingTable);
        if(allowBookingTable==1)btnBooking.setVisibility(View.VISIBLE);
        else btnBooking.setVisibility(View.GONE);

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT);
        getSupportActionBar().setCustomView(vi, params);

        btnViewOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionViewOrder();
            }
        });
        btnGetBill.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionBill();
            }
        });
        btnCustomerEntry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionCustomerEntry();
            }
        });
        btnBooking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionBooking();
            }
        });
        btnChangeTable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionChangeTable();
            }
        });
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstTime=true;
                connectPrinter();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lstOrderItem=new ArrayList<>();
                clearChoosedTable();
                try {
                    wifiDisConn();
                }catch (IOException e) {
                    Log.e(TAG,e.getMessage(),e);
                }catch (InterruptedException e) {
                    Log.e(TAG,e.getMessage(),e);
                }
                finish();
            }
        });
    }

    private void connectPrinter(){
        if(db.getFeatureResult(FeatureList.fUseMultiPrinter)!=1) { // not use multi printer
            Cursor cur = db.getPrinterSetting();
            if (cur.moveToFirst()) printerIPAddress = cur.getString(0);
            wifiPort = PrinterWiFiPort.getInstance();
            try {
                wifiConn(printerIPAddress);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }else{  // use multi printer
            printerIPAddress=db.getNetworkPrinter();
            if(printerIPAddress.length()!=0) {
                wifiPort = PrinterWiFiPort.getInstance();
                try {
                    wifiConn(printerIPAddress);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }else{
                setTitle("");
            }
        }

        /**else{
            systemSetting.showMessage(SystemSetting.INFO,"Not found Network Printer!",context,getLayoutInflater());
        }**/
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
        private final ProgressDialog dialog = new ProgressDialog(SaleActivity.this);

        @Override
        protected void onPreExecute()
        {
            if(isFirstTime) {
                dialog.setTitle("Printer Connect");
                dialog.setMessage("Connecting");
                dialog.setCancelable(false);
                dialog.show();
            }
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
                printer_message="Printer Online!";
                setTitle("");
            }
            else
            {
                if(dialog.isShowing())
                    dialog.dismiss();
                printer_message="Printer Offline!";
                setTitle("");
                AlertView.showAlert("Failed", "Check Devices!"+ioException, context);
            }
            isFirstTime=false;
            super.onPostExecute(result);
        }
    }

    private void actionCustomerEntry(){
        Intent intent_cus_entry=new Intent(this,TableActivity.class);
        intent_cus_entry.putExtra("role", "cusinfo");
        startActivity(intent_cus_entry);
    }

    private void actionChangeTable(){
        Intent intent=new Intent(this,ChangeTableActivity.class);
        startActivity(intent);
    }

    private void actionBill(){
        Intent intent_bill=new Intent(this,TableActivity.class);
        intent_bill.putExtra("role", "bill");
        startActivity(intent_bill);
    }

    private void actionViewOrder(){
        Intent intent_view_order=new Intent(this,TableActivity.class);
        intent_view_order.putExtra("role", "view");
        startActivity(intent_view_order);
    }

    private void actionBooking(){
        Intent intent_booking=new Intent(this,BookingListActivity.class);
        intent_booking.putExtra("waiterid",waiter_id);
        intent_booking.putExtra("waitername",waiter_name);
        startActivity(intent_booking);
    }

    @Override
    public void onPlusButtonClickListener(int position,EditText editText){
        float floatQty = Float.parseFloat(editText.getText().toString());
        int integerQty;
        if(floatQty==Math.round(floatQty)){
            integerQty=Integer.parseInt(editText.getText().toString());
            integerQty=integerQty+1;
            if(integerQty<1)
                integerQty=1;
            editText.setText(String.valueOf(integerQty));
            lstOrderItem.get(position).setStringQty(String.valueOf(integerQty));
            lstOrderItem.get(position).setIntegerQty(integerQty);
            double price=lstOrderItem.get(position).getSalePrice();
            double amount=price*integerQty;
            lstOrderItem.get(position).setAmount(amount);
        }else{
            systemSetting.showMessage(SystemSetting.INFO,"Change quantity on clicking calculator!",context,getLayoutInflater());
        }
    }

    @Override
    public void onMinusButtonClickListener(int position,EditText editText){
        float floatQty = Float.parseFloat(editText.getText().toString());
        int integerQty;
        if(floatQty==Math.round(floatQty)){
            integerQty=Integer.parseInt(editText.getText().toString());
            integerQty=integerQty-1;
            if(integerQty<1)
                integerQty=1;
            editText.setText(String.valueOf(integerQty));
            lstOrderItem.get(position).setStringQty(String.valueOf(integerQty));
            lstOrderItem.get(position).setIntegerQty(integerQty);
            double price=lstOrderItem.get(position).getSalePrice();
            double amount=price*integerQty;
            lstOrderItem.get(position).setAmount(amount);
        }else{
            systemSetting.showMessage(SystemSetting.INFO,"Change quantity on clicking calculator!",context,getLayoutInflater());
        }
    }

    @Override
    public void onCalculatorButtonClickListener(int position,EditText editText){
        showCalculatorDialog(1,position,editText,null);
    }

    @Override
    public void onTasteButtonClickListener(int position,TextView textView){
        String curTaste=lstOrderItem.get(position).getTaste();
        showTasteDialog(position);
        tvShowTaste.setText(curTaste);
        isTasteEdit=true;
        taste_position=position;
        tvTaste=textView;
    }

    @Override
    public void onTasteClickListener(int position){
        String curTaste=tvShowTaste.getText().toString();
        if(curTaste.length()!=0)curTaste=curTaste+",";
        tvShowTaste.setText(curTaste+lstTaste.get(position).getTasteName());
    }

    @Override
    public void onCancelButtonClickListener(int position,View row){
        lstOrderItem.remove(position);
        orderListAdapter=new OrderListAdapter(this,lstOrderItem);
        lvOrder.setAdapter(orderListAdapter);
        orderListAdapter.setOnOrderButtonClickListener(this);
    }

    @Override
    public void onPriceButtonClickListener(int position,Button btnPrice){
        showCalculatorDialog(2,position,null,btnPrice);
    }

    @Override
    public void onOrderClickListener(int position) {
        if(saleItemSubRvAdapter != null)saleItemSubRvAdapter.lstItemSubGroup=new ArrayList<>();
        if (lstItemData.size() != 0) {
            int outOfOrder = lstItemData.get(position).getOutOfOrder();
            if (outOfOrder == 1) {
                systemSetting.showMessage(SystemSetting.ERROR, "Out of Order Item!", context, getLayoutInflater());
                return;
            } else {
                String itemId=lstItemData.get(position).getItemid();
                List<ItemSubGroupData> lstItemSubGroupData=db.getItemSubByItemID(itemId);
                if(lstItemSubGroupData.size()!=0){
                    showItemSubDialog(lstItemSubGroupData,position);
                }else {
                    isShowTasteAndPlaceOrder(position);
                }
            }
        }
    }

    private void chooseTable(){
        Intent intent=new Intent(SaleActivity.this,TableActivity.class);
        intent.putExtra("from_waiter_main", true);
        intent.putExtra("from_customer_info", false);
        intent.putExtra("role", "just_choice");
        startActivity(intent);
    }

    private void clearChoosedTable(){
        tvTableName.setTag(null);
        tvTableName.setText("");
        choosed_table_id=0;
        choosed_table_name="";
    }

    private void setEmptyItem(){
        tvSubMenuName.setText("Items");
        List<String> listEmptyItem=new ArrayList<>();
        listEmptyItem.add("No Items!");
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.list_empty,R.id.tvEmpty,listEmptyItem);
        lvItem.setAdapter(adapter);
    }

    private void getAllowedMainMenu(){
        lstMainMenuData=new ArrayList<>();
        Cursor cur=db.getAllowMainMenu();
        if(cur.getCount()!=0) {
            while (cur.moveToNext()) {
                MainMenuData data=new MainMenuData();
                data.setMainMenuID(cur.getInt(0));
                data.setMainMenuName(cur.getString(1));
                data.setCounterid(cur.getInt(2));
                lstMainMenuData.add(data);
            }
            if(!cur.isClosed()){
                cur.close();
            }
        }
    }

    private void getAllSubMenu(){
        Cursor cur=db.getSubMenu();
        lstSubMenuData=new ArrayList<>();
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                SubMenuData data=new SubMenuData();
                data.setSubMenuID(cur.getInt(0));
                data.setSubMenuName(cur.getString(1));
                data.setMainMenuID(cur.getInt(2));
                data.setSortCode(cur.getString(3));
                data.setMainMenuName(cur.getString(4));
                lstSubMenuData.add(data);
            }
            if(!cur.isClosed()){
                cur.close();
            }
        }
    }

    private void setMenuToExpList(){
        listDataHeader=new ArrayList<>();
        listDataChild=new HashMap<>();
        for(int i=0;i<lstMainMenuData.size();i++){
            int mainMenuID=lstMainMenuData.get(i).getMainMenuID();
            String mainMenuName=lstMainMenuData.get(i).getMainMenuName();

            List<String> lstSubMenuName=new ArrayList<>();
            for(int j=0;j<lstSubMenuData.size();j++){
                if(lstSubMenuData.get(j).getMainMenuID()==mainMenuID){
                    lstSubMenuName.add(lstSubMenuData.get(j).getSubMenuName());
                }
            }
            if(lstSubMenuName.size()!=0){
                listDataChild.put(mainMenuName, lstSubMenuName);
                listDataHeader.add(mainMenuName);
            }
        }
        expListAdapter=new MenuExpandableListAdapter(this,listDataHeader,listDataChild);
        expList.setAdapter(expListAdapter);
    }

    private void getItemBySubMenuID(int subMenuID){
        lstItemData=new ArrayList<>();
        Cursor cur=db.getItemBySubMenu(subMenuID);
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                ItemData data=new ItemData();
                data.setId(cur.getInt(0));
                data.setItemid(cur.getString(1));
                data.setItemName(cur.getString(2));
                data.setSubMenuID(cur.getInt(3));
                data.setPrice(cur.getDouble(4));
                data.setOutOfOrder(cur.getInt(5));
                data.setSubMenuName(cur.getString(6));
                data.setsTypeID(cur.getInt(7));
                data.setItemImage(cur.getBlob(8));
                lstItemData.add(data);
            }
            if(!cur.isClosed()){
                cur.close();
            }
            if(allowItemImage != 1) {
                itemListAdapter = new ItemListAdapter(this, lstItemData);
                lvItem.setAdapter(itemListAdapter);
            }
        }
        else{
            setEmptyItem();
        }
    }

    private void getAllTaste(){
        lstTaste=new ArrayList();
        Cursor cur=db.getTaste();
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                TasteData data=new TasteData();
                data.setTasteid(cur.getInt(0));
                data.setTasteName(cur.getString(1));
                lstTaste.add(data);
            }
        }
    }

    private void setLayoutResource(){
        expList =(ExpandableListView)findViewById(android.R.id.list);
        lvItem =(ListView)findViewById(R.id.lvItem);
        lvOrder =(ListView) findViewById(R.id.lvOrder);
        btnSendOrder=(Button)findViewById(R.id.btnSendOrder);
        tvWaiterName=(TextView)findViewById(R.id.tvWaiterName);
        tvSubMenuName =(TextView)findViewById(R.id.tvSubMenuName);
        btnChooseTable =(Button)findViewById(R.id.btnChooseTable);
        btnPrint=(Button)findViewById(R.id.btnPrint);
        tvStartTime=(TextView)findViewById(R.id.tvStartTime);
        tvTableName =(TextView)findViewById(R.id.tvTableName);
        layoutItem=(LinearLayout)findViewById(R.id.layoutItem);
        layoutOrderPlace=(LinearLayout)findViewById(R.id.layoutOrderPlace);

        if(db.getFeatureResult(FeatureList.fPrintButtonInSale) ==1)btnPrint.setVisibility(View.VISIBLE);
        else btnPrint.setVisibility(View.GONE);

        if(db.getFeatureResult(FeatureList.fStartEndTime)==1)tvStartTime.setVisibility(View.VISIBLE);
        else tvStartTime.setVisibility(View.GONE);

        if(allowItemImage == 1){
            layoutItem.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    layoutOrderPlace.getLayoutParams();
            params.weight = 2;
            layoutOrderPlace.setLayoutParams(params);
        }
        else layoutItem.setVisibility(View.VISIBLE);
    }

    private void showTasteDialog(final int position){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dg_taste, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);
        tvShowTaste=(TextView)view.findViewById(R.id.tvTaste);
        final GridView gvTaste=(GridView)view.findViewById(R.id.gvTaste);
        final Button btnOK=(Button)view.findViewById(R.id.btnOK);
        final Button btnCancel=(Button)view.findViewById(R.id.btnCancel);
        final Button btnClear=(Button)view.findViewById(R.id.btnClear);
        final EditText etCustomTaste=(EditText)view.findViewById(R.id.etCustomTaste);
        final Button btnTasteAdd=(Button)view.findViewById(R.id.btnTasteAdd);

        dgTasteGridAdapter =new DgTasteGridAdapter(this,lstTaste);
        gvTaste.setAdapter(dgTasteGridAdapter);
        dgTasteGridAdapter.setOnTasteClickListener(this);

        dialog.setCancelable(true);
        final AlertDialog alertDialog=dialog.create();
        alertDialog.show();

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                if(!isTasteEdit){
                    placeOrder(position,tvShowTaste.getText().toString());
                }else{
                    tvTaste.setText("");
                    tvTaste.setText(tvShowTaste.getText().toString());
                    lstOrderItem.get(taste_position).setTaste(tvShowTaste.getText().toString());
                }
                isTasteEdit=false;
                alertDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                if(!isTasteEdit){
                    placeOrder(position,"");
                }
                isTasteEdit=false;
                alertDialog.dismiss();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                tvShowTaste.setText("");
            }
        });
        btnTasteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etCustomTaste.getText().toString().length()!=0){
                    String currentTaste=tvShowTaste.getText().toString();
                    if(currentTaste.length()!=0)tvShowTaste.setText(currentTaste+","+etCustomTaste.getText().toString());
                    else tvShowTaste.setText(etCustomTaste.getText().toString());
                    etCustomTaste.setText("");
                }
            }
        });
    }

    private void showTimeDialog(){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dg_time, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);
        final Spinner spTimePeriod=(Spinner)view.findViewById(R.id.spTimePeriod);
        final EditText etHour=(EditText) view.findViewById(R.id.etHour);
        final EditText etMinute=(EditText) view.findViewById(R.id.etMinute);
        final Button btnOK=(Button)view.findViewById(R.id.btnOK);
        final Button btnCancel=(Button)view.findViewById(R.id.btnCancel);

        String currentTime=getCurrentTime();

        // 10:00 am
        String arr[]=currentTime.split(":");
        String hour=arr[0];  //get hour
        String str=arr[1];
        String arr2[]=str.split("\\s+");
        String minute=arr2[0];  //get minute
        String period=arr2[1];  //get period

        etHour.setText(hour);
        etMinute.setText(minute);

        List<String> list=new ArrayList<>();
        list.add("AM");
        list.add("PM");
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,list);
        spTimePeriod.setAdapter(arrayAdapter);

        if(period.equals("am"))spTimePeriod.setSelection(0);
        else spTimePeriod.setSelection(1);

        dialog.setCancelable(true);
        final AlertDialog alertDialog=dialog.create();
        alertDialog.show();

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                String hour=etHour.getText().toString();
                String minute=etMinute.getText().toString();
                String period=spTimePeriod.getSelectedItem().toString();
                tvStartTime.setText(hour+":"+minute+" "+period);
                alertDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                alertDialog.dismiss();
            }
        });
    }

    private void placeOrder(int position,String taste){
        String itemSub="";
        int itemSubPrice=0;

        if(saleItemSubRvAdapter != null){
            for(int i=0;i<saleItemSubRvAdapter.lstItemSubGroup.size();i++){
                for(int x=0;x<saleItemSubRvAdapter.lstItemSubGroup.get(i).getLstItemSubData().size();x++){
                    if(saleItemSubRvAdapter.lstItemSubGroup.get(i).getLstItemSubData().get(x).isSelected()){
                        itemSubPrice+=saleItemSubRvAdapter.lstItemSubGroup.get(i).getLstItemSubData().get(x).getPrice();
                        itemSub+=saleItemSubRvAdapter.lstItemSubGroup.get(i).getLstItemSubData().get(x).getSubName()+",";
                    }
                }
            }
        }
        if(itemSub.length()!=0) itemSub=itemSub.substring(0, itemSub.length() - 1);

        TransactionData data=new TransactionData();
        data.setItemid(lstItemData.get(position).getItemid());
        data.setItemName(lstItemData.get(position).getItemName());
        data.setSalePrice(lstItemData.get(position).getPrice()+itemSubPrice);
        data.setCounterID(lstItemData.get(position).getCounterID());
        data.setStype(lstItemData.get(position).getsTypeID());
        data.setStringQty("1");
        data.setIntegerQty(1);
        data.setTaste(taste);
        data.setAmount(lstItemData.get(position).getPrice()+itemSubPrice);
        if(allowOrderTime==1) data.setOrderTime(getCurrentTime());
        data.setItemImage(lstItemData.get(position).getItemImage());
        data.setAllItemSub(itemSub);

        lstOrderItem.add(data);

        orderListAdapter=new OrderListAdapter(this,lstOrderItem);
        lvOrder.setAdapter(orderListAdapter);
        orderListAdapter.setOnOrderButtonClickListener(this);

        Toast.makeText(context,"Order Added!",Toast.LENGTH_SHORT).show();
    }

    private void showCalculatorDialog(final int type,final int position,final EditText etOrderQty,final Button btnOrderPrice){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dg_calculator, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);

        final EditText etValue=(EditText)view.findViewById(R.id.etQty);
        final ImageButton btnBackspace=(ImageButton)view.findViewById(R.id.btnBackspace);
        final Button btnOK=(Button)view.findViewById(R.id.btnOK);
        final Button btnOne=(Button)view.findViewById(R.id.btnOne);
        final Button btnTwo=(Button)view.findViewById(R.id.btnTwo);
        final Button btnThree=(Button)view.findViewById(R.id.btnThree);
        final Button btnFour=(Button)view.findViewById(R.id.btnFour);
        final Button btnFive=(Button)view.findViewById(R.id.btnFive);
        final Button btnSix=(Button)view.findViewById(R.id.btnSix);
        final Button btnSeven=(Button)view.findViewById(R.id.btnSeven);
        final Button btnEight=(Button)view.findViewById(R.id.btnEight);
        final Button btnNine=(Button)view.findViewById(R.id.btnNine);
        final Button btnZero=(Button)view.findViewById(R.id.btnZero);
        final Button btnPoint=(Button)view.findViewById(R.id.btnPoint);
        final Button btnClear=(Button)view.findViewById(R.id.btnClear);

        /*String curQty=lstOrderItem.get(position).getStringQty();
        etQty.setText(curQty);*/

        dialog.setCancelable(true);
        final AlertDialog alertDialog=dialog.create();
        alertDialog.show();

        etValue.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT=2;
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if(event.getRawX() >= (etValue.getRight() - etValue.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(etValue.getText().toString().length()!=0){
                            String searchkey=etValue.getText().toString();
                            searchkey=searchkey.substring(0,searchkey.length()-1);
                            etValue.setText(searchkey);
                        }
                    }
                }
                return false;
            }
        });

        btnBackspace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    searchkey=searchkey.substring(0,searchkey.length()-1);
                    etValue.setText(searchkey);
                }
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                if(etValue.getText().toString().length()!=0){
                    double price,amount;
                    float floatQty;
                    int integerQty;
                    String value=etValue.getText().toString();
                    char lastChar=value.charAt(value.length()-1);
                    if(lastChar=='.') {
                        systemSetting.showMessage(SystemSetting.INFO,"Please enter number behind point!",context,getLayoutInflater());
                        return;
                    }else{
                        if(type==1) {
                            floatQty = Float.parseFloat(value);
                            if (floatQty == Math.round(floatQty)) {
                                integerQty = Math.round(floatQty);
                                etOrderQty.setText(String.valueOf(integerQty));
                                lstOrderItem.get(position).setStringQty(String.valueOf(integerQty));
                                lstOrderItem.get(position).setIntegerQty(integerQty);
                                price = lstOrderItem.get(position).getSalePrice();
                                amount = price * integerQty;
                                lstOrderItem.get(position).setAmount(amount);
                            } else {
                                etOrderQty.setText(String.valueOf(floatQty));
                                lstOrderItem.get(position).setStringQty(String.valueOf(floatQty));
                                lstOrderItem.get(position).setFloatQty(floatQty);
                                price = lstOrderItem.get(position).getSalePrice();
                                amount = price * floatQty;
                                lstOrderItem.get(position).setAmount(amount);
                            }
                        }else if(type==2){
                            btnOrderPrice.setText(value);
                            lstOrderItem.get(position).setSalePrice(Double.parseDouble(value));
                            String sQty=lstOrderItem.get(position).getStringQty();
                            floatQty=Float.parseFloat(sQty);
                            if(floatQty==Math.round(floatQty)){
                                integerQty=Math.round(floatQty);
                                amount=Double.parseDouble(value)*integerQty;
                                lstOrderItem.get(position).setAmount(amount);
                            }else{
                                amount=Double.parseDouble(value)*floatQty;
                                lstOrderItem.get(position).setAmount(amount);
                            }
                        }
                    }
                    alertDialog.dismiss();

                }else{
                    systemSetting.showMessage(SystemSetting.INFO,"Enter Value",context,getLayoutInflater());
                }
            }
        });

        btnOne.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    String newkey=searchkey+btnOne.getText().toString();
                    etValue.setText(newkey);
                }else{
                    etValue.setText(btnOne.getText().toString());
                }
            }
        });

        btnTwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    String newkey=searchkey+btnTwo.getText().toString();
                    etValue.setText(newkey);
                }else{
                    etValue.setText(btnTwo.getText().toString());
                }
            }
        });

        btnThree.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    String newkey=searchkey+btnThree.getText().toString();
                    etValue.setText(newkey);
                }else{
                    etValue.setText(btnThree.getText().toString());
                }
            }
        });

        btnFour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    String newkey=searchkey+btnFour.getText().toString();
                    etValue.setText(newkey);
                }else{
                    etValue.setText(btnFour.getText().toString());
                }
            }
        });

        btnFive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    String newkey=searchkey+btnFive.getText().toString();
                    etValue.setText(newkey);
                }else{
                    etValue.setText(btnFive.getText().toString());
                }
            }
        });

        btnSix.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    String newkey=searchkey+btnSix.getText().toString();
                    etValue.setText(newkey);
                }else{
                    etValue.setText(btnSix.getText().toString());
                }
            }
        });

        btnSeven.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    String newkey=searchkey+btnSeven.getText().toString();
                    etValue.setText(newkey);
                }else{
                    etValue.setText(btnSeven.getText().toString());
                }
            }
        });

        btnEight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    String newkey=searchkey+btnEight.getText().toString();
                    etValue.setText(newkey);
                }else{
                    etValue.setText(btnEight.getText().toString());
                }
            }
        });

        btnNine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    String newkey=searchkey+btnNine.getText().toString();
                    etValue.setText(newkey);
                }else{
                    etValue.setText(btnNine.getText().toString());
                }
            }
        });

        btnZero.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    String newkey=searchkey+btnZero.getText().toString();
                    etValue.setText(newkey);
                }else{
                    etValue.setText(btnZero.getText().toString());
                }
            }
        });

        btnPoint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    String searchkey=etValue.getText().toString();
                    String newkey=searchkey+btnPoint.getText().toString();
                    etValue.setText(newkey);
                }else{
                    etValue.setText(btnPoint.getText().toString());
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etValue.getText().toString().length()!=0){
                    etValue.setText("");
                }
            }
        });
    }

    private void sendOrder(){
        String orderItemID,orderItemName,orderTaste,orderTime,startTime="";
        int tranid=0,orderCounterID,orderIntQuantity,srNo=0,orderSType,orderState;
        float floatQty,orderFloatQuantity;
        double orderAmount,orderPrice;

        tranid=db.getTranIDFromMasterSaleTempByTableID(orderTableID);
        orderState=db.isOrderStateOrNotByTableID(orderTableID);

        if(db.getFeatureResult(FeatureList.fStartEndTime)==1){
            if(orderState==0) {
                if (!tvStartTime.getText().toString().equals("Set Time"))
                    startTime = tvStartTime.getText().toString();
                else {
                    systemSetting.showMessage(SystemSetting.ERROR, "Set Time!", context, getLayoutInflater());
                    return;
                }
            }
        }

        if(orderState==0)db.updateMasterSaleTempByTableID(orderTableID,orderWaiterID,startTime);

        Cursor curTran=db.getTransactionFromTranSaleTemp(orderTableID);
        if(curTran.moveToFirst()){
            Cursor curSrno=db.getMaxSrNoFromTranSaleTemp(orderTableID);
            if(curSrno.moveToFirst()){
                srNo=curSrno.getInt(0);
            }
        }

        insert_order="";
        for(int i=0;i<lstOrderItem.size();i++){
            orderCounterID=lstOrderItem.get(i).getCounterID();
            orderItemID=lstOrderItem.get(i).getItemid();
            orderItemName=lstOrderItem.get(i).getItemName();
            orderTaste=lstOrderItem.get(i).getTaste();
            orderAmount=lstOrderItem.get(i).getAmount();
            orderPrice=lstOrderItem.get(i).getSalePrice();
            orderSType=lstOrderItem.get(i).getStype();
            orderTime=lstOrderItem.get(i).getOrderTime();
            srNo+=1;
            floatQty = Float.parseFloat(lstOrderItem.get(i).getStringQty());
            if(floatQty==Math.round(floatQty)){
                orderIntQuantity=Integer.parseInt(lstOrderItem.get(i).getStringQty());
                db.insertTranSaleTempByIntegerQty(tranid,srNo,orderItemID,orderItemName,orderIntQuantity,orderPrice,orderAmount,orderTime,orderTaste,orderCounterID,orderTableID,orderSType);
            }else{
                orderFloatQuantity=floatQty;
                db.insertTranSaleTempByFloatQty(tranid,srNo,orderItemID,orderItemName,orderFloatQuantity,orderPrice,orderAmount,orderTime,orderTaste,orderCounterID,orderTableID,orderSType);
            }

        }

        // for booking tableid delete
        Cursor cur=db.getBookingByTableID(orderTableID);
        if(cur.moveToFirst()){
            db.deleteBooking(orderTableID);
        }
        systemSetting.showMessage(SystemSetting.SUCCESS,orderTableName+" Order Sent!",context,getLayoutInflater());
        clearOrder();
        if(db.getFeatureResult(FeatureList.fPrintOrder)==1){
            printOrder();
        }
    }

    private void clearOrder(){
        lstOrderItem=new ArrayList<>();
        orderListAdapter=new OrderListAdapter(this,lstOrderItem);
        lvOrder.setAdapter(orderListAdapter);
        orderListAdapter.setOnOrderButtonClickListener(this);
    }

    private String getCurrentTime(){
        String time;
        cCalendar= Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat(SystemSetting.TIME_FORMAT);
        time=timeFormat.format(cCalendar.getTime());
        return time;
    }

    private String getCurrentEndTime(){
        if(db.getFeatureResult(FeatureList.fStartEndTime)==0)return "";
        cCalendar= Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat(SystemSetting.TIME_FORMAT);
        endTime=timeFormat.format(cCalendar.getTime());
        endTime=endTime.toUpperCase();
        return endTime;
    }

    private void showItemImageDialog(String menu){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dg_item_list_image, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);
        final TextView tvMenu=(TextView)view.findViewById(R.id.tvMenu);
        gvItemImage=(GridView)view.findViewById(R.id.gvItemImage);
        final ImageButton btnClose=(ImageButton)view.findViewById(R.id.btnClose);

        tvMenu.setText(menu);
        itemImageListAdapter =new ItemImageListAdapter(this,lstItemData);
        gvItemImage.setAdapter(itemImageListAdapter);
        itemImageListAdapter.setOnOrderClickListener(this);

        dialog.setCancelable(true);
        final AlertDialog alertDialog=dialog.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.show();
        alertDialog.getWindow().setAttributes(lp);

        gvItemImage.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(selectedItemView == null){
                    selectedItemView = view;
                    view.setBackgroundColor(getResources().getColor(R.color.colorInfo));
                }else if(selectedItemView == view){
                    selectedItemView.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                    selectedItemView = null;
                }else{
                    selectedItemView.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                    view.setBackgroundColor(getResources().getColor(R.color.colorInfo));
                    selectedItemView = view;
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alertDialog.dismiss();
            }
        });
    }

    private void showItemSubDialog(List<ItemSubGroupData> lstItemSubGroupData,int position){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dg_sale_item_sub, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);

        final ImageButton btnClose=view.findViewById(R.id.btnClose);
        final RecyclerView rvRootItemSub=view.findViewById(R.id.rvRootItemSub);
        final Button btnOk=view.findViewById(R.id.btnOk);

        saleItemSubRvAdapter =new SaleItemSubRvAdapter(lstItemSubGroupData,context);
        rvRootItemSub.setAdapter(saleItemSubRvAdapter);
        rvRootItemSub.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        dialog.setCancelable(false);
        final AlertDialog alertDialog=dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowTasteAndPlaceOrder(position);
                alertDialog.dismiss();
            }
        });
    }

    private void isShowTasteAndPlaceOrder(int position){
        allowAutoTaste = db.getFeatureResult(FeatureList.fAutoTaste);
        if (allowAutoTaste == 1) showTasteDialog(position);
        else placeOrder(position, "");
    }
}
