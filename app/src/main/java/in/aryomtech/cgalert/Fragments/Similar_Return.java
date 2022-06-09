package in.aryomtech.cgalert.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import in.aryomtech.cgalert.Fragments.Adapter.similarAdapter;
import in.aryomtech.cgalert.Fragments.Adapter.similarAdapter2;
import in.aryomtech.cgalert.Fragments.model.Excel_data;
import in.aryomtech.cgalert.Fragments.model.filterdata;
import in.aryomtech.cgalert.R;


public class Similar_Return extends Fragment {

    View view;
    private Context contextNullSafe;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    Query query;
    List<Excel_data> excel_data=new ArrayList<>();
    List<Excel_data> excel_data_duplicates=new ArrayList<>();
    List<filterdata> filtered_mylist=new ArrayList<>();
    EditText search;
    ImageView cg_logo;
    TextView no_data;
    List<filterdata> save_locally_list=new ArrayList<>();
    List<String> filtered_data=new ArrayList<>();
    List<String> joined_list=new ArrayList<>();
    List<String> station_dist=new ArrayList<>();
    List<String> filtered_station_dist=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_similar__return, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        search=view.findViewById(R.id.search);
        cg_logo=view.findViewById(R.id.imageView3);
        no_data=view.findViewById(R.id.no_data);
        //Initialize RecyclerView
        mRecyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setLayoutManager(mManager);
        //adapter
        //Initialize Database
        query = FirebaseDatabase.getInstance().getReference().child("data").orderByChild("type").equalTo("RM RETURN");
        getdata();
        //Set listener to SwipeRefreshLayout for refresh action
        mSwipeRefreshLayout.setOnRefreshListener(this::getdata);
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s+"");
            }
        });
        return view;
    }
    private void search(String str) {
        filtered_mylist.clear();
        for(filterdata object:save_locally_list){
            if (object.getCn().toLowerCase().contains(str.toLowerCase().trim())) {
                filtered_mylist.add(object);
            } else if (object.getCt().toLowerCase().contains(str.toLowerCase().trim())) {
                filtered_mylist.add(object);
            } else if (object.getYear().toLowerCase().contains(str.toLowerCase().trim())) {
                filtered_mylist.add(object);
            } else if (object.getStn().toLowerCase().contains(str.toLowerCase().trim())) {
                filtered_mylist.add(object);
            }
            else if(object.getDis_n().toLowerCase().contains(str.toLowerCase().trim())){
                filtered_mylist.add(object);
            }

        }
        similarAdapter2 similarAdapter2=new similarAdapter2(getContextNullSafety(),filtered_mylist,excel_data_duplicates);
        similarAdapter2.notifyDataSetChanged();
        if(mRecyclerView!=null)
            mRecyclerView.setAdapter(similarAdapter2);
        //adapter
    }
    private void getdata() {
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.clear();
                joined_list.clear();
                excel_data_duplicates.clear();
                save_locally_list.clear();
                filtered_data.clear();
                station_dist.clear();
                filtered_station_dist.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    excel_data.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Excel_data.class));
                    joined_list.add(excel_data.get(excel_data.size()-1).getD().toLowerCase().trim()+" "+excel_data.get(excel_data.size()-1).getH().trim()+" "+excel_data.get(excel_data.size()-1).getI().trim()+"="+excel_data.get(excel_data.size()-1).getB().trim()+" "+excel_data.get(excel_data.size()-1).getC().trim());
                    station_dist.add(excel_data.get(excel_data.size()-1).getB().trim()+" "+excel_data.get(excel_data.size()-1).getC().trim());
                }
                mSwipeRefreshLayout.setRefreshing(false);
                Collections.reverse(excel_data);
                Collections.reverse(joined_list);
                Collections.reverse(station_dist);
                //filtering data
                for(int i=0;i<joined_list.size();i++){//n
                    if(Collections.frequency(joined_list,joined_list.get(i))>1){ //n   7292 = 7
                        excel_data_duplicates.add(excel_data.get(i));
                        if(!filtered_data.contains(joined_list.get(i))){//n
                            filtered_data.add(joined_list.get(i)); // n
                            filtered_station_dist.add(station_dist.get(i));
                            remove_spaces_and_store(filtered_data,filtered_station_dist);
                        }
                    }
                }

                Log.e("filtered data",filtered_data+"");
                Log.e("station list",filtered_station_dist+"");
                if(excel_data_duplicates.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                similarAdapter2 similarAdapter2=new similarAdapter2(getContextNullSafety(),save_locally_list,excel_data_duplicates);
                similarAdapter2.notifyDataSetChanged();
                if(mRecyclerView!=null)
                    mRecyclerView.setAdapter(similarAdapter2);
                //adapter
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void remove_spaces_and_store(List<String> filtered_data,List<String> filtered_station_dist) {
        String case_type="",case_no="",year="",station_name="",district="";
        for(int i=0;i<filtered_data.size();i++){
            String str=filtered_data.get(i);
            String stat_dist=filtered_station_dist.get(i);
            int count=0;
            int temp_j=0;
            int space_pos=0;
            for(int j=0;j<str.length();j++) {
                if(' '==str.charAt(j)){
                    if(count==0) {
                        case_type = str.substring(0,j);
                        count++;
                        space_pos=j;
                    }
                    else if(count==1){
                        case_no= str.substring(space_pos+1,j);
                        temp_j=j;
                    }
                }
                if('='==str.charAt(j)){
                    year=str.substring(temp_j,j);
                    break;
                }
            }
            for(int j=0;j<stat_dist.length();j++) {
                if(' '==stat_dist.charAt(j)){
                    station_name= stat_dist.substring(0,j);
                    district=stat_dist.substring(j+1);
                    break;
                }
            }
        }
        save_locally_list.add(new filterdata(case_type,case_no,year,station_name,district));

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