package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.WaiterData;
import listener.DefineUserRightClickListener;

/**
 * Created by User on 6/6/2017.
 */
public class DefineUserRightAdapter extends BaseAdapter {
    private Context context;
    DefineUserRightClickListener defineUserRightClickListener;
    List<WaiterData> lstWaiterData;

    public DefineUserRightAdapter(Context context, List<WaiterData> lstWaiterData){
        this.context=context;
        this.lstWaiterData =lstWaiterData;
    }

    public void setOnDefineButtonClickListener(DefineUserRightClickListener defineUserRightClickListener){
        this.defineUserRightClickListener =defineUserRightClickListener;
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
        Button btnDefine;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_define_user_right, parent,false);
            holder=new ViewHolder();
            holder.tvWaiterName=(TextView) row.findViewById(R.id.tvName);
            holder.btnDefine =(Button) row.findViewById(R.id.btnDefine);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvWaiterName.setText(lstWaiterData.get(position).getWaiterName());

        holder.btnDefine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(defineUserRightClickListener !=null){
                    defineUserRightClickListener.onDefineButtonClickListener(position);
                }
            }
        });

        return row;
    }
}
