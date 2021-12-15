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

import data.TasteData;
import listener.DialogTasteClickListener;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class DgTasteGridAdapter extends BaseAdapter {

    private Context context;
    private List<TasteData> lstTaste;
    DialogTasteClickListener tasteClickListener;

    public DgTasteGridAdapter(Context context, List<TasteData> lstTaste){
        this.context=context;
        this.lstTaste=lstTaste;
    }

    public void setOnTasteClickListener(DialogTasteClickListener tasteClickListener){
        this.tasteClickListener =tasteClickListener;
    }

    public int getCount(){
        return lstTaste.size();
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
            vi=inflater.inflate(R.layout.list_taste, null);
        }
        TextView tvListTaste=(TextView)vi.findViewById(R.id.tvListTaste);

        tvListTaste.setText(lstTaste.get(position).getTasteName());

        tvListTaste.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(tasteClickListener !=null){
                    tasteClickListener.onTasteClickListener(position);
                }
            }
        });

        return vi;
    }
}
