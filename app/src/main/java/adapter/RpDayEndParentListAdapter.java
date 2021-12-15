package adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.text.DecimalFormat;
import java.util.List;

import data.RpDayEndData;
import data.TransactionData;

/**
 * Created by User on 5/30/2018.
 */
public class RpDayEndParentListAdapter extends BaseAdapter {
    private Context context;
    List<RpDayEndData> lstRpDayEndData;
    int tabletSize;
    RpDayEndChildListAdapter rpDayEndChildListAdapter;

    public RpDayEndParentListAdapter(Context context, List<RpDayEndData> lstRpDayEndData){
        Configuration config = context.getResources().getConfiguration();
        if (config.smallestScreenWidthDp <= 600) {
            tabletSize = 8;
        } else {
            tabletSize = 10;
        }
        this.context=context;
        this.lstRpDayEndData = lstRpDayEndData;
    }

    @Override
    public int getCount(){
        return lstRpDayEndData.size();
    }

    @Override
    public String getItem(int position){
        return lstRpDayEndData.get(position).getReportName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvReportName,tvShopName,tvShopDesc,tvPrintDate,tvHeaderSlipID,tvHeaderTotalAmt,tvHeaderTax,tvHeaderCharges,tvHeaderDis,tvHeaderNetAmt,
                tvTotalAmt,tvTax,tvCharges,tvDis,tvNetAmt,tvPrintNo,tvSubTotal,tvCommercialTax,tvServiceCharges,tvDiscount,tvGrandTotal;
        //ListView lvPrintList;
        LinearLayout layoutPrintList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_rp_day_end_parent, parent,false);
            holder = new ViewHolder();
            holder.tvReportName = (TextView) row.findViewById(R.id.tvReportName);
            holder.tvShopName = (TextView) row.findViewById(R.id.tvShopName);
            holder.tvShopDesc = (TextView) row.findViewById(R.id.tvShopDesc);
            holder.tvPrintDate = (TextView) row.findViewById(R.id.tvPrintDate);
            holder.tvPrintNo = (TextView) row.findViewById(R.id.tvPrintNo);
            holder.tvHeaderSlipID = (TextView) row.findViewById(R.id.tvHeaderSlipID);
            holder.tvHeaderTotalAmt = (TextView) row.findViewById(R.id.tvHeaderTotalAmt);
            holder.tvHeaderTax = (TextView) row.findViewById(R.id.tvHeaderTax);
            holder.tvHeaderCharges = (TextView) row.findViewById(R.id.tvHeaderCharges);
            holder.tvHeaderDis = (TextView) row.findViewById(R.id.tvHeaderDis);
            holder.tvHeaderNetAmt = (TextView) row.findViewById(R.id.tvHeaderNetAmt);
            //holder.lvPrintList = (ListView) row.findViewById(R.id.lvPrintList);
            holder.tvTotalAmt = (TextView) row.findViewById(R.id.tvTotalAmt);
            holder.tvTax = (TextView) row.findViewById(R.id.tvTax);
            holder.tvCharges = (TextView) row.findViewById(R.id.tvCharges);
            holder.tvDis = (TextView) row.findViewById(R.id.tvDis);
            holder.tvNetAmt = (TextView) row.findViewById(R.id.tvNetAmt);
            holder.tvSubTotal = (TextView) row.findViewById(R.id.tvSubTotal);
            holder.tvCommercialTax = (TextView) row.findViewById(R.id.tvCommercialTax);
            holder.tvServiceCharges = (TextView) row.findViewById(R.id.tvServiceCharges);
            holder.tvDiscount = (TextView) row.findViewById(R.id.tvDiscount);
            holder.tvGrandTotal = (TextView) row.findViewById(R.id.tvGrandTotal);
            holder.layoutPrintList=(LinearLayout)row.findViewById(R.id.layoutPrintList);

