package in.aryomtech.cgalert.Writ;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import in.aryomtech.cgalert.Fragments.model.stationData;
import in.aryomtech.cgalert.R;

public class WritAdapter extends RecyclerView.Adapter<WritAdapter.ViewHolder> {

    List<WritModel> list;
    Context context;
    private Timer timer;
    String pushkey;

    public WritAdapter(List<WritModel> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public void setTasks(List<WritModel> todoList) {
        this.list = todoList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public WritAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_writ_card, parent, false);
        return new WritAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WritAdapter.ViewHolder holder, int position) {

        holder.district.setText(list.get(position).getDistrict());
        holder.dateOfFiling.setText(list.get(position).getDateOfFiling());
        holder.nature.setText(list.get(position).getNature());
        //holder.appellants.setText(Arrays.toString(list.get(position).getAppellants()));
        //holder.respondents.setText(Arrays.toString(list.get(position).getRespondents()));
        holder.summary.setText(list.get(position).getSummary());
        holder.judgementDate.setText(list.get(position).getJudgementDate());
        holder.caseYear.setText("/" + list.get(position).getCaseYear());
        holder.caseNo.setText("/" + list.get(position).getCaseNo());
        holder.judgement.setText(list.get(position).getJudgement());



        if (!list.get(position).getJudgement().equals("DISMISSED")) {
            //holder.layoutJudge.setVisibility(View.VISIBLE);
            holder.layoutDue.setVisibility(View.VISIBLE);
            holder.dueDays.setText(list.get(position).getDueDate());
            //holder.judgementSummary.setText(list.get(position).getdSummary());
        }

        pushkey = list.get(position).getPushkey();

        holder.writLayout.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("respondents", list.get(position).getRespondents());
            bundle.putStringArrayList("appellants", list.get(position).getAppellants());
            bundle.putString("judge_summary", list.get(position).getdSummary());
            bundle.putString("synopsis", list.get(position).getSummary());
            bundle.putString("pushkey", pushkey);
            Fragment fragment = new AppellantFragment();
            fragment.setArguments(bundle);
            FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.swipe, fragment);
            ft.commit();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView district, dateOfFiling, nature, appellants, respondents, summary, judgementDate, judgement, judgementSummary, dueDays, caseNo, caseYear;
        ConstraintLayout writLayout;
        LinearLayout layoutJudge, layoutDue;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            district = itemView.findViewById(R.id.district_ed);
            dateOfFiling = itemView.findViewById(R.id.date_of_filing);
            nature = itemView.findViewById(R.id.nature);
            appellants = itemView.findViewById(R.id.appellants);
            respondents = itemView.findViewById(R.id.respondents);
            layoutJudge = itemView.findViewById(R.id.layout_judge);
            summary = itemView.findViewById(R.id.summarytxt);
            judgementDate = itemView.findViewById(R.id.judgement_date);
            judgement = itemView.findViewById(R.id.judgementType);
            judgementSummary = itemView.findViewById(R.id.judgement_summary);
            dueDays = itemView.findViewById(R.id.due_date);
            writLayout = itemView.findViewById(R.id.writ_layout);
            layoutDue = itemView.findViewById(R.id.layout_due);
            caseNo = itemView.findViewById(R.id.case_no);
            caseYear = itemView.findViewById(R.id.case_year);
        }
    }
}
