package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.WaiterData;
import listener.CheckedListener;

/**
 * Created by NweYiAung on 27-12-2016.
 */
public class DgChooseWaiterListAdapter extends BaseAdapter {

    private Context context;
    private List<WaiterData> lstWaiterData;
    CheckedListener checkedListener;
    /*public static boolean checkAll;*/

    public DgChooseWaiterListAdapter(Context context, List<WaiterData> lstWaiterData){
        this.context=context;
        this. lstWaiterData =lstWaiterData;
    }

    public void setCustomCheckedListener(CheckedListener checkedListener){
        this.checkedListener=checkedListener;
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
        protected TextView tvWaiterName;
        protected CheckBox chkWaiter;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_dg_choose_waiter, null);
            viewHolder = new ViewHolder();
            viewHolder.chkWaiter=(CheckBox)convertView.findViewById(R.id.chkWaiter);
            viewHolder.tvWaiterName = (TextView) convertView.findViewById(R.id.tvWaiterName);
            viewHolder.chkWaiter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    lstWaiterData.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    if(checkedListener!=null){
                        if(buttonView.isChecked()){
                            checkedListener.onWaiterCheckedListener(getPosition);
                        }else{
                            checkedListener.onWaiterUnCheckedListener(getPosition);
                        }
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvWaiterName, viewHolder.tvWaiterName);
            convertView.setTag(R.id.chkWaiter, viewHolder.chkWaiter);

            /*if(checkAll)viewHolder.chkWaiter.setChecked(true);
            else viewHolder.chkWaiter.setChecked(false);*/
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.chkWaiter.setTag(position); // This line is important.
        viewHolder.tvWaiterName.setText(lstWaiterData.get(position).getWaiterName());
        viewHolder.chkWaiter.setChecked(lstWaiterData.get(position).isSelected());
        return convertView;
    }
}
