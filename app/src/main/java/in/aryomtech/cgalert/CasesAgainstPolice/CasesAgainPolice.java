package in.aryomtech.cgalert.CasesAgainstPolice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.aryomtech.cgalert.R;


public class CasesAgainPolice extends Fragment {

    View view;
    RecyclerView recyclerView;
    Context contextNullSafe;
    List<caseAgainstModel> list;
    DatabaseReference reference;
    ImageView back, form;
    TextView station;
    ImageView cg_logo;
    TextView no_data;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view  = inflater.inflate(R.layout.fragment_cases_again_police2, container, false);
        back = view.findViewById(R.id.back);
        form = view.findViewById(R.id.form);

        cg_logo=view.findViewById(R.id.imageView3);
        no_data=view.findViewById(R.id.no_data);

        recyclerView = view.findViewById(R.id.rv);
        list = new ArrayList<>();
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(mManager);
        reference = FirebaseDatabase.getInstance().getReference().child("writ");
        station = view.findViewById(R.id.textView10);
        back = view.findViewById(R.id.imageView4);
        station.setText("stat_name");

        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(caseAgainstModel.class));
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                caseAgainstAdapter adapter = new caseAgainstAdapter(list,getContextNullSafety());
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        form.setOnClickListener(v->{
           /* mSwipeRefreshLayout.setRefreshing(false);
            Fragment fragment = new CasesAgainPoliceForm();
            assert getFragmentManager() != null;
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.swipe, fragment);
            ft.addToBackStack(null);
            ft.commit();*/
        });


        back.setOnClickListener(v -> {
            FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }
            ft.commit();
        });

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