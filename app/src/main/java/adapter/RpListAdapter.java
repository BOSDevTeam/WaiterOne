package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.ReportData;
import data.TasteData;
import listener.DialogTasteClickListener;
import listener.ReportListClickListener;

/**
 * Created by user on 5/13/2017.
 */
public class RpListAdapter extends BaseAdapter {

    private Context context;
    private List<ReportData> lstReport;
    ReportListClickListener reportClickListener;

    public RpListAdapter(Context context, List<ReportData> lstReport){
        this.context=context;
        this.lstReport =lstReport;
    }

    public void setOnReportListClickListener(ReportListClickListener reportClickListener){
        this.reportClickListener =reportClickListener;
    }

    public int getCount(){
        return lstReport.size();
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
            vi=inflater.inflate(R.layout.list_report, null);
        }
        TextView tvListReport=(TextView)vi.findViewById(R.id.tvListReport);

        tvListReport.setText(lstReport.get(position).getReportName());

        tvListReport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(reportClickListener !=null){
                    reportClickListener.onReportListClickListener(position);
                }
            }
        });

        return vi;
    }
}
