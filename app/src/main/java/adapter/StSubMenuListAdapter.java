package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.SubMenuData;
import listener.SetupEditDeleteButtonClickListener;

/**
 * Created by NweYiAung on 01-03-2017.
 */
public class StSubMenuListAdapter extends BaseAdapter {

    private Context context;
    SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener;
    List<SubMenuData> lstSubMenuData;

    public StSubMenuListAdapter(Context context, List<SubMenuData> lstSubMenuData){
        this.context=context;
        this.lstSubMenuData =lstSubMenuData;
    }

    public void setOnSetupEditDeleteButtonClickListener(SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener){
        this.setupEditDeleteButtonClickListener =setupEditDeleteButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstSubMenuData.size();
    }

    @Override
    public String getItem(int position){
        return lstSubMenuData.get(position).getSubMenuName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvMainMenuName, tvSortCode,tvSubMenuName;
        ImageButton btnEdit,btnDelete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_st_sub_menu, parent,false);
            holder=new ViewHolder();
            holder.tvMainMenuName =(TextView) row.findViewById(R.id.tvMainMenuName);
            holder.tvSortCode =(TextView) row.findViewById(R.id.tvSortCode);
            holder.tvSubMenuName =(TextView) row.findViewById(R.id.tvSubMenuName);
            holder.btnEdit=(ImageButton) row.findViewById(R.id.btnEdit);
            holder.btnDelete=(ImageButton) row.findViewById(R.id.btnDelete);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvMainMenuName.setText(lstSubMenuData.get(position).getMainMenuName());
        holder.tvSubMenuName.setText(lstSubMenuData.get(position).getSubMenuName());
        holder.tvSortCode.setText(lstSubMenuData.get(position).getSortCode());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(setupEditDeleteButtonClickListener !=null){
                    setupEditDeleteButtonClickListener.onEditButtonClickListener(position);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(setupEditDeleteButtonClickListener !=null){
                    setupEditDeleteButtonClickListener.onDeleteButtonClickListener(position);
                }
            }
        });

        return row;
    }
}
