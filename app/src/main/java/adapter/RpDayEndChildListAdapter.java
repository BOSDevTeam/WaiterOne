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
 * Created by User on 5/30/2018.
 */
public class RpDayEndChildListAdapter extends BaseAdapter {
    private Context context;
    List<TransactionData> lstTranData;
    int tabletSize;

    public RpDayEndChildListAdapter(Context context, List<TransactionData> lstTranData){
        Configuration config = context.getResources().getConfiguration();
        if (config.smallestScreenWidthDp <= 600) {
            tabletSize = 8;
        } else {
            tabletSize = 10;
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
        TextView tvSlipID,tvItemTotalAmt,tvItemTax,tvItemCharges,tvItemDis,tvItemNetAmt;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_rp_day_end_child, parent,false);
            holder = new ViewHolder();
            holder.tvSlipID = (TextView) row.findViewById(R.id.tvSlipID);
            holder.tvItemTotalAmt = (TextView) row.findViewById(R.id.tvTotalAmt);
            holder.tvItemTax = (TextView) row.findViewById(R.id.tvTax);
            holder.tvItemCharges = (TextView) row.findViewById(R.id.tvCharges);
            holder.tvItemDis = (TextView) row.findViewById(R.id.tvDis);
            holder.tvItemNetAmt = (TextView) row.findViewById(R.id.tvNetAmt);

            if (tabletSize == 10) {
                holder.tvSlipID.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvItemTotalAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvItemTax.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvItemCharges.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvItemDis.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
                holder.tvItemNetAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_10));
            } else {
                holder.tvSlipID.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvItemTotalAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvItemTax.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvItemCharges.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvItemDis.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
                holder.tvItemNetAmt.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.print_label_8));
            }

            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        holder.tvSlipID.setText(String.valueOf(lstTranData.get(position).getSlipid()));
        holder.tvItemTotalAmt.setText(String.valueOf(new DecimalFormat("#").format(lstTranData.get(position).getSubTotal())));
        holder.tvItemTax.setText(String.valueOf(new DecimalFormat("#").format(lstTranData.get(position).getTax())));
        holder.tvItemCharges.setText(String.valueOf(new DecimalFormat("#").format(lstTranData.get(position).getCharges())));
        holder.tvItemDis.setText(String.valueOf(new DecimalFormat("#").format(lstTranData.get(position).getDiscount())));
        holder.tvItemNetAmt.setText(String.valueOf(new DecimalFormat("#").format(lstTranData.get(position).getGrandTotal())));

        return row;
    }
}
