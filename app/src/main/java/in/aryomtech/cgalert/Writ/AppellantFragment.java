package in.aryomtech.cgalert.Writ;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import in.aryomtech.cgalert.DistrictData;
import in.aryomtech.cgalert.R;


public class AppellantFragment extends Fragment {


    View view;
    String respondents, appellants, judge_summary, synopsis;
    TextView respo, appella, judgement_summary, summary;
    ImageView back;
    Context contextNullSafe;

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
        back = view.findViewById(R.id.imageView4);
        respo = view.findViewById(R.id.respondents_list);
        appella = view.findViewById(R.id.appellants_list);
        judgement_summary = view.findViewById(R.id.summary_edt);
        summary = view.findViewById(R.id.synop_edt);


        back.setOnClickListener(v->{
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().remove(AppellantFragment.this).commit();
        });

        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        
        respo.setText(respondents);
        appella.setText(appellants);
        judgement_summary.setText(judge_summary.toLowerCase(Locale.ROOT));
        summary.setText(synopsis.toLowerCase());

        return view;
    }

    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }
}