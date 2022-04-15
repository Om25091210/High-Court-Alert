package in.aryomtech.cgalert.Fragments.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.aryomtech.cgalert.Fragments.model.Excel_data;
import in.aryomtech.cgalert.Fragments.onAgainClickInterface;
import in.aryomtech.cgalert.Fragments.onClickInterface;
import in.aryomtech.cgalert.R;

public class Excel_Adapter extends RecyclerView.Adapter<Excel_Adapter.ViewHolder> {

    Context context;
    List<Excel_data> list;
    boolean is_selected=false;
    boolean isadmin=false;
    onClickInterface onClickInterface;
    onAgainClickInterface onAgainClickInterface;

    public Excel_Adapter(Context context, List<Excel_data> list,onClickInterface onClickInterface,onAgainClickInterface onAgainClickInterface) {
        this.context = context;
        this.list = list;
        this.onClickInterface=onClickInterface;
        this.onAgainClickInterface=onAgainClickInterface;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        isadmin=context.getSharedPreferences("isAdmin_or_not",Context.MODE_PRIVATE)
                .getBoolean("authorizing_admin",false);
        holder.textViewTitle.setText(list.get(position).getB() + "");
        holder.textViewBody.setText(list.get(position).getC());
        holder.Rm.setText(list.get(position).getK());
        holder.mcrc.setText(list.get(position).getD());
        holder.pr_case_no.setText(list.get(position).getD()+" No.");
        holder.crime_no.setText(list.get(position).getH() +"/"+ list.get(position).getI());
        holder.case_no.setText(list.get(position).getE() +"/"+ list.get(position).getG());
        holder.name.setText(list.get(position).getF());
        holder.receiving_date.setText(list.get(position).getJ());
        if(list.get(position).getL()!=null){
            if(!list.get(position).getL().equals("None")) {
                holder.day_left.setVisibility(View.VISIBLE);
                if (nDays_Between_Dates(list.get(position).getL()) == 0) {
                    holder.day_left.setText("0d");
                    holder.day_left.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
                } else if (nDays_Between_Dates(list.get(position).getL()) <= 5) {
                    holder.day_left.setText(nDays_Between_Dates(list.get(position).getL()) + "d");
                    holder.day_left.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
                } else {
                    holder.day_left.setText("--");
                    holder.day_left.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_red_clock, 0, 0, 0);
                }
            }
            else
                holder.day_left.setVisibility(View.GONE);
        }
        else
            holder.day_left.setVisibility(View.GONE);

        if(list.get(position).getReminded()!=null) {
           if(list.get(position).getReminded().equals("once")){
               holder.tick.setImageResource(R.drawable.ic_blue_tick);
           }
           else if(list.get(position).getReminded().equals("twice")){
               holder.tick.setImageResource(R.drawable.ic_green_tick);
           }
        }
        if (list.get(position).getJ().equals("None") || list.get(position).getJ().equals("nan"))
            holder.layout.setBackgroundResource(R.drawable.bg_card_red);
        else
            holder.layout.setBackgroundResource(R.drawable.bg_card_white);
        if (list.get(position).getType().equals("MCRC_RM_COLL")) {
            holder.type.setVisibility(View.VISIBLE);
            holder.type.setImageResource(R.drawable.ic_submit_type);
        } else if (list.get(position).getType().equals("MCRC _RM_ RETURN")) {
            holder.type.setVisibility(View.VISIBLE);
            holder.type.setImageResource(R.drawable.ic_return_type);
        } else
            holder.type.setVisibility(View.GONE);

        if (is_selected){
            String added="Added";
            holder.add_button.setText(added);
            holder.add_button.setBackgroundResource(R.drawable.add_bg_green);
        }
        else{
            String add="Add";
            holder.add_button.setText(add);
            holder.add_button.setBackgroundResource(R.drawable.add_card);
        }

        holder.add_button.setOnClickListener(v->{
            if(holder.add_button.getText().toString().equalsIgnoreCase("add")){
                onClickInterface.setClick(position);
                String added="Added";
                holder.add_button.setText(added);
                holder.add_button.setBackgroundResource(R.drawable.add_bg_green);
            }
            else if(holder.add_button.getText().toString().equalsIgnoreCase("added")){
                onAgainClickInterface.set_remove_click(position);
                String add="Add";
                holder.add_button.setText(add);
                holder.add_button.setBackgroundResource(R.drawable.add_card);
            }
        });

        holder.layout.setOnClickListener(v->{
            if (holder.layout_details.getVisibility() == View.VISIBLE) {
                holder.layout_details.setVisibility(View.GONE);
                // Its visible
            } else {
                holder.layout_details.setVisibility(View.VISIBLE);
                // Either gone or invisible
            }
        });

        if(isadmin)
            holder.add_button.setVisibility(View.VISIBLE);
        else
            holder.add_button.setVisibility(View.GONE);

    }

    public static int nDays_Between_Dates(String date1) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String str = formatter.format(date);
        int diffDays = 0;
        try {
            SimpleDateFormat dates = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            Date startDate = dates.parse(date1);
            Date endDate = dates.parse(str);
            if(startDate.after(endDate)) {
                long diff = endDate.getTime() - startDate.getTime();
                diffDays = (int) (diff / (24 * 60 * 60 * 1000));
            }
            else if(startDate.equals(endDate))
                return 0;
            else{
                return 6;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Math.abs(diffDays);
    }
    public void selectAll(){
        is_selected=true;
        notifyDataSetChanged();
    }
    public void unselectall(){
        is_selected=false;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle,add_button;
        TextView textViewBody;
        TextView day_left;
        TextView Rm,mcrc,crime_no,case_no,pr_case_no,name,receiving_date;
        ConstraintLayout layout;
        LinearLayout layout_details;
        ImageView tick,type;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.station_name);//
            textViewBody = itemView.findViewById(R.id.dist_name);//
            add_button = itemView.findViewById(R.id.textView21);
            Rm = itemView.findViewById(R.id.name_rm);//
            day_left = itemView.findViewById(R.id.day_left);//
            mcrc = itemView.findViewById(R.id.case_type);//
            crime_no = itemView.findViewById(R.id.crime_no);//
            case_no = itemView.findViewById(R.id.case_no);//
            pr_case_no = itemView.findViewById(R.id.pr_case_no);//
            tick = itemView.findViewById(R.id.imageView2);
            layout = itemView.findViewById(R.id.layout);
            layout_details = itemView.findViewById(R.id.linearLayout3);
            type = itemView.findViewById(R.id.type);
            name = itemView.findViewById(R.id.person_name);//
            receiving_date = itemView.findViewById(R.id.receiving_date);//
        }
    }
}
