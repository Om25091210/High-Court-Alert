package in.aryomtech.cgalert.policestation;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.Fragments.Adapter.Return_Adapter;
import in.aryomtech.cgalert.Fragments.model.Excel_data;
import in.aryomtech.cgalert.R;
import soup.neumorphism.NeumorphButton;


public class p_mcrc_rm_return extends Fragment {

    View view;
    private Context contextNullSafe;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    Query query;
    List<String> phone_numbers=new ArrayList<>();
    List<String> station_name_list=new ArrayList<>();
    List<Excel_data> excel_data=new ArrayList<>();
    DatabaseReference phone_numbers_ref;
    List<Excel_data> mylist=new ArrayList<>();
    EditText search;
    ImageView cg_logo;
    TextView no_data;
    ArrayList<String> added_list;
    int onback=0;
    CheckBox select_all;
    Return_Adapter excel_adapter;
    List<String> district_name_list=new ArrayList<>();
    NeumorphButton join;
    String stat_name;
    private in.aryomtech.cgalert.Fragments.Interface.onClickInterface onClickInterface;
    private in.aryomtech.cgalert.Fragments.Interface.onAgainClickInterface onAgainClickInterface;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_p_mcrc_rm_return, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        added_list=new ArrayList<>();
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        search=view.findViewById(R.id.search);
        cg_logo=view.findViewById(R.id.imageView3);
        no_data=view.findViewById(R.id.no_data);
        //Initialize RecyclerView
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        select_all=view.findViewById(R.id.checkBox4);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setLayoutManager(mManager);
        join=view.findViewById(R.id.join);
        excel_adapter= new Return_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface,"");
        if(mRecyclerView!=null)
            mRecyclerView.setAdapter(excel_adapter);
        excel_adapter.notifyDataSetChanged();
        //Initialize Database
        query = FirebaseDatabase.getInstance().getReference().child("data").orderByChild("type").equalTo("RM RETURN");
        phone_numbers_ref=FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        stat_name= getContextNullSafety().getSharedPreferences("station_name_K",Context.MODE_PRIVATE)
                .getString("the_station_name2003","");
        getdata();

        onClickInterface = position -> {
            if(!added_list.contains(excel_data.get(position).getPushkey()))
                added_list.add(excel_data.get(position).getPushkey());
            String txt="Send "+"("+added_list.size()+")";
            select_all.setChecked(false);
            join.setText(txt);
        };

        onAgainClickInterface=removePosition -> {
            added_list.remove(excel_data.get(removePosition).getPushkey());
            String txt="Send "+"("+added_list.size()+")";
            select_all.setChecked(false);
            join.setText(txt);
        };
        select_all.setOnClickListener(v->{
            if (select_all.isChecked()){
                for (int i=0;i<excel_data.size();i++){
                    if(!added_list.contains(excel_data.get(i).getPushkey()))
                        added_list.add(excel_data.get(i).getPushkey());
                }
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.selext_all();
            }
            else{
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselect_all();
            }
            excel_adapter.notifyDataSetChanged();
            Log.e("added_peeps",added_list+"");
        });
        //Set listener to SwipeRefreshLayout for refresh action
        mSwipeRefreshLayout.setOnRefreshListener(this::getdata);
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s+"");
            }
        });

        join.setOnClickListener(v-> gather_number());
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(onback==0){
                    Toast.makeText(contextNullSafe, "Press back again to exit", Toast.LENGTH_SHORT).show();
                    onback=1;
                }
                else{
                    ((FragmentActivity) getContextNullSafety()).finish();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);

        return view;
    }
    private void search(String str) {
        mylist.clear();
        for(Excel_data object:excel_data) {
            if (object.getB().toLowerCase().contains(str.toLowerCase().trim())) {
                mylist.add(object);
            } else if (object.getC().toLowerCase().contains(str.toLowerCase().trim())) {
                mylist.add(object);
            } else if (object.getE().toLowerCase().contains(str.toLowerCase().trim())) {
                mylist.add(object);
            } else if (object.getH().toLowerCase().contains(str.toLowerCase().trim())) {
                mylist.add(object);
            }
            else if(object.getK().toLowerCase().contains(str.toLowerCase().trim())){
                mylist.add(object);
            }
            else if(object.getJ().toLowerCase().contains(str.toLowerCase().trim())){
                mylist.add(object);
            }
            else if(object.getDate().toLowerCase().contains(str.toLowerCase().trim())){
                mylist.add(object);
            }
        }
        excel_adapter=new Return_Adapter(getContextNullSafety(),mylist,onClickInterface,onAgainClickInterface,"");
        excel_adapter.notifyDataSetChanged();
        if(mRecyclerView!=null)
            mRecyclerView.setAdapter(excel_adapter);
    }
    private void getdata() {
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.hasChildren()) {
                        if(snapshot.child(ds.getKey()).child("B").getValue(String.class).toUpperCase().equals(stat_name.substring(3))) {
                            excel_data.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Excel_data.class));
                        }
                    }
                }
                if(excel_data.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselect_all();
                Collections.reverse(excel_data);
                excel_adapter=new Return_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface,"");
                excel_adapter.notifyDataSetChanged();
                if(mRecyclerView!=null)
                    mRecyclerView.setAdapter(excel_adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void gather_number() {
        station_name_list.clear();
        district_name_list.clear();
        phone_numbers.clear();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int h=0;h<added_list.size();h++){
                    String station_name=snapshot.child(added_list.get(h)).child("B").getValue(String.class).trim();
                    String district_name=snapshot.child(added_list.get(h)).child("C").getValue(String.class).trim();
                    if(district_name_list.contains(district_name)) {
                        district_name_list.add(district_name);
                    }
                    if(station_name_list.contains(station_name)){
                        station_name_list.add(station_name);
                    }
                    if(!snapshot.child(added_list.get(h)).child("reminded").exists()){
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("data");
                        reference.child(added_list.get(h)).child("reminded").setValue("once");
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_MONTH,4);
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        reference.child(added_list.get(h)).child("date_of_alert").setValue(simpleDateFormat.format(cal.getTime())+"");
                    }
                    else if(Objects.equals(snapshot.child(added_list.get(h)).child("reminded").getValue(String.class), "once")){
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("data");
                        reference.child(added_list.get(h)).child("reminded").setValue("twice");
                    }
                }
                Log.e("district_name_list = ",district_name_list+"");
                Log.e("station_name_list = ",station_name_list+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        //TODO: first district comparision then station name comparison and get the numbers...
        filter_by_district();

    }
    private void filter_by_district() {
        phone_numbers_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0;i<district_name_list.size();i++){
                    for(DataSnapshot ds_key:snapshot.getChildren()){
                        if(district_name_list.get(i).toLowerCase().trim().equals(ds_key.getKey().trim())){
                            if(snapshot.child(district_name_list.get(i)).child("PS "+station_name_list.get(i)).exists()){
                                phone_numbers.add(snapshot.child(district_name_list.get(i)).child("PS "+station_name_list.get(i)).getValue(String.class));
                            }
                        }
                    }
                }
                Log.e("phone_numbers = ",phone_numbers+"");
                for(int pos=0;pos<phone_numbers.size();pos++){
                    // httpCall("https://2factor.in/API/R1/?module=TRANS_SMS&apikey=89988543-35b9-11ec-a13b-0200cd936042&to="+phone_numbers.get(pos)+"&from=OMSAIT&templatename=TESTING&var1="+"Himanshi"+"&var2="+"OM is Love");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**CALL THIS IF YOU NEED CONTEXT*/
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