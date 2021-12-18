package adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bosictsolution.waiterone.R;

import java.util.Collections;
import java.util.List;

import data.OpenOrderData;
import holder.OpenOrderViewHolder;

/**
 * Created by User on 9/1/2017.
 */
public class OpenOrderRecyclerAdapter extends RecyclerView.Adapter<OpenOrderViewHolder>{

    List<OpenOrderData> list = Collections.emptyList();
    Context context;

    public OpenOrderRecyclerAdapter(List<OpenOrderData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public OpenOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_child_open_order, parent, false);
        OpenOrderViewHolder holder = new OpenOrderViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(OpenOrderViewHolder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.tvTranID.setText("#:"+list.get(position).getTranid());
        holder.tvDateTime.setText("from "+list.get(position).getDate()+" "+list.get(position).getTime());
        holder.tvTable.setText("Table: "+list.get(position).getTable());
        holder.tvGuest.setText("Guests: "+list.get(position).getGuest());

    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, OpenOrderData data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(OpenOrderData data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }


}
