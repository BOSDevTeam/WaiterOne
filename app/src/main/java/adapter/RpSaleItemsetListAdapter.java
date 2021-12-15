package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.ItemData;

/**
 * Created by NweYiAung on 10-01-2017.
 */
public class RpSaleItemsetListAdapter extends BaseAdapter {

    private Context context;
    private List<ItemData> lstMenuItemData;

    public RpSaleItemsetListAdapter(Context context, List<ItemData> lstMenuItemData){
        this.context=context;
        this.lstMenuItemData =lstMenuItemData;
    }

    public int getCount(){
        return lstMenuItemData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ReportSaleItemsetHolder {
        TextView tvListItemset, tvPairedEatenTime;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
        ReportSaleItemsetHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_rp_sale_itemset, null);

            holder=new ReportSaleItemsetHolder();
            holder.tvListItemset =(TextView)vi.findViewById(R.id.tvListItemset);
            holder.tvPairedEatenTime =(TextView)vi.findViewById(R.id.tvListPairedEatenTime);

            vi.setTag(holder);
        }else{
            holder=(ReportSaleItemsetHolder)vi.getTag();
        }
        holder.tvListItemset.setText(lstMenuItemData.get(position).getItemSet());
        holder.tvPairedEatenTime.setText(String.valueOf(lstMenuItemData.get(position).getPairedEatenTimes()));

        return vi;
    }
}
