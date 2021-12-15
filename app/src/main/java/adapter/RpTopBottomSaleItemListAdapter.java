package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.RpSummaryItemData;

/**
 * Created by NweYiAung on 26-12-2016.
 */
public class RpTopBottomSaleItemListAdapter extends BaseAdapter {

    private Context context;
    private List<RpSummaryItemData> lstRpSummaryItemData;

    public RpTopBottomSaleItemListAdapter(Context context, List<RpSummaryItemData> lstReportItemSummaryData){
        this.context=context;
        this.lstRpSummaryItemData =lstReportItemSummaryData;
    }

    public int getCount(){
        return lstRpSummaryItemData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ReportTopBottomSaleItemHolder{
        TextView tvListItem,tvListMenu,tvListQty;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
        ReportTopBottomSaleItemHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_rp_top_bottom_saleitem, null);

            holder=new ReportTopBottomSaleItemHolder();
            holder.tvListItem=(TextView)vi.findViewById(R.id.tvListItem);
            holder.tvListMenu=(TextView)vi.findViewById(R.id.tvListMenu);
            holder.tvListQty=(TextView)vi.findViewById(R.id.tvListQty);

            vi.setTag(holder);
        }else{
            holder=(ReportTopBottomSaleItemHolder)vi.getTag();
        }
        holder.tvListItem.setText(lstRpSummaryItemData.get(position).getName());
        holder.tvListMenu.setText(lstRpSummaryItemData.get(position).getMenu());
        holder.tvListQty.setText(String.valueOf(lstRpSummaryItemData.get(position).getQty()));

        return vi;
    }
}
