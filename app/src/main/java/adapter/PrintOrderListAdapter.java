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
import java.util.List;

import common.DBHelper;
import data.PrintOrderData;
import data.TransactionData;

public class PrintOrderListAdapter extends BaseAdapter {
    private Context context;
    List<PrintOrderData> lstPrintRealData;
    int tabletSize;
    DBHelper db;

    public PrintOrderListAdapter(Context context, List<PrintOrderData> lstPrintRealData){
        Configuration config=context.getResources().getConfiguration();
        if(config.smallestScreenWidthDp<=600){
            tabletSize=8;
        }else{
            tabletSize=10;
        }

        this.context=context;
        db=new DBHelper(this.context);
        this.lstPrintRealData=lstPrintRealData;
    }

    @Override
    public int getCount(){
        return lstPrintRealData.size();
    }

    @Override
    public String getItem(int position){
        return lstPrintRealData.get(position).getsTypeName();
    }

    @Override
    public long getItemId(int position){
        return lstPrintRealData.get(position).getsTypeId();
    }

    static class ViewHolder {
        TextView tvDate,tvTime, tvTable, tvUser, tvHeaderItem, tvHeaderQty,tvLblTable,tvLblUser;
        LinearLayout layoutPrintList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final PrintOrderListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_print_order_parent, parent,false);
            holder = new PrintOrderListAdapter.ViewHolder();
            holder.tvTime = (TextView) row.findViewById(R.id.tvTime);
            holder.tvDate = (TextView) row.findViewById(R.id.tvDate);
            holder.tvTable = (TextView) row.findViewById(R.id.tvTable);
            holder.tvUser = (TextView) row.findViewById(R.id.tvUser);
            holder.tvLblTable = (TextView) row.findViewById(R.id.tvLblTable);
            holder.tvLblUser = (TextView) row.findViewById(R.id.tvLblUser);
            holder.tvHeaderItem = (TextView) row.findViewById(R.id.tvHeaderItem);
            holder.tvHeaderQty = (TextView) row.findViewById(R.id.tvHeaderQty);
            holder.layoutPrintList = (LinearLayout) row.findViewById(R.id.layoutPrintList);

            if(tabletSize==10){
                holder.tvTime.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvDate.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvTable.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvUser.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvLblTable.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvLblUser.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvHeaderItem.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvHeaderQty.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            }else{
                holder.tvTime.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvDate.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvTable.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvUser.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvLblTable.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvLblUser.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvHeaderItem.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvHeaderQty.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            }

            row.setTag(holder);
        } else {
            row = convertView;
            holder = (PrintOrderListAdapter.ViewHolder) row.getTag();
        }

        holder.tvDate.setText(lstPrintRealData.get(position).getDate());
        holder.tvTime.setText(lstPrintRealData.get(position).getTime());
        holder.tvTable.setText(lstPrintRealData.get(position).getTableName());
        holder.tvUser.setText(lstPrintRealData.get(position).getUserName());

        for (int i=0; i<lstPrintRealData.get(position).getLstTran().size(); i++) {
            TransactionData data = lstPrintRealData.get(position).getLstTran().get(i);
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row1 = layoutInflater.inflate(R.layout.list_print_order_child, null);
            TextView tvItemName =(TextView) row1.findViewById(R.id.tvPrintListItem);
            TextView tvQuantity =(TextView) row1.findViewById(R.id.tvPrintListQty);

            if(tabletSize==10) {
                tvItemName.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                tvQuantity.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            }else{
                tvItemName.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                tvQuantity.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            }

            if(data.getTaste().toString().length()!=0) tvItemName.setText(data.getItemName()+"("+data.getTaste()+")");
            else tvItemName.setText(data.getItemName());
            float floatQty = Float.parseFloat(data.getStringQty());
            if(floatQty==Math.round(floatQty)){
                tvQuantity.setText(String.valueOf(data.getIntegerQty()));
            }else{
                tvQuantity.setText(String.valueOf(data.getFloatQty()));
            }
            holder.layoutPrintList.addView(row1);
        }

        return row;
    }
}
