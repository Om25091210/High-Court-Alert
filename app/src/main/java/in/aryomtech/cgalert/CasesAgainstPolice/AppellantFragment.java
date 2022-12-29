package in.aryomtech.cgalert.CasesAgainstPolice;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.aryomtech.cgalert.R;


public class AppellantFragment extends Fragment {


    View view;
    String respondents, appellants;
    TextView respo, appella;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_appellant, container, false);
        assert getArguments() != null;
        respondents = getArguments().getString("respondents");
        appellants = getArguments().getString("appellants");

        respo = view.findViewById(R.id.respondents_list);
        appella = view.findViewById(R.id.appellants_list);
        return view;
    }
}