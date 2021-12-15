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
 * Created by NweYiAung on 06-02-2017.
 */
public class RpTopBottomSaleMenuListAdapter extends BaseAdapter {

    private Context context;
    private List<RpSummaryItemData> lstRpSummaryItemData;

    public RpTopBottomSaleMenuListAdapter(Context context, List<RpSummaryItemData> lstReportItemSummaryData){
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

    static class ReportTopBottomSaleMenuHolder{
        TextView tvListSubMenu,tvListMainMenu,tvListQty;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
        ReportTopBottomSaleMenuHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_rp_top_bottom_salemenu, null);

            holder=new ReportTopBottomSaleMenuHolder();
            holder.tvListSubMenu=(TextView)vi.findViewById(R.id.tvListSubMenu);
            holder.tvListMainMenu=(TextView)vi.findViewById(R.id.tvListMainMenu);
            holder.tvListQty=(TextView)vi.findViewById(R.id.tvListQty);

            vi.setTag(holder);
        }else{
            holder=(ReportTopBottomSaleMenuHolder)vi.getTag();
        }
        holder.tvListSubMenu.setText(lstRpSummaryItemData.get(position).getSubMenuName());
        holder.tvListMainMenu.setText(lstRpSummaryItemData.get(position).getMainMenuName());
        holder.tvListQty.setText(String.valueOf(lstRpSummaryItemData.get(position).getQty()));

        return vi;
    }
}
