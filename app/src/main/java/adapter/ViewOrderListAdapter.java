package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.TransactionData;
import listener.ViewOrderListButtonClickListener;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class ViewOrderListAdapter extends BaseAdapter {

    private Context context;
    List<TransactionData> lstTransactionData;
    float floatQty;
    ViewOrderListButtonClickListener viewOrderListButtonClickListener;

    public ViewOrderListAdapter(Context context,List<TransactionData> lstTransactionData){
        this.context=context;
        this.lstTransactionData=lstTransactionData;
    }

    public void setOnItemDeletedClickListener(ViewOrderListButtonClickListener viewOrderListButtonClickListener){
        this.viewOrderListButtonClickListener = viewOrderListButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstTransactionData.size();
    }

    @Override
    public String getItem(int position){
        return lstTransactionData.get(position).getItemName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvItemDescription,tvItemQuantity,tvItemAmount;
        ImageButton btnItemDelete;
    }

    @Override
    public View getView(final int position,View convertView,ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_view_order, parent,false);
            holder=new ViewHolder();
            holder.tvItemDescription=(TextView) row.findViewById(R.id.tvItemDescription);
            holder.tvItemQuantity=(TextView) row.findViewById(R.id.tvItemQuantity);
            holder.tvItemAmount=(TextView) row.findViewById(R.id.tvItemAmount);
            holder.btnItemDelete=(ImageButton) row.findViewById(R.id.btnItemDelete);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.btnItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewOrderListButtonClickListener !=null){
                    viewOrderListButtonClickListener.onItemDeletedClickListener(position);
                }
            }
        });

        holder.tvItemDescription.setText(lstTransactionData.get(position).getItemName());
        floatQty = Float.parseFloat(lstTransactionData.get(position).getStringQty());
        if(floatQty==Math.round(floatQty)){
            holder.tvItemQuantity.setText(String.valueOf(lstTransactionData.get(position).getIntegerQty()));
        }else{
            holder.tvItemQuantity.setText(String.valueOf(lstTransactionData.get(position).getFloatQty()));
        }
        holder.tvItemAmount.setText(String.valueOf(lstTransactionData.get(position).getAmount()));

        return row;
    }
}
