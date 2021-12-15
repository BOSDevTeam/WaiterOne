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
 * Created by NweYiAung on 03-01-2017.
 */
public class RpChooseTableListAdapter extends BaseAdapter {

    private Context context;
    private List<TableData> lstTableData;

    public RpChooseTableListAdapter(Context context, List<TableData> lstTableData){
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
        protected TextView tvListChoosedTable;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.list_rp_choosed_table, null);
            viewHolder = new ViewHolder();
            viewHolder.tvListChoosedTable = (TextView) convertView.findViewById(R.id.tvListChoosedTable);

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvListChoosedTable, viewHolder.tvListChoosedTable);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvListChoosedTable.setText(lstTableData.get(position).getTableName());
        return convertView;
    }
}
