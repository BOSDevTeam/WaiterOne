package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.RpSaleByMenuData;

/**
 * Created by NweYiAung on 05-01-2017.
 */
public class RpSaleByMenuListAdapter extends BaseAdapter {

    private Context context;
    private List<RpSaleByMenuData> lstRpSaleByMenuData;

    public RpSaleByMenuListAdapter(Context context, List<RpSaleByMenuData> lstRpSaleByMenuData){
        this.context=context;
        this.lstRpSaleByMenuData = lstRpSaleByMenuData;
    }

    public int getCount(){
        return lstRpSaleByMenuData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ReportSaleByMenuHolder{
        TextView tvListItemName,tvListQuantity,tvListSaleAmount,tvListMainMenuName,tvListTotalByMainMenu,tvListTotalBySubMenu,tvListSubMenuName;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
        ReportSaleByMenuHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_rp_sale_by_menu, null);

            holder=new ReportSaleByMenuHolder();
            holder.tvListItemName=(TextView)vi.findViewById(R.id.tvListItemName);
            holder.tvListQuantity=(TextView)vi.findViewById(R.id.tvListQuantity);
            holder.tvListSaleAmount=(TextView)vi.findViewById(R.id.tvListSaleAmount);
            holder.tvListMainMenuName=(TextView)vi.findViewById(R.id.tvListMainMenuName);
            holder.tvListTotalByMainMenu=(TextView)vi.findViewById(R.id.tvListTotalByMainMenu);
            holder.tvListTotalBySubMenu=(TextView)vi.findViewById(R.id.tvListTotalBySubMenu);
            holder.tvListSubMenuName=(TextView)vi.findViewById(R.id.tvListSubMenuName);

            vi.setTag(holder);
        }else{
            holder=(ReportSaleByMenuHolder)vi.getTag();
        }

        /**if(lstRpSaleByMenuData.get(position).getTotalBySubMenu()==0.0) holder.tvListTotalBySubMenu.setText("");
        else holder.tvListTotalBySubMenu.setText("Total: "+String.valueOf(lstRpSaleByMenuData.get(position).getTotalBySubMenu()));
        if(lstRpSaleByMenuData.get(position).getTotalByMainMenu()==0.0) holder.tvListTotalByMainMenu.setText("");
        else holder.tvListTotalByMainMenu.setText("Total: "+String.valueOf(lstRpSaleByMenuData.get(position).getTotalByMainMenu()));

        String isnull= lstRpSaleByMenuData.get(position).getMainMenuName();
        if(isnull==null) holder.tvListMainMenuName.setText("");
        else holder.tvListMainMenuName.setText(String.valueOf(lstRpSaleByMenuData.get(position).getMainMenuName()));

        isnull= lstRpSaleByMenuData.get(position).getSubMenuName();
        if(isnull==null) holder.tvListSubMenuName.setText("");
        else holder.tvListSubMenuName.setText(String.valueOf(lstRpSaleByMenuData.get(position).getSubMenuName()));**/

        String isnull= lstRpSaleByMenuData.get(position).getMainMenuName();
        if(isnull==null) {
            holder.tvListMainMenuName.setVisibility(View.GONE);
            holder.tvListTotalByMainMenu.setVisibility(View.GONE);
        }
        else {
            holder.tvListMainMenuName.setVisibility(View.VISIBLE);
            holder.tvListTotalByMainMenu.setVisibility(View.VISIBLE);
            holder.tvListMainMenuName.setText(String.valueOf(lstRpSaleByMenuData.get(position).getMainMenuName()));
            holder.tvListTotalByMainMenu.setText("Total: "+String.valueOf(lstRpSaleByMenuData.get(position).getTotalByMainMenu()));
        }

        isnull= lstRpSaleByMenuData.get(position).getSubMenuName();
        if(isnull==null) {
            holder.tvListSubMenuName.setVisibility(View.GONE);
            holder.tvListTotalBySubMenu.setVisibility(View.GONE);
        }
        else {
            holder.tvListSubMenuName.setVisibility(View.VISIBLE);
            holder.tvListTotalBySubMenu.setVisibility(View.VISIBLE);
            holder.tvListSubMenuName.setText(String.valueOf(lstRpSaleByMenuData.get(position).getSubMenuName()));
            holder.tvListTotalBySubMenu.setText("Total: "+String.valueOf(lstRpSaleByMenuData.get(position).getTotalBySubMenu()));
        }

        holder.tvListItemName.setText(lstRpSaleByMenuData.get(position).getItemName());
        holder.tvListQuantity.setText(String.valueOf(lstRpSaleByMenuData.get(position).getQuantity()));
        holder.tvListSaleAmount.setText(String.valueOf(lstRpSaleByMenuData.get(position).getSaleAmount()));

        return vi;
    }
}
