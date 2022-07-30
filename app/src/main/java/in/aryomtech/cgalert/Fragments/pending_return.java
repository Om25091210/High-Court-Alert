package in.aryomtech.cgalert.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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

import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
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

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.DB.TinyDB;
import in.aryomtech.cgalert.Fragments.Adapter.Excel_Adapter;
import in.aryomtech.cgalert.Fragments.model.Excel_data;
import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.fcm.Specific;
import soup.neumorphism.NeumorphButton;


public class pending_return extends Fragment {

    View view;
    Query query_return;
    private Context contextNullSafe;
    boolean isadmin=false;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    LinkedList<Excel_data> excel_data=new LinkedList<>();
    LinkedList<Excel_data> mylist=new LinkedList<>();
    EditText search;
    DatabaseReference user_ref;
    Dialog dialog,dialog1,j_dialog;
    int c=0;
    String sp_of;
    List<Excel_data> j_data_list=new ArrayList<>();
    TextView message, notification,phone_sms,no_data;
    CheckBox select_all;
    LinkedList<String> phone_numbers=new LinkedList<>();
    LinkedList<String> station_name_list=new LinkedList<>();
    Excel_Adapter excel_adapter;
    DatabaseReference phone_numbers_ref;
    ArrayList<String> added_list;
    List<String> noti_keys_copy_selected_phone=new ArrayList<>();

