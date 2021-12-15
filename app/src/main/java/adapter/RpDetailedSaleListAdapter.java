package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.RpDetailedSaleData;

/**
 * Created by NweYiAung on 26-12-2016.
 */
public class RpDetailedSaleListAdapter extends BaseAdapter {

    private Context context;
    private List<RpDetailedSaleData> lstRpDetailedSaleData;

    public RpDetailedSaleListAdapter(Context context, List<RpDetailedSaleData> lstRpDetailedSaleData){
        this.context=context;
        this.lstRpDetailedSaleData = lstRpDetailedSaleData;
    }

    public int getCount(){
        return lstRpDetailedSaleData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ReportDetailedSaleHolder{
        TextView tvListDate,tvListWaiter,tvListTable,tvListCustomerNumber,tvListSubTotal,tvListTax,tvListCharges,tvListGrandTotal,tvListDiscount;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
       ReportDetailedSaleHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_rp_detailed_sale, null);

            holder=new ReportDetailedSaleHolder();
            holder.tvListDate=(TextView)vi.findViewById(R.id.tvListDate);
            holder.tvListWaiter=(TextView)vi.findViewById(R.id.tvListWaiter);
            holder.tvListTable=(TextView)vi.findViewById(R.id.tvListTable);
            holder.tvListCustomerNumber=(TextView)vi.findViewById(R.id.tvListCustomerNumber);
            holder.tvListSubTotal=(TextView)vi.findViewById(R.id.tvListSubTotal);
            holder.tvListTax=(TextView)vi.findViewById(R.id.tvListTax);
            holder.tvListCharges=(TextView)vi.findViewById(R.id.tvListCharges);
            holder.tvListGrandTotal=(TextView)vi.findViewById(R.id.tvListGrandTotal);
            holder.tvListDiscount=(TextView)vi.findViewById(R.id.tvListDiscount);

            vi.setTag(holder);
        }else{
            holder=(ReportDetailedSaleHolder)vi.getTag();
        }
        holder.tvListDate.setText(lstRpDetailedSaleData.get(position).getDate());
        holder.tvListWaiter.setText(lstRpDetailedSaleData.get(position).getWaiter());
        holder.tvListTable.setText(String.valueOf(lstRpDetailedSaleData.get(position).getTable()));
        holder.tvListCustomerNumber.setText(String.valueOf(lstRpDetailedSaleData.get(position).getCustomerNumber()));
        holder.tvListSubTotal.setText(String.valueOf(lstRpDetailedSaleData.get(position).getSubtotal()));
        holder.tvListTax.setText(String.valueOf(lstRpDetailedSaleData.get(position).getTax()));
        holder.tvListCharges.setText(String.valueOf(lstRpDetailedSaleData.get(position).getCharges()));
        holder.tvListGrandTotal.setText(String.valueOf(lstRpDetailedSaleData.get(position).getGrandtotal()));
        holder.tvListDiscount.setText(String.valueOf(lstRpDetailedSaleData.get(position).getDiscount()));

        return vi;
    }
}
