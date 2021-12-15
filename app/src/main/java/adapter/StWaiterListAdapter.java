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

import data.WaiterData;
import listener.SetupEditDeleteButtonClickListener;

/**
 * Created by NweYiAung on 01-03-2017.
 */
public class StWaiterListAdapter extends BaseAdapter {

    private Context context;
    SetupEditDeleteButtonClickListener waiterButtonClickListener;
    List<WaiterData> lstWaiterData;

    public StWaiterListAdapter(Context context, List<WaiterData> lstWaiterData){
        this.context=context;
        this.lstWaiterData =lstWaiterData;
    }

    public void setOnSetupEditDeleteButtonClickListener(SetupEditDeleteButtonClickListener waiterButtonClickListener){
        this.waiterButtonClickListener =waiterButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstWaiterData.size();
    }

    @Override
    public String getItem(int position){
        return lstWaiterData.get(position).getWaiterName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvWaiterName;
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
            holder.tvWaiterName=(TextView) row.findViewById(R.id.tvName);
            holder.btnEdit=(ImageButton) row.findViewById(R.id.btnEdit);
            holder.btnDelete=(ImageButton) row.findViewById(R.id.btnDelete);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvWaiterName.setText(lstWaiterData.get(position).getWaiterName());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(waiterButtonClickListener !=null){
                    waiterButtonClickListener.onEditButtonClickListener(position);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(waiterButtonClickListener !=null){
                    waiterButtonClickListener.onDeleteButtonClickListener(position);
                }
            }
        });

        return row;
    }
}
