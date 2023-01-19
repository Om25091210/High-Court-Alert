package in.aryomtech.cgalert.Writ.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import in.aryomtech.cgalert.R;

public class AppellantAdapter extends RecyclerView.Adapter<AppellantAdapter.ViewHolder> {

    Context context;
    ArrayList<String> appellant_list;
    public AppellantAdapter(Context context, ArrayList<String> appellant_list) {
        this.context=context;
        this.appellant_list=appellant_list;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.respondents_cards,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.textView.setText(appellant_list.get(position));
    }

    @Override
    public int getItemCount() {
        return appellant_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.text_View);
        }
    }
}
