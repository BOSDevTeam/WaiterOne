package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

import java.util.List;

import data.ItemSubData;

public class ItemSubRvAdapter extends RecyclerView.Adapter<ItemSubRvAdapter.ViewHolder>  {

    private Context context;
    List<ItemSubData> lstItemSub;

    public ItemSubRvAdapter(List<ItemSubData> lstItemSub, Context context) {
        this.lstItemSub = lstItemSub;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sub, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.tvSubName.setText(lstItemSub.get(position).getSubName());
        holder.tvPrice.setText(String.valueOf(lstItemSub.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return lstItemSub.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubName,tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSubName =itemView.findViewById(R.id.tvSubName);
            tvPrice =  itemView.findViewById(R.id.tvPrice);
        }
    }
}
