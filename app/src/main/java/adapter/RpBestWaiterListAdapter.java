package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.WaiterData;

/**
 * Created by NweYiAung on 03-01-2017.
 */
public class RpBestWaiterListAdapter extends BaseAdapter {

    private Context context;
    private List<WaiterData> lstWaiterData;

    public RpBestWaiterListAdapter(Context context, List<WaiterData> lstWaiterData){
        this.context=context;
        this.lstWaiterData =lstWaiterData;
    }

    public int getCount(){
        return  lstWaiterData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        protected TextView tvListBestWaiter,tvListSaledTableCount,tvListSaledCustomerNumber;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.list_rp_best_waiter, null);
            viewHolder = new ViewHolder();
            viewHolder.tvListBestWaiter = (TextView) convertView.findViewById(R.id.tvListBestWaiter);
            viewHolder.tvListSaledTableCount = (TextView) convertView.findViewById(R.id.tvListSaledTableCount);
            viewHolder.tvListSaledCustomerNumber = (TextView) convertView.findViewById(R.id.tvListSaledCustomerNumber);

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvListBestWaiter, viewHolder.tvListBestWaiter);
            convertView.setTag(R.id.tvListSaledTableCount, viewHolder.tvListSaledTableCount);
            convertView.setTag(R.id.tvListSaledCustomerNumber, viewHolder.tvListSaledCustomerNumber);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvListBestWaiter.setText(lstWaiterData.get(position).getWaiterName());
        viewHolder.tvListSaledTableCount.setText(String.valueOf(lstWaiterData.get(position).getSaledTableCount()));
        viewHolder.tvListSaledCustomerNumber.setText(String.valueOf(lstWaiterData.get(position).getSaledCustomerNumber()));

        return convertView;
    }
}
