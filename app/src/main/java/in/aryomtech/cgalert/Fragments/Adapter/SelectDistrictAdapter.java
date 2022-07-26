package in.aryomtech.cgalert.Fragments.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.aryomtech.cgalert.R;

public class SelectDistrictAdapter extends RecyclerView.Adapter<SelectDistrictAdapter.ViewHolder> {

    List<String> list;
    Context context;

    public SelectDistrictAdapter(List<String> list,Context context) {
        this.list = list;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_district_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.district.setText(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{
        TextView district;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            district=itemView.findViewById(R.id.dis);
        }
    }
}
