package in.aryomtech.cgalert.Writ.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class WritAdapter extends RecyclerView.Adapter<WritAdapter.ViewHolder> {

    List<WritModel> list;
    Context context;
    String  appellant_list = "";
    String  respondent_list = "";
    String pushkey;

    public WritAdapter(List<WritModel> list, Context context) {
        this.list = list;
        this.context = context;
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
        holder.nature.setText(list.get(position).getCase_nature());
        holder.summary.setText(list.get(position).getSummary());
        if(list.get(position).getJudgementDate()!=null) {
            holder.judgementDate.setText(list.get(position).getJudgementDate());
        }
        else{
            holder.judgementDate.setText("--");
        }
        holder.caseYear.setText("/" + list.get(position).getCaseYear());
        holder.caseNo.setText("/" + list.get(position).getCaseNo());
        if(list.get(position).getJudgementt()!=null) {
            holder.judgement.setText(list.get(position).getJudgementt());
        }
        else{
            holder.judgement.setText("--");
        }

        if(list.get(position).getRemind()!=null) {
            if(list.get(position).getRemind().equals("once")){
                holder.tick.setVisibility(View.VISIBLE);
                holder.tick.setImageResource(R.drawable.ic_blue_tick);
            }
            else if(list.get(position).getRemind().equals("twice")){
                holder.tick.setVisibility(View.VISIBLE);
                holder.tick.setImageResource(R.drawable.ic_green_tick);
            }
        }
        else
            holder.tick.setVisibility(View.GONE);

        if(list.get(position).getSeenn()!=null)
            holder.seen.setVisibility(View.VISIBLE);
        else
            holder.seen.setVisibility(View.GONE);

        if(list.get(position).getSentt()!=null)
            holder.notified.setVisibility(View.VISIBLE);
        else
            holder.notified.setVisibility(View.GONE);

        if(list.get(position).getDecisionDate()!=null) {
            if (list.get(position).getDecisionDate().equals("")) {
                if(list.get(position).getJudgementt().equals("DISMISSED")){
                    holder.writLayout.setBackgroundResource(R.drawable.bg_card_white);
                }else {
                    holder.writLayout.setBackgroundResource(R.drawable.bg_card_red);
                }
            } else {
                holder.writLayout.setBackgroundResource(R.drawable.bg_card_white);
            }
        }
        if(list.get(position).getJudgementt()!=null) {
            if (list.get(position).getJudgementt().equals("DISMISSED")) {
                holder.writLayout.setBackgroundResource(R.drawable.bg_card_white);
            } else {
                if(!list.get(position).getDecisionDate().equals("")) {
                    holder.writLayout.setBackgroundResource(R.drawable.bg_card_white);
                }else{
                    holder.writLayout.setBackgroundResource(R.drawable.bg_card_red);
                }
            }
        }

        if(list.get(position).getJudgementt()!=null) {
            if (!list.get(position).getJudgementt().equals("DISMISSED")) {
                //holder.layoutJudge.setVisibility(View.VISIBLE);
                holder.layoutDue.setVisibility(View.VISIBLE);
                holder.dueDays.setText(list.get(position).getDueDate());
                //holder.judgementSummary.setText(list.get(position).getdSummary());
            }
        }
        holder.view.setOnClickListener(v->{
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(Uri.parse(list.get(position).getUploaded_file()), "application/pdf");

                Intent chooser = Intent.createChooser(browserIntent, "View PDF");
                chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // optional

                context.startActivity(chooser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        pushkey = list.get(position).getkey();

        holder.writLayout.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("respondents", list.get(position).getRespondents());
            bundle.putStringArrayList("appellants", list.get(position).getAppellant());
            bundle.putString("judge_summary", list.get(position).getdSummary());
            bundle.putString("synopsis", list.get(position).getSummary());
            bundle.putString("pushkey", list.get(position).getkey());
            bundle.putString("decision", list.get(position).getDecisionDate());
            Fragment fragment = new AppellantFragment();
            fragment.setArguments(bundle);
            FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(R.id.swipe, fragment,"writform");
            ft.addToBackStack(null);
            ft.commit();
        });
        ArrayList<String> appellants = list.get(position).getAppellant();
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
                "Nature of the case - " +list.get(position).getCase_nature() + "\nCase Number - " + list.get(position).getCaseNo() +"\nCase Year  - "+ list.get(position).getCaseYear()+"\n" +
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
        TextView district,view, dateOfFiling, nature, appellants, respondents, summary, judgementDate, judgement, judgementSummary, dueDays, caseNo, caseYear;
        ConstraintLayout writLayout;
        ImageView share,notified,seen,tick;
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
            view  = itemView.findViewById(R.id.view);
            seen  = itemView.findViewById(R.id.seen);
            notified  = itemView.findViewById(R.id.notified);
            tick  = itemView.findViewById(R.id.imageView2);
        }
    }
}
