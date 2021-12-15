package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.TransactionData;
import listener.DeleteVouListBtnListener;

public class MasterSaleListAdapter extends BaseAdapter {

    private Context context;
    DeleteVouListBtnListener deleteVouListBtnListener;
    List<TransactionData> lstTransactionData;

    public MasterSaleListAdapter(Context context,List<TransactionData> lstTransactionData){
        this.context=context;
        this.lstTransactionData=lstTransactionData;
    }

    public void setOnButtonClickListener(DeleteVouListBtnListener deleteVouListBtnListener){
        this.deleteVouListBtnListener =deleteVouListBtnListener;
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
        TextView tvVouNo,tvDate,tvWaiter,tvTable;
        ImageButton btnDelete;
        View row;
        Button btnDetail;
    }

    @Override
    public View getView(final int position,View convertView,ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_master_sale, parent,false);
            holder=new ViewHolder();
            holder.tvVouNo=(TextView) row.findViewById(R.id.tvVouNo);
            holder.tvDate=(TextView) row.findViewById(R.id.tvDate);
            holder.tvWaiter=(TextView) row.findViewById(R.id.tvWaiter);
            holder.tvTable=(TextView) row.findViewById(R.id.tvTable);
            holder.btnDelete=(ImageButton) row.findViewById(R.id.btnDelete);
            holder.btnDetail=(Button)row.findViewById(R.id.btnDetail);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvVouNo.setText(lstTransactionData.get(position).getVouno());
        holder.tvDate.setText(lstTransactionData.get(position).getDate());
        holder.tvWaiter.setText(lstTransactionData.get(position).getWaiterName());
        holder.tvTable.setText(lstTransactionData.get(position).getTableName());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(deleteVouListBtnListener !=null){
                    deleteVouListBtnListener.onDeleteClickListener(position);
                }
            }
        });

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(deleteVouListBtnListener !=null){
                    deleteVouListBtnListener.onDetailClickListener(position);
                }
            }
        });

        return row;
    }
}
