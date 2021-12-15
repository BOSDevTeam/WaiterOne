package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.CompanyData;

/**
 * Created by User on 6/21/2017.
 */
public class SpCompanyAdapter extends BaseAdapter {

    private Context context;
    List<CompanyData> lstCompanyData;

    public SpCompanyAdapter(Context context, List<CompanyData> lstCompanyData){
        this.context=context;
        this.lstCompanyData =lstCompanyData;
    }

    public int getCount(){
        return lstCompanyData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.sp_black, null);
        }
        TextView tvSpinnerItem=(TextView)vi.findViewById(R.id.tvSpinnerItem);

        tvSpinnerItem.setText(lstCompanyData.get(position).getCompanyName());

        return vi;
    }
}
