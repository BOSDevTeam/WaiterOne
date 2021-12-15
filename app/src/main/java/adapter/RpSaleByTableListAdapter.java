package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.RpSaleByTableData;

/**
 * Created by NweYiAung on 30-12-2016.
 */
public class RpSaleByTableListAdapter extends BaseAdapter {

    private Context context;
    private List<RpSaleByTableData> lstRpSaleByTableData;

    public RpSaleByTableListAdapter(Context context, List<RpSaleByTableData> lstRpSaleByTableData){
        this.context=context;
        this.lstRpSaleByTableData = lstRpSaleByTableData;
    }

    public int getCount(){
        return lstRpSaleByTableData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ReportSaleByTableHolder{
        TextView tvListDate,tvListWaiterName,tvListTableName,tvListTableTypeName, tvListGrandTotal,tvListTotalByTable,tvListTotalByTableType,tvListCustomer;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
       ReportSaleByTableHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_rp_sale_by_table, null);

            holder=new ReportSaleByTableHolder();
            holder.tvListDate=(TextView)vi.findViewById(R.id.tvListDate);
            holder.tvListWaiterName=(TextView)vi.findViewById(R.id.tvListWaiter);
            holder.tvListTableName=(TextView)vi.findViewById(R.id.tvListTableName);
            holder.tvListGrandTotal =(TextView)vi.findViewById(R.id.tvListGrandTotal);
            holder.tvListTotalByTable=(TextView)vi.findViewById(R.id.tvListTotalByTable);
            holder.tvListTableTypeName=(TextView)vi.findViewById(R.id.tvListTableTypeName);
            holder.tvListTotalByTableType=(TextView)vi.findViewById(R.id.tvListTotalByTableType);
            holder.tvListCustomer=(TextView)vi.findViewById(R.id.tvListCustomer);

            vi.setTag(holder);
        }else{
            holder=(ReportSaleByTableHolder)vi.getTag();
        }

        /**if(lstRpSaleByTableData.get(position).getTotalByTable()==0.0) holder.tvListTotalByTable.setText("");
        else holder.tvListTotalByTable.setText("Total: "+String.valueOf(lstRpSaleByTableData.get(position).getTotalByTable()));
        if(lstRpSaleByTableData.get(position).getTotalByTableType()==0.0) holder.tvListTotalByTableType.setText("");
        else holder.tvListTotalByTableType.setText("Total: "+String.valueOf(lstRpSaleByTableData.get(position).getTotalByTableType()));**/

        String isnull= lstRpSaleByTableData.get(position).getTableTypeName();
        if(isnull==null) {
            holder.tvListTableTypeName.setVisibility(View.GONE);
            holder.tvListTotalByTableType.setVisibility(View.GONE);
        }
        else {
            holder.tvListTableTypeName.setVisibility(View.VISIBLE);
            holder.tvListTotalByTableType.setVisibility(View.VISIBLE);
            holder.tvListTableTypeName.setText(String.valueOf(lstRpSaleByTableData.get(position).getTableTypeName()));
            holder.tvListTotalByTableType.setText("Total: "+String.valueOf(lstRpSaleByTableData.get(position).getTotalByTableType()));
        }

        isnull= lstRpSaleByTableData.get(position).getTableName();
        if(isnull==null) {
            holder.tvListTableName.setVisibility(View.GONE);
            holder.tvListTotalByTable.setVisibility(View.GONE);
        }
        else {
            holder.tvListTableName.setVisibility(View.VISIBLE);
            holder.tvListTotalByTable.setVisibility(View.VISIBLE);
            holder.tvListTableName.setText(String.valueOf(lstRpSaleByTableData.get(position).getTableName()));
            holder.tvListTotalByTable.setText("Total: "+String.valueOf(lstRpSaleByTableData.get(position).getTotalByTable()));
        }

        holder.tvListWaiterName.setText(lstRpSaleByTableData.get(position).getWaiterName());
        holder.tvListDate.setText(lstRpSaleByTableData.get(position).getDate());
        holder.tvListCustomer.setText(String.valueOf(lstRpSaleByTableData.get(position).getTotalCustomer()));
        holder.tvListGrandTotal.setText(String.valueOf(lstRpSaleByTableData.get(position).getGrandTotal()));

        return vi;
    }
}
