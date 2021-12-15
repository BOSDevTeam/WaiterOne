package adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.List;

import common.DBHelper;
import common.FeatureList;
import data.PrintBillData;
import data.TransactionData;

/**
 * Created by User on 7/30/2018.
 */
public class PrintBillParentListAdapter extends BaseAdapter {
    private Context context;
    List<PrintBillData> lstPrintBillData;
    PrintBillChildListAdapter printBillChildListAdapter;
    int tabletSize;
    DBHelper db;

    public PrintBillParentListAdapter(Context context, List<PrintBillData> lstPrintBillData){

        Configuration config=context.getResources().getConfiguration();
        if(config.smallestScreenWidthDp<=600){
            tabletSize=8;
        }else{
            tabletSize=10;
        }

        this.context=context;
        db=new DBHelper(this.context);
        this.lstPrintBillData = lstPrintBillData;
    }

    @Override
    public int getCount(){
        return lstPrintBillData.size();
    }

    @Override
    public String getItem(int position){
        return lstPrintBillData.get(position).getShopName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvShopName, tvShopDesc, tvAddress, tvPhone, tvDateTime,tvPrintNo, tvSlipID,
                tvTable, tvUser, tvSubTotal, tvSubTotalAmt, tvServiceCharges, tvServiceChargesAmt, tvCommercialTax,
                tvCommercialTaxAmt, tvDiscount, tvDiscountAmt, tvGrandTotal, tvGrandTotalAmt,
                tvPaid, tvPaidAmount, tvChange, tvChangeAmount, tvMessage, tvOtherMessage,
                tvHeaderItem,tvHeaderQty,tvHeaderPrice,tvHeaderAmount,tvStartTime,tvEndTime,tvOrderNumber,tvLabelOrderNumber;
        //ListView lvPrintList;
        ImageView imgLogo;
        LinearLayout layoutPrintList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_print_bill_parent, parent,false);
            holder = new ViewHolder();
            holder.imgLogo =(ImageView)row.findViewById(R.id.imgLogo);
            holder.tvShopName = (TextView) row.findViewById(R.id.tvShopName);
            holder.tvShopDesc = (TextView) row.findViewById(R.id.tvShopDesc);
            holder.tvAddress = (TextView) row.findViewById(R.id.tvAddress);
            holder.tvPhone = (TextView) row.findViewById(R.id.tvPhone);
            holder.tvPrintNo = (TextView) row.findViewById(R.id.tvPrintNo);
            holder.tvDateTime = (TextView) row.findViewById(R.id.tvDateTime);
            holder.tvSlipID = (TextView) row.findViewById(R.id.tvSlipID);
            holder.tvTable = (TextView) row.findViewById(R.id.tvTable);
            holder.tvUser = (TextView) row.findViewById(R.id.tvUser);
            holder.tvSubTotal = (TextView) row.findViewById(R.id.tvSubTotal);
            //holder.lvPrintList = (ListView) row.findViewById(R.id.lvPrintList);
            holder.tvSubTotalAmt = (TextView) row.findViewById(R.id.tvSubTotalAmt);
            holder.tvServiceCharges = (TextView) row.findViewById(R.id.tvServiceCharges);
            holder.tvServiceChargesAmt = (TextView) row.findViewById(R.id.tvServiceChargesAmt);
            holder.tvCommercialTax = (TextView) row.findViewById(R.id.tvCommercialTax);
            holder.tvCommercialTaxAmt = (TextView) row.findViewById(R.id.tvCommercialTaxAmt);
            holder.tvDiscount = (TextView) row.findViewById(R.id.tvDiscount);
            holder.tvDiscountAmt = (TextView) row.findViewById(R.id.tvDiscountAmt);
            holder.tvGrandTotal = (TextView) row.findViewById(R.id.tvGrandTotal);
            holder.tvGrandTotalAmt = (TextView) row.findViewById(R.id.tvGrandTotalAmt);
            holder.tvPaid = (TextView) row.findViewById(R.id.tvPaid);
            holder.tvPaidAmount = (TextView) row.findViewById(R.id.tvPaidAmt);
            holder.tvChange = (TextView) row.findViewById(R.id.tvChange);
            holder.tvChangeAmount = (TextView) row.findViewById(R.id.tvChangeAmt);
            holder.tvMessage = (TextView) row.findViewById(R.id.tvMessage);
            holder.tvOtherMessage = (TextView) row.findViewById(R.id.tvOtherMessage);
            holder.tvHeaderItem = (TextView) row.findViewById(R.id.tvHeaderItem);
            holder.tvHeaderQty = (TextView) row.findViewById(R.id.tvHeaderQty);
            holder.tvHeaderPrice = (TextView) row.findViewById(R.id.tvHeaderPrice);
            holder.tvHeaderAmount = (TextView) row.findViewById(R.id.tvHeaderAmount);
            holder.layoutPrintList = (LinearLayout) row.findViewById(R.id.layoutPrintList);
            holder.tvStartTime = (TextView) row.findViewById(R.id.tvStartTime);
            holder.tvEndTime = (TextView) row.findViewById(R.id.tvEndTime);
            holder.tvOrderNumber = (TextView) row.findViewById(R.id.tvOrderNumber);
            holder.tvLabelOrderNumber = (TextView) row.findViewById(R.id.tvLabelOrderNumber);

