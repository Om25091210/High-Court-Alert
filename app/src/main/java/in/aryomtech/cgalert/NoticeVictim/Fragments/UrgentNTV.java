package in.aryomtech.cgalert.NoticeVictim.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.Adapter_PhoneNo;
import in.aryomtech.cgalert.DB.TinyDB;
import in.aryomtech.cgalert.Dashboard;
import in.aryomtech.cgalert.Fragments.Mcrc_Rm_Coll;
import in.aryomtech.cgalert.Fragments.Mcrc_Rm_Return;
import in.aryomtech.cgalert.Fragments.Similar_Collection;
import in.aryomtech.cgalert.Fragments.Similar_Return;
import in.aryomtech.cgalert.Fragments.model.Excel_data;
import in.aryomtech.cgalert.Fragments.model.stationData;
import in.aryomtech.cgalert.Fragments.today;
import in.aryomtech.cgalert.Fragments.urgent_data;
import in.aryomtech.cgalert.Home;
import in.aryomtech.cgalert.NoticeVictim.NoticeAdapter;
import in.aryomtech.cgalert.NoticeVictim.Notice_model;
import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.policestation.p_Home;
import in.aryomtech.myapplication.v4.FragmentPagerItemAdapter;
import in.aryomtech.myapplication.v4.FragmentPagerItems;

public class UrgentNTV extends Fragment {

    View view;
    RecyclerView recyclerView;
    Context contextNullSafe;
    List<Notice_model> list;
    DatabaseReference reference;
    String stat_name;
    FirebaseAuth auth;
    FirebaseUser user;
    String sp_of;
    TinyDB tinyDB;
    ImageView cg_logo;
    TextView no_data;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String ps_or_admin="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notice_main, container, false);

        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        stat_name= getContextNullSafety().getSharedPreferences("station_name_K",MODE_PRIVATE)
                .getString("the_station_name2003","");

        cg_logo=view.findViewById(R.id.imageView3);
        no_data=view.findViewById(R.id.no_data);
        recyclerView = view.findViewById(R.id.rv);
        list = new ArrayList<>();
        tinyDB=new TinyDB(getContextNullSafety());
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(mManager);
        reference = FirebaseDatabase.getInstance().getReference().child("notice");
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        ps_or_admin=getContextNullSafety().getSharedPreferences("useris?",MODE_PRIVATE)
                .getString("the_user_is?","");
        sp_of=getContextNullSafety().getSharedPreferences("Is_SP",MODE_PRIVATE)
                .getString("Yes_of","none");
        if(sp_of.equals("none")) {
            if(tinyDB.getInt("num_station")==0){
                getDataForIG();
            }
            else if(tinyDB.getInt("num_station")==10){
                getDataForSDOP();
            }
            else{
                if(ps_or_admin.equals("home")) {
                    get_data();
                }
                else if(ps_or_admin.equals("p_home")){
                    get_ps_data();
                }
            }
        }
        else
            getdata_for_sp();
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if(sp_of.equals("none")) {
                if(tinyDB.getInt("num_station")==0){
                    getDataForIG();
                }
                else if(tinyDB.getInt("num_station")==10){
                    getDataForSDOP();
                }
                else{
                    if(ps_or_admin.equals("home")) {
                        get_data();
                    }
                    else if(ps_or_admin.equals("p_home")){
                        get_ps_data();
                    }
                }
            }
            else
                getdata_for_sp();
        });
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;
    }

    private void get_data() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    try {
                        Date dNow = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        Date list1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(snapshot.child(ds.getKey()).child("hearingDate").getValue(String.class) + "");
                        Date current = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(ft.format(dNow));
                        Log.e("date", list1.before(current) + "");
                        if (list1.before(current)) {
                            list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Notice_model.class));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                Collections.reverse(list);
                NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), list);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getdata_for_sp() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    try {
                        Date dNow = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        Date list1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(snapshot.child(ds.getKey()).child("hearingDate").getValue(String.class) + "");
                        Date current = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(ft.format(dNow));
                        Log.e("date", list1.before(current) + "");
                        if (list1.before(current)) {
                            if (Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class)).trim().toUpperCase().equals(sp_of)) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Notice_model.class));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), list);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDataForSDOP() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    try {
                        Date dNow = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        Date list1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(snapshot.child(ds.getKey()).child("hearingDate").getValue(String.class) + "");
                        Date current = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(ft.format(dNow));
                        Log.e("date", list1.before(current) + "");
                        if (list1.before(current)) {
                            if (tinyDB.getListString("districts_list").contains(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class)).trim().toUpperCase())
                                    && tinyDB.getListString("stations_list").contains("PS "+Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("station").getValue(String.class)).trim().toUpperCase())) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Notice_model.class));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), list);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDataForIG() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    try {
                        Date dNow = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        Date list1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(snapshot.child(ds.getKey()).child("hearingDate").getValue(String.class) + "");
                        Date current = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(ft.format(dNow));
                        Log.e("date", list1.before(current) + "");
                        if (list1.before(current)) {
                            if (tinyDB.getListString("districts_list").contains(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class)).trim().toUpperCase())) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Notice_model.class));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), list);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void get_ps_data() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    try {
                        Date dNow = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        Date list1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(snapshot.child(ds.getKey()).child("hearingDate").getValue(String.class) + "");
                        Date current = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(ft.format(dNow));
                        Log.e("date", list1.before(current) + "");
                        if (list1.before(current)) {
                            if ((Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("station").getValue(String.class)).trim()).toUpperCase().equals(stat_name.substring(3).trim())) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Notice_model.class));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), list);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
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