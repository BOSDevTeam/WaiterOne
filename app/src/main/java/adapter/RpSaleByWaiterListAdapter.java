package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.RpSaleByWaiterData;

/**
 * Created by NweYiAung on 27-12-2016.
 */
public class RpSaleByWaiterListAdapter extends BaseAdapter {

    private Context context;
    private List<RpSaleByWaiterData> lstRpSaleByWaiterData;

    public RpSaleByWaiterListAdapter(Context context, List<RpSaleByWaiterData> lstRpSaleByWaiterData){
        this.context=context;
        this.lstRpSaleByWaiterData = lstRpSaleByWaiterData;
    }

    public int getCount(){
        return lstRpSaleByWaiterData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ReportSaleByWaiterHolder{
        TextView tvListDate,tvListWaiterName,tvListTable, tvListGrandTotal,tvListTotal,tvListCustomer,tvListTotalSaleTable;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
        ReportSaleByWaiterHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_rp_sale_by_waiter, null);

            holder=new ReportSaleByWaiterHolder();
            holder.tvListDate=(TextView)vi.findViewById(R.id.tvListDate);
            holder.tvListWaiterName=(TextView)vi.findViewById(R.id.tvListWaiterName);
            holder.tvListTable=(TextView)vi.findViewById(R.id.tvListTable);
            holder.tvListGrandTotal =(TextView)vi.findViewById(R.id.tvListGrandTotal);
            holder.tvListTotal=(TextView)vi.findViewById(R.id.tvListTotal);
            holder.tvListCustomer=(TextView)vi.findViewById(R.id.tvListCustomer);
            holder.tvListTotalSaleTable=(TextView)vi.findViewById(R.id.tvListTotalSaleTable);

            vi.setTag(holder);
        }else{
            holder=(ReportSaleByWaiterHolder)vi.getTag();
        }

        String isnull= lstRpSaleByWaiterData.get(position).getWaiterName();
        if(isnull==null){
            holder.tvListWaiterName.setVisibility(View.GONE);
            holder.tvListTotal.setVisibility(View.GONE);
            holder.tvListTotalSaleTable.setVisibility(View.GONE);
        }
        else{
            holder.tvListWaiterName.setVisibility(View.VISIBLE);
            holder.tvListTotal.setVisibility(View.VISIBLE);
            holder.tvListTotalSaleTable.setVisibility(View.VISIBLE);
            holder.tvListWaiterName.setText(lstRpSaleByWaiterData.get(position).getWaiterName());
            holder.tvListTotalSaleTable.setText("Total Sale Table: "+String.valueOf(lstRpSaleByWaiterData.get(position).getTotalSaleTable()));
            holder.tvListTotal.setText("Total Amount: "+String.valueOf(lstRpSaleByWaiterData.get(position).getTotal()));
        }
        /**holder.tvListWaiterName.setText(lstRpSaleByWaiterData.get(position).getWaiterName());
        if(lstRpSaleByWaiterData.get(position).getTotal()==0.0) {
            holder.tvListTotal.setVisibility(View.GONE);
        }
        else {
            holder.tvListTotal.setVisibility(View.VISIBLE);
            holder.tvListTotal.setText("Total Amount: "+String.valueOf(lstRpSaleByWaiterData.get(position).getTotal()));
        }
        if(lstRpSaleByWaiterData.get(position).getTotalSaleTable()==0) {
            holder.tvListTotalSaleTable.setVisibility(View.GONE);
        }
        else {
            holder.tvListTotalSaleTable.setVisibility(View.VISIBLE);
            holder.tvListTotalSaleTable.setText("Total Sale Table: "+String.valueOf(lstRpSaleByWaiterData.get(position).getTotalSaleTable()));
        }**/
        holder.tvListDate.setText(lstRpSaleByWaiterData.get(position).getDate());
        holder.tvListTable.setText(String.valueOf(lstRpSaleByWaiterData.get(position).getTableName()));
        holder.tvListCustomer.setText(String.valueOf(lstRpSaleByWaiterData.get(position).getTotalCustomer()));
        holder.tvListGrandTotal.setText(String.valueOf(lstRpSaleByWaiterData.get(position).getGrandTotal()));

        return vi;
    }

}
