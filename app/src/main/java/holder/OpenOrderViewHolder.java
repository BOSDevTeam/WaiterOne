package holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bosictsolution.waiterone.R;

/**
 * Created by User on 9/4/2017.
 */
public class OpenOrderViewHolder extends RecyclerView.ViewHolder  {

    public TextView tvTranID,tvDateTime,tvTable,tvGuest;

    public OpenOrderViewHolder(View itemView) {
        super(itemView);
        tvTranID = (TextView) itemView.findViewById(R.id.tvTranID);
        tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
        tvTable = (TextView) itemView.findViewById(R.id.tvTable);
        tvGuest = (TextView) itemView.findViewById(R.id.tvGuest);
    }
}