    List<String> not_sent_sms_list=new ArrayList<>();
    List<String> keys_selected=new ArrayList<>();
    List<String> keys_copy_selected_phone=new ArrayList<>();
    DatabaseReference reference;
    NeumorphButton join;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    ImageView bulk_delete,cg_logo,j_column;
    List<String> case_data_list=new ArrayList<>();
    List<String> case_data_list_filter=new ArrayList<>();
    List<String> district_name_list=new ArrayList<>();
    private in.aryomtech.cgalert.Fragments.Interface.onClickInterface onClickInterface;
    private in.aryomtech.cgalert.Fragments.Interface.onAgainClickInterface onAgainClickInterface;
    TinyDB tinyDB;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_pending_return, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        query_return = FirebaseDatabase.getInstance().getReference().child("data").orderByChild("type").equalTo("RM RETURN");
        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        added_list=new ArrayList<>();
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        search=view.findViewById(R.id.search);
        select_all=view.findViewById(R.id.checkBox4);
        join=view.findViewById(R.id.join);
        cg_logo=view.findViewById(R.id.imageView3);
        no_data=view.findViewById(R.id.no_data);
        bulk_delete=view.findViewById(R.id.imageRemoveImage);
        j_column=view.findViewById(R.id.j_column);
        //Initialize RecyclerView
        mRecyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setLayoutManager(mManager);
        excel_adapter= new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface);

        isadmin=getContextNullSafety().getSharedPreferences("isAdmin_or_not",Context.MODE_PRIVATE)
                .getBoolean("authorizing_admin",false);
        if(isadmin) {
            join.setVisibility(View.VISIBLE);
            bulk_delete.setVisibility(View.VISIBLE);
            select_all.setVisibility(View.VISIBLE);
        }
        else {
            join.setVisibility(View.GONE);
            bulk_delete.setVisibility(View.GONE);
            select_all.setVisibility(View.GONE);
        }

        //Initialize Database
        reference = FirebaseDatabase.getInstance().getReference().child("data");
        user_ref=FirebaseDatabase.getInstance().getReference().child("users");
        phone_numbers_ref=FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        onClickInterface = position -> {
            if(search.getText().toString().equals("")) {
                if (!added_list.contains(excel_data.get(position).getPushkey()))
                    added_list.add(excel_data.get(position).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(added_list.size() == excel_data.size());
                join.setText(txt);
                Log.e("added_peeps", added_list + "");
            }
            else{
                if (!added_list.contains(mylist.get(position).getPushkey()))
                    added_list.add(mylist.get(position).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(added_list.size() == mylist.size());
                join.setText(txt);
                Log.e("added_peeps", added_list + "");
            }
        };

        onAgainClickInterface=removePosition -> {
            if(search.getText().toString().equals("")) {
                added_list.remove(excel_data.get(removePosition).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(false);
                join.setText(txt);
                Log.e("added_peeps", added_list + "");
            }
            else{
                added_list.remove(mylist.get(removePosition).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(false);
                join.setText(txt);
                Log.e("added_peeps", added_list + "");
            }
        };
        select_all.setOnClickListener(v->{
            if (select_all.isChecked()){
                if(search.getText().toString().equals("")) {
                    for (int i = 0; i < excel_data.size(); i++) {
                        if (!added_list.contains(excel_data.get(i).getPushkey()))
                            added_list.add(excel_data.get(i).getPushkey());
                    }
                    String txt = "Send " + "(" + added_list.size() + ")";
                    join.setText(txt);
                    excel_adapter.selectAll();
                }
                else{
                    for (int i = 0; i < mylist.size(); i++) {
                        if (!added_list.contains(mylist.get(i).getPushkey()))
                            added_list.add(mylist.get(i).getPushkey());
                    }
                    String txt = "Send " + "(" + added_list.size() + ")";
                    join.setText(txt);
                    excel_adapter.selectAll();
                }
            }
            else{
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselectall();
            }
            excel_adapter.notifyDataSetChanged();
            Log.e("added_peeps",added_list+"");
        });
        //Set listener to SwipeRefreshLayout for refresh action
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if(sp_of.equals("none")) {
                if (tinyDB.getInt("num_station") == 0) {
                    getDataForIG();
                } else if (tinyDB.getInt("num_station") == 10) {
                    getDataForSDOP();
                } else {
                    get_pending();
                }
            }
            else
                getdata_for_sp();
        });
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                select_all.setChecked(false);
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                search(s+"");
            }
        });

        bulk_delete.setOnClickListener(v->{
            Dialog dialog = new Dialog(getContextNullSafety());
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView text=dialog.findViewById(R.id.textView94);
            text.setText("Delete All?");
            TextView yes=dialog.findViewById(R.id.textView95);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(vi-> dialog.dismiss());
            yes.setOnClickListener(vi-> {
                if(added_list!=null){
                    for(int i=0;i<added_list.size();i++){
                        reference.child(added_list.get(i)).removeValue();
                        for(int j=0;j<excel_data.size();j++){
                            if(excel_data.get(j).getPushkey().equals(added_list.get(i)))
                                excel_adapter.remove(excel_data.get(j));
                        }
                    }
                }
                dialog.dismiss();
            });

        });

        join.setOnClickListener(v->{
            dialog = new Dialog(getContextNullSafety());
            dialog.setContentView(R.layout.message_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            message = dialog.findViewById(R.id.message);
            notification = dialog.findViewById(R.id.notification);
            phone_sms = dialog.findViewById(R.id.phone_sms);

            message.setOnClickListener(v1->{
                dialog.dismiss();
                gather_number("sms");
            });

            notification.setOnClickListener(v2->{
                dialog.dismiss();
                gather_number("notify");
            });

            phone_sms.setOnClickListener(v3->{
                dialog.dismiss();
                gather_number("phonesms");
            });

        });

        j_column.setOnClickListener(v->{
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(added_list!=null){
                        if(added_list.size()!=0) {
                            j_dialog = new Dialog(getContextNullSafety());
                            j_dialog.setCancelable(true);
                            j_dialog.setContentView(R.layout.j_column_dialog);
                            TextView dates=j_dialog.findViewById(R.id.diary);
                            j_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            j_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            j_dialog.show();
                            dates.setOnClickListener(view -> {
                                Calendar cal = Calendar.getInstance();
                                int year = cal.get(Calendar.YEAR);
                                int month = cal.get(Calendar.MONTH);
                                int day = cal.get(Calendar.DAY_OF_MONTH);

                                DatePickerDialog dialog = new DatePickerDialog(
                                        getActivity(),
                                        mDateSetListener,
                                        year,month,day);

                                dialog.show();
                            });

                            mDateSetListener = (datePicker, year, month, day) -> {

                                String d=String.valueOf(day);
                                String m=String.valueOf(month+1);
                                Log.e("month",m+"");
                                month = month + 1;
                                Log.e("month",month+"");
                                if(String.valueOf(day).length()==1)
                                    d="0"+ day;
                                if(String.valueOf(month).length()==1)
                                    m="0"+ month;
                                String date = d + "." + m + "." + year;
                                dates.setText(date);
                            };

                            TextView cancel=j_dialog.findViewById(R.id.textView96);
                            TextView yes=j_dialog.findViewById(R.id.textView95);
                            cancel.setOnClickListener(vi-> j_dialog.dismiss());
                            yes.setOnClickListener(vi-> {
                                Snackbar.make(mRecyclerView,"Gathering data...",Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.parseColor("#ea4a1f"))
                                        .setTextColor(Color.parseColor("#000000"))
                                        .setBackgroundTint(Color.parseColor("#D9F5F8"))
                                        .show();
                                for (int i = 0; i < added_list.size(); i++) {
                                    j_data_list.add(snapshot.child(added_list.get(i)).getValue(Excel_data.class));
                                }

                                dialog1 = new Dialog(getContextNullSafety());
                                dialog1.setCancelable(false);
                                dialog1.setContentView(R.layout.loading_dialog);
                                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                                lottieAnimationView.setAnimation("done.json");
                                dialog1.show();

                                update_J_Excel(j_data_list,dates.getText().toString());

                                Log.e("dates",j_data_list.size()+"");
                            });
                        }
                    }
                    //TODO: dialog k andar date and ok mai send all
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        sp_of=getContextNullSafety().getSharedPreferences("Is_SP",MODE_PRIVATE)
                .getString("Yes_of","none");
        tinyDB=new TinyDB(getContextNullSafety());
        if(sp_of.equals("none")) {
            if(tinyDB.getInt("num_station")==0){
                getDataForIG();
            }
            else if(tinyDB.getInt("num_station")==10){
                getDataForSDOP();
            }
            else{
                get_pending();
            }
        }
        else
            getdata_for_sp();

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

        return view;
    }

    private void getdata_for_sp() {
        search.setText("");
        select_all.setChecked(false);
        added_list.clear();
        String txt="Send "+"("+added_list.size()+")";
        join.setText(txt);
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(true);
        query_return.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        if(snapshot.child(ds.getKey()).child("C").getValue(String.class).equals(sp_of)) {
                            excel_data.add(snapshot.child(ds.getKey()).getValue(Excel_data.class));
                        }
                    }
                }
                if(excel_data.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselectall();
                mSwipeRefreshLayout.setRefreshing(false);
                Collections.reverse(excel_data);
                excel_adapter=new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface);
                excel_adapter.notifyDataSetChanged();
                if(mRecyclerView!=null)
                    mRecyclerView.setAdapter(excel_adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void getDataForIG() {
        search.setText("");
        select_all.setChecked(false);
        added_list.clear();
        String txt="Send "+"("+added_list.size()+")";
        join.setText(txt);
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(true);
        query_return.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        if(tinyDB.getListString("districts_list").contains(snapshot.child(Objects.requireNonNull(ds.getKey())).child("C").getValue(String.class))){
                            excel_data.add(snapshot.child(ds.getKey()).getValue(Excel_data.class));
                        }
                    }
                }
                if(excel_data.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselectall();
                mSwipeRefreshLayout.setRefreshing(false);
                Collections.reverse(excel_data);
                excel_adapter=new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface);
                excel_adapter.notifyDataSetChanged();
                if(mRecyclerView!=null)
                    mRecyclerView.setAdapter(excel_adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void getDataForSDOP() {
        search.setText("");
        select_all.setChecked(false);
        added_list.clear();
        String txt="Send "+"("+added_list.size()+")";
        join.setText(txt);
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(true);
        query_return.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        if(tinyDB.getListString("districts_list")
                                .contains(snapshot.child(Objects.requireNonNull(ds.getKey())).child("C").getValue(String.class))
                                && tinyDB.getListString("stations_list").contains("PS "+snapshot.child(Objects.requireNonNull(ds.getKey())).child("B").getValue(String.class))){
                            excel_data.add(snapshot.child(ds.getKey()).getValue(Excel_data.class));
                        }
                    }
                }
                if(excel_data.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselectall();
                mSwipeRefreshLayout.setRefreshing(false);
                Collections.reverse(excel_data);
                excel_adapter=new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface);
                excel_adapter.notifyDataSetChanged();
                if(mRecyclerView!=null)
                    mRecyclerView.setAdapter(excel_adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    public void gather_number(String type) {
        dialog1 = new Dialog(getContextNullSafety());
        dialog1.setCancelable(true);
        dialog1.setContentView(R.layout.loading_dialog);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.show();
        Snackbar.make(mRecyclerView,"Gathering number of stations...",Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#ea4a1f"))
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                .show();
        station_name_list.clear();
        district_name_list.clear();
        case_data_list.clear();
        case_data_list_filter.clear();
        not_sent_sms_list.clear();
        keys_selected.clear();
        keys_copy_selected_phone.clear();
        phone_numbers.clear();
        Log.e("added_pushkey",added_list+"");
        query_return.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(added_list.size()!=0) {
                    for (int h = 0; h < added_list.size(); h++) {
                        String station_name = "PS " + snapshot.child(added_list.get(h)).child("B").getValue(String.class).toUpperCase().trim();
                        String district_name = snapshot.child(added_list.get(h)).child("C").getValue(String.class).toUpperCase().trim();

                        String K = snapshot.child(added_list.get(h)).child("K").getValue(String.class).trim();
                        String C = snapshot.child(added_list.get(h)).child("C").getValue(String.class).trim();
                        String D = snapshot.child(added_list.get(h)).child("D").getValue(String.class).trim();
                        String E = snapshot.child(added_list.get(h)).child("E").getValue(String.class).trim();
                        String G = snapshot.child(added_list.get(h)).child("G").getValue(String.class).trim();
                        String H = snapshot.child(added_list.get(h)).child("H").getValue(String.class).trim();
                        String I = snapshot.child(added_list.get(h)).child("I").getValue(String.class).trim();
                        String B = snapshot.child(added_list.get(h)).child("B").getValue(String.class).trim();
                        String type = snapshot.child(added_list.get(h)).child("type").getValue(String.class).trim();

                        case_data_list.add(type+"~"+K+"~"+C+"~"+D+"~"+E+"~"+G+"~"+H+"~"+I+"~"+B+"~");
                        district_name_list.add(district_name);
                        station_name_list.add(station_name);
                        keys_selected.add(added_list.get(h));

                    }
                    Log.e("district_name_list = ", district_name_list + "");
                    Log.e("station_name_list = ", station_name_list + "");
                    filter_by_district(type);
                }
                else{
                    Snackbar.make(mRecyclerView,"Zero selections...",Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#ea4a1f"))
                            .setTextColor(Color.parseColor("#000000"))
                            .setBackgroundTint(Color.parseColor("#D9F5F8"))
                            .show();
                    dialog1.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void filter_by_district(String type) {
        Snackbar.make(mRecyclerView,"Filtering data...",Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#ea4a1f"))
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                .show();
        phone_numbers_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0;i<district_name_list.size();i++){
                    for(DataSnapshot ds_key:snapshot.getChildren()){
                        if(district_name_list.get(i).toLowerCase().trim().equals(ds_key.getKey().toLowerCase().trim())){
                            if(snapshot.child(ds_key.getKey().trim()).child(station_name_list.get(i)).exists()){
                                phone_numbers.add(snapshot.child(ds_key.getKey()).child(station_name_list.get(i)).getValue(String.class));
                                case_data_list_filter.add(case_data_list.get(i));
                                keys_copy_selected_phone.add(keys_selected.get(i));
                            }
                            else{
                                not_sent_sms_list.add(keys_selected.get(i));
                            }
                        }
                    }
                }
                Log.e("phone_numbers = ",phone_numbers+"");
                if(phone_numbers.size()!=0) {
                    for (int pos = 0; pos < phone_numbers.size(); pos++) {
                        if (type.equals("sms")) {
                            //httpCall("https://2factor.in/API/R1/?module=TRANS_SMS&apikey=89988543-35b9-11ec-a13b-0200cd936042&to="+phone_numbers.get(pos)+"&from=OMSAIT&templatename=TESTING&var1="+"Himanshi"+"&var2="+"OM is Love");
                        } else if (type.equals("phonesms")) {
                            send_phone_sms(phone_numbers);
                            break;
                        } else {
                            send_notification(phone_numbers);
                            break;
                        }
                    }
                }
                else{
                    Toast.makeText(getContextNullSafety(), "No data found.", Toast.LENGTH_SHORT).show();
                    dialog1.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void send_phone_sms(List<String> phone_numbers) {
        SmsManager sms = SmsManager.getDefault();
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0;i<phone_numbers.size();i++) {
                    int check=0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (snapshot.child(ds.getKey()).child(phone_numbers.get(i)).exists()) {
                            check=1;
                            reference.child(keys_copy_selected_phone.get(i)).child("reminded").setValue("once");
                            String body = extract_data(i);
                            ArrayList<String> list = sms.divideMessage(body);
                            sms.sendMultipartTextMessage(phone_numbers.get(i), null, list, null, null);
                        }
                    }
                    if(check==0)
                        not_sent_sms_list.add(keys_copy_selected_phone.get(i));
                }
                //TODO :Sent to next section.
                Log.e("number does not exist = ",not_sent_sms_list+"");
                getContextNullSafety().getSharedPreferences("saving_RM_pending_return_not_sms",Context.MODE_PRIVATE).edit()
                        .putString("RM_pending_return_list",not_sent_sms_list+"").apply();

                Snackbar.make(mRecyclerView,"Notified successfully...",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#ea4a1f"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setBackgroundTint(Color.parseColor("#D9F5F8"))
                        .show();
                dialog1.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void send_notification(List<String> phone_numbers) {
        noti_keys_copy_selected_phone.clear();
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0;i<phone_numbers.size();i++){
                    int check=0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (snapshot.child(ds.getKey()).child(phone_numbers.get(i)).exists()) {
                            check=1;
                            String body=extract_data(i);
                            if(snapshot.child(ds.getKey()).child("token").exists()) {
                                reference.child(keys_copy_selected_phone.get(i)).child("reminded").setValue("once");
                                for (DataSnapshot dd : snapshot.child(ds.getKey()).child("token").getChildren()) {
                                    String token = snapshot.child(ds.getKey()).child("token").child(Objects.requireNonNull(dd.getKey())).getValue(String.class);
                                    if (token != null) {
                                        Specific specific = new Specific();
                                        specific.noti("High Court Alert", body, token,keys_copy_selected_phone.get(i));
                                    }
                                }
                            }
                            else{
                                noti_keys_copy_selected_phone.add(keys_copy_selected_phone.get(i));
                            }
                        }
                    }
                    if(check==0)
                        not_sent_sms_list.add(keys_copy_selected_phone.get(i));
                }

                //TODO :Sent to next section.
                Log.e("number does not exist = ",not_sent_sms_list+"");
                Log.e("keys copy selected phone = ",noti_keys_copy_selected_phone+"");

                getContextNullSafety().getSharedPreferences("saving_RM_pending_return_not_noti",Context.MODE_PRIVATE).edit()
                        .putString("RM_pending_return_list",noti_keys_copy_selected_phone+"").apply();

                getContextNullSafety().getSharedPreferences("saving_RM_pending_return_not_sms",Context.MODE_PRIVATE).edit()
                        .putString("RM_pending_return_list",not_sent_sms_list+"").apply();

                Snackbar.make(mRecyclerView,"Notified successfully...",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#ea4a1f"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setBackgroundTint(Color.parseColor("#D9F5F8"))
                        .show();
                dialog1.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void httpCall(String url) {

        RequestQueue queue = Volley.newRequestQueue(getContextNullSafety());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // enjoy your response
                        Snackbar.make(mRecyclerView,"SMS sent successfully...",Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.parseColor("#ea4a1f"))
                                .setTextColor(Color.parseColor("#000000"))
                                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                                .show();
                        dialog1.dismiss();
                        Log.e("Status of code = ","Success");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // enjoy your error status
                Log.e("Status of code = ","Wrong");
            }
        });

        queue.add(stringRequest);
    }
    private String extract_data(int index) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        Date date = new Date();
        String format=case_data_list_filter.get(index);
        String C=null,B=null,K=null,D=null,E=null,G=null,H=null,I=null,type=null;
        int temp=0;
        for(int i=0;i<format.length();i++){
            if(format.charAt(i)=='~'){
                if(type==null){
                    type=format.substring(0,i);
                    temp=i+1;
                }
                else if(K==null){
                    K=format.substring(temp,i);
                    temp=i+1;
                }
                else if (C==null){
                    C=format.substring(temp,i);
                    temp=i+1;
                }
                else if(D==null){
                    D=format.substring(temp,i);
                    temp=i+1;
                }
                else if(E==null){
                    E=format.substring(temp,i);
                    temp=i+1;
                }
                else if(G==null){
                    G=format.substring(temp,i);
                    temp=i+1;
                }
                else if(H==null){
                    H=format.substring(temp,i);
                    temp=i+1;
                }
                else if(I==null){
                    I=format.substring(temp,i);
                    temp=i+1;
                }
                else if(B==null){
                    B=format.substring(temp,i);

                }
            }
        }
        if(type.equals("RM RETURN")){
            String current=formatter.format(date);
            return "हाईकोर्ट अलर्ट:-डायरी वापसी"+"\nदिनाँक:- "+current+" \n"
                    +"\n"+C+"\n"+D+" No. "+E+"/"+G+"\n"
                    +"Crime No. "+H+"/"+I+"\n"
                    +"Police station: "+B+"\n"
                    +"1)उपरोक्त मूल केश डायरी  महाधिवक्ता कार्यालय द्वारा दी गयी मूल पावती लाने पर ही दी जाएगी।\n"
                    +"2) उपरोक्त मूल केश डायरी "+K+" से पांच दिवस के भीतर बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय से वापिस ले जावें।";
        }
        else{
            String current=formatter.format(date);
            return "हाईकोर्ट अलर्ट:-डायरी माँग"+"\nदिनाँक:- "+current+" \n"
                    +"\n"+C+"\n"+D+" No. "+E+"/"+G+"\n"
                    +"Crime No. "+H+"/"+I+"\n"
                    +"Police station: "+B+"\n"
                    +"उपरोक्त मूल केश डायरी तथा पूर्व अपराधिक रिकॉर्ड, दिनाँक "+K+" तक बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय छतीसगढ़ में  अनिवार्यतः जमा करें।";
        }
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
        excel_adapter=new Excel_Adapter(getContextNullSafety(),mylist,onClickInterface,onAgainClickInterface);
        excel_adapter.notifyDataSetChanged();
        if(mRecyclerView!=null)
            mRecyclerView.setAdapter(excel_adapter);
    }

    private void get_pending() {
        search.setText("");
        select_all.setChecked(false);
        added_list.clear();
        String txt="Send "+"("+added_list.size()+")";
        join.setText(txt);
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(true);
        query_return.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        excel_data.add(snapshot.child(ds.getKey()).getValue(Excel_data.class));
                    }
                }
                if(excel_data.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselectall();
                mSwipeRefreshLayout.setRefreshing(false);
                Collections.reverse(excel_data);
                excel_adapter=new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface);
                excel_adapter.notifyDataSetChanged();
                if(mRecyclerView!=null)
                    mRecyclerView.setAdapter(excel_adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void update_J_Excel(List<Excel_data> j_dates_list,String j_date) {
        Snackbar.make(mRecyclerView,"Updating dates...",Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#ea4a1f"))
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                .show();
        j_dialog.dismiss();
        Log.e("Sheet",j_date+"");
        JSONObject jsonBody = new JSONObject();
        try
        {
            jsonBody.put("cno", Integer.parseInt(j_dates_list.get(c).getE()));
            jsonBody.put("crno", Integer.parseInt(j_dates_list.get(c).getH()));
            jsonBody.put("ps", j_dates_list.get(c).getB());
            jsonBody.put("nod", j_dates_list.get(c).getC());
            jsonBody.put("subject", j_dates_list.get(c).getType());
            jsonBody.put("j_column", j_date);
            Log.d("body", "httpCall_collect: "+jsonBody);
        }
        catch (Exception e)
        {
            Log.e("Error","JSON ERROR");
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getContextNullSafety());
        String URL = "https://high-court-alertsystem.herokuapp.com/j_column";

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL,jsonBody,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // enjoy your response
                        String code=response.optString("code")+"";
                        if(code.equals("202")){
                            reference.child(added_list.get(c)).child("J").setValue(j_date);
                            Snackbar.make(join,"Data Uploaded to Excel.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog1.dismiss();
                                    c++;
                                    if(c!=j_dates_list.size())
                                        update_J_Excel(j_dates_list, j_date);
                                    else
                                        c=0;
                                }
                            },2000);
                        }
                        else{
                            Snackbar.make(join,"Failed to Upload in Excel.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#000000"))
                                    .setTextColor(Color.parseColor("#000000"))
                                    .setBackgroundTint(Color.parseColor("#FF5252"))
                                    .show();
                            LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                            lottieAnimationView.setAnimation("error.json");
                            dialog1.show();
                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog1.dismiss();
                                    c++;
                                    if(c!=j_dates_list.size())
                                        update_J_Excel(j_dates_list, j_date);
                                    else
                                        c=0;
                                }
                            },2000);
                        }
                        Log.e("BULK code",code+"");
                        Log.e("response",response.toString());
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // enjoy your error status
                Log.e("Status of code = ","Wrong");
            }
        });
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 15000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 15000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });
        Log.d("string", stringRequest.toString());
        requestQueue.add(stringRequest);

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