package in.aryomtech.cgalert.NoticeVictim;

import static android.content.Context.MODE_PRIVATE;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.aryomtech.cgalert.Fragments.model.Excel_data;
import in.aryomtech.cgalert.R;

public class NoticemainAdmin extends Fragment {

    View view;
    RecyclerView recyclerView;
    Context contextNullSafe;
    List<Notice_model> list;
    DatabaseReference reference;
    ImageView form, back;
    TextView station;
    boolean isadmin = false;
    String stat_name;
    FirebaseAuth auth;
    ImageView cg_logo;
    TextView no_data;
    SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_noticemain_admin, container, false);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        stat_name = getContextNullSafety().getSharedPreferences("station_name_K", MODE_PRIVATE)
                .getString("the_station_name2003", "");

        recyclerView = view.findViewById(R.id.rv);
        list = new ArrayList<>();

        cg_logo=view.findViewById(R.id.imageView3);
        no_data=view.findViewById(R.id.no_data);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), list);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(mManager);
        reference = FirebaseDatabase.getInstance().getReference().child("notice");
        form = view.findViewById(R.id.form);
        station = view.findViewById(R.id.textView10);
        back = view.findViewById(R.id.imageView4);
        station.setText("ADMIN");
       // get_status_of_admin();


        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Notice_model.class));
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), list);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(v -> {
            FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }
            ft.commit();
        });


        form.setOnClickListener(v -> {
            Fragment fragment = new NoticeForm();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.layout, fragment);
            ft.addToBackStack(null);
            ft.commit();
        });

        return view;
    }


    private void get_status_of_admin() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("admin");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isadmin = snapshot.child(user.getPhoneNumber().substring(3) + "").exists();
                if (isadmin) {
                    form.setVisibility(View.VISIBLE);
                    station.setText(stat_name);
                    getContextNullSafety().getSharedPreferences("isAdmin_or_not", MODE_PRIVATE).edit()
                            .putBoolean("authorizing_admin", true).apply();
                } else {
                    form.setVisibility(View.GONE);
                    station.setText("ADMIN");
                    getContextNullSafety().getSharedPreferences("isAdmin_or_not", MODE_PRIVATE).edit()
                            .putBoolean("authorizing_admin", false).apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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