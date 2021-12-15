package adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import data.ItemData;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class ItemListAdapter extends BaseAdapter{

    private Context context;
    List<ItemData> lstItemData;

    public ItemListAdapter(Context context,List<ItemData> lstItemData){
        this.context=context;
        this.lstItemData=lstItemData;
    }

    public int getCount(){
        return lstItemData.size();
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
            vi=inflater.inflate(R.layout.list_item, null);
        }
        TextView tvName=(TextView)vi.findViewById(R.id.tvListItemName);
        TextView tvPrice=(TextView)vi.findViewById(R.id.tvListItemPrice);

        tvName.setText(lstItemData.get(position).getItemName());
        tvPrice.setText(String.valueOf(lstItemData.get(position).getPrice()));

        return vi;
    }
}
