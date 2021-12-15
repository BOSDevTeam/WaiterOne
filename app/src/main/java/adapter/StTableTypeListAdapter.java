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

import data.TableTypeData;
import listener.SetupEditDeleteButtonClickListener;

/**
 * Created by NweYiAung on 01-03-2017.
 */
public class StTableTypeListAdapter extends BaseAdapter {

    private Context context;
    SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener;
    List<TableTypeData> lstTableTypeData;

    public StTableTypeListAdapter(Context context, List<TableTypeData> lstTableTypeData){
        this.context=context;
        this.lstTableTypeData =lstTableTypeData;
    }

    public void setOnSetupEditDeleteButtonClickListener(SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener){
        this.setupEditDeleteButtonClickListener =setupEditDeleteButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstTableTypeData.size();
    }

    @Override
    public String getItem(int position){
        return lstTableTypeData.get(position).getTableTypeName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvTableTypeName;
        ImageButton btnEdit,btnDelete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_st_waiter_tabletype_taste_stype, parent,false);
            holder=new ViewHolder();
            holder.tvTableTypeName=(TextView) row.findViewById(R.id.tvName);
            holder.btnEdit=(ImageButton) row.findViewById(R.id.btnEdit);
            holder.btnDelete=(ImageButton) row.findViewById(R.id.btnDelete);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvTableTypeName.setText(lstTableTypeData.get(position).getTableTypeName());

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
