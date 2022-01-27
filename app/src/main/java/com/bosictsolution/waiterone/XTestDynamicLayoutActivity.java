package com.bosictsolution.waiterone;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import common.DBHelper;
import common.SystemSetting;
import customfont.BOSTextView;
import data.TransactionData;

/**
 * Created by User on 5/29/2018.
 */
public class XTestDynamicLayoutActivity extends AppCompatActivity {

    ScrollView scrollView;
    LinearLayout layoutFooter,layoutParent;

    int tabletSize;
    private final int PRINT_TRAN_SIZE=10;
    DBHelper db;
    private Calendar calendar;
    String date,time,shopName,description,printDate;
    double subTotal=0,taxTotal=0,chargesTotal=0,disTotal=0,grandTotal=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp <= 600) {
            tabletSize = 8;
        } else {
            tabletSize = 10;
        }

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0073c4")));

        db = new DBHelper(this);
        calendar=Calendar.getInstance();
        setTitle("Day End Report");
        getData();
        createPrintLayout();
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

    private void createDynamicLinearLayout(LinearLayout layoutPrint){
        layoutPrint = new LinearLayout(this);
        LinearLayout.LayoutParams layoutPrintParams = new LinearLayout.LayoutParams(600, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutPrintParams.setMargins(0,10,0,0);
        layoutPrint.setOrientation(LinearLayout.VERTICAL);
        layoutPrint.setGravity(Gravity.CENTER);
        layoutPrint.setPadding(0,0,17,0);
        layoutPrint.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        layoutPrint.setLayoutParams(layoutPrintParams);
    }

    private void createPrintLayout(){
        scrollView=new ScrollView(this);
        ScrollView.LayoutParams scrollViewParams = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT);
        scrollView.setLayoutParams(scrollViewParams);

        layoutParent=new LinearLayout(this);
        LinearLayout.LayoutParams layoutParentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParent.setOrientation(LinearLayout.VERTICAL);
        layoutParent.setGravity(Gravity.CENTER);
        layoutParent.setPadding(5,5,5,5);
        layoutParent.setBackgroundColor(getResources().getColor(R.color.colorBookingTable));
        layoutParent.setLayoutParams(layoutParentParams);

        LinearLayout layoutPrint = new LinearLayout(this);
        LinearLayout.LayoutParams layoutPrintParams = new LinearLayout.LayoutParams(600, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutPrintParams.setMargins(0,10,0,0);
        layoutPrint.setOrientation(LinearLayout.VERTICAL);
        layoutPrint.setGravity(Gravity.CENTER);
        layoutPrint.setPadding(0,0,17,0);
        layoutPrint.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        layoutPrint.setLayoutParams(layoutPrintParams);

        BOSTextView tvReportName=new BOSTextView(this,null);
        TableRow.LayoutParams tvReportNameParams = new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        tvReportName.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvReportName.setPadding(5,5,5,5);
        tvReportName.setTypeface(Typeface.DEFAULT_BOLD);
        tvReportName.setText("End of Day Report");
        if(tabletSize==10)tvReportName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_title_10));
        else tvReportName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_title_8));
        tvReportName.setLayoutParams(tvReportNameParams);

        BOSTextView tvShopName=new BOSTextView(this,null);
        TableRow.LayoutParams tvShopNameParams = new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        tvShopName.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvShopName.setPadding(5,5,5,5);
        tvShopName.setTypeface(Typeface.DEFAULT_BOLD);
        if(tabletSize==10)tvShopName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
        else tvShopName.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
        tvShopName.setLayoutParams(tvShopNameParams);

        BOSTextView tvShopDesc=new BOSTextView(this,null);
        TableRow.LayoutParams tvShopDescParams = new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        tvShopDesc.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvShopDesc.setPadding(5,5,5,5);
        tvShopDesc.setTypeface(Typeface.DEFAULT_BOLD);
        if(tabletSize==10)tvShopDesc.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
        else tvShopDesc.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
        tvShopDesc.setLayoutParams(tvShopDescParams);
        if(description.length()!=0)tvShopDesc.setText(description);
        else tvShopDesc.setVisibility(View.GONE);

        BOSTextView tvPrintDate=new BOSTextView(this,null);
        TableRow.LayoutParams tvPrintDateParams = new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        tvPrintDate.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvPrintDate.setPadding(5,5,5,5);
        tvPrintDate.setTypeface(Typeface.DEFAULT_BOLD);
        if(tabletSize==10)tvPrintDate.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
        else tvPrintDate.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
        tvPrintDate.setLayoutParams(tvPrintDateParams);
        if(printDate.length()!=0)tvPrintDate.setText(printDate);
        else tvPrintDate.setVisibility(View.GONE);

        LinearLayout layoutHeader = new LinearLayout(this);
        LinearLayout.LayoutParams layoutHeaderParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutHeaderParams.setMargins(0,10,0,0);
        layoutHeader.setLayoutParams(layoutHeaderParams);

        BOSTextView tvHeaderSlipID=new BOSTextView(this,null);
        TableRow.LayoutParams tvHeaderSlipIDParams = new TableRow.LayoutParams( 0, TableRow.LayoutParams.WRAP_CONTENT,0.8f);
        tvHeaderSlipID.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvHeaderSlipID.setPadding(5,5,5,5);
        tvHeaderSlipID.setTypeface(Typeface.DEFAULT_BOLD);
        tvHeaderSlipID.setText("Slip");
        if(tabletSize==10)tvHeaderSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
        else tvHeaderSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
        tvHeaderSlipID.setLayoutParams(tvHeaderSlipIDParams);

        BOSTextView tvHeaderTotalAmt=new BOSTextView(this,null);
        TableRow.LayoutParams tvHeaderTotalAmtParams = new TableRow.LayoutParams( 0, TableRow.LayoutParams.WRAP_CONTENT,1.3f);
        tvHeaderTotalAmt.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvHeaderTotalAmt.setPadding(5,5,5,5);
        tvHeaderTotalAmt.setTypeface(Typeface.DEFAULT_BOLD);
        tvHeaderTotalAmt.setText("Total");
        tvHeaderTotalAmt.setGravity(Gravity.RIGHT);
        if(tabletSize==10)tvHeaderTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
        else tvHeaderTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
        tvHeaderTotalAmt.setLayoutParams(tvHeaderTotalAmtParams);

        BOSTextView tvHeaderTax=new BOSTextView(this,null);
        TableRow.LayoutParams tvHeaderTaxParams = new TableRow.LayoutParams( 0, TableRow.LayoutParams.WRAP_CONTENT,1.2f);
        tvHeaderTax.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvHeaderTax.setPadding(5,5,5,5);
        tvHeaderTax.setTypeface(Typeface.DEFAULT_BOLD);
        tvHeaderTax.setText("Tax");
        tvHeaderTax.setGravity(Gravity.RIGHT);
        if(tabletSize==10)tvHeaderTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
        else tvHeaderTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
        tvHeaderTax.setLayoutParams(tvHeaderTaxParams);

        BOSTextView tvHeaderCharges=new BOSTextView(this,null);
        TableRow.LayoutParams tvHeaderChargesParams = new TableRow.LayoutParams( 0, TableRow.LayoutParams.WRAP_CONTENT,1.2f);
        tvHeaderCharges.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvHeaderCharges.setPadding(5,5,5,5);
        tvHeaderCharges.setTypeface(Typeface.DEFAULT_BOLD);
        tvHeaderCharges.setText("Charges");
        tvHeaderCharges.setGravity(Gravity.RIGHT);
        if(tabletSize==10)tvHeaderCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
        else tvHeaderCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
        tvHeaderCharges.setLayoutParams(tvHeaderChargesParams);

        BOSTextView tvHeaderDis=new BOSTextView(this,null);
        TableRow.LayoutParams tvHeaderDisParams = new TableRow.LayoutParams( 0, TableRow.LayoutParams.WRAP_CONTENT,1.2f);
        tvHeaderDis.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvHeaderDis.setPadding(5,5,5,5);
        tvHeaderDis.setTypeface(Typeface.DEFAULT_BOLD);
        tvHeaderDis.setText("Dis");
        tvHeaderDis.setGravity(Gravity.RIGHT);
        if(tabletSize==10)tvHeaderDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
        else tvHeaderDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
        tvHeaderDis.setLayoutParams(tvHeaderDisParams);

        BOSTextView tvHeaderNetAmt=new BOSTextView(this,null);
        TableRow.LayoutParams tvHeaderNetAmtParams = new TableRow.LayoutParams( 0, TableRow.LayoutParams.WRAP_CONTENT,1.3f);
        tvHeaderNetAmt.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvHeaderNetAmt.setPadding(5,5,5,5);
        tvHeaderNetAmt.setTypeface(Typeface.DEFAULT_BOLD);
        tvHeaderNetAmt.setText("Net Amt");
        tvHeaderNetAmt.setGravity(Gravity.RIGHT);
        if(tabletSize==10)tvHeaderNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
        else tvHeaderNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
        tvHeaderNetAmt.setLayoutParams(tvHeaderNetAmtParams);

        LinearLayout layoutHeaderBar = new LinearLayout(this);
        LinearLayout.LayoutParams layoutHeaderBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        layoutHeaderBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        layoutHeaderBar.setLayoutParams(layoutHeaderBarParams);

        LinearLayout layoutPrintList = new LinearLayout(this);
        LinearLayout.LayoutParams layoutPrintListParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutPrintList.setOrientation(LinearLayout.VERTICAL);
        layoutPrintList.setLayoutParams(layoutPrintListParams);

        LinearLayout layoutFooterBar = new LinearLayout(this);
        LinearLayout.LayoutParams layoutFooterBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        layoutFooterBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        layoutFooterBar.setLayoutParams(layoutFooterBarParams);

        layoutFooter=new LinearLayout(this);
        LinearLayout.LayoutParams layoutFooterParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutFooterParams.setMargins(0,0,0,10);
        layoutFooter.setLayoutParams(layoutFooterParams);

        BOSTextView tvEmpty=new BOSTextView(this,null);
        TableRow.LayoutParams tvEmptyParams  = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f);
        tvEmpty.setLayoutParams(tvEmptyParams);

        BOSTextView tvTotalAmt=new BOSTextView(this,null);
        TableRow.LayoutParams tvTotalAmtParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.3f);
        tvTotalAmt.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvTotalAmt.setPadding(5,5,5,5);
        tvTotalAmt.setGravity(Gravity.RIGHT);
        tvTotalAmt.setTypeface(Typeface.DEFAULT_BOLD);
        if(tabletSize==10)tvTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
        else tvTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
        tvTotalAmt.setLayoutParams(tvTotalAmtParams);

        BOSTextView tvTax=new BOSTextView(this,null);
        TableRow.LayoutParams tvTaxParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.2f);
        tvTax.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvTax.setPadding(5,5,5,5);
        tvTax.setGravity(Gravity.RIGHT);
        tvTax.setTypeface(Typeface.DEFAULT_BOLD);
        if(tabletSize==10)tvTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
        else tvTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
        tvTax.setLayoutParams(tvTaxParams);

        BOSTextView tvCharges=new BOSTextView(this,null);
        TableRow.LayoutParams tvChargesParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.2f);
        tvCharges.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvCharges.setPadding(5,5,5,5);
        tvCharges.setGravity(Gravity.RIGHT);
        tvCharges.setTypeface(Typeface.DEFAULT_BOLD);
        if(tabletSize==10)tvCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
        else tvCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
        tvCharges.setLayoutParams(tvChargesParams);

        BOSTextView tvDis=new BOSTextView(this,null);
        TableRow.LayoutParams tvDisParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.2f);
        tvDis.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvDis.setPadding(5,5,5,5);
        tvDis.setGravity(Gravity.RIGHT);
        tvDis.setTypeface(Typeface.DEFAULT_BOLD);
        if(tabletSize==10)tvDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
        else tvDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
        tvDis.setLayoutParams(tvDisParams);

        BOSTextView tvNetAmt=new BOSTextView(this,null);
        TableRow.LayoutParams tvNetAmtParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.3f);
        tvNetAmt.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvNetAmt.setPadding(5,5,5,5);
        tvNetAmt.setGravity(Gravity.RIGHT);
        tvNetAmt.setTypeface(Typeface.DEFAULT_BOLD);
        if(tabletSize==10)tvNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
        else tvNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
        tvNetAmt.setLayoutParams(tvNetAmtParams);

        for(int i = 0; i< RpDayEndActivity.lstTransaction.size(); i++){
            subTotal+= RpDayEndActivity.lstTransaction.get(i).getSubTotal();
            taxTotal+= RpDayEndActivity.lstTransaction.get(i).getTax();
            chargesTotal+= RpDayEndActivity.lstTransaction.get(i).getCharges();
            disTotal+= RpDayEndActivity.lstTransaction.get(i).getDiscount();
            grandTotal+= RpDayEndActivity.lstTransaction.get(i).getGrandTotal();
        }
        tvTotalAmt.setText(String.valueOf(new DecimalFormat("#").format(subTotal)));
        tvTax.setText(String.valueOf(new DecimalFormat("#").format(taxTotal)));
        tvCharges.setText(String.valueOf(new DecimalFormat("#").format(chargesTotal)));
        tvDis.setText(String.valueOf(new DecimalFormat("#").format(disTotal)));
        tvNetAmt.setText(String.valueOf(new DecimalFormat("#").format(grandTotal)));

        while(RpDayEndActivity.lstTransaction.size()>PRINT_TRAN_SIZE) {
            for (int i = 0; i < PRINT_TRAN_SIZE; i++) {
                TransactionData data = RpDayEndActivity.lstTransaction.get(i);
                LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View row = layoutInflater.inflate(R.layout.list_rp_day_end_child, null);
                TextView tvSlipID = (TextView) row.findViewById(R.id.tvSlipID);
                TextView tvItemTotalAmt = (TextView) row.findViewById(R.id.tvTotalAmt);
                TextView tvItemTax = (TextView) row.findViewById(R.id.tvTax);
                TextView tvItemCharges = (TextView) row.findViewById(R.id.tvCharges);
                TextView tvItemDis = (TextView) row.findViewById(R.id.tvDis);
                TextView tvItemNetAmt = (TextView) row.findViewById(R.id.tvNetAmt);

                if (tabletSize == 10) {
                    tvSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                } else {
                    tvSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                }

                tvSlipID.setText(String.valueOf(data.getSlipid()));
                String totalAmt = new DecimalFormat("#").format(data.getSubTotal());
                tvItemTotalAmt.setText(totalAmt);
                String tax = new DecimalFormat("#").format(data.getTax());
                tvItemTax.setText(tax);
                String charges = new DecimalFormat("#").format(data.getCharges());
                tvItemCharges.setText(charges);
                String dis = new DecimalFormat("#").format(data.getDiscount());
                tvItemDis.setText(dis);
                String netAmt = new DecimalFormat("#").format(data.getGrandTotal());
                tvItemNetAmt.setText(netAmt);
                layoutPrintList.addView(row);
            }

            for (int i = 0; i < PRINT_TRAN_SIZE; i++) {
                RpDayEndActivity.lstTransaction.remove(i);
            }
        }

        if(RpDayEndActivity.lstTransaction.size()<=PRINT_TRAN_SIZE){
            for (int i = 0; i < RpDayEndActivity.lstTransaction.size(); i++) {
                TransactionData data = RpDayEndActivity.lstTransaction.get(i);
                LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View row = layoutInflater.inflate(R.layout.list_rp_day_end_child, null);
                TextView tvSlipID = (TextView) row.findViewById(R.id.tvSlipID);
                TextView tvItemTotalAmt = (TextView) row.findViewById(R.id.tvTotalAmt);
                TextView tvItemTax = (TextView) row.findViewById(R.id.tvTax);
                TextView tvItemCharges = (TextView) row.findViewById(R.id.tvCharges);
                TextView tvItemDis = (TextView) row.findViewById(R.id.tvDis);
                TextView tvItemNetAmt = (TextView) row.findViewById(R.id.tvNetAmt);

                if (tabletSize == 10) {
                    tvSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_10));
                } else {
                    tvSlipID.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemTotalAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemTax.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemCharges.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemDis.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemNetAmt.setTextSize(getResources().getDimensionPixelSize(R.dimen.print_label_8));
                }

                tvSlipID.setText(String.valueOf(data.getSlipid()));
                String totalAmt = new DecimalFormat("#").format(data.getSubTotal());
                tvItemTotalAmt.setText(totalAmt);
                String tax = new DecimalFormat("#").format(data.getTax());
                tvItemTax.setText(tax);
                String charges = new DecimalFormat("#").format(data.getCharges());
                tvItemCharges.setText(charges);
                String dis = new DecimalFormat("#").format(data.getDiscount());
                tvItemDis.setText(dis);
                String netAmt = new DecimalFormat("#").format(data.getGrandTotal());
                tvItemNetAmt.setText(netAmt);
                layoutPrintList.addView(row);
            }
        }

        // adding views
        layoutPrint.addView(tvReportName);

        if(shopName.length()!=0) {
            tvShopName.setText(shopName);
            layoutPrint.addView(tvShopName);
        }

        if(description.length()!=0) {
            tvShopDesc.setText(description);
            layoutPrint.addView(tvShopDesc);
        }

        if(printDate.length()!=0) {
            tvPrintDate.setText(printDate);
            layoutPrint.addView(tvPrintDate);
        }

        layoutHeader.addView(tvHeaderSlipID);
        layoutHeader.addView(tvHeaderTotalAmt);
        layoutHeader.addView(tvHeaderTax);
        layoutHeader.addView(tvHeaderCharges);
        layoutHeader.addView(tvHeaderDis);
        layoutHeader.addView(tvHeaderNetAmt);
        layoutPrint.addView(layoutHeader);

        layoutPrint.addView(layoutHeaderBar);
        layoutPrint.addView(layoutPrintList);
        layoutPrint.addView(layoutFooterBar);

        layoutFooter.addView(tvEmpty);
        layoutFooter.addView(tvTotalAmt);
        layoutFooter.addView(tvTax);
        layoutFooter.addView(tvCharges);
        layoutFooter.addView(tvDis);
        layoutFooter.addView(tvNetAmt);
        layoutPrint.addView(layoutFooter);

        layoutParent.addView(layoutPrint);

        scrollView.addView(layoutParent);

        setContentView(scrollView);
    }
}
