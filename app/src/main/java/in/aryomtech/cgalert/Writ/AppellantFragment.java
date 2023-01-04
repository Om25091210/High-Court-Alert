package in.aryomtech.cgalert.Writ;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.aryomtech.cgalert.R;


public class AppellantFragment extends Fragment {


    View view;
    String respondents, appellants, judge_summary, synopsis;
    TextView respo, appella, judgement_summary, summary;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_appellant, container, false);
        assert getArguments() != null;
        respondents = getArguments().getString("respondents");
        appellants = getArguments().getString("appellants");
        judge_summary = getArguments().getString("judge_summary");
        synopsis = getArguments().getString("synopsis");

        respo = view.findViewById(R.id.respondents_list);
        appella = view.findViewById(R.id.appellants_list);
        judgement_summary = view.findViewById(R.id.summary_edt);
        summary = view.findViewById(R.id.synop_edt);

        respo.setText(respondents);
        appella.setText(appellants);
        judgement_summary.setText(judge_summary);
        summary.setText(synopsis);

        return view;
    }
}