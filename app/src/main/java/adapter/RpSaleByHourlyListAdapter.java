package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.RpSaleByHourlyData;

/**
 * Created by NweYiAung on 07-02-2017.
 */
public class RpSaleByHourlyListAdapter extends BaseAdapter {

    private Context context;
    private List<RpSaleByHourlyData> lstRpSaleByHourlyData;

    public RpSaleByHourlyListAdapter(Context context, List<RpSaleByHourlyData> lstRpSaleByHourlyData){
        this.context=context;
        this.lstRpSaleByHourlyData = lstRpSaleByHourlyData;
    }

    public int getCount(){
        return lstRpSaleByHourlyData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ReportSaleByHourlyHolder {
        TextView tvListTime, tvListTotalTransaction, tvListTotalItem, tvListTotalCustomer, tvListTotalAmount;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
        ReportSaleByHourlyHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_rp_sale_by_hourly, null);

            holder=new ReportSaleByHourlyHolder();
            holder.tvListTime =(TextView)vi.findViewById(R.id.tvListTime);
            holder.tvListTotalTransaction =(TextView)vi.findViewById(R.id.tvListTotalTransaction);
            holder.tvListTotalItem =(TextView)vi.findViewById(R.id.tvListTotalItem);
            holder.tvListTotalCustomer =(TextView)vi.findViewById(R.id.tvListTotalCustomer);
            holder.tvListTotalAmount =(TextView)vi.findViewById(R.id.tvListTotalAmount);

            vi.setTag(holder);
        }else{
            holder=(ReportSaleByHourlyHolder)vi.getTag();
        }

        holder.tvListTime.setText(lstRpSaleByHourlyData.get(position).getCTime());
        holder.tvListTotalTransaction.setText(String.valueOf(lstRpSaleByHourlyData.get(position).getTotalTransaction()));
        holder.tvListTotalItem.setText(String.valueOf(lstRpSaleByHourlyData.get(position).getTotalItem()));
        holder.tvListTotalCustomer.setText(String.valueOf(lstRpSaleByHourlyData.get(position).getTotalCustomer()));
        holder.tvListTotalAmount.setText(String.valueOf(lstRpSaleByHourlyData.get(position).getTotalAmount()));

        return vi;
    }
}