            if(tabletSize==10){
                holder.tvShopName.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_title_10));
                holder.tvShopDesc.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvAddress.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvPhone.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvSlipID.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvDateTime.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvTable.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvUser.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvHeaderItem.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvHeaderQty.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvHeaderPrice.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvHeaderAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvSubTotal.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvSubTotalAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvCommercialTax.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvCommercialTaxAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvServiceCharges.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvServiceChargesAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvDiscount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvDiscountAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvPaid.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvPaidAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvChange.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvChangeAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvMessage.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvOtherMessage.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvGrandTotal.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_grand_amount_10));
                holder.tvGrandTotalAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_grand_amount_10));
                holder.tvStartTime.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvEndTime.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvOrderNumber.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvLabelOrderNumber.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            }else{
                holder.tvShopName.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_title_8));
                holder.tvShopDesc.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvAddress.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvPhone.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvSlipID.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvDateTime.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvTable.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvUser.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvHeaderItem.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvHeaderQty.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvHeaderPrice.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvHeaderAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvSubTotal.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvSubTotalAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvCommercialTax.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvCommercialTaxAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvServiceCharges.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvServiceChargesAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvDiscount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvDiscountAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvPaid.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvPaidAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvChange.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvChangeAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvMessage.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvOtherMessage.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvGrandTotal.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_grand_amount_8));
                holder.tvGrandTotalAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_grand_amount_8));
                holder.tvStartTime.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvEndTime.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvOrderNumber.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvLabelOrderNumber.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            }

                if(db.getFeatureResult(FeatureList.fPaperConstrict)==1 ) {
                    holder.tvShopName.setPadding(2, 2, 2, 2);
                    holder.tvShopDesc.setPadding(2, 2, 2, 2);
                    holder.tvAddress.setPadding(2, 2, 2, 2);
                    holder.tvPhone.setPadding(2, 2, 2, 2);
                    holder.tvSlipID.setPadding(2, 2, 2, 2);
                    holder.tvDateTime.setPadding(2, 2, 2, 2);
                    holder.tvTable.setPadding(2, 2, 2, 2);
                    holder.tvUser.setPadding(2, 2, 2, 2);
                    holder.tvHeaderItem.setPadding(2, 2, 2, 2);
                    holder.tvHeaderQty.setPadding(2, 2, 2, 2);
                    holder.tvHeaderPrice.setPadding(2, 2, 2, 2);
                    holder.tvHeaderAmount.setPadding(2, 2, 2, 2);
                    holder.tvSubTotal.setPadding(2, 2, 2, 2);
                    holder.tvSubTotalAmt.setPadding(2, 2, 2, 2);
                    holder.tvCommercialTax.setPadding(2, 2, 2, 2);
                    holder.tvCommercialTaxAmt.setPadding(2, 2, 2, 2);
                    holder.tvServiceCharges.setPadding(2, 2, 2, 2);
                    holder.tvServiceChargesAmt.setPadding(2, 2, 2, 2);
                    holder.tvDiscount.setPadding(2, 2, 2, 2);
                    holder.tvDiscountAmt.setPadding(2, 2, 2, 2);
                    holder.tvPaid.setPadding(2, 2, 2, 2);
                    holder.tvPaidAmount.setPadding(2, 2, 2, 2);
                    holder.tvChange.setPadding(2, 2, 2, 2);
                    holder.tvChangeAmount.setPadding(2, 2, 2, 2);
                    holder.tvMessage.setPadding(2, 2, 2, 2);
                    holder.tvOtherMessage.setPadding(2, 2, 2, 2);
                    holder.tvGrandTotal.setPadding(2, 2, 2, 2);
                    holder.tvGrandTotalAmt.setPadding(2, 2, 2, 2);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 10, 0, 0);
                    holder.tvMessage.setLayoutParams(params);
                }

            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        try {
            File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File f=new File(directory, "shoplogo.png");
            if(f.exists()) {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                holder.imgLogo.setImageBitmap(b);
                holder.imgLogo.setVisibility(View.VISIBLE);
            }else{
                holder.imgLogo.setVisibility(View.GONE);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if(lstPrintBillData.get(position).getShopName()!=null) {
            if (lstPrintBillData.get(position).getShopName().trim().length() == 0)
                holder.tvShopName.setVisibility(View.GONE);
            else holder.tvShopName.setText(lstPrintBillData.get(position).getShopName());
        }else{
            holder.tvShopName.setVisibility(View.GONE);
        }

        if(lstPrintBillData.get(position).getShopDesp()!=null) {
            if (lstPrintBillData.get(position).getShopDesp().trim().length() == 0)
                holder.tvShopDesc.setVisibility(View.GONE);
            else holder.tvShopDesc.setText(lstPrintBillData.get(position).getShopDesp());
        }else{
            holder.tvShopDesc.setVisibility(View.GONE);
        }

        if(lstPrintBillData.get(position).getAddress()!=null) {
            if (lstPrintBillData.get(position).getAddress().trim().length() == 0)
                holder.tvAddress.setVisibility(View.GONE);
            else holder.tvAddress.setText(lstPrintBillData.get(position).getAddress());
        }else{
            holder.tvAddress.setVisibility(View.GONE);
        }

        if(lstPrintBillData.get(position).getPhone()!=null) {
            if (lstPrintBillData.get(position).getPhone().trim().length() == 0)
                holder.tvPhone.setVisibility(View.GONE);
            else holder.tvPhone.setText(lstPrintBillData.get(position).getPhone());
        }else{
            holder.tvPhone.setVisibility(View.GONE);
        }

        if(lstPrintBillData.get(position).getOrderNumber()!=null) {
            if (lstPrintBillData.get(position).getOrderNumber().trim().length() == 0) {
                holder.tvOrderNumber.setVisibility(View.GONE);
                holder.tvLabelOrderNumber.setVisibility(View.GONE);
            } else {
                holder.tvOrderNumber.setText("# "+lstPrintBillData.get(position).getOrderNumber());
            }
        }else{
            holder.tvLabelOrderNumber.setVisibility(View.GONE);
            holder.tvOrderNumber.setVisibility(View.GONE);
        }

        if(db.getFeatureResult(FeatureList.fStartEndTime)==1){
            holder.tvStartTime.setVisibility(View.VISIBLE);
            holder.tvEndTime.setVisibility(View.VISIBLE);

            if(lstPrintBillData.get(position).getStartTime()!=null) {
                if (lstPrintBillData.get(position).getStartTime().trim().length() != 0)
                    holder.tvStartTime.setText("Start Time - "+lstPrintBillData.get(position).getStartTime());
            }

            if(lstPrintBillData.get(position).getEndTime()!=null) {
                if (lstPrintBillData.get(position).getEndTime().trim().length() != 0)
                    holder.tvEndTime.setText("End Time - "+lstPrintBillData.get(position).getEndTime());
            }
        }else{
            holder.tvStartTime.setVisibility(View.GONE);
            holder.tvEndTime.setVisibility(View.GONE);
        }

        if(lstPrintBillData.get(position).getMessage1()!=null) {
            if (lstPrintBillData.get(position).getMessage1().trim().length() == 0)
                holder.tvMessage.setVisibility(View.GONE);
            else holder.tvMessage.setText(lstPrintBillData.get(position).getMessage1());
        }else{
            holder.tvMessage.setVisibility(View.GONE);
        }

        if(lstPrintBillData.get(position).getMessage2()!=null) {
            if (lstPrintBillData.get(position).getMessage2().trim().length() == 0)
                holder.tvOtherMessage.setVisibility(View.GONE);
            else holder.tvOtherMessage.setText(lstPrintBillData.get(position).getMessage2());
        }else{
            holder.tvOtherMessage.setVisibility(View.GONE);
        }

        holder.tvSlipID.setText(lstPrintBillData.get(position).getSlipNo());
        holder.tvDateTime.setText(lstPrintBillData.get(position).getPrintDate());
        holder.tvTable.setText(lstPrintBillData.get(position).getTable());
        holder.tvUser.setText(lstPrintBillData.get(position).getUser());

        int number=position+1;
        holder.tvPrintNo.setText("Page : "+number);

        if(lstPrintBillData.size()-1 == position) {
            holder.tvSubTotalAmt.setText(String.valueOf(new DecimalFormat("#,###").format(lstPrintBillData.get(position).getSubTotal())));
            holder.tvCommercialTaxAmt.setText(String.valueOf(new DecimalFormat("#,###").format(lstPrintBillData.get(position).getTax())));
            holder.tvServiceChargesAmt.setText(String.valueOf(new DecimalFormat("#,###").format(lstPrintBillData.get(position).getCharges())));
            holder.tvDiscountAmt.setText(String.valueOf(new DecimalFormat("#,###").format(lstPrintBillData.get(position).getDiscount())));
            holder.tvGrandTotalAmt.setText(String.valueOf(new DecimalFormat("#,###").format(lstPrintBillData.get(position).getNetAmount())));
            holder.tvPaidAmount.setText(String.valueOf(new DecimalFormat("#,###").format(lstPrintBillData.get(position).getPaidAmount())));
            holder.tvChangeAmount.setText(String.valueOf(new DecimalFormat("#,###").format(lstPrintBillData.get(position).getChangeAmount())));
        }else{
            holder.tvSubTotalAmt.setVisibility(View.GONE);
            holder.tvCommercialTaxAmt.setVisibility(View.GONE);
            holder.tvServiceChargesAmt.setVisibility(View.GONE);
            holder.tvDiscountAmt.setVisibility(View.GONE);
            holder.tvGrandTotalAmt.setVisibility(View.GONE);
            holder.tvPaidAmount.setVisibility(View.GONE);
            holder.tvChangeAmount.setVisibility(View.GONE);
            holder.tvSubTotal.setVisibility(View.GONE);
            holder.tvCommercialTax.setVisibility(View.GONE);
            holder.tvServiceCharges.setVisibility(View.GONE);
            holder.tvDiscount.setVisibility(View.GONE);
            holder.tvGrandTotal.setVisibility(View.GONE);
            holder.tvPaid.setVisibility(View.GONE);
            holder.tvChange.setVisibility(View.GONE);
            holder.tvMessage.setVisibility(View.GONE);
            holder.tvOtherMessage.setVisibility(View.GONE);
            holder.tvStartTime.setVisibility(View.GONE);
            holder.tvEndTime.setVisibility(View.GONE);
        }

        /**printBillChildListAdapter =new PrintBillChildListAdapter(context,lstPrintBillData.get(position).getLstTran());

        int totalHeight=0;
        int adapterCount= printBillChildListAdapter.getCount();
        for(int size=0;size<adapterCount;size++){
            View listItem= printBillChildListAdapter.getView(size, null, holder.lvPrintList);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params=holder.lvPrintList.getLayoutParams();
        params.height=totalHeight+(holder.lvPrintList.getDividerHeight()*(adapterCount-1))+30;
        holder.lvPrintList.setLayoutParams(params);

        holder.lvPrintList.setAdapter(printBillChildListAdapter);**/

        for (int i = 0; i< lstPrintBillData.get(position).getLstTran().size(); i++) {
            TransactionData data = lstPrintBillData.get(position).getLstTran().get(i);
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row1 = layoutInflater.inflate(R.layout.list_print, null);
            TextView tvItemName =(TextView) row1.findViewById(R.id.tvPrintListItem);
            TextView tvQuantity =(TextView) row1.findViewById(R.id.tvPrintListQty);
            TextView tvPrice =(TextView) row1.findViewById(R.id.tvPrintListPrice);
            TextView tvAmount =(TextView) row1.findViewById(R.id.tvPrintListAmount);

            if(tabletSize==10) {
                tvItemName.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvQuantity.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvPrice.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            }else{
                tvItemName.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvQuantity.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvPrice.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            }

            tvItemName.setText(data.getItemName());
            float floatQty = Float.parseFloat(data.getStringQty());
            if(floatQty==Math.round(floatQty)){
                tvQuantity.setText(String.valueOf(data.getIntegerQty()));
            }else{
                tvQuantity.setText(String.valueOf(data.getFloatQty()));
            }
            String price=new DecimalFormat("#,###").format(data.getSalePrice());
            tvPrice.setText(price);
            String amount=new DecimalFormat("#,###").format(data.getAmount());
            tvAmount.setText(amount);
            holder.layoutPrintList.addView(row1);
        }

        return row;
    }

}
