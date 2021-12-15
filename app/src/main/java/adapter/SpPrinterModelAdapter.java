package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.PrinterModelData;

/**
 * Created by NweYiAung on 15-02-2017.
 */
public class SpPrinterModelAdapter extends BaseAdapter{
    private Context context;
    List<PrinterModelData> lstModelData;

    public SpPrinterModelAdapter(Context context, List<PrinterModelData> lstModelData){
        this.context=context;
        this.lstModelData = lstModelData;
    }

    public int getCount(){
        return lstModelData.size();
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
            vi=inflater.inflate(R.layout.sp_printer_setting, null);
        }
        TextView tvSpinnerItem=(TextView)vi.findViewById(R.id.tvSpinnerItem);

        tvSpinnerItem.setText(lstModelData.get(position).getModelName());

        return vi;
    }

}
