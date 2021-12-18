package com.bosictsolution.waiterone;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andprn.jpos.command.ESCPOSConst;
import com.andprn.jpos.printer.ESCPOSPrinter;
import com.bosictsolution.waiterone.print.GPrinterCommand;
import com.bosictsolution.waiterone.print.PrintPic;
import com.bosictsolution.waiterone.print.PrintQueue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import adapter.RpSummaryItemListAdapter;
import common.SystemSetting;
import data.RpSummaryItemData;
import common.DBHelper;

/**
 * Created by NweYiAung on 23-12-2016.
 */
public class RpSummaryItemActivity extends AppCompatActivity {

    DBHelper db;
    RpSummaryItemData rpSummaryItemData;
    List<RpSummaryItemData> lstRpSummaryItemData;
    RpSummaryItemListAdapter rpSummaryItemListAdapter;
    private ProgressDialog progDialog;
    TextView tvHeaderItem,tvHeaderQty,tvHeaderPrice, tvHeaderAmount,tvFromDate,tvToDate,tvToday,tvNumberOFItems,tvTotalQty,tvTotalAmount;
    LinearLayout layoutSummaryItemPrint,layoutPrintList,layoutReportSummaryItem;
    TextView tvPrintHeaderItem,tvPrintHeaderQty,tvPrintHeaderPrice, tvPrintHeaderAmount,tvPrintFromDate,tvPrintToDate,tvPrintTitle;
    public static String fromDate,toDate;
    private Calendar cCalendar;
    int totalQty;
    double totalAmount;
    public static ArrayList<String> lstCheckedItems =new ArrayList<>();
    private ESCPOSPrinter posPtr;
    final Context context = this;
    Button btnPrint;
    int tabletSize,interfaceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_summary_item);

        Configuration config=getResources().getConfiguration();
        if(config.smallestScreenWidthDp<=600){
            tabletSize=8;
        }else{
            tabletSize=10;
        }

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0084B9")));

        Intent i=getIntent();
        fromDate=i.getStringExtra("from_date");
        toDate=i.getStringExtra("to_date");
        lstCheckedItems =i.getStringArrayListExtra("checked_item");
        interfaceId=i.getIntExtra("InterfaceID",0);

        db = new DBHelper(this);
        progDialog=new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(false);
        setTitle("Summary Item Report");
        setLayoutResource();
        setPrintLayoutResource();
        getData();

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePrintLayoutToBitmap();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
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

    @Override
    public void setTitle(CharSequence title) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.actionbar_label, null);
        TextView tvActionBarText = (TextView) vi.findViewById(R.id.tvActionBarText);
        tvActionBarText.setText(title);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setCustomView(vi, params);
    }

    private void setLayoutResource() {
        tvHeaderItem = (TextView) findViewById(R.id.tvHeaderItem);
        tvHeaderQty = (TextView) findViewById(R.id.tvHeaderQty);
        tvHeaderPrice = (TextView) findViewById(R.id.tvHeaderPrice);
        tvHeaderAmount = (TextView) findViewById(R.id.tvHeaderAmount);
        layoutReportSummaryItem = (LinearLayout) findViewById(R.id.layoutReportSummaryItem);
        tvNumberOFItems = (TextView) findViewById(R.id.tvNumberOFItems);
        tvTotalQty = (TextView) findViewById(R.id.tvTotalQty);
        tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmount);
        btnPrint=(Button)findViewById(R.id.btnPrint);

        tvFromDate = (TextView) findViewById(R.id.tvFromDate);
        tvToDate = (TextView) findViewById(R.id.tvToDate);
        tvFromDate.setText("From Date: "+fromDate);
        tvToDate.setText("To Date: "+toDate);
        tvToday = (TextView) findViewById(R.id.tvToday);
        cCalendar=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(SystemSetting.DATE_FORMAT);
        String todayDate=dateFormat.format(cCalendar.getTime());
        if(todayDate.equals(fromDate) && todayDate.equals(toDate)) tvToday.setVisibility(View.VISIBLE);
        else tvToday.setVisibility(View.GONE);
    }

    private void setPrintLayoutResource() {
        tvPrintTitle = (TextView) findViewById(R.id.tvPrintTitle);
        tvPrintHeaderItem = (TextView) findViewById(R.id.tvPrintHeaderItem);
        tvPrintHeaderQty = (TextView) findViewById(R.id.tvPrintHeaderQty);
        tvPrintHeaderPrice = (TextView) findViewById(R.id.tvPrintHeaderPrice);
        tvPrintHeaderAmount = (TextView) findViewById(R.id.tvPrintHeaderAmount);
        layoutSummaryItemPrint=(LinearLayout)findViewById(R.id.layoutSummaryItemPrint);
        layoutPrintList=(LinearLayout)findViewById(R.id.layoutPrintList);

        tvPrintFromDate = (TextView) findViewById(R.id.tvPrintFromDate);
        tvPrintToDate = (TextView) findViewById(R.id.tvPrintToDate);
        tvPrintFromDate.setText("From Date: "+fromDate);
        tvPrintToDate.setText("To Date: "+toDate);

        if(tabletSize==10){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutSummaryItemPrint.getLayoutParams();
            params.width = 600;
            tvPrintTitle.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_title_10));
            tvPrintFromDate.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvPrintToDate.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvPrintHeaderItem.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvPrintHeaderQty.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvPrintHeaderPrice.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            tvPrintHeaderAmount.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
        }else{
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutSummaryItemPrint.getLayoutParams();
            params.width = 600;
            tvPrintTitle.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_title_8));
            tvPrintFromDate.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvPrintToDate.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvPrintHeaderItem.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvPrintHeaderQty.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvPrintHeaderPrice.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            tvPrintHeaderAmount.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
        }
    }

    private void getData(){
        String itemList="";
        if(lstCheckedItems.size()!=0) {
            for (int i = 0; i < lstCheckedItems.size(); i++) {
                itemList += lstCheckedItems.get(i) + ",";
            }
        }
        if(itemList.length()!=0) {
            itemList = itemList.substring(0, itemList.length() - 1);
        }
        Cursor cur=db.getReportSummaryItem(fromDate,toDate,itemList);
        lstRpSummaryItemData =new ArrayList<>();
        List<Integer> lstMainMenuID=new ArrayList<>();
        List<Integer> lstSubMenuID=new ArrayList<>();

        while(cur.moveToNext()){
            rpSummaryItemData =new RpSummaryItemData();
            lstMainMenuID.add(cur.getInt(7));
            lstSubMenuID.add(cur.getInt(9));
            rpSummaryItemData.setMainMenuID(cur.getInt(7));
            rpSummaryItemData.setSubMenuID(cur.getInt(9));
            int type_times= Collections.frequency(lstMainMenuID,cur.getInt(7));
            if(type_times == 1) {
                rpSummaryItemData.setTotalByMainMenu(cur.getDouble(8));
                rpSummaryItemData.setMainMenuName(cur.getString(3));
            }
            int times= Collections.frequency(lstSubMenuID,cur.getInt(9));
            if(times == 1) {
                rpSummaryItemData.setTotalBySubMenu(cur.getDouble(10));
                rpSummaryItemData.setSubMenuName(cur.getString(2));
            }
            rpSummaryItemData.setName(cur.getString(1));
            rpSummaryItemData.setQty(cur.getInt(4));
            rpSummaryItemData.setPrice(cur.getDouble(5));
            rpSummaryItemData.setAmount(cur.getDouble(6));

            totalQty+=cur.getInt(4);
            totalAmount+=cur.getDouble(6);
            lstRpSummaryItemData.add(rpSummaryItemData);
        }
        setData();
    }

    private void setData(){
        tvTotalQty.setText("Total Qty: "+String.valueOf(totalQty));
        tvTotalAmount.setText("Total Amount: "+String.valueOf(totalAmount));
        tvNumberOFItems.setText("No. of Items: "+String.valueOf(lstRpSummaryItemData.size()));
        setupShowLayoutList();
        setupPrintLayoutList();
        //rpSummaryItemListAdapter =new RpSummaryItemListAdapter(this, lstRpSummaryItemData);
        //lvReportSummaryItem.setAdapter(rpSummaryItemListAdapter);
    }

    private void setupShowLayoutList() {
        for (int i = 0; i < lstRpSummaryItemData.size(); i++) {
            RpSummaryItemData data = lstRpSummaryItemData.get(i);
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.list_rp_summary_item, null);
            TextView tvListItem = (TextView) row.findViewById(R.id.tvListItem);
            TextView tvListQty = (TextView) row.findViewById(R.id.tvListQty);
            TextView tvListPrice = (TextView) row.findViewById(R.id.tvListPrice);
            TextView tvListAmount = (TextView) row.findViewById(R.id.tvListAmount);
            TextView tvListMainMenuName = (TextView) row.findViewById(R.id.tvListMainMenuName);
            TextView tvListTotalByMainMenu = (TextView) row.findViewById(R.id.tvListTotalByMainMenu);
            TextView tvListTotalBySubMenu = (TextView) row.findViewById(R.id.tvListTotalBySubMenu);
            TextView tvListSubMenuName = (TextView) row.findViewById(R.id.tvListSubMenuName);

            String isnull = data.getMainMenuName();
            if (isnull == null) {
                tvListMainMenuName.setVisibility(View.GONE);
                tvListTotalByMainMenu.setVisibility(View.GONE);
            } else {
                tvListMainMenuName.setVisibility(View.VISIBLE);
                tvListTotalByMainMenu.setVisibility(View.VISIBLE);
                tvListMainMenuName.setText(data.getMainMenuName());
                String total = new DecimalFormat("#").format(data.getTotalByMainMenu());
                tvListTotalByMainMenu.setText("Total: " + total);
            }

            isnull = data.getSubMenuName();
            if (isnull == null) {
                tvListSubMenuName.setVisibility(View.GONE);
                tvListTotalBySubMenu.setVisibility(View.GONE);
            } else {
                tvListSubMenuName.setVisibility(View.VISIBLE);
                tvListTotalBySubMenu.setVisibility(View.VISIBLE);
                tvListSubMenuName.setText(data.getSubMenuName());
                String total = new DecimalFormat("#").format(data.getTotalBySubMenu());
                tvListTotalBySubMenu.setText("Total: " + total);
            }

            tvListItem.setText(data.getName());
            tvListQty.setText(String.valueOf(data.getQty()));
            String price = new DecimalFormat("#").format(data.getPrice());
            tvListPrice.setText(price);
            String amount = new DecimalFormat("#").format(data.getAmount());
            tvListAmount.setText(amount);
            layoutReportSummaryItem.addView(row);
        }
    }

    private void setupPrintLayoutList() {
        for (int i = 0; i < lstRpSummaryItemData.size(); i++) {
            RpSummaryItemData data = lstRpSummaryItemData.get(i);
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.list_rp_summary_item, null);
            TextView tvListItem = (TextView) row.findViewById(R.id.tvListItem);
            TextView tvListQty = (TextView) row.findViewById(R.id.tvListQty);
            TextView tvListPrice =(TextView) row.findViewById(R.id.tvListPrice);
            TextView tvListAmount = (TextView) row.findViewById(R.id.tvListAmount);
            TextView tvListMainMenuName = (TextView) row.findViewById(R.id.tvListMainMenuName);
            TextView tvListTotalByMainMenu = (TextView) row.findViewById(R.id.tvListTotalByMainMenu);
            TextView tvListTotalBySubMenu =(TextView) row.findViewById(R.id.tvListTotalBySubMenu);
            TextView tvListSubMenuName = (TextView) row.findViewById(R.id.tvListSubMenuName);

            if(tabletSize==10){
                tvListMainMenuName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_main_menu_10));
                tvListTotalByMainMenu.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_main_menu_10));
                tvListSubMenuName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_sub_menu_10));
                tvListTotalBySubMenu.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_sub_menu_10));
                tvListItem.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvListQty.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvListPrice.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvListAmount.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
            }else{
                tvListMainMenuName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_main_menu_8));
                tvListTotalByMainMenu.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_main_menu_8));
                tvListSubMenuName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_sub_menu_8));
                tvListTotalBySubMenu.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_sub_menu_8));
                tvListItem.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvListQty.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvListPrice.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvListAmount.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
            }

            String isnull= data.getMainMenuName();
            if(isnull==null) {
                tvListMainMenuName.setVisibility(View.GONE);
                tvListTotalByMainMenu.setVisibility(View.GONE);
            }
            else {
                tvListMainMenuName.setVisibility(View.VISIBLE);
                tvListTotalByMainMenu.setVisibility(View.VISIBLE);
                tvListMainMenuName.setText(data.getMainMenuName());
                String total = new DecimalFormat("#").format(data.getTotalByMainMenu());
                tvListTotalByMainMenu.setText("Total: " + total);
            }

            isnull= data.getSubMenuName();
            if(isnull==null) {
                tvListSubMenuName.setVisibility(View.GONE);
                tvListTotalBySubMenu.setVisibility(View.GONE);
            }
            else {
               tvListSubMenuName.setVisibility(View.VISIBLE);
               tvListTotalBySubMenu.setVisibility(View.VISIBLE);
               tvListSubMenuName.setText(data.getSubMenuName());
                String total = new DecimalFormat("#").format(data.getTotalBySubMenu());
                tvListTotalBySubMenu.setText("Total: " + total);
            }

            tvListItem.setText(data.getName());
            tvListQty.setText(String.valueOf(data.getQty()));
            String price=new DecimalFormat("#").format(data.getPrice());
            tvListPrice.setText(price);
            String amount=new DecimalFormat("#").format(data.getAmount());
            tvListAmount.setText(amount);
            layoutPrintList.addView(row);
        }
    }

    private void changePrintLayoutToBitmap(){
        Bitmap bitmap=Bitmap.createBitmap(layoutSummaryItemPrint.getWidth(), layoutSummaryItemPrint.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        layoutSummaryItemPrint.draw(canvas);
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

    private String savePrintLayoutToWaiterOneDB(Bitmap bitmapImage){
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logoPath=new File(directory,"summaryItemPrint.png");

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
        File filePath=new File(directory,"summaryItemPrint.png");
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
        File filePath=new File(directory,"summaryItemPrint.png");
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
}
