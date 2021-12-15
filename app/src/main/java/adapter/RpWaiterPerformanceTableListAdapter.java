package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.TableData;

/**
 * Created by NweYiAung on 05-01-2017.
 */
public class RpWaiterPerformanceTableListAdapter extends BaseAdapter {

    private Context context;
    private List<TableData> lstTableData;

    public RpWaiterPerformanceTableListAdapter(Context context, List<TableData> lstTableData){
        this.context=context;
        this.lstTableData =lstTableData;
    }

    public int getCount(){
        return  lstTableData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        protected TextView tvListWaiter;
        protected TextView tvListSaledTableName;
        protected TextView tvListTableCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.list_rp_waiter_performance_table, null);
            viewHolder = new ViewHolder();
            viewHolder.tvListWaiter = (TextView) convertView.findViewById(R.id.tvListWaiter);
            viewHolder.tvListSaledTableName = (TextView) convertView.findViewById(R.id.tvListSaledTableName);
            viewHolder.tvListTableCount = (TextView) convertView.findViewById(R.id.tvListTableCount);

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvListWaiter, viewHolder.tvListWaiter);
            convertView.setTag(R.id.tvListSaledTableName, viewHolder.tvListSaledTableName);
            convertView.setTag(R.id.tvListTableCount, viewHolder.tvListTableCount);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvListWaiter.setText(lstTableData.get(position).getWaiterName());
        viewHolder.tvListSaledTableName.setText(lstTableData.get(position).getTableName());
        viewHolder.tvListTableCount.setText(String.valueOf(lstTableData.get(position).getWaiterSaledTableCount()));

        return convertView;
    }
}
