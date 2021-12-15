package adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.text.DecimalFormat;
import java.util.List;

import data.TransactionData;

/**
 * Created by User on 7/30/2018.
 */
public class PrintBillChildListAdapter extends BaseAdapter {
    private Context context;
    List<TransactionData> lstTranData;
    float floatQty;
    int tabletSize;

    public PrintBillChildListAdapter(Context context, List<TransactionData> lstTranData){

        Configuration config=context.getResources().getConfiguration();
        if(config.smallestScreenWidthDp<=600){
            tabletSize=8;
        }else{
            tabletSize=10;
        }
        this.context=context;
        this.lstTranData=lstTranData;
    }

    @Override
    public int getCount(){
        return lstTranData.size();
    }

    @Override
    public String getItem(int position){
        return lstTranData.get(position).getItemName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvPrintListItem, tvPrintListQty, tvPrintListPrice, tvPrintListAmount;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_print_bill_child, parent,false);
            holder = new ViewHolder();
            holder.tvPrintListItem = (TextView) row.findViewById(R.id.tvPrintListItem);
            holder.tvPrintListQty = (TextView) row.findViewById(R.id.tvPrintListQty);
            holder.tvPrintListPrice = (TextView) row.findViewById(R.id.tvPrintListPrice);
            holder.tvPrintListAmount = (TextView) row.findViewById(R.id.tvPrintListAmount);

            if(tabletSize==10) {
                holder.tvPrintListItem.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvPrintListQty.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvPrintListPrice.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvPrintListAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            }else{
                holder.tvPrintListItem.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvPrintListQty.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvPrintListPrice.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvPrintListAmount.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            }


            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        holder.tvPrintListItem.setText(lstTranData.get(position).getItemName());
        floatQty = Float.parseFloat(lstTranData.get(position).getStringQty());
        if(floatQty==Math.round(floatQty)){
            holder.tvPrintListQty.setText(String.valueOf(lstTranData.get(position).getIntegerQty()));
        }else{
            holder.tvPrintListQty.setText(String.valueOf(lstTranData.get(position).getFloatQty()));
        }
        String price= String.valueOf(new DecimalFormat("#,###").format(Math.round(lstTranData.get(position).getSalePrice())));
        holder.tvPrintListPrice.setText(price);
        String amount=String.valueOf(new DecimalFormat("#,###").format(Math.round(lstTranData.get(position).getAmount())));
        holder.tvPrintListAmount.setText(amount);

        return row;
    }
}
