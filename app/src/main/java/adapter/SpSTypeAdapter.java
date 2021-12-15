package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.STypeData;

/**
 * Created by NweYiAung on 15-02-2017.
 */
public class SpSTypeAdapter extends BaseAdapter{
    private Context context;
    List<STypeData> lstSTypeData;

    public SpSTypeAdapter(Context context, List<STypeData> lstSTypeData){
        this.context=context;
        this.lstSTypeData = lstSTypeData;
    }

    public int getCount(){
        return lstSTypeData.size();
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
            vi=inflater.inflate(R.layout.sp_black, null);
        }
        TextView tvSpinnerItem=(TextView)vi.findViewById(R.id.tvSpinnerItem);

        tvSpinnerItem.setText(lstSTypeData.get(position).getsTypeName());

        return vi;
    }

}
