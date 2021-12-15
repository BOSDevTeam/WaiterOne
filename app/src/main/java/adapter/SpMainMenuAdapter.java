package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.MainMenuData;

/**
 * Created by NweYiAung on 15-02-2017.
 */
public class SpMainMenuAdapter extends BaseAdapter{
    private Context context;
    List<MainMenuData> lstMainMenuData;

    public SpMainMenuAdapter(Context context, List<MainMenuData> lstMainMenuData){
        this.context=context;
        this.lstMainMenuData =lstMainMenuData;
    }

    public int getCount(){
        return lstMainMenuData.size();
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

        tvSpinnerItem.setText(lstMainMenuData.get(position).getMainMenuName());

        return vi;
    }

}