            if(tabletSize==10){
                holder.tvReportName.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_title_10));
                holder.tvShopName.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvShopDesc.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvPrintDate.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvHeaderSlipID.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
                holder.tvHeaderTotalAmt.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
                holder.tvHeaderTax.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
                holder.tvHeaderCharges.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
                holder.tvHeaderDis.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
                holder.tvHeaderNetAmt.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_10));
                holder.tvTotalAmt.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvTax.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvCharges.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvDis.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvNetAmt.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvSubTotal.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvCommercialTax.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvServiceCharges.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvDiscount.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvGrandTotal.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            }else{
                holder.tvReportName.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_title_8));
                holder.tvShopName.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvShopDesc.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvPrintDate.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvHeaderSlipID.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
                holder.tvHeaderTotalAmt.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
                holder.tvHeaderTax.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
                holder.tvHeaderCharges.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
                holder.tvHeaderDis.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
                holder.tvHeaderNetAmt.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_header_8));
                holder.tvTotalAmt.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvTax.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvCharges.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvDis.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvNetAmt.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvSubTotal.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvCommercialTax.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvServiceCharges.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvDiscount.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvGrandTotal.setTextSize( context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            }

            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        holder.tvReportName.setText(lstRpDayEndData.get(position).getReportName());
        holder.tvHeaderSlipID.setText(lstRpDayEndData.get(position).getSlipHeader());
        holder.tvHeaderTotalAmt.setText(lstRpDayEndData.get(position).getTotalHeader());
        holder.tvHeaderTax.setText(lstRpDayEndData.get(position).getTaxHeader());
        holder.tvHeaderCharges.setText(lstRpDayEndData.get(position).getChargesHeader());
        holder.tvHeaderDis.setText(lstRpDayEndData.get(position).getDisHeader());
        holder.tvHeaderNetAmt.setText(lstRpDayEndData.get(position).getNetHeader());
        if(lstRpDayEndData.get(position).getShopName()!=null) {
            if (lstRpDayEndData.get(position).getShopName().trim().length() == 0)
                holder.tvShopName.setVisibility(View.GONE);
            else holder.tvShopName.setText(lstRpDayEndData.get(position).getShopName());
        }else{
            holder.tvShopName.setVisibility(View.GONE);
        }
        if(lstRpDayEndData.get(position).getShopDesp()!=null) {
            if (lstRpDayEndData.get(position).getShopDesp().trim().length() == 0)
                holder.tvShopDesc.setVisibility(View.GONE);
            else holder.tvShopDesc.setText(lstRpDayEndData.get(position).getShopDesp());
        }else{
            holder.tvShopDesc.setVisibility(View.GONE);
        }
        holder.tvPrintDate.setText(lstRpDayEndData.get(position).getPrintDate());
        int number=position+1;
        holder.tvPrintNo.setText("Page : "+number);

        if(lstRpDayEndData.size()-1 == position) {
            holder.tvTotalAmt.setText(String.valueOf(new DecimalFormat("#,###").format(lstRpDayEndData.get(position).getTotalAmt())));
            holder.tvTax.setText(String.valueOf(new DecimalFormat("#,###").format(lstRpDayEndData.get(position).getTax())));
            holder.tvCharges.setText(String.valueOf(new DecimalFormat("#,###").format(lstRpDayEndData.get(position).getCharges())));
            holder.tvDis.setText(String.valueOf(new DecimalFormat("#,###").format(lstRpDayEndData.get(position).getDiscount())));
            holder.tvNetAmt.setText(String.valueOf(new DecimalFormat("#,###").format(lstRpDayEndData.get(position).getNetAmt())));
        }else{
            holder.tvTotalAmt.setVisibility(View.GONE);
            holder.tvTax.setVisibility(View.GONE);
            holder.tvCharges.setVisibility(View.GONE);
            holder.tvDis.setVisibility(View.GONE);
            holder.tvNetAmt.setVisibility(View.GONE);
            holder.tvSubTotal.setVisibility(View.GONE);
            holder.tvCommercialTax.setVisibility(View.GONE);
            holder.tvServiceCharges.setVisibility(View.GONE);
            holder.tvDiscount.setVisibility(View.GONE);
            holder.tvGrandTotal.setVisibility(View.GONE);
        }

        /**rpDayEndChildListAdapter =new RpDayEndChildListAdapter(context,lstRpDayEndData.get(position).getLstTran());

        int totalHeight=0;
        int adapterCount= rpDayEndChildListAdapter.getCount();
        for(int size=0;size<adapterCount;size++){
            View listItem= rpDayEndChildListAdapter.getView(size, null, holder.lvPrintList);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params=holder.lvPrintList.getLayoutParams();
        params.height=totalHeight+(holder.lvPrintList.getDividerHeight()*(adapterCount-1))+30;
        holder.lvPrintList.setLayoutParams(params);

        holder.lvPrintList.setAdapter(rpDayEndChildListAdapter);

        return row;**/

            for (int i = 0; i < lstRpDayEndData.get(position).getLstTran().size(); i++) {
                TransactionData data = lstRpDayEndData.get(position).getLstTran().get(i);
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View crow = layoutInflater.inflate(R.layout.list_rp_day_end_child, null);
                TextView tvSlipID = (TextView) crow.findViewById(R.id.tvSlipID);
                TextView tvItemTotalAmt = (TextView) crow.findViewById(R.id.tvTotalAmt);
                TextView tvItemTax = (TextView) crow.findViewById(R.id.tvTax);
                TextView tvItemCharges = (TextView) crow.findViewById(R.id.tvCharges);
                TextView tvItemDis = (TextView) crow.findViewById(R.id.tvDis);
                TextView tvItemNetAmt = (TextView) crow.findViewById(R.id.tvNetAmt);

                if (tabletSize == 10) {
                    tvSlipID.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemTotalAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemTax.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemCharges.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemDis.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                    tvItemNetAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                } else {
                    tvSlipID.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemTotalAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemTax.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemCharges.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemDis.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                    tvItemNetAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
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
                holder.layoutPrintList.addView(crow);
            }
        return row;
    }
}
