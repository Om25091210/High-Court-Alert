package in.aryomtech.cgalert.Writ.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import in.aryomtech.cgalert.R;
import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class RespondentAdapter extends RecyclerView.Adapter<RespondentAdapter.ViewHolder> {

    Context context;
    ArrayList<String> task_list;
    int flag;
    public RespondentAdapter(Context context, ArrayList<String> task_list, int i) {
        this.context=context;
        this.task_list=task_list;
        this.flag=i;
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
        if(flag==1){
            holder.tick.setVisibility(View.VISIBLE);
        }
        else{
            holder.tick.setVisibility(View.GONE);
        }
        holder.textView.setText(task_list.get(position));
    }

    @Override
    public int getItemCount() {
        return task_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView tick;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.text_View);
            tick=itemView.findViewById(R.id.tick);
        }
    }
}
