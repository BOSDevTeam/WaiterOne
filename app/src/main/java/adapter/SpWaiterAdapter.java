package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import common.SystemSetting;
import data.WaiterData;

/**
 * Created by NweYiAung on 15-02-2017.
 */
public class SpWaiterAdapter extends BaseAdapter{
    private Context context;
    List<WaiterData> lstWaiterData;

    public SpWaiterAdapter(Context context, List<WaiterData> lstWaiterData){
        this.context=context;
        this.lstWaiterData =lstWaiterData;
    }

    public int getCount(){
        return lstWaiterData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(final int position,View convertView,ViewGroup parent){
        View vi=convertView;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(SystemSetting.isSpTxtWhite) vi=inflater.inflate(R.layout.sp_white, null);
            else vi=inflater.inflate(R.layout.sp_black, null);
        }
        TextView tvSpinnerItem=(TextView)vi.findViewById(R.id.tvSpinnerItem);

        tvSpinnerItem.setText(lstWaiterData.get(position).getWaiterName());

        return vi;
    }

}
