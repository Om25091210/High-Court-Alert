package in.aryomtech.cgalert.Writ;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.DB.TinyDB;
import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.Writ.Adapter.WritAdapter;
import in.aryomtech.cgalert.Writ.Model.WritModel;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class WritDecided extends Fragment {

    View view;
    RecyclerView recyclerView;
    Context contextNullSafe;
    List<WritModel> list;
    DatabaseReference reference;
    String stat_name,ds_name;
    FirebaseAuth auth;
    FirebaseUser user;
    String sp_of;
    TinyDB tinyDB;
    ImageView cg_logo;
    TextView no_data;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String ps_or_admin="";
    EditText search;
    List<String> list_string=new ArrayList<>();
    WritAdapter adapter;
    List<WritModel> mylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_writ_decided, container, false);

        view = inflater.inflate(R.layout.fragment_writ_need, container, false);
        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        stat_name= getContextNullSafety().getSharedPreferences("station_name_K",MODE_PRIVATE)
                .getString("the_station_name2003","");
        ds_name=getContextNullSafety().getSharedPreferences("district_name_K",MODE_PRIVATE)
                .getString("the_district_name2002","");

        cg_logo=view.findViewById(R.id.imageView3);
        no_data=view.findViewById(R.id.no_data);
        recyclerView = view.findViewById(R.id.rv);
        list = new ArrayList<>();
        search = view.findViewById(R.id.search);
        mylist= new ArrayList<>();
        tinyDB=new TinyDB(getContextNullSafety());
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(mManager);
        reference = FirebaseDatabase.getInstance().getReference().child("writ");
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
                else {
                    getdata_for_sp();
                }
            });

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s+"");
            }
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

    private void getdata_for_sp() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    if (snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").getValue(String.class)!=null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("case_nature").getValue(String.class) != null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("caseNo").getValue(String.class) != null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("dateOfFiling").getValue(String.class) !=null) {
                        if (!Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").getValue(String.class)).trim().equals("") || snapshot.child(Objects.requireNonNull(ds.getKey())).child("Judgement").getValue(String.class).equals("DISMISSED")) {
                            if(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class).trim().toUpperCase().equals(sp_of)) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(WritModel.class));
                            }
                        }
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                WritAdapter adapter = new WritAdapter(list, getContextNullSafety());
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void get_data() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if (snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").getValue(String.class)!=null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("case_nature").getValue(String.class) != null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("caseNo").getValue(String.class) != null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("dateOfFiling").getValue(String.class) !=null) {
                        if (!Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").getValue(String.class)).trim().equals("") || snapshot.child(Objects.requireNonNull(ds.getKey())).child("Judgement").getValue(String.class).equals("DISMISSED")) {
                            list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(WritModel.class));
                        }
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                Collections.reverse(list);
                WritAdapter adapter = new WritAdapter(list, getContextNullSafety());
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);
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
                    if (snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").getValue(String.class)!=null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("case_nature").getValue(String.class) != null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("caseNo").getValue(String.class) != null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("dateOfFiling").getValue(String.class) !=null) {
                        if (!Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").getValue(String.class)).trim().equals("") || snapshot.child(Objects.requireNonNull(ds.getKey())).child("Judgement").getValue(String.class).equals("DISMISSED")) {
                            if (tinyDB.getListString("districts_list").contains(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class)).trim().toUpperCase())) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(WritModel.class));
                            }
                        }
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                WritAdapter adapter = new WritAdapter(list, getContextNullSafety());
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
                    if (snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").getValue(String.class)!=null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("case_nature").getValue(String.class) != null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("caseNo").getValue(String.class) != null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("dateOfFiling").getValue(String.class) !=null) {
                        if (!Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").getValue(String.class)).trim().equals("") || snapshot.child(Objects.requireNonNull(ds.getKey())).child("Judgement").getValue(String.class).equals("DISMISSED")) {
                            if (tinyDB.getListString("districts_list").contains(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class)).trim().toUpperCase())) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(WritModel.class));
                            }
                        }
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                WritAdapter adapter = new WritAdapter( list, getContextNullSafety());
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
        Date dNow = new Date( );
        SimpleDateFormat ft =
                new SimpleDateFormat ("dd.MM.yyyy",Locale.getDefault());
        String cr_dt=ft.format(dNow);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    if (snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").getValue(String.class)!=null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("case_nature").getValue(String.class) != null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("caseNo").getValue(String.class) != null && snapshot.child(Objects.requireNonNull(ds.getKey())).child("dateOfFiling").getValue(String.class) !=null) {
                        if (!Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").getValue(String.class)).trim().equals("") || snapshot.child(Objects.requireNonNull(ds.getKey())).child("Judgement").getValue(String.class).equals("DISMISSED")) {
                            if (ds_name.equals(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class)).trim().toUpperCase())) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(WritModel.class));
                            }
                        }
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                WritAdapter adapter = new WritAdapter(list, getContextNullSafety());
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void convert_to_list(WritModel object) {
        list_string.clear();
        try{
            list_string.add(object.getDistrict().toLowerCase());
            list_string.add(object.getJudgement().toLowerCase());
            list_string.add(object.getCaseYear().toLowerCase());
            list_string.add(object.getCaseNo().toLowerCase());
            list_string.add(object.getCase_nature().toLowerCase());
            list_string.add(object.getPushkey().toLowerCase());
            list_string.add(object.getDateOfFiling().toLowerCase());
            list_string.add(object.getDistrict().toLowerCase());
            list_string.add(object.getJudgementDate().toLowerCase());
            list_string.add(object.getDueDate().toLowerCase());
            list_string.add(String.valueOf(object.getAppellant()));
            list_string.add(String.valueOf(object.getRespondents()));
            list_string.add(object.getSummary().toLowerCase());
            list_string.add(object.getdSummary().toLowerCase());
        }
        catch (NullPointerException e){
            System.out.println("Error");
        }
    }

    private void search(String str) {
        if(str.equals("")){
            adapter = new WritAdapter(list, getContextNullSafety());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else {
            String[] str_Args = str.toLowerCase().split(" ");
            mylist.clear();
            int count = 0;
            boolean not_once = true;
            List<Integer> c_list = new ArrayList<>();
            for (WritModel object : list) {
                convert_to_list(object);
                for (String s : list_string) {
                    for (String str_arg : str_Args) {
                        if (str_arg.contains("/") && not_once) {
                            String sub1 = str_arg.substring(0, str_arg.indexOf("/"));
                            String sub2 = str_arg.substring(str_arg.indexOf("/") + 1);
                            try {
                                if (list_string.get(3).contains(sub1) && list_string.get(2).contains(sub2)) {
                                    count++;
                                    not_once = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (s.contains(str_arg)) {
                            count++;
                        }
                    }
                }
                c_list.add(count);
                System.out.println(c_list + "");
                if (count >= str_Args.length)
                    mylist.add(object);
                count = 0;
            }
            adapter = new WritAdapter(mylist, getContextNullSafety());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
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