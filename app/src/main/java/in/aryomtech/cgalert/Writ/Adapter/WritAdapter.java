package in.aryomtech.cgalert.Writ.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.Writ.AppellantFragment;
import in.aryomtech.cgalert.Writ.Model.WritModel;

public class WritAdapter extends RecyclerView.Adapter<WritAdapter.ViewHolder> {

    List<WritModel> list;
    Context context;
    String  appellant_list = "";
    String  respondent_list = "";
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
            bundle.putString("pushkey", list.get(position).getPushkey());
            bundle.putString("decision", list.get(position).getDecisionDate());
            Fragment fragment = new AppellantFragment();
            fragment.setArguments(bundle);
            FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(R.id.swipe, fragment);
            ft.addToBackStack(null);
            ft.commit();
        });
        ArrayList<String> appellants = list.get(position).getAppellants();
        ArrayList<String> respondents = list.get(position).getRespondents();

        if (appellants != null) {
            for (int i = 0; i < appellants.size(); i++) {
                appellant_list = appellant_list +  (i+1) + "\u2022 " + appellants.get(i) + "\n";
            }
        }
        if (respondents != null) {
            for (int i = 0; i < respondents.size(); i++) {
                respondent_list = respondent_list + (i+1) + "\u2022 " + respondents.get(i) + "\n";
            }

        }

        String message = "रिट केस अलर्ट :- "+"\n" +
                "District - " + list.get(position).getDistrict() + "\n" +
                "Date of filling - " + list.get(position).getDateOfFiling()+ "\n" +
                "Nature of the case - " +list.get(position).getNature() + "\nCase Number - " + list.get(position).getCaseNo() +"\nCase Year  - "+ list.get(position).getCaseYear()+"\n" +
                "Judgement date - " + list.get(position).getJudgementDate() +  "\n" +
                "Due days - " + list.get(position).getDueDate()+  "\n" +
                "Appellants list -\n" + appellant_list +"\nRespondents list -\n"+ respondent_list+  "\n" +
                "Synopsis of the case - " + list.get(position).getSummary() +
                "\n\nJudgement summary of the case - " + list.get(position).getdSummary() +
                "\n\nDecided date - " + list.get(position).getDecisionDate();

        holder.share.setOnClickListener(v->{
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message );
            context.startActivity(Intent.createChooser(shareIntent, "Share link using"));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView district, dateOfFiling, nature, appellants, respondents, summary, judgementDate, judgement, judgementSummary, dueDays, caseNo, caseYear;
        ConstraintLayout writLayout;
        ImageView share;
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
            share = itemView.findViewById(R.id.share);
        }
    }
}
