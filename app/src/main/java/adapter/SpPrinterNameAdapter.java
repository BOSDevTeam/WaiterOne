package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.PrinterData;

/**
 * Created by NweYiAung on 15-02-2017.
 */
public class SpPrinterNameAdapter extends BaseAdapter{
    private Context context;
    List<PrinterData> lstPrinterData;

    public SpPrinterNameAdapter(Context context, List<PrinterData> lstPrinterData){
        this.context=context;
        this.lstPrinterData = lstPrinterData;
    }

    public int getCount(){
        return lstPrinterData.size();
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

        tvSpinnerItem.setText(lstPrinterData.get(position).getPrinterName());

        return vi;
    }

}
