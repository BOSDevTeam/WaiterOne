package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.TransactionData;
import listener.DeleteTranListBtnListener;

public class TranSaleListAdapter extends BaseAdapter {

    private Context context;
    DeleteTranListBtnListener deleteTranListBtnListener;
    List<TransactionData> lstTransactionData;

    public TranSaleListAdapter(Context context, List<TransactionData> lstTransactionData){
        this.context=context;
        this.lstTransactionData=lstTransactionData;
    }

    public void setOnButtonClickListener(DeleteTranListBtnListener deleteTranListBtnListener){
        this.deleteTranListBtnListener =deleteTranListBtnListener;
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
        TextView tvItem,tvQty,tvPrice,tvAmount;
        ImageButton btnDelete;
        View row;
    }

    @Override
    public View getView(final int position,View convertView,ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_tran_sale, parent,false);
            holder=new ViewHolder();
            holder.tvItem=(TextView) row.findViewById(R.id.tvItem);
            holder.tvQty=(TextView) row.findViewById(R.id.tvQty);
            holder.tvPrice=(TextView) row.findViewById(R.id.tvPrice);
            holder.tvAmount=(TextView) row.findViewById(R.id.tvAmount);
            holder.btnDelete=(ImageButton) row.findViewById(R.id.btnDelete);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvItem.setText(lstTransactionData.get(position).getItemName());
        holder.tvQty.setText(lstTransactionData.get(position).getStringQty());
        holder.tvPrice.setText(String.valueOf(lstTransactionData.get(position).getSalePrice()));
        holder.tvAmount.setText(String.valueOf(lstTransactionData.get(position).getAmount()));

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(deleteTranListBtnListener !=null){
                    deleteTranListBtnListener.onDeleteClickListener(position);
                }
            }
        });

        return row;
    }
}
