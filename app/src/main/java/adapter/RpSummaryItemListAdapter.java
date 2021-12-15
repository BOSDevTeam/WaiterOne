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
public class RpSummaryItemListAdapter extends BaseAdapter {

    private Context context;
    private List<RpSummaryItemData> lstRpSummaryItemData;

    public RpSummaryItemListAdapter(Context context, List<RpSummaryItemData> lstReportItemSummaryData){
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

    static class ReportSummaryItemHolder{
        TextView tvListItem,tvListQty,tvListPrice,tvListAmount,tvListMainMenuName,tvListTotalByMainMenu,tvListTotalBySubMenu,tvListSubMenuName;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
        ReportSummaryItemHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_rp_summary_item, null);

            holder=new ReportSummaryItemHolder();
            holder.tvListItem=(TextView)vi.findViewById(R.id.tvListItem);
            holder.tvListQty=(TextView)vi.findViewById(R.id.tvListQty);
            holder.tvListPrice=(TextView)vi.findViewById(R.id.tvListPrice);
            holder.tvListAmount=(TextView)vi.findViewById(R.id.tvListAmount);
            holder.tvListMainMenuName=(TextView)vi.findViewById(R.id.tvListMainMenuName);
            holder.tvListTotalByMainMenu=(TextView)vi.findViewById(R.id.tvListTotalByMainMenu);
            holder.tvListTotalBySubMenu=(TextView)vi.findViewById(R.id.tvListTotalBySubMenu);
            holder.tvListSubMenuName=(TextView)vi.findViewById(R.id.tvListSubMenuName);

            vi.setTag(holder);
        }else{
            holder=(ReportSummaryItemHolder)vi.getTag();
        }

        /**if(lstRpSummaryItemData.get(position).getTotalBySubMenu()==0.0) holder.tvListTotalBySubMenu.setText("");
        else holder.tvListTotalBySubMenu.setText("Total: "+String.valueOf(lstRpSummaryItemData.get(position).getTotalBySubMenu()));
        if(lstRpSummaryItemData.get(position).getTotalByMainMenu()==0.0) holder.tvListTotalByMainMenu.setText("");
        else holder.tvListTotalByMainMenu.setText("Total: "+String.valueOf(lstRpSummaryItemData.get(position).getTotalByMainMenu()));

        String isnull= lstRpSummaryItemData.get(position).getMainMenuName();
        if(isnull==null) holder.tvListMainMenuName.setText("");
        else holder.tvListMainMenuName.setText(String.valueOf(lstRpSummaryItemData.get(position).getMainMenuName()));

        isnull= lstRpSummaryItemData.get(position).getSubMenuName();
        if(isnull==null) holder.tvListSubMenuName.setText("");
        else holder.tvListSubMenuName.setText(String.valueOf(lstRpSummaryItemData.get(position).getSubMenuName()));**/

        String isnull= lstRpSummaryItemData.get(position).getMainMenuName();
        if(isnull==null) {
            holder.tvListMainMenuName.setVisibility(View.GONE);
            holder.tvListTotalByMainMenu.setVisibility(View.GONE);
        }
        else {
            holder.tvListMainMenuName.setVisibility(View.VISIBLE);
            holder.tvListTotalByMainMenu.setVisibility(View.VISIBLE);
            holder.tvListMainMenuName.setText(String.valueOf(lstRpSummaryItemData.get(position).getMainMenuName()));
            holder.tvListTotalByMainMenu.setText("Total: "+String.valueOf(lstRpSummaryItemData.get(position).getTotalByMainMenu()));
        }

        isnull= lstRpSummaryItemData.get(position).getSubMenuName();
        if(isnull==null) {
            holder.tvListSubMenuName.setVisibility(View.GONE);
            holder.tvListTotalBySubMenu.setVisibility(View.GONE);
        }
        else {
            holder.tvListSubMenuName.setVisibility(View.VISIBLE);
            holder.tvListTotalBySubMenu.setVisibility(View.VISIBLE);
            holder.tvListSubMenuName.setText(String.valueOf(lstRpSummaryItemData.get(position).getSubMenuName()));
            holder.tvListTotalBySubMenu.setText("Total: "+String.valueOf(lstRpSummaryItemData.get(position).getTotalBySubMenu()));
        }

        holder.tvListItem.setText(lstRpSummaryItemData.get(position).getName());
        holder.tvListQty.setText(String.valueOf(lstRpSummaryItemData.get(position).getQty()));
        holder.tvListPrice.setText(String.valueOf(lstRpSummaryItemData.get(position).getPrice()));
        holder.tvListAmount.setText(String.valueOf(lstRpSummaryItemData.get(position).getAmount()));

        return vi;
    }
}
