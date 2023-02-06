package in.aryomtech.cgalert.NoticeVictim.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.om.mylibrary.RobotoCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.NoticeVictim.Adapter.NoticeAdapter;
import in.aryomtech.cgalert.NoticeVictim.Adapter.ServedAdapter;
import in.aryomtech.cgalert.NoticeVictim.model.Notice_model;
import in.aryomtech.cgalert.R;
import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class Served extends Fragment implements RobotoCalendarView.RobotoCalendarListener, LocationListener {

    RobotoCalendarView robotoCalendarView;
    View view;
    EditText search;
    String stat_name;
    Context contextNullSafe;
    DatabaseReference reference;
    List<Notice_model> list;
    List<String> list_string=new ArrayList<>();
    List<Notice_model> mylist;
    String ps_or_admin="";
    ServedAdapter adapter;
    private in.aryomtech.cgalert.NoticeVictim.Interface.onUploadInterface onUploadInterface;
    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_served, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        robotoCalendarView = view.findViewById(R.id.roboto);
        search=view.findViewById(R.id.search);
        recyclerView = view.findViewById(R.id.cal_rv);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(mManager);
        stat_name= getContextNullSafety().getSharedPreferences("station_name_K",MODE_PRIVATE)
                .getString("the_station_name2003","");
        reference = FirebaseDatabase.getInstance().getReference().child("notice");
        list = new ArrayList<>();
        mylist = new ArrayList<>();
        // Set listener, in this case, the same activity
        robotoCalendarView.setRobotoCalendarListener(Served.this);

        robotoCalendarView.setShortWeekDays(false);

        robotoCalendarView.showDateTitle(true);

        robotoCalendarView.setDate(new Date());
        ps_or_admin=getContextNullSafety().getSharedPreferences("useris?",MODE_PRIVATE)
                .getString("the_user_is?","");
        if(ps_or_admin.equals("home")) {
            get_all_data();
        }
        else if(ps_or_admin.equals("p_home")){
            get_all_data_ps();
        }
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s+"");
            }
        });
        return view;
    }

    private void get_all_data_ps() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot ds:snapshot.getChildren()) {
                    if ((Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("station").getValue(String.class)).trim()).toUpperCase().equals(stat_name.substring(3).trim())) {
                        if (snapshot.child(Objects.requireNonNull(ds.getKey())).child("uploaded_date").exists()) {
                            list.add(snapshot.child(ds.getKey()).getValue(Notice_model.class));
                        }
                    }
                }
                Collections.reverse(list);
                adapter = new ServedAdapter(getContextNullSafety(),onUploadInterface, list);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void search(String str) {
        if(str.equals("")){
            adapter = new ServedAdapter(getContextNullSafety(), onUploadInterface, list);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else {
            String[] str_Args = str.toLowerCase().split(" ");
            mylist.clear();
            int count = 0;
            boolean not_once = true;
            List<Integer> c_list = new ArrayList<>();
            for (Notice_model object : list) {
                convert_to_list(object);
                for (String s : list_string) {
                    for (String str_arg : str_Args) {
                        if (str_arg.contains("/") && not_once) {
                            String sub1 = str_arg.substring(0, str_arg.indexOf("/"));
                            String sub2 = str_arg.substring(str_arg.indexOf("/") + 1);
                            if (list_string.get(3).contains(sub1) && list_string.get(4).contains(sub2)) {
                                count++;
                                not_once = false;
                            } else if (list_string.get(12).contains(sub1) && list_string.get(2).contains(sub2)) {
                                count++;
                                not_once = false;
                            }
                        } else if (s.contains(str_arg)) {
                            count++;
                        }
                    }
                }
                c_list.add(count);
                System.out.println(c_list + "");
                if (count == str_Args.length)
                    mylist.add(object);
                count = 0;
            }
            adapter = new ServedAdapter(getContextNullSafety(), onUploadInterface, mylist);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
    private void convert_to_list(Notice_model object) {
        list_string.clear();
        try{
            list_string.add(object.getAdvocate().toLowerCase());
            list_string.add(object.getCaseType().toLowerCase());
            list_string.add(object.getCaseYear().toLowerCase());
            list_string.add(object.getCrimeNo().toLowerCase());
            list_string.add(object.getCrimeYear().toLowerCase());
            list_string.add(object.getPushkey().toLowerCase());
            list_string.add(object.getDoc_url().toLowerCase());
            list_string.add(object.getDistrict().toLowerCase());
            list_string.add(object.getHearingDate().toLowerCase());
            list_string.add(object.getNoticeDate().toLowerCase());
            list_string.add(object.getStation().toLowerCase());
            list_string.add(object.getAppellant().toLowerCase());
            list_string.add(object.getCaseNo().toLowerCase());
            list_string.add(object.getReminded().toLowerCase());
            list_string.add(object.getSeen().toLowerCase());
            list_string.add(object.getSent().toLowerCase());
            list_string.add(object.getNumber().toLowerCase());
            list_string.add(object.getUploaded_file().toLowerCase());
            list_string.add(object.getUploaded_date().toLowerCase());
        }
        catch (NullPointerException e){
            System.out.println("Error");
        }
    }
    private void get_all_data() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot ds:snapshot.getChildren()) {
                    if(snapshot.child(ds.getKey()).child("district").exists()) {
                        if (snapshot.child(ds.getKey()).child("advocate").exists()) {
                            if (snapshot.child(Objects.requireNonNull(ds.getKey())).child("uploaded_date").exists()) {
                                list.add(snapshot.child(ds.getKey()).getValue(Notice_model.class));
                            }
                        }
                    }
                }
                Collections.reverse(list);
                adapter = new ServedAdapter(getContextNullSafety(),onUploadInterface, list);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onDayClick(Date date) {
        if(ps_or_admin.equals("home")) {
            get_data(date);
        }
        else if(ps_or_admin.equals("p_home")){
            get_ps_data(date);
        }
    }

    private void get_ps_data(Date date) {
        SimpleDateFormat ft =
                new SimpleDateFormat ("dd.MM.yyyy", Locale.getDefault());
        String cr_dt=ft.format(date);
        Log.e("date",cr_dt+"");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot ds:snapshot.getChildren()) {
                    Log.e("logg",ds.getKey()+"");
                    if(snapshot.child(ds.getKey()).child("district").exists()) {
                        if (snapshot.child(ds.getKey()).child("advocate").exists()) {
                            if ((Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("station").getValue(String.class)).trim()).toUpperCase().equals(stat_name.substring(3).trim())) {
                                if (snapshot.child(Objects.requireNonNull(ds.getKey())).child("uploaded_date").exists()) {
                                    if (Objects.requireNonNull(snapshot.child(ds.getKey()).child("uploaded_date").getValue(String.class)).equals(cr_dt)) {
                                        list.add(snapshot.child(ds.getKey()).getValue(Notice_model.class));
                                    }
                                }
                            }
                        }
                    }
                }
                Collections.reverse(list);
                ServedAdapter adapter = new ServedAdapter(getContextNullSafety(),onUploadInterface, list);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onDayLongClick(Date date) {

    }

    @Override
    public void onRightButtonClick() {

    }

    @Override
    public void onLeftButtonClick() {

    }
    public void get_data(Date date){
        SimpleDateFormat ft =
                new SimpleDateFormat ("dd.MM.yyyy", Locale.getDefault());
        String cr_dt=ft.format(date);
        Log.e("date",cr_dt+"");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot ds:snapshot.getChildren()) {
                    Log.e("logg",ds.getKey()+"");
                    if(snapshot.child(ds.getKey()).child("district").exists()) {
                        if (snapshot.child(ds.getKey()).child("advocate").exists()) {
                            if (snapshot.child(Objects.requireNonNull(ds.getKey())).child("uploaded_date").exists()) {
                                if (Objects.requireNonNull(snapshot.child(ds.getKey()).child("uploaded_date").getValue(String.class)).equals(cr_dt)) {
                                    list.add(snapshot.child(ds.getKey()).getValue(Notice_model.class));
                                    Log.e("loooog", snapshot.child(ds.getKey()).child("uploaded_date").getValue(String.class) + "");
                                }
                            }
                        }
                    }
                }
                Collections.reverse(list);
                ServedAdapter adapter = new ServedAdapter(getContextNullSafety(),onUploadInterface, list);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}